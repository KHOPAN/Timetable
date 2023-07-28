package com.khopan.timetable.data;

import java.io.Serializable;

public class Subject implements Serializable {
	private static final long serialVersionUID = -8100675733741855866L;

	public int subjectDataIndex;
	public int startTimeHour;
	public int startTimeMinute;
	public int endTimeHour;
	public int endTimeMinute;

	public Subject() {

	}
}
