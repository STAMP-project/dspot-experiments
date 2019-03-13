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
package org.springframework.boot.jta.atomikos;


import javax.jms.ConnectionFactory;
import javax.jms.XAConnectionFactory;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link AtomikosXAConnectionFactoryWrapper}.
 *
 * @author Phillip Webb
 */
public class AtomikosXAConnectionFactoryWrapperTests {
    @Test
    public void wrap() {
        XAConnectionFactory connectionFactory = Mockito.mock(XAConnectionFactory.class);
        AtomikosXAConnectionFactoryWrapper wrapper = new AtomikosXAConnectionFactoryWrapper();
        ConnectionFactory wrapped = wrapper.wrapConnectionFactory(connectionFactory);
        assertThat(wrapped).isInstanceOf(AtomikosConnectionFactoryBean.class);
        assertThat(getXaConnectionFactory()).isSameAs(connectionFactory);
    }
}

