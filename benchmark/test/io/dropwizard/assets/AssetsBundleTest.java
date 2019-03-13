package io.dropwizard.assets;


import io.dropwizard.jetty.setup.ServletEnvironment;
import io.dropwizard.servlets.assets.AssetServlet;
import io.dropwizard.setup.Environment;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class AssetsBundleTest {
    private final ServletEnvironment servletEnvironment = Mockito.mock(ServletEnvironment.class);

    private final Environment environment = Mockito.mock(Environment.class);

    private AssetServlet servlet = new AssetServlet("/", "/", null, null);

    private String servletPath = "";

    @Test
    public void hasADefaultPath() throws Exception {
        runBundle(new AssetsBundle());
        assertThat(servletPath).isEqualTo("/assets/*");
        assertThat(servlet.getIndexFile()).isEqualTo("index.htm");
        assertThat(servlet.getResourceURL()).isEqualTo(normalize("assets"));
        assertThat(servlet.getUriPath()).isEqualTo("/assets");
    }

    @Test
    public void canHaveCustomPaths() throws Exception {
        runBundle(new AssetsBundle("/json"));
        assertThat(servletPath).isEqualTo("/json/*");
        assertThat(servlet.getIndexFile()).isEqualTo("index.htm");
        assertThat(servlet.getResourceURL()).isEqualTo(normalize("json"));
        assertThat(servlet.getUriPath()).isEqualTo("/json");
    }

    @Test
    public void canHaveDifferentUriAndResourcePaths() throws Exception {
        runBundle(new AssetsBundle("/json", "/what"));
        assertThat(servletPath).isEqualTo("/what/*");
        assertThat(servlet.getIndexFile()).isEqualTo("index.htm");
        assertThat(servlet.getResourceURL()).isEqualTo(normalize("json"));
        assertThat(servlet.getUriPath()).isEqualTo("/what");
    }

    @Test
    public void canSupportDiffrentAssetsBundleName() throws Exception {
        runBundle(new AssetsBundle("/json", "/what/new", "index.txt", "customAsset1"), "customAsset1");
        assertThat(servletPath).isEqualTo("/what/new/*");
        assertThat(servlet.getIndexFile()).isEqualTo("index.txt");
        assertThat(servlet.getResourceURL()).isEqualTo(normalize("json"));
        assertThat(servlet.getUriPath()).isEqualTo("/what/new");
        runBundle(new AssetsBundle("/json", "/what/old", "index.txt", "customAsset2"), "customAsset2");
        assertThat(servletPath).isEqualTo("/what/old/*");
        assertThat(servlet.getIndexFile()).isEqualTo("index.txt");
        assertThat(servlet.getResourceURL()).isEqualTo(normalize("json"));
        assertThat(servlet.getUriPath()).isEqualTo("/what/old");
    }

    @Test
    public void canHaveDifferentUriAndResourcePathsAndIndexFilename() throws Exception {
        runBundle(new AssetsBundle("/json", "/what", "index.txt"));
        assertThat(servletPath).isEqualTo("/what/*");
        assertThat(servlet.getIndexFile()).isEqualTo("index.txt");
        assertThat(servlet.getResourceURL()).isEqualTo(normalize("json"));
        assertThat(servlet.getUriPath()).isEqualTo("/what");
    }
}

