package dev.morphia.query;


import dev.morphia.Key;
import dev.morphia.TestBase;
import dev.morphia.TestMapping;
import org.junit.Test;


public class TestMandatoryId extends TestBase {
    @Test(expected = ValidationException.class)
    public final void testMissingIdNoImplicitMapCall() {
        final Key<TestMapping.MissingId> save = getDs().save(new TestMapping.MissingId());
        getDs().getByKey(TestMapping.MissingId.class, save);
    }
}

