package dev.morphia.mapping.validation.fieldrules;


import dev.morphia.TestBase;
import dev.morphia.annotations.Reference;
import dev.morphia.mapping.validation.ConstraintViolationException;
import dev.morphia.testutil.TestEntity;
import org.junit.Test;


/**
 *
 *
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class LazyReferenceOnArrayTest extends TestBase {
    @Test(expected = ConstraintViolationException.class)
    public void testLazyRefOnArray() {
        getMorphia().map(LazyReferenceOnArrayTest.LazyOnArray.class);
    }

    public static class R extends TestEntity {}

    public static class LazyOnArray extends TestEntity {
        @Reference(lazy = true)
        private LazyReferenceOnArrayTest.R[] r;
    }
}

