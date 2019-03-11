package org.apereo.cas.web.flow.actions;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 *
 *
 * @author Auke van Leeuwen
 * @since 4.2.0
 */
public class CasDefaultFlowUrlHandlerTests {
    private final CasDefaultFlowUrlHandler urlHandler = new CasDefaultFlowUrlHandler();

    private final MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    public void verifyCreateFlowExecutionUrlWithSingleValuedAttributes() {
        setupRequest("/cas", "/app", "/foo");
        request.setParameter("bar", "baz");
        request.setParameter("qux", "quux");
        val url = urlHandler.createFlowExecutionUrl("foo", "12345", request);
        Assertions.assertEquals("/cas/app/foo?bar=baz&qux=quux&execution=12345", url);
    }

    @Test
    public void verifyCreateFlowExecutionUrlWithMultiValuedAttributes() {
        setupRequest("/cas", "/app", "/foo");
        request.setParameter("bar", "baz1", "baz2");
        request.setParameter("qux", "quux");
        val url = urlHandler.createFlowExecutionUrl("foo", "12345", request);
        Assertions.assertEquals("/cas/app/foo?bar=baz1&bar=baz2&qux=quux&execution=12345", url);
    }
}

