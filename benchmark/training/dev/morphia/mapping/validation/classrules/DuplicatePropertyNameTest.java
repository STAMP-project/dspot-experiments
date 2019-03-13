package dev.morphia.mapping.validation.classrules;


import dev.morphia.TestBase;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import dev.morphia.mapping.validation.ConstraintViolationException;
import java.util.Map;
import org.junit.Test;


/**
 *
 *
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class DuplicatePropertyNameTest extends TestBase {
    @Test(expected = ConstraintViolationException.class)
    public void testDuplicatedPropertyNameDifferentType() throws Exception {
        getMorphia().map(DuplicatePropertyNameTest.DuplicatedPropertyName2.class);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testDuplicatedPropertyNameSameType() throws Exception {
        getMorphia().map(DuplicatePropertyNameTest.DuplicatedPropertyName.class);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testDuplicatedPropertyNameShadowedFields() throws Exception {
        getMorphia().map(DuplicatePropertyNameTest.Extends.class);
    }

    @Entity
    public static class DuplicatedPropertyName {
        @Id
        private String id;

        @Property("value")
        private String content1;

        @Property("value")
        private String content2;
    }

    @Entity
    public static class DuplicatedPropertyName2 {
        @Id
        private String id;

        @Embedded("value")
        private Map<String, Integer> content1;

        @Property("value")
        private String content2;
    }

    @Entity
    public static class Super {
        private String foo;
    }

    public static class Extends extends DuplicatePropertyNameTest.Super {
        private String foo;
    }
}

