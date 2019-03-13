/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. See accompanying LICENSE file.
 */
package org.apache.hadoop.security.authentication.server;


import AuthenticationToken.ANONYMOUS;
import PseudoAuthenticationHandler.ANONYMOUS_ALLOWED;
import PseudoAuthenticationHandler.TYPE;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestPseudoAuthenticationHandler {
    @Test
    public void testInit() throws Exception {
        PseudoAuthenticationHandler handler = new PseudoAuthenticationHandler();
        try {
            Properties props = new Properties();
            props.setProperty(ANONYMOUS_ALLOWED, "false");
            handler.init(props);
            Assert.assertEquals(false, handler.getAcceptAnonymous());
        } finally {
            handler.destroy();
        }
    }

    @Test
    public void testType() throws Exception {
        PseudoAuthenticationHandler handler = new PseudoAuthenticationHandler();
        Assert.assertEquals(TYPE, handler.getType());
    }

    @Test
    public void testAnonymousOn() throws Exception {
        PseudoAuthenticationHandler handler = new PseudoAuthenticationHandler();
        try {
            Properties props = new Properties();
            props.setProperty(ANONYMOUS_ALLOWED, "true");
            handler.init(props);
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            AuthenticationToken token = handler.authenticate(request, response);
            Assert.assertEquals(ANONYMOUS, token);
        } finally {
            handler.destroy();
        }
    }

    @Test
    public void testAnonymousOff() throws Exception {
        PseudoAuthenticationHandler handler = new PseudoAuthenticationHandler();
        try {
            Properties props = new Properties();
            props.setProperty(ANONYMOUS_ALLOWED, "false");
            handler.init(props);
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            AuthenticationToken token = handler.authenticate(request, response);
            Assert.assertNull(token);
        } finally {
            handler.destroy();
        }
    }

    @Test
    public void testUserNameAnonymousOff() throws Exception {
        _testUserName(false);
    }

    @Test
    public void testUserNameAnonymousOn() throws Exception {
        _testUserName(true);
    }
}

