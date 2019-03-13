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


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Test case for ToStringStyle.
 */
public class ToStringStyleTest {
    private static class ToStringStyleImpl extends ToStringStyle {
        private static final long serialVersionUID = 1L;
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSetArrayStart() {
        final ToStringStyle style = new ToStringStyleTest.ToStringStyleImpl();
        style.setArrayStart(null);
        Assertions.assertEquals("", style.getArrayStart());
    }

    @Test
    public void testSetArrayEnd() {
        final ToStringStyle style = new ToStringStyleTest.ToStringStyleImpl();
        style.setArrayEnd(null);
        Assertions.assertEquals("", style.getArrayEnd());
    }

    @Test
    public void testSetArraySeparator() {
        final ToStringStyle style = new ToStringStyleTest.ToStringStyleImpl();
        style.setArraySeparator(null);
        Assertions.assertEquals("", style.getArraySeparator());
    }

    @Test
    public void testSetContentStart() {
        final ToStringStyle style = new ToStringStyleTest.ToStringStyleImpl();
        style.setContentStart(null);
        Assertions.assertEquals("", style.getContentStart());
    }

    @Test
    public void testSetContentEnd() {
        final ToStringStyle style = new ToStringStyleTest.ToStringStyleImpl();
        style.setContentEnd(null);
        Assertions.assertEquals("", style.getContentEnd());
    }

    @Test
    public void testSetFieldNameValueSeparator() {
        final ToStringStyle style = new ToStringStyleTest.ToStringStyleImpl();
        style.setFieldNameValueSeparator(null);
        Assertions.assertEquals("", style.getFieldNameValueSeparator());
    }

    @Test
    public void testSetFieldSeparator() {
        final ToStringStyle style = new ToStringStyleTest.ToStringStyleImpl();
        style.setFieldSeparator(null);
        Assertions.assertEquals("", style.getFieldSeparator());
    }

    @Test
    public void testSetNullText() {
        final ToStringStyle style = new ToStringStyleTest.ToStringStyleImpl();
        style.setNullText(null);
        Assertions.assertEquals("", style.getNullText());
    }

    @Test
    public void testSetSizeStartText() {
        final ToStringStyle style = new ToStringStyleTest.ToStringStyleImpl();
        style.setSizeStartText(null);
        Assertions.assertEquals("", style.getSizeStartText());
    }

    @Test
    public void testSetSizeEndText() {
        final ToStringStyle style = new ToStringStyleTest.ToStringStyleImpl();
        style.setSizeEndText(null);
        Assertions.assertEquals("", style.getSizeEndText());
    }

    @Test
    public void testSetSummaryObjectStartText() {
        final ToStringStyle style = new ToStringStyleTest.ToStringStyleImpl();
        style.setSummaryObjectStartText(null);
        Assertions.assertEquals("", style.getSummaryObjectStartText());
    }

    @Test
    public void testSetSummaryObjectEndText() {
        final ToStringStyle style = new ToStringStyleTest.ToStringStyleImpl();
        style.setSummaryObjectEndText(null);
        Assertions.assertEquals("", style.getSummaryObjectEndText());
    }

    /**
     * An object used to test {@link ToStringStyle}.
     */
    static class Person {
        /**
         * Test String field.
         */
        String name;

        /**
         * Test integer field.
         */
        int age;

        /**
         * Test boolean field.
         */
        boolean smoker;
    }
}

