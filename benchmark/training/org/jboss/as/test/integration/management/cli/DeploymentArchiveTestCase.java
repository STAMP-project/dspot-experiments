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
package org.jboss.as.test.integration.management.cli;


import java.io.File;
import java.net.URL;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.integration.management.base.AbstractCliTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * This tests a CLI deployment archive functionality.
 * See https://community.jboss.org/wiki/CLIDeploymentArchive
 *
 * @author Ivo Studensky <istudens@redhat.com>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class DeploymentArchiveTestCase extends AbstractCliTestBase {
    private static final String MODULE_NAME = "org.jboss.test.deploymentarchive";

    private static final String WEB_ARCHIVE_NAME = "deploymentarchive";

    private static final String MODULE_ARCHIVE = "deploymentarchivemodule.jar";

    private static final String MODULE_XML_FILE = "module.xml";

    private static final String DEPLOY_SCR = ((((((("deploy " + (DeploymentArchiveTestCase.WEB_ARCHIVE_NAME)) + ".war\n") + "module add --name=") + (DeploymentArchiveTestCase.MODULE_NAME)) + " --resources=") + (DeploymentArchiveTestCase.MODULE_ARCHIVE)) + " --module-xml=") + (DeploymentArchiveTestCase.MODULE_XML_FILE);

    private static final String UNDEPLOY_SCR = ((("undeploy " + (DeploymentArchiveTestCase.WEB_ARCHIVE_NAME)) + ".war\n") + "module remove --name=") + (DeploymentArchiveTestCase.MODULE_NAME);

    private static final String MODULE_XML = (((((((("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<module xmlns=\"urn:jboss:module:1.5\" name=\"") + (DeploymentArchiveTestCase.MODULE_NAME)) + "\" slot=\"main\">") + "    <resources>") + "        <resource-root path=\"") + (DeploymentArchiveTestCase.MODULE_ARCHIVE)) + "\"/>") + "    </resources>") + "</module>";

    private static File cliFile;

    @ArquillianResource
    URL url;

    @Test
    public void testDeployUndeploy() throws Exception {
        testDeploy();
        testUndeploy();
    }
}

