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


import org.junit.jupiter.api.Test;


/**
 * Tests {@link org.apache.commons.lang3.builder.HashCodeBuilder} and
 * {@link org.apache.commons.lang3.builder.EqualsBuilderTest} to insure that equal
 * objects must have equal hash codes.
 */
public class HashCodeBuilderAndEqualsBuilderTest {
    @Test
    public void testInteger() {
        testInteger(false);
    }

    @Test
    public void testIntegerWithTransients() {
        testInteger(true);
    }

    @Test
    public void testFixture() {
        testFixture(false);
    }

    @Test
    public void testFixtureWithTransients() {
        testFixture(true);
    }

    static class TestFixture {
        int i;

        char c;

        String string;

        short s;

        TestFixture(final int i, final char c, final String string, final short s) {
            this.i = i;
            this.c = c;
            this.string = string;
            this.s = s;
        }
    }

    static class SubTestFixture extends HashCodeBuilderAndEqualsBuilderTest.TestFixture {
        transient String tString;

        SubTestFixture(final int i, final char c, final String string, final short s, final String tString) {
            super(i, c, string, s);
            this.tString = tString;
        }
    }

    static class AllTransientFixture {
        transient int i;

        transient char c;

        transient String string;

        transient short s;

        AllTransientFixture(final int i, final char c, final String string, final short s) {
            this.i = i;
            this.c = c;
            this.string = string;
            this.s = s;
        }
    }

    static class SubAllTransientFixture extends HashCodeBuilderAndEqualsBuilderTest.AllTransientFixture {
        transient String tString;

        SubAllTransientFixture(final int i, final char c, final String string, final short s, final String tString) {
            super(i, c, string, s);
            this.tString = tString;
        }
    }
}

