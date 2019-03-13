/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.packaging;


import AvailableSettings.SCANNER;
import StandardScanParameters.INSTANCE;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.hibernate.boot.archive.scan.internal.StandardScanner;
import org.hibernate.boot.archive.scan.spi.MappingFileDescriptor;
import org.hibernate.boot.archive.scan.spi.ScanEnvironment;
import org.hibernate.boot.archive.scan.spi.ScanOptions;
import org.hibernate.boot.archive.scan.spi.ScanResult;
import org.hibernate.boot.archive.scan.spi.Scanner;
import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
import org.hibernate.jpa.test.pack.defaultpar.ApplicationServer;
import org.hibernate.jpa.test.pack.defaultpar.Version;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 */
public class ScannerTest extends PackagingTestCase {
    @Test
    public void testNativeScanner() throws Exception {
        File defaultPar = buildDefaultPar();
        addPackageToClasspath(defaultPar);
        PersistenceUnitDescriptor descriptor = new ParsedPersistenceXmlDescriptor(defaultPar.toURL());
        ScanEnvironment env = new org.hibernate.jpa.boot.internal.StandardJpaScanEnvironmentImpl(descriptor);
        ScanOptions options = new org.hibernate.boot.archive.scan.internal.StandardScanOptions("hbm,class", descriptor.isExcludeUnlistedClasses());
        Scanner scanner = new StandardScanner();
        ScanResult scanResult = scanner.scan(env, options, INSTANCE);
        Assert.assertEquals(3, scanResult.getLocatedClasses().size());
        assertClassesContained(scanResult, ApplicationServer.class);
        assertClassesContained(scanResult, Version.class);
        Assert.assertEquals(2, scanResult.getLocatedMappingFiles().size());
        for (MappingFileDescriptor mappingFileDescriptor : scanResult.getLocatedMappingFiles()) {
            Assert.assertNotNull(mappingFileDescriptor.getName());
            Assert.assertNotNull(mappingFileDescriptor.getStreamAccess());
            InputStream stream = mappingFileDescriptor.getStreamAccess().accessInputStream();
            Assert.assertNotNull(stream);
            stream.close();
        }
    }

    @Test
    public void testCustomScanner() throws Exception {
        File defaultPar = buildDefaultPar();
        File explicitPar = buildExplicitPar();
        addPackageToClasspath(defaultPar, explicitPar);
        EntityManagerFactory emf;
        CustomScanner.resetUsed();
        final HashMap integration = new HashMap();
        emf = Persistence.createEntityManagerFactory("defaultpar", integration);
        Assert.assertTrue((!(CustomScanner.isUsed())));
        emf.close();
        CustomScanner.resetUsed();
        emf = Persistence.createEntityManagerFactory("manager1", integration);
        Assert.assertTrue(CustomScanner.isUsed());
        emf.close();
        CustomScanner.resetUsed();
        integration.put(SCANNER, new CustomScanner());
        emf = Persistence.createEntityManagerFactory("defaultpar", integration);
        Assert.assertTrue(CustomScanner.isUsed());
        emf.close();
        CustomScanner.resetUsed();
        emf = Persistence.createEntityManagerFactory("defaultpar", null);
        Assert.assertTrue((!(CustomScanner.isUsed())));
        emf.close();
    }
}

