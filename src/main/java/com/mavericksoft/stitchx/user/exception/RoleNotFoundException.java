package com.mavericksoft.stitchx.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class RoleNotFoundException extends Exception {
	
	private static final long serialVersionUID = 871618588278915308L;

	public RoleNotFoundException(String message) {
		super(message);
	}

}
