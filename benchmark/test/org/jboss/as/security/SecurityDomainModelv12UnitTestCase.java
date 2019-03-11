/**
 * JBoss, Home of Professional Open Source.
 *  Copyright 2013, Red Hat, Inc., and individual contributors
 *  as indicated by the @author tags. See the copyright.txt file in the
 *  distribution for a full listing of individual contributors.
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * /
 */
package org.jboss.as.security;


import SecurityExtension.SUBSYSTEM_NAME;
import java.io.File;
import java.util.List;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.jboss.as.subsystem.test.KernelServices;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;


/**
 * <p>
 * Security subsystem tests for the version 1.2 of the subsystem schema.
 * </p>
 */
public class SecurityDomainModelv12UnitTestCase extends AbstractSubsystemBaseTest {
    private static String oldConfig;

    private static final char[] KEYSTORE_PASSWORD = "changeit".toCharArray();

    private static final char[] TRUSTSTORE_PASSWORD = "rmi+ssl".toCharArray();

    private static final String WORKING_DIRECTORY_LOCATION = "./target/test-classes";

    private static final String KEYSTORE_FILENAME = "clientcert.jks";

    private static final String TRUSTSTORE_FILENAME = "keystore.jks";

    private static final File KEY_STORE_FILE = new File(SecurityDomainModelv12UnitTestCase.WORKING_DIRECTORY_LOCATION, SecurityDomainModelv12UnitTestCase.KEYSTORE_FILENAME);

    private static final File TRUST_STORE_FILE = new File(SecurityDomainModelv12UnitTestCase.WORKING_DIRECTORY_LOCATION, SecurityDomainModelv12UnitTestCase.TRUSTSTORE_FILENAME);

    public SecurityDomainModelv12UnitTestCase() {
        super(SUBSYSTEM_NAME, new SecurityExtension());
    }

    @Test
    public void testOrder() throws Exception {
        KernelServices service = createKernelServicesBuilder(createAdditionalInitialization()).setSubsystemXmlResource("securitysubsystemv12.xml").build();
        PathAddress address = PathAddress.pathAddress().append("subsystem", "security").append("security-domain", "ordering");
        address = address.append("authentication", "classic");
        ModelNode writeOp = Util.createOperation("write-attribute", address);
        writeOp.get("name").set("login-modules");
        for (int i = 1; i <= 6; i++) {
            ModelNode module = writeOp.get("value").add();
            module.get("code").set(("module-" + i));
            module.get("flag").set("optional");
            module.get("module-options");
        }
        service.executeOperation(writeOp);
        ModelNode readOp = Util.createOperation("read-attribute", address);
        readOp.get("name").set("login-modules");
        ModelNode result = service.executeForResult(readOp);
        List<ModelNode> modules = result.asList();
        Assert.assertEquals("There should be exactly 6 modules but there are not", 6, modules.size());
        for (int i = 1; i <= 6; i++) {
            ModelNode module = modules.get((i - 1));
            Assert.assertEquals(module.get("code").asString(), ("module-" + i));
        }
    }
}

