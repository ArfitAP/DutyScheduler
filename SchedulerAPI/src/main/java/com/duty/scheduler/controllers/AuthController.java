package com.duty.scheduler.controllers;


import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duty.scheduler.models.ERole;
import com.duty.scheduler.models.Role;
import com.duty.scheduler.models.User;
import com.duty.scheduler.payload.request.GoogleLoginRequest;
import com.duty.scheduler.payload.request.LoginRequest;
import com.duty.scheduler.payload.request.SignupRequest;
import com.duty.scheduler.payload.request.UsernameSetupRequest;
import com.duty.scheduler.payload.response.JwtResponse;
import com.duty.scheduler.payload.response.MessageResponse;
import com.duty.scheduler.repository.RoleRepository;
import com.duty.scheduler.repository.UserRepository;
import com.duty.scheduler.security.jwt.JwtUtils;
import com.duty.scheduler.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	MessageSource messageSource;

	@Value("${dutyscheduler.app.googleClientId}")
	private String googleClientId;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt,
												 userDetails.getId(),
												 userDetails.getUsername(),
												 userDetails.getEmail(),
												 roles));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest, Locale locale) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse(messageSource.getMessage("error.username.taken", null, locale)));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse(messageSource.getMessage("error.email.taken", null, locale)));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(),
							 signUpRequest.getEmail(),
							 encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.role.not.found", null, locale)));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.role.not.found", null, locale)));
					roles.add(adminRole);

					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.role.not.found", null, locale)));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse(messageSource.getMessage("success.user.registered", null, locale)));
	}

	@PostMapping("/google")
	public ResponseEntity<?> googleAuth(@Valid @RequestBody GoogleLoginRequest googleLoginRequest, Locale locale) {
		try {
			GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
					new NetHttpTransport(), GsonFactory.getDefaultInstance())
					.setAudience(Collections.singletonList(googleClientId))
					.build();

			GoogleIdToken idToken = verifier.verify(googleLoginRequest.getCredential());
			if (idToken == null) {
				return ResponseEntity.badRequest()
						.body(new MessageResponse(messageSource.getMessage("error.google.invalid.token", null, locale)));
			}

			GoogleIdToken.Payload payload = idToken.getPayload();
			String email = payload.getEmail();
			String googleId = payload.getSubject();

			Optional<User> existingUser = userRepository.findByEmail(email);

			if (existingUser.isPresent()) {
				User user = existingUser.get();

				if (googleLoginRequest.isRegistration()) {
					return ResponseEntity.badRequest()
							.body(new MessageResponse(messageSource.getMessage("error.google.email.registered", null, locale)));
				}

				boolean usernameRequired = user.getUsername() == null || user.getUsername().isBlank();

				List<String> roles = user.getRoles().stream()
						.map(role -> role.getName().name())
						.collect(Collectors.toList());

				String jwt = usernameRequired ? "" : jwtUtils.generateJwtTokenForUsername(user.getUsername());

				return ResponseEntity.ok(new JwtResponse(jwt,
						user.getId(),
						user.getUsername(),
						user.getEmail(),
						roles,
						usernameRequired));
			} else {
				User newUser = User.createGoogleUser(email, googleId);

				Set<Role> roles = new HashSet<>();
				Role userRole = roleRepository.findByName(ERole.ROLE_USER)
						.orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.role.not.found", null, locale)));
				roles.add(userRole);
				newUser.setRoles(roles);
				userRepository.save(newUser);

				List<String> roleNames = roles.stream()
						.map(role -> role.getName().name())
						.collect(Collectors.toList());

				return ResponseEntity.ok(new JwtResponse("",
						newUser.getId(),
						null,
						newUser.getEmail(),
						roleNames,
						true));
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(new MessageResponse(messageSource.getMessage("error.google.auth.failed", null, locale) + " " + e.getMessage()));
		}
	}

	@PostMapping("/set-username/{userId}")
	public ResponseEntity<?> setUsernameForUser(@Valid @RequestBody UsernameSetupRequest request,
												@org.springframework.web.bind.annotation.PathVariable Long userId,
												Locale locale) {
		if (userRepository.existsByUsername(request.getUsername())) {
			return ResponseEntity.badRequest()
					.body(new MessageResponse(messageSource.getMessage("error.username.already.taken", null, locale)));
		}

		Optional<User> optionalUser = userRepository.findById(userId);
		if (optionalUser.isEmpty()) {
			return ResponseEntity.badRequest()
					.body(new MessageResponse(messageSource.getMessage("error.user.not.found", null, locale)));
		}

		User user = optionalUser.get();
		if (!"google".equals(user.getProvider())) {
			return ResponseEntity.badRequest()
					.body(new MessageResponse(messageSource.getMessage("error.username.google.only", null, locale)));
		}

		if (user.getUsername() != null && !user.getUsername().isBlank()) {
			return ResponseEntity.badRequest()
					.body(new MessageResponse(messageSource.getMessage("error.username.already.set", null, locale)));
		}

		user.setUsername(request.getUsername());
		userRepository.save(user);

		List<String> roles = user.getRoles().stream()
				.map(role -> role.getName().name())
				.collect(Collectors.toList());

		String jwt = jwtUtils.generateJwtTokenForUsername(user.getUsername());

		return ResponseEntity.ok(new JwtResponse(jwt,
				user.getId(),
				user.getUsername(),
				user.getEmail(),
				roles));
	}
}
