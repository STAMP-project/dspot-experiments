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
package org.springframework.boot.test.autoconfigure;


import WebApplicationType.NONE;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;


/**
 * Tests for {@link SpringBootDependencyInjectionTestExecutionListener}.
 *
 * @author Phillip Webb
 */
public class SpringBootDependencyInjectionTestExecutionListenerTests {
    @Rule
    public OutputCapture out = new OutputCapture();

    private SpringBootDependencyInjectionTestExecutionListener reportListener = new SpringBootDependencyInjectionTestExecutionListener();

    @Test
    public void orderShouldBeSameAsDependencyInjectionTestExecutionListener() {
        Ordered injectionListener = new DependencyInjectionTestExecutionListener();
        assertThat(this.reportListener.getOrder()).isEqualTo(injectionListener.getOrder());
    }

    @Test
    public void prepareFailingTestInstanceShouldPrintReport() throws Exception {
        TestContext testContext = Mockito.mock(TestContext.class);
        BDDMockito.given(testContext.getTestInstance()).willThrow(new IllegalStateException());
        SpringApplication application = new SpringApplication(SpringBootDependencyInjectionTestExecutionListenerTests.Config.class);
        application.setWebApplicationType(NONE);
        ConfigurableApplicationContext applicationContext = application.run();
        BDDMockito.given(testContext.getApplicationContext()).willReturn(applicationContext);
        try {
            this.reportListener.prepareTestInstance(testContext);
        } catch (IllegalStateException ex) {
            // Expected
        }
        this.out.expect(Matchers.containsString("CONDITIONS EVALUATION REPORT"));
        this.out.expect(Matchers.containsString("Positive matches"));
        this.out.expect(Matchers.containsString("Negative matches"));
    }

    @Test
    public void originalFailureIsThrownWhenReportGenerationFails() throws Exception {
        TestContext testContext = Mockito.mock(TestContext.class);
        IllegalStateException originalFailure = new IllegalStateException();
        BDDMockito.given(testContext.getTestInstance()).willThrow(originalFailure);
        SpringApplication application = new SpringApplication(SpringBootDependencyInjectionTestExecutionListenerTests.Config.class);
        application.setWebApplicationType(NONE);
        BDDMockito.given(testContext.getApplicationContext()).willThrow(new RuntimeException());
        assertThatIllegalStateException().isThrownBy(() -> this.reportListener.prepareTestInstance(testContext)).isEqualTo(originalFailure);
    }

    @Configuration
    @ImportAutoConfiguration(JacksonAutoConfiguration.class)
    static class Config {}
}

