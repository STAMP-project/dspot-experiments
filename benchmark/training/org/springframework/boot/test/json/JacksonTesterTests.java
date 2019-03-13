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


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.Test;
import org.springframework.core.ResolvableType;


/**
 * Tests for {@link JacksonTester}.
 *
 * @author Phillip Webb
 */
public class JacksonTesterTests extends AbstractJsonMarshalTesterTests {
    @Test
    public void initFieldsWhenTestIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> JacksonTester.initFields(null, new ObjectMapper())).withMessageContaining("TestInstance must not be null");
    }

    @Test
    public void initFieldsWhenMarshallerIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> JacksonTester.initFields(new org.springframework.boot.test.json.InitFieldsTestClass(), ((ObjectMapper) (null)))).withMessageContaining("Marshaller must not be null");
    }

    @Test
    public void initFieldsShouldSetNullFields() {
        JacksonTesterTests.InitFieldsTestClass test = new JacksonTesterTests.InitFieldsTestClass();
        assertThat(test.test).isNull();
        assertThat(test.base).isNull();
        JacksonTester.initFields(test, new ObjectMapper());
        assertThat(test.test).isNotNull();
        assertThat(test.base).isNotNull();
        assertThat(test.test.getType().resolve()).isEqualTo(List.class);
        assertThat(test.test.getType().resolveGeneric()).isEqualTo(ExampleObject.class);
    }

    abstract static class InitFieldsBaseClass {
        public JacksonTester<ExampleObject> base;

        public JacksonTester<ExampleObject> baseSet = new JacksonTester(JacksonTesterTests.InitFieldsBaseClass.class, ResolvableType.forClass(ExampleObject.class), new ObjectMapper());
    }

    static class InitFieldsTestClass extends JacksonTesterTests.InitFieldsBaseClass {
        public JacksonTester<List<ExampleObject>> test;

        public JacksonTester<ExampleObject> testSet = new JacksonTester(JacksonTesterTests.InitFieldsBaseClass.class, ResolvableType.forClass(ExampleObject.class), new ObjectMapper());
    }
}

