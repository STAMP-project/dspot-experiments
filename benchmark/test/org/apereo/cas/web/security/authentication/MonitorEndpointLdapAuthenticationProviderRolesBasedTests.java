package org.apereo.cas.web.security.authentication;


import java.util.Collections;
import lombok.val;
import org.apereo.cas.util.junit.EnabledIfContinuousIntegration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link MonitorEndpointLdapAuthenticationProviderRolesBasedTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@TestPropertySource(properties = { "cas.monitor.endpoints.ldap.ldapAuthz.roleAttribute=roomNumber", "cas.monitor.endpoints.ldap.ldapAuthz.searchFilter=cn={user}", "cas.monitor.endpoints.ldap.ldapAuthz.baseDn=ou=people,dc=example,dc=org", "cas.monitor.endpoints.ldap.ldapAuthz.rolePrefix=ROLE_" })
@EnabledIfContinuousIntegration
public class MonitorEndpointLdapAuthenticationProviderRolesBasedTests extends BaseMonitorEndpointLdapAuthenticationProviderTests {
    @Test
    public void verifyAuthorizedByRole() {
        val securityProperties = new SecurityProperties();
        securityProperties.getUser().setRoles(Collections.singletonList("ROLE_888"));
        val provider = new MonitorEndpointLdapAuthenticationProvider(casProperties.getMonitor().getEndpoints().getLdap(), securityProperties);
        val token = provider.authenticate(new UsernamePasswordAuthenticationToken("authzcas", "123456"));
        Assertions.assertNotNull(token);
    }

    @Test
    public void verifyUnauthorizedByRole() {
        val securityProperties = new SecurityProperties();
        securityProperties.getUser().setRoles(Collections.singletonList("SOME_BAD_ROLE"));
        val provider = new MonitorEndpointLdapAuthenticationProvider(casProperties.getMonitor().getEndpoints().getLdap(), securityProperties);
        Assertions.assertThrows(BadCredentialsException.class, () -> provider.authenticate(new UsernamePasswordAuthenticationToken("authzcas", "123456")));
    }

    @Test
    public void verifyUserNotFound() {
        val securityProperties = new SecurityProperties();
        securityProperties.getUser().setRoles(Collections.singletonList("SOME_BAD_ROLE"));
        val provider = new MonitorEndpointLdapAuthenticationProvider(casProperties.getMonitor().getEndpoints().getLdap(), securityProperties);
        Assertions.assertThrows(BadCredentialsException.class, () -> provider.authenticate(new UsernamePasswordAuthenticationToken("UNKNOWN_USER", "123456")));
    }

    @Test
    public void verifyUserBadPassword() {
        val securityProperties = new SecurityProperties();
        securityProperties.getUser().setRoles(Collections.singletonList("SOME_BAD_ROLE"));
        val provider = new MonitorEndpointLdapAuthenticationProvider(casProperties.getMonitor().getEndpoints().getLdap(), securityProperties);
        Assertions.assertThrows(BadCredentialsException.class, () -> provider.authenticate(new UsernamePasswordAuthenticationToken("authzcas", "BAD_PASSWORD")));
    }
}

