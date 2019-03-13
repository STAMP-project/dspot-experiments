package dev.morphia.mapping.validation.fieldrules;


import com.mongodb.DBObject;
import dev.morphia.TestBase;
import dev.morphia.annotations.PreSave;
import dev.morphia.annotations.Serialized;
import dev.morphia.annotations.Transient;
import dev.morphia.testutil.TestEntity;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class SerializedNameTest extends TestBase {
    @Test
    public void testCheck() {
        final SerializedNameTest.E e = new SerializedNameTest.E();
        getDs().save(e);
        Assert.assertTrue(e.document.contains("changedName"));
    }

    public static class E extends TestEntity {
        @Serialized("changedName")
        private final byte[] b = "foo".getBytes();

        @Transient
        private String document;

        @PreSave
        public void preSave(final DBObject o) {
            document = o.toString();
        }
    }
}

