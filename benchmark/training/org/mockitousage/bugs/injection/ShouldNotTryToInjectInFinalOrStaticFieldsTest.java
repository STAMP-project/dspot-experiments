/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs.injection;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


// issue 262
@RunWith(MockitoJUnitRunner.class)
public class ShouldNotTryToInjectInFinalOrStaticFieldsTest {
    public static class ExampleService {
        public static final List<String> CONSTANTS = Arrays.asList("c1", "c1");

        public final Set<String> aSet = new HashSet<String>();
    }

    @Spy
    private List<String> unrelatedList = new ArrayList<String>();

    @Mock
    private Set<String> unrelatedSet;

    @InjectMocks
    private ShouldNotTryToInjectInFinalOrStaticFieldsTest.ExampleService exampleService = new ShouldNotTryToInjectInFinalOrStaticFieldsTest.ExampleService();

    @Test
    public void dont_inject_in_final() {
        Assert.assertNotSame(unrelatedSet, exampleService.aSet);
    }
}

