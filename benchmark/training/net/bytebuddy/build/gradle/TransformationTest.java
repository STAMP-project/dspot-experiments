package net.bytebuddy.build.gradle;


import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import net.bytebuddy.test.utility.MockitoRule;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public class TransformationTest {
    private static final String FOO = "foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private File file;

    @Mock
    private File explicit;

    @Mock
    private File other;

    @Mock
    private Project project;

    @Test
    public void testPlugin() throws Exception {
        Transformation transformation = new Transformation(project);
        transformation.setPlugin(TransformationTest.FOO);
        MatcherAssert.assertThat(transformation.getPlugin(), CoreMatchers.is(TransformationTest.FOO));
    }

    @Test(expected = GradleException.class)
    public void testEmptyPlugin() throws Exception {
        getPlugin();
    }

    @Test(expected = GradleException.class)
    public void testUnnamedPlugin() throws Exception {
        Transformation transformation = new Transformation(project);
        transformation.setPlugin("");
        transformation.getPlugin();
    }

    @Test
    public void testRawPlugin() throws Exception {
        MatcherAssert.assertThat(getRawPlugin(), CoreMatchers.nullValue(String.class));
    }

    @Test
    public void testExplicitClassPath() throws Exception {
        Transformation transformation = new Transformation(project);
        transformation.setClassPath(Collections.singleton(file));
        Iterator<? extends File> iterator = transformation.getClassPath(explicit, Collections.singleton(other)).iterator();
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.is(file));
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
    }

    @Test
    public void testImplicitClassPath() throws Exception {
        Transformation transformation = new Transformation(project);
        Iterator<? extends File> iterator = transformation.getClassPath(explicit, Collections.singleton(other)).iterator();
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.is(explicit));
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.is(other));
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
    }
}

