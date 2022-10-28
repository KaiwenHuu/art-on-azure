package com.example.demo.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DESCRIPTIONS")
public class Description implements Serializable{
	
	public Description() {
		
	}

	public Description(String caption, String filename, String location) {
		super();
		this.caption = caption;
		this.filename = filename;
		this.location = location;
	}

	@Id
	private Long id;

	@Column
	private String caption;

	@Column
	private String filename;

	@Column
	private String location;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getFileName() {
		return filename;
	}

	public void setFileName(String filename) {
		this.filename = filename;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "Description [id=" + id + ", caption=" + caption + ", fileName=" + filename + ", location="
				+ location + "]";
	}
	
}