package org.pac4j.core.context;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;


/**
 * Tests {@link JEEContext}.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public final class JEEContextTest implements TestsConstants {
    private HttpServletRequest request;

    private HttpServletResponse response;

    @Test
    public void testGetHeaderNameMatches() {
        internalTestGetHeader("kEy");
    }

    @Test
    public void testGetHeaderNameStriclyMatches() {
        internalTestGetHeader(TestsConstants.KEY);
    }
}

