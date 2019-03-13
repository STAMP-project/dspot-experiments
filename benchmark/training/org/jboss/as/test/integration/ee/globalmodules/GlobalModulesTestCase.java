/**
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.as.test.integration.ee.globalmodules;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.dmr.ModelNode;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexWriter;
import org.jboss.jandex.Indexer;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(Arquillian.class)
@ServerSetup(GlobalModulesTestCase.GlobalModulesTestCaseServerSetup.class)
public class GlobalModulesTestCase {
    static class GlobalModulesTestCaseServerSetup implements ServerSetupTask {
        @Override
        public void setup(final ManagementClient managementClient, final String containerId) throws Exception {
            final ModelNode value = new ModelNode();
            ModelNode module = new ModelNode();
            module.get("name").set("org.jboss.test.globalModule");
            module.get("annotations").set(true);
            value.add(module);
            final ModelNode op = new ModelNode();
            op.get(OP_ADDR).set(SUBSYSTEM, "ee");
            op.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
            op.get(NAME).set("global-modules");
            op.get(VALUE).set(value);
            managementClient.getControllerClient().execute(op);
            File testModuleRoot = new File(GlobalModulesTestCase.getModulePath(), "org/jboss/test/globalModule");
            File file = testModuleRoot;
            while (!(GlobalModulesTestCase.getModulePath().equals(file.getParentFile())))
                file = file.getParentFile();

            GlobalModulesTestCase.GlobalModulesTestCaseServerSetup.deleteRecursively(file);
            GlobalModulesTestCase.GlobalModulesTestCaseServerSetup.createTestModule(testModuleRoot);
        }

        @Override
        public void tearDown(final ManagementClient managementClient, final String containerId) throws Exception {
            final ModelNode op = new ModelNode();
            op.get(OP_ADDR).set(SUBSYSTEM, "ee");
            op.get(OP).set(UNDEFINE_ATTRIBUTE_OPERATION);
            op.get(NAME).set("global-modules");
            managementClient.getControllerClient().execute(op);
            File testModuleRoot = new File(GlobalModulesTestCase.getModulePath(), "org/jboss/test/globalModule");
            File file = testModuleRoot;
            while (!(GlobalModulesTestCase.getModulePath().equals(file.getParentFile())))
                file = file.getParentFile();

            GlobalModulesTestCase.GlobalModulesTestCaseServerSetup.deleteRecursively(file);
        }

        private static void deleteRecursively(File file) {
            if (file.exists()) {
                if (file.isDirectory()) {
                    for (String name : file.list()) {
                        GlobalModulesTestCase.GlobalModulesTestCaseServerSetup.deleteRecursively(new File(file, name));
                    }
                }
                file.delete();
            }
        }

        private static void createTestModule(File testModuleRoot) throws IOException {
            if (testModuleRoot.exists()) {
                throw new IllegalArgumentException((testModuleRoot + " already exists"));
            }
            File file = new File(testModuleRoot, "main");
            if (!(file.mkdirs())) {
                throw new IllegalArgumentException(("Could not create " + file));
            }
            URL url = GlobalModulesTestCase.class.getResource("module.xml");
            if (url == null) {
                throw new IllegalStateException("Could not find module.xml");
            }
            GlobalModulesTestCase.copyFile(new File(file, "module.xml"), url.openStream());
            JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "globalTest.jar");
            jar.addClasses(GlobalModuleEjb.class, GlobalModuleInterceptor.class);
            final ClassLoader cl = GlobalModulesTestCase.class.getClassLoader();
            // create annotation index
            Indexer indexer = new Indexer();
            indexer.index(cl.getResourceAsStream(((GlobalModuleEjb.class.getName().replace(".", "/")) + ".class")));
            indexer.index(cl.getResourceAsStream(((GlobalModuleInterceptor.class.getName().replace(".", "/")) + ".class")));
            final Index index = indexer.complete();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            IndexWriter writer = new IndexWriter(out);
            writer.write(index);
            jar.addAsManifestResource(new ByteArrayAsset(out.toByteArray()), "jandex.idx");
            FileOutputStream jarFile = new FileOutputStream(new File(file, "globalTest.jar"));
            try {
                jar.as(ZipExporter.class).exportTo(jarFile);
            } finally {
                jarFile.flush();
                jarFile.close();
            }
        }
    }

    @ArquillianResource
    private InitialContext ctx;

    @Test
    public void testDataSourceDefinition() throws SQLException, NamingException {
        GlobalModuleEjb bean = ((GlobalModuleEjb) (ctx.lookup(("java:module/" + (GlobalModuleEjb.class.getSimpleName())))));
        Assert.assertEquals(((GlobalModuleInterceptor.class.getSimpleName()) + (GlobalModuleEjb.class.getSimpleName())), bean.getName());
    }
}

