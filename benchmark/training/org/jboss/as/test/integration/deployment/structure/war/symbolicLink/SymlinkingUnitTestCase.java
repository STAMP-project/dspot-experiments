package org.jboss.as.test.integration.deployment.structure.war.symbolicLink;


import java.io.File;
import java.io.IOException;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Unit test to make sure that the symbolic linking feature on the web subsystem is enabled properly.
 * Corresponding JIRA: AS7-3414.
 *
 * @author navssurtani
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SymlinkingUnitTestCase {
    private static final Logger logger = Logger.getLogger(SymlinkingUnitTestCase.class);

    private static final String WAR_NAME = "explodedDeployment.war";

    private static File warDeployment = null;

    private static File symbolic = null;

    private static ModelControllerClient controllerClient = TestSuiteEnvironment.getModelControllerClient();

    @Test
    public void testEnabled() throws IOException {
        // logger.infof("Testing enabled bit");
        // By default we should not be able to browse to the symlinked page.
        Assert.assertTrue(((getURLcode("symbolic")) == 404));
        setup(true);
        // First make sure that we can browse to index.html. This should work in the enabled or disabled version.
        Assert.assertTrue(((getURLcode("index")) == 200));
        // Now with symbolic.html.
        Assert.assertTrue(((getURLcode("symbolic")) == 200));
    }

    @Test
    public void testDisabled() throws IOException {
        SymlinkingUnitTestCase.logger.infof("Testing disabled bit.");
        // By default we should not be able to browse to the symlinked page.
        Assert.assertTrue(((getURLcode("symbolic")) == 404));
        setup(false);
        // First make sure that we can browse to index.html. This should work in the enabled or disabled version.
        Assert.assertTrue(((getURLcode("index")) == 200));
        // Now with symbolic.html.
        Assert.assertTrue(((getURLcode("symbolic")) == 404));
    }
}

