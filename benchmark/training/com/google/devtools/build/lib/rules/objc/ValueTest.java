/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.rules.objc;


import com.google.common.testing.EqualsTester;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@class Value}.
 */
@RunWith(JUnit4.class)
public class ValueTest {
    private static final class PersonName extends Value<ValueTest.PersonName> {
        public PersonName(String first, String last) {
            super(first, last);
        }
    }

    private enum NameData {

        JOHN_DOE,
        JANE_DOE,
        JOHN_SMITH;}

    @Test
    public void nullNotAllowedInMemberData() {
        try {
            new ValueTest.PersonName(null, "Doe");
            Assert.fail("should have thrown");
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void equality() {
        EqualsTester tester = new EqualsTester();
        for (ValueTest.NameData what : ValueTest.NameData.values()) {
            tester.addEqualityGroup(make(what), make(what));
        }
        tester.testEquals();
    }

    @Test
    public void testToString() {
        assertThat(make(ValueTest.NameData.JOHN_DOE).toString()).contains("John");
        assertThat(make(ValueTest.NameData.JOHN_DOE).toString()).contains("Doe");
    }
}

