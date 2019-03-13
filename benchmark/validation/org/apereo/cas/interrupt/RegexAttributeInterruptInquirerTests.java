package org.apereo.cas.interrupt;


import RegisteredServiceProperty.RegisteredServiceProperties.SKIP_INTERRUPT_NOTIFICATIONS;
import java.util.LinkedHashMap;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.services.DefaultRegisteredServiceProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link RegexAttributeInterruptInquirerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class RegexAttributeInterruptInquirerTests {
    @Test
    public void verifyResponseCanBeFoundFromAttributes() {
        val q = new RegexAttributeInterruptInquirer("member..", "CA.|system");
        val response = q.inquire(CoreAuthenticationTestUtils.getAuthentication("casuser"), CoreAuthenticationTestUtils.getRegisteredService(), CoreAuthenticationTestUtils.getService(), CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword(), new MockRequestContext());
        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.isBlock());
        Assertions.assertTrue(response.isSsoEnabled());
        Assertions.assertTrue(response.isInterrupt());
    }

    @Test
    public void verifyInterruptSkipped() {
        val q = new RegexAttributeInterruptInquirer("member..", "CA.|system");
        val registeredService = CoreAuthenticationTestUtils.getRegisteredService();
        val properties = new LinkedHashMap<String, org.apereo.cas.services.RegisteredServiceProperty>();
        val value = new DefaultRegisteredServiceProperty();
        value.addValue(Boolean.TRUE.toString());
        properties.put(SKIP_INTERRUPT_NOTIFICATIONS.getPropertyName(), value);
        Mockito.when(registeredService.getProperties()).thenReturn(properties);
        val response = q.inquire(CoreAuthenticationTestUtils.getAuthentication("casuser"), registeredService, CoreAuthenticationTestUtils.getService(), CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword(), new MockRequestContext());
        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.isInterrupt());
    }
}

