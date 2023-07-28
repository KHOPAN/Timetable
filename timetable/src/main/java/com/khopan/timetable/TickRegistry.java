package com.khopan.timetable;

import java.util.ArrayList;
import java.util.List;

public class TickRegistry {
	static List<Runnable> RunnableList = new ArrayList<>();

	public static void attach(Runnable runnable) {
		if(!TickRegistry.RunnableList.contains(runnable)) {
			TickRegistry.RunnableList.add(runnable);
		}
	}

	public static void detach(Runnable runnable) {
		TickRegistry.RunnableList.remove(runnable);
	}

	public static void tick() {
		for(int i = 0; i < TickRegistry.RunnableList.size(); i++) {
			TickRegistry.RunnableList.get(i).run();
		}
	}
}
