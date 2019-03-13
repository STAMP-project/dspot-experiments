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
package org.apache.commons.lang3.builder;


import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests {@link DiffResult}.
 */
public class DiffResultTest {
    private static final DiffResultTest.SimpleClass SIMPLE_FALSE = new DiffResultTest.SimpleClass(false);

    private static final DiffResultTest.SimpleClass SIMPLE_TRUE = new DiffResultTest.SimpleClass(true);

    private static final ToStringStyle SHORT_STYLE = ToStringStyle.SHORT_PREFIX_STYLE;

    private static class SimpleClass implements Diffable<DiffResultTest.SimpleClass> {
        private final boolean booleanField;

        SimpleClass(final boolean booleanField) {
            this.booleanField = booleanField;
        }

        static String getFieldName() {
            return "booleanField";
        }

        @Override
        public DiffResult diff(final DiffResultTest.SimpleClass obj) {
            return new DiffBuilder(this, obj, ToStringStyle.SHORT_PREFIX_STYLE).append(DiffResultTest.SimpleClass.getFieldName(), booleanField, obj.booleanField).build();
        }
    }

    private static class EmptyClass {}

    @Test
    public void testListIsNonModifiable() {
        final DiffResultTest.SimpleClass lhs = new DiffResultTest.SimpleClass(true);
        final DiffResultTest.SimpleClass rhs = new DiffResultTest.SimpleClass(false);
        final List<Diff<?>> diffs = lhs.diff(rhs).getDiffs();
        final DiffResult list = new DiffResult(lhs, rhs, diffs, DiffResultTest.SHORT_STYLE);
        Assertions.assertEquals(diffs, list.getDiffs());
        Assertions.assertEquals(1, list.getNumberOfDiffs());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> list.getDiffs().remove(0));
    }

    @Test
    public void testIterator() {
        final DiffResultTest.SimpleClass lhs = new DiffResultTest.SimpleClass(true);
        final DiffResultTest.SimpleClass rhs = new DiffResultTest.SimpleClass(false);
        final List<Diff<?>> diffs = lhs.diff(rhs).getDiffs();
        final Iterator<Diff<?>> expectedIterator = diffs.iterator();
        final DiffResult list = new DiffResult(lhs, rhs, diffs, DiffResultTest.SHORT_STYLE);
        final Iterator<Diff<?>> iterator = list.iterator();
        while (iterator.hasNext()) {
            Assertions.assertTrue(expectedIterator.hasNext());
            Assertions.assertEquals(expectedIterator.next(), iterator.next());
        } 
    }

    @Test
    public void testToStringOutput() {
        final DiffResult list = new DiffBuilder(new DiffResultTest.EmptyClass(), new DiffResultTest.EmptyClass(), ToStringStyle.SHORT_PREFIX_STYLE).append("test", false, true).build();
        Assertions.assertEquals("DiffResultTest.EmptyClass[test=false] differs from DiffResultTest.EmptyClass[test=true]", list.toString());
    }

    @Test
    public void testToStringSpecifyStyleOutput() {
        final DiffResult list = DiffResultTest.SIMPLE_FALSE.diff(DiffResultTest.SIMPLE_TRUE);
        Assertions.assertEquals(list.getToStringStyle(), DiffResultTest.SHORT_STYLE);
        final String lhsString = new ToStringBuilder(DiffResultTest.SIMPLE_FALSE, ToStringStyle.MULTI_LINE_STYLE).append(DiffResultTest.SimpleClass.getFieldName(), DiffResultTest.SIMPLE_FALSE.booleanField).build();
        final String rhsString = new ToStringBuilder(DiffResultTest.SIMPLE_TRUE, ToStringStyle.MULTI_LINE_STYLE).append(DiffResultTest.SimpleClass.getFieldName(), DiffResultTest.SIMPLE_TRUE.booleanField).build();
        final String expectedOutput = String.format("%s differs from %s", lhsString, rhsString);
        Assertions.assertEquals(expectedOutput, list.toString(ToStringStyle.MULTI_LINE_STYLE));
    }

    @Test
    public void testNullLhs() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DiffResult(null, DiffResultTest.SIMPLE_FALSE, DiffResultTest.SIMPLE_TRUE.diff(DiffResultTest.SIMPLE_FALSE).getDiffs(), DiffResultTest.SHORT_STYLE));
    }

    @Test
    public void testNullRhs() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DiffResult(DiffResultTest.SIMPLE_TRUE, null, DiffResultTest.SIMPLE_TRUE.diff(DiffResultTest.SIMPLE_FALSE).getDiffs(), DiffResultTest.SHORT_STYLE));
    }

    @Test
    public void testNullList() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DiffResult(DiffResultTest.SIMPLE_TRUE, DiffResultTest.SIMPLE_FALSE, null, DiffResultTest.SHORT_STYLE));
    }

    @Test
    public void testNullStyle() {
        final DiffResult diffResult = new DiffResult(DiffResultTest.SIMPLE_TRUE, DiffResultTest.SIMPLE_FALSE, DiffResultTest.SIMPLE_TRUE.diff(DiffResultTest.SIMPLE_FALSE).getDiffs(), null);
        Assertions.assertEquals(ToStringStyle.DEFAULT_STYLE, diffResult.getToStringStyle());
    }

    @Test
    public void testNoDifferencesString() {
        final DiffResult diffResult = new DiffBuilder(DiffResultTest.SIMPLE_TRUE, DiffResultTest.SIMPLE_TRUE, DiffResultTest.SHORT_STYLE).build();
        Assertions.assertEquals(DiffResult.OBJECTS_SAME_STRING, diffResult.toString());
    }
}

