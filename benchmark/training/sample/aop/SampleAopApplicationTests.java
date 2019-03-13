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
package sample.aop;


import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.rule.OutputCapture;


/**
 * Tests for {@link SampleAopApplication}.
 *
 * @author Dave Syer
 * @author Phillip Webb
 */
public class SampleAopApplicationTests {
    @Rule
    public final OutputCapture output = new OutputCapture();

    private String profiles;

    @Test
    public void testDefaultSettings() throws Exception {
        SampleAopApplication.main(new String[0]);
        assertThat(this.output.toString()).contains("Hello Phil");
    }

    @Test
    public void testCommandLineOverrides() throws Exception {
        SampleAopApplication.main(new String[]{ "--name=Gordon" });
        assertThat(this.output.toString()).contains("Hello Gordon");
    }
}

