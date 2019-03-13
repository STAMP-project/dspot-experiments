package io.fabric8.maven.docker.service;


import com.google.gson.JsonObject;
import io.fabric8.maven.docker.access.DockerAccess;
import io.fabric8.maven.docker.access.VolumeCreateConfig;
import io.fabric8.maven.docker.config.VolumeConfiguration;
import io.fabric8.maven.docker.util.JsonFactory;
import mockit.Expectations;
import mockit.Mocked;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Basic Unit Tests for {@link VolumeService}
 *
 * @author Tom Burton
 * @version Dec 16, 2016
 */
public class VolumeServiceTest {
    private VolumeCreateConfig volumeConfig;

    @Mocked
    private DockerAccess docker;

    @Test
    public void testCreateVolumeConfig() throws Exception {
        final VolumeConfiguration config = new VolumeConfiguration.Builder().name("testVolume").driver("test").opts(withMap("opts")).labels(withMap("labels")).build();
        new Expectations() {
            {
                // Use a 'delegate' to verify the argument given directly. No need
                // for an 'intermediate' return method in the service just to check this.
                docker.createVolume(with(new mockit.Delegate<VolumeCreateConfig>() {
                    void check(VolumeCreateConfig vcc) {
                        Assert.assertThat(vcc.getName(), Matchers.is("testVolume"));
                        JsonObject vccJson = JsonFactory.newJsonObject(vcc.toJson());
                        Assert.assertEquals("test", vccJson.get("Driver").getAsString());
                    }
                }));
                result = "testVolume";
            }
        };
        String volume = new VolumeService(docker).createVolume(config);
        Assert.assertEquals(volume, "testVolume");
    }

    @Test
    public void testCreateVolume() throws Exception {
        VolumeConfiguration vc = new VolumeConfiguration.Builder().name("testVolume").driver("test").opts(withMap("opts")).labels(withMap("labels")).build();
        new Expectations() {
            {
                docker.createVolume(((VolumeCreateConfig) (any)));
                result = "testVolume";
            }
        };
        Assert.assertThat(vc.getName(), Matchers.is("testVolume"));
        String name = new VolumeService(docker).createVolume(vc);
        Assert.assertThat(name, Matchers.is("testVolume"));
    }

    @Test
    public void testRemoveVolume() throws Exception {
        VolumeConfiguration vc = new VolumeConfiguration.Builder().name("testVolume").driver("test").opts(withMap("opts")).labels(withMap("labels")).build();
        new Expectations() {
            {
                docker.createVolume(((VolumeCreateConfig) (any)));
                result = "testVolume";
                docker.removeVolume("testVolume");
            }
        };
        VolumeService volumeService = new VolumeService(docker);
        String name = volumeService.createVolume(vc);
        volumeService.removeVolume(name);
    }
}

