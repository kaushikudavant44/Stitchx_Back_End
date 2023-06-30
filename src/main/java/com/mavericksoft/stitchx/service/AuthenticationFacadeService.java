/**
 * 
 */
package com.mavericksoft.stitchx.service;

import org.springframework.security.core.Authentication;

/**
 * @author Sudarshan
 *
 */
public interface AuthenticationFacadeService {

	Authentication getAuthentication();

}
