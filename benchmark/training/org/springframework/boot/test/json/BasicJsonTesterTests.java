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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;


/**
 * Tests for {@link BasicJsonTester}.
 *
 * @author Phillip Webb
 */
public class BasicJsonTesterTests {
    private static final String JSON = "{\"spring\":[\"boot\",\"framework\"]}";

    private BasicJsonTester json = new BasicJsonTester(getClass());

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void createWhenResourceLoadClassIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new BasicJsonTester(null)).withMessageContaining("ResourceLoadClass must not be null");
    }

    @Test
    public void fromJsonStringShouldReturnJsonContent() {
        assertThat(this.json.from(BasicJsonTesterTests.JSON)).isEqualToJson("source.json");
    }

    @Test
    public void fromResourceStringShouldReturnJsonContent() {
        assertThat(this.json.from("source.json")).isEqualToJson(BasicJsonTesterTests.JSON);
    }

    @Test
    public void fromResourceStringWithClassShouldReturnJsonContent() {
        assertThat(this.json.from("source.json", getClass())).isEqualToJson(BasicJsonTesterTests.JSON);
    }

    @Test
    public void fromByteArrayShouldReturnJsonContent() {
        assertThat(this.json.from(BasicJsonTesterTests.JSON.getBytes())).isEqualToJson("source.json");
    }

    @Test
    public void fromFileShouldReturnJsonContent() throws Exception {
        File file = this.temp.newFile("file.json");
        FileCopyUtils.copy(BasicJsonTesterTests.JSON.getBytes(), file);
        assertThat(this.json.from(file)).isEqualToJson("source.json");
    }

    @Test
    public void fromInputStreamShouldReturnJsonContent() {
        InputStream inputStream = new ByteArrayInputStream(BasicJsonTesterTests.JSON.getBytes());
        assertThat(this.json.from(inputStream)).isEqualToJson("source.json");
    }

    @Test
    public void fromResourceShouldReturnJsonContent() {
        Resource resource = new ByteArrayResource(BasicJsonTesterTests.JSON.getBytes());
        assertThat(this.json.from(resource)).isEqualToJson("source.json");
    }
}

