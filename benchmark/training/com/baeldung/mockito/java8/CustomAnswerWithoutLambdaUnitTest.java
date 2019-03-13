package com.baeldung.mockito.java8;


import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class CustomAnswerWithoutLambdaUnitTest {
    private class PersonAnswer implements Answer<Stream<JobPosition>> {
        @Override
        public Stream<JobPosition> answer(InvocationOnMock invocation) throws Throwable {
            Person person = invocation.getArgument(0);
            if (person.getName().equals("Peter")) {
                return Stream.<JobPosition>builder().add(new JobPosition("Teacher")).build();
            }
            return Stream.empty();
        }
    }

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

