/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.exception;


import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Abstract test of an ExceptionContext implementation.
 */
public abstract class AbstractExceptionContextTest<T extends ExceptionContext & Serializable> {
    protected static final String TEST_MESSAGE_2 = "This is monotonous";

    protected static final String TEST_MESSAGE = "Test Message";

    protected T exceptionContext;

    protected static class ObjectWithFaultyToString {
        @Override
        public String toString() {
            throw new RuntimeException("Crap");
        }
    }

    @Test
    public void testAddContextValue() {
        final String message = exceptionContext.getFormattedExceptionMessage(AbstractExceptionContextTest.TEST_MESSAGE);
        Assertions.assertTrue(message.contains(AbstractExceptionContextTest.TEST_MESSAGE));
        Assertions.assertTrue(message.contains("test1"));
        Assertions.assertTrue(message.contains("test2"));
        Assertions.assertTrue(message.contains("test Date"));
        Assertions.assertTrue(message.contains("test Nbr"));
        Assertions.assertTrue(message.contains("some value"));
        Assertions.assertTrue(message.contains("5"));
        Assertions.assertNull(exceptionContext.getFirstContextValue("test1"));
        Assertions.assertEquals("some value", exceptionContext.getFirstContextValue("test2"));
        Assertions.assertEquals(5, exceptionContext.getContextLabels().size());
        Assertions.assertTrue(exceptionContext.getContextLabels().contains("test1"));
        Assertions.assertTrue(exceptionContext.getContextLabels().contains("test2"));
        Assertions.assertTrue(exceptionContext.getContextLabels().contains("test Date"));
        Assertions.assertTrue(exceptionContext.getContextLabels().contains("test Nbr"));
        exceptionContext.addContextValue("test2", "different value");
        Assertions.assertEquals(5, exceptionContext.getContextLabels().size());
        Assertions.assertTrue(exceptionContext.getContextLabels().contains("test2"));
        final String contextMessage = exceptionContext.getFormattedExceptionMessage(null);
        Assertions.assertFalse(contextMessage.contains(AbstractExceptionContextTest.TEST_MESSAGE));
    }

    @Test
    public void testSetContextValue() {
        exceptionContext.addContextValue("test2", "different value");
        exceptionContext.setContextValue("test3", "3");
        final String message = exceptionContext.getFormattedExceptionMessage(AbstractExceptionContextTest.TEST_MESSAGE);
        Assertions.assertTrue(message.contains(AbstractExceptionContextTest.TEST_MESSAGE));
        Assertions.assertTrue(message.contains("test Poorly written obj"));
        Assertions.assertTrue(message.contains("Crap"));
        Assertions.assertNull(exceptionContext.getFirstContextValue("crap"));
        Assertions.assertTrue(((exceptionContext.getFirstContextValue("test Poorly written obj")) instanceof AbstractExceptionContextTest.ObjectWithFaultyToString));
        Assertions.assertEquals(7, exceptionContext.getContextEntries().size());
        Assertions.assertEquals(6, exceptionContext.getContextLabels().size());
        Assertions.assertTrue(exceptionContext.getContextLabels().contains("test Poorly written obj"));
        Assertions.assertFalse(exceptionContext.getContextLabels().contains("crap"));
        exceptionContext.setContextValue("test Poorly written obj", "replacement");
        Assertions.assertEquals(7, exceptionContext.getContextEntries().size());
        Assertions.assertEquals(6, exceptionContext.getContextLabels().size());
        exceptionContext.setContextValue("test2", "another");
        Assertions.assertEquals(6, exceptionContext.getContextEntries().size());
        Assertions.assertEquals(6, exceptionContext.getContextLabels().size());
        final String contextMessage = exceptionContext.getFormattedExceptionMessage(null);
        Assertions.assertFalse(contextMessage.contains(AbstractExceptionContextTest.TEST_MESSAGE));
    }

    @Test
    public void testGetFirstContextValue() {
        exceptionContext.addContextValue("test2", "different value");
        Assertions.assertNull(exceptionContext.getFirstContextValue("test1"));
        Assertions.assertEquals("some value", exceptionContext.getFirstContextValue("test2"));
        Assertions.assertNull(exceptionContext.getFirstContextValue("crap"));
        exceptionContext.setContextValue("test2", "another");
        Assertions.assertEquals("another", exceptionContext.getFirstContextValue("test2"));
    }

    @Test
    public void testGetContextValues() {
        exceptionContext.addContextValue("test2", "different value");
        Assertions.assertEquals(exceptionContext.getContextValues("test1"), Collections.singletonList(null));
        Assertions.assertEquals(exceptionContext.getContextValues("test2"), Arrays.asList("some value", "different value"));
        exceptionContext.setContextValue("test2", "another");
        Assertions.assertEquals("another", exceptionContext.getFirstContextValue("test2"));
    }

    @Test
    public void testGetContextLabels() {
        Assertions.assertEquals(5, exceptionContext.getContextEntries().size());
        exceptionContext.addContextValue("test2", "different value");
        final Set<String> labels = exceptionContext.getContextLabels();
        Assertions.assertEquals(6, exceptionContext.getContextEntries().size());
        Assertions.assertEquals(5, labels.size());
        Assertions.assertTrue(labels.contains("test1"));
        Assertions.assertTrue(labels.contains("test2"));
        Assertions.assertTrue(labels.contains("test Date"));
        Assertions.assertTrue(labels.contains("test Nbr"));
    }

    @Test
    public void testGetContextEntries() {
        Assertions.assertEquals(5, exceptionContext.getContextEntries().size());
        exceptionContext.addContextValue("test2", "different value");
        final List<Pair<String, Object>> entries = exceptionContext.getContextEntries();
        Assertions.assertEquals(6, entries.size());
        Assertions.assertEquals("test1", entries.get(0).getKey());
        Assertions.assertEquals("test2", entries.get(1).getKey());
        Assertions.assertEquals("test Date", entries.get(2).getKey());
        Assertions.assertEquals("test Nbr", entries.get(3).getKey());
        Assertions.assertEquals("test Poorly written obj", entries.get(4).getKey());
        Assertions.assertEquals("test2", entries.get(5).getKey());
    }

    @Test
    public void testJavaSerialization() {
        exceptionContext.setContextValue("test Poorly written obj", "serializable replacement");
        final T clone = SerializationUtils.deserialize(SerializationUtils.serialize(exceptionContext));
        Assertions.assertEquals(exceptionContext.getFormattedExceptionMessage(null), clone.getFormattedExceptionMessage(null));
    }
}

