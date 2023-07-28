package com.khopan.property;

@FunctionalInterface
public interface Updater<T> {
	public void valueUpdated(T value);
}
