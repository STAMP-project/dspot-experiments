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
package org.springframework.boot.test.autoconfigure.web.servlet.mockmvc;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


/**
 * Tests for {@link WebMvcTest} when a specific controller is defined.
 *
 * @author Phillip Webb
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@WithMockUser
@TestPropertySource(properties = "spring.test.mockmvc.print=NONE")
public class WebMvcTestPrintDefaultOverrideIntegrationTests {
    @Rule
    public OutputCapture output = new OutputCapture();

    @Autowired
    private MockMvc mvc;

    @Test
    public void shouldFindController1() throws Exception {
        this.mvc.perform(get("/one")).andExpect(content().string("one")).andExpect(status().isOk());
        assertThat(this.output.toString()).doesNotContain("Request URI = /one");
    }
}

