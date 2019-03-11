/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.as.test.integration.domain.suites;


import java.io.InputStream;
import java.util.Map;
import org.jboss.as.controller.client.helpers.domain.DeploymentPlan;
import org.jboss.as.controller.client.helpers.domain.DeploymentPlanResult;
import org.jboss.as.controller.client.helpers.domain.DomainClient;
import org.jboss.as.controller.client.helpers.domain.DomainDeploymentManager;
import org.jboss.as.test.integration.domain.management.util.DomainLifecycleUtil;
import org.jboss.as.test.integration.domain.management.util.DomainTestSupport;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.xnio.IoUtils;


/**
 *
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
public class ReadEnvironmentVariablesTestCase {
    private static DomainTestSupport testSupport;

    private static DomainLifecycleUtil domainMasterLifecycleUtil;

    private static DomainLifecycleUtil domainSlaveLifecycleUtil;

    @Test
    public void testReadEnvironmentVariablesForServers() throws Exception {
        DomainClient client = ReadEnvironmentVariablesTestCase.domainMasterLifecycleUtil.createDomainClient();
        DomainDeploymentManager manager = client.getDeploymentManager();
        try {
            // Deploy the archive
            WebArchive archive = ShrinkWrap.create(WebArchive.class, "env-test.war").addClass(EnvironmentTestServlet.class);
            archive.addAsResource(new StringAsset("Manifest-Version: 1.0\nDependencies: org.jboss.dmr \n"), "META-INF/MANIFEST.MF");
            archive.addAsManifestResource(createPermissionsXmlAsset(new RuntimePermission("getenv.*")), "permissions.xml");
            final InputStream contents = archive.as(ZipExporter.class).exportAsInputStream();
            try {
                DeploymentPlan plan = manager.newDeploymentPlan().add("env-test.war", contents).deploy("env-test.war").toServerGroup("main-server-group").toServerGroup("other-server-group").build();
                DeploymentPlanResult result = manager.execute(plan).get();
                Assert.assertTrue(result.isValid());
            } finally {
                IoUtils.safeClose(contents);
            }
            Map<String, String> env = getEnvironmentVariables(client, "master", "main-one", "standard-sockets");
            checkEnvironmentVariable(env, "DOMAIN_TEST_MAIN_GROUP", "main_group");
            checkEnvironmentVariable(env, "DOMAIN_TEST_SERVER", "server");
            checkEnvironmentVariable(env, "DOMAIN_TEST_JVM", "jvm");
            env = getEnvironmentVariables(client, "slave", "main-three", "standard-sockets");
            checkEnvironmentVariable(env, "DOMAIN_TEST_MAIN_GROUP", "main_group");
            Assert.assertFalse(env.containsKey("DOMAIN_TEST_SERVER"));
            Assert.assertFalse(env.containsKey("DOMAIN_TEST_JVM"));
            env = getEnvironmentVariables(client, "slave", "other-two", "other-sockets");
            Assert.assertFalse(env.containsKey("DOMAIN_TEST_MAIN_GROUP"));
            Assert.assertFalse(env.containsKey("DOMAIN_TEST_SERVER"));
            Assert.assertFalse(env.containsKey("DOMAIN_TEST_JVM"));
        } finally {
            DeploymentPlanResult result = manager.execute(manager.newDeploymentPlan().undeploy("env-test.war").build()).get();
            Assert.assertTrue(result.isValid());
            IoUtils.safeClose(client);
        }
    }
}

