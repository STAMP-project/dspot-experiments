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
package org.springframework.boot.actuate.metrics.export.prometheus;


import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.PushGateway;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusPushGatewayManager.PushGatewayTaskScheduler;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusPushGatewayManager.ShutdownOperation;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;


/**
 * Tests for {@link PrometheusPushGatewayManager}.
 *
 * @author Phillip Webb
 */
public class PrometheusPushGatewayManagerTests {
    @Mock
    private PushGateway pushGateway;

    @Mock
    private CollectorRegistry registry;

    private TaskScheduler scheduler;

    private Duration pushRate = Duration.ofSeconds(1);

    private Map<String, String> groupingKey = Collections.singletonMap("foo", "bar");

    @Captor
    private ArgumentCaptor<Runnable> task;

    @Mock
    private ScheduledFuture<Object> future;

    @Test
    public void createWhenPushGatewayIsNullThrowsException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new PrometheusPushGatewayManager(null, this.registry, this.scheduler, this.pushRate, "job", this.groupingKey, null)).withMessage("PushGateway must not be null");
    }

    @Test
    public void createWhenCollectorRegistryIsNullThrowsException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new PrometheusPushGatewayManager(this.pushGateway, null, this.scheduler, this.pushRate, "job", this.groupingKey, null)).withMessage("Registry must not be null");
    }

    @Test
    public void createWhenSchedulerIsNullThrowsException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new PrometheusPushGatewayManager(this.pushGateway, this.registry, null, this.pushRate, "job", this.groupingKey, null)).withMessage("Scheduler must not be null");
    }

    @Test
    public void createWhenPushRateIsNullThrowsException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new PrometheusPushGatewayManager(this.pushGateway, this.registry, this.scheduler, null, "job", this.groupingKey, null)).withMessage("PushRate must not be null");
    }

    @Test
    public void createWhenJobIsEmptyThrowsException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new PrometheusPushGatewayManager(this.pushGateway, this.registry, this.scheduler, this.pushRate, "", this.groupingKey, null)).withMessage("Job must not be empty");
    }

    @Test
    public void createShouldSchedulePushAsFixedRate() throws Exception {
        new PrometheusPushGatewayManager(this.pushGateway, this.registry, this.scheduler, this.pushRate, "job", this.groupingKey, null);
        Mockito.verify(this.scheduler).scheduleAtFixedRate(this.task.capture(), ArgumentMatchers.eq(this.pushRate));
        this.task.getValue().run();
        Mockito.verify(this.pushGateway).pushAdd(this.registry, "job", this.groupingKey);
    }

    @Test
    public void shutdownWhenOwnsSchedulerDoesShutdownScheduler() {
        PushGatewayTaskScheduler ownedScheduler = mockScheduler(PushGatewayTaskScheduler.class);
        PrometheusPushGatewayManager manager = new PrometheusPushGatewayManager(this.pushGateway, this.registry, ownedScheduler, this.pushRate, "job", this.groupingKey, null);
        manager.shutdown();
        Mockito.verify(ownedScheduler).shutdown();
    }

    @Test
    public void shutdownWhenDoesNotOwnSchedulerDoesNotShutdownScheduler() {
        ThreadPoolTaskScheduler otherScheduler = mockScheduler(ThreadPoolTaskScheduler.class);
        PrometheusPushGatewayManager manager = new PrometheusPushGatewayManager(this.pushGateway, this.registry, otherScheduler, this.pushRate, "job", this.groupingKey, null);
        manager.shutdown();
        Mockito.verify(otherScheduler, Mockito.never()).shutdown();
    }

    @Test
    public void shutdownWhenShutdownOperationIsPushPerformsPushOnShutdown() throws Exception {
        PrometheusPushGatewayManager manager = new PrometheusPushGatewayManager(this.pushGateway, this.registry, this.scheduler, this.pushRate, "job", this.groupingKey, ShutdownOperation.PUSH);
        manager.shutdown();
        Mockito.verify(this.future).cancel(false);
        Mockito.verify(this.pushGateway).pushAdd(this.registry, "job", this.groupingKey);
    }

    @Test
    public void shutdownWhenShutdownOperationIsDeletePerformsDeleteOnShutdown() throws Exception {
        PrometheusPushGatewayManager manager = new PrometheusPushGatewayManager(this.pushGateway, this.registry, this.scheduler, this.pushRate, "job", this.groupingKey, ShutdownOperation.DELETE);
        manager.shutdown();
        Mockito.verify(this.future).cancel(false);
        Mockito.verify(this.pushGateway).delete("job", this.groupingKey);
    }

    @Test
    public void shutdownWhenShutdownOperationIsNoneDoesNothing() {
        PrometheusPushGatewayManager manager = new PrometheusPushGatewayManager(this.pushGateway, this.registry, this.scheduler, this.pushRate, "job", this.groupingKey, ShutdownOperation.NONE);
        manager.shutdown();
        Mockito.verify(this.future).cancel(false);
        Mockito.verifyZeroInteractions(this.pushGateway);
    }

    @Test
    public void pushWhenUnknownHostExceptionIsThrownDoesShutdown() throws Exception {
        new PrometheusPushGatewayManager(this.pushGateway, this.registry, this.scheduler, this.pushRate, "job", this.groupingKey, null);
        Mockito.verify(this.scheduler).scheduleAtFixedRate(this.task.capture(), ArgumentMatchers.eq(this.pushRate));
        BDDMockito.willThrow(new UnknownHostException("foo")).given(this.pushGateway).pushAdd(this.registry, "job", this.groupingKey);
        this.task.getValue().run();
        Mockito.verify(this.future).cancel(false);
    }

    @Test
    public void pushDoesNotThrowException() throws Exception {
        new PrometheusPushGatewayManager(this.pushGateway, this.registry, this.scheduler, this.pushRate, "job", this.groupingKey, null);
        Mockito.verify(this.scheduler).scheduleAtFixedRate(this.task.capture(), ArgumentMatchers.eq(this.pushRate));
        BDDMockito.willThrow(RuntimeException.class).given(this.pushGateway).pushAdd(this.registry, "job", this.groupingKey);
        this.task.getValue().run();
    }
}

