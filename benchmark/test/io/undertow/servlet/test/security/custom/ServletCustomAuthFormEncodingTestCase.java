/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.servlet.test.security.custom;


import ServletContainer.Factory;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.LoginConfig;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.api.ServletSecurityInfo;
import io.undertow.servlet.test.SimpleServletTestCase;
import io.undertow.servlet.test.security.SendUsernameServlet;
import io.undertow.servlet.test.security.constraint.ServletIdentityManager;
import io.undertow.servlet.test.security.form.FormLoginServlet;
import io.undertow.servlet.test.util.TestClassIntrospector;
import io.undertow.testutils.DefaultServer;
import javax.servlet.ServletException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test case that validates the use of the DeploymentManagerImpl authMechanism override
 *
 * @author Stuart Douglas
 * @author Anil Saldhana
 */
@RunWith(DefaultServer.class)
public class ServletCustomAuthFormEncodingTestCase {
    @Test
    public void testAuthFormEncoding() throws ServletException {
        final ServletContainer container = Factory.newInstance();
        ServletInfo s = new ServletInfo("servlet", SendUsernameServlet.class).setServletSecurityInfo(new ServletSecurityInfo().addRoleAllowed("role1")).addMapping("/secured/*");
        ServletInfo s1 = new ServletInfo("loginPage", FormLoginServlet.class).setServletSecurityInfo(new ServletSecurityInfo().addRoleAllowed("group1")).addMapping("/FormLoginServlet");
        ServletIdentityManager identityManager = new ServletIdentityManager();
        identityManager.addUser("user1", "password1", "role1");
        DeploymentInfo builder = new DeploymentInfo().setClassLoader(SimpleServletTestCase.class.getClassLoader()).setContextPath("/servletContext").setClassIntrospecter(TestClassIntrospector.INSTANCE).setDeploymentName("servletContext.war").setIdentityManager(identityManager).setLoginConfig(new LoginConfig("FORM", "Test Realm", "/FormLoginServlet", "/error.html")).addServlets(s, s1).addAuthenticationMechanism("FORM", CustomEncodingAuthenticationMechanism.FACTORY);
        DeploymentManager manager = container.addDeployment(builder);
        manager.deploy();
        CustomEncodingAuthenticationMechanism authenticationMechanism = getCustomeAuth(manager);
        Assert.assertEquals("ISO-8859-1", authenticationMechanism.charset);
        manager.undeploy();
        builder = new DeploymentInfo().setClassLoader(SimpleServletTestCase.class.getClassLoader()).setContextPath("/servletContext").setClassIntrospecter(TestClassIntrospector.INSTANCE).setDeploymentName("servletContext2.war").setIdentityManager(identityManager).setLoginConfig(new LoginConfig("FORM", "Test Realm", "/FormLoginServlet", "/error.html")).addServlets(s, s1).setDefaultRequestEncoding("UTF-8").addAuthenticationMechanism("FORM", CustomEncodingAuthenticationMechanism.FACTORY);
        manager = container.addDeployment(builder);
        manager.deploy();
        authenticationMechanism = getCustomeAuth(manager);
        Assert.assertEquals("UTF-8", authenticationMechanism.charset);
        manager.undeploy();
        builder = new DeploymentInfo().setClassLoader(SimpleServletTestCase.class.getClassLoader()).setContextPath("/servletContext").setClassIntrospecter(TestClassIntrospector.INSTANCE).setDeploymentName("servletContext3.war").setIdentityManager(identityManager).setLoginConfig(new LoginConfig("FORM", "Test Realm", "/FormLoginServlet", "/error.html")).addServlets(s, s1).setDefaultEncoding("UTF-8").addAuthenticationMechanism("FORM", CustomEncodingAuthenticationMechanism.FACTORY);
        manager = container.addDeployment(builder);
        manager.deploy();
        authenticationMechanism = getCustomeAuth(manager);
        Assert.assertEquals("UTF-8", authenticationMechanism.charset);
        manager.undeploy();
    }
}

