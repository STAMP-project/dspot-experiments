/**
 * JBoss, Home of Professional Open Source.
 *  Copyright 2015, Red Hat, Inc., and individual contributors
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
package org.jboss.as.ee.subsystem;


import EeExtension.SUBSYSTEM_NAME;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.jboss.as.subsystem.test.KernelServices;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.wildfly.security.manager.WildFlySecurityManager;


/**
 *
 *
 * @author <a href="opalka.richard@gmail.com">Richard Opalka</a>
 */
public class EeLegacySubsystemTestCase extends AbstractSubsystemBaseTest {
    public EeLegacySubsystemTestCase() {
        super(SUBSYSTEM_NAME, new EeExtension());
    }

    @Test
    public void testLegacyConfigurations() throws Exception {
        // Get a list of all the logging_x_x.xml files
        final Pattern pattern = Pattern.compile("(subsystem)_\\d+_\\d+\\.xml");
        // Using the CP as that's the standardSubsystemTest will use to find the config file
        final String cp = WildFlySecurityManager.getPropertyPrivileged("java.class.path", ".");
        final String[] entries = cp.split(Pattern.quote(File.pathSeparator));
        final List<String> configs = new ArrayList<>();
        for (String entry : entries) {
            final Path path = Paths.get(entry);
            if (Files.isDirectory(path)) {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                        final String name = file.getFileName().toString();
                        if (pattern.matcher(name).matches()) {
                            configs.add(name);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        }
        // The paths shouldn't be empty
        Assert.assertFalse("No configs were found", configs.isEmpty());
        for (String configId : configs) {
            // Run the standard subsystem test, but don't compare the XML as it should never match
            standardSubsystemTest(configId, false);
        }
    }

    @Test
    public void testSubsystem() throws Exception {
        KernelServices services = standardSubsystemTest("subsystem_1_2.xml", false);
        ModelNode model = services.readWholeModel();
        ModelNode subsystem = model.require(SUBSYSTEM).require(SUBSYSTEM_NAME);
        ModelNode globalModules = subsystem.require(GlobalModulesDefinition.GLOBAL_MODULES);
        Assert.assertEquals("org.jboss.logging", globalModules.require(0).require(GlobalModulesDefinition.NAME).asString());
        Assert.assertEquals("main", globalModules.require(0).require(GlobalModulesDefinition.SLOT).asString());
        Assert.assertEquals("org.apache.log4j", globalModules.require(1).require(GlobalModulesDefinition.NAME).asString());
        Assert.assertTrue(globalModules.require(1).require(GlobalModulesDefinition.ANNOTATIONS).asBoolean());
        Assert.assertTrue(globalModules.require(1).require(GlobalModulesDefinition.META_INF).asBoolean());
        Assert.assertFalse(globalModules.require(1).require(GlobalModulesDefinition.SERVICES).asBoolean());
        Assert.assertFalse(subsystem.require(EESubsystemModel.ANNOTATION_PROPERTY_REPLACEMENT).asBoolean());
        Assert.assertTrue(subsystem.require(EESubsystemModel.EAR_SUBDEPLOYMENTS_ISOLATED).asBoolean());
        Assert.assertTrue(subsystem.require(EESubsystemModel.JBOSS_DESCRIPTOR_PROPERTY_REPLACEMENT).asBoolean());
        Assert.assertFalse(subsystem.require(EESubsystemModel.SPEC_DESCRIPTOR_PROPERTY_REPLACEMENT).asBoolean());
    }

    boolean extensionAdded = false;
}

