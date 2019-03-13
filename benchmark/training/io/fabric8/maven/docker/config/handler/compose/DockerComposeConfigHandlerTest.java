package io.fabric8.maven.docker.config.handler.compose;


import io.fabric8.maven.docker.config.ImageConfiguration;
import io.fabric8.maven.docker.config.NetworkConfig;
import io.fabric8.maven.docker.config.handler.ExternalConfigHandlerException;
import java.io.IOException;
import java.util.List;
import mockit.Injectable;
import mockit.Mocked;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenReaderFilter;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author roland
 * @since 28.08.17
 */
public class DockerComposeConfigHandlerTest {
    @Injectable
    ImageConfiguration unresolved;

    @Mocked
    MavenProject project;

    @Mocked
    MavenSession session;

    @Mocked
    MavenReaderFilter readerFilter;

    private DockerComposeConfigHandler handler;

    @Test
    public void simple() throws IOException, MavenFilteringException {
        setupComposeExpectations("docker-compose.yml");
        List<ImageConfiguration> configs = handler.resolve(unresolved, project, session);
        Assert.assertEquals(1, configs.size());
        validateRunConfiguration(configs.get(0).getRunConfiguration());
    }

    @Test
    public void networkAliases() throws IOException, MavenFilteringException {
        setupComposeExpectations("docker-compose-network-aliases.yml");
        List<ImageConfiguration> configs = handler.resolve(unresolved, project, session);
        // Service 1 has 1 network (network1) with 2 aliases (alias1, alias2)
        NetworkConfig netSvc = configs.get(0).getRunConfiguration().getNetworkingConfig();
        Assert.assertEquals("network1", netSvc.getName());
        Assert.assertEquals(2, netSvc.getAliases().size());
        Assert.assertEquals("alias1", netSvc.getAliases().get(0));
        Assert.assertEquals("alias2", netSvc.getAliases().get(1));
        // Service 2 has 1 network (network1) with no aliases
        netSvc = configs.get(1).getRunConfiguration().getNetworkingConfig();
        Assert.assertEquals("network1", netSvc.getName());
        Assert.assertEquals(0, netSvc.getAliases().size());
        // Service 3 has 1 network (network1) with 1 aliase (alias1)
        netSvc = configs.get(2).getRunConfiguration().getNetworkingConfig();
        Assert.assertEquals("network1", netSvc.getName());
        Assert.assertEquals(1, netSvc.getAliases().size());
        Assert.assertEquals("alias1", netSvc.getAliases().get(0));
    }

    @Test
    public void positiveVersionTest() throws IOException, MavenFilteringException {
        for (String composeFile : new String[]{ "version/compose-version-2.yml", "version/compose-version-2x.yml" }) {
            setupComposeExpectations(composeFile);
            Assert.assertNotNull(handler.resolve(unresolved, project, session));
        }
    }

    @Test
    public void negativeVersionTest() throws IOException, MavenFilteringException {
        for (String composeFile : new String[]{ "version/compose-wrong-version.yml", "version/compose-no-version.yml" }) {
            try {
                setupComposeExpectations(composeFile);
                handler.resolve(unresolved, project, session);
                Assert.fail();
            } catch (ExternalConfigHandlerException exp) {
                Assert.assertTrue(exp.getMessage().contains("2.x"));
            }
        }
    }
}

