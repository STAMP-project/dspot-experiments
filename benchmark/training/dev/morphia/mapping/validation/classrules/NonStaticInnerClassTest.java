package dev.morphia.mapping.validation.classrules;


import dev.morphia.TestBase;
import dev.morphia.annotations.Id;
import dev.morphia.mapping.MappingException;
import org.bson.types.ObjectId;
import org.junit.Test;


/**
 *
 *
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class NonStaticInnerClassTest extends TestBase {
    @Test(expected = MappingException.class)
    public void testInValidInnerClass() throws Exception {
        getMorphia().map(NonStaticInnerClassTest.InValid.class);
    }

    @Test
    public void testValidInnerClass() throws Exception {
        getMorphia().map(NonStaticInnerClassTest.Valid.class);
    }

    static class Valid {
        @Id
        private ObjectId id;
    }

    class InValid {
        @Id
        private ObjectId id;
    }
}

