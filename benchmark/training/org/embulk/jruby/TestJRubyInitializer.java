package org.embulk.jruby;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;


public class TestJRubyInitializer {
    @Test
    public void testArguments() {
        final List<String> loadPath = new ArrayList<>();
        loadPath.add("/load/path");
        final List<String> classpath = new ArrayList<>();
        classpath.add("/classpath");
        final List<String> options = new ArrayList<>();
        options.add("--option");
        options.add("arg");
        final JRubyInitializer initializer = JRubyInitializer.of(null, LoggerFactory.getLogger(TestJRubyInitializer.class), "/gem/home", true, loadPath, classpath, options, "/bundle");
        // TODO: Test through mocked ScriptingContainerDelegate, not through probing methods for testing.
        Assert.assertEquals("/gem/home", initializer.probeGemHomeForTesting());
        Assert.assertTrue(initializer.probeUseDefaultEmbulkGemHomeForTesting());
        Assert.assertEquals(1, initializer.probeJRubyLoadPathForTesting().size());
        Assert.assertEquals("/load/path", initializer.probeJRubyLoadPathForTesting().get(0));
        Assert.assertEquals(1, initializer.probeJRubyClasspathForTesting().size());
        Assert.assertEquals("/classpath", initializer.probeJRubyClasspathForTesting().get(0));
        Assert.assertEquals(2, initializer.probeJRubyOptionsForTesting().size());
        Assert.assertEquals("--option", initializer.probeJRubyOptionsForTesting().get(0));
        Assert.assertEquals("arg", initializer.probeJRubyOptionsForTesting().get(1));
        Assert.assertEquals("/bundle", initializer.probeJRubyBundlerPluginSourceDirectoryForTesting());
    }
}

