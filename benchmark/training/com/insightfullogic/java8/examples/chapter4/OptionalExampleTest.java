package com.insightfullogic.java8.examples.chapter4;


import java.util.Optional;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;


public class OptionalExampleTest {
    @Test
    public void examples() {
        // BEGIN value_creation
        Optional<String> a = Optional.of("a");
        Assert.assertEquals("a", a.get());
        // END value_creation
        // BEGIN is_present
        Optional emptyOptional = Optional.empty();
        Optional alsoEmpty = Optional.ofNullable(null);
        Assert.assertFalse(emptyOptional.isPresent());
        // a is defined above
        Assert.assertTrue(a.isPresent());
        // END is_present
        // BEGIN orElse
        Assert.assertEquals("b", emptyOptional.orElse("b"));
        Assert.assertEquals("c", emptyOptional.orElseGet(() -> "c"));
        // END orElse
    }
}

