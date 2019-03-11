package cucumber.runtime.io;


import java.io.IOException;
import java.net.URI;
import org.junit.Assert;
import org.junit.Test;


// https://github.com/cucumber/cucumber-jvm/issues/808
public class ZipResourceIteratorFactoryTest {
    @Test
    public void is_factory_for_jar_protocols() throws IOException {
        ZipResourceIteratorFactory factory = new ZipResourceIteratorFactory();
        Assert.assertTrue(factory.isFactoryFor(URI.create("jar:file:cucumber-core.jar!/cucumber/runtime/io")));
        Assert.assertTrue(factory.isFactoryFor(URI.create("zip:file:cucumber-core.jar!/cucumber/runtime/io")));
        Assert.assertTrue(factory.isFactoryFor(URI.create("wsjar:file:cucumber-core.jar!/cucumber/runtime/io")));
        Assert.assertFalse(factory.isFactoryFor(URI.create("file:cucumber-core")));
        Assert.assertFalse(factory.isFactoryFor(URI.create("http://http://cukes.info/cucumber-core.jar")));
    }
}

