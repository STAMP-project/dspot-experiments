package com.ctrip.framework.apollo.biz.service;


import NamespaceBranchStatus.ACTIVE;
import NamespaceBranchStatus.DELETED;
import ReleaseOperation.ABANDON_GRAY_RELEASE;
import ReleaseOperation.APPLY_GRAY_RULES;
import Sql.ExecutionPhase;
import com.ctrip.framework.apollo.biz.AbstractIntegrationTest;
import com.ctrip.framework.apollo.biz.entity.GrayReleaseRule;
import com.ctrip.framework.apollo.biz.entity.Namespace;
import com.ctrip.framework.apollo.biz.entity.ReleaseHistory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;


public class NamespaceBranchServiceTest extends AbstractIntegrationTest {
    @Autowired
    private NamespaceBranchService namespaceBranchService;

    @Autowired
    private ReleaseHistoryService releaseHistoryService;

    private String testApp = "test";

    private String testCluster = "default";

    private String testNamespace = "application";

    private String testBranchName = "child-cluster";

    private String operator = "apollo";

    private Pageable pageable = PageRequest.of(0, 10);

    @Test
    @Sql(scripts = "/sql/namespace-branch-test.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/clean.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testFindBranch() {
        Namespace branch = namespaceBranchService.findBranch(testApp, testCluster, testNamespace);
        Assert.assertNotNull(branch);
        Assert.assertEquals(testBranchName, branch.getClusterName());
    }

    @Test
    @Sql(scripts = "/sql/namespace-branch-test.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/clean.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testUpdateBranchGrayRulesWithUpdateOnce() {
        GrayReleaseRule rule = instanceGrayReleaseRule();
        namespaceBranchService.updateBranchGrayRules(testApp, testCluster, testNamespace, testBranchName, rule);
        GrayReleaseRule activeRule = namespaceBranchService.findBranchGrayRules(testApp, testCluster, testNamespace, testBranchName);
        Assert.assertNotNull(activeRule);
        Assert.assertEquals(rule.getAppId(), activeRule.getAppId());
        Assert.assertEquals(rule.getRules(), activeRule.getRules());
        Assert.assertEquals(Long.valueOf(0), activeRule.getReleaseId());
        Page<ReleaseHistory> releaseHistories = releaseHistoryService.findReleaseHistoriesByNamespace(testApp, testCluster, testNamespace, pageable);
        ReleaseHistory releaseHistory = releaseHistories.getContent().get(0);
        Assert.assertEquals(1, releaseHistories.getTotalElements());
        Assert.assertEquals(APPLY_GRAY_RULES, releaseHistory.getOperation());
        Assert.assertEquals(0, releaseHistory.getReleaseId());
        Assert.assertEquals(0, releaseHistory.getPreviousReleaseId());
        Assert.assertTrue(releaseHistory.getOperationContext().contains(rule.getRules()));
    }

    @Test
    @Sql(scripts = "/sql/namespace-branch-test.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/clean.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testUpdateBranchGrayRulesWithUpdateTwice() {
        GrayReleaseRule firstRule = instanceGrayReleaseRule();
        namespaceBranchService.updateBranchGrayRules(testApp, testCluster, testNamespace, testBranchName, firstRule);
        GrayReleaseRule secondRule = instanceGrayReleaseRule();
        secondRule.setRules("[{\"clientAppId\":\"branch-test\",\"clientIpList\":[\"10.38.57.112\"]}]");
        namespaceBranchService.updateBranchGrayRules(testApp, testCluster, testNamespace, testBranchName, secondRule);
        GrayReleaseRule activeRule = namespaceBranchService.findBranchGrayRules(testApp, testCluster, testNamespace, testBranchName);
        Assert.assertNotNull(secondRule);
        Assert.assertEquals(secondRule.getAppId(), activeRule.getAppId());
        Assert.assertEquals(secondRule.getRules(), activeRule.getRules());
        Assert.assertEquals(Long.valueOf(0), activeRule.getReleaseId());
        Page<ReleaseHistory> releaseHistories = releaseHistoryService.findReleaseHistoriesByNamespace(testApp, testCluster, testNamespace, pageable);
        ReleaseHistory firstReleaseHistory = releaseHistories.getContent().get(1);
        ReleaseHistory secondReleaseHistory = releaseHistories.getContent().get(0);
        Assert.assertEquals(2, releaseHistories.getTotalElements());
        Assert.assertEquals(APPLY_GRAY_RULES, firstReleaseHistory.getOperation());
        Assert.assertEquals(APPLY_GRAY_RULES, secondReleaseHistory.getOperation());
        Assert.assertTrue(firstReleaseHistory.getOperationContext().contains(firstRule.getRules()));
        Assert.assertFalse(firstReleaseHistory.getOperationContext().contains(secondRule.getRules()));
        Assert.assertTrue(secondReleaseHistory.getOperationContext().contains(firstRule.getRules()));
        Assert.assertTrue(secondReleaseHistory.getOperationContext().contains(secondRule.getRules()));
    }

    @Test
    @Sql(scripts = "/sql/namespace-branch-test.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/clean.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testUpdateRulesReleaseIdWithOldRuleNotExist() {
        long latestReleaseId = 100;
        namespaceBranchService.updateRulesReleaseId(testApp, testCluster, testNamespace, testBranchName, latestReleaseId, operator);
        GrayReleaseRule activeRule = namespaceBranchService.findBranchGrayRules(testApp, testCluster, testNamespace, testBranchName);
        Assert.assertNull(activeRule);
    }

    @Test
    @Sql(scripts = "/sql/namespace-branch-test.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/clean.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testUpdateRulesReleaseIdWithOldRuleExist() {
        GrayReleaseRule rule = instanceGrayReleaseRule();
        namespaceBranchService.updateBranchGrayRules(testApp, testCluster, testNamespace, testBranchName, rule);
        long latestReleaseId = 100;
        namespaceBranchService.updateRulesReleaseId(testApp, testCluster, testNamespace, testBranchName, latestReleaseId, operator);
        GrayReleaseRule activeRule = namespaceBranchService.findBranchGrayRules(testApp, testCluster, testNamespace, testBranchName);
        Assert.assertNotNull(activeRule);
        Assert.assertEquals(Long.valueOf(latestReleaseId), activeRule.getReleaseId());
        Assert.assertEquals(rule.getRules(), activeRule.getRules());
        Assert.assertEquals(ACTIVE, activeRule.getBranchStatus());
    }

    @Test
    @Sql(scripts = "/sql/namespace-branch-test.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/clean.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testDeleteBranch() {
        GrayReleaseRule rule = instanceGrayReleaseRule();
        namespaceBranchService.updateBranchGrayRules(testApp, testCluster, testNamespace, testBranchName, rule);
        namespaceBranchService.deleteBranch(testApp, testCluster, testNamespace, testBranchName, DELETED, operator);
        Namespace branch = namespaceBranchService.findBranch(testApp, testCluster, testNamespace);
        Assert.assertNull(branch);
        GrayReleaseRule latestRule = namespaceBranchService.findBranchGrayRules(testApp, testCluster, testNamespace, testBranchName);
        Assert.assertNotNull(latestRule);
        Assert.assertEquals(DELETED, latestRule.getBranchStatus());
        Assert.assertEquals("[]", latestRule.getRules());
        Page<ReleaseHistory> releaseHistories = releaseHistoryService.findReleaseHistoriesByNamespace(testApp, testCluster, testNamespace, pageable);
        ReleaseHistory firstReleaseHistory = releaseHistories.getContent().get(1);
        ReleaseHistory secondReleaseHistory = releaseHistories.getContent().get(0);
        Assert.assertEquals(2, releaseHistories.getTotalElements());
        Assert.assertEquals(APPLY_GRAY_RULES, firstReleaseHistory.getOperation());
        Assert.assertEquals(ABANDON_GRAY_RELEASE, secondReleaseHistory.getOperation());
    }
}

