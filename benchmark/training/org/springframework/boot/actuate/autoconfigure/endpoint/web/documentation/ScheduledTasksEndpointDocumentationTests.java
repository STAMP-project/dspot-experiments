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
package org.springframework.boot.actuate.autoconfigure.endpoint.web.documentation;


import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;
import org.junit.Test;
import org.springframework.boot.actuate.scheduling.ScheduledTasksEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;


/**
 * Tests for generating documentation describing the {@link ScheduledTasksEndpoint}.
 *
 * @author Andy Wilkinson
 */
public class ScheduledTasksEndpointDocumentationTests extends MockMvcEndpointDocumentationTests {
    @Test
    public void scheduledTasks() throws Exception {
        this.mockMvc.perform(get("/actuator/scheduledtasks")).andExpect(status().isOk()).andDo(document("scheduled-tasks", preprocessResponse(replacePattern(Pattern.compile(("org.*\\.ScheduledTasksEndpointDocumentationTests\\$" + "TestConfiguration")), "com.example.Processor")), responseFields(fieldWithPath("cron").description("Cron tasks, if any."), targetFieldWithPrefix("cron.[]."), fieldWithPath("cron.[].expression").description("Cron expression."), fieldWithPath("fixedDelay").description("Fixed delay tasks, if any."), targetFieldWithPrefix("fixedDelay.[]."), initialDelayWithPrefix("fixedDelay.[]."), fieldWithPath("fixedDelay.[].interval").description(("Interval, in milliseconds, between the end of the last" + " execution and the start of the next.")), fieldWithPath("fixedRate").description("Fixed rate tasks, if any."), targetFieldWithPrefix("fixedRate.[]."), fieldWithPath("fixedRate.[].interval").description("Interval, in milliseconds, between the start of each execution."), initialDelayWithPrefix("fixedRate.[]."), fieldWithPath("custom").description("Tasks with custom triggers, if any."), targetFieldWithPrefix("custom.[]."), fieldWithPath("custom.[].trigger").description("Trigger for the task.")))).andDo(MockMvcResultHandlers.print());
    }

    @Configuration
    @EnableScheduling
    @Import(AbstractEndpointDocumentationTests.BaseDocumentationConfiguration.class)
    static class TestConfiguration {
        @Bean
        public ScheduledTasksEndpoint endpoint(Collection<ScheduledTaskHolder> holders) {
            return new ScheduledTasksEndpoint(holders);
        }

        @Scheduled(cron = "0 0 0/3 1/1 * ?")
        public void processOrders() {
        }

        @Scheduled(fixedDelay = 5000, initialDelay = 5000)
        public void purge() {
        }

        @Scheduled(fixedRate = 3000, initialDelay = 10000)
        public void retrieveIssues() {
        }

        @Bean
        public SchedulingConfigurer schedulingConfigurer() {
            return ( registrar) -> registrar.addTriggerTask(new org.springframework.boot.actuate.autoconfigure.endpoint.web.documentation.CustomTriggeredRunnable(), new org.springframework.boot.actuate.autoconfigure.endpoint.web.documentation.CustomTrigger());
        }

        static class CustomTrigger implements Trigger {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                return new Date();
            }
        }

        static class CustomTriggeredRunnable implements Runnable {
            @Override
            public void run() {
            }
        }
    }
}

