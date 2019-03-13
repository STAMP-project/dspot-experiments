/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.jpa.mockprovider.classtransformer;


import java.io.InputStream;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Hibernate "hibernate.ejb.use_class_enhancer" test that causes hibernate to add a
 * javax.persistence.spi.ClassTransformer to the pu.
 *
 * @author Scott Marlow
 */
@RunWith(Arquillian.class)
public class ClassFileTransformerTestCase {
    private static final String ARCHIVE_NAME = "jpa_classTransformerTestWithMockProvider";

    @ArquillianResource
    private InitialContext iniCtx;

    @Test
    public void test_use_class_enhancer() throws Exception {
        try {
            Assert.assertTrue("entity classes are enhanced", ((TestClassTransformer.getTransformedClasses().size()) > 0));
        } finally {
            TestClassTransformer.clearTransformedClasses();
        }
    }

    @Test
    public void test_persistenceUnitInfoURLS() throws Exception {
        try {
            Assert.assertTrue(("testing that PersistenceUnitInfo.getPersistenceUnitRootUrl() url is vfs based, failed because getPersistenceUnitRootUrl is " + (TestPersistenceProvider.getPersistenceUnitInfo("mypc").getPersistenceUnitRootUrl().getProtocol())), "vfs".equals(TestPersistenceProvider.getPersistenceUnitInfo("mypc").getPersistenceUnitRootUrl().getProtocol()));
            InputStream inputStream = TestPersistenceProvider.getPersistenceUnitInfo("mypc").getPersistenceUnitRootUrl().openStream();
            Assert.assertNotNull("getPersistenceUnitRootUrl().openStream() returned non-null value", inputStream);
            Assert.assertTrue("getPersistenceUnitRootUrl returned a JarInputStream", (inputStream instanceof JarInputStream));
            JarInputStream jarInputStream = ((JarInputStream) (inputStream));
            ZipEntry entry = jarInputStream.getNextEntry();
            Assert.assertNotNull("got zip entry from getPersistenceUnitRootUrl", entry);
            while ((entry != null) && (!(entry.getName().contains("persistence.xml")))) {
                entry = jarInputStream.getNextEntry();
            } 
            Assert.assertNotNull(("didn't find persistence.xml in getPersistenceUnitRootUrl, details=" + (urlOpenStreamDetails(TestPersistenceProvider.getPersistenceUnitInfo("mypc").getPersistenceUnitRootUrl().openStream()))), entry);
        } finally {
            TestPersistenceProvider.clearLastPersistenceUnitInfo();
        }
    }

    @Test
    public void test_persistenceProviderAdapterInitialized() {
        try {
            Assert.assertTrue("persistence unit adapter was initialized", TestAdapter.wasInitialized());
        } finally {
            TestAdapter.clearInitialized();
        }
    }
}

