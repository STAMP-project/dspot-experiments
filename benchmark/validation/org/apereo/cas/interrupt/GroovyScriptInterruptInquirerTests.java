package org.apereo.cas.interrupt;


import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link GroovyScriptInterruptInquirerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Tag("Groovy")
public class GroovyScriptInterruptInquirerTests {
    @Test
    public void verifyResponseCanBeFoundFromGroovy() {
        val q = new GroovyScriptInterruptInquirer(new ClassPathResource("interrupt.groovy"));
        val response = q.inquire(CoreAuthenticationTestUtils.getAuthentication("casuser"), CoreAuthenticationTestUtils.getRegisteredService(), CoreAuthenticationTestUtils.getService(), CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword(), new MockRequestContext());
        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.isBlock());
        Assertions.assertTrue(response.isSsoEnabled());
        Assertions.assertEquals(2, response.getLinks().size());
    }
}

