package io.playdata.themorethebetter.exception;

public class NotFoundException extends RuntimeException {
	public NotFoundException() {
		super("해당 정보를 찾을 수 없습니다.");
	}

	public NotFoundException(String str) {
		super(str);
	}
}