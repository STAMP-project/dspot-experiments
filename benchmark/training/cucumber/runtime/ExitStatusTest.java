package cucumber.runtime;


import Result.Type.AMBIGUOUS;
import Result.Type.FAILED;
import Result.Type.PASSED;
import Result.Type.PENDING;
import Result.Type.SKIPPED;
import Result.Type.UNDEFINED;
import cucumber.runner.EventBus;
import org.junit.Assert;
import org.junit.Test;


public class ExitStatusTest {
    private static final long ANY_TIMESTAMP = 1234567890;

    private EventBus bus;

    private ExitStatus exitStatus;

    @Test
    public void non_strict_wip_with_ambiguous_scenarios() {
        createNonStrictWipExitStatus();
        bus.send(testCaseFinishedWithStatus(AMBIGUOUS));
        Assert.assertEquals(0, exitStatus.exitStatus());
    }

    @Test
    public void non_strict_wip_with_failed_scenarios() {
        createNonStrictWipExitStatus();
        bus.send(testCaseFinishedWithStatus(FAILED));
        Assert.assertEquals(0, exitStatus.exitStatus());
    }

    @Test
    public void non_strict_wip_with_passed_scenarios() {
        createNonStrictWipExitStatus();
        bus.send(testCaseFinishedWithStatus(PASSED));
        Assert.assertEquals(1, exitStatus.exitStatus());
    }

    @Test
    public void non_strict_wip_with_pending_scenarios() {
        createNonStrictWipExitStatus();
        bus.send(testCaseFinishedWithStatus(PENDING));
        Assert.assertEquals(0, exitStatus.exitStatus());
    }

    @Test
    public void non_strict_wip_with_skipped_scenarios() {
        createNonStrictWipExitStatus();
        bus.send(testCaseFinishedWithStatus(SKIPPED));
        Assert.assertEquals(0, exitStatus.exitStatus());
    }

    @Test
    public void non_strict_wip_with_undefined_scenarios() {
        createNonStrictWipExitStatus();
        bus.send(testCaseFinishedWithStatus(UNDEFINED));
        Assert.assertEquals(0, exitStatus.exitStatus());
    }

    @Test
    public void non_strict_with_ambiguous_scenarios() {
        createNonStrictExitStatus();
        bus.send(testCaseFinishedWithStatus(AMBIGUOUS));
        Assert.assertEquals(1, exitStatus.exitStatus());
    }

    @Test
    public void non_strict_with_failed_scenarios() {
        createNonStrictExitStatus();
        bus.send(testCaseFinishedWithStatus(FAILED));
        Assert.assertEquals(1, exitStatus.exitStatus());
    }

    @Test
    public void non_strict_with_passed_scenarios() {
        createNonStrictExitStatus();
        bus.send(testCaseFinishedWithStatus(PASSED));
        Assert.assertEquals(0, exitStatus.exitStatus());
    }

    @Test
    public void non_strict_with_pending_scenarios() {
        createNonStrictExitStatus();
        bus.send(testCaseFinishedWithStatus(PENDING));
        Assert.assertEquals(0, exitStatus.exitStatus());
    }

    @Test
    public void non_strict_with_skipped_scenarios() {
        createNonStrictExitStatus();
        bus.send(testCaseFinishedWithStatus(SKIPPED));
        Assert.assertEquals(0, exitStatus.exitStatus());
    }

    @Test
    public void non_strict_with_undefined_scenarios() {
        createNonStrictExitStatus();
        bus.send(testCaseFinishedWithStatus(UNDEFINED));
        Assert.assertEquals(0, exitStatus.exitStatus());
    }

    @Test
    public void should_pass_if_no_features_are_found() {
        createStrictRuntime();
        Assert.assertEquals(0, exitStatus.exitStatus());
    }

    @Test
    public void strict_wip_with_ambiguous_scenarios() {
        createStrictWipRuntime();
        bus.send(testCaseFinishedWithStatus(AMBIGUOUS));
        Assert.assertEquals(0, exitStatus.exitStatus());
    }

    @Test
    public void strict_wip_with_failed_failed_scenarios() {
        createStrictWipRuntime();
        bus.send(testCaseFinishedWithStatus(FAILED));
        bus.send(testCaseFinishedWithStatus(FAILED));
        Assert.assertEquals(0, exitStatus.exitStatus());
    }

    @Test
    public void strict_wip_with_failed_passed_scenarios() {
        createStrictWipRuntime();
        bus.send(testCaseFinishedWithStatus(PASSED));
        bus.send(testCaseFinishedWithStatus(FAILED));
        Assert.assertEquals(1, exitStatus.exitStatus());
    }

    @Test
    public void strict_wip_with_failed_scenarios() {
        createStrictWipRuntime();
        bus.send(testCaseFinishedWithStatus(FAILED));
        Assert.assertEquals(0, exitStatus.exitStatus());
    }

    @Test
    public void strict_wip_with_passed_failed_scenarios() {
        createStrictWipRuntime();
        bus.send(testCaseFinishedWithStatus(PASSED));
        bus.send(testCaseFinishedWithStatus(FAILED));
        Assert.assertEquals(1, exitStatus.exitStatus());
    }

    @Test
    public void strict_wip_with_passed_scenarios() {
        createStrictWipRuntime();
        bus.send(testCaseFinishedWithStatus(PASSED));
        Assert.assertEquals(1, exitStatus.exitStatus());
    }

    @Test
    public void strict_wip_with_pending_scenarios() {
        createStrictWipRuntime();
        bus.send(testCaseFinishedWithStatus(PENDING));
        Assert.assertEquals(0, exitStatus.exitStatus());
    }

    @Test
    public void strict_wip_with_skipped_scenarios() {
        createNonStrictWipExitStatus();
        bus.send(testCaseFinishedWithStatus(SKIPPED));
        Assert.assertEquals(0, exitStatus.exitStatus());
    }

    @Test
    public void strict_wip_with_undefined_scenarios() {
        createStrictWipRuntime();
        bus.send(testCaseFinishedWithStatus(UNDEFINED));
        Assert.assertEquals(0, exitStatus.exitStatus());
    }

    @Test
    public void strict_with_ambiguous_scenarios() {
        createStrictRuntime();
        bus.send(testCaseFinishedWithStatus(AMBIGUOUS));
        Assert.assertEquals(1, exitStatus.exitStatus());
    }

    @Test
    public void strict_with_failed_failed_scenarios() {
        createStrictRuntime();
        bus.send(testCaseFinishedWithStatus(FAILED));
        bus.send(testCaseFinishedWithStatus(FAILED));
        Assert.assertEquals(1, exitStatus.exitStatus());
    }

    @Test
    public void strict_with_failed_passed_scenarios() {
        createStrictRuntime();
        bus.send(testCaseFinishedWithStatus(FAILED));
        bus.send(testCaseFinishedWithStatus(PASSED));
        Assert.assertEquals(1, exitStatus.exitStatus());
    }

    @Test
    public void strict_with_failed_scenarios() {
        createStrictRuntime();
        bus.send(testCaseFinishedWithStatus(FAILED));
        Assert.assertEquals(1, exitStatus.exitStatus());
    }

    @Test
    public void strict_with_passed_failed_scenarios() {
        createStrictRuntime();
        bus.send(testCaseFinishedWithStatus(PASSED));
        bus.send(testCaseFinishedWithStatus(FAILED));
        Assert.assertEquals(1, exitStatus.exitStatus());
    }

    @Test
    public void strict_with_passed_passed_scenarios() {
        createStrictRuntime();
        bus.send(testCaseFinishedWithStatus(PASSED));
        bus.send(testCaseFinishedWithStatus(PASSED));
        Assert.assertEquals(0, exitStatus.exitStatus());
    }

    @Test
    public void strict_with_passed_scenarios() {
        createStrictRuntime();
        bus.send(testCaseFinishedWithStatus(PASSED));
        Assert.assertEquals(0, exitStatus.exitStatus());
    }

    @Test
    public void strict_with_pending_scenarios() {
        createStrictRuntime();
        bus.send(testCaseFinishedWithStatus(PENDING));
        Assert.assertEquals(1, exitStatus.exitStatus());
    }

    @Test
    public void strict_with_skipped_scenarios() {
        createNonStrictExitStatus();
        bus.send(testCaseFinishedWithStatus(SKIPPED));
        Assert.assertEquals(0, exitStatus.exitStatus());
    }

    @Test
    public void strict_with_undefined_scenarios() {
        createStrictRuntime();
        bus.send(testCaseFinishedWithStatus(UNDEFINED));
        Assert.assertEquals(1, exitStatus.exitStatus());
    }
}

