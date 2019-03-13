/**
 * This file is part of the Heritrix web crawler (crawler.archive.org).
 *
 *  Licensed to the Internet Archive (IA) by one or more individual
 *  contributors.
 *
 *  The IA licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.archive.modules.fetcher;


import HttpServletResponse.SC_OK;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import junit.framework.TestCase;
import org.archive.crawler.prefetch.PreconditionEnforcer;
import org.archive.modules.CrawlURI;
import org.archive.modules.credential.HtmlFormCredential;
import org.mortbay.jetty.servlet.SessionHandler;


/* Somewhat redundant to org.archive.crawler.selftest.FormAuthSelfTest, but 
the code is written, it's easier to run in eclipse, and no doubt tests 
somewhat different stuff.
 */
public class FormAuthTest extends TestCase {
    private static Logger logger = Logger.getLogger(FormAuthTest.class.getName());

    protected static final String DEFAULT_PAYLOAD_STRING = "abcdefghijklmnopqrstuvwxyz0123456789\n";

    protected static final String FORM_AUTH_REALM = "form-auth-realm";

    protected static final String FORM_AUTH_ROLE = "form-auth-role";

    protected static final String FORM_AUTH_LOGIN = "form-auth-login";

    protected static final String FORM_AUTH_PASSWORD = "form-auth-password";

    protected FetchHTTP fetchHttp;

    public void testFormAuth() throws Exception {
        startHttpServers();
        HtmlFormCredential cred = new HtmlFormCredential();
        cred.setDomain("localhost:7779");
        cred.setLoginUri("/j_security_check");
        HashMap<String, String> formItems = new HashMap<String, String>();
        formItems.put("j_username", FormAuthTest.FORM_AUTH_LOGIN);
        formItems.put("j_password", FormAuthTest.FORM_AUTH_PASSWORD);
        cred.setFormItems(formItems);
        getFetcher().getCredentialStore().getCredentials().put("form-auth-credential", cred);
        CrawlURI curi = makeCrawlURI("http://localhost:7779/");
        getFetcher().process(curi);
        FormAuthTest.logger.info((('\n' + (httpRequestString(curi))) + (contentString(curi))));
        runDefaultChecks(curi, "hostHeader");
        // jetty needs us to hit a restricted url so it can redirect to the
        // login page and remember where to redirect back to after successful
        // login (if not we get a NPE within jetty)
        curi = makeCrawlURI("http://localhost:7779/auth/1");
        getFetcher().process(curi);
        FormAuthTest.logger.info(((('\n' + (httpRequestString(curi))) + "\n\n") + (rawResponseString(curi))));
        TestCase.assertEquals(302, curi.getFetchStatus());
        TestCase.assertTrue(curi.getHttpResponseHeader("Location").startsWith("http://localhost:7779/login.html"));
        PreconditionEnforcer preconditionEnforcer = new PreconditionEnforcer();
        preconditionEnforcer.setServerCache(getFetcher().getServerCache());
        preconditionEnforcer.setCredentialStore(getFetcher().getCredentialStore());
        boolean result = preconditionEnforcer.credentialPrecondition(curi);
        TestCase.assertTrue(result);
        CrawlURI loginUri = curi.getPrerequisiteUri();
        TestCase.assertEquals("http://localhost:7779/j_security_check", loginUri.toString());
        // there's some special logic with side effects in here for the login uri itself
        result = preconditionEnforcer.credentialPrecondition(loginUri);
        TestCase.assertFalse(result);
        loginUri.setRecorder(getRecorder());
        getFetcher().process(loginUri);
        FormAuthTest.logger.info(((('\n' + (httpRequestString(loginUri))) + "\n\n") + (rawResponseString(loginUri))));
        TestCase.assertEquals(302, loginUri.getFetchStatus());// 302 on successful login

        TestCase.assertEquals("http://localhost:7779/auth/1", loginUri.getHttpResponseHeader("location"));
        curi = makeCrawlURI("http://localhost:7779/auth/1");
        getFetcher().process(curi);
        FormAuthTest.logger.info((('\n' + (httpRequestString(curi))) + (contentString(curi))));
        runDefaultChecks(curi, "hostHeader", "requestLine");
    }

    protected static final String LOGIN_HTML = "<html>" + (((((((("<head><title>Log In</title></head>" + "<body>") + "<form action='/j_security_check' method='post'>") + "<div> username: <input name='j_username' type='text'/> </div>") + "<div> password: <input name='j_password' type='password'/> </div>") + "<div> <input type='submit' /> </div>") + "</form>") + "</body>") + "</html>");

    protected static class FormAuthTestHandler extends SessionHandler {
        public FormAuthTestHandler() {
            super();
        }

        @Override
        public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
            if (target.endsWith("/set-cookie")) {
                response.addCookie(new Cookie("test-cookie-name", "test-cookie-value"));
            }
            if (target.equals("/login.html")) {
                response.setContentType("text/html;charset=US-ASCII");
                response.setStatus(SC_OK);
                response.getOutputStream().write(FormAuthTest.LOGIN_HTML.getBytes("US-ASCII"));
                setHandled(true);
            } else {
                response.setContentType("text/plain;charset=US-ASCII");
                response.setDateHeader("Last-Modified", 0);
                response.setStatus(SC_OK);
                response.getOutputStream().write(FormAuthTest.DEFAULT_PAYLOAD_STRING.getBytes("US-ASCII"));
                setHandled(true);
            }
        }
    }
}

