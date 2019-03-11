package com.baeldung.java8.optional;


import com.baeldung.optional.OrElseAndOrElseGet;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OrElseAndOrElseGetUnitTest {
    private OrElseAndOrElseGet orElsevsOrElseGet = new OrElseAndOrElseGet();

    private static final Logger LOG = LoggerFactory.getLogger(OrElseAndOrElseGetUnitTest.class);

    @Test
    public void givenNonEmptyOptional_whenOrElseUsed_thenGivenStringReturned() {
        OrElseAndOrElseGetUnitTest.LOG.info("In givenNonEmptyOptional_whenOrElseUsed_thenGivenStringReturned()");
        String name = orElsevsOrElseGet.getNameUsingOrElse("baeldung");
        Assert.assertEquals(name, "baeldung");
    }

    @Test
    public void givenEmptyOptional_whenOrElseUsed_thenRandomStringReturned() {
        OrElseAndOrElseGetUnitTest.LOG.info("In givenEmptyOptional_whenOrElseUsed_thenRandomStringReturned()");
        String name = orElsevsOrElseGet.getNameUsingOrElse(null);
        Assert.assertTrue(orElsevsOrElseGet.names.contains(name));
    }

    @Test
    public void givenNonEmptyOptional_whenOrElseGetUsed_thenGivenStringReturned() {
        OrElseAndOrElseGetUnitTest.LOG.info("In givenNonEmptyOptional_whenOrElseGetUsed_thenGivenStringReturned()");
        String name = orElsevsOrElseGet.getNameUsingOrElseGet("baeldung");
        Assert.assertEquals(name, "baeldung");
    }

    @Test
    public void givenEmptyOptional_whenOrElseGetUsed_thenRandomStringReturned() {
        OrElseAndOrElseGetUnitTest.LOG.info("In givenEmptyOptional_whenOrElseGetUsed_thenRandomStringReturned()");
        String name = orElsevsOrElseGet.getNameUsingOrElseGet(null);
        Assert.assertTrue(orElsevsOrElseGet.names.contains(name));
    }
}

