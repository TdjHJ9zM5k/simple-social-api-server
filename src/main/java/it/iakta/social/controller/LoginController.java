package it.iakta.social.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.iakta.social.dto.user.UserDTO;
import it.iakta.social.login.payload.LoginRequest;
import it.iakta.social.login.payload.LoginResponse;
import it.iakta.social.login.payload.MessageResponse;
import it.iakta.social.security.jwt.JwtUtils;
import it.iakta.social.security.service.UserDetailsImpl;
import it.iakta.social.service.UserService;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class LoginController {
	
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserService userService;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;
  
  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(
		  @Valid @RequestBody LoginRequest loginRequest,
		  @AuthenticationPrincipal UserDetailsImpl loginUserDetailsImpl) {

	if (loginUserDetailsImpl != null) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse("Error: You are already signed in!"));
	}
    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .body(new LoginResponse(userDetails.getId(),
        						userDetails.getUsername()
			        		));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO signUpRequest) {
    if (userService.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }
    String registeredUser = userService.addUser(signUpRequest);
    return ResponseEntity.ok(new MessageResponse("User "+registeredUser+" registered successfully!"));
  }

  @PostMapping("/signout")
  public ResponseEntity<?> logoutUser() {
    ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(new MessageResponse("You've been signed out!"));
  }
  
  @GetMapping("/whoami")
  public ResponseEntity<?> whoAmI(
		  @AuthenticationPrincipal UserDetailsImpl userDetails
		  ) {
	return ResponseEntity.ok(new LoginResponse(userDetails.getId(), userDetails.getUsername()));
  }
  
}