package com.baeldung.mockito.java8;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;


public class CustomAnswerWithLambdaUnitTest {
    @InjectMocks
    private UnemploymentServiceImpl unemploymentService;

    @Mock
    private JobService jobService;

    @Test
    public void whenPersonWithJobHistory_thenSearchReturnsValue() {
        Person peter = new Person("Peter");
        Assert.assertEquals("Teacher", unemploymentService.searchJob(peter, "").get().getTitle());
    }

    @Test
    public void whenPersonWithNoJobHistory_thenSearchReturnsEmpty() {
        Person linda = new Person("Linda");
        Assert.assertFalse(unemploymentService.searchJob(linda, "").isPresent());
    }
}

