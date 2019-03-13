/**
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.test.integration.ejb.injection.ejbref.lookup;


import java.net.URL;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Tomas Remes
 */
@RunWith(Arquillian.class)
@RunAsClient
public class EjbRefLookupTestCase {
    private static final String EAR_DEPLOYMENT = "ejb-test-ear";

    private static final String WAR_DEPLOYMENT = "webapp-test";

    @ArquillianResource
    @OperateOnDeployment(EjbRefLookupTestCase.WAR_DEPLOYMENT)
    private URL warUrl;

    @Test
    public void testEjbRefInServlet() throws Exception {
        Assert.assertEquals("1", doGetReq("test1"));
    }

    @Test
    public void testJBossEjbRefInServlet() throws Exception {
        Assert.assertEquals("1", doGetReq("test2"));
    }

    @Test
    public void testEjbRefInRest() throws Exception {
        Assert.assertEquals("1", doGetReq("rest/first/text"));
    }

    @Test
    public void testJBossEjbRefInRest() throws Exception {
        Assert.assertEquals("1", doGetReq("rest/second/text"));
    }
}

