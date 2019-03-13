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
package sample.profile;


import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.rule.OutputCapture;


public class SampleProfileApplicationTests {
    @Rule
    public final OutputCapture output = new OutputCapture();

    private String profiles;

    @Test
    public void testDefaultProfile() throws Exception {
        SampleProfileApplication.main(new String[0]);
        assertThat(this.output.toString()).contains("Hello Phil");
    }

    @Test
    public void testGoodbyeProfile() throws Exception {
        System.setProperty("spring.profiles.active", "goodbye");
        SampleProfileApplication.main(new String[0]);
        assertThat(this.output.toString()).contains("Goodbye Everyone");
    }

    @Test
    public void testGenericProfile() throws Exception {
        /* This is a profile that requires a new environment property, and one which is
        only overridden in the current working directory. That file also only contains
        partial overrides, and the default application.yml should still supply the
        "name" property.
         */
        System.setProperty("spring.profiles.active", "generic");
        SampleProfileApplication.main(new String[0]);
        assertThat(this.output.toString()).contains("Bonjour Phil");
    }

    @Test
    public void testGoodbyeProfileFromCommandline() throws Exception {
        SampleProfileApplication.main(new String[]{ "--spring.profiles.active=goodbye" });
        assertThat(this.output.toString()).contains("Goodbye Everyone");
    }
}

