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
package org.springframework.boot.test.mock.mockito;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.example.ExampleServiceCaller;
import org.springframework.boot.test.mock.mockito.example.SimpleExampleService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Integration tests for using {@link SpyBean} with {@link DirtiesContext} and
 * {@link ClassMode#BEFORE_EACH_TEST_METHOD}.
 *
 * @author Andy Wilkinson
 */
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class SpyBeanWithDirtiesContextClassModeBeforeMethodIntegrationTests {
    @SpyBean
    private SimpleExampleService exampleService;

    @Autowired
    private ExampleServiceCaller caller;

    @Test
    public void testSpying() throws Exception {
        this.caller.sayGreeting();
        Mockito.verify(this.exampleService).greeting();
    }

    @Configuration
    @Import(ExampleServiceCaller.class)
    static class Config {}
}

