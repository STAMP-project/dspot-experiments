package com.baeldung.jdeffered;


import Result.SUCCESS;
import com.baeldung.jdeffered.PipeDemo.Result;
import org.junit.Assert;
import org.junit.Test;


public class JDeferredUnitTest {
    @Test
    public void givenJob_expectPromise() {
        PromiseDemo.startJob("Baeldung Job");
    }

    @Test
    public void givenMsg_expectModifiedMsg() {
        String msg = FilterDemo.filter("Baeldung");
        Assert.assertEquals("Hello Baeldung", msg);
    }

    @Test
    public void givenNum_validateNum_expectStatus() {
        Result result = PipeDemo.validate(80);
        Assert.assertEquals(result, SUCCESS);
    }
}

