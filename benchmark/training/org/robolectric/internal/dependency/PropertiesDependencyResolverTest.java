package org.robolectric.internal.dependency;


import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class PropertiesDependencyResolverTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private DependencyJar exampleDep;

    private DependencyResolver mock;

    private boolean cColonBackslash;

    @Test
    public void whenAbsolutePathIsProvidedInProperties_shouldReturnFileUrl() throws Exception {
        String absolutePath = (cColonBackslash) ? "c:\\tmp\\file.jar" : "/tmp/file.jar";
        DependencyResolver resolver = new PropertiesDependencyResolver(propsFile("com.group:example:1.3", new File(absolutePath).getAbsoluteFile()), mock);
        URL url = resolver.getLocalArtifactUrl(exampleDep);
        if (cColonBackslash) {
            assertThat(url).isEqualTo(Paths.get("c:\\tmp\\file.jar").toUri().toURL());
        } else {
            assertThat(url).isEqualTo(Paths.get("/tmp/file.jar").toUri().toURL());
        }
    }

    @Test
    public void whenRelativePathIsProvidedInProperties_shouldReturnFileUrl() throws Exception {
        DependencyResolver resolver = new PropertiesDependencyResolver(propsFile("com.group:example:1.3", new File("path", "1")), mock);
        URL url = resolver.getLocalArtifactUrl(exampleDep);
        assertThat(url).isEqualTo(temporaryFolder.getRoot().toPath().resolve("path").resolve("1").toUri().toURL());
    }

    @Test
    public void whenMissingFromProperties_shouldDelegate() throws Exception {
        DependencyResolver resolver = new PropertiesDependencyResolver(propsFile("nothing", new File("interesting")), mock);
        Mockito.when(mock.getLocalArtifactUrl(exampleDep)).thenReturn(new URL("file:///path/3"));
        URL url = resolver.getLocalArtifactUrl(exampleDep);
        assertThat(url).isEqualTo(new URL("file:///path/3"));
    }

    @Test
    public void whenDelegateIsNull_shouldGiveGoodMessage() throws Exception {
        DependencyResolver resolver = new PropertiesDependencyResolver(propsFile("nothing", new File("interesting")), null);
        try {
            resolver.getLocalArtifactUrl(exampleDep);
            Assert.fail("should have failed");
        } catch (Exception e) {
            assertThat(e.getMessage()).contains(("no artifacts found for " + (exampleDep)));
        }
    }
}

