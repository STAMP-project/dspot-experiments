package org.apereo.cas.interrupt;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.LinkedHashMap;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link JsonResourceInterruptInquirerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class JsonResourceInterruptInquirerTests {
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Test
    public void verifyResponseCanSerializeIntoJson() throws Exception {
        val map = new LinkedHashMap<String, InterruptResponse>();
        var response = new InterruptResponse("Message", CollectionUtils.wrap("text", "link", "text2", "link2"), false, true);
        map.put("casuser", response);
        val f = File.createTempFile("interrupt", "json");
        JsonResourceInterruptInquirerTests.MAPPER.writer().withDefaultPrettyPrinter().writeValue(f, map);
        Assertions.assertTrue(f.exists());
        val q = new JsonResourceInterruptInquirer(new org.springframework.core.io.FileSystemResource(f));
        response = q.inquire(CoreAuthenticationTestUtils.getAuthentication("casuser"), CoreAuthenticationTestUtils.getRegisteredService(), CoreAuthenticationTestUtils.getService(), CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword(), new MockRequestContext());
        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.isBlock());
        Assertions.assertTrue(response.isSsoEnabled());
        Assertions.assertEquals(2, response.getLinks().size());
    }
}

