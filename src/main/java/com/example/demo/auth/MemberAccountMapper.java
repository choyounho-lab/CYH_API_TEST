package com.example.demo.auth;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberAccountMapper {

	MemberAccount findByLoginId(@Param("loginId") String loginId);

	int countByLoginId(@Param("loginId") String loginId);

	int countByEmail(@Param("email") String email);

	int insertMember(
			@Param("loginId") String loginId,
			@Param("passwordHash") String passwordHash,
			@Param("displayName") String displayName,
			@Param("email") String email);

	int recordLoginSuccess(@Param("memberId") long memberId);

	int recordLoginFailure(@Param("memberId") long memberId);
}
