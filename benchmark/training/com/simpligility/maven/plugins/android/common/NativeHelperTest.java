package com.simpligility.maven.plugins.android.common;


import Const.ArtifactType;
import com.simpligility.maven.plugins.android.AndroidNdk;
import java.io.File;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 *
 *
 * @author Johan Lindquist
 */
public class NativeHelperTest {
    @Rule
    public TemporaryFolder apklibDir = new TemporaryFolder();

    private NativeHelper nativeHelper;

    @Test
    public void shouldNotIncludeLibsFolderAsNativeDependenciesSourceWhenNoNativeLibsInside() throws Exception {
        new File(apklibDir.getRoot(), "some-apklib/libs").mkdirs();
        new File(apklibDir.getRoot(), "some-apklib/libs/some.jar").createNewFile();
        Set<Artifact> nativeDependencies = nativeHelper.getNativeDependenciesArtifacts(null, apklibDir.getRoot(), true);
        Assert.assertTrue("Included JARs as native dependencies, but shouldn't", nativeDependencies.isEmpty());
    }

    @Test
    public void shouldIncludeLibsFolderAsNativeDependenciesSourceWhenNativeLibsInside() throws Exception {
        new File(apklibDir.getRoot(), "some-apklib/libs").mkdirs();
        new File(apklibDir.getRoot(), "some-apklib/libs/some.jar").createNewFile();
        new File(apklibDir.getRoot(), "some-apklib/libs/some.so").createNewFile();
        Set<Artifact> nativeDependencies = nativeHelper.getNativeDependenciesArtifacts(null, apklibDir.getRoot(), true);
        Assert.assertTrue("Included attached native artifacts, but shouldn't", nativeDependencies.isEmpty());
    }

    @Test
    public void architectureResolutionForPlainArchitectureClassifier() throws Exception {
        for (String ndkArchitecture : AndroidNdk.NDK_ARCHITECTURES) {
            Artifact artifact = new DefaultArtifact("acme", "acme", "1.0", "runtime", ArtifactType.NATIVE_SYMBOL_OBJECT, ndkArchitecture, null);
            String architecture = NativeHelper.extractArchitectureFromArtifact(artifact, "armeabi");
            Assert.assertNotNull("unexpected null architecture", architecture);
            Assert.assertEquals("unexpected architecture", ndkArchitecture, architecture);
        }
    }

    @Test
    public void architectureResolutionForMixedArchitectureClassifier() throws Exception {
        for (String ndkArchitecture : AndroidNdk.NDK_ARCHITECTURES) {
            Artifact artifact = new DefaultArtifact("acme", "acme", "1.0", "runtime", ArtifactType.NATIVE_SYMBOL_OBJECT, (ndkArchitecture + "-acme"), null);
            String architecture = NativeHelper.extractArchitectureFromArtifact(artifact, "armeabi");
            Assert.assertNotNull("unexpected null architecture", architecture);
            Assert.assertEquals("unexpected architecture", ndkArchitecture, architecture);
        }
    }

    @Test
    public void architectureResolutionForDefaultLegacyArchitectureClassifier() throws Exception {
        Artifact artifact = new DefaultArtifact("acme", "acme", "1.0", "runtime", ArtifactType.NATIVE_SYMBOL_OBJECT, "acme", null);
        String architecture = NativeHelper.extractArchitectureFromArtifact(artifact, "armeabi");
        Assert.assertNotNull("unexpected null architecture", architecture);
        Assert.assertEquals("unexpected architecture", "armeabi", architecture);
    }

    @Test
    public void artifactHasHardwareArchitecture() throws Exception {
        for (String ndkArchitecture : AndroidNdk.NDK_ARCHITECTURES) {
            Artifact artifact = new DefaultArtifact("acme", "acme", "1.0", "runtime", ArtifactType.NATIVE_SYMBOL_OBJECT, ndkArchitecture, null);
            boolean value = NativeHelper.artifactHasHardwareArchitecture(artifact, ndkArchitecture, "armeabi");
            Assert.assertTrue("unexpected value", value);
        }
    }

    @Test
    public void artifactHasHardwareArchitectureWithClassifier() throws Exception {
        for (String ndkArchitecture : AndroidNdk.NDK_ARCHITECTURES) {
            Artifact artifact = new DefaultArtifact("acme", "acme", "1.0", "runtime", ArtifactType.NATIVE_SYMBOL_OBJECT, (ndkArchitecture + "-acme"), null);
            boolean value = NativeHelper.artifactHasHardwareArchitecture(artifact, ndkArchitecture, "armeabi");
            Assert.assertTrue("unexpected value", value);
        }
    }

    @Test
    public void artifactHasHardwareArchitectureWithDefaultLegacyClassifier() throws Exception {
        Artifact artifact = new DefaultArtifact("acme", "acme", "1.0", "runtime", ArtifactType.NATIVE_SYMBOL_OBJECT, "acme", null);
        boolean value = NativeHelper.artifactHasHardwareArchitecture(artifact, "armeabi", "armeabi");
        Assert.assertTrue("unexpected value", value);
    }

    @Test
    public void artifactHasHardwareArchitectureNotNativeLibrary() throws Exception {
        Artifact artifact = new DefaultArtifact("acme", "acme", "1.0", "runtime", "jar", "armeabi", null);
        boolean value = NativeHelper.artifactHasHardwareArchitecture(artifact, "armeabi", "armeabi");
        Assert.assertFalse("unexpected value", value);
    }
}

