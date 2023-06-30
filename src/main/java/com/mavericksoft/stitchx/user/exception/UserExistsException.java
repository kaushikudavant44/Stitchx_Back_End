/**
 * 
 */
package com.mavericksoft.stitchx.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Sudarshan
 *
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UserExistsException extends Exception {

	private static final long serialVersionUID = 871618588278915308L;

	public UserExistsException(String message) {
		super(message);
	}

}
