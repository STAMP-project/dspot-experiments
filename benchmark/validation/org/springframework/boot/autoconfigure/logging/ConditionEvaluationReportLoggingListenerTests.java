/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.autoconfigure.logging;


import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;


/**
 * Tests for {@link ConditionEvaluationReportLoggingListener}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @author Madhura Bhave
 */
public class ConditionEvaluationReportLoggingListenerTests {
    @Rule
    public final OutputCapture output = new OutputCapture();

    private ConditionEvaluationReportLoggingListener initializer = new ConditionEvaluationReportLoggingListener();

    @Test
    public void logsDebugOnContextRefresh() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        this.initializer.initialize(context);
        context.register(ConditionEvaluationReportLoggingListenerTests.Config.class);
        context.refresh();
        withDebugLogging(() -> this.initializer.onApplicationEvent(new org.springframework.context.event.ContextRefreshedEvent(context)));
        assertThat(this.output.toString()).contains("CONDITIONS EVALUATION REPORT");
    }

    @Test
    public void logsDebugOnError() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        this.initializer.initialize(context);
        context.register(ConditionEvaluationReportLoggingListenerTests.ErrorConfig.class);
        assertThatExceptionOfType(Exception.class).isThrownBy(context::refresh).satisfies(( ex) -> withDebugLogging(() -> this.initializer.onApplicationEvent(new ApplicationFailedEvent(new SpringApplication(), new String[0], context, ex))));
        assertThat(this.output.toString()).contains("CONDITIONS EVALUATION REPORT");
    }

    @Test
    public void logsInfoOnErrorIfDebugDisabled() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        this.initializer.initialize(context);
        context.register(ConditionEvaluationReportLoggingListenerTests.ErrorConfig.class);
        assertThatExceptionOfType(Exception.class).isThrownBy(context::refresh).satisfies(( ex) -> this.initializer.onApplicationEvent(new ApplicationFailedEvent(new SpringApplication(), new String[0], context, ex)));
        assertThat(this.output.toString()).contains(("Error starting" + (" ApplicationContext. To display the conditions report re-run" + " your application with 'debug' enabled.")));
    }

    @Test
    public void logsOutput() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        this.initializer.initialize(context);
        context.register(ConditionEvaluationReportLoggingListenerTests.Config.class);
        ConditionEvaluationReport.get(context.getBeanFactory()).recordExclusions(Arrays.asList("com.foo.Bar"));
        context.refresh();
        withDebugLogging(() -> this.initializer.onApplicationEvent(new org.springframework.context.event.ContextRefreshedEvent(context)));
        assertThat(this.output.toString()).contains("not a servlet web application (OnWebApplicationCondition)");
    }

    @Test
    public void canBeUsedInApplicationContext() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(ConditionEvaluationReportLoggingListenerTests.Config.class);
        new ConditionEvaluationReportLoggingListener().initialize(context);
        context.refresh();
        assertThat(context.getBean(ConditionEvaluationReport.class)).isNotNull();
    }

    @Test
    public void canBeUsedInNonGenericApplicationContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.register(ConditionEvaluationReportLoggingListenerTests.Config.class);
        new ConditionEvaluationReportLoggingListener().initialize(context);
        context.refresh();
        assertThat(context.getBean(ConditionEvaluationReport.class)).isNotNull();
    }

    @Test
    public void listenerWithInfoLevelShouldLogAtInfo() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        ConditionEvaluationReportLoggingListener initializer = new ConditionEvaluationReportLoggingListener(LogLevel.INFO);
        initializer.initialize(context);
        context.register(ConditionEvaluationReportLoggingListenerTests.Config.class);
        context.refresh();
        initializer.onApplicationEvent(new org.springframework.context.event.ContextRefreshedEvent(context));
        assertThat(this.output.toString()).contains("CONDITIONS EVALUATION REPORT");
    }

    @Test
    public void listenerSupportsOnlyInfoAndDebug() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ConditionEvaluationReportLoggingListener(LogLevel.TRACE)).withMessageContaining("LogLevel must be INFO or DEBUG");
    }

    @Test
    public void noErrorIfNotInitialized() {
        this.initializer.onApplicationEvent(new ApplicationFailedEvent(new SpringApplication(), new String[0], null, new RuntimeException("Planned")));
        assertThat(this.output.toString()).contains("Unable to provide the conditions report");
    }

    @Configuration
    @Import({ WebMvcAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class })
    static class Config {}

    @Configuration
    @Import(WebMvcAutoConfiguration.class)
    static class ErrorConfig {
        @Bean
        public String iBreak() {
            throw new RuntimeException();
        }
    }
}

