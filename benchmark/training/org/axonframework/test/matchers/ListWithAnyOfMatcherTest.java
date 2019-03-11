/**
 * Copyright (c) 2010-2012. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.test.matchers;


import java.util.Arrays;
import java.util.List;
import org.axonframework.eventhandling.EventMessage;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 *
 *
 * @author Allard Buijze
 */
public class ListWithAnyOfMatcherTest {
    private Matcher<EventMessage<?>> mockMatcher1;

    private Matcher<EventMessage<?>> mockMatcher2;

    private Matcher<EventMessage<?>> mockMatcher3;

    private Matcher<List<EventMessage<?>>> testSubject;

    private StubEvent stubEvent1;

    private StubEvent stubEvent2;

    @Test
    public void testMatch_FullMatch() {
        Assert.assertTrue(testSubject.matches(Arrays.asList(stubEvent1, stubEvent2)));
        Mockito.verify(mockMatcher1).matches(stubEvent1);
        Mockito.verify(mockMatcher1).matches(stubEvent2);
        Mockito.verify(mockMatcher2).matches(stubEvent1);
        Mockito.verify(mockMatcher2).matches(stubEvent2);
        Mockito.verify(mockMatcher3).matches(stubEvent1);
        Mockito.verify(mockMatcher3).matches(stubEvent2);
    }

    @Test
    public void testMatch_OnlyOneEventMatches() {
        Mockito.when(mockMatcher1.matches(stubEvent1)).thenReturn(false);
        Mockito.when(mockMatcher2.matches(stubEvent1)).thenReturn(false);
        Mockito.when(mockMatcher3.matches(stubEvent1)).thenReturn(false);
        Assert.assertTrue(testSubject.matches(Arrays.asList(stubEvent1, stubEvent2)));
        Mockito.verify(mockMatcher1).matches(stubEvent1);
        Mockito.verify(mockMatcher1).matches(stubEvent2);
        Mockito.verify(mockMatcher2).matches(stubEvent1);
        Mockito.verify(mockMatcher2).matches(stubEvent2);
        Mockito.verify(mockMatcher3).matches(stubEvent1);
        Mockito.verify(mockMatcher3).matches(stubEvent2);
    }

    @Test
    public void testMatch_NoMatches() {
        Mockito.when(mockMatcher1.matches(ArgumentMatchers.any())).thenReturn(false);
        Mockito.when(mockMatcher2.matches(ArgumentMatchers.any())).thenReturn(false);
        Mockito.when(mockMatcher3.matches(ArgumentMatchers.any())).thenReturn(false);
        Assert.assertFalse(testSubject.matches(Arrays.asList(stubEvent1, stubEvent2)));
        Mockito.verify(mockMatcher1).matches(stubEvent1);
        Mockito.verify(mockMatcher1).matches(stubEvent2);
        Mockito.verify(mockMatcher2).matches(stubEvent1);
        Mockito.verify(mockMatcher2).matches(stubEvent2);
        Mockito.verify(mockMatcher3).matches(stubEvent1);
        Mockito.verify(mockMatcher3).matches(stubEvent2);
    }

    @Test
    public void testMatch_OneMatcherDoesNotMatch() {
        Mockito.when(mockMatcher1.matches(ArgumentMatchers.any())).thenReturn(false);
        Mockito.when(mockMatcher2.matches(stubEvent1)).thenReturn(false);
        Mockito.when(mockMatcher3.matches(stubEvent1)).thenReturn(false);
        Assert.assertTrue(testSubject.matches(Arrays.asList(stubEvent1, stubEvent2)));
        Mockito.verify(mockMatcher1).matches(stubEvent1);
        Mockito.verify(mockMatcher1).matches(stubEvent2);
        Mockito.verify(mockMatcher2).matches(stubEvent1);
        Mockito.verify(mockMatcher2).matches(stubEvent2);
        Mockito.verify(mockMatcher3).matches(stubEvent1);
        Mockito.verify(mockMatcher3).matches(stubEvent2);
    }

    @Test
    public void testDescribe() {
        testSubject.matches(Arrays.asList(stubEvent1, stubEvent2));
        Mockito.doAnswer(new ListWithAnyOfMatcherTest.DescribingAnswer("A")).when(mockMatcher1).describeTo(ArgumentMatchers.isA(Description.class));
        Mockito.doAnswer(new ListWithAnyOfMatcherTest.DescribingAnswer("B")).when(mockMatcher2).describeTo(ArgumentMatchers.isA(Description.class));
        Mockito.doAnswer(new ListWithAnyOfMatcherTest.DescribingAnswer("C")).when(mockMatcher3).describeTo(ArgumentMatchers.isA(Description.class));
        StringDescription description = new StringDescription();
        testSubject.describeTo(description);
        String actual = description.toString();
        Assert.assertEquals("list with any of: <A>, <B> or <C>", actual);
    }

    @Test
    public void testDescribe_OneMatcherFailed() {
        Mockito.when(mockMatcher1.matches(ArgumentMatchers.any())).thenReturn(false);
        Mockito.when(mockMatcher2.matches(ArgumentMatchers.any())).thenReturn(false);
        Mockito.when(mockMatcher3.matches(ArgumentMatchers.any())).thenReturn(false);
        testSubject.matches(Arrays.asList(stubEvent1, stubEvent2));
        Mockito.doAnswer(new ListWithAnyOfMatcherTest.DescribingAnswer("A")).when(mockMatcher1).describeTo(ArgumentMatchers.isA(Description.class));
        Mockito.doAnswer(new ListWithAnyOfMatcherTest.DescribingAnswer("B")).when(mockMatcher2).describeTo(ArgumentMatchers.isA(Description.class));
        Mockito.doAnswer(new ListWithAnyOfMatcherTest.DescribingAnswer("C")).when(mockMatcher3).describeTo(ArgumentMatchers.isA(Description.class));
        StringDescription description = new StringDescription();
        testSubject.describeTo(description);
        String actual = description.toString();
        Assert.assertEquals("list with any of: <A> (NO MATCH), <B> (NO MATCH) or <C> (NO MATCH)", actual);
    }

    private static class DescribingAnswer implements Answer<Object> {
        private String description;

        public DescribingAnswer(String description) {
            this.description = description;
        }

        @Override
        public Object answer(InvocationOnMock invocation) {
            Description descriptionParameter = ((Description) (invocation.getArguments()[0]));
            descriptionParameter.appendText(this.description);
            return Void.class;
        }
    }
}

