package org.jboss.as.test.integration.deployment.dependencies;


import java.io.IOException;
import javax.naming.Context;
import javax.naming.NamingException;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ArchiveDeployer;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.dmr.ModelNode;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests inter deployment dependencies
 */
@RunWith(Arquillian.class)
@RunAsClient
public class InterDeploymentDependenciesTestCase {
    private static final String APP_NAME = "";

    private static final String DISTINCT_NAME = "";

    private static JavaArchive DEPENDEE = ShrinkWrap.create(JavaArchive.class, "dependee.jar").addClasses(DependeeEjb.class, StringView.class);

    private static JavaArchive DEPENDENT = ShrinkWrap.create(JavaArchive.class, "dependent.jar").addClasses(DependentEjb.class, StringView.class).addAsManifestResource(InterDeploymentDependenciesTestCase.class.getPackage(), "jboss-all.xml", "jboss-all.xml");

    private static Context context;

    @ArquillianResource
    public ManagementClient managementClient;

    // We don't inject this via @ArquillianResource because ARQ can't fully control
    // DEPENDEE and DEPENDENT and things go haywire if we try. But we use ArchiveDeployer
    // because it's a convenient API for handling deploy/undeploy of Shrinkwrap archives
    private ArchiveDeployer deployer;

    @Test
    public void testDeploymentDependencies() throws NamingException, DeploymentException {
        try {
            deployer.deploy(InterDeploymentDependenciesTestCase.DEPENDENT);
            Assert.fail("Deployment did not fail");
        } catch (Exception e) {
            // expected
        }
        deployer.deploy(InterDeploymentDependenciesTestCase.DEPENDEE);
        deployer.deploy(InterDeploymentDependenciesTestCase.DEPENDENT);
        StringView ejb = InterDeploymentDependenciesTestCase.lookupStringView();
        Assert.assertEquals("hello", ejb.getString());
    }

    @Test
    public void testDeploymentDependenciesWithRestart() throws IOException, NamingException, DeploymentException {
        try {
            deployer.deploy(InterDeploymentDependenciesTestCase.DEPENDENT);
            Assert.fail("Deployment did not fail");
        } catch (Exception e) {
            // expected
        }
        deployer.deploy(InterDeploymentDependenciesTestCase.DEPENDEE);
        deployer.deploy(InterDeploymentDependenciesTestCase.DEPENDENT);
        StringView ejb = InterDeploymentDependenciesTestCase.lookupStringView();
        Assert.assertEquals("hello", ejb.getString());
        ModelNode response = managementClient.getControllerClient().execute(Util.createEmptyOperation("redeploy", PathAddress.pathAddress("deployment", InterDeploymentDependenciesTestCase.DEPENDEE.getName())));
        Assert.assertEquals(response.toString(), "success", response.get("outcome").asString());
        ejb = InterDeploymentDependenciesTestCase.lookupStringView();
        Assert.assertEquals("hello", ejb.getString());
    }
}

