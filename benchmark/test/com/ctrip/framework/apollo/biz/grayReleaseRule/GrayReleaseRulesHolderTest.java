package com.ctrip.framework.apollo.biz.grayReleaseRule;


import ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR;
import Topics.APOLLO_RELEASE_TOPIC;
import com.ctrip.framework.apollo.biz.config.BizConfig;
import com.ctrip.framework.apollo.biz.entity.GrayReleaseRule;
import com.ctrip.framework.apollo.biz.repository.GrayReleaseRuleRepository;
import com.ctrip.framework.apollo.common.constants.NamespaceBranchStatus;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class GrayReleaseRulesHolderTest {
    private static final Joiner STRING_JOINER = Joiner.on(CLUSTER_NAMESPACE_SEPARATOR);

    private GrayReleaseRulesHolder grayReleaseRulesHolder;

    @Mock
    private BizConfig bizConfig;

    @Mock
    private GrayReleaseRuleRepository grayReleaseRuleRepository;

    private Gson gson = new Gson();

    private AtomicLong idCounter;

    @Test
    public void testScanGrayReleaseRules() throws Exception {
        String someAppId = "someAppId";
        String someClusterName = "someClusterName";
        String someNamespaceName = "someNamespaceName";
        String anotherNamespaceName = "anotherNamespaceName";
        Long someReleaseId = 1L;
        int activeBranchStatus = NamespaceBranchStatus.ACTIVE;
        String someClientAppId = "clientAppId1";
        String someClientIp = "1.1.1.1";
        String anotherClientAppId = "clientAppId2";
        String anotherClientIp = "2.2.2.2";
        GrayReleaseRule someRule = assembleGrayReleaseRule(someAppId, someClusterName, someNamespaceName, Lists.newArrayList(assembleRuleItem(someClientAppId, Sets.newHashSet(someClientIp))), someReleaseId, activeBranchStatus);
        Mockito.when(bizConfig.grayReleaseRuleScanInterval()).thenReturn(30);
        Mockito.when(grayReleaseRuleRepository.findFirst500ByIdGreaterThanOrderByIdAsc(0L)).thenReturn(Lists.newArrayList(someRule));
        // scan rules
        grayReleaseRulesHolder.afterPropertiesSet();
        Assert.assertEquals(someReleaseId, grayReleaseRulesHolder.findReleaseIdFromGrayReleaseRule(someClientAppId, someClientIp, someAppId, someClusterName, someNamespaceName));
        Assert.assertNull(grayReleaseRulesHolder.findReleaseIdFromGrayReleaseRule(someClientAppId, anotherClientIp, someAppId, someClusterName, someNamespaceName));
        Assert.assertNull(grayReleaseRulesHolder.findReleaseIdFromGrayReleaseRule(anotherClientAppId, someClientIp, someAppId, someClusterName, someNamespaceName));
        Assert.assertNull(grayReleaseRulesHolder.findReleaseIdFromGrayReleaseRule(anotherClientAppId, anotherClientIp, someAppId, someClusterName, someNamespaceName));
        Assert.assertTrue(grayReleaseRulesHolder.hasGrayReleaseRule(someClientAppId, someClientIp, someNamespaceName));
        Assert.assertFalse(grayReleaseRulesHolder.hasGrayReleaseRule(someClientAppId, anotherClientIp, someNamespaceName));
        Assert.assertFalse(grayReleaseRulesHolder.hasGrayReleaseRule(someClientAppId, someClientIp, anotherNamespaceName));
        Assert.assertFalse(grayReleaseRulesHolder.hasGrayReleaseRule(anotherClientAppId, anotherClientIp, someNamespaceName));
        Assert.assertFalse(grayReleaseRulesHolder.hasGrayReleaseRule(anotherClientAppId, anotherClientIp, anotherNamespaceName));
        GrayReleaseRule anotherRule = assembleGrayReleaseRule(someAppId, someClusterName, someNamespaceName, Lists.newArrayList(assembleRuleItem(anotherClientAppId, Sets.newHashSet(anotherClientIp))), someReleaseId, activeBranchStatus);
        Mockito.when(grayReleaseRuleRepository.findByAppIdAndClusterNameAndNamespaceName(someAppId, someClusterName, someNamespaceName)).thenReturn(Lists.newArrayList(anotherRule));
        // send message
        grayReleaseRulesHolder.handleMessage(assembleReleaseMessage(someAppId, someClusterName, someNamespaceName), APOLLO_RELEASE_TOPIC);
        Assert.assertNull(grayReleaseRulesHolder.findReleaseIdFromGrayReleaseRule(someClientAppId, someClientIp, someAppId, someClusterName, someNamespaceName));
        Assert.assertEquals(someReleaseId, grayReleaseRulesHolder.findReleaseIdFromGrayReleaseRule(anotherClientAppId, anotherClientIp, someAppId, someClusterName, someNamespaceName));
        Assert.assertFalse(grayReleaseRulesHolder.hasGrayReleaseRule(someClientAppId, someClientIp, someNamespaceName));
        Assert.assertFalse(grayReleaseRulesHolder.hasGrayReleaseRule(someClientAppId, someClientIp, anotherNamespaceName));
        Assert.assertTrue(grayReleaseRulesHolder.hasGrayReleaseRule(anotherClientAppId, anotherClientIp, someNamespaceName));
        Assert.assertFalse(grayReleaseRulesHolder.hasGrayReleaseRule(anotherClientAppId, someClientIp, someNamespaceName));
        Assert.assertFalse(grayReleaseRulesHolder.hasGrayReleaseRule(anotherClientAppId, anotherClientIp, anotherNamespaceName));
    }
}

