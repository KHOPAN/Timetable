package com.khopan.property;

@FunctionalInterface
public interface SimpleSetter<T> {
	public void set(T value);
}
