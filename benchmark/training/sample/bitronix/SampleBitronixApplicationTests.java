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
package sample.bitronix;


import bitronix.tm.resource.jms.PoolingConnectionFactory;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.context.ApplicationContext;


/**
 * Basic integration tests for demo application.
 *
 * @author Phillip Webb
 */
public class SampleBitronixApplicationTests {
    @Rule
    public final OutputCapture output = new OutputCapture();

    @Test
    public void testTransactionRollback() throws Exception {
        SampleBitronixApplication.main(new String[]{  });
        assertThat(this.output.toString()).has(substring(1, "---->"));
        assertThat(this.output.toString()).has(substring(1, "----> josh"));
        assertThat(this.output.toString()).has(substring(2, "Count is 1"));
        assertThat(this.output.toString()).has(substring(1, "Simulated error"));
    }

    @Test
    public void testExposesXaAndNonXa() {
        ApplicationContext context = SpringApplication.run(SampleBitronixApplication.class);
        Object jmsConnectionFactory = context.getBean("jmsConnectionFactory");
        Object xaJmsConnectionFactory = context.getBean("xaJmsConnectionFactory");
        Object nonXaJmsConnectionFactory = context.getBean("nonXaJmsConnectionFactory");
        assertThat(jmsConnectionFactory).isSameAs(xaJmsConnectionFactory);
        assertThat(jmsConnectionFactory).isInstanceOf(PoolingConnectionFactory.class);
        assertThat(nonXaJmsConnectionFactory).isNotInstanceOf(PoolingConnectionFactory.class);
    }
}

