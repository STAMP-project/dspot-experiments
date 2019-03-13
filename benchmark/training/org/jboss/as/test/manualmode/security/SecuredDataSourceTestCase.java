/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.manualmode.security;


import HttpServletResponse.SC_OK;
import java.io.File;
import java.net.URI;
import org.apache.commons.io.FileUtils;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.integration.management.base.AbstractCliTestBase;
import org.jboss.as.test.integration.security.common.Utils;
import org.jboss.as.test.integration.security.common.servlets.DataSourceTestServlet;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests a DataSource which uses Credentials stored in a security domain.
 *
 * @author Josef Cacek
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SecuredDataSourceTestCase extends AbstractCliTestBase {
    private static final String CONTAINER = "default-jbossas";

    private static final String DEPLOYMENT = "deployment";

    private static final String TEST_NAME = SecuredDataSourceTestCase.class.getSimpleName();

    private static final String BATCH_CLI_FILENAME = (SecuredDataSourceTestCase.TEST_NAME) + ".cli";

    private static final String REMOVE_BATCH_CLI_FILENAME1 = (SecuredDataSourceTestCase.TEST_NAME) + "-remove.cli";

    private static final String REMOVE_BATCH_CLI_FILENAME2 = (SecuredDataSourceTestCase.TEST_NAME) + "-remove2.cli";

    private static final File WORK_DIR = new File(("secured-ds-" + (System.currentTimeMillis())));

    private static final File BATCH_CLI_FILE = new File(SecuredDataSourceTestCase.WORK_DIR, SecuredDataSourceTestCase.BATCH_CLI_FILENAME);

    private static final File REMOVE_BATCH_CLI_FILE1 = new File(SecuredDataSourceTestCase.WORK_DIR, SecuredDataSourceTestCase.REMOVE_BATCH_CLI_FILENAME1);

    private static final File REMOVE_BATCH_CLI_FILE2 = new File(SecuredDataSourceTestCase.WORK_DIR, SecuredDataSourceTestCase.REMOVE_BATCH_CLI_FILENAME2);

    @ArquillianResource
    private static ContainerController container;

    @ArquillianResource
    private Deployer deployer;

    @Test
    public void test() throws Exception {
        final URI uri = new URI((((((((("http://" + (TestSuiteEnvironment.getServerAddress())) + ":8080/") + (SecuredDataSourceTestCase.TEST_NAME)) + (DataSourceTestServlet.SERVLET_PATH)) + "?") + (DataSourceTestServlet.PARAM_DS)) + "=") + (SecuredDataSourceTestCase.TEST_NAME)));
        final String body = Utils.makeCall(uri, SC_OK);
        Assert.assertEquals("true", body);
    }

    /**
     * Configure the AS and LDAP as the first step in this testcase.
     *
     * @throws Exception
     * 		
     */
    @Test
    @InSequence(Integer.MIN_VALUE)
    public void initServer() throws Exception {
        SecuredDataSourceTestCase.container.start(SecuredDataSourceTestCase.CONTAINER);
        SecuredDataSourceTestCase.WORK_DIR.mkdirs();
        FileUtils.copyInputStreamToFile(getClass().getResourceAsStream(SecuredDataSourceTestCase.BATCH_CLI_FILENAME), SecuredDataSourceTestCase.BATCH_CLI_FILE);
        FileUtils.copyInputStreamToFile(getClass().getResourceAsStream(SecuredDataSourceTestCase.REMOVE_BATCH_CLI_FILENAME1), SecuredDataSourceTestCase.REMOVE_BATCH_CLI_FILE1);
        FileUtils.copyInputStreamToFile(getClass().getResourceAsStream(SecuredDataSourceTestCase.REMOVE_BATCH_CLI_FILENAME2), SecuredDataSourceTestCase.REMOVE_BATCH_CLI_FILE2);
        initCLI();
        final boolean batchResult = SecuredDataSourceTestCase.runBatch(SecuredDataSourceTestCase.BATCH_CLI_FILE);
        closeCLI();
        try {
            Assert.assertTrue("Server configuration failed", batchResult);
        } finally {
            SecuredDataSourceTestCase.container.stop(SecuredDataSourceTestCase.CONTAINER);
        }
        SecuredDataSourceTestCase.container.start(SecuredDataSourceTestCase.CONTAINER);
        deployer.deploy(SecuredDataSourceTestCase.DEPLOYMENT);
    }

    /**
     * Revert the AS configuration and stop the server as the last but one step.
     *
     * @throws Exception
     * 		
     */
    @Test
    @InSequence(Integer.MAX_VALUE)
    public void closeServer() throws Exception {
        Assert.assertTrue(SecuredDataSourceTestCase.container.isStarted(SecuredDataSourceTestCase.CONTAINER));
        deployer.undeploy(SecuredDataSourceTestCase.DEPLOYMENT);
        initCLI();
        boolean batchResult = SecuredDataSourceTestCase.runBatch(SecuredDataSourceTestCase.REMOVE_BATCH_CLI_FILE1);
        // server reload
        SecuredDataSourceTestCase.container.stop(SecuredDataSourceTestCase.CONTAINER);
        SecuredDataSourceTestCase.container.start(SecuredDataSourceTestCase.CONTAINER);
        batchResult = batchResult && (SecuredDataSourceTestCase.runBatch(SecuredDataSourceTestCase.REMOVE_BATCH_CLI_FILE2));
        closeCLI();
        SecuredDataSourceTestCase.container.stop(SecuredDataSourceTestCase.CONTAINER);
        FileUtils.deleteQuietly(SecuredDataSourceTestCase.WORK_DIR);
        Assert.assertTrue("Reverting server configuration failed", batchResult);
    }
}

