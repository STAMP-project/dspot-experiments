package cucumber.runtime.io;


import java.net.MalformedURLException;
import java.net.URI;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


public class DelegatingResourceIteratorFactoryTest {
    @Test
    public void should_load_test_resource_iterator() throws MalformedURLException {
        ResourceIteratorFactory factory = new DelegatingResourceIteratorFactory(new ZipThenFileResourceIteratorFactory());
        URI url = URI.create(TestResourceIteratorFactory.TEST_URL);
        Assert.assertTrue(factory.isFactoryFor(url));
        Iterator<Resource> iterator = factory.createIterator(url, "test", "test");
        Assert.assertTrue((iterator instanceof TestResourceIterator));
    }
}

