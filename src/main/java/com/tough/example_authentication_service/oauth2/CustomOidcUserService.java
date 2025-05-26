package com.tough.example_authentication_service.oauth2;

import com.tough.example_authentication_service.model.User;
import com.tough.example_authentication_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CustomOidcUserService extends OidcUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomOidcUserService.class);

    private final UserRepository userRepository;

    @Autowired
    public CustomOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        User appUser = findOrCreateUser(oidcUser);

        return new DefaultOidcUser(
                appUser.getAuthorities(),
                userRequest.getIdToken(),
                oidcUser.getUserInfo(),
                "sub"
        );
    }

    private User findOrCreateUser(OidcUser oidcUser) {
        String googleSubject = oidcUser.getAttribute("sub");

        return userRepository.findByGoogleSubject(googleSubject).orElseGet(() -> {
            User newUser = new User();
            newUser.setGoogleSubject(googleSubject);
            newUser.setEmail(oidcUser.getAttribute("email"));
            newUser.setName(oidcUser.getAttribute("name"));
            newUser.setRoles(Set.of("ROLE_USER"));

            LOGGER.info("Creating new user, Google subject: {}, Name: {}", newUser.getGoogleSubject(), newUser.getName());

            return userRepository.save(newUser);
        });
    }
}