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


import javax.validation.ConstraintViolationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;


/**
 * Tests for {@link WebMvcTest} when no explicit controller is defined.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@WithMockUser
public class WebMvcTestAllControllersIntegrationTests {
    @Autowired
    private MockMvc mvc;

    @Autowired(required = false)
    private ErrorAttributes errorAttributes;

    @Test
    public void shouldFindController1() throws Exception {
        this.mvc.perform(get("/one")).andExpect(content().string("one")).andExpect(status().isOk());
    }

    @Test
    public void shouldFindController2() throws Exception {
        this.mvc.perform(get("/two")).andExpect(content().string("hellotwo")).andExpect(status().isOk());
    }

    @Test
    public void shouldFindControllerAdvice() throws Exception {
        this.mvc.perform(get("/error")).andExpect(content().string("recovered")).andExpect(status().isOk());
    }

    @Test
    public void shouldRunValidationSuccess() throws Exception {
        this.mvc.perform(get("/three/OK")).andExpect(status().isOk()).andExpect(content().string("Hello OK"));
    }

    @Test
    public void shouldRunValidationFailure() throws Exception {
        assertThatExceptionOfType(NestedServletException.class).isThrownBy(() -> this.mvc.perform(get("/three/invalid"))).withCauseInstanceOf(ConstraintViolationException.class);
    }

    @Test
    public void shouldNotFilterErrorAttributes() {
        assertThat(this.errorAttributes).isNotNull();
    }
}

