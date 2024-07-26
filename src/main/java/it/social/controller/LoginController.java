package it.social.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.*;

import it.social.dto.user.UserDTO;
import it.social.login.payload.LoginRequest;
import it.social.login.payload.LoginResponse;
import it.social.login.payload.MessageResponse;
import it.social.security.jwt.JwtUtils;
import it.social.security.service.UserDetailsImpl;
import it.social.service.UserService;
import jakarta.validation.Valid;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class LoginController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Operation(summary = "User Sign In",
               description = "Authenticate the user with username and password, and generate a JWT token for session management. The user must not be already signed in.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully authenticated the user and generated JWT token"),
        @ApiResponse(responseCode = "400", description = "Bad Request: User is already signed in")
    })
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(
            @Valid @Parameter(description = "Login request containing username and password", required = true) @RequestBody LoginRequest loginRequest,
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

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new LoginResponse(userDetails.getId(), userDetails.getUsername()));
    }

    @Operation(summary = "User Registration",
               description = "Register a new user with a username, password, and email. The username must be unique.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully registered the user"),
        @ApiResponse(responseCode = "400", description = "Bad Request: Username is already taken")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(
            @Valid @Parameter(description = "Registration request containing username, password, and email", required = true) @RequestBody UserDTO signUpRequest) {

        if (userService.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        String registeredUser = userService.addUser(signUpRequest);
        return ResponseEntity.ok(new MessageResponse("User " + registeredUser + " registered successfully!"));
    }

    @Operation(summary = "User Sign Out",
               description = "Logout the user and clear the JWT token to end the session.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully signed out the user")
    })
    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }

    @Operation(summary = "Who Am I",
               description = "Retrieve details of the currently authenticated user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user details")
    })
    @GetMapping("/whoami")
    public ResponseEntity<?> whoAmI(
            @AuthenticationPrincipal @Parameter(hidden = true) UserDetailsImpl userDetails) {
        return ResponseEntity.ok(new LoginResponse(userDetails.getId(), userDetails.getUsername()));
    }
}
