/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.test.json;


import java.util.Collections;
import java.util.Map;
import org.junit.Test;


/**
 * Tests for {@link ObjectContentAssert}.
 *
 * @author Phillip Webb
 */
public class ObjectContentAssertTests {
    private static final ExampleObject SOURCE = new ExampleObject();

    private static final ExampleObject DIFFERENT;

    static {
        DIFFERENT = new ExampleObject();
        ObjectContentAssertTests.DIFFERENT.setAge(123);
    }

    @Test
    public void isEqualToWhenObjectsAreEqualShouldPass() {
        assertThat(forObject(ObjectContentAssertTests.SOURCE)).isEqualTo(ObjectContentAssertTests.SOURCE);
    }

    @Test
    public void isEqualToWhenObjectsAreDifferentShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forObject(SOURCE)).isEqualTo(DIFFERENT));
    }

    @Test
    public void asArrayForArrayShouldReturnObjectArrayAssert() {
        ExampleObject[] source = new ExampleObject[]{ ObjectContentAssertTests.SOURCE };
        assertThat(forObject(source)).asArray().containsExactly(ObjectContentAssertTests.SOURCE);
    }

    @Test
    public void asArrayForNonArrayShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forObject(SOURCE)).asArray());
    }

    @Test
    public void asMapForMapShouldReturnMapAssert() {
        Map<String, ExampleObject> source = Collections.singletonMap("a", ObjectContentAssertTests.SOURCE);
        assertThat(forObject(source)).asMap().containsEntry("a", ObjectContentAssertTests.SOURCE);
    }

    @Test
    public void asMapForNonMapShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forObject(SOURCE)).asMap());
    }
}

