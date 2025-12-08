package com.neb.dto.user;


import com.neb.dto.client.AddClientRequest;

import lombok.Data;

@Data
public class RegisterNewClientRequest {

	private UserDto userDto;
	private AddClientRequest clientReq;
}
