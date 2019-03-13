/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
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
package org.jboss.as.jpa.hibernate4.scan;


import AbstractScannerImpl.ResultCollector;
import VirtualFileSystemArchiveDescriptorFactory.INSTANCE;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.hibernate.jpa.boot.archive.internal.ArchiveHelper;
import org.hibernate.jpa.boot.archive.spi.ArchiveDescriptor;
import org.hibernate.jpa.boot.internal.ClassDescriptorImpl;
import org.hibernate.jpa.boot.scan.internal.StandardScanOptions;
import org.hibernate.jpa.boot.scan.spi.AbstractScannerImpl;
import org.hibernate.jpa.boot.spi.MappingFileDescriptor;
import org.hibernate.jpa.test.pack.defaultpar.ApplicationServer;
import org.hibernate.jpa.test.pack.defaultpar.Version;
import org.hibernate.jpa.test.pack.explodedpar.Carpet;
import org.jboss.vfs.TempFileProvider;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class ScannerTest {
    protected static ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();

    protected static ClassLoader bundleClassLoader;

    protected static TempFileProvider tempFileProvider;

    protected static File testSrcDirectory;

    /**
     * Directory where shrink-wrap built archives are written
     */
    protected static File shrinkwrapArchiveDirectory;

    static {
        try {
            ScannerTest.tempFileProvider = TempFileProvider.create("test", new ScheduledThreadPoolExecutor(2));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // we make an assumption here that the directory which holds compiled classes (nested) also holds
        // sources.   We therefore look for our module directory name, and use that to locate bundles
        final URL scannerTestsClassFileUrl = ScannerTest.originalClassLoader.getResource(((ScannerTest.class.getName().replace('.', '/')) + ".class"));
        if (scannerTestsClassFileUrl == null) {
            // blow up
            Assert.fail("Could not find ScannerTests class file url");
        }
        // look for the module name in that url
        final int position = scannerTestsClassFileUrl.getFile().lastIndexOf("/hibernate4_3/");
        if (position == (-1)) {
            Assert.fail("Unable to setup packaging test");
        }
        final String moduleDirectoryPath = scannerTestsClassFileUrl.getFile().substring(0, (position + ("/hibernate4_3".length())));
        final File moduleDirectory = new File(moduleDirectoryPath);
        ScannerTest.testSrcDirectory = new File(new File(moduleDirectory, "src"), "test");
        final File bundlesDirectory = new File(ScannerTest.testSrcDirectory, "bundles");
        try {
            ScannerTest.bundleClassLoader = new URLClassLoader(new URL[]{ bundlesDirectory.toURL() }, ScannerTest.originalClassLoader);
        } catch (MalformedURLException e) {
            Assert.fail("Unable to build custom class loader");
        }
        ScannerTest.shrinkwrapArchiveDirectory = new File(moduleDirectory, "target/packages");
        ScannerTest.shrinkwrapArchiveDirectory.mkdirs();
    }

    @Test
    public void testInputStreamZippedJar() throws Exception {
        File defaultPar = buildDefaultPar();
        addPackageToClasspath(defaultPar);
        final VirtualFile virtualFile = VFS.getChild(defaultPar.getAbsolutePath());
        Closeable closeable = VFS.mountZip(virtualFile, virtualFile, ScannerTest.tempFileProvider);
        try {
            ArchiveDescriptor archiveDescriptor = INSTANCE.buildArchiveDescriptor(defaultPar.toURI().toURL());
            AbstractScannerImpl.ResultCollector resultCollector = new AbstractScannerImpl.ResultCollector(new StandardScanOptions());
            archiveDescriptor.visitArchive(new AbstractScannerImpl.ArchiveContextImpl(new PersistenceUnitDescriptorAdapter(), true, resultCollector));
            validateResults(resultCollector, ApplicationServer.class, Version.class);
        } finally {
            closeable.close();
        }
    }

    @Test
    public void testNestedJarProtocol() throws Exception {
        File defaultPar = buildDefaultPar();
        File nestedEar = buildNestedEar(defaultPar);
        addPackageToClasspath(nestedEar);
        final VirtualFile nestedEarVirtualFile = VFS.getChild(nestedEar.getAbsolutePath());
        Closeable closeable = VFS.mountZip(nestedEarVirtualFile, nestedEarVirtualFile, ScannerTest.tempFileProvider);
        try {
            VirtualFile parVirtualFile = nestedEarVirtualFile.getChild("defaultpar.par");
            Closeable closeable2 = VFS.mountZip(parVirtualFile, parVirtualFile, ScannerTest.tempFileProvider);
            try {
                ArchiveDescriptor archiveDescriptor = INSTANCE.buildArchiveDescriptor(parVirtualFile.toURL());
                AbstractScannerImpl.ResultCollector resultCollector = new AbstractScannerImpl.ResultCollector(new StandardScanOptions());
                archiveDescriptor.visitArchive(new AbstractScannerImpl.ArchiveContextImpl(new PersistenceUnitDescriptorAdapter(), true, resultCollector));
                validateResults(resultCollector, ApplicationServer.class, Version.class);
            } finally {
                closeable2.close();
            }
        } finally {
            closeable.close();
        }
        File nestedEarDir = buildNestedEarDir(defaultPar);
        final VirtualFile nestedEarDirVirtualFile = VFS.getChild(nestedEarDir.getAbsolutePath());
        try {
            VirtualFile parVirtualFile = nestedEarDirVirtualFile.getChild("defaultpar.par");
            closeable = VFS.mountZip(parVirtualFile, parVirtualFile, ScannerTest.tempFileProvider);
            try {
                ArchiveDescriptor archiveDescriptor = INSTANCE.buildArchiveDescriptor(parVirtualFile.toURL());
                AbstractScannerImpl.ResultCollector resultCollector = new AbstractScannerImpl.ResultCollector(new StandardScanOptions());
                archiveDescriptor.visitArchive(new AbstractScannerImpl.ArchiveContextImpl(new PersistenceUnitDescriptorAdapter(), true, resultCollector));
                validateResults(resultCollector, ApplicationServer.class, Version.class);
            } finally {
                closeable.close();
            }
        } finally {
            closeable.close();
        }
    }

    @Test
    public void testJarProtocol() throws Exception {
        File war = buildWar();
        addPackageToClasspath(war);
        final VirtualFile warVirtualFile = VFS.getChild(war.getAbsolutePath());
        Closeable closeable = VFS.mountZip(warVirtualFile, warVirtualFile, ScannerTest.tempFileProvider);
        try {
            ArchiveDescriptor archiveDescriptor = INSTANCE.buildArchiveDescriptor(warVirtualFile.toURL());
            AbstractScannerImpl.ResultCollector resultCollector = new AbstractScannerImpl.ResultCollector(new StandardScanOptions());
            archiveDescriptor.visitArchive(new AbstractScannerImpl.ArchiveContextImpl(new PersistenceUnitDescriptorAdapter(), true, resultCollector));
            validateResults(resultCollector, org.hibernate.jpa.test.pack.war.ApplicationServer.class, org.hibernate.jpa.test.pack.war.Version.class);
        } finally {
            closeable.close();
        }
    }

    @Test
    public void testZippedJar() throws Exception {
        File defaultPar = buildDefaultPar();
        addPackageToClasspath(defaultPar);
        final VirtualFile virtualFile = VFS.getChild(defaultPar.getAbsolutePath());
        Closeable closeable = VFS.mountZip(virtualFile, virtualFile, ScannerTest.tempFileProvider);
        try {
            ArchiveDescriptor archiveDescriptor = INSTANCE.buildArchiveDescriptor(virtualFile.toURL());
            AbstractScannerImpl.ResultCollector resultCollector = new AbstractScannerImpl.ResultCollector(new StandardScanOptions());
            archiveDescriptor.visitArchive(new AbstractScannerImpl.ArchiveContextImpl(new PersistenceUnitDescriptorAdapter(), true, resultCollector));
            validateResults(resultCollector, ApplicationServer.class, Version.class);
        } finally {
            closeable.close();
        }
    }

    @Test
    public void testExplodedJar() throws Exception {
        File explodedPar = buildExplodedPar();
        addPackageToClasspath(explodedPar);
        String dirPath = explodedPar.getAbsolutePath();
        if (dirPath.endsWith("/")) {
            dirPath = dirPath.substring(0, ((dirPath.length()) - 1));
        }
        final VirtualFile virtualFile = VFS.getChild(dirPath);
        ArchiveDescriptor archiveDescriptor = INSTANCE.buildArchiveDescriptor(virtualFile.toURL());
        AbstractScannerImpl.ResultCollector resultCollector = new AbstractScannerImpl.ResultCollector(new StandardScanOptions());
        archiveDescriptor.visitArchive(new AbstractScannerImpl.ArchiveContextImpl(new PersistenceUnitDescriptorAdapter(), true, resultCollector));
        Assert.assertEquals(1, resultCollector.getClassDescriptorSet().size());
        Assert.assertEquals(1, resultCollector.getPackageDescriptorSet().size());
        Assert.assertEquals(1, resultCollector.getMappingFileSet().size());
        Assert.assertTrue(resultCollector.getClassDescriptorSet().contains(new ClassDescriptorImpl(Carpet.class.getName(), null)));
        for (MappingFileDescriptor mappingFileDescriptor : resultCollector.getMappingFileSet()) {
            Assert.assertNotNull(mappingFileDescriptor.getStreamAccess());
            final InputStream stream = mappingFileDescriptor.getStreamAccess().accessInputStream();
            Assert.assertNotNull(stream);
            stream.close();
        }
    }

    @Test
    public void testGetBytesFromInputStream() throws Exception {
        File file = buildLargeJar();
        long start = System.currentTimeMillis();
        InputStream stream = new BufferedInputStream(new FileInputStream(file));
        int oldLength = getBytesFromInputStream(stream).length;
        stream.close();
        long oldTime = (System.currentTimeMillis()) - start;
        start = System.currentTimeMillis();
        stream = new BufferedInputStream(new FileInputStream(file));
        int newLength = ArchiveHelper.getBytesFromInputStream(stream).length;
        stream.close();
        long newTime = (System.currentTimeMillis()) - start;
        Assert.assertEquals(oldLength, newLength);
        Assert.assertTrue((oldTime > newTime));
    }

    @Test
    public void testGetBytesFromZeroInputStream() throws Exception {
        // Ensure that JarVisitorFactory#getBytesFromInputStream
        // can handle 0 length streams gracefully.
        URL emptyTxtUrl = getClass().getResource("/org/hibernate/jpa/test/packaging/empty.txt");
        if (emptyTxtUrl == null) {
            throw new RuntimeException("Bah!");
        }
        InputStream emptyStream = new BufferedInputStream(emptyTxtUrl.openStream());
        int length = ArchiveHelper.getBytesFromInputStream(emptyStream).length;
        Assert.assertEquals(length, 0);
        emptyStream.close();
    }
}

