package com.example.demo.entity.jdbc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class Template {
	
	@Autowired
	private JdbcTemplate template;
	
	public List<Long> getIds(String tableName) {
		String query = "select id from " + tableName;
		return template.queryForList(query, Long.class);
	}

}