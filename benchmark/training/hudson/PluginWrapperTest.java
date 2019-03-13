package hudson;


import PluginWrapper.Dependency;
import PluginWrapper.NOTICE;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class PluginWrapperTest {
    @Test
    public void dependencyTest() {
        String version = "plugin:0.0.2";
        PluginWrapper.Dependency dependency = new PluginWrapper.Dependency(version);
        Assert.assertEquals("plugin", dependency.shortName);
        Assert.assertEquals("0.0.2", dependency.version);
        Assert.assertEquals(false, dependency.optional);
    }

    @Test
    public void optionalDependencyTest() {
        String version = "plugin:0.0.2;resolution:=optional";
        PluginWrapper.Dependency dependency = new PluginWrapper.Dependency(version);
        Assert.assertEquals("plugin", dependency.shortName);
        Assert.assertEquals("0.0.2", dependency.version);
        Assert.assertEquals(true, dependency.optional);
    }

    @Test
    public void jenkinsCoreTooOld() throws Exception {
        PluginWrapper pw = pluginWrapper("fake").requiredCoreVersion("3.0").buildLoaded();
        try {
            pw.resolvePluginDependencies();
            Assert.fail();
        } catch (IOException ex) {
            assertContains(ex, "fake version 42 failed to load", "update Jenkins from version 2.0 to version 3.0");
        }
    }

    @Test
    public void dependencyNotInstalled() throws Exception {
        PluginWrapper pw = pluginWrapper("dependee").deps("dependency:42").buildLoaded();
        try {
            pw.resolvePluginDependencies();
            Assert.fail();
        } catch (IOException ex) {
            assertContains(ex, "dependee version 42 failed to load", "dependency version 42 is missing. To fix, install version 42 or later");
        }
    }

    @Test
    public void dependencyOutdated() throws Exception {
        pluginWrapper("dependency").version("3").buildLoaded();
        PluginWrapper pw = pluginWrapper("dependee").deps("dependency:5").buildLoaded();
        try {
            pw.resolvePluginDependencies();
            Assert.fail();
        } catch (IOException ex) {
            assertContains(ex, "dependee version 42 failed to load", "dependency version 3 is older than required. To fix, install version 5 or later");
        }
    }

    @Test
    public void dependencyFailedToLoad() throws Exception {
        pluginWrapper("dependency").version("5").buildFailed();
        PluginWrapper pw = pluginWrapper("dependee").deps("dependency:3").buildLoaded();
        try {
            pw.resolvePluginDependencies();
            Assert.fail();
        } catch (IOException ex) {
            assertContains(ex, "dependee version 42 failed to load", "dependency version 5 failed to load. Fix this plugin first");
        }
    }

    // per test
    private final HashMap<String, PluginWrapper> plugins = new HashMap<>();

    private final PluginManager pm = Mockito.mock(PluginManager.class);

    {
        Mockito.when(pm.getPlugin(ArgumentMatchers.any(String.class))).thenAnswer(new Answer<PluginWrapper>() {
            @Override
            public PluginWrapper answer(InvocationOnMock invocation) throws Throwable {
                return plugins.get(invocation.getArguments()[0]);
            }
        });
    }

    private final class PluginWrapperBuilder {
        private String name;

        private String version = "42";

        private String requiredCoreVersion = "1.0";

        private List<PluginWrapper.Dependency> deps = new ArrayList<>();

        private List<PluginWrapper.Dependency> optDeps = new ArrayList<>();

        private PluginWrapperBuilder(String name) {
            this.name = name;
        }

        public PluginWrapperTest.PluginWrapperBuilder version(String version) {
            this.version = version;
            return this;
        }

        public PluginWrapperTest.PluginWrapperBuilder requiredCoreVersion(String requiredCoreVersion) {
            this.requiredCoreVersion = requiredCoreVersion;
            return this;
        }

        public PluginWrapperTest.PluginWrapperBuilder deps(String... deps) {
            for (String dep : deps) {
                this.deps.add(new PluginWrapper.Dependency(dep));
            }
            return this;
        }

        public PluginWrapperTest.PluginWrapperBuilder optDeps(String... optDeps) {
            for (String dep : optDeps) {
                this.optDeps.add(new PluginWrapper.Dependency(dep));
            }
            return this;
        }

        private PluginWrapper buildLoaded() {
            PluginWrapper pw = build();
            plugins.put(name, pw);
            return pw;
        }

        private PluginWrapper buildFailed() {
            PluginWrapper pw = build();
            NOTICE.addPlugin(pw);
            return pw;
        }

        private PluginWrapper build() {
            Manifest manifest = Mockito.mock(Manifest.class);
            Attributes attributes = new Attributes();
            attributes.put(new Attributes.Name("Short-Name"), name);
            attributes.put(new Attributes.Name("Jenkins-Version"), requiredCoreVersion);
            attributes.put(new Attributes.Name("Plugin-Version"), version);
            Mockito.when(manifest.getMainAttributes()).thenReturn(attributes);
            return new PluginWrapper(pm, new File((("/tmp/" + (name)) + ".jpi")), manifest, null, null, new File((("/tmp/" + (name)) + ".jpi.disabled")), deps, optDeps);
        }
    }

    @Issue("JENKINS-52665")
    @Test
    public void isSnapshot() {
        Assert.assertFalse(PluginWrapper.isSnapshot("1.0"));
        Assert.assertFalse(PluginWrapper.isSnapshot("1.0-alpha-1"));
        Assert.assertFalse(PluginWrapper.isSnapshot("1.0-rc9999.abc123def456"));
        Assert.assertTrue(PluginWrapper.isSnapshot("1.0-SNAPSHOT"));
        Assert.assertTrue(PluginWrapper.isSnapshot("1.0-20180719.153600-1"));
        Assert.assertTrue(PluginWrapper.isSnapshot("1.0-SNAPSHOT (private-abcd1234-jqhacker)"));
    }
}

