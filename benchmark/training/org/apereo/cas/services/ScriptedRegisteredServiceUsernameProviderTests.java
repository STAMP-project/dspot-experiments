package org.apereo.cas.services;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link ScriptedRegisteredServiceUsernameProviderTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class ScriptedRegisteredServiceUsernameProviderTests {
    @Test
    public void verifyUsernameProvider() {
        val p = new ScriptedRegisteredServiceUsernameProvider();
        p.setScript("file:src/test/resources/uidscript.groovy");
        val id = p.resolveUsername(RegisteredServiceTestUtils.getPrincipal(), RegisteredServiceTestUtils.getService(), RegisteredServiceTestUtils.getRegisteredService());
        Assertions.assertEquals("test", id);
    }
}

