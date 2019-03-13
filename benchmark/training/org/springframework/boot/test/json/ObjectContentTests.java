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


import org.junit.Test;
import org.springframework.core.ResolvableType;


/**
 * Tests for {@link ObjectContent}.
 *
 * @author Phillip Webb
 */
public class ObjectContentTests {
    private static final ExampleObject OBJECT = new ExampleObject();

    private static final ResolvableType TYPE = ResolvableType.forClass(ExampleObject.class);

    @Test
    public void createWhenObjectIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ObjectContent<ExampleObject>(TYPE, null)).withMessageContaining("Object must not be null");
    }

    @Test
    public void createWhenTypeIsNullShouldCreateContent() {
        ObjectContent<ExampleObject> content = new ObjectContent(null, ObjectContentTests.OBJECT);
        assertThat(content).isNotNull();
    }

    @Test
    public void assertThatShouldReturnObjectContentAssert() {
        ObjectContent<ExampleObject> content = new ObjectContent(ObjectContentTests.TYPE, ObjectContentTests.OBJECT);
        assertThat(content.assertThat()).isInstanceOf(ObjectContentAssert.class);
    }

    @Test
    public void getObjectShouldReturnObject() {
        ObjectContent<ExampleObject> content = new ObjectContent(ObjectContentTests.TYPE, ObjectContentTests.OBJECT);
        assertThat(content.getObject()).isEqualTo(ObjectContentTests.OBJECT);
    }

    @Test
    public void toStringWhenHasTypeShouldReturnString() {
        ObjectContent<ExampleObject> content = new ObjectContent(ObjectContentTests.TYPE, ObjectContentTests.OBJECT);
        assertThat(content.toString()).isEqualTo(((("ObjectContent " + (ObjectContentTests.OBJECT)) + " created from ") + (ObjectContentTests.TYPE)));
    }

    @Test
    public void toStringWhenHasNoTypeShouldReturnString() {
        ObjectContent<ExampleObject> content = new ObjectContent(null, ObjectContentTests.OBJECT);
        assertThat(content.toString()).isEqualTo(("ObjectContent " + (ObjectContentTests.OBJECT)));
    }
}

