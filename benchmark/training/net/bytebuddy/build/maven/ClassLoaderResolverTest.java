package net.bytebuddy.build.maven;


import net.bytebuddy.test.utility.MockitoRule;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResult;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ClassLoaderResolverTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    private static final String JAR = "jar";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private Log log;

    @Mock
    private RepositorySystem repositorySystem;

    @Mock
    private RepositorySystemSession repositorySystemSession;

    @Mock
    private DependencyNode root;

    @Mock
    private DependencyNode child;

    private ClassLoaderResolver classLoaderResolver;

    @Test
    public void testResolution() throws Exception {
        MatcherAssert.assertThat(classLoaderResolver.resolve(new MavenCoordinate(ClassLoaderResolverTest.FOO, ClassLoaderResolverTest.BAR, ClassLoaderResolverTest.QUX, ClassLoaderResolverTest.JAR)), CoreMatchers.sameInstance(classLoaderResolver.resolve(new MavenCoordinate(ClassLoaderResolverTest.FOO, ClassLoaderResolverTest.BAR, ClassLoaderResolverTest.QUX, ClassLoaderResolverTest.JAR))));
    }

    @Test(expected = MojoExecutionException.class)
    public void testCollectionFailure() throws Exception {
        Mockito.when(repositorySystem.collectDependencies(ArgumentMatchers.eq(repositorySystemSession), ArgumentMatchers.any(CollectRequest.class))).thenThrow(new DependencyCollectionException(new org.eclipse.aether.collection.CollectResult(new CollectRequest())));
        classLoaderResolver.resolve(new MavenCoordinate(ClassLoaderResolverTest.FOO, ClassLoaderResolverTest.BAR, ClassLoaderResolverTest.QUX, ClassLoaderResolverTest.JAR));
    }

    @Test(expected = MojoExecutionException.class)
    public void testResolutionFailure() throws Exception {
        Mockito.when(repositorySystem.resolveDependencies(ArgumentMatchers.eq(repositorySystemSession), ArgumentMatchers.any(DependencyRequest.class))).thenThrow(new org.eclipse.aether.resolution.DependencyResolutionException(new DependencyResult(new DependencyRequest(root, Mockito.mock(DependencyFilter.class))), new Throwable()));
        classLoaderResolver.resolve(new MavenCoordinate(ClassLoaderResolverTest.FOO, ClassLoaderResolverTest.BAR, ClassLoaderResolverTest.QUX, ClassLoaderResolverTest.JAR));
    }

    @Test
    public void testClose() throws Exception {
        classLoaderResolver.resolve(new MavenCoordinate(ClassLoaderResolverTest.FOO, ClassLoaderResolverTest.BAR, ClassLoaderResolverTest.QUX, ClassLoaderResolverTest.JAR));
        classLoaderResolver.close();
    }
}

