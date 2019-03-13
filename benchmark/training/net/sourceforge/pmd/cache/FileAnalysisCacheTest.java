/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cache;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.net.sourceforge.pmd.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;


public class FileAnalysisCacheTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Rule
    public RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    private File unexistingCacheFile;

    private File newCacheFile;

    private File emptyCacheFile;

    private File sourceFile;

    @Test
    public void testLoadFromNonExistingFile() throws IOException {
        final FileAnalysisCache cache = new FileAnalysisCache(unexistingCacheFile);
        Assert.assertNotNull("Cache creation from non existing file failed.", cache);
    }

    @Test
    public void testLoadFromEmptyFile() throws IOException {
        final FileAnalysisCache cache = new FileAnalysisCache(emptyCacheFile);
        Assert.assertNotNull("Cache creation from empty file failed.", cache);
    }

    @Test
    public void testLoadFromDirectoryShouldntThrow() throws IOException {
        new FileAnalysisCache(tempFolder.getRoot());
    }

    @Test
    public void testLoadFromUnreadableFileShouldntThrow() throws IOException {
        emptyCacheFile.setReadable(false);
        new FileAnalysisCache(emptyCacheFile);
    }

    @Test
    public void testStoreCreatesFile() {
        final FileAnalysisCache cache = new FileAnalysisCache(unexistingCacheFile);
        cache.persist();
        Assert.assertTrue("Cache file doesn't exist after store", unexistingCacheFile.exists());
    }

    @Test
    public void testStoreOnUnwritableFileShouldntThrow() {
        emptyCacheFile.setWritable(false);
        final FileAnalysisCache cache = new FileAnalysisCache(emptyCacheFile);
        cache.persist();
    }

    @Test
    public void testStorePersistsFilesWithViolations() {
        final FileAnalysisCache cache = new FileAnalysisCache(newCacheFile);
        cache.isUpToDate(sourceFile);
        final RuleViolation rv = Mockito.mock(RuleViolation.class);
        Mockito.when(rv.getFilename()).thenReturn(sourceFile.getPath());
        final net.sourceforge.pmd.Rule rule = Mockito.mock(Rule.class, Mockito.RETURNS_SMART_NULLS);
        Mockito.when(rv.getRule()).thenReturn(rule);
        cache.ruleViolationAdded(rv);
        cache.persist();
        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        Assert.assertTrue("Cache believes unmodified file with violations is not up to date", reloadedCache.isUpToDate(sourceFile));
        final List<RuleViolation> cachedViolations = reloadedCache.getCachedViolations(sourceFile);
        Assert.assertEquals("Cached rule violations count mismatch", 1, cachedViolations.size());
    }

    @Test
    public void testCacheValidityWithNoChanges() {
        final RuleSets rs = Mockito.mock(RuleSets.class);
        final ClassLoader cl = Mockito.mock(ClassLoader.class);
        setupCacheWithFiles(newCacheFile, rs, cl, sourceFile);
        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        reloadedCache.checkValidity(rs, cl);
        Assert.assertTrue("Cache believes unmodified file is not up to date without ruleset / classpath changes", reloadedCache.isUpToDate(sourceFile));
    }

    @Test
    public void testRulesetChangeInvalidatesCache() {
        final RuleSets rs = Mockito.mock(RuleSets.class);
        final ClassLoader cl = Mockito.mock(ClassLoader.class);
        setupCacheWithFiles(newCacheFile, rs, cl, sourceFile);
        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        Mockito.when(rs.getChecksum()).thenReturn(1L);
        reloadedCache.checkValidity(rs, cl);
        Assert.assertFalse("Cache believes unmodified file is up to date after ruleset changed", reloadedCache.isUpToDate(sourceFile));
    }

    @Test
    public void testAuxClasspathNonExistingAuxclasspathEntriesIgnored() throws IOException, MalformedURLException {
        final RuleSets rs = Mockito.mock(RuleSets.class);
        final URLClassLoader cl = Mockito.mock(URLClassLoader.class);
        Mockito.when(cl.getURLs()).thenReturn(new URL[]{ new File(tempFolder.getRoot(), "non-existing-dir").toURI().toURL() });
        setupCacheWithFiles(newCacheFile, rs, cl, sourceFile);
        final FileAnalysisCache analysisCache = new FileAnalysisCache(newCacheFile);
        Mockito.when(cl.getURLs()).thenReturn(new URL[]{  });
        analysisCache.checkValidity(rs, cl);
        Assert.assertTrue("Cache believes unmodified file is not up to date after non-existing auxclasspath entry removed", analysisCache.isUpToDate(sourceFile));
    }

    @Test
    public void testAuxClasspathChangeWithoutDFAorTypeResolutionDoesNotInvalidatesCache() throws IOException, MalformedURLException {
        final RuleSets rs = Mockito.mock(RuleSets.class);
        final URLClassLoader cl = Mockito.mock(URLClassLoader.class);
        Mockito.when(cl.getURLs()).thenReturn(new URL[]{  });
        setupCacheWithFiles(newCacheFile, rs, cl, sourceFile);
        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        Mockito.when(cl.getURLs()).thenReturn(new URL[]{ tempFolder.newFile().toURI().toURL() });
        reloadedCache.checkValidity(rs, cl);
        Assert.assertTrue("Cache believes unmodified file is not up to date after auxclasspath changed when no rule cares", reloadedCache.isUpToDate(sourceFile));
    }

    @Test
    public void testAuxClasspathChangeInvalidatesCache() throws IOException, MalformedURLException {
        final RuleSets rs = Mockito.mock(RuleSets.class);
        final URLClassLoader cl = Mockito.mock(URLClassLoader.class);
        Mockito.when(cl.getURLs()).thenReturn(new URL[]{  });
        setupCacheWithFiles(newCacheFile, rs, cl, sourceFile);
        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        final File classpathFile = tempFolder.newFile();
        Mockito.when(cl.getURLs()).thenReturn(new URL[]{ classpathFile.toURI().toURL() });
        // Make sure the auxclasspath file is not empty
        Files.write(Paths.get(classpathFile.getAbsolutePath()), "some text".getBytes());
        final net.sourceforge.pmd.Rule r = Mockito.mock(Rule.class);
        Mockito.when(r.isDfa()).thenReturn(true);
        Mockito.when(rs.getAllRules()).thenReturn(Collections.singleton(r));
        reloadedCache.checkValidity(rs, cl);
        Assert.assertFalse("Cache believes unmodified file is up to date after auxclasspath changed", reloadedCache.isUpToDate(sourceFile));
    }

    @Test
    public void testAuxClasspathJarContentsChangeInvalidatesCache() throws IOException, MalformedURLException {
        final RuleSets rs = Mockito.mock(RuleSets.class);
        final URLClassLoader cl = Mockito.mock(URLClassLoader.class);
        final File classpathFile = tempFolder.newFile();
        Mockito.when(cl.getURLs()).thenReturn(new URL[]{ classpathFile.toURI().toURL() });
        final net.sourceforge.pmd.Rule r = Mockito.mock(Rule.class);
        Mockito.when(r.isDfa()).thenReturn(true);
        Mockito.when(rs.getAllRules()).thenReturn(Collections.singleton(r));
        setupCacheWithFiles(newCacheFile, rs, cl, sourceFile);
        // Edit the auxclasspath referenced file
        Files.write(Paths.get(classpathFile.getAbsolutePath()), "some text".getBytes());
        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        reloadedCache.checkValidity(rs, cl);
        Assert.assertFalse("Cache believes cache is up to date when a auxclasspath file changed", reloadedCache.isUpToDate(sourceFile));
    }

    @Test
    public void testClasspathChangeInvalidatesCache() throws IOException, MalformedURLException {
        final RuleSets rs = Mockito.mock(RuleSets.class);
        final ClassLoader cl = Mockito.mock(ClassLoader.class);
        final File classpathFile = tempFolder.newFile();
        setupCacheWithFiles(newCacheFile, rs, cl, sourceFile);
        // Edit the classpath referenced file
        Files.write(Paths.get(classpathFile.getAbsolutePath()), "some text".getBytes());
        System.setProperty("java.class.path", (((System.getProperty("java.class.path")) + (File.pathSeparator)) + (classpathFile.getAbsolutePath())));
        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        reloadedCache.checkValidity(rs, cl);
        Assert.assertFalse("Cache believes cache is up to date when the classpath changed", reloadedCache.isUpToDate(sourceFile));
    }

    @Test
    public void testClasspathContentsChangeInvalidatesCache() throws IOException, MalformedURLException {
        final RuleSets rs = Mockito.mock(RuleSets.class);
        final ClassLoader cl = Mockito.mock(ClassLoader.class);
        final File classpathFile = tempFolder.newFile();
        // Add a file to classpath
        Files.write(Paths.get(classpathFile.getAbsolutePath()), "some text".getBytes());
        System.setProperty("java.class.path", (((System.getProperty("java.class.path")) + (File.pathSeparator)) + (classpathFile.getAbsolutePath())));
        setupCacheWithFiles(newCacheFile, rs, cl, sourceFile);
        // Change the file's contents
        Files.write(Paths.get(classpathFile.getAbsolutePath()), "some other text".getBytes());
        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        reloadedCache.checkValidity(rs, cl);
        Assert.assertFalse("Cache believes cache is up to date when a classpath file changed", reloadedCache.isUpToDate(sourceFile));
    }

    @Test
    public void testWildcardClasspath() throws IOException, MalformedURLException {
        final RuleSets rs = Mockito.mock(RuleSets.class);
        final ClassLoader cl = Mockito.mock(ClassLoader.class);
        setupCacheWithFiles(newCacheFile, rs, cl, sourceFile);
        // Prepare two jar files
        final File classpathJar1 = tempFolder.newFile("mylib1.jar");
        Files.write(classpathJar1.toPath(), "content of mylib1.jar".getBytes(StandardCharsets.UTF_8));
        final File classpathJar2 = tempFolder.newFile("mylib2.jar");
        Files.write(classpathJar2.toPath(), "content of mylib2.jar".getBytes(StandardCharsets.UTF_8));
        System.setProperty("java.class.path", ((((System.getProperty("java.class.path")) + (File.pathSeparator)) + (tempFolder.getRoot().getAbsolutePath())) + "/*"));
        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        reloadedCache.checkValidity(rs, cl);
        Assert.assertFalse("Cache believes cache is up to date when the classpath changed", reloadedCache.isUpToDate(sourceFile));
    }

    @Test
    public void testWildcardClasspathContentsChangeInvalidatesCache() throws IOException, MalformedURLException {
        final RuleSets rs = Mockito.mock(RuleSets.class);
        final ClassLoader cl = Mockito.mock(ClassLoader.class);
        // Prepare two jar files
        final File classpathJar1 = tempFolder.newFile("mylib1.jar");
        Files.write(classpathJar1.toPath(), "content of mylib1.jar".getBytes(StandardCharsets.UTF_8));
        final File classpathJar2 = tempFolder.newFile("mylib2.jar");
        Files.write(classpathJar2.toPath(), "content of mylib2.jar".getBytes(StandardCharsets.UTF_8));
        System.setProperty("java.class.path", ((((System.getProperty("java.class.path")) + (File.pathSeparator)) + (tempFolder.getRoot().getAbsolutePath())) + "/*"));
        setupCacheWithFiles(newCacheFile, rs, cl, sourceFile);
        // Change one file's contents
        Files.write(Paths.get(classpathJar2.getAbsolutePath()), "some other text".getBytes(StandardCharsets.UTF_8));
        final FileAnalysisCache reloadedCache = new FileAnalysisCache(newCacheFile);
        reloadedCache.checkValidity(rs, cl);
        Assert.assertFalse("Cache believes cache is up to date when the classpath changed", reloadedCache.isUpToDate(sourceFile));
    }

    @Test
    public void testUnknownFileIsNotUpToDate() throws IOException {
        final FileAnalysisCache cache = new FileAnalysisCache(newCacheFile);
        Assert.assertFalse("Cache believes an unknown file is up to date", cache.isUpToDate(sourceFile));
    }

    @Test
    public void testFileIsUpToDate() throws IOException {
        setupCacheWithFiles(newCacheFile, Mockito.mock(RuleSets.class), Mockito.mock(ClassLoader.class), sourceFile);
        final FileAnalysisCache cache = new FileAnalysisCache(newCacheFile);
        Assert.assertTrue("Cache believes a known, unchanged file is not up to date", cache.isUpToDate(sourceFile));
    }

    @Test
    public void testFileIsNotUpToDateWhenEdited() throws IOException {
        setupCacheWithFiles(newCacheFile, Mockito.mock(RuleSets.class), Mockito.mock(ClassLoader.class), sourceFile);
        // Edit the file
        Files.write(Paths.get(sourceFile.getAbsolutePath()), "some text".getBytes());
        final FileAnalysisCache cache = new FileAnalysisCache(newCacheFile);
        Assert.assertFalse("Cache believes a known, changed file is up to date", cache.isUpToDate(sourceFile));
    }
}

