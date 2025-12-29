package com.neb.dto;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDTO<T> {
	
	private String message;
	private T data;
	private LocalDateTime dateTime;
	
	public ResponseDTO(T data,String message,LocalDateTime dateTime) {
		this.data  = data;
		this.message = message;
		this.dateTime = dateTime;
		
	}
	public ResponseDTO(String message,T data) {
		this.data  = data;
		this.message = message;
	}
	public ResponseDTO() {
		// TODO Auto-generated constructor stub
	}
	public ResponseDTO(T data) {
		this.data = data;
	}

}
