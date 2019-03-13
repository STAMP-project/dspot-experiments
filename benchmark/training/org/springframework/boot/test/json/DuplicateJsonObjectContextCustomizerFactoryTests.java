/**
 * Copyright 2012-2017 the original author or authors.
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


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.boot.testsupport.runner.classpath.ClassPathOverrides;
import org.springframework.boot.testsupport.runner.classpath.ModifiedClassPathRunner;


/**
 * Tests for {@link DuplicateJsonObjectContextCustomizerFactory}.
 *
 * @author Andy Wilkinson
 */
@RunWith(ModifiedClassPathRunner.class)
@ClassPathOverrides("org.json:json:20140107")
public class DuplicateJsonObjectContextCustomizerFactoryTests {
    @Rule
    public OutputCapture output = new OutputCapture();

    @Test
    public void warningForMultipleVersions() {
        new DuplicateJsonObjectContextCustomizerFactory().createContextCustomizer(null, null).customizeContext(null, null);
        assertThat(this.output.toString()).contains("Found multiple occurrences of org.json.JSONObject on the class path:");
    }
}

