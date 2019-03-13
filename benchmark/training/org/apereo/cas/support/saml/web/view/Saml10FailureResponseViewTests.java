package org.apereo.cas.support.saml.web.view;


import java.util.Collections;
import lombok.val;
import org.apereo.cas.support.saml.AbstractOpenSamlTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Unit test for {@link Saml10FailureResponseView} class
 *
 * @author Scott Battaglia
 * @author Marvin S. Addison
 * @since 3.1
 */
public class Saml10FailureResponseViewTests extends AbstractOpenSamlTests {
    private Saml10FailureResponseView view;

    @Test
    public void verifyResponse() throws Exception {
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        request.addParameter("TARGET", "service");
        val description = "Validation failed";
        this.view.renderMergedOutputModel(Collections.singletonMap("description", description), request, response);
        val responseText = response.getContentAsString();
        Assertions.assertTrue(responseText.contains("Status"));
        Assertions.assertTrue(responseText.contains(description));
    }
}

