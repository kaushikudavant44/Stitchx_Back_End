package com.mavericksoft.stitchx.user.api.response;

public class QCResponse {

	String field;

	String actual;

	String expected;
	
	boolean isCorrectMeasurement;

	public QCResponse(String field, String actual, String expected, boolean isCorrectMeasurement) {
		super();
		this.field = field;
		this.actual = actual;
		this.expected = expected;
		this.isCorrectMeasurement = isCorrectMeasurement;
	}
	
	public boolean isCorrectMeasurement() {
		return isCorrectMeasurement;
	}



	public void setCorrectMeasurement(boolean isCorrectMeasurement) {
		this.isCorrectMeasurement = isCorrectMeasurement;
	}



	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getActual() {
		return actual;
	}

	public void setActual(String actual) {
		this.actual = actual;
	}

	public String getExpected() {
		return expected;
	}

	public void setExpected(String expected) {
		this.expected = expected;
	}

}
