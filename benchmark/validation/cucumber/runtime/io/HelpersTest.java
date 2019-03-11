package cucumber.runtime.io;


import java.net.URI;
import org.junit.Assert;
import org.junit.Test;


public class HelpersTest {
    @Test
    public void computes_file_path_for_jar_protocols() throws Exception {
        Assert.assertEquals("foo bar+zap/cucumber-core.jar", Helpers.jarFilePath(URI.create("jar:file:foo%20bar+zap/cucumber-core.jar!/cucumber/runtime/io")).getSchemeSpecificPart());
        Assert.assertEquals("foo bar+zap/cucumber-core.jar", Helpers.jarFilePath(URI.create("zip:file:foo%20bar+zap/cucumber-core.jar!/cucumber/runtime/io")).getSchemeSpecificPart());
        Assert.assertEquals("foo bar+zap/cucumber-core.jar", Helpers.jarFilePath(URI.create("wsjar:file:foo%20bar+zap/cucumber-core.jar!/cucumber/runtime/io")).getSchemeSpecificPart());
        Assert.assertEquals("foo bar+zap/cucumber-core.jar", Helpers.jarFilePath(URI.create("jar:file:foo%20bar+zap/cucumber-core.jar!/")).getSchemeSpecificPart());
        Assert.assertEquals("foo bar+zap/cucumber-core.jar", Helpers.jarFilePath(URI.create("zip:file:foo%20bar+zap/cucumber-core.jar!/")).getSchemeSpecificPart());
        Assert.assertEquals("foo bar+zap/cucumber-core.jar", Helpers.jarFilePath(URI.create("wsjar:file:foo%20bar+zap/cucumber-core.jar!/")).getSchemeSpecificPart());
    }
}

