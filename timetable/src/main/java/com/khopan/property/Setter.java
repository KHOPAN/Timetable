package com.khopan.property;

@FunctionalInterface
public interface Setter<T, R> {
	public R set(T value);
}
