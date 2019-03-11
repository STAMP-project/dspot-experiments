package com.baeldung.mockito.java8;


import java.util.Optional;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


public class UnemploymentServiceImplUnitTest {
    @Mock
    private JobService jobService;

    @InjectMocks
    private UnemploymentServiceImpl unemploymentService;

    @Test
    public void givenReturnIsOfTypeOptional_whenMocked_thenValueIsEmpty() {
        Person person = new Person();
        Mockito.when(jobService.findCurrentJobPosition(ArgumentMatchers.any(Person.class))).thenReturn(Optional.empty());
        Assert.assertTrue(unemploymentService.personIsEntitledToUnemploymentSupport(person));
    }

    @Test
    public void givenReturnIsOfTypeOptional_whenDefaultValueIsReturned_thenValueIsEmpty() {
        Person person = new Person();
        // This will fail when Mockito 1 is used
        Assert.assertTrue(unemploymentService.personIsEntitledToUnemploymentSupport(person));
    }

    @Test
    public void givenReturnIsOfTypeStream_whenMocked_thenValueIsEmpty() {
        Person person = new Person();
        Mockito.when(jobService.listJobs(ArgumentMatchers.any(Person.class))).thenReturn(Stream.empty());
        Assert.assertFalse(unemploymentService.searchJob(person, "").isPresent());
    }

    @Test
    public void givenReturnIsOfTypeStream_whenDefaultValueIsReturned_thenValueIsEmpty() {
        Person person = new Person();
        // This will fail when Mockito 1 is used
        Assert.assertFalse(unemploymentService.searchJob(person, "").isPresent());
    }
}

