package com.baeldung.mockito.java8;


import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ArgumentMatcherWithoutLambdaUnitTest {
    private class PeterArgumentMatcher implements ArgumentMatcher<Person> {
        @Override
        public boolean matches(Person p) {
            return p.getName().equals("Peter");
        }
    }

    @InjectMocks
    private UnemploymentServiceImpl unemploymentService;

    @Mock
    private JobService jobService;

    @Test
    public void whenPersonWithJob_thenIsNotEntitled() {
        Person peter = new Person("Peter");
        Person linda = new Person("Linda");
        JobPosition teacher = new JobPosition("Teacher");
        Mockito.when(jobService.findCurrentJobPosition(ArgumentMatchers.argThat(new ArgumentMatcherWithoutLambdaUnitTest.PeterArgumentMatcher()))).thenReturn(Optional.of(teacher));
        Assert.assertTrue(unemploymentService.personIsEntitledToUnemploymentSupport(linda));
        Assert.assertFalse(unemploymentService.personIsEntitledToUnemploymentSupport(peter));
    }
}

