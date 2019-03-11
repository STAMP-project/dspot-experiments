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


import org.axonframework.test.aggregate.MyEvent;
import org.axonframework.test.aggregate.MyOtherEvent;
import org.hamcrest.StringDescription;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 */
public class EqualFieldsMatcherTest {
    private EqualFieldsMatcher<MyEvent> testSubject;

    private MyEvent expectedEvent;

    private String aggregateId = "AggregateId";

    @Test
    public void testMatches_SameInstance() {
        Assert.assertTrue(testSubject.matches(expectedEvent));
    }

    @Test
    public void testMatches_EqualInstance() {
        Assert.assertTrue(testSubject.matches(new MyEvent(aggregateId, 1)));
    }

    @Test
    public void testMatches_WrongEventType() {
        Assert.assertFalse(testSubject.matches(new MyOtherEvent()));
    }

    @Test
    public void testMatches_WrongFieldValue() {
        Assert.assertFalse(testSubject.matches(new MyEvent(aggregateId, 2)));
        Assert.assertEquals("someValue", testSubject.getFailedField().getName());
    }

    @Test
    public void testMatches_WrongFieldValueInIgnoredField() {
        testSubject = Matchers.equalTo(expectedEvent, ( field) -> !(field.getName().equals("someValue")));
        Assert.assertTrue(testSubject.matches(new MyEvent(aggregateId, 2)));
    }

    @Test
    public void testMatches_WrongFieldValueInArray() {
        Assert.assertFalse(testSubject.matches(new MyEvent(aggregateId, 1, new byte[]{ 1, 2 })));
        Assert.assertEquals("someBytes", testSubject.getFailedField().getName());
    }

    @Test
    public void testDescription_AfterSuccess() {
        testSubject.matches(expectedEvent);
        StringDescription description = new StringDescription();
        testSubject.describeTo(description);
        Assert.assertEquals("org.axonframework.test.aggregate.MyEvent", description.toString());
    }

    @Test
    public void testDescription_AfterMatchWithWrongType() {
        testSubject.matches(new MyOtherEvent());
        StringDescription description = new StringDescription();
        testSubject.describeTo(description);
        Assert.assertEquals("org.axonframework.test.aggregate.MyEvent", description.toString());
    }

    @Test
    public void testDescription_AfterMatchWithWrongFieldValue() {
        testSubject.matches(new MyEvent(aggregateId, 2));
        StringDescription description = new StringDescription();
        testSubject.describeTo(description);
        Assert.assertEquals("org.axonframework.test.aggregate.MyEvent (failed on field 'someValue')", description.toString());
    }
}

