package com.example.demo.database;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DatabaseProbeMapper {

	int ping();
}
