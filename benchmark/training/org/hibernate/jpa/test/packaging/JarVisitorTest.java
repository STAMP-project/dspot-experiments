/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.packaging;


import ClassDescriptor.Categorization;
import StandardArchiveDescriptorFactory.INSTANCE;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Collections;
import java.util.List;
import org.hibernate.boot.archive.internal.ArchiveHelper;
import org.hibernate.boot.archive.internal.ExplodedArchiveDescriptor;
import org.hibernate.boot.archive.internal.JarFileBasedArchiveDescriptor;
import org.hibernate.boot.archive.internal.JarProtocolArchiveDescriptor;
import org.hibernate.boot.archive.internal.StandardArchiveDescriptorFactory;
import org.hibernate.boot.archive.scan.internal.ScanResultCollector;
import org.hibernate.boot.archive.scan.internal.StandardScanOptions;
import org.hibernate.boot.archive.scan.internal.StandardScanParameters;
import org.hibernate.boot.archive.scan.spi.MappingFileDescriptor;
import org.hibernate.boot.archive.scan.spi.ScanEnvironment;
import org.hibernate.boot.archive.scan.spi.ScanResult;
import org.hibernate.boot.archive.spi.ArchiveDescriptor;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.jpa.test.pack.defaultpar.ApplicationServer;
import org.hibernate.jpa.test.pack.defaultpar.Version;
import org.hibernate.jpa.test.pack.explodedpar.Carpet;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 * @author Brett Meyer
 */
// Nothing dialect-specific -- no need to run in matrix.
@RequiresDialect(H2Dialect.class)
@SuppressWarnings("unchecked")
public class JarVisitorTest extends PackagingTestCase {
    @Test
    public void testHttp() throws Exception {
        final URL url = ArchiveHelper.getJarURLFromURLEntry(new URL("jar:http://www.ibiblio.org/maven/hibernate/jars/hibernate-annotations-3.0beta1.jar!/META-INF/persistence.xml"), "/META-INF/persistence.xml");
        try {
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
        } catch (IOException ie) {
            // fail silently
            return;
        }
        ScanResult result = standardScan(url);
        Assert.assertEquals(0, result.getLocatedClasses().size());
        Assert.assertEquals(0, result.getLocatedPackages().size());
        Assert.assertEquals(0, result.getLocatedMappingFiles().size());
    }

    private static class ScanEnvironmentImpl implements ScanEnvironment {
        private final URL rootUrl;

        private ScanEnvironmentImpl(URL rootUrl) {
            this.rootUrl = rootUrl;
        }

        @Override
        public URL getRootUrl() {
            return rootUrl;
        }

        @Override
        public List<URL> getNonRootUrls() {
            return Collections.emptyList();
        }

        @Override
        public List<String> getExplicitlyListedClassNames() {
            return Collections.emptyList();
        }

        @Override
        public List<String> getExplicitlyListedMappingFiles() {
            return Collections.emptyList();
        }
    }

    @Test
    public void testInputStreamZippedJar() throws Exception {
        File defaultPar = buildDefaultPar();
        addPackageToClasspath(defaultPar);
        ScanResult result = standardScan(defaultPar.toURL());
        validateResults(result, ApplicationServer.class, Version.class);
    }

    @Test
    public void testNestedJarProtocol() throws Exception {
        File defaultPar = buildDefaultPar();
        File nestedEar = buildNestedEar(defaultPar);
        File nestedEarDir = buildNestedEarDir(defaultPar);
        addPackageToClasspath(nestedEar);
        String jarFileName = (nestedEar.toURL().toExternalForm()) + "!/defaultpar.par";
        URL rootUrl = new URL(jarFileName);
        JarProtocolArchiveDescriptor archiveDescriptor = new JarProtocolArchiveDescriptor(StandardArchiveDescriptorFactory.INSTANCE, rootUrl, "");
        ScanEnvironment environment = new JarVisitorTest.ScanEnvironmentImpl(rootUrl);
        ScanResultCollector collector = new ScanResultCollector(environment, new StandardScanOptions(), StandardScanParameters.INSTANCE);
        archiveDescriptor.visitArchive(new org.hibernate.boot.archive.scan.spi.AbstractScannerImpl.ArchiveContextImpl(true, collector));
        validateResults(collector.toScanResult(), ApplicationServer.class, Version.class);
        jarFileName = (nestedEarDir.toURL().toExternalForm()) + "!/defaultpar.par";
        rootUrl = new URL(jarFileName);
        archiveDescriptor = new JarProtocolArchiveDescriptor(StandardArchiveDescriptorFactory.INSTANCE, rootUrl, "");
        environment = new JarVisitorTest.ScanEnvironmentImpl(rootUrl);
        collector = new ScanResultCollector(environment, new StandardScanOptions(), StandardScanParameters.INSTANCE);
        archiveDescriptor.visitArchive(new org.hibernate.boot.archive.scan.spi.AbstractScannerImpl.ArchiveContextImpl(true, collector));
        validateResults(collector.toScanResult(), ApplicationServer.class, Version.class);
    }

    @Test
    public void testJarProtocol() throws Exception {
        File war = buildWar();
        addPackageToClasspath(war);
        String jarFileName = (war.toURL().toExternalForm()) + "!/WEB-INF/classes";
        URL rootUrl = new URL(jarFileName);
        JarProtocolArchiveDescriptor archiveDescriptor = new JarProtocolArchiveDescriptor(StandardArchiveDescriptorFactory.INSTANCE, rootUrl, "");
        final ScanEnvironment environment = new JarVisitorTest.ScanEnvironmentImpl(rootUrl);
        final ScanResultCollector collector = new ScanResultCollector(environment, new StandardScanOptions(), StandardScanParameters.INSTANCE);
        archiveDescriptor.visitArchive(new org.hibernate.boot.archive.scan.spi.AbstractScannerImpl.ArchiveContextImpl(true, collector));
        validateResults(collector.toScanResult(), org.hibernate.jpa.test.pack.war.ApplicationServer.class, org.hibernate.jpa.test.pack.war.Version.class);
    }

    @Test
    public void testZippedJar() throws Exception {
        File defaultPar = buildDefaultPar();
        addPackageToClasspath(defaultPar);
        ScanResult result = standardScan(defaultPar.toURL());
        validateResults(result, ApplicationServer.class, Version.class);
    }

    @Test
    public void testExplodedJar() throws Exception {
        File explodedPar = buildExplodedPar();
        addPackageToClasspath(explodedPar);
        String dirPath = explodedPar.toURL().toExternalForm();
        // TODO - shouldn't  ExplodedJarVisitor take care of a trailing slash?
        if (dirPath.endsWith("/")) {
            dirPath = dirPath.substring(0, ((dirPath.length()) - 1));
        }
        ScanResult result = standardScan(ArchiveHelper.getURLFromPath(dirPath));
        Assert.assertEquals(1, result.getLocatedClasses().size());
        Assert.assertEquals(1, result.getLocatedPackages().size());
        Assert.assertEquals(1, result.getLocatedMappingFiles().size());
        Assert.assertTrue(result.getLocatedClasses().contains(new org.hibernate.boot.archive.scan.internal.ClassDescriptorImpl(Carpet.class.getName(), Categorization.MODEL, null)));
        for (MappingFileDescriptor mappingFileDescriptor : result.getLocatedMappingFiles()) {
            Assert.assertNotNull(mappingFileDescriptor.getStreamAccess());
            final InputStream stream = mappingFileDescriptor.getStreamAccess().accessInputStream();
            Assert.assertNotNull(stream);
            stream.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-6806")
    public void testJarVisitorFactory() throws Exception {
        final File explodedPar = buildExplodedPar();
        final File defaultPar = buildDefaultPar();
        addPackageToClasspath(explodedPar, defaultPar);
        // setting URL to accept vfs based protocol
        URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
            public URLStreamHandler createURLStreamHandler(String protocol) {
                if (("vfszip".equals(protocol)) || ("vfsfile".equals(protocol)))
                    return new URLStreamHandler() {
                        protected URLConnection openConnection(URL u) throws IOException {
                            return null;
                        }
                    };

                return null;
            }
        });
        URL jarUrl = defaultPar.toURL();
        ArchiveDescriptor descriptor = INSTANCE.buildArchiveDescriptor(jarUrl);
        Assert.assertEquals(JarFileBasedArchiveDescriptor.class.getName(), descriptor.getClass().getName());
        jarUrl = explodedPar.toURL();
        descriptor = INSTANCE.buildArchiveDescriptor(jarUrl);
        Assert.assertEquals(ExplodedArchiveDescriptor.class.getName(), descriptor.getClass().getName());
        jarUrl = new URL(defaultPar.toURL().toExternalForm().replace("file:", "vfszip:"));
        descriptor = INSTANCE.buildArchiveDescriptor(jarUrl);
        Assert.assertEquals(JarFileBasedArchiveDescriptor.class.getName(), descriptor.getClass().getName());
        jarUrl = new URL(explodedPar.toURL().toExternalForm().replace("file:", "vfsfile:"));
        descriptor = INSTANCE.buildArchiveDescriptor(jarUrl);
        Assert.assertEquals(ExplodedArchiveDescriptor.class.getName(), descriptor.getClass().getName());
    }

    @Test
    @TestForIssue(jiraKey = "EJB-230")
    public void testDuplicateFilterExplodedJarExpected() throws Exception {
        // File explodedPar = buildExplodedPar();
        // addPackageToClasspath( explodedPar );
        // 
        // Filter[] filters = getFilters();
        // Filter[] dupeFilters = new Filter[filters.length * 2];
        // int index = 0;
        // for ( Filter filter : filters ) {
        // dupeFilters[index++] = filter;
        // }
        // filters = getFilters();
        // for ( Filter filter : filters ) {
        // dupeFilters[index++] = filter;
        // }
        // String dirPath = explodedPar.toURL().toExternalForm();
        // // TODO - shouldn't  ExplodedJarVisitor take care of a trailing slash?
        // if ( dirPath.endsWith( "/" ) ) {
        // dirPath = dirPath.substring( 0, dirPath.length() - 1 );
        // }
        // JarVisitor jarVisitor = new ExplodedJarVisitor( dirPath, dupeFilters );
        // assertEquals( "explodedpar", jarVisitor.getUnqualifiedJarName() );
        // Set[] entries = jarVisitor.getMatchingEntries();
        // assertEquals( 1, entries[1].size() );
        // assertEquals( 1, entries[0].size() );
        // assertEquals( 1, entries[2].size() );
        // for ( Entry entry : ( Set<Entry> ) entries[2] ) {
        // InputStream is = entry.getInputStream();
        // if ( is != null ) {
        // assertTrue( 0 < is.available() );
        // is.close();
        // }
        // }
        // for ( Entry entry : ( Set<Entry> ) entries[5] ) {
        // InputStream is = entry.getInputStream();
        // if ( is != null ) {
        // assertTrue( 0 < is.available() );
        // is.close();
        // }
        // }
        // 
        // Entry entry = new Entry( Carpet.class.getName(), null );
        // assertTrue( entries[1].contains( entry ) );
    }

    @Test
    @TestForIssue(jiraKey = "HHH-7835")
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
    @TestForIssue(jiraKey = "HHH-7835")
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

