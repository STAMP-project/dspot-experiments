package dev.morphia.mapping.validation.fieldrules;


import com.mongodb.DBObject;
import dev.morphia.TestBase;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.PreSave;
import dev.morphia.annotations.Property;
import dev.morphia.annotations.Transient;
import dev.morphia.mapping.validation.ConstraintViolationException;
import dev.morphia.testutil.TestEntity;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class PropertyAndEmbeddedTest extends TestBase {
    @Test(expected = ConstraintViolationException.class)
    public void testCheck() {
        final PropertyAndEmbeddedTest.E e = new PropertyAndEmbeddedTest.E();
        getDs().save(e);
        Assert.assertTrue(e.document.contains("myFunkyR"));
        getMorphia().map(PropertyAndEmbeddedTest.E2.class);
    }

    public static class E extends TestEntity {
        @Embedded("myFunkyR")
        private PropertyAndEmbeddedTest.R r = new PropertyAndEmbeddedTest.R();

        @Transient
        private String document;

        @PreSave
        public void preSave(final DBObject o) {
            document = o.toString();
        }
    }

    public static class E2 extends TestEntity {
        @Embedded
        @Property("myFunkyR")
        private String s;
    }

    public static class R {
        private String foo = "bar";
    }
}

