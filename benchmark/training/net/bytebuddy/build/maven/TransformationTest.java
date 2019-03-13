package net.bytebuddy.build.maven;


import org.apache.maven.plugin.MojoExecutionException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class TransformationTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    private static final String JAR = "jar";

    @Test
    public void testResolved() throws Exception {
        Transformation transformation = new Transformation();
        transformation.plugin = TransformationTest.FOO;
        transformation.groupId = TransformationTest.BAR;
        transformation.artifactId = TransformationTest.QUX;
        transformation.version = TransformationTest.BAZ;
        transformation.packaging = TransformationTest.JAR;
        MatcherAssert.assertThat(transformation.getPlugin(), CoreMatchers.is(TransformationTest.FOO));
        MatcherAssert.assertThat(transformation.getRawPlugin(), CoreMatchers.is(TransformationTest.FOO));
        MatcherAssert.assertThat(transformation.getGroupId(TransformationTest.FOO), CoreMatchers.is(TransformationTest.BAR));
        MatcherAssert.assertThat(transformation.getArtifactId(TransformationTest.FOO), CoreMatchers.is(TransformationTest.QUX));
        MatcherAssert.assertThat(transformation.getVersion(TransformationTest.FOO), CoreMatchers.is(TransformationTest.BAZ));
        MatcherAssert.assertThat(transformation.getPackaging(TransformationTest.JAR), CoreMatchers.is(TransformationTest.JAR));
    }

    @Test
    public void testUndefined() throws Exception {
        Transformation transformation = new Transformation();
        MatcherAssert.assertThat(transformation.getGroupId(TransformationTest.BAR), CoreMatchers.is(TransformationTest.BAR));
        MatcherAssert.assertThat(transformation.getArtifactId(TransformationTest.QUX), CoreMatchers.is(TransformationTest.QUX));
        MatcherAssert.assertThat(transformation.getVersion(TransformationTest.BAZ), CoreMatchers.is(TransformationTest.BAZ));
        MatcherAssert.assertThat(transformation.getPackaging(TransformationTest.JAR), CoreMatchers.is(TransformationTest.JAR));
    }

    @Test
    public void testEmpty() throws Exception {
        Transformation transformation = new Transformation();
        transformation.groupId = "";
        transformation.artifactId = "";
        transformation.version = "";
        transformation.packaging = "";
        MatcherAssert.assertThat(transformation.getGroupId(TransformationTest.BAR), CoreMatchers.is(TransformationTest.BAR));
        MatcherAssert.assertThat(transformation.getArtifactId(TransformationTest.QUX), CoreMatchers.is(TransformationTest.QUX));
        MatcherAssert.assertThat(transformation.getVersion(TransformationTest.BAZ), CoreMatchers.is(TransformationTest.BAZ));
        MatcherAssert.assertThat(transformation.getPackaging(TransformationTest.JAR), CoreMatchers.is(TransformationTest.JAR));
    }

    @Test(expected = MojoExecutionException.class)
    public void testUndefinedName() throws Exception {
        new Transformation().getPlugin();
    }

    @Test
    public void testAsCoordinateResolved() throws Exception {
        Transformation transformation = new Transformation();
        transformation.groupId = TransformationTest.BAR;
        transformation.artifactId = TransformationTest.QUX;
        transformation.version = TransformationTest.BAZ;
        MatcherAssert.assertThat(transformation.asCoordinate(TransformationTest.FOO, TransformationTest.FOO, TransformationTest.FOO, TransformationTest.JAR), CoreMatchers.is(new MavenCoordinate(TransformationTest.BAR, TransformationTest.QUX, TransformationTest.BAZ, TransformationTest.JAR)));
    }

    @Test
    public void testAsCoordinateUnresolved() throws Exception {
        Transformation transformation = new Transformation();
        MatcherAssert.assertThat(transformation.asCoordinate(TransformationTest.BAR, TransformationTest.QUX, TransformationTest.BAZ, TransformationTest.JAR), CoreMatchers.is(new MavenCoordinate(TransformationTest.BAR, TransformationTest.QUX, TransformationTest.BAZ, TransformationTest.JAR)));
    }
}

