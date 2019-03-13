package dev.morphia.ext.entityscanner;


import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import dev.morphia.Morphia;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author us@thomas-daily.de
 */
@SuppressWarnings("deprecation")
public class EntityScannerTest {
    @Test
    public void testScanning() throws Exception {
        final Morphia m = new Morphia();
        Assert.assertFalse(m.isMapped(EntityScannerTest.E.class));
        new EntityScanner(m, Predicates.equalTo(((EntityScannerTest.E.class.getName()) + ".class")));
        Assert.assertTrue(m.isMapped(EntityScannerTest.E.class));
        Assert.assertFalse(m.isMapped(EntityScannerTest.F.class));
        new EntityScanner(m, new Predicate<String>() {
            @Override
            public boolean apply(final String input) {
                return input.startsWith(EntityScannerTest.class.getPackage().getName());
            }
        });
        Assert.assertTrue(m.isMapped(EntityScannerTest.F.class));
    }

    @Entity
    private static class E {
        @Id
        private ObjectId id;
    }

    @Entity
    private static class F {
        @Id
        private ObjectId id;
    }
}

