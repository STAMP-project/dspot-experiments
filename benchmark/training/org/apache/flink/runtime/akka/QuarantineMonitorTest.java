/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.akka;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.UntypedActor;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.flink.runtime.testingUtils.TestingUtils;
import org.apache.flink.util.Preconditions;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.FiniteDuration;


/**
 * Tests for {@link QuarantineMonitor}.
 */
public class QuarantineMonitorTest extends TestLogger {
    private static final Logger LOG = LoggerFactory.getLogger(QuarantineMonitorTest.class);

    private static final FiniteDuration zeroDelay = new FiniteDuration(0L, TimeUnit.SECONDS);

    // we need two actor systems because we're quarantining one of them
    private static ActorSystem actorSystem1;

    private ActorSystem actorSystem2;

    /**
     * Tests that the quarantine monitor detects if an actor system has been quarantined by another
     * actor system.
     */
    @Test(timeout = 5000L)
    public void testWatcheeQuarantined() throws InterruptedException, ExecutionException {
        QuarantineMonitorTest.TestingQuarantineHandler quarantineHandler = new QuarantineMonitorTest.TestingQuarantineHandler();
        ActorRef watchee = null;
        ActorRef watcher = null;
        ActorRef monitor = null;
        FiniteDuration timeout = new FiniteDuration(5, TimeUnit.SECONDS);
        FiniteDuration interval = new FiniteDuration(200, TimeUnit.MILLISECONDS);
        try {
            // start the quarantine monitor in the watchee actor system
            monitor = actorSystem2.actorOf(QuarantineMonitorTest.getQuarantineMonitorProps(quarantineHandler), "quarantineMonitor");
            watchee = actorSystem2.actorOf(QuarantineMonitorTest.getWatcheeProps(timeout, interval, quarantineHandler), "watchee");
            watcher = QuarantineMonitorTest.actorSystem1.actorOf(QuarantineMonitorTest.getWatcherProps(timeout, interval, quarantineHandler), "watcher");
            final Address actorSystem1Address = AkkaUtils.getAddress(QuarantineMonitorTest.actorSystem1);
            final String watcheeAddress = AkkaUtils.getAkkaURL(actorSystem2, watchee);
            final String watcherAddress = AkkaUtils.getAkkaURL(QuarantineMonitorTest.actorSystem1, watcher);
            // ping the watcher continuously
            watchee.tell(new QuarantineMonitorTest.Ping(watcherAddress), ActorRef.noSender());
            // start watching the watchee
            watcher.tell(new QuarantineMonitorTest.Watch(watcheeAddress), ActorRef.noSender());
            CompletableFuture<String> quarantineFuture = quarantineHandler.getWasQuarantinedByFuture();
            Assert.assertEquals(actorSystem1Address.toString(), quarantineFuture.get());
        } finally {
            TestingUtils.stopActor(watchee);
            TestingUtils.stopActor(watcher);
            TestingUtils.stopActor(monitor);
        }
    }

    /**
     * Tests that the quarantine monitor detects if an actor system quarantines another actor
     * system.
     */
    @Test(timeout = 5000L)
    public void testWatcherQuarantining() throws InterruptedException, ExecutionException {
        QuarantineMonitorTest.TestingQuarantineHandler quarantineHandler = new QuarantineMonitorTest.TestingQuarantineHandler();
        ActorRef watchee = null;
        ActorRef watcher = null;
        ActorRef monitor = null;
        FiniteDuration timeout = new FiniteDuration(5, TimeUnit.SECONDS);
        FiniteDuration interval = new FiniteDuration(200, TimeUnit.MILLISECONDS);
        try {
            // start the quarantine monitor in the watcher actor system
            monitor = QuarantineMonitorTest.actorSystem1.actorOf(QuarantineMonitorTest.getQuarantineMonitorProps(quarantineHandler), "quarantineMonitor");
            watchee = actorSystem2.actorOf(QuarantineMonitorTest.getWatcheeProps(timeout, interval, quarantineHandler), "watchee");
            watcher = QuarantineMonitorTest.actorSystem1.actorOf(QuarantineMonitorTest.getWatcherProps(timeout, interval, quarantineHandler), "watcher");
            final Address actorSystem1Address = AkkaUtils.getAddress(actorSystem2);
            final String watcheeAddress = AkkaUtils.getAkkaURL(actorSystem2, watchee);
            final String watcherAddress = AkkaUtils.getAkkaURL(QuarantineMonitorTest.actorSystem1, watcher);
            // ping the watcher continuously
            watchee.tell(new QuarantineMonitorTest.Ping(watcherAddress), ActorRef.noSender());
            // start watching the watchee
            watcher.tell(new QuarantineMonitorTest.Watch(watcheeAddress), ActorRef.noSender());
            CompletableFuture<String> quarantineFuture = quarantineHandler.getHasQuarantinedFuture();
            Assert.assertEquals(actorSystem1Address.toString(), quarantineFuture.get());
        } finally {
            TestingUtils.stopActor(watchee);
            TestingUtils.stopActor(watcher);
            TestingUtils.stopActor(monitor);
        }
    }

    private static class TestingQuarantineHandler implements QuarantineHandler , QuarantineMonitorTest.ErrorHandler {
        private final CompletableFuture<String> wasQuarantinedByFuture;

        private final CompletableFuture<String> hasQuarantinedFuture;

        public TestingQuarantineHandler() {
            this.wasQuarantinedByFuture = new CompletableFuture<>();
            this.hasQuarantinedFuture = new CompletableFuture<>();
        }

        @Override
        public void wasQuarantinedBy(String remoteSystem, ActorSystem actorSystem) {
            wasQuarantinedByFuture.complete(remoteSystem);
        }

        @Override
        public void hasQuarantined(String remoteSystem, ActorSystem actorSystem) {
            hasQuarantinedFuture.complete(remoteSystem);
        }

        public CompletableFuture<String> getWasQuarantinedByFuture() {
            return wasQuarantinedByFuture;
        }

        public CompletableFuture<String> getHasQuarantinedFuture() {
            return hasQuarantinedFuture;
        }

        @Override
        public void handleError(Throwable failure) {
            wasQuarantinedByFuture.completeExceptionally(failure);
            hasQuarantinedFuture.completeExceptionally(failure);
        }
    }

    private interface ErrorHandler {
        void handleError(Throwable failure);
    }

    static class Watcher extends UntypedActor {
        private final FiniteDuration timeout;

        private final FiniteDuration interval;

        private final QuarantineMonitorTest.ErrorHandler errorHandler;

        Watcher(FiniteDuration timeout, FiniteDuration interval, QuarantineMonitorTest.ErrorHandler errorHandler) {
            this.timeout = Preconditions.checkNotNull(timeout);
            this.interval = Preconditions.checkNotNull(interval);
            this.errorHandler = Preconditions.checkNotNull(errorHandler);
        }

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof QuarantineMonitorTest.Watch) {
                QuarantineMonitorTest.Watch watch = ((QuarantineMonitorTest.Watch) (message));
                getContext().actorSelection(watch.getTarget()).resolveOne(timeout).onComplete(new akka.dispatch.OnComplete<ActorRef>() {
                    @Override
                    public void onComplete(Throwable failure, ActorRef success) throws Throwable {
                        if (success != null) {
                            getContext().watch(success);
                            // constantly ping the watchee
                            getContext().system().scheduler().schedule(QuarantineMonitorTest.zeroDelay, interval, success, "Watcher message", getContext().dispatcher(), getSelf());
                        } else {
                            errorHandler.handleError(failure);
                        }
                    }
                }, getContext().dispatcher());
            }
        }
    }

    static class Watchee extends UntypedActor {
        private final FiniteDuration timeout;

        private final FiniteDuration interval;

        private final QuarantineMonitorTest.ErrorHandler errorHandler;

        Watchee(FiniteDuration timeout, FiniteDuration interval, QuarantineMonitorTest.ErrorHandler errorHandler) {
            this.timeout = Preconditions.checkNotNull(timeout);
            this.interval = Preconditions.checkNotNull(interval);
            this.errorHandler = Preconditions.checkNotNull(errorHandler);
        }

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof QuarantineMonitorTest.Ping) {
                final QuarantineMonitorTest.Ping ping = ((QuarantineMonitorTest.Ping) (message));
                getContext().actorSelection(ping.getTarget()).resolveOne(timeout).onComplete(new akka.dispatch.OnComplete<ActorRef>() {
                    @Override
                    public void onComplete(Throwable failure, ActorRef success) throws Throwable {
                        if (success != null) {
                            // constantly ping the target
                            getContext().system().scheduler().schedule(QuarantineMonitorTest.zeroDelay, interval, success, "Watchee message", getContext().dispatcher(), getSelf());
                        } else {
                            errorHandler.handleError(failure);
                        }
                    }
                }, getContext().dispatcher());
            }
        }
    }

    static class Watch {
        private final String target;

        Watch(String target) {
            this.target = target;
        }

        public String getTarget() {
            return target;
        }
    }

    static class Ping {
        private final String target;

        Ping(String target) {
            this.target = target;
        }

        public String getTarget() {
            return target;
        }
    }
}

