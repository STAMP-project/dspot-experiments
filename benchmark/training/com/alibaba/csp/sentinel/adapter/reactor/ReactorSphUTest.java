package com.alibaba.csp.sentinel.adapter.reactor;


import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;


/**
 *
 *
 * @author Eric Zhao
 */
public class ReactorSphUTest {
    @Test
    public void testReactorEntryNormalWhenFlowControlTriggered() {
        String resourceName = createResourceName("testReactorEntryNormalWhenFlowControlTriggered");
        FlowRuleManager.loadRules(Collections.singletonList(new FlowRule(resourceName).setCount(0)));
        StepVerifier.create(ReactorSphU.entryWith(resourceName, Mono.just(60)).subscribeOn(Schedulers.elastic()).map(( e) -> e * 3)).expectError(BlockException.class).verify();
        ClusterNode cn = ClusterBuilderSlot.getClusterNode(resourceName);
        Assert.assertNotNull(cn);
        Assert.assertEquals(0, cn.passQps(), 0.01);
        Assert.assertEquals(1, cn.blockRequest());
        FlowRuleManager.loadRules(new ArrayList());
    }

    @Test
    public void testReactorEntryWithCommon() {
        String resourceName = createResourceName("testReactorEntryWithCommon");
        StepVerifier.create(ReactorSphU.entryWith(resourceName, Mono.just(60)).subscribeOn(Schedulers.elastic()).map(( e) -> e * 3)).expectNext(180).verifyComplete();
        ClusterNode cn = ClusterBuilderSlot.getClusterNode(resourceName);
        Assert.assertNotNull(cn);
        Assert.assertEquals(1, cn.passQps(), 0.01);
    }

    @Test
    public void testReactorEntryWithBizException() {
        String resourceName = createResourceName("testReactorEntryWithBizException");
        StepVerifier.create(ReactorSphU.entryWith(resourceName, Mono.error(new IllegalStateException()))).expectError(IllegalStateException.class).verify();
        ClusterNode cn = ClusterBuilderSlot.getClusterNode(resourceName);
        Assert.assertNotNull(cn);
        Assert.assertEquals(1, cn.passQps(), 0.01);
        Assert.assertEquals(1, cn.totalException());
    }
}

