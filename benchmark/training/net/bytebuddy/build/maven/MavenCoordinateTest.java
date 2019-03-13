package net.bytebuddy.build.maven;


import org.eclipse.aether.artifact.Artifact;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class MavenCoordinateTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String JAR = "jar";

    @Test
    public void testAsArtifact() throws Exception {
        Artifact artifact = new MavenCoordinate(MavenCoordinateTest.FOO, MavenCoordinateTest.BAR, MavenCoordinateTest.QUX, MavenCoordinateTest.JAR).asArtifact();
        MatcherAssert.assertThat(artifact.getGroupId(), CoreMatchers.is(MavenCoordinateTest.FOO));
        MatcherAssert.assertThat(artifact.getArtifactId(), CoreMatchers.is(MavenCoordinateTest.BAR));
        MatcherAssert.assertThat(artifact.getVersion(), CoreMatchers.is(MavenCoordinateTest.QUX));
        MatcherAssert.assertThat(artifact.getExtension(), CoreMatchers.is(MavenCoordinateTest.JAR));
    }
}

