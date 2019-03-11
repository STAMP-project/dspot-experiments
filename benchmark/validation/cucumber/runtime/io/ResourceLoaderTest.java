package cucumber.runtime.io;


import io.cucumber.core.model.FeaturePath;
import java.net.URI;
import org.junit.Assert;
import org.junit.Test;


public class ResourceLoaderTest {
    @Test
    public void loads_resources_from_filesystem_dir() {
        URI uri = FeaturePath.parse("src/test/resources/cucumber/runtime");
        Iterable<Resource> files = new FileResourceLoader().resources(uri, ".properties");
        Assert.assertEquals(3, toList(files).size());
    }

    @Test
    public void loads_resource_from_filesystem_file() {
        URI uri = FeaturePath.parse("src/test/resources/cucumber/runtime/bar.properties");
        Iterable<Resource> files = new FileResourceLoader().resources(uri, ".doesntmatter");
        Assert.assertEquals(1, toList(files).size());
    }

    @Test
    public void loads_resources_from_jar_on_classpath() {
        URI uri = FeaturePath.parse("classpath:cucumber");
        Iterable<Resource> files = new ClasspathResourceLoader(Thread.currentThread().getContextClassLoader()).resources(uri, ".properties");
        Assert.assertEquals(4, toList(files).size());
    }
}

