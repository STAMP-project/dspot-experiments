package org.apereo.cas.services;


import java.util.Collections;
import lombok.val;
import org.apereo.cas.CoreAttributesTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link ScriptedRegisteredServiceAttributeReleasePolicyTests}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
public class ScriptedRegisteredServiceAttributeReleasePolicyTests {
    @Test
    public void verifyInlineScript() {
        val p = new ScriptedRegisteredServiceAttributeReleasePolicy();
        p.setScriptFile("groovy { return attributes }");
        val principal = CoreAttributesTestUtils.getPrincipal("cas", Collections.singletonMap("attribute", "value"));
        val attrs = p.getAttributes(principal, CoreAttributesTestUtils.getService(), CoreAttributesTestUtils.getRegisteredService());
        Assertions.assertEquals(attrs.size(), principal.getAttributes().size());
    }
}

