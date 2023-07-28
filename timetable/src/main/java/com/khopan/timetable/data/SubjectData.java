package com.khopan.timetable.data;

import java.io.Serializable;

public class SubjectData implements Serializable {
	private static final long serialVersionUID = -4154141569114856595L;

	public String subjectName;
	public String subjectIdentifier;
	public String[] teacherList;

	public SubjectData() {

	}
}
