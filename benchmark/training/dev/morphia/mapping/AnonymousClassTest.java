package dev.morphia.mapping;


import dev.morphia.Key;
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
public class AnonymousClassTest extends TestBase {
    @Test
    public void testDelete() throws Exception {
        final AnonymousClassTest.E e = new AnonymousClassTest.E();
        e.id = new AnonymousClassTest.CId("test");
        final Key<AnonymousClassTest.E> key = getDs().save(e);
        getDs().delete(AnonymousClassTest.E.class, e.id);
    }

    @Test
    public void testMapping() throws Exception {
        AnonymousClassTest.E e = new AnonymousClassTest.E();
        e.id = new AnonymousClassTest.CId("test");
        getDs().save(e);
        e = getDs().get(e);
        Assert.assertEquals("test", e.id.name);
        Assert.assertNotNull(e.id.id);
    }

    @Test
    public void testOtherDelete() throws Exception {
        final AnonymousClassTest.E e = new AnonymousClassTest.E();
        e.id = new AnonymousClassTest.CId("test");
        getDs().save(e);
        getAds().delete(getDs().getCollection(AnonymousClassTest.E.class).getName(), AnonymousClassTest.E.class, e.id);
    }

    @Embedded
    private static class CId implements Serializable {
        private final ObjectId id = new ObjectId();

        private String name;

        CId() {
        }

        CId(final String n) {
            name = n;
        }

        @Override
        public int hashCode() {
            int result = id.hashCode();
            result = (31 * result) + (name.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof AnonymousClassTest.CId)) {
                return false;
            }
            final AnonymousClassTest.CId other = ((AnonymousClassTest.CId) (obj));
            return (other.id.equals(id)) && (other.name.equals(name));
        }
    }

    private static class E {
        @Id
        private AnonymousClassTest.CId id;

        private String e;
    }
}

