package dev.morphia.mapping;


import dev.morphia.TestBase;
import dev.morphia.annotations.Id;
import dev.morphia.query.FindOptions;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author ScottHernandez
 */
public class URIMappingTest extends TestBase {
    @Test
    public void testURIField() throws Exception {
        final URIMappingTest.ContainsURI entity = new URIMappingTest.ContainsURI();
        final URI testURI = new URI("http://lamest.local/test.html");
        entity.uri = testURI;
        getDs().save(entity);
        final URIMappingTest.ContainsURI loaded = getDs().find(URIMappingTest.ContainsURI.class).find(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(loaded.uri);
        Assert.assertEquals(testURI, loaded.uri);
    }

    @Test
    public void testURIMap() throws Exception {
        final URIMappingTest.ContainsURIKeyedMap entity = new URIMappingTest.ContainsURIKeyedMap();
        final URI testURI = new URI("http://lamest.local/test.html");
        entity.uris.put(testURI, "first");
        getDs().save(entity);
        final URIMappingTest.ContainsURIKeyedMap loaded = getDs().find(URIMappingTest.ContainsURIKeyedMap.class).find(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(loaded.uris);
        Assert.assertEquals(1, loaded.uris.size());
        Assert.assertEquals(testURI, loaded.uris.keySet().iterator().next());
    }

    private static class ContainsURI {
        @Id
        private ObjectId id;

        private URI uri;
    }

    private static class ContainsURIKeyedMap {
        private final Map<URI, String> uris = new HashMap<URI, String>();

        @Id
        private ObjectId id;
    }
}

