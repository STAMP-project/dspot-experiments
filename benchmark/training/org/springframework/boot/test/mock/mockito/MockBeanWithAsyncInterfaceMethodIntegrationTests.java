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
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Tests for a mock bean where the mocked interface has an async method.
 *
 * @author Andy Wilkinson
 */
@RunWith(SpringRunner.class)
public class MockBeanWithAsyncInterfaceMethodIntegrationTests {
    @MockBean
    private MockBeanWithAsyncInterfaceMethodIntegrationTests.Transformer transformer;

    @Autowired
    private MockBeanWithAsyncInterfaceMethodIntegrationTests.MyService service;

    @Test
    public void mockedMethodsAreNotAsync() {
        BDDMockito.given(this.transformer.transform("foo")).willReturn("bar");
        assertThat(this.service.transform("foo")).isEqualTo("bar");
    }

    private interface Transformer {
        @Async
        String transform(String input);
    }

    private static class MyService {
        private final MockBeanWithAsyncInterfaceMethodIntegrationTests.Transformer transformer;

        MyService(MockBeanWithAsyncInterfaceMethodIntegrationTests.Transformer transformer) {
            this.transformer = transformer;
        }

        public String transform(String input) {
            return this.transformer.transform(input);
        }
    }

    @Configuration
    @EnableAsync
    static class MyConfiguration {
        @Bean
        public MockBeanWithAsyncInterfaceMethodIntegrationTests.MyService myService(MockBeanWithAsyncInterfaceMethodIntegrationTests.Transformer transformer) {
            return new MockBeanWithAsyncInterfaceMethodIntegrationTests.MyService(transformer);
        }
    }
}

