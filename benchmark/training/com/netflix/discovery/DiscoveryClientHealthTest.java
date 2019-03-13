package com.netflix.discovery;


import InstanceInfo.InstanceStatus;
import InstanceInfo.InstanceStatus.DOWN;
import InstanceInfo.InstanceStatus.OUT_OF_SERVICE;
import InstanceInfo.InstanceStatus.STARTING;
import InstanceInfo.InstanceStatus.UP;
import com.netflix.appinfo.HealthCheckCallback;
import com.netflix.appinfo.HealthCheckHandler;
import com.netflix.appinfo.InstanceInfo;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Nitesh Kant
 */
public class DiscoveryClientHealthTest extends AbstractDiscoveryClientTester {
    @Test
    public void testCallback() throws Exception {
        DiscoveryClientHealthTest.MyHealthCheckCallback myCallback = new DiscoveryClientHealthTest.MyHealthCheckCallback(true);
        Assert.assertTrue(((client) instanceof DiscoveryClient));
        DiscoveryClient clientImpl = ((DiscoveryClient) (client));
        InstanceInfoReplicator instanceInfoReplicator = clientImpl.getInstanceInfoReplicator();
        instanceInfoReplicator.run();
        Assert.assertEquals("Instance info status not as expected.", STARTING, clientImpl.getInstanceInfo().getStatus());
        Assert.assertFalse("Healthcheck callback invoked when status is STARTING.", myCallback.isInvoked());
        client.registerHealthCheckCallback(myCallback);
        clientImpl.getInstanceInfo().setStatus(OUT_OF_SERVICE);
        Assert.assertEquals("Instance info status not as expected.", OUT_OF_SERVICE, clientImpl.getInstanceInfo().getStatus());
        myCallback.reset();
        instanceInfoReplicator.run();
        Assert.assertFalse("Healthcheck callback invoked when status is OOS.", myCallback.isInvoked());
        clientImpl.getInstanceInfo().setStatus(DOWN);
        Assert.assertEquals("Instance info status not as expected.", DOWN, clientImpl.getInstanceInfo().getStatus());
        myCallback.reset();
        instanceInfoReplicator.run();
        Assert.assertTrue("Healthcheck callback not invoked.", myCallback.isInvoked());
        Assert.assertEquals("Instance info status not as expected.", UP, clientImpl.getInstanceInfo().getStatus());
    }

    @Test
    public void testHandler() throws Exception {
        DiscoveryClientHealthTest.MyHealthCheckHandler myHealthCheckHandler = new DiscoveryClientHealthTest.MyHealthCheckHandler(InstanceStatus.UP);
        Assert.assertTrue(((client) instanceof DiscoveryClient));
        DiscoveryClient clientImpl = ((DiscoveryClient) (client));
        InstanceInfoReplicator instanceInfoReplicator = clientImpl.getInstanceInfoReplicator();
        Assert.assertEquals("Instance info status not as expected.", STARTING, clientImpl.getInstanceInfo().getStatus());
        client.registerHealthCheck(myHealthCheckHandler);
        instanceInfoReplicator.run();
        Assert.assertTrue("Healthcheck callback not invoked when status is STARTING.", myHealthCheckHandler.isInvoked());
        Assert.assertEquals("Instance info status not as expected post healthcheck.", UP, clientImpl.getInstanceInfo().getStatus());
        clientImpl.getInstanceInfo().setStatus(OUT_OF_SERVICE);
        Assert.assertEquals("Instance info status not as expected.", OUT_OF_SERVICE, clientImpl.getInstanceInfo().getStatus());
        myHealthCheckHandler.reset();
        instanceInfoReplicator.run();
        Assert.assertTrue("Healthcheck callback not invoked when status is OUT_OF_SERVICE.", myHealthCheckHandler.isInvoked());
        Assert.assertEquals("Instance info status not as expected post healthcheck.", UP, clientImpl.getInstanceInfo().getStatus());
        clientImpl.getInstanceInfo().setStatus(DOWN);
        Assert.assertEquals("Instance info status not as expected.", DOWN, clientImpl.getInstanceInfo().getStatus());
        myHealthCheckHandler.reset();
        instanceInfoReplicator.run();
        Assert.assertTrue("Healthcheck callback not invoked when status is DOWN.", myHealthCheckHandler.isInvoked());
        Assert.assertEquals("Instance info status not as expected post healthcheck.", UP, clientImpl.getInstanceInfo().getStatus());
        clientImpl.getInstanceInfo().setStatus(UP);
        myHealthCheckHandler.reset();
        myHealthCheckHandler.shouldException = true;
        instanceInfoReplicator.run();
        Assert.assertTrue("Healthcheck callback not invoked when status is UP.", myHealthCheckHandler.isInvoked());
        Assert.assertEquals("Instance info status not as expected post healthcheck.", DOWN, clientImpl.getInstanceInfo().getStatus());
    }

    @Test
    public void shouldRegisterHealthCheckHandlerInConcurrentEnvironment() throws Exception {
        HealthCheckHandler myHealthCheckHandler = new DiscoveryClientHealthTest.MyHealthCheckHandler(InstanceStatus.UP);
        int testsCount = 20;
        int threadsCount = testsCount * 2;
        CountDownLatch starterLatch = new CountDownLatch(threadsCount);
        CountDownLatch finishLatch = new CountDownLatch(threadsCount);
        List<DiscoveryClient> discoveryClients = IntStream.range(0, testsCount).mapToObj(( i) -> ((DiscoveryClient) (getSetupDiscoveryClient()))).collect(Collectors.toList());
        Stream<Thread> registerCustomHandlerThreads = discoveryClients.stream().map(( client) -> new com.netflix.discovery.SimultaneousStarter(starterLatch, finishLatch, () -> client.registerHealthCheck(myHealthCheckHandler)));
        Stream<Thread> lazyInitOfDefaultHandlerThreads = discoveryClients.stream().map(( client) -> new com.netflix.discovery.SimultaneousStarter(starterLatch, finishLatch, client::getHealthCheckHandler));
        List<Thread> threads = Stream.concat(registerCustomHandlerThreads, lazyInitOfDefaultHandlerThreads).collect(Collectors.toList());
        Collections.shuffle(threads);
        threads.forEach(Thread::start);
        try {
            finishLatch.await();
            discoveryClients.forEach(( client) -> Assert.assertSame("Healthcheck handler should be custom.", myHealthCheckHandler, client.getHealthCheckHandler()));
        } finally {
            // cleanup resources
            discoveryClients.forEach(DiscoveryClient::shutdown);
        }
    }

    public static class SimultaneousStarter extends Thread {
        private final CountDownLatch starterLatch;

        private final CountDownLatch finishLatch;

        private final Runnable runnable;

        public SimultaneousStarter(CountDownLatch starterLatch, CountDownLatch finishLatch, Runnable runnable) {
            this.starterLatch = starterLatch;
            this.finishLatch = finishLatch;
            this.runnable = runnable;
        }

        @Override
        public void run() {
            starterLatch.countDown();
            try {
                starterLatch.await();
                runnable.run();
                finishLatch.countDown();
            } catch (InterruptedException e) {
                throw new RuntimeException("Something went wrong...");
            }
        }
    }

    private static class MyHealthCheckCallback implements HealthCheckCallback {
        private final boolean health;

        private volatile boolean invoked;

        private MyHealthCheckCallback(boolean health) {
            this.health = health;
        }

        @Override
        public boolean isHealthy() {
            invoked = true;
            return health;
        }

        public boolean isInvoked() {
            return invoked;
        }

        public void reset() {
            invoked = false;
        }
    }

    private static class MyHealthCheckHandler implements HealthCheckHandler {
        private final InstanceStatus health;

        private volatile boolean invoked;

        volatile boolean shouldException;

        private MyHealthCheckHandler(InstanceInfo.InstanceStatus health) {
            this.health = health;
        }

        public boolean isInvoked() {
            return invoked;
        }

        public void reset() {
            shouldException = false;
            invoked = false;
        }

        @Override
        public InstanceStatus getStatus(InstanceInfo.InstanceStatus currentStatus) {
            invoked = true;
            if (shouldException) {
                throw new RuntimeException("test induced exception");
            }
            return health;
        }
    }
}

