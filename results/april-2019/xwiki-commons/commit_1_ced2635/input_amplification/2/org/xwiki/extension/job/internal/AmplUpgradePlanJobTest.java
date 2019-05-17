package org.xwiki.extension.job.internal;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.xwiki.extension.ExtensionId;
import org.xwiki.extension.InstalledExtension;
import org.xwiki.extension.TestResources;
import org.xwiki.extension.job.plan.ExtensionPlan;
import org.xwiki.extension.job.plan.ExtensionPlanAction;
import org.xwiki.extension.job.plan.ExtensionPlanNode;
import org.xwiki.extension.repository.internal.installed.DefaultInstalledExtension;
import org.xwiki.extension.test.AbstractExtensionHandlerTest;
import org.xwiki.extension.version.Version;


public class AmplUpgradePlanJobTest extends AbstractExtensionHandlerTest {
    @Test(timeout = 10000)
    public void testUpgradePlanOnRoot_remove46283() throws Throwable {
        InstalledExtension extension = ((DefaultInstalledExtension) (install(TestResources.REMOTE_UPGRADE10_ID)));
        ExtensionPlan plan = upgradePlan();
        int o_testUpgradePlanOnRoot_remove46283__7 = plan.getTree().size();
        Assert.assertEquals(2, ((int) (o_testUpgradePlanOnRoot_remove46283__7)));
        ExtensionPlanNode node = getNode(TestResources.REMOTE_UPGRADE20_ID, plan.getTree());
        ExtensionPlanAction action = node.getAction();
        ExtensionId o_testUpgradePlanOnRoot_remove46283__14 = action.getExtension().getId();
        Assert.assertEquals("2.0", ((Version) (((ExtensionId) (o_testUpgradePlanOnRoot_remove46283__14)).getVersion())).getValue());
        Assert.assertEquals(510300123, ((int) (((ExtensionId) (o_testUpgradePlanOnRoot_remove46283__14)).hashCode())));
        Assert.assertEquals("upgrade", ((ExtensionId) (o_testUpgradePlanOnRoot_remove46283__14)).getId());
        action.getAction();
        action.getAction();
        action.getAction();
        String o_testUpgradePlanOnRoot_remove46283__17 = action.getNamespace();
        Assert.assertNull(o_testUpgradePlanOnRoot_remove46283__17);
        int o_testUpgradePlanOnRoot_remove46283__18 = node.getChildren().size();
        Assert.assertEquals(0, ((int) (o_testUpgradePlanOnRoot_remove46283__18)));
        node = getNode(TestResources.INSTALLED_WITHMISSINDEPENDENCY_ID, plan.getTree());
        action = node.getAction();
        ExtensionId o_testUpgradePlanOnRoot_remove46283__25 = action.getExtension().getId();
        Assert.assertEquals("version", ((Version) (((ExtensionId) (o_testUpgradePlanOnRoot_remove46283__25)).getVersion())).getValue());
        Assert.assertEquals(-438100544, ((int) (((ExtensionId) (o_testUpgradePlanOnRoot_remove46283__25)).hashCode())));
        Assert.assertEquals("installedwithmissingdependency", ((ExtensionId) (o_testUpgradePlanOnRoot_remove46283__25)).getId());
        int o_testUpgradePlanOnRoot_remove46283__28 = action.getPreviousExtensions().size();
        Assert.assertEquals(0, ((int) (o_testUpgradePlanOnRoot_remove46283__28)));
        String o_testUpgradePlanOnRoot_remove46283__30 = action.getNamespace();
        Assert.assertNull(o_testUpgradePlanOnRoot_remove46283__30);
        String o_testUpgradePlanOnRoot_remove46283__31 = action.getNamespace();
        Assert.assertNull(o_testUpgradePlanOnRoot_remove46283__31);
        int o_testUpgradePlanOnRoot_remove46283__32 = node.getChildren().size();
        Assert.assertEquals(1, ((int) (o_testUpgradePlanOnRoot_remove46283__32)));
        node = node.getChildren().iterator().next();
        action = node.getAction();
        ExtensionId o_testUpgradePlanOnRoot_remove46283__40 = action.getExtension().getId();
        Assert.assertEquals("version", ((Version) (((ExtensionId) (o_testUpgradePlanOnRoot_remove46283__40)).getVersion())).getValue());
        Assert.assertEquals(1407256064, ((int) (((ExtensionId) (o_testUpgradePlanOnRoot_remove46283__40)).hashCode())));
        Assert.assertEquals("missingdependency", ((ExtensionId) (o_testUpgradePlanOnRoot_remove46283__40)).getId());
        int o_testUpgradePlanOnRoot_remove46283__43 = action.getPreviousExtensions().size();
        Assert.assertEquals(0, ((int) (o_testUpgradePlanOnRoot_remove46283__43)));
        int o_testUpgradePlanOnRoot_remove46283__45 = action.getPreviousExtensions().size();
        Assert.assertEquals(0, ((int) (o_testUpgradePlanOnRoot_remove46283__45)));
        String o_testUpgradePlanOnRoot_remove46283__47 = action.getNamespace();
        Assert.assertNull(o_testUpgradePlanOnRoot_remove46283__47);
        String o_testUpgradePlanOnRoot_remove46283__48 = action.getNamespace();
        Assert.assertNull(o_testUpgradePlanOnRoot_remove46283__48);
        String o_testUpgradePlanOnRoot_remove46283__49 = action.getNamespace();
        Assert.assertNull(o_testUpgradePlanOnRoot_remove46283__49);
        int o_testUpgradePlanOnRoot_remove46283__50 = node.getChildren().size();
        Assert.assertEquals(0, ((int) (o_testUpgradePlanOnRoot_remove46283__50)));
        int o_testUpgradePlanOnRoot_remove46283__52 = node.getChildren().size();
        Assert.assertEquals(0, ((int) (o_testUpgradePlanOnRoot_remove46283__52)));
        int o_testUpgradePlanOnRoot_remove46283__54 = plan.getActions().size();
        Assert.assertEquals(3, ((int) (o_testUpgradePlanOnRoot_remove46283__54)));
        int o_testUpgradePlanOnRoot_remove46283__56 = upgradePlan(null, Arrays.asList(TestResources.REMOTE_UPGRADE10_ID, TestResources.INSTALLED_WITHMISSINDEPENDENCY_ID)).getTree().size();
        Assert.assertEquals(0, ((int) (o_testUpgradePlanOnRoot_remove46283__56)));
        int o_testUpgradePlanOnRoot_remove46283__60 = upgradePlan(null, Arrays.asList(TestResources.REMOTE_UPGRADE10_ID)).getTree().size();
        Assert.assertEquals(1, ((int) (o_testUpgradePlanOnRoot_remove46283__60)));
        int o_testUpgradePlanOnRoot_remove46283__64 = upgradePlan(null, Arrays.asList(TestResources.REMOTE_UPGRADE20_ID)).getTree().size();
        Assert.assertEquals(2, ((int) (o_testUpgradePlanOnRoot_remove46283__64)));
        Assert.assertEquals(2, ((int) (o_testUpgradePlanOnRoot_remove46283__7)));
        Assert.assertEquals("2.0", ((Version) (((ExtensionId) (o_testUpgradePlanOnRoot_remove46283__14)).getVersion())).getValue());
        Assert.assertEquals(510300123, ((int) (((ExtensionId) (o_testUpgradePlanOnRoot_remove46283__14)).hashCode())));
        Assert.assertEquals("upgrade", ((ExtensionId) (o_testUpgradePlanOnRoot_remove46283__14)).getId());
        Assert.assertNull(o_testUpgradePlanOnRoot_remove46283__17);
        Assert.assertEquals(0, ((int) (o_testUpgradePlanOnRoot_remove46283__18)));
        Assert.assertEquals("version", ((Version) (((ExtensionId) (o_testUpgradePlanOnRoot_remove46283__25)).getVersion())).getValue());
        Assert.assertEquals(-438100544, ((int) (((ExtensionId) (o_testUpgradePlanOnRoot_remove46283__25)).hashCode())));
        Assert.assertEquals("installedwithmissingdependency", ((ExtensionId) (o_testUpgradePlanOnRoot_remove46283__25)).getId());
        Assert.assertEquals(0, ((int) (o_testUpgradePlanOnRoot_remove46283__28)));
        Assert.assertNull(o_testUpgradePlanOnRoot_remove46283__30);
        Assert.assertNull(o_testUpgradePlanOnRoot_remove46283__31);
        Assert.assertEquals(1, ((int) (o_testUpgradePlanOnRoot_remove46283__32)));
        Assert.assertEquals("version", ((Version) (((ExtensionId) (o_testUpgradePlanOnRoot_remove46283__40)).getVersion())).getValue());
        Assert.assertEquals(1407256064, ((int) (((ExtensionId) (o_testUpgradePlanOnRoot_remove46283__40)).hashCode())));
        Assert.assertEquals("missingdependency", ((ExtensionId) (o_testUpgradePlanOnRoot_remove46283__40)).getId());
        Assert.assertEquals(0, ((int) (o_testUpgradePlanOnRoot_remove46283__43)));
        Assert.assertEquals(0, ((int) (o_testUpgradePlanOnRoot_remove46283__45)));
        Assert.assertNull(o_testUpgradePlanOnRoot_remove46283__47);
        Assert.assertNull(o_testUpgradePlanOnRoot_remove46283__48);
        Assert.assertNull(o_testUpgradePlanOnRoot_remove46283__49);
        Assert.assertEquals(0, ((int) (o_testUpgradePlanOnRoot_remove46283__50)));
        Assert.assertEquals(0, ((int) (o_testUpgradePlanOnRoot_remove46283__52)));
        Assert.assertEquals(3, ((int) (o_testUpgradePlanOnRoot_remove46283__54)));
        Assert.assertEquals(0, ((int) (o_testUpgradePlanOnRoot_remove46283__56)));
        Assert.assertEquals(1, ((int) (o_testUpgradePlanOnRoot_remove46283__60)));
    }
}

