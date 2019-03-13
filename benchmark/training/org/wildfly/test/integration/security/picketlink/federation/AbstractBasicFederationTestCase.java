/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.wildfly.test.integration.security.picketlink.federation;


import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import java.net.URL;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Igor
 */
public abstract class AbstractBasicFederationTestCase {
    @ArquillianResource
    @OperateOnDeployment("identity-provider")
    private URL idpUrl;

    @ArquillianResource
    @OperateOnDeployment("service-provider-1")
    private URL serviceProvider1;

    @ArquillianResource
    @OperateOnDeployment("service-provider-2")
    private URL serviceProvider2;

    public static final String GLOBAL_LOGOUT_URL_PARAM = "?GLO=true";

    public static final String LOCAL_LOGOUT_URL_PARAM = "?LLO=true";

    private static Logger LOGGER = Logger.getLogger(AbstractBasicFederationTestCase.class);

    @Test
    public void testFederationWithGlobalLogout() throws Exception {
        WebConversation conversation = new WebConversation();
        AbstractBasicFederationTestCase.LOGGER.trace((("REQEST: " + (AbstractBasicFederationTestCase.formatUrl(this.serviceProvider1))) + "/index.jsp"));
        WebRequest request = new GetMethodWebRequest(((AbstractBasicFederationTestCase.formatUrl(this.serviceProvider1)) + "/index.jsp"));
        WebResponse response = conversation.getResponse(request);
        AbstractBasicFederationTestCase.LOGGER.trace(("RESPONSE: " + (response.getText())));
        Assert.assertTrue(response.getURL().getPath().startsWith("/idp"));
        Assert.assertEquals(1, response.getForms().length);
        WebForm webForm = response.getForms()[0];
        webForm.setParameter("j_username", "tomcat");
        webForm.setParameter("j_password", "tomcat");
        webForm.getSubmitButtons()[0].click();
        response = conversation.getCurrentPage();
        Assert.assertTrue(("cannot reach protected content at " + (AbstractBasicFederationTestCase.formatUrl(this.serviceProvider1))), response.getText().contains(("Welcome to " + (formatContextPath(this.serviceProvider1)))));
        AbstractBasicFederationTestCase.LOGGER.trace(("REQEST: " + (AbstractBasicFederationTestCase.formatUrl(this.serviceProvider2))));
        request = new GetMethodWebRequest(AbstractBasicFederationTestCase.formatUrl(this.serviceProvider2));
        response = conversation.getResponse(request);
        AbstractBasicFederationTestCase.LOGGER.trace(("RESPONSE: " + (response.getText())));
        Assert.assertTrue(("cannot reach protected content at " + (AbstractBasicFederationTestCase.formatUrl(this.serviceProvider2))), response.getText().contains(("Welcome to " + (formatContextPath(this.serviceProvider2)))));
        if (performGlobalLogout()) {
            // global logout from serviceProvider2
            AbstractBasicFederationTestCase.LOGGER.trace((("REQEST: " + (AbstractBasicFederationTestCase.formatUrl(this.serviceProvider2))) + (AbstractBasicFederationTestCase.GLOBAL_LOGOUT_URL_PARAM)));
            response = conversation.getResponse(((AbstractBasicFederationTestCase.formatUrl(this.serviceProvider2)) + (AbstractBasicFederationTestCase.GLOBAL_LOGOUT_URL_PARAM)));
            AbstractBasicFederationTestCase.LOGGER.trace(((("GLO response(" + (this.serviceProvider2)) + "):") + (response.getText())));
            Assert.assertTrue("cannot reach logged out page", response.getText().contains("Logout"));
            // check if GLO was successful, so serviceProvider1 is requesting IDP login form
            AbstractBasicFederationTestCase.LOGGER.trace(("REQEST: " + (AbstractBasicFederationTestCase.formatUrl(this.serviceProvider1))));
            request = new GetMethodWebRequest(AbstractBasicFederationTestCase.formatUrl(this.serviceProvider1));
            response = conversation.getResponse(request);
            AbstractBasicFederationTestCase.LOGGER.trace(("RESPONSE: " + (response.getText())));
            Assert.assertTrue("cannot reach IDP", response.getURL().getPath().startsWith("/idp"));
            Assert.assertEquals("no form present on supposed IDP login page", 1, response.getForms().length);
        }
    }

    @Test
    public void testFederationWithLocalLogout() throws Exception {
        WebConversation conversation = new WebConversation();
        AbstractBasicFederationTestCase.LOGGER.trace(("REQEST: " + (AbstractBasicFederationTestCase.formatUrl(this.serviceProvider1))));
        WebRequest request = new GetMethodWebRequest(AbstractBasicFederationTestCase.formatUrl(this.serviceProvider1));
        WebResponse response = conversation.getResponse(request);
        AbstractBasicFederationTestCase.LOGGER.trace(("RESPONSE: " + (response.getText())));
        Assert.assertTrue(response.getURL().getPath().startsWith("/idp"));
        Assert.assertEquals(1, response.getForms().length);
        WebForm webForm = response.getForms()[0];
        webForm.setParameter("j_username", "tomcat");
        webForm.setParameter("j_password", "tomcat");
        webForm.getSubmitButtons()[0].click();
        response = conversation.getCurrentPage();
        Assert.assertTrue(("cannot reach protected content at " + (AbstractBasicFederationTestCase.formatUrl(this.serviceProvider1))), response.getText().contains(("Welcome to " + (formatContextPath(this.serviceProvider1)))));
        AbstractBasicFederationTestCase.LOGGER.trace(("REQEST: " + (AbstractBasicFederationTestCase.formatUrl(this.serviceProvider2))));
        request = new GetMethodWebRequest(AbstractBasicFederationTestCase.formatUrl(this.serviceProvider2));
        response = conversation.getResponse(request);
        AbstractBasicFederationTestCase.LOGGER.trace(("RESPONSE: " + (response.getText())));
        Assert.assertTrue(("cannot reach protected content at " + (AbstractBasicFederationTestCase.formatUrl(this.serviceProvider2))), response.getText().contains(("Welcome to " + (formatContextPath(this.serviceProvider2)))));
        // local logout from serviceProvider2
        AbstractBasicFederationTestCase.LOGGER.trace((("REQEST: " + (AbstractBasicFederationTestCase.formatUrl(this.serviceProvider2))) + (AbstractBasicFederationTestCase.LOCAL_LOGOUT_URL_PARAM)));
        response = conversation.getResponse(((AbstractBasicFederationTestCase.formatUrl(this.serviceProvider2)) + (AbstractBasicFederationTestCase.LOCAL_LOGOUT_URL_PARAM)));
        AbstractBasicFederationTestCase.LOGGER.trace(((("LLO response(" + (this.serviceProvider2)) + "):") + (response.getText())));
        Assert.assertTrue("cannot reach locally logged out page", response.getText().contains("Logout"));
        // check if it was really LLO
        AbstractBasicFederationTestCase.LOGGER.trace(("REQEST: " + (AbstractBasicFederationTestCase.formatUrl(this.serviceProvider1))));
        request = new GetMethodWebRequest(AbstractBasicFederationTestCase.formatUrl(this.serviceProvider1));
        response = conversation.getResponse(request);
        AbstractBasicFederationTestCase.LOGGER.trace(("RESPONSE: " + (response.getText())));
        Assert.assertTrue(("cannot reach protected content at " + (AbstractBasicFederationTestCase.formatUrl(this.serviceProvider1))), response.getText().contains(("Welcome to " + (formatContextPath(this.serviceProvider1)))));
        // LLO from serviceProvider1
        AbstractBasicFederationTestCase.LOGGER.trace((("REQEST: " + (AbstractBasicFederationTestCase.formatUrl(this.serviceProvider1))) + (AbstractBasicFederationTestCase.LOCAL_LOGOUT_URL_PARAM)));
        response = conversation.getResponse(((AbstractBasicFederationTestCase.formatUrl(this.serviceProvider1)) + (AbstractBasicFederationTestCase.LOCAL_LOGOUT_URL_PARAM)));
        AbstractBasicFederationTestCase.LOGGER.trace(((("LLO response(" + (this.serviceProvider1)) + "):") + (response.getText())));
        Assert.assertTrue("cannot reach locally logged out page", response.getText().contains("Logout"));
    }
}

