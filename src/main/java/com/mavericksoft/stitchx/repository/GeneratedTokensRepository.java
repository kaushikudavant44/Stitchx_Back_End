/**
 * 
 */
package com.mavericksoft.stitchx.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mavericksoft.stitchx.models.GeneratedTokens;

/**
 * @author ADMIN
 *
 */
public interface GeneratedTokensRepository extends JpaRepository<GeneratedTokens, Long> {

	GeneratedTokens findTopByOrderByIdDesc();

}
