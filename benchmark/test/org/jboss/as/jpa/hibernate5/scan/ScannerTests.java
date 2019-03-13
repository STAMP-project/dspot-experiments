/**
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.jpa.hibernate5.scan;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.hibernate.boot.archive.internal.ArchiveHelper;
import org.jboss.vfs.TempFileProvider;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class ScannerTests {
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
            ScannerTests.tempFileProvider = TempFileProvider.create("test", new ScheduledThreadPoolExecutor(2));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // we make an assumption here that the directory which holds compiled classes (nested) also holds
        // sources.   We therefore look for our module directory name, and use that to locate bundles
        final URL scannerTestsClassFileUrl = ScannerTests.originalClassLoader.getResource(((ScannerTests.class.getName().replace('.', '/')) + ".class"));
        if (scannerTestsClassFileUrl == null) {
            // blow up
            Assert.fail("Could not find ScannerTests class file url");
        }
        // look for the module name in that url
        final int position = scannerTestsClassFileUrl.getFile().lastIndexOf("/hibernate5/");
        if (position == (-1)) {
            Assert.fail("Unable to setup packaging test");
        }
        final String moduleDirectoryPath = scannerTestsClassFileUrl.getFile().substring(0, (position + ("/hibernate5".length())));
        final File moduleDirectory = new File(moduleDirectoryPath);
        ScannerTests.testSrcDirectory = new File(new File(moduleDirectory, "src"), "test");
        final File bundlesDirectory = new File(ScannerTests.testSrcDirectory, "bundles");
        try {
            ScannerTests.bundleClassLoader = new URLClassLoader(new URL[]{ bundlesDirectory.toURL() }, ScannerTests.originalClassLoader);
        } catch (MalformedURLException e) {
            Assert.fail("Unable to build custom class loader");
        }
        ScannerTests.shrinkwrapArchiveDirectory = new File(moduleDirectory, "target/packages");
        ScannerTests.shrinkwrapArchiveDirectory.mkdirs();
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

