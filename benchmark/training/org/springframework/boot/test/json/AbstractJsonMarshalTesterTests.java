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


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ReflectionUtils;


/**
 * Tests for {@link AbstractJsonMarshalTester}.
 *
 * @author Phillip Webb
 */
public abstract class AbstractJsonMarshalTesterTests {
    private static final String JSON = "{\"name\":\"Spring\",\"age\":123}";

    private static final String MAP_JSON = ("{\"a\":" + (AbstractJsonMarshalTesterTests.JSON)) + "}";

    private static final String ARRAY_JSON = ("[" + (AbstractJsonMarshalTesterTests.JSON)) + "]";

    private static final ExampleObject OBJECT = AbstractJsonMarshalTesterTests.createExampleObject("Spring", 123);

    private static final ResolvableType TYPE = ResolvableType.forClass(ExampleObject.class);

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void writeShouldReturnJsonContent() throws Exception {
        JsonContent<Object> content = createTester(AbstractJsonMarshalTesterTests.TYPE).write(AbstractJsonMarshalTesterTests.OBJECT);
        assertThat(content).isEqualToJson(AbstractJsonMarshalTesterTests.JSON);
    }

    @Test
    public void writeListShouldReturnJsonContent() throws Exception {
        ResolvableType type = AbstractJsonMarshalTesterTests.ResolvableTypes.get("listOfExampleObject");
        List<ExampleObject> value = Collections.singletonList(AbstractJsonMarshalTesterTests.OBJECT);
        JsonContent<Object> content = createTester(type).write(value);
        assertThat(content).isEqualToJson(AbstractJsonMarshalTesterTests.ARRAY_JSON);
    }

    @Test
    public void writeArrayShouldReturnJsonContent() throws Exception {
        ResolvableType type = AbstractJsonMarshalTesterTests.ResolvableTypes.get("arrayOfExampleObject");
        ExampleObject[] value = new ExampleObject[]{ AbstractJsonMarshalTesterTests.OBJECT };
        JsonContent<Object> content = createTester(type).write(value);
        assertThat(content).isEqualToJson(AbstractJsonMarshalTesterTests.ARRAY_JSON);
    }

    @Test
    public void writeMapShouldReturnJsonContent() throws Exception {
        ResolvableType type = AbstractJsonMarshalTesterTests.ResolvableTypes.get("mapOfExampleObject");
        Map<String, Object> value = new LinkedHashMap<>();
        value.put("a", AbstractJsonMarshalTesterTests.OBJECT);
        JsonContent<Object> content = createTester(type).write(value);
        assertThat(content).isEqualToJson(AbstractJsonMarshalTesterTests.MAP_JSON);
    }

    @Test
    public void createWhenResourceLoadClassIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> createTester(null, ResolvableType.forClass(.class))).withMessageContaining("ResourceLoadClass must not be null");
    }

    @Test
    public void createWhenTypeIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> createTester(getClass(), null)).withMessageContaining("Type must not be null");
    }

    @Test
    public void parseBytesShouldReturnObject() throws Exception {
        AbstractJsonMarshalTester<Object> tester = createTester(AbstractJsonMarshalTesterTests.TYPE);
        assertThat(tester.parse(AbstractJsonMarshalTesterTests.JSON.getBytes())).isEqualTo(AbstractJsonMarshalTesterTests.OBJECT);
    }

    @Test
    public void parseStringShouldReturnObject() throws Exception {
        AbstractJsonMarshalTester<Object> tester = createTester(AbstractJsonMarshalTesterTests.TYPE);
        assertThat(tester.parse(AbstractJsonMarshalTesterTests.JSON)).isEqualTo(AbstractJsonMarshalTesterTests.OBJECT);
    }

    @Test
    public void readResourcePathShouldReturnObject() throws Exception {
        AbstractJsonMarshalTester<Object> tester = createTester(AbstractJsonMarshalTesterTests.TYPE);
        assertThat(tester.read("example.json")).isEqualTo(AbstractJsonMarshalTesterTests.OBJECT);
    }

    @Test
    public void readFileShouldReturnObject() throws Exception {
        File file = this.temp.newFile("example.json");
        FileCopyUtils.copy(AbstractJsonMarshalTesterTests.JSON.getBytes(), file);
        AbstractJsonMarshalTester<Object> tester = createTester(AbstractJsonMarshalTesterTests.TYPE);
        assertThat(tester.read(file)).isEqualTo(AbstractJsonMarshalTesterTests.OBJECT);
    }

    @Test
    public void readInputStreamShouldReturnObject() throws Exception {
        InputStream stream = new ByteArrayInputStream(AbstractJsonMarshalTesterTests.JSON.getBytes());
        AbstractJsonMarshalTester<Object> tester = createTester(AbstractJsonMarshalTesterTests.TYPE);
        assertThat(tester.read(stream)).isEqualTo(AbstractJsonMarshalTesterTests.OBJECT);
    }

    @Test
    public void readResourceShouldReturnObject() throws Exception {
        Resource resource = new ByteArrayResource(AbstractJsonMarshalTesterTests.JSON.getBytes());
        AbstractJsonMarshalTester<Object> tester = createTester(AbstractJsonMarshalTesterTests.TYPE);
        assertThat(tester.read(resource)).isEqualTo(AbstractJsonMarshalTesterTests.OBJECT);
    }

    @Test
    public void readReaderShouldReturnObject() throws Exception {
        Reader reader = new StringReader(AbstractJsonMarshalTesterTests.JSON);
        AbstractJsonMarshalTester<Object> tester = createTester(AbstractJsonMarshalTesterTests.TYPE);
        assertThat(tester.read(reader)).isEqualTo(AbstractJsonMarshalTesterTests.OBJECT);
    }

    @Test
    public void parseListShouldReturnContent() throws Exception {
        ResolvableType type = AbstractJsonMarshalTesterTests.ResolvableTypes.get("listOfExampleObject");
        AbstractJsonMarshalTester<Object> tester = createTester(type);
        assertThat(tester.parse(AbstractJsonMarshalTesterTests.ARRAY_JSON)).asList().containsOnly(AbstractJsonMarshalTesterTests.OBJECT);
    }

    @Test
    public void parseArrayShouldReturnContent() throws Exception {
        ResolvableType type = AbstractJsonMarshalTesterTests.ResolvableTypes.get("arrayOfExampleObject");
        AbstractJsonMarshalTester<Object> tester = createTester(type);
        assertThat(tester.parse(AbstractJsonMarshalTesterTests.ARRAY_JSON)).asArray().containsOnly(AbstractJsonMarshalTesterTests.OBJECT);
    }

    @Test
    public void parseMapShouldReturnContent() throws Exception {
        ResolvableType type = AbstractJsonMarshalTesterTests.ResolvableTypes.get("mapOfExampleObject");
        AbstractJsonMarshalTester<Object> tester = createTester(type);
        assertThat(tester.parse(AbstractJsonMarshalTesterTests.MAP_JSON)).asMap().containsEntry("a", AbstractJsonMarshalTesterTests.OBJECT);
    }

    /**
     * Access to field backed by {@link ResolvableType}.
     */
    public static class ResolvableTypes {
        public List<ExampleObject> listOfExampleObject;

        public ExampleObject[] arrayOfExampleObject;

        public Map<String, ExampleObject> mapOfExampleObject;

        public static ResolvableType get(String name) {
            Field field = ReflectionUtils.findField(AbstractJsonMarshalTesterTests.ResolvableTypes.class, name);
            return ResolvableType.forField(field);
        }
    }
}

