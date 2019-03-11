package net.bytebuddy.build.maven;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.EntryPoint;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.inline.MethodNameTransformer;
import net.bytebuddy.test.utility.MockitoRule;
import org.apache.maven.plugin.MojoExecutionException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.build.EntryPoint.Default.REBASE;
import static net.bytebuddy.build.EntryPoint.Default.REDEFINE;
import static net.bytebuddy.build.EntryPoint.Default.REDEFINE_LOCAL;


public class InitializationTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    private static final String JAR = "jar";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private ClassLoaderResolver classLoaderResolver;

    @Test
    public void testResolved() throws Exception {
        Initialization initialization = new Initialization();
        initialization.groupId = InitializationTest.BAR;
        initialization.artifactId = InitializationTest.QUX;
        initialization.version = InitializationTest.BAZ;
        initialization.packaging = InitializationTest.JAR;
        MatcherAssert.assertThat(initialization.getGroupId(InitializationTest.FOO), CoreMatchers.is(InitializationTest.BAR));
        MatcherAssert.assertThat(initialization.getArtifactId(InitializationTest.FOO), CoreMatchers.is(InitializationTest.QUX));
        MatcherAssert.assertThat(initialization.getVersion(InitializationTest.FOO), CoreMatchers.is(InitializationTest.BAZ));
        MatcherAssert.assertThat(initialization.getPackaging(InitializationTest.JAR), CoreMatchers.is(InitializationTest.JAR));
    }

    @Test
    public void testRebase() throws Exception {
        Initialization initialization = new Initialization();
        initialization.entryPoint = REBASE.name();
        MatcherAssert.assertThat(initialization.getEntryPoint(classLoaderResolver, InitializationTest.BAR, InitializationTest.QUX, InitializationTest.BAZ, InitializationTest.JAR), CoreMatchers.is(((EntryPoint) (REBASE))));
        Mockito.verifyZeroInteractions(classLoaderResolver);
    }

    @Test
    public void testRedefine() throws Exception {
        Initialization initialization = new Initialization();
        initialization.entryPoint = REDEFINE.name();
        MatcherAssert.assertThat(initialization.getEntryPoint(classLoaderResolver, InitializationTest.BAR, InitializationTest.QUX, InitializationTest.BAZ, InitializationTest.JAR), CoreMatchers.is(((EntryPoint) (REDEFINE))));
        Mockito.verifyZeroInteractions(classLoaderResolver);
    }

    @Test
    public void testRedefineLocal() throws Exception {
        Initialization initialization = new Initialization();
        initialization.entryPoint = REDEFINE_LOCAL.name();
        MatcherAssert.assertThat(initialization.getEntryPoint(classLoaderResolver, InitializationTest.BAR, InitializationTest.QUX, InitializationTest.BAZ, InitializationTest.JAR), CoreMatchers.is(((EntryPoint) (REDEFINE_LOCAL))));
        Mockito.verifyZeroInteractions(classLoaderResolver);
    }

    @Test
    public void testDecorate() throws Exception {
        Initialization initialization = new Initialization();
        initialization.entryPoint = DECORATE.name();
        MatcherAssert.assertThat(initialization.getEntryPoint(classLoaderResolver, InitializationTest.BAR, InitializationTest.QUX, InitializationTest.BAZ, InitializationTest.JAR), CoreMatchers.is(((EntryPoint) (DECORATE))));
        Mockito.verifyZeroInteractions(classLoaderResolver);
    }

    @Test
    public void testCustom() throws Exception {
        Initialization initialization = new Initialization();
        initialization.entryPoint = InitializationTest.Foo.class.getName();
        Mockito.when(classLoaderResolver.resolve(new MavenCoordinate(InitializationTest.BAR, InitializationTest.QUX, InitializationTest.BAZ, InitializationTest.JAR))).thenReturn(InitializationTest.Foo.class.getClassLoader());
        MatcherAssert.assertThat(initialization.getEntryPoint(classLoaderResolver, InitializationTest.BAR, InitializationTest.QUX, InitializationTest.BAZ, InitializationTest.JAR), CoreMatchers.instanceOf(InitializationTest.Foo.class));
        Mockito.verify(classLoaderResolver).resolve(new MavenCoordinate(InitializationTest.BAR, InitializationTest.QUX, InitializationTest.BAZ, InitializationTest.JAR));
        Mockito.verifyNoMoreInteractions(classLoaderResolver);
    }

    @Test(expected = MojoExecutionException.class)
    public void testCustomFailed() throws Exception {
        Initialization initialization = new Initialization();
        initialization.entryPoint = InitializationTest.FOO;
        Mockito.when(classLoaderResolver.resolve(new MavenCoordinate(InitializationTest.BAR, InitializationTest.QUX, InitializationTest.BAZ, InitializationTest.JAR))).thenReturn(InitializationTest.Foo.class.getClassLoader());
        initialization.getEntryPoint(classLoaderResolver, InitializationTest.BAR, InitializationTest.QUX, InitializationTest.BAZ, InitializationTest.JAR);
    }

    @Test(expected = MojoExecutionException.class)
    public void testEmpty() throws Exception {
        Initialization initialization = new Initialization();
        initialization.entryPoint = "";
        initialization.getEntryPoint(classLoaderResolver, InitializationTest.BAR, InitializationTest.QUX, InitializationTest.BAZ, InitializationTest.JAR);
    }

    @Test(expected = MojoExecutionException.class)
    public void testNull() throws Exception {
        new Initialization().getEntryPoint(classLoaderResolver, InitializationTest.BAR, InitializationTest.QUX, InitializationTest.BAZ, InitializationTest.JAR);
    }

    @Test
    public void testDefault() throws Exception {
        Initialization initialization = Initialization.makeDefault();
        MatcherAssert.assertThat(initialization.entryPoint, CoreMatchers.is(REBASE.name()));
        MatcherAssert.assertThat(initialization.groupId, CoreMatchers.nullValue(String.class));
        MatcherAssert.assertThat(initialization.artifactId, CoreMatchers.nullValue(String.class));
        MatcherAssert.assertThat(initialization.version, CoreMatchers.nullValue(String.class));
    }

    @Test
    public void testAsCoordinateResolved() throws Exception {
        Initialization initialization = new Initialization();
        initialization.groupId = InitializationTest.BAR;
        initialization.artifactId = InitializationTest.QUX;
        initialization.version = InitializationTest.BAZ;
        MatcherAssert.assertThat(initialization.asCoordinate(InitializationTest.FOO, InitializationTest.FOO, InitializationTest.FOO, InitializationTest.JAR), CoreMatchers.is(new MavenCoordinate(InitializationTest.BAR, InitializationTest.QUX, InitializationTest.BAZ, InitializationTest.JAR)));
    }

    @Test
    public void testAsCoordinateUnresolved() throws Exception {
        Initialization initialization = new Initialization();
        MatcherAssert.assertThat(initialization.asCoordinate(InitializationTest.BAR, InitializationTest.QUX, InitializationTest.BAZ, InitializationTest.JAR), CoreMatchers.is(new MavenCoordinate(InitializationTest.BAR, InitializationTest.QUX, InitializationTest.BAZ, InitializationTest.JAR)));
    }

    public static class Foo implements EntryPoint {
        public ByteBuddy byteBuddy(ClassFileVersion classFileVersion) {
            throw new AssertionError();
        }

        public DynamicType.Builder<?> transform(TypeDescription typeDescription, ByteBuddy byteBuddy, ClassFileLocator classFileLocator, MethodNameTransformer methodNameTransformer) {
            throw new AssertionError();
        }
    }
}

