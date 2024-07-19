package it.iakta.social.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import it.iakta.social.service.UserService;

@RestController
public class UserUtilsController {

	@Autowired
    private UserService userService;

    @GetMapping("/users")
    public  ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
