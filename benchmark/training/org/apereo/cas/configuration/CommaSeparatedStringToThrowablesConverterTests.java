package org.apereo.cas.configuration;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link CommaSeparatedStringToThrowablesConverterTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class CommaSeparatedStringToThrowablesConverterTests {
    @Test
    public void verifyConverters() {
        val c = new CommaSeparatedStringToThrowablesConverter();
        val list = c.convert((((Exception.class.getName()) + ',') + (RuntimeException.class.getName())));
        Assertions.assertEquals(2, list.size());
    }

    @Test
    public void verifyConverter() {
        val c = new CommaSeparatedStringToThrowablesConverter();
        val list = c.convert(Exception.class.getName());
        Assertions.assertEquals(1, list.size());
    }
}

