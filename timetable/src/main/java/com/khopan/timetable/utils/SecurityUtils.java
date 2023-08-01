package com.khopan.timetable.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Random;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SecurityUtils {
	private SecurityUtils() {}

	public static byte[] encrypt(JsonNode node, String key) {
		if(node == null || key == null || key.isEmpty()) {
			return null;
		}

		try {
			byte[] data = node.toString().getBytes(StandardCharsets.UTF_8);
			data = SecurityUtils.compress(data);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(SecurityUtils.getKey(key), "AES"), new IvParameterSpec(new byte[16]));
			data = cipher.doFinal(data);
			data = SecurityUtils.compress(data);
			return data;
		} catch(Throwable Errors) {
			Errors.printStackTrace();
			return null;
		}
	}

	public static JsonNode decrypt(byte[] data, String key) {
		if(data == null || key == null || key.isEmpty()) {
			return null;
		}

		try {
			data = SecurityUtils.decompress(data);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(SecurityUtils.getKey(key), "AES"), new IvParameterSpec(new byte[16]));
			data = cipher.doFinal(data);
			data = SecurityUtils.decompress(data);
			String result = new String(data, StandardCharsets.UTF_8);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readTree(result);
		} catch(Throwable Errors) {
			return null;
		}
	}

	private static byte[] compress(byte[] data) {
		Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
		deflater.setInput(data);
		deflater.finish();
		byte[] output = new byte[data.length * 2];
		int size = deflater.deflate(output);
		byte[] result = new byte[size];
		System.arraycopy(output, 0, result, 0, size);
		deflater.end();
		return result;
	}

	private static byte[] decompress(byte[] data) {
		try {
			Inflater inflater = new Inflater();
			inflater.setInput(data);
			byte[] output = new byte[data.length * 10];
			int size = inflater.inflate(output);
			byte[] result = new byte[size];
			System.arraycopy(output, 0, result, 0, size);
			inflater.end();
			return result;
		} catch(Throwable Errors) {
			return null;
		}
	}

	private static byte[] getKey(String key) {
		byte[] keyData = key.getBytes(StandardCharsets.UTF_8);
		byte[] result = new byte[32];
		new Random(SecurityUtils.keyHash(key)).nextBytes(result);
		System.arraycopy(keyData, 0, result, 0, Math.min(keyData.length, result.length));
		return result;
	}

	private static long keyHash(String key) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] hash = digest.digest(key.getBytes(StandardCharsets.UTF_8));
			byte[] result = new byte[8];
			System.arraycopy(hash, 0, result, 0, Math.min(hash.length, result.length));
			return SecurityUtils.byteToLong(result);
		} catch(Throwable Errors) {
			return new Random(key.length()).nextLong();
		}
	}

	private static long byteToLong(byte[] data) {
		if(data == null || data.length != 8) {
			return 0;
		}

		return (((long) data[0] & 0xFF) << 56) |
				(((long) data[1] & 0xFF) << 48) |
				(((long) data[2] & 0xFF) << 40) |
				(((long) data[3] & 0xFF) << 32) |
				(((long) data[4] & 0xFF) << 24) |
				(((long) data[5] & 0xFF) << 16) |
				(((long) data[6] & 0xFF) << 8) |
				((long) data[7] & 0xFF);
	}
}
