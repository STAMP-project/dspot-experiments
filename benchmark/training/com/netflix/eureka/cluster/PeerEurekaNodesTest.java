package com.netflix.eureka.cluster;


import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.shared.transport.ClusterSampleData;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Tomasz Bak
 */
public class PeerEurekaNodesTest {
    private static final String PEER_EUREKA_URL_A = "http://a.eureka.test";

    private static final String PEER_EUREKA_URL_B = "http://b.eureka.test";

    private static final String PEER_EUREKA_URL_C = "http://c.eureka.test";

    private final PeerAwareInstanceRegistry registry = Mockito.mock(PeerAwareInstanceRegistry.class);

    private final PeerEurekaNodesTest.TestablePeerEurekaNodes peerEurekaNodes = new PeerEurekaNodesTest.TestablePeerEurekaNodes(registry, ClusterSampleData.newEurekaServerConfig());

    @Test
    public void testInitialStartupShutdown() throws Exception {
        peerEurekaNodes.withPeerUrls(PeerEurekaNodesTest.PEER_EUREKA_URL_A);
        // Start
        start();
        PeerEurekaNode peerNode = getPeerNode(PeerEurekaNodesTest.PEER_EUREKA_URL_A);
        Assert.assertThat(peerNode, CoreMatchers.is(CoreMatchers.notNullValue()));
        // Shutdown
        shutdown();
        Mockito.verify(peerNode, Mockito.times(1)).shutDown();
    }

    @Test
    public void testReloadWithNoPeerChange() throws Exception {
        // Start
        peerEurekaNodes.withPeerUrls(PeerEurekaNodesTest.PEER_EUREKA_URL_A);
        start();
        PeerEurekaNode peerNode = getPeerNode(PeerEurekaNodesTest.PEER_EUREKA_URL_A);
        Assert.assertThat(peerEurekaNodes.awaitNextReload(60, TimeUnit.SECONDS), CoreMatchers.is(true));
        Assert.assertThat(getPeerNode(PeerEurekaNodesTest.PEER_EUREKA_URL_A), CoreMatchers.is(CoreMatchers.equalTo(peerNode)));
    }

    @Test
    public void testReloadWithPeerUpdates() throws Exception {
        // Start
        peerEurekaNodes.withPeerUrls(PeerEurekaNodesTest.PEER_EUREKA_URL_A);
        start();
        PeerEurekaNode peerNodeA = getPeerNode(PeerEurekaNodesTest.PEER_EUREKA_URL_A);
        // Add one more peer
        peerEurekaNodes.withPeerUrls(PeerEurekaNodesTest.PEER_EUREKA_URL_A, PeerEurekaNodesTest.PEER_EUREKA_URL_B);
        Assert.assertThat(peerEurekaNodes.awaitNextReload(60, TimeUnit.SECONDS), CoreMatchers.is(true));
        Assert.assertThat(getPeerNode(PeerEurekaNodesTest.PEER_EUREKA_URL_A), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(getPeerNode(PeerEurekaNodesTest.PEER_EUREKA_URL_B), CoreMatchers.is(CoreMatchers.notNullValue()));
        // Remove first peer, and add yet another one
        peerEurekaNodes.withPeerUrls(PeerEurekaNodesTest.PEER_EUREKA_URL_B, PeerEurekaNodesTest.PEER_EUREKA_URL_C);
        Assert.assertThat(peerEurekaNodes.awaitNextReload(60, TimeUnit.SECONDS), CoreMatchers.is(true));
        Assert.assertThat(getPeerNode(PeerEurekaNodesTest.PEER_EUREKA_URL_A), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(getPeerNode(PeerEurekaNodesTest.PEER_EUREKA_URL_B), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(getPeerNode(PeerEurekaNodesTest.PEER_EUREKA_URL_C), CoreMatchers.is(CoreMatchers.notNullValue()));
        Mockito.verify(peerNodeA, Mockito.times(1)).shutDown();
    }

    static class TestablePeerEurekaNodes extends PeerEurekaNodes {
        private AtomicReference<List<String>> peerUrlsRef = new AtomicReference<>(Collections.<String>emptyList());

        private final ConcurrentHashMap<String, PeerEurekaNode> peerEurekaNodeByUrl = new ConcurrentHashMap<>();

        private final AtomicInteger reloadCounter = new AtomicInteger();

        TestablePeerEurekaNodes(PeerAwareInstanceRegistry registry, EurekaServerConfig serverConfig) {
            super(registry, serverConfig, new DefaultEurekaClientConfig(), new com.netflix.eureka.resources.DefaultServerCodecs(serverConfig), Mockito.mock(ApplicationInfoManager.class));
        }

        void withPeerUrls(String... peerUrls) {
            this.peerUrlsRef.set(Arrays.asList(peerUrls));
        }

        boolean awaitNextReload(long timeout, TimeUnit timeUnit) throws InterruptedException {
            int lastReloadCounter = reloadCounter.get();
            long endTime = (System.currentTimeMillis()) + (timeUnit.toMillis(timeout));
            while ((endTime > (System.currentTimeMillis())) && (lastReloadCounter == (reloadCounter.get()))) {
                Thread.sleep(10);
            } 
            return lastReloadCounter != (reloadCounter.get());
        }

        @Override
        protected void updatePeerEurekaNodes(List<String> newPeerUrls) {
            super.updatePeerEurekaNodes(newPeerUrls);
            reloadCounter.incrementAndGet();
        }

        @Override
        protected List<String> resolvePeerUrls() {
            return peerUrlsRef.get();
        }

        @Override
        protected PeerEurekaNode createPeerEurekaNode(String peerEurekaNodeUrl) {
            if (peerEurekaNodeByUrl.containsKey(peerEurekaNodeUrl)) {
                throw new IllegalStateException((("PeerEurekaNode for URL " + peerEurekaNodeUrl) + " is already created"));
            }
            PeerEurekaNode peerEurekaNode = Mockito.mock(PeerEurekaNode.class);
            Mockito.when(peerEurekaNode.getServiceUrl()).thenReturn(peerEurekaNodeUrl);
            return peerEurekaNode;
        }
    }
}

