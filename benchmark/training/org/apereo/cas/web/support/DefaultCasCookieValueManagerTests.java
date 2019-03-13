package org.apereo.cas.web.support;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import lombok.val;
import org.apereo.inspektr.common.web.ClientInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Daniel Frett
 * @since 5.3.0
 */
public class DefaultCasCookieValueManagerTests {
    private static final String CLIENT_IP = "127.0.0.1";

    private static final String USER_AGENT = "Test-Client/1.0.0";

    private static final String VALUE = "cookieValue";

    private DefaultCasCookieValueManager cookieValueManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ClientInfo clientInfo;

    @Mock
    private Cookie cookie;

    @Test
    public void verifyEncodeAndDecodeCookie() {
        whenGettingClientIp().thenReturn(DefaultCasCookieValueManagerTests.CLIENT_IP);
        whenGettingUserAgent().thenReturn(DefaultCasCookieValueManagerTests.USER_AGENT);
        // test encoding first
        val encoded = cookieValueManager.buildCookieValue(DefaultCasCookieValueManagerTests.VALUE, request);
        Assertions.assertEquals(String.join("@", DefaultCasCookieValueManagerTests.VALUE, DefaultCasCookieValueManagerTests.CLIENT_IP, DefaultCasCookieValueManagerTests.USER_AGENT), encoded);
        // now test decoding the cookie
        Mockito.when(cookie.getValue()).thenReturn(encoded);
        val decoded = cookieValueManager.obtainCookieValue(cookie, request);
        Assertions.assertEquals(DefaultCasCookieValueManagerTests.VALUE, decoded);
    }
}

