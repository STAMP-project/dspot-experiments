/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.jca.archive;


import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.jca.JcaMgmtBase;
import org.jboss.as.test.integration.jca.JcaMgmtServerSetupTask;
import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="vrastsel@redhat.com">Vladimir Rastseluev</a> JBQA-6006 archive validation checking
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup(ArchiveValidationDeploymentTestCase.ArchiveValidationDeploymentTestCaseSetup.class)
public class ArchiveValidationDeploymentTestCase extends JcaMgmtBase {
    private static Logger log = Logger.getLogger("ArchiveValidationDeploymentTestCase");

    static class ArchiveValidationDeploymentTestCaseSetup extends JcaMgmtServerSetupTask {
        boolean enabled;

        boolean failingOnError;

        boolean failingOnWarning;

        @Override
        public void doSetup(ManagementClient managementClient) throws Exception {
            enabled = getArchiveValidationAttribute("enabled");
            failingOnError = getArchiveValidationAttribute("fail-on-error");
            failingOnWarning = getArchiveValidationAttribute("fail-on-warn");
            ArchiveValidationDeploymentTestCase.log.trace(((((("//save//" + (enabled)) + "//") + (failingOnError)) + "//") + (failingOnWarning)));
        }
    }

    @ArquillianResource
    Deployer deployer;

    @Test
    public void testValidationDisabled() throws Throwable {
        setArchiveValidation(false, false, false);
        goodTest("ok");
        goodTest("error");
        goodTest("warning");
    }

    @Test
    public void testValidationDisabled1() throws Throwable {
        setArchiveValidation(false, true, false);
        goodTest("ok");
        goodTest("error");
        goodTest("warning");
    }

    @Test
    public void testValidationDisabled2() throws Throwable {
        setArchiveValidation(false, false, true);
        goodTest("ok");
        goodTest("error");
        goodTest("warning");
    }

    @Test
    public void testValidationDisabled3() throws Throwable {
        setArchiveValidation(false, true, true);
        goodTest("ok");
        goodTest("error");
        goodTest("warning");
    }

    @Test
    public void testValidationEnabled() throws Throwable {
        setArchiveValidation(true, false, false);
        goodTest("ok");
        goodTest("error");
        goodTest("warning");
    }

    @Test
    public void testValidationOfErrorsEnabled() throws Throwable {
        setArchiveValidation(true, true, false);
        goodTest("ok");
        badTest("error", "fail on errors is enabled");
        goodTest("warning");
    }

    @Test
    public void testValidationOfErrorsAndWarningsEnabled() throws Throwable {
        setArchiveValidation(true, true, true);
        goodTest("ok");
        badTest("error", "fail on errors and warnings is enabled");
        badTest("warning", "fail on errors and warnings is enabled");
    }

    @Test
    public void testValidationOfWarningsEnabled() throws Throwable {
        setArchiveValidation(true, false, true);
        goodTest("ok");
        badTest("error", "fail on warnings is enabled");
        badTest("warning", "fail on warnings is enabled");
    }
}

