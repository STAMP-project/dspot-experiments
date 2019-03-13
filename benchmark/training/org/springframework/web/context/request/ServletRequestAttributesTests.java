/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.context.request;


import RequestAttributes.SCOPE_REQUEST;
import RequestAttributes.SCOPE_SESSION;
import java.io.Serializable;
import java.math.BigInteger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpSession;


/**
 *
 *
 * @author Rick Evans
 * @author Juergen Hoeller
 */
public class ServletRequestAttributesTests {
    private static final String KEY = "ThatThingThatThing";

    @SuppressWarnings("serial")
    private static final Serializable VALUE = new Serializable() {};

    @Test(expected = IllegalArgumentException.class)
    public void ctorRejectsNullArg() throws Exception {
        new ServletRequestAttributes(null);
    }

    @Test
    public void setRequestScopedAttribute() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes attrs = new ServletRequestAttributes(request);
        attrs.setAttribute(ServletRequestAttributesTests.KEY, ServletRequestAttributesTests.VALUE, SCOPE_REQUEST);
        Object value = request.getAttribute(ServletRequestAttributesTests.KEY);
        Assert.assertSame(ServletRequestAttributesTests.VALUE, value);
    }

    @Test
    public void setRequestScopedAttributeAfterCompletion() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes attrs = new ServletRequestAttributes(request);
        request.close();
        try {
            attrs.setAttribute(ServletRequestAttributesTests.KEY, ServletRequestAttributesTests.VALUE, SCOPE_REQUEST);
            Assert.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException ex) {
            // expected
        }
    }

    @Test
    public void setSessionScopedAttribute() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(ServletRequestAttributesTests.KEY, ServletRequestAttributesTests.VALUE);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        ServletRequestAttributes attrs = new ServletRequestAttributes(request);
        attrs.setAttribute(ServletRequestAttributesTests.KEY, ServletRequestAttributesTests.VALUE, SCOPE_SESSION);
        Assert.assertSame(ServletRequestAttributesTests.VALUE, session.getAttribute(ServletRequestAttributesTests.KEY));
    }

    @Test
    public void setSessionScopedAttributeAfterCompletion() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(ServletRequestAttributesTests.KEY, ServletRequestAttributesTests.VALUE);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        ServletRequestAttributes attrs = new ServletRequestAttributes(request);
        Assert.assertSame(ServletRequestAttributesTests.VALUE, attrs.getAttribute(ServletRequestAttributesTests.KEY, SCOPE_SESSION));
        attrs.requestCompleted();
        request.close();
        attrs.setAttribute(ServletRequestAttributesTests.KEY, ServletRequestAttributesTests.VALUE, SCOPE_SESSION);
        Assert.assertSame(ServletRequestAttributesTests.VALUE, session.getAttribute(ServletRequestAttributesTests.KEY));
    }

    @Test
    public void getSessionScopedAttributeDoesNotForceCreationOfSession() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        ServletRequestAttributes attrs = new ServletRequestAttributes(request);
        Object value = attrs.getAttribute(ServletRequestAttributesTests.KEY, SCOPE_SESSION);
        Assert.assertNull(value);
        Mockito.verify(request).getSession(false);
    }

    @Test
    public void removeSessionScopedAttribute() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(ServletRequestAttributesTests.KEY, ServletRequestAttributesTests.VALUE);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        ServletRequestAttributes attrs = new ServletRequestAttributes(request);
        attrs.removeAttribute(ServletRequestAttributesTests.KEY, SCOPE_SESSION);
        Object value = session.getAttribute(ServletRequestAttributesTests.KEY);
        Assert.assertNull(value);
    }

    @Test
    public void removeSessionScopedAttributeDoesNotForceCreationOfSession() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        ServletRequestAttributes attrs = new ServletRequestAttributes(request);
        attrs.removeAttribute(ServletRequestAttributesTests.KEY, SCOPE_SESSION);
        Mockito.verify(request).getSession(false);
    }

    @Test
    public void updateAccessedAttributes() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        BDDMockito.given(request.getSession(ArgumentMatchers.anyBoolean())).willReturn(session);
        BDDMockito.given(session.getAttribute(ServletRequestAttributesTests.KEY)).willReturn(ServletRequestAttributesTests.VALUE);
        ServletRequestAttributes attrs = new ServletRequestAttributes(request);
        Assert.assertSame(ServletRequestAttributesTests.VALUE, attrs.getAttribute(ServletRequestAttributesTests.KEY, SCOPE_SESSION));
        attrs.requestCompleted();
        Mockito.verify(session, Mockito.times(2)).getAttribute(ServletRequestAttributesTests.KEY);
        Mockito.verify(session).setAttribute(ServletRequestAttributesTests.KEY, ServletRequestAttributesTests.VALUE);
        Mockito.verifyNoMoreInteractions(session);
    }

    @Test
    public void skipImmutableString() {
        doSkipImmutableValue("someString");
    }

    @Test
    public void skipImmutableCharacter() {
        doSkipImmutableValue(new Character('x'));
    }

    @Test
    public void skipImmutableBoolean() {
        doSkipImmutableValue(Boolean.TRUE);
    }

    @Test
    public void skipImmutableInteger() {
        doSkipImmutableValue(new Integer(1));
    }

    @Test
    public void skipImmutableFloat() {
        doSkipImmutableValue(new Float(1.1));
    }

    @Test
    public void skipImmutableBigInteger() {
        doSkipImmutableValue(new BigInteger("1"));
    }
}

