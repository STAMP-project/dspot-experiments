/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.test.web.servlet.samples.standalone.resultmatchers;


import MvcUriComponentsBuilder.MethodInvocationInfo;
import java.lang.reflect.Method;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;


/**
 * Examples of expectations on the controller type and controller method.
 *
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 */
public class HandlerAssertionTests {
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new HandlerAssertionTests.SimpleController()).alwaysExpect(MockMvcResultMatchers.status().isOk()).build();

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void handlerType() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.handler().handlerType(HandlerAssertionTests.SimpleController.class));
    }

    @Test
    public void methodCallOnNonMock() throws Exception {
        exception.expect(AssertionError.class);
        exception.expectMessage("The supplied object [bogus] is not an instance of");
        exception.expectMessage(MethodInvocationInfo.class.getName());
        exception.expectMessage("Ensure that you invoke the handler method via MvcUriComponentsBuilder.on()");
        this.mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.handler().methodCall("bogus"));
    }

    @Test
    public void methodCall() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.handler().methodCall(on(HandlerAssertionTests.SimpleController.class).handle()));
    }

    @Test
    public void methodName() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.handler().methodName("handle"));
    }

    @Test
    public void methodNameMatchers() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.handler().methodName(equalTo("handle")));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.handler().methodName(is(not("save"))));
    }

    @Test
    public void method() throws Exception {
        Method method = HandlerAssertionTests.SimpleController.class.getMethod("handle");
        this.mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.handler().method(method));
    }

    @RestController
    static class SimpleController {
        @RequestMapping("/")
        public ResponseEntity<Void> handle() {
            return ResponseEntity.ok().build();
        }
    }
}

