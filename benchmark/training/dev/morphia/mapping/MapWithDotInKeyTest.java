package dev.morphia.mapping;


import com.mongodb.BasicDBObject;
import dev.morphia.TestBase;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Id;
import java.io.Serializable;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author scott hernandez
 */
public class MapWithDotInKeyTest extends TestBase {
    @Test
    public void testMapping() throws Exception {
        MapWithDotInKeyTest.E e = new MapWithDotInKeyTest.E();
        put("a.b", "a");
        put("c.e.g", "b");
        try {
            getDs().save(e);
        } catch (Exception ex) {
            return;
        }
        Assert.assertFalse("Should have got rejection for dot in field names", true);
        e = getDs().get(e);
        Assert.assertEquals("a", get("a.b"));
        Assert.assertEquals("b", get("c.e.g"));
    }

    private static class Goo implements Serializable {
        @Id
        private ObjectId id = new ObjectId();

        private String name;

        Goo() {
        }

        Goo(final String n) {
            name = n;
        }
    }

    private static class E {
        @Embedded
        private final MapWithDotInKeyTest.MyMap mymap = new MapWithDotInKeyTest.MyMap();

        @Id
        private ObjectId id;
    }

    private static class MyMap extends BasicDBObject {}
}

