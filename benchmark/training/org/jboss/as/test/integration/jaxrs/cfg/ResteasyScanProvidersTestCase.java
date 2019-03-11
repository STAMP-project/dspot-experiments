/**
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.as.test.integration.jaxrs.cfg;


import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests for RESTEasy configuration parameter 'resteasy.scan.providers'
 *
 * @author Pavel Janousek
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResteasyScanProvidersTestCase {
    private static final String depNameTrue = "dep_true";

    private static final String depNameFalse = "dep_false";

    private static final String depNameInvalid = "dep_invalid";

    private static final String depNameTrueApp = "dep_true_app";

    private static final String depNameFalseApp = "dep_false_app";

    private static final String depNameInvalidApp = "dep_invalid_app";

    @ArquillianResource
    private Deployer deployer;

    // @Ignore("AS7-4254")
    @Test
    public void testDeployInvalid() throws Exception {
        try {
            deployer.deploy(ResteasyScanProvidersTestCase.depNameInvalid);
            Assert.fail("Test should not go here - invalid deployment (invalid value of resteasy.scan.providers)!");
        } catch (Exception e) {
        }
    }

    @Test
    public void testDeployInvalidApp() throws Exception {
        try {
            deployer.deploy(ResteasyScanProvidersTestCase.depNameInvalidApp);
            Assert.fail("Test should not go here - invalid deployment (invalid value of resteasy.scan.providers)!");
        } catch (Exception e) {
        }
    }
}

