package org.apereo.cas.support.rest;


import java.util.Collection;
import javax.security.auth.login.FailedLoginException;
import lombok.val;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.DefaultAuthenticationResultBuilder;
import org.apereo.cas.authentication.DefaultPrincipalElectionStrategy;
import org.apereo.cas.support.rest.resources.UserAuthenticationResource;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;


/**
 * This is {@link UserAuthenticationResourceTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@ExtendWith(MockitoExtension.class)
@DirtiesContext
public class UserAuthenticationResourceTests {
    private static final String TICKETS_RESOURCE_URL = "/cas/v1/users";

    @Mock
    private AuthenticationSystemSupport authenticationSupport;

    @InjectMocks
    private UserAuthenticationResource userAuthenticationResource;

    private MockMvc mockMvc;

    @Test
    public void verifyStatus() throws Exception {
        val result = new DefaultAuthenticationResultBuilder().collect(CoreAuthenticationTestUtils.getAuthentication()).build(new DefaultPrincipalElectionStrategy());
        Mockito.when(authenticationSupport.handleAndFinalizeSingleAuthenticationTransaction(ArgumentMatchers.any(), ArgumentMatchers.anyCollection())).thenReturn(result);
        this.mockMvc.perform(post(UserAuthenticationResourceTests.TICKETS_RESOURCE_URL).param("username", "casuser").param("password", "Mellon")).andExpect(status().isOk());
    }

    @Test
    public void verifyStatusAuthnFails() throws Exception {
        this.mockMvc.perform(post(UserAuthenticationResourceTests.TICKETS_RESOURCE_URL).param("username", "casuser").param("password", "Mellon")).andExpect(status().isInternalServerError());
    }

    @Test
    public void verifyStatusAuthnException() throws Exception {
        val ex = new org.apereo.cas.authentication.AuthenticationException(CollectionUtils.wrap("error", new FailedLoginException()));
        Mockito.when(authenticationSupport.handleAndFinalizeSingleAuthenticationTransaction(ArgumentMatchers.any(), ArgumentMatchers.any(Collection.class))).thenThrow(ex);
        this.mockMvc.perform(post(UserAuthenticationResourceTests.TICKETS_RESOURCE_URL).param("username", "casuser").param("password", "Mellon")).andExpect(status().isUnauthorized());
    }
}

