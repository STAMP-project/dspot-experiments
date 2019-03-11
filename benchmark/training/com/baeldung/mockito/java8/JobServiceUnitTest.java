package com.baeldung.mockito.java8;


import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class JobServiceUnitTest {
    @Mock
    private JobService jobService;

    @Test
    public void givenDefaultMethod_whenCallRealMethod_thenNoExceptionIsRaised() {
        Person person = new Person();
        Mockito.when(jobService.findCurrentJobPosition(person)).thenReturn(Optional.of(new JobPosition()));
        Mockito.doCallRealMethod().when(jobService).assignJobPosition(Mockito.any(Person.class), Mockito.any(JobPosition.class));
        Assert.assertFalse(jobService.assignJobPosition(person, new JobPosition()));
    }

    @Test
    public void givenReturnIsOfTypeOptional_whenDefaultValueIsReturned_thenValueIsEmpty() {
        Person person = new Person();
        Mockito.when(jobService.findCurrentJobPosition(person)).thenReturn(Optional.empty());
        Mockito.doCallRealMethod().when(jobService).assignJobPosition(Mockito.any(Person.class), Mockito.any(JobPosition.class));
        Assert.assertTrue(jobService.assignJobPosition(person, new JobPosition()));
    }
}

