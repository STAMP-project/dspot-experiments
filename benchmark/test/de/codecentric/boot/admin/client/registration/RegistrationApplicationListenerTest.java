/**
 * Copyright 2014-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codecentric.boot.admin.client.registration;


import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;


public class RegistrationApplicationListenerTest {
    @Test
    public void should_schedule_register_task() {
        ApplicationRegistrator registrator = Mockito.mock(ApplicationRegistrator.class);
        ThreadPoolTaskScheduler scheduler = Mockito.mock(ThreadPoolTaskScheduler.class);
        RegistrationApplicationListener listener = new RegistrationApplicationListener(registrator, scheduler);
        listener.onApplicationReady(new ApplicationReadyEvent(Mockito.mock(SpringApplication.class), null, Mockito.mock(ConfigurableWebApplicationContext.class)));
        Mockito.verify(scheduler).scheduleAtFixedRate(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.eq(Duration.ofSeconds(10)));
    }

    @Test
    public void should_no_schedule_register_task_when_not_autoRegister() {
        ApplicationRegistrator registrator = Mockito.mock(ApplicationRegistrator.class);
        ThreadPoolTaskScheduler scheduler = Mockito.mock(ThreadPoolTaskScheduler.class);
        RegistrationApplicationListener listener = new RegistrationApplicationListener(registrator, scheduler);
        listener.setAutoRegister(false);
        listener.onApplicationReady(new ApplicationReadyEvent(Mockito.mock(SpringApplication.class), null, Mockito.mock(ConfigurableWebApplicationContext.class)));
        Mockito.verify(scheduler, Mockito.never()).scheduleAtFixedRate(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.eq(Duration.ofSeconds(10)));
    }

    @Test
    public void should_cancel_register_task_on_context_close() {
        ApplicationRegistrator registrator = Mockito.mock(ApplicationRegistrator.class);
        ThreadPoolTaskScheduler scheduler = Mockito.mock(ThreadPoolTaskScheduler.class);
        RegistrationApplicationListener listener = new RegistrationApplicationListener(registrator, scheduler);
        ScheduledFuture<?> task = Mockito.mock(ScheduledFuture.class);
        Mockito.when(scheduler.scheduleAtFixedRate(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.eq(Duration.ofSeconds(10)))).then(( invocation) -> task);
        listener.onApplicationReady(new ApplicationReadyEvent(Mockito.mock(SpringApplication.class), null, Mockito.mock(ConfigurableWebApplicationContext.class)));
        Mockito.verify(scheduler).scheduleAtFixedRate(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.eq(Duration.ofSeconds(10)));
        listener.onClosedContext(new ContextClosedEvent(Mockito.mock(WebApplicationContext.class)));
        Mockito.verify(task).cancel(true);
    }

    @Test
    public void should_start_and_cancel_task_on_request() {
        ApplicationRegistrator registrator = Mockito.mock(ApplicationRegistrator.class);
        ThreadPoolTaskScheduler scheduler = Mockito.mock(ThreadPoolTaskScheduler.class);
        RegistrationApplicationListener listener = new RegistrationApplicationListener(registrator, scheduler);
        ScheduledFuture<?> task = Mockito.mock(ScheduledFuture.class);
        Mockito.when(scheduler.scheduleAtFixedRate(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.eq(Duration.ofSeconds(10)))).then(( invocation) -> task);
        listener.startRegisterTask();
        Mockito.verify(scheduler).scheduleAtFixedRate(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.eq(Duration.ofSeconds(10)));
        listener.stopRegisterTask();
        Mockito.verify(task).cancel(true);
    }

    @Test
    public void should_not_deregister_when_not_autoDeregister() {
        ApplicationRegistrator registrator = Mockito.mock(ApplicationRegistrator.class);
        ThreadPoolTaskScheduler scheduler = Mockito.mock(ThreadPoolTaskScheduler.class);
        RegistrationApplicationListener listener = new RegistrationApplicationListener(registrator, scheduler);
        listener.onClosedContext(new ContextClosedEvent(Mockito.mock(WebApplicationContext.class)));
        Mockito.verify(registrator, Mockito.never()).deregister();
    }

    @Test
    public void should_deregister_when_autoDeregister() {
        ApplicationRegistrator registrator = Mockito.mock(ApplicationRegistrator.class);
        ThreadPoolTaskScheduler scheduler = Mockito.mock(ThreadPoolTaskScheduler.class);
        RegistrationApplicationListener listener = new RegistrationApplicationListener(registrator, scheduler);
        listener.setAutoDeregister(true);
        listener.onClosedContext(new ContextClosedEvent(Mockito.mock(ApplicationContext.class)));
        Mockito.verify(registrator).deregister();
    }

    @Test
    public void should_deregister_when_autoDeregister_and_parent_is_bootstrap_contex() {
        ApplicationRegistrator registrator = Mockito.mock(ApplicationRegistrator.class);
        ThreadPoolTaskScheduler scheduler = Mockito.mock(ThreadPoolTaskScheduler.class);
        RegistrationApplicationListener listener = new RegistrationApplicationListener(registrator, scheduler);
        listener.setAutoDeregister(true);
        ApplicationContext parentContext = Mockito.mock(ApplicationContext.class);
        Mockito.when(parentContext.getId()).thenReturn("bootstrap");
        ApplicationContext mockContext = Mockito.mock(ApplicationContext.class);
        Mockito.when(mockContext.getParent()).thenReturn(parentContext);
        listener.onClosedContext(new ContextClosedEvent(mockContext));
        Mockito.verify(registrator).deregister();
    }

    @Test
    public void should_not_deregister_when_autoDeregister_and_not_root() {
        ApplicationRegistrator registrator = Mockito.mock(ApplicationRegistrator.class);
        ThreadPoolTaskScheduler scheduler = Mockito.mock(ThreadPoolTaskScheduler.class);
        RegistrationApplicationListener listener = new RegistrationApplicationListener(registrator, scheduler);
        listener.setAutoDeregister(true);
        ApplicationContext mockContext = Mockito.mock(ApplicationContext.class);
        Mockito.when(mockContext.getParent()).thenReturn(Mockito.mock(ApplicationContext.class));
        listener.onClosedContext(new ContextClosedEvent(mockContext));
        Mockito.verify(registrator, Mockito.never()).deregister();
    }

    @Test
    public void should_init_and_shutdown_taskScheduler() {
        ApplicationRegistrator registrator = Mockito.mock(ApplicationRegistrator.class);
        ThreadPoolTaskScheduler scheduler = Mockito.mock(ThreadPoolTaskScheduler.class);
        RegistrationApplicationListener listener = new RegistrationApplicationListener(registrator, scheduler);
        listener.afterPropertiesSet();
        Mockito.verify(scheduler, Mockito.times(1)).afterPropertiesSet();
        listener.destroy();
        Mockito.verify(scheduler, Mockito.times(1)).destroy();
    }
}

