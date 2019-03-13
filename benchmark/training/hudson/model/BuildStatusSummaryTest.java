package hudson.model;


import Result.ABORTED;
import Result.FAILURE;
import Result.NOT_BUILT;
import Result.SUCCESS;
import Result.UNSTABLE;
import hudson.model.Run.Summary;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests {@link Run#getBuildStatusSummary()}.
 *
 * @author kutzi
 */
@SuppressWarnings("rawtypes")
public class BuildStatusSummaryTest {
    private Run build;

    private Run prevBuild;

    @Test
    public void testStatusUnknownIfRunIsStillBuilding() {
        Mockito.when(this.build.getResult()).thenReturn(null);
        Mockito.when(this.build.isBuilding()).thenReturn(true);
        Summary summary = this.build.getBuildStatusSummary();
        Assert.assertEquals(Messages.Run_Summary_Unknown(), summary.message);
    }

    @Test
    public void testSuccess() {
        Mockito.when(this.build.getResult()).thenReturn(SUCCESS);
        Mockito.when(this.prevBuild.getResult()).thenReturn(SUCCESS);
        Summary summary = this.build.getBuildStatusSummary();
        Assert.assertFalse(summary.isWorse);
        Assert.assertEquals(Messages.Run_Summary_Stable(), summary.message);
        // same if there is no previous build
        Mockito.when(this.build.getPreviousBuild()).thenReturn(null);
        summary = this.build.getBuildStatusSummary();
        Assert.assertFalse(summary.isWorse);
        Assert.assertEquals(Messages.Run_Summary_Stable(), summary.message);
        // from NOT_BUILD should also mean normal success and not 'back to normal'
        Mockito.when(this.prevBuild.getResult()).thenReturn(NOT_BUILT);
        summary = this.build.getBuildStatusSummary();
        Assert.assertFalse(summary.isWorse);
        Assert.assertEquals(Messages.Run_Summary_Stable(), summary.message);
        // same if previous one was aborted
        Mockito.when(this.prevBuild.getResult()).thenReturn(ABORTED);
        summary = this.build.getBuildStatusSummary();
        Assert.assertFalse(summary.isWorse);
        Assert.assertEquals(Messages.Run_Summary_Stable(), summary.message);
    }

    @Test
    public void testFixed() {
        Mockito.when(this.build.getResult()).thenReturn(SUCCESS);
        Mockito.when(this.prevBuild.getResult()).thenReturn(FAILURE);
        Summary summary = this.build.getBuildStatusSummary();
        Assert.assertFalse(summary.isWorse);
        Assert.assertEquals(Messages.Run_Summary_BackToNormal(), summary.message);
        // same from unstable:
        Mockito.when(this.prevBuild.getResult()).thenReturn(UNSTABLE);
        summary = this.build.getBuildStatusSummary();
        Assert.assertFalse(summary.isWorse);
        Assert.assertEquals(Messages.Run_Summary_BackToNormal(), summary.message);
    }

    @Test
    public void testFailure() {
        Mockito.when(this.build.getResult()).thenReturn(FAILURE);
        Mockito.when(this.prevBuild.getResult()).thenReturn(FAILURE);
        Summary summary = this.build.getBuildStatusSummary();
        Assert.assertFalse(summary.isWorse);
        Assert.assertEquals(Messages.Run_Summary_BrokenForALongTime(), summary.message);
    }

    @Test
    public void testBecameFailure() {
        Mockito.when(this.build.getResult()).thenReturn(FAILURE);
        Mockito.when(this.prevBuild.getResult()).thenReturn(SUCCESS);
        Mockito.when(this.build.getPreviousNotFailedBuild()).thenReturn(this.prevBuild);
        Summary summary = this.build.getBuildStatusSummary();
        Assert.assertTrue(summary.isWorse);
        Assert.assertEquals(Messages.Run_Summary_BrokenSinceThisBuild(), summary.message);
    }

    @Test
    public void testFailureSince() {
        Mockito.when(this.build.getResult()).thenReturn(FAILURE);
        Mockito.when(this.prevBuild.getResult()).thenReturn(FAILURE);
        Mockito.when(this.prevBuild.getDisplayName()).thenReturn("prevBuild");
        Run prevPrevBuild = Mockito.mock(Run.class);
        Mockito.when(prevPrevBuild.getNextBuild()).thenReturn(prevBuild);
        Mockito.when(this.build.getPreviousNotFailedBuild()).thenReturn(prevPrevBuild);
        Summary summary = this.build.getBuildStatusSummary();
        Assert.assertFalse(summary.isWorse);
        Assert.assertEquals(Messages.Run_Summary_BrokenSince(this.prevBuild.getDisplayName()), summary.message);
    }

    @Test
    public void testBecameUnstable() {
        Mockito.when(this.build.getResult()).thenReturn(UNSTABLE);
        Mockito.when(this.prevBuild.getResult()).thenReturn(SUCCESS);
        Summary summary = this.build.getBuildStatusSummary();
        Assert.assertTrue(summary.isWorse);
        // assertEquals(Messages.Run_Summary_Stable(), summary.message);
    }

    @Test
    public void testUnstableAfterFailure() {
        Mockito.when(this.build.getResult()).thenReturn(UNSTABLE);
        Mockito.when(this.prevBuild.getResult()).thenReturn(FAILURE);
        Summary summary = this.build.getBuildStatusSummary();
        Assert.assertFalse(summary.isWorse);
        Assert.assertEquals(Messages.Run_Summary_Unstable(), summary.message);
    }

    @Test
    public void testNonTestRelatedUnstable() {
        Mockito.when(this.build.getResult()).thenReturn(UNSTABLE);
        Mockito.when(this.prevBuild.getResult()).thenReturn(UNSTABLE);
        Summary summary = this.build.getBuildStatusSummary();
        Assert.assertFalse(summary.isWorse);
        Assert.assertEquals(Messages.Run_Summary_Unstable(), summary.message);
    }

    @Test
    public void testNonTestRelatedBecameUnstable() {
        Mockito.when(this.build.getResult()).thenReturn(UNSTABLE);
        Mockito.when(this.prevBuild.getResult()).thenReturn(SUCCESS);
        Summary summary = this.build.getBuildStatusSummary();
        Assert.assertTrue(summary.isWorse);
        // assertEquals(Messages.Run_Summary_Unstable(), summary.message);
    }

    @Test
    public void testAborted() {
        Mockito.when(this.build.getResult()).thenReturn(ABORTED);
        Summary summary = this.build.getBuildStatusSummary();
        Assert.assertFalse(summary.isWorse);
        Assert.assertEquals(Messages.Run_Summary_Aborted(), summary.message);
    }

    @Test
    public void testNotBuilt() {
        Mockito.when(this.build.getResult()).thenReturn(NOT_BUILT);
        Summary summary = this.build.getBuildStatusSummary();
        Assert.assertFalse(summary.isWorse);
        Assert.assertEquals(Messages.Run_Summary_NotBuilt(), summary.message);
    }
}

