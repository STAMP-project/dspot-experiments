package dev.morphia.issue50;


import dev.morphia.TestBase;
import dev.morphia.annotations.Id;
import dev.morphia.mapping.validation.ConstraintViolationException;
import dev.morphia.testutil.TestEntity;
import org.junit.Test;


public class TestIdTwice extends TestBase {
    @Test(expected = ConstraintViolationException.class)
    public final void shouldThrowExceptionIfThereIsMoreThanOneId() {
        getMorphia().map(TestIdTwice.A.class);
    }

    public static class A extends TestEntity {
        @Id
        private String extraId;

        @Id
        private String broken;
    }
}

