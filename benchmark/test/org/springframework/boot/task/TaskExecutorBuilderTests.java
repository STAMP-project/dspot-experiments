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
package org.springframework.boot.task;


import java.time.Duration;
import java.util.Collections;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * Tests for {@link TaskExecutorBuilder}.
 *
 * @author Stephane Nicoll
 * @author Filip Hrisafov
 */
public class TaskExecutorBuilderTests {
    private TaskExecutorBuilder builder = new TaskExecutorBuilder();

    @Test
    public void poolSettingsShouldApply() {
        ThreadPoolTaskExecutor executor = this.builder.queueCapacity(10).corePoolSize(4).maxPoolSize(8).allowCoreThreadTimeOut(true).keepAlive(Duration.ofMinutes(1)).build();
        assertThat(executor).hasFieldOrPropertyWithValue("queueCapacity", 10);
        assertThat(executor.getCorePoolSize()).isEqualTo(4);
        assertThat(executor.getMaxPoolSize()).isEqualTo(8);
        assertThat(executor).hasFieldOrPropertyWithValue("allowCoreThreadTimeOut", true);
        assertThat(executor.getKeepAliveSeconds()).isEqualTo(60);
    }

    @Test
    public void awaitTerminationShouldApply() {
        ThreadPoolTaskExecutor executor = this.builder.awaitTermination(true).build();
        assertThat(executor).hasFieldOrPropertyWithValue("waitForTasksToCompleteOnShutdown", true);
    }

    @Test
    public void awaitTerminationPeriodShouldApply() {
        ThreadPoolTaskExecutor executor = this.builder.awaitTerminationPeriod(Duration.ofMinutes(1)).build();
        assertThat(executor).hasFieldOrPropertyWithValue("awaitTerminationSeconds", 60);
    }

    @Test
    public void threadNamePrefixShouldApply() {
        ThreadPoolTaskExecutor executor = this.builder.threadNamePrefix("test-").build();
        assertThat(executor.getThreadNamePrefix()).isEqualTo("test-");
    }

    @Test
    public void taskDecoratorShouldApply() {
        TaskDecorator taskDecorator = Mockito.mock(TaskDecorator.class);
        ThreadPoolTaskExecutor executor = this.builder.taskDecorator(taskDecorator).build();
        assertThat(ReflectionTestUtils.getField(executor, "taskDecorator")).isSameAs(taskDecorator);
    }

    @Test
    public void customizersWhenCustomizersAreNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.builder.customizers(((TaskExecutorCustomizer[]) (null)))).withMessageContaining("Customizers must not be null");
    }

    @Test
    public void customizersCollectionWhenCustomizersAreNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.builder.customizers(((Set<TaskExecutorCustomizer>) (null)))).withMessageContaining("Customizers must not be null");
    }

    @Test
    public void customizersShouldApply() {
        TaskExecutorCustomizer customizer = Mockito.mock(TaskExecutorCustomizer.class);
        ThreadPoolTaskExecutor executor = this.builder.customizers(customizer).build();
        Mockito.verify(customizer).customize(executor);
    }

    @Test
    public void customizersShouldBeAppliedLast() {
        TaskDecorator taskDecorator = Mockito.mock(TaskDecorator.class);
        ThreadPoolTaskExecutor executor = Mockito.spy(new ThreadPoolTaskExecutor());
        this.builder.queueCapacity(10).corePoolSize(4).maxPoolSize(8).allowCoreThreadTimeOut(true).keepAlive(Duration.ofMinutes(1)).awaitTermination(true).awaitTerminationPeriod(Duration.ofSeconds(30)).threadNamePrefix("test-").taskDecorator(taskDecorator).additionalCustomizers(( taskExecutor) -> {
            verify(taskExecutor).setQueueCapacity(10);
            verify(taskExecutor).setCorePoolSize(4);
            verify(taskExecutor).setMaxPoolSize(8);
            verify(taskExecutor).setAllowCoreThreadTimeOut(true);
            verify(taskExecutor).setKeepAliveSeconds(60);
            verify(taskExecutor).setWaitForTasksToCompleteOnShutdown(true);
            verify(taskExecutor).setAwaitTerminationSeconds(30);
            verify(taskExecutor).setThreadNamePrefix("test-");
            verify(taskExecutor).setTaskDecorator(taskDecorator);
        });
        this.builder.configure(executor);
    }

    @Test
    public void customizersShouldReplaceExisting() {
        TaskExecutorCustomizer customizer1 = Mockito.mock(TaskExecutorCustomizer.class);
        TaskExecutorCustomizer customizer2 = Mockito.mock(TaskExecutorCustomizer.class);
        ThreadPoolTaskExecutor executor = this.builder.customizers(customizer1).customizers(Collections.singleton(customizer2)).build();
        Mockito.verifyZeroInteractions(customizer1);
        Mockito.verify(customizer2).customize(executor);
    }

    @Test
    public void additionalCustomizersWhenCustomizersAreNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.builder.additionalCustomizers(((TaskExecutorCustomizer[]) (null)))).withMessageContaining("Customizers must not be null");
    }

    @Test
    public void additionalCustomizersCollectionWhenCustomizersAreNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.builder.additionalCustomizers(((Set<TaskExecutorCustomizer>) (null)))).withMessageContaining("Customizers must not be null");
    }

    @Test
    public void additionalCustomizersShouldAddToExisting() {
        TaskExecutorCustomizer customizer1 = Mockito.mock(TaskExecutorCustomizer.class);
        TaskExecutorCustomizer customizer2 = Mockito.mock(TaskExecutorCustomizer.class);
        ThreadPoolTaskExecutor executor = this.builder.customizers(customizer1).additionalCustomizers(customizer2).build();
        Mockito.verify(customizer1).customize(executor);
        Mockito.verify(customizer2).customize(executor);
    }
}

