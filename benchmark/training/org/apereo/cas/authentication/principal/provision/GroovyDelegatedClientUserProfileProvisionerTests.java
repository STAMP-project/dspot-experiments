package org.apereo.cas.authentication.principal.provision;


import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.core.io.ClassPathResource;


/**
 * This is {@link GroovyDelegatedClientUserProfileProvisionerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
@Tag("Groovy")
public class GroovyDelegatedClientUserProfileProvisionerTests {
    @Test
    public void verifyOperation() {
        val p = new GroovyDelegatedClientUserProfileProvisioner(new ClassPathResource("delegated-provisioner.groovy"));
        val commonProfile = new CommonProfile();
        commonProfile.setClientName("CasClient");
        commonProfile.setId("testuser");
        val client = new org.pac4j.cas.client.CasClient(new CasConfiguration("http://cas.example.org"));
        p.execute(CoreAuthenticationTestUtils.getPrincipal(), commonProfile, client);
    }
}

