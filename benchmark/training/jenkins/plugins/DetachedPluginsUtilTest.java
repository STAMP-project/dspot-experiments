package jenkins.plugins;


import DetachedPluginsUtil.DETACHED_LIST;
import DetachedPluginsUtil.DetachedPlugin;
import hudson.util.VersionNumber;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import jenkins.util.java.JavaUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;


public class DetachedPluginsUtilTest {
    @Test
    public void checkJaxb() {
        final List<DetachedPluginsUtil.DetachedPlugin> plugins = DETACHED_LIST.stream().filter(( plugin) -> plugin.getShortName().equals("jaxb")).collect(Collectors.toList());
        Assert.assertEquals(1, plugins.size());
        DetachedPluginsUtil.DetachedPlugin jaxb = plugins.get(0);
        Assert.assertEquals(new VersionNumber("11"), jaxb.getMinimumJavaVersion());
        final List<DetachedPluginsUtil.DetachedPlugin> detachedPlugins = DetachedPluginsUtil.getDetachedPlugins();
        if (JavaUtils.isRunningWithJava8OrBelow()) {
            Assert.assertEquals(0, detachedPlugins.stream().filter(( plugin) -> plugin.getShortName().equals("jaxb")).collect(Collectors.toList()).size());
        } else
            if (JavaUtils.getCurrentJavaRuntimeVersionNumber().isNewerThanOrEqualTo(new VersionNumber("11.0.2"))) {
                Assert.assertEquals(1, detachedPlugins.stream().filter(( plugin) -> plugin.getShortName().equals("jaxb")).collect(Collectors.toList()).size());
                final List<DetachedPluginsUtil.DetachedPlugin> detachedPluginsSince2_161 = DetachedPluginsUtil.getDetachedPlugins(new VersionNumber("2.161"));
                Assert.assertEquals(1, detachedPluginsSince2_161.size());
                Assert.assertEquals("jaxb", detachedPluginsSince2_161.get(0).getShortName());
            }

    }

    /**
     * Checks the format of the <code>/jenkins/split-plugins.txt</code> file has maximum 4 columns.
     */
    @Test
    public void checkSplitPluginsFileFormat() throws IOException {
        final List<String> splitPluginsLines = IOUtils.readLines(getClass().getResourceAsStream("/jenkins/split-plugins.txt"));
        Assert.assertTrue((!(splitPluginsLines.isEmpty())));
        // File is not only comments
        final List<String> linesWithoutComments = splitPluginsLines.stream().filter(( line) -> !(line.startsWith("#"))).collect(Collectors.toList());
        Assert.assertFalse("weird, split-plugins.txt only has comments?", linesWithoutComments.isEmpty());
        // 
        Assert.assertFalse("no whitespaces only lines allowed", linesWithoutComments.stream().filter(( line) -> line.trim().isEmpty()).anyMatch(( line) -> !(line.isEmpty())));
        Assert.assertTrue("max 4 columns is supported", linesWithoutComments.stream().map(( line) -> line.split(" ")).noneMatch(( line) -> (line.length) > 4));
    }
}

