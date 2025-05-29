package com.tough.example_authentication_service.service;

import com.tough.example_authentication_service.model.User;
import com.tough.example_authentication_service.oauth2.CustomOidcUserService;
import com.tough.example_authentication_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomOidcUserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    CustomOidcUserService customOidcUserService;

    @Test
    void testLoadUserSuccessNewUser() {

        User user = new User();
        user.setRoles(Set.of("ROLE_USER"));

        when(userRepository.findByIdpSubject(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        OidcUserRequest mockRequest = setupSpringSecurityMocks();

        OidcUser oidcUser = customOidcUserService.loadUser(mockRequest);

        assertNotNull(oidcUser);
        assertEquals(1, oidcUser.getAuthorities().size());
        assertTrue(oidcUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertEquals("test-id-token", oidcUser.getIdToken().getTokenValue());
    }

    @Test
    void testLoadUserSuccessExistingUser() {
        User user = new User();
        user.setRoles(Set.of("ROLE_USER"));

        when(userRepository.findByIdpSubject(anyString())).thenReturn(Optional.of(user));
        OidcUserRequest mockRequest = setupSpringSecurityMocks();

        OidcUser oidcUser = customOidcUserService.loadUser(mockRequest);

        assertNotNull(oidcUser);
        assertEquals(1, oidcUser.getAuthorities().size());
        assertTrue(oidcUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertEquals("test-id-token", oidcUser.getIdToken().getTokenValue());
    }

    private OidcUserRequest setupSpringSecurityMocks() {
        OidcUserRequest mockRequest = mock(OidcUserRequest.class);
        ClientRegistration mockClientReg = mock(ClientRegistration.class);
        ClientRegistration.ProviderDetails mockProviderDetails = mock(ClientRegistration.ProviderDetails.class);
        ClientRegistration.ProviderDetails.UserInfoEndpoint mockUserInfoEndpoint = mock(ClientRegistration.ProviderDetails.UserInfoEndpoint.class);

        when(mockRequest.getClientRegistration()).thenReturn(mockClientReg);
        when(mockClientReg.getProviderDetails()).thenReturn(mockProviderDetails);
        when(mockProviderDetails.getUserInfoEndpoint()).thenReturn(mockUserInfoEndpoint);

        when(mockRequest.getIdToken()).thenReturn(mockOidcIdToken());
        when(mockRequest.getAccessToken()).thenReturn(mockAccessToken());
        return mockRequest;
    }

    private OidcIdToken mockOidcIdToken() {
        return new OidcIdToken("test-id-token",
                Instant.now(),
                Instant.now().plusSeconds(600),
                Map.of("sub", "user123", "email", "test@example.com")
        );
    }

    private OAuth2AccessToken mockAccessToken() {
        return new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "token",
                Instant.now(),
                Instant.now().plusSeconds(600)
        );
    }
}