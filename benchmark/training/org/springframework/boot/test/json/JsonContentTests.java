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
 * Tests for {@link JsonContent}.
 *
 * @author Phillip Webb
 */
public class JsonContentTests {
    private static final String JSON = "{\"name\":\"spring\", \"age\":100}";

    private static final ResolvableType TYPE = ResolvableType.forClass(ExampleObject.class);

    @Test
    public void createWhenResourceLoadClassIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new JsonContent<ExampleObject>(null, TYPE, JSON)).withMessageContaining("ResourceLoadClass must not be null");
    }

    @Test
    public void createWhenJsonIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new JsonContent<ExampleObject>(getClass(), TYPE, null)).withMessageContaining("JSON must not be null");
    }

    @Test
    public void createWhenTypeIsNullShouldCreateContent() {
        JsonContent<ExampleObject> content = new JsonContent(getClass(), null, JsonContentTests.JSON);
        assertThat(content).isNotNull();
    }

    @Test
    @SuppressWarnings("deprecation")
    public void assertThatShouldReturnJsonContentAssert() {
        JsonContent<ExampleObject> content = new JsonContent(getClass(), JsonContentTests.TYPE, JsonContentTests.JSON);
        assertThat(content.assertThat()).isInstanceOf(JsonContentAssert.class);
    }

    @Test
    public void getJsonShouldReturnJson() {
        JsonContent<ExampleObject> content = new JsonContent(getClass(), JsonContentTests.TYPE, JsonContentTests.JSON);
        assertThat(content.getJson()).isEqualTo(JsonContentTests.JSON);
    }

    @Test
    public void toStringWhenHasTypeShouldReturnString() {
        JsonContent<ExampleObject> content = new JsonContent(getClass(), JsonContentTests.TYPE, JsonContentTests.JSON);
        assertThat(content.toString()).isEqualTo(((("JsonContent " + (JsonContentTests.JSON)) + " created from ") + (JsonContentTests.TYPE)));
    }

    @Test
    public void toStringWhenHasNoTypeShouldReturnString() {
        JsonContent<ExampleObject> content = new JsonContent(getClass(), null, JsonContentTests.JSON);
        assertThat(content.toString()).isEqualTo(("JsonContent " + (JsonContentTests.JSON)));
    }
}

