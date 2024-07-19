package it.iakta.social.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.iakta.social.dto.UserDTO;
import it.iakta.social.entity.User;
import it.iakta.social.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

//    public User addUser(UserDTO userDTO) {
//        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
//        User user = new User(userDTO.getUsername(), encryptedPassword);
//        User user = new User(userDTO.getUsername(), userDTO.getPassword());
//        return userRepository.save(user);
//    }
    public String addUser(UserDTO userDTO) {
    	String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        User user = new User(userDTO.getUsername(), encryptedPassword);
        return userRepository.save(user).getUsername();
    }
    public List<UserDTO> getAllUsers() {
        List<UserDTO> users = new ArrayList<>();
        userRepository.findAll().forEach(user -> 
            users.add(new UserDTO(user.getUsername(), user.getPassword()))
        );
        return users;
    }
    
}