package io.dropwizard.util;


import com.google.common.base.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static java.util.Optional.empty;
import static java.util.Optional.of;


public class OptionalsTest {
    @Test
    public void testFromGuavaOptional() throws Exception {
        Assertions.assertFalse(Optionals.fromGuavaOptional(Optional.absent()).isPresent());
        Assertions.assertTrue(Optionals.fromGuavaOptional(Optional.of("Foo")).isPresent());
        Assertions.assertEquals(of("Foo"), Optionals.fromGuavaOptional(Optional.of("Foo")));
    }

    @Test
    public void testToGuavaOptional() throws Exception {
        Assertions.assertFalse(Optionals.toGuavaOptional(empty()).isPresent());
        Assertions.assertTrue(Optionals.toGuavaOptional(of("Foo")).isPresent());
        Assertions.assertEquals(Optional.of("Foo"), Optionals.toGuavaOptional(of("Foo")));
    }
}

