package com.simpligility.maven.plugins.android.phase01generatesources;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Covers method {@link GenerateSourcesMojo#getPackageCompareMap(Set)} with tests
 *
 * @author Oleg Green - olegalex.green@gmail.com
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(GenerateSourcesMojo.class)
public class GetPackageCompareMapTest {
    public static final String PROJECT_ARTIFACT_ID = "main_application";

    public static final String PROJECT_PACKAGE_NAME = "com.jayway.maven.application";

    public static final String COM_JAYWAY_MAVEN_LIBRARY_PACKAGE = "com.jayway.maven.library";

    public static final String COM_JAYWAY_MAVEN_LIBRARY2_PACKAGE = "com.jayway.maven.library2";

    public static final String COM_JAYWAY_MAVEN_LIBRARY3_PACKAGE = "com.jayway.maven.library3";

    public static final Artifact LIBRARY1_ARTIFACT = GetPackageCompareMapTest.createArtifact("library1");

    public static final Artifact LIBRARY2_ARTIFACT = GetPackageCompareMapTest.createArtifact("library2");

    public static final Artifact LIBRARY3_ARTIFACT = GetPackageCompareMapTest.createArtifact("library3");

    public static final Map<Artifact, String> TEST_DATA_1 = new HashMap<Artifact, String>() {
        {
            put(GetPackageCompareMapTest.LIBRARY1_ARTIFACT, GetPackageCompareMapTest.COM_JAYWAY_MAVEN_LIBRARY_PACKAGE);
            put(GetPackageCompareMapTest.LIBRARY2_ARTIFACT, GetPackageCompareMapTest.PROJECT_PACKAGE_NAME);
            put(GetPackageCompareMapTest.LIBRARY3_ARTIFACT, GetPackageCompareMapTest.COM_JAYWAY_MAVEN_LIBRARY_PACKAGE);
        }
    };

    public static final Map<Artifact, String> TEST_DATA_2 = new HashMap<Artifact, String>() {
        {
            put(GetPackageCompareMapTest.LIBRARY1_ARTIFACT, GetPackageCompareMapTest.COM_JAYWAY_MAVEN_LIBRARY_PACKAGE);
            put(GetPackageCompareMapTest.LIBRARY2_ARTIFACT, GetPackageCompareMapTest.COM_JAYWAY_MAVEN_LIBRARY2_PACKAGE);
            put(GetPackageCompareMapTest.LIBRARY3_ARTIFACT, GetPackageCompareMapTest.COM_JAYWAY_MAVEN_LIBRARY3_PACKAGE);
        }
    };

    private MavenProject project;

    private Artifact projectArtifact;

    private GenerateSourcesMojo mojo;

    @Test(expected = IllegalArgumentException.class)
    public void testNoDependencies() throws MojoExecutionException {
        PowerMock.replay(mojo);
        mojo.getPackageCompareMap(null);
    }

    @Test
    public void testEmptyDependencies() throws MojoExecutionException {
        PowerMock.replay(mojo);
        Map<String, Set<Artifact>> map = mojo.getPackageCompareMap(new HashSet<Artifact>());
        Assert.assertNotNull(map);
        Assert.assertEquals(1, map.size());
        Assert.assertTrue(map.containsKey(GetPackageCompareMapTest.PROJECT_PACKAGE_NAME));
        Set<Artifact> artifactSet = map.get(GetPackageCompareMapTest.PROJECT_PACKAGE_NAME);
        Assert.assertEquals(1, artifactSet.size());
        Assert.assertTrue(artifactSet.contains(projectArtifact));
    }

    @Test
    public void testData1() throws Exception {
        mockExtractPackageNameFromArtifactMethod(GetPackageCompareMapTest.TEST_DATA_1);
        PowerMock.replay(mojo);
        Map<String, Set<Artifact>> map = mojo.getPackageCompareMap(GetPackageCompareMapTest.TEST_DATA_1.keySet());
        Assert.assertNotNull(map);
        Assert.assertEquals(2, map.size());
        Assert.assertTrue(map.containsKey(GetPackageCompareMapTest.PROJECT_PACKAGE_NAME));
        Assert.assertTrue(map.containsKey(GetPackageCompareMapTest.COM_JAYWAY_MAVEN_LIBRARY_PACKAGE));
        Set<Artifact> artifactSet1 = map.get(GetPackageCompareMapTest.PROJECT_PACKAGE_NAME);
        Assert.assertEquals(2, artifactSet1.size());
        Assert.assertTrue(artifactSet1.contains(GetPackageCompareMapTest.LIBRARY2_ARTIFACT));
        Assert.assertTrue(artifactSet1.contains(projectArtifact));
        Set<Artifact> artifactSet2 = map.get(GetPackageCompareMapTest.COM_JAYWAY_MAVEN_LIBRARY_PACKAGE);
        Assert.assertEquals(2, artifactSet2.size());
        Assert.assertTrue(artifactSet2.contains(GetPackageCompareMapTest.LIBRARY1_ARTIFACT));
        Assert.assertTrue(artifactSet2.contains(GetPackageCompareMapTest.LIBRARY3_ARTIFACT));
        PowerMock.verify(mojo);
        EasyMock.verify(project, projectArtifact);
    }

    @Test
    public void testData2() throws Exception {
        mockExtractPackageNameFromArtifactMethod(GetPackageCompareMapTest.TEST_DATA_2);
        PowerMock.replay(mojo);
        Map<String, Set<Artifact>> map = mojo.getPackageCompareMap(GetPackageCompareMapTest.TEST_DATA_2.keySet());
        Assert.assertNotNull(map);
        Assert.assertEquals(4, map.size());
        Assert.assertTrue(map.containsKey(GetPackageCompareMapTest.PROJECT_PACKAGE_NAME));
        Set<Artifact> artifactSet1 = map.get(GetPackageCompareMapTest.PROJECT_PACKAGE_NAME);
        Assert.assertEquals(1, artifactSet1.size());
        Assert.assertTrue(artifactSet1.contains(projectArtifact));
        Set<Artifact> artifactSet2 = map.get(GetPackageCompareMapTest.COM_JAYWAY_MAVEN_LIBRARY_PACKAGE);
        Assert.assertEquals(1, artifactSet2.size());
        Assert.assertTrue(artifactSet2.contains(GetPackageCompareMapTest.LIBRARY1_ARTIFACT));
        Set<Artifact> artifactSet3 = map.get(GetPackageCompareMapTest.COM_JAYWAY_MAVEN_LIBRARY2_PACKAGE);
        Assert.assertEquals(1, artifactSet3.size());
        Assert.assertTrue(artifactSet3.contains(GetPackageCompareMapTest.LIBRARY2_ARTIFACT));
        Set<Artifact> artifactSet4 = map.get(GetPackageCompareMapTest.COM_JAYWAY_MAVEN_LIBRARY3_PACKAGE);
        Assert.assertEquals(1, artifactSet4.size());
        Assert.assertTrue(artifactSet4.contains(GetPackageCompareMapTest.LIBRARY3_ARTIFACT));
        PowerMock.verify(mojo);
        EasyMock.verify(project, projectArtifact);
    }
}

