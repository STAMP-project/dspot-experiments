package org.apereo.cas.util.services;


import lombok.val;
import org.apereo.cas.services.util.RegisteredServiceJsonSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link DefaultRegisteredServiceJsonSerializerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
public class DefaultRegisteredServiceJsonSerializerTests {
    @Test
    public void checkNullability() {
        val zer = new RegisteredServiceJsonSerializer();
        val json = "    {\n" + (((("        \"@class\" : \"org.apereo.cas.services.RegexRegisteredService\",\n" + "            \"serviceId\" : \"^https://xyz.*\",\n") + "            \"name\" : \"XYZ\",\n") + "            \"id\" : \"20161214\"\n") + "    }");
        val s = zer.from(json);
        Assertions.assertNotNull(s);
        Assertions.assertNotNull(s.getAccessStrategy());
        Assertions.assertNotNull(s.getAttributeReleasePolicy());
        Assertions.assertNotNull(s.getProxyPolicy());
        Assertions.assertNotNull(s.getUsernameAttributeProvider());
    }
}

