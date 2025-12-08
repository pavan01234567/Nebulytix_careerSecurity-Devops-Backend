package com.neb.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.neb.constants.Role;
import com.neb.dto.user.UserDto;
import com.neb.entity.Users;
import com.neb.exception.CustomeException;
import com.neb.repo.UsersRepository;

@Service
public class UsersService implements UserDetailsService{
	
	@Autowired
	private BCryptPasswordEncoder pwdEncoder;
	
	@Autowired
	public UsersRepository usersRepository;
	
	public Users createUser(UserDto dto) {
		
	    if (usersRepository.existsByEmail(dto.getEmail())) {
	        throw new CustomeException("Email already exists: " + dto.getEmail());
	    }

	    Users user = new Users();
	    user.setEmail(dto.getEmail());
	    user.setPassword(pwdEncoder.encode(dto.getPassword()));
	    user.setRoles(dto.getRoles());

	    return usersRepository.save(user);
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		Users users = usersRepository.findByEmail(email);
		
		List<SimpleGrantedAuthority> authorities = users.getRoles().stream()
		.map(role->new SimpleGrantedAuthority(role.name())).collect(Collectors.toList());
		
		
		User user = new User(users.getEmail(),users.getPassword(),authorities);
	
		return user;
	}
	
	public void deleteUser(Long id) {
		
		Users user = usersRepository.findById(id).orElseThrow(()->new CustomeException("user not found with id"+id));
		usersRepository.deleteById(id);
	}
	
	public Users findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

}
