/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.naming.shared;


import java.util.concurrent.TimeUnit;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * A test case which verifies proper release of shared binds, i.e., automated unbind only after every deployment that shares the bind is undeployed.
 *
 * @author Eduardo Martins
 */
@RunWith(Arquillian.class)
public class SharedBindingTestCase {
    private static final String BEAN_ONE_JAR_NAME = "BEAN_ONE";

    private static final String BEAN_TWO_JAR_NAME = "BEAN_TWO";

    private static final String TEST_RESULTS_BEAN_JAR_NAME = "TEST_RESULTS_BEAN_JAR_NAME";

    @ArquillianResource
    public Deployer deployer;

    @ArquillianResource
    private InitialContext initialContext;

    @Test
    public void test() throws InterruptedException, NamingException {
        // deploy bean one and two
        deployer.deploy(SharedBindingTestCase.BEAN_ONE_JAR_NAME);
        boolean undeployedBeanOne = false;
        try {
            try {
                deployer.deploy(SharedBindingTestCase.BEAN_TWO_JAR_NAME);
                // undeploy bean one first
                deployer.undeploy(SharedBindingTestCase.BEAN_ONE_JAR_NAME);
                undeployedBeanOne = true;
            } finally {
                deployer.undeploy(SharedBindingTestCase.BEAN_TWO_JAR_NAME);
            }
        } finally {
            if (!undeployedBeanOne) {
                deployer.undeploy(SharedBindingTestCase.BEAN_ONE_JAR_NAME);
            }
        }
        // lookup bean three and assert test results
        final TestResults testResults = ((TestResults) (initialContext.lookup(((((("java:global/" + (SharedBindingTestCase.TEST_RESULTS_BEAN_JAR_NAME)) + "/") + (TestResultsBean.class.getSimpleName())) + "!") + (TestResults.class.getName())))));
        testResults.await(5, TimeUnit.SECONDS);
        Assert.assertTrue(testResults.isPostContructOne());
        Assert.assertTrue(testResults.isPostContructTwo());
        Assert.assertTrue(testResults.isPreDestroyOne());
        Assert.assertTrue(testResults.isPreDestroyTwo());
    }
}

