package com.simpligility.maven.plugins.android;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.apache.maven.artifact.Artifact;
import org.junit.Assert;
import org.junit.Test;


public class InclusionExclusionResolverTest {
    private static final Artifact A1 = InclusionExclusionResolverTest.artifact("jar", "G1", "A1", "1.0");

    private static final Artifact A2 = InclusionExclusionResolverTest.artifact("jar", "G2", "A1", "1.0");

    private static final Artifact A3 = InclusionExclusionResolverTest.artifact("aar", "G1", "A2", "1.0");

    private static final Artifact A4 = InclusionExclusionResolverTest.artifact("jar", "G1", "A3", "2.0-rc");

    private static final Artifact A5 = InclusionExclusionResolverTest.artifact("aar", "G2", "A2", "2.0-rc");

    private static final Collection<Artifact> ALL = InclusionExclusionResolverTest.collect(InclusionExclusionResolverTest.A1, InclusionExclusionResolverTest.A2, InclusionExclusionResolverTest.A3, InclusionExclusionResolverTest.A4, InclusionExclusionResolverTest.A5);

    private static final Collection<Artifact> NONE = Collections.emptySet();

    @Test
    public void testSkipDependenciesFalse() {
        Assert.assertEquals("No artifacts must be skiped", InclusionExclusionResolverTest.ALL, InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, false, null, null, null, null));
    }

    @Test
    public void testSkipDependenciesTrue() {
        Assert.assertEquals("All artifacts must be skipped", InclusionExclusionResolverTest.NONE, InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, true, null, null, null, null));
    }

    @Test
    public void testSkipDependenciesIncludeTypes() {
        Assert.assertEquals("All artifacts must be skipped, but AAR artifacts have higher priority", InclusionExclusionResolverTest.collect(InclusionExclusionResolverTest.A3, InclusionExclusionResolverTest.A5), InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, true, Collections.singleton("aar"), null, null, null));
        Assert.assertEquals("All artifacts must be skipped, but JAR artifacts have higher priority", InclusionExclusionResolverTest.collect(InclusionExclusionResolverTest.A1, InclusionExclusionResolverTest.A2, InclusionExclusionResolverTest.A4), InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, true, Collections.singleton("jar"), null, null, null));
        Assert.assertEquals("No artifacts must be skipped", InclusionExclusionResolverTest.ALL, InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, false, Collections.singleton("aar"), null, null, null));
        Assert.assertEquals("No artifacts must be skipped", InclusionExclusionResolverTest.ALL, InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, false, Collections.singleton("jar"), null, null, null));
    }

    @Test
    public void testSkipDependenciesExcludeTypes() {
        Assert.assertEquals("All artifacts must be skipped, especially AAR artifacts", InclusionExclusionResolverTest.NONE, InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, true, null, Collections.singleton("aar"), null, null));
        Assert.assertEquals("All artifacts must be skipped, especially JAR artifacts", InclusionExclusionResolverTest.NONE, InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, true, null, Collections.singleton("jar"), null, null));
        Assert.assertEquals("AAR artifacts must be skipped", InclusionExclusionResolverTest.collect(InclusionExclusionResolverTest.A1, InclusionExclusionResolverTest.A2, InclusionExclusionResolverTest.A4), InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, false, null, Collections.singleton("aar"), null, null));
        Assert.assertEquals("JAR artifacts must be skipped", InclusionExclusionResolverTest.collect(InclusionExclusionResolverTest.A3, InclusionExclusionResolverTest.A5), InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, false, null, Collections.singleton("jar"), null, null));
        Assert.assertEquals("All artifacts must be skipped, especially both JAR and AAR artifacts", InclusionExclusionResolverTest.NONE, InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, false, null, Arrays.asList("aar", "jar"), null, null));
    }

    @Test
    public void testMatchingArtifactTypesIncludeExcludePriority() {
        Assert.assertEquals("Include must have higher priority", InclusionExclusionResolverTest.ALL, InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, false, Collections.singleton("jar"), Collections.singleton("jar"), null, null));
        Assert.assertEquals("Include must have higher priority", InclusionExclusionResolverTest.collect(InclusionExclusionResolverTest.A1, InclusionExclusionResolverTest.A2, InclusionExclusionResolverTest.A4), InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, false, Collections.singleton("jar"), Arrays.asList("aar", "jar"), null, null));
        Assert.assertEquals("Include must have higher priority", InclusionExclusionResolverTest.collect(InclusionExclusionResolverTest.A1, InclusionExclusionResolverTest.A2, InclusionExclusionResolverTest.A4), InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, true, Collections.singleton("jar"), Collections.singleton("jar"), null, null));
        Assert.assertEquals("Include must have higher priority", InclusionExclusionResolverTest.collect(InclusionExclusionResolverTest.A1, InclusionExclusionResolverTest.A2, InclusionExclusionResolverTest.A4), InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, true, Collections.singleton("jar"), Arrays.asList("aar", "jar"), null, null));
    }

    @Test
    public void testIncludeExcludeByQualifiers() {
        Assert.assertEquals("Empty exclude must do nothing", InclusionExclusionResolverTest.ALL, InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, false, null, null, null, Collections.singleton("")));
        Assert.assertEquals("Empty include must do nothing", InclusionExclusionResolverTest.NONE, InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, true, null, null, Collections.singleton(""), null));
        Assert.assertEquals("Skip all and must include all of group G2", InclusionExclusionResolverTest.collect(InclusionExclusionResolverTest.A2, InclusionExclusionResolverTest.A5), InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, true, null, null, Collections.singleton("G2"), null));
        Assert.assertEquals("Skip all and must include all of group G2 and artifact A2", InclusionExclusionResolverTest.collect(InclusionExclusionResolverTest.A5), InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, true, null, null, Collections.singleton("G2:A2"), null));
        Assert.assertEquals("Do not skip and must exclude group G2", InclusionExclusionResolverTest.collect(InclusionExclusionResolverTest.A1, InclusionExclusionResolverTest.A3, InclusionExclusionResolverTest.A4), InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, false, null, null, null, Collections.singleton("G2")));
        Assert.assertEquals("Do not skip and must exclude group G2 and artifact A2", InclusionExclusionResolverTest.collect(InclusionExclusionResolverTest.A1, InclusionExclusionResolverTest.A2, InclusionExclusionResolverTest.A3, InclusionExclusionResolverTest.A4), InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, false, null, null, null, Collections.singleton("G2:A2")));
        Assert.assertEquals("Do not skip and must exclude group G2 and artifact A2 with invalid version", InclusionExclusionResolverTest.ALL, InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, false, null, null, null, Collections.singleton("G2:A2:-")));
        Assert.assertEquals("Do not skip and must exclude group G2 and artifact A2 with valid version", InclusionExclusionResolverTest.collect(InclusionExclusionResolverTest.A1, InclusionExclusionResolverTest.A2, InclusionExclusionResolverTest.A3, InclusionExclusionResolverTest.A4), InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, false, null, null, null, Collections.singleton("G2:A2:2.0-rc")));
    }

    @Test
    public void testIncludeExcludeTypeQualifierIntersections() {
        Assert.assertEquals("Exclude all JARs but include by artifact qualifiers", InclusionExclusionResolverTest.collect(InclusionExclusionResolverTest.A2, InclusionExclusionResolverTest.A3, InclusionExclusionResolverTest.A4, InclusionExclusionResolverTest.A5), InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, false, null, Collections.singleton("jar"), Arrays.asList("G2:A1", "G1:A3"), null));
        Assert.assertEquals("Exclude all JARs but include by artifact qualifiers", InclusionExclusionResolverTest.collect(InclusionExclusionResolverTest.A2, InclusionExclusionResolverTest.A3, InclusionExclusionResolverTest.A4, InclusionExclusionResolverTest.A5), InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, false, null, Collections.singleton("jar"), Arrays.asList("G2:A1", "G1:A3"), Arrays.asList("G2:A1", "G1:A3")));
        Assert.assertEquals("Skip all but must include all AAR files despite the concrete artifact exclusion", InclusionExclusionResolverTest.collect(InclusionExclusionResolverTest.A3, InclusionExclusionResolverTest.A5), InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, true, Collections.singleton("aar"), null, null, Collections.singleton("G2:A2:2.0-rc")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalQualifier() {
        InclusionExclusionResolver.filterArtifacts(InclusionExclusionResolverTest.ALL, false, null, null, Collections.singleton("G1:A1:V:X"), null);
    }
}

