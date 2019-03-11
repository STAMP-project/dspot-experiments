/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;


import PMD.SUPPRESS_MARKER;
import RulePriority.HIGH;
import RulePriority.LOW;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import net.sourceforge.pmd.cache.FileAnalysisCache;
import net.sourceforge.pmd.cache.NoopAnalysisCache;
import net.sourceforge.pmd.renderers.CSVRenderer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.ClasspathClassLoader;
import org.junit.Assert;
import org.junit.Test;


public class ConfigurationTest {
    @Test
    public void testSuppressMarker() {
        PMDConfiguration configuration = new PMDConfiguration();
        Assert.assertEquals("Default suppress marker", SUPPRESS_MARKER, configuration.getSuppressMarker());
        configuration.setSuppressMarker("CUSTOM_MARKER");
        Assert.assertEquals("Changed suppress marker", "CUSTOM_MARKER", configuration.getSuppressMarker());
    }

    @Test
    public void testThreads() {
        PMDConfiguration configuration = new PMDConfiguration();
        Assert.assertEquals("Default threads", Runtime.getRuntime().availableProcessors(), configuration.getThreads());
        configuration.setThreads(0);
        Assert.assertEquals("Changed threads", 0, configuration.getThreads());
    }

    @Test
    public void testClassLoader() throws IOException {
        PMDConfiguration configuration = new PMDConfiguration();
        Assert.assertEquals("Default ClassLoader", PMDConfiguration.class.getClassLoader(), configuration.getClassLoader());
        configuration.prependClasspath("some.jar");
        Assert.assertEquals("Prepended ClassLoader class", ClasspathClassLoader.class, configuration.getClassLoader().getClass());
        URL[] urls = getURLs();
        Assert.assertEquals("urls length", 1, urls.length);
        Assert.assertTrue("url[0]", urls[0].toString().endsWith("/some.jar"));
        Assert.assertEquals("parent classLoader", PMDConfiguration.class.getClassLoader(), configuration.getClassLoader().getParent());
        configuration.setClassLoader(null);
        Assert.assertEquals("Revert to default ClassLoader", PMDConfiguration.class.getClassLoader(), configuration.getClassLoader());
    }

    @Test
    public void testRuleSets() {
        PMDConfiguration configuration = new PMDConfiguration();
        Assert.assertEquals("Default RuleSets", null, configuration.getRuleSets());
        configuration.setRuleSets("/rulesets/basic.xml");
        Assert.assertEquals("Changed RuleSets", "/rulesets/basic.xml", configuration.getRuleSets());
    }

    @Test
    public void testMinimumPriority() {
        PMDConfiguration configuration = new PMDConfiguration();
        Assert.assertEquals("Default minimum priority", LOW, configuration.getMinimumPriority());
        configuration.setMinimumPriority(HIGH);
        Assert.assertEquals("Changed minimum priority", HIGH, configuration.getMinimumPriority());
    }

    @Test
    public void testSourceEncoding() {
        PMDConfiguration configuration = new PMDConfiguration();
        Assert.assertEquals("Default source encoding", System.getProperty("file.encoding"), configuration.getSourceEncoding().name());
        configuration.setSourceEncoding(StandardCharsets.UTF_16LE.name());
        Assert.assertEquals("Changed source encoding", StandardCharsets.UTF_16LE, configuration.getSourceEncoding());
    }

    @Test
    public void testInputPaths() {
        PMDConfiguration configuration = new PMDConfiguration();
        Assert.assertEquals("Default input paths", null, configuration.getInputPaths());
        configuration.setInputPaths("a,b,c");
        Assert.assertEquals("Changed input paths", "a,b,c", configuration.getInputPaths());
    }

    @Test
    public void testReportShortNames() {
        PMDConfiguration configuration = new PMDConfiguration();
        Assert.assertEquals("Default report short names", false, configuration.isReportShortNames());
        configuration.setReportShortNames(true);
        Assert.assertEquals("Changed report short names", true, configuration.isReportShortNames());
    }

    @Test
    public void testReportFormat() {
        PMDConfiguration configuration = new PMDConfiguration();
        Assert.assertEquals("Default report format", null, configuration.getReportFormat());
        configuration.setReportFormat("csv");
        Assert.assertEquals("Changed report format", "csv", configuration.getReportFormat());
    }

    @Test
    public void testCreateRenderer() {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setReportFormat("csv");
        Renderer renderer = configuration.createRenderer();
        Assert.assertEquals("Renderer class", CSVRenderer.class, renderer.getClass());
        Assert.assertEquals("Default renderer show suppressed violations", false, renderer.isShowSuppressedViolations());
        configuration.setShowSuppressedViolations(true);
        renderer = configuration.createRenderer();
        Assert.assertEquals("Renderer class", CSVRenderer.class, renderer.getClass());
        Assert.assertEquals("Changed renderer show suppressed violations", true, renderer.isShowSuppressedViolations());
    }

    @Test
    public void testReportFile() {
        PMDConfiguration configuration = new PMDConfiguration();
        Assert.assertEquals("Default report file", null, configuration.getReportFile());
        configuration.setReportFile("somefile");
        Assert.assertEquals("Changed report file", "somefile", configuration.getReportFile());
    }

    @Test
    public void testShowSuppressedViolations() {
        PMDConfiguration configuration = new PMDConfiguration();
        Assert.assertEquals("Default show suppressed violations", false, configuration.isShowSuppressedViolations());
        configuration.setShowSuppressedViolations(true);
        Assert.assertEquals("Changed show suppressed violations", true, configuration.isShowSuppressedViolations());
    }

    @Test
    public void testReportProperties() {
        PMDConfiguration configuration = new PMDConfiguration();
        Assert.assertEquals("Default report properties size", 0, configuration.getReportProperties().size());
        configuration.getReportProperties().put("key", "value");
        Assert.assertEquals("Changed report properties size", 1, configuration.getReportProperties().size());
        Assert.assertEquals("Changed report properties value", "value", configuration.getReportProperties().get("key"));
        configuration.setReportProperties(new Properties());
        Assert.assertEquals("Replaced report properties size", 0, configuration.getReportProperties().size());
    }

    @Test
    public void testDebug() {
        PMDConfiguration configuration = new PMDConfiguration();
        Assert.assertEquals("Default debug", false, configuration.isDebug());
        configuration.setDebug(true);
        Assert.assertEquals("Changed debug", true, configuration.isDebug());
    }

    @Test
    public void testStressTest() {
        PMDConfiguration configuration = new PMDConfiguration();
        Assert.assertEquals("Default stress test", false, configuration.isStressTest());
        configuration.setStressTest(true);
        Assert.assertEquals("Changed stress test", true, configuration.isStressTest());
    }

    @Test
    public void testBenchmark() {
        PMDConfiguration configuration = new PMDConfiguration();
        Assert.assertEquals("Default benchmark", false, configuration.isBenchmark());
        configuration.setBenchmark(true);
        Assert.assertEquals("Changed benchmark", true, configuration.isBenchmark());
    }

    @Test
    public void testAnalysisCache() throws IOException {
        final PMDConfiguration configuration = new PMDConfiguration();
        Assert.assertNotNull("Default cache is null", configuration.getAnalysisCache());
        Assert.assertTrue("Default cache is not a noop", ((configuration.getAnalysisCache()) instanceof NoopAnalysisCache));
        configuration.setAnalysisCache(null);
        Assert.assertNotNull("Default cache was set to null", configuration.getAnalysisCache());
        final File cacheFile = File.createTempFile("pmd-", ".cache");
        cacheFile.deleteOnExit();
        final FileAnalysisCache analysisCache = new FileAnalysisCache(cacheFile);
        configuration.setAnalysisCache(analysisCache);
        Assert.assertSame("Confgured cache not stored", analysisCache, configuration.getAnalysisCache());
    }

    @Test
    public void testAnalysisCacheLocation() throws IOException {
        final PMDConfiguration configuration = new PMDConfiguration();
        configuration.setAnalysisCacheLocation(null);
        Assert.assertNotNull("Null cache location accepted", configuration.getAnalysisCache());
        Assert.assertTrue("Null cache location accepted", ((configuration.getAnalysisCache()) instanceof NoopAnalysisCache));
        configuration.setAnalysisCacheLocation("pmd.cache");
        Assert.assertNotNull("Not null cache location produces null cache", configuration.getAnalysisCache());
        Assert.assertTrue("File cache location doesn't produce a file cache", ((configuration.getAnalysisCache()) instanceof FileAnalysisCache));
    }

    @Test
    public void testIgnoreIncrementalAnalysis() throws IOException {
        final PMDConfiguration configuration = new PMDConfiguration();
        // set dummy cache location
        final File cacheFile = File.createTempFile("pmd-", ".cache");
        cacheFile.deleteOnExit();
        final FileAnalysisCache analysisCache = new FileAnalysisCache(cacheFile);
        configuration.setAnalysisCache(analysisCache);
        Assert.assertNotNull("Null cache location accepted", configuration.getAnalysisCache());
        Assert.assertFalse("Non null cache location, cache should not be noop", ((configuration.getAnalysisCache()) instanceof NoopAnalysisCache));
        configuration.setIgnoreIncrementalAnalysis(true);
        Assert.assertTrue("Ignoring incremental analysis should turn the cache into a noop", ((configuration.getAnalysisCache()) instanceof NoopAnalysisCache));
    }
}

