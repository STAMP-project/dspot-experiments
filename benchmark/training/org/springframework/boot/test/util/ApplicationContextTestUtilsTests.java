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
package org.springframework.boot.test.util;


import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;


/**
 * Tests for {@link ApplicationContextTestUtils}.
 *
 * @author Stephane Nicoll
 */
public class ApplicationContextTestUtilsTests {
    @Test
    public void closeNull() {
        ApplicationContextTestUtils.closeAll(null);
    }

    @Test
    public void closeNonClosableContext() {
        ApplicationContext mock = Mockito.mock(ApplicationContext.class);
        ApplicationContextTestUtils.closeAll(mock);
    }

    @Test
    public void closeContextAndParent() {
        ConfigurableApplicationContext mock = Mockito.mock(ConfigurableApplicationContext.class);
        ConfigurableApplicationContext parent = Mockito.mock(ConfigurableApplicationContext.class);
        BDDMockito.given(mock.getParent()).willReturn(parent);
        BDDMockito.given(parent.getParent()).willReturn(null);
        ApplicationContextTestUtils.closeAll(mock);
        Mockito.verify(mock).getParent();
        Mockito.verify(mock).close();
        Mockito.verify(parent).getParent();
        Mockito.verify(parent).close();
    }
}

