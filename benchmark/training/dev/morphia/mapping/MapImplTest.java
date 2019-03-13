package dev.morphia.mapping;


import com.mongodb.BasicDBObject;
import dev.morphia.TestBase;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Id;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scott hernandez
 */
public class MapImplTest extends TestBase {
    @Test
    public void testEmbeddedMap() throws Exception {
        getMorphia().map(MapImplTest.ContainsMapOfEmbeddedGoos.class).map(MapImplTest.ContainsMapOfEmbeddedInterfaces.class);
        final MapImplTest.Goo g1 = new MapImplTest.Goo("Scott");
        final MapImplTest.ContainsMapOfEmbeddedGoos cmoeg = new MapImplTest.ContainsMapOfEmbeddedGoos();
        cmoeg.values.put("first", g1);
        getDs().save(cmoeg);
        // check className in the map values.
        final BasicDBObject goo = ((BasicDBObject) (get("first")));
        Assert.assertFalse(goo.containsField(getMorphia().getMapper().getOptions().getDiscriminatorField()));
    }

    // @Ignore("waiting on issue 184")
    @Test
    public void testEmbeddedMapUpdateOperations() throws Exception {
        getMorphia().map(MapImplTest.ContainsMapOfEmbeddedGoos.class).map(MapImplTest.ContainsMapOfEmbeddedInterfaces.class);
        final MapImplTest.Goo g1 = new MapImplTest.Goo("Scott");
        final MapImplTest.Goo g2 = new MapImplTest.Goo("Ralph");
        final MapImplTest.ContainsMapOfEmbeddedGoos cmoeg = new MapImplTest.ContainsMapOfEmbeddedGoos();
        cmoeg.values.put("first", g1);
        getDs().save(cmoeg);
        getDs().update(cmoeg, getDs().createUpdateOperations(MapImplTest.ContainsMapOfEmbeddedGoos.class).set("values.second", g2));
        // check className in the map values.
        final BasicDBObject goo = ((BasicDBObject) (get("second")));
        Assert.assertFalse("className should not be here.", goo.containsField(getMorphia().getMapper().getOptions().getDiscriminatorField()));
    }

    @Test
    public void testEmbeddedMapUpdateOperationsOnInterfaceValue() throws Exception {
        getMorphia().map(MapImplTest.ContainsMapOfEmbeddedGoos.class).map(MapImplTest.ContainsMapOfEmbeddedInterfaces.class);
        final MapImplTest.Goo g1 = new MapImplTest.Goo("Scott");
        final MapImplTest.Goo g2 = new MapImplTest.Goo("Ralph");
        final MapImplTest.ContainsMapOfEmbeddedInterfaces cmoei = new MapImplTest.ContainsMapOfEmbeddedInterfaces();
        cmoei.values.put("first", g1);
        getDs().save(cmoei);
        getDs().update(cmoei, getDs().createUpdateOperations(MapImplTest.ContainsMapOfEmbeddedInterfaces.class).set("values.second", g2));
        // check className in the map values.
        final BasicDBObject goo = ((BasicDBObject) (get("second")));
        Assert.assertTrue("className should be here.", goo.containsField(getMorphia().getMapper().getOptions().getDiscriminatorField()));
    }

    @Test
    public void testEmbeddedMapWithValueInterface() throws Exception {
        getMorphia().map(MapImplTest.ContainsMapOfEmbeddedGoos.class).map(MapImplTest.ContainsMapOfEmbeddedInterfaces.class);
        final MapImplTest.Goo g1 = new MapImplTest.Goo("Scott");
        final MapImplTest.ContainsMapOfEmbeddedInterfaces cmoei = new MapImplTest.ContainsMapOfEmbeddedInterfaces();
        cmoei.values.put("first", g1);
        getDs().save(cmoei);
        // check className in the map values.
        final BasicDBObject goo = ((BasicDBObject) (get("first")));
        Assert.assertTrue(goo.containsField(getMorphia().getMapper().getOptions().getDiscriminatorField()));
    }

    @Test
    public void testMapping() throws Exception {
        MapImplTest.E e = new MapImplTest.E();
        e.mymap.put("1", "a");
        e.mymap.put("2", "b");
        getDs().save(e);
        e = getDs().get(e);
        Assert.assertEquals("a", e.mymap.get("1"));
        Assert.assertEquals("b", e.mymap.get("2"));
    }

    private static class ContainsMapOfEmbeddedInterfaces {
        @Embedded
        private final Map<String, Serializable> values = new HashMap<String, Serializable>();

        @Id
        private ObjectId id;
    }

    private static class ContainsMapOfEmbeddedGoos {
        private final Map<String, MapImplTest.Goo> values = new HashMap<String, MapImplTest.Goo>();

        @Id
        private ObjectId id;
    }

    @Embedded
    private static class Goo implements Serializable {
        private String name;

        Goo() {
        }

        Goo(final String n) {
            name = n;
        }
    }

    private static class E {
        @Embedded
        private final MapImplTest.MyMap mymap = new MapImplTest.MyMap();

        @Id
        private ObjectId id;
    }

    private static class MyMap extends HashMap<String, String> {}
}

