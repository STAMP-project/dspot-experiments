package org.robolectric.internal;


import Config.Builder;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class MavenManifestFactoryTest {
    private Builder configBuilder;

    private MavenManifestFactoryTest.MyMavenManifestFactory myMavenManifestFactory;

    @Test
    public void identify() throws Exception {
        ManifestIdentifier manifestIdentifier = myMavenManifestFactory.identify(configBuilder.build());
        assertThat(manifestIdentifier.getManifestFile()).isEqualTo(Paths.get("_fakefs_path").resolve("to").resolve("DifferentManifest.xml"));
        assertThat(manifestIdentifier.getResDir()).isEqualTo(Paths.get("_fakefs_path/to/res"));
    }

    @Test
    public void withDotSlashManifest_identify() throws Exception {
        configBuilder.setManifest("./DifferentManifest.xml");
        ManifestIdentifier manifestIdentifier = myMavenManifestFactory.identify(configBuilder.build());
        assertThat(manifestIdentifier.getManifestFile().normalize()).isEqualTo(Paths.get("_fakefs_path/to/DifferentManifest.xml"));
        assertThat(manifestIdentifier.getResDir().normalize()).isEqualTo(Paths.get("_fakefs_path/to/res"));
    }

    @Test
    public void withDotDotSlashManifest_identify() throws Exception {
        configBuilder.setManifest("../DifferentManifest.xml");
        ManifestIdentifier manifestIdentifier = myMavenManifestFactory.identify(configBuilder.build());
        assertThat(manifestIdentifier.getManifestFile()).isEqualTo(Paths.get("_fakefs_path/to/../DifferentManifest.xml"));
        assertThat(manifestIdentifier.getResDir()).isEqualTo(Paths.get("_fakefs_path/to/../res"));
    }

    private static class MyMavenManifestFactory extends MavenManifestFactory {
        @Override
        Path getBaseDir() {
            return Paths.get("_fakefs_path").resolve("to");
        }
    }
}

