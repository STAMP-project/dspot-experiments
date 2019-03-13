package dev.morphia.mapping;


import dev.morphia.TestBase;
import dev.morphia.annotations.Id;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Query;
import dev.morphia.query.UpdateOperations;
import java.util.HashMap;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.Test;


public class UpdateRetainsClassInfoTest extends TestBase {
    @Test
    public void retainsClassName() {
        final UpdateRetainsClassInfoTest.X x = new UpdateRetainsClassInfoTest.X();
        final UpdateRetainsClassInfoTest.E1 e1 = new UpdateRetainsClassInfoTest.E1();
        e1.foo = "narf";
        x.map.put("k1", e1);
        final UpdateRetainsClassInfoTest.E2 e2 = new UpdateRetainsClassInfoTest.E2();
        e2.bar = "narf";
        x.map.put("k2", e2);
        getDs().save(x);
        final Query<UpdateRetainsClassInfoTest.X> query = getDs().find(UpdateRetainsClassInfoTest.X.class);
        final UpdateOperations<UpdateRetainsClassInfoTest.X> update = getDs().createUpdateOperations(UpdateRetainsClassInfoTest.X.class);
        update.set("map.k2", e2);
        getDs().update(query, update);
        // fails due to type now missing
        getDs().find(UpdateRetainsClassInfoTest.X.class).find(new FindOptions().limit(1)).next();
    }

    public abstract static class E {
        @Id
        private ObjectId id = new ObjectId();
    }

    public static class E1 extends UpdateRetainsClassInfoTest.E {
        private String foo;
    }

    public static class E2 extends UpdateRetainsClassInfoTest.E {
        private String bar;
    }

    public static class X {
        private final Map<String, UpdateRetainsClassInfoTest.E> map = new HashMap<String, UpdateRetainsClassInfoTest.E>();

        @Id
        private ObjectId id;
    }
}

