package cucumber.runtime.junit;


import Result.Type.FAILED;
import Result.Type.PASSED;
import Result.Type.PENDING;
import Result.Type.SKIPPED;
import Result.Type.UNDEFINED;
import cucumber.api.PendingException;
import cucumber.api.PickleStepTestStep;
import cucumber.api.Result;
import cucumber.runtime.junit.PickleRunners.PickleRunner;
import gherkin.pickles.PickleStep;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.AssumptionViolatedException;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class JUnitReporterTest {
    private JUnitReporter jUnitReporter;

    private RunNotifier runNotifier;

    @Test
    public void test_case_started_fires_test_started_for_pickle() {
        createNonStrictReporter();
        PickleRunner pickleRunner = mockPickleRunner(Collections.<PickleStep>emptyList());
        runNotifier = Mockito.mock(RunNotifier.class);
        jUnitReporter.startExecutionUnit(pickleRunner, runNotifier);
        jUnitReporter.handleTestCaseStarted();
        Mockito.verify(runNotifier).fireTestStarted(pickleRunner.getDescription());
    }

    @Test
    public void test_step_started_does_not_fire_test_started_for_step_by_default() {
        createNonStrictReporter();
        PickleStep runnerStep = mockStep();
        PickleRunner pickleRunner = mockPickleRunner(runnerSteps(runnerStep));
        runNotifier = Mockito.mock(RunNotifier.class);
        jUnitReporter.startExecutionUnit(pickleRunner, runNotifier);
        jUnitReporter.handleStepStarted(runnerStep);
        Mockito.verify(runNotifier, Mockito.never()).fireTestStarted(pickleRunner.describeChild(runnerStep));
    }

    @Test
    public void test_step_started_fires_test_started_for_step_when_using_step_notifications() {
        createNonStrictReporter("--step-notifications");
        PickleStep runnerStep = mockStep();
        PickleRunner pickleRunner = mockPickleRunner(runnerSteps(runnerStep));
        runNotifier = Mockito.mock(RunNotifier.class);
        jUnitReporter.startExecutionUnit(pickleRunner, runNotifier);
        jUnitReporter.handleStepStarted(runnerStep);
        Mockito.verify(runNotifier).fireTestStarted(pickleRunner.describeChild(runnerStep));
    }

    @Test
    public void test_step_finished_fires_only_test_finished_for_passed_step() {
        createNonStrictReporter();
        createDefaultRunNotifier();
        Description description = Mockito.mock(Description.class);
        setUpStepNotifierAndStepErrors(description);
        Result result = mockResult(PASSED);
        jUnitReporter.handleStepResult(Mockito.mock(PickleStepTestStep.class), result);
        Mockito.verify(runNotifier).fireTestFinished(description);
    }

    @Test
    public void test_step_finished_fires_assumption_failed_and_test_finished_for_skipped_step() {
        createNonStrictReporter();
        createDefaultRunNotifier();
        Description description = Mockito.mock(Description.class);
        setUpStepNotifierAndStepErrors(description);
        Result result = mockResult(SKIPPED);
        jUnitReporter.handleStepResult(Mockito.mock(PickleStepTestStep.class), result);
        ArgumentCaptor<Failure> failureArgumentCaptor = ArgumentCaptor.forClass(Failure.class);
        Mockito.verify(runNotifier).fireTestAssumptionFailed(failureArgumentCaptor.capture());
        Mockito.verify(runNotifier).fireTestFinished(description);
        Failure failure = failureArgumentCaptor.getValue();
        Assert.assertEquals(description, failure.getDescription());
        Assert.assertTrue(((failure.getException()) instanceof SkippedThrowable));
        Assert.assertEquals("This step is skipped", failure.getException().getMessage());
    }

    @Test
    public void test_step_finished_fires_assumption_failed_and_test_finished_for_skipped_step_with_assumption_violated() {
        createNonStrictReporter();
        createDefaultRunNotifier();
        Description description = Mockito.mock(Description.class);
        setUpStepNotifierAndStepErrors(description);
        Throwable exception = new AssumptionViolatedException("Oops");
        Result result = mockResult(SKIPPED, exception);
        jUnitReporter.handleStepResult(Mockito.mock(PickleStepTestStep.class), result);
        ArgumentCaptor<Failure> failureArgumentCaptor = ArgumentCaptor.forClass(Failure.class);
        Mockito.verify(runNotifier).fireTestAssumptionFailed(failureArgumentCaptor.capture());
        Mockito.verify(runNotifier).fireTestFinished(description);
        Failure failure = failureArgumentCaptor.getValue();
        Assert.assertEquals(description, failure.getDescription());
        Assert.assertEquals(exception, failure.getException());
    }

    @Test
    public void test_step_finished_adds_no_step_exeption_for_skipped_step_without_exception() {
        createNonStrictReporter();
        createDefaultRunNotifier();
        setUpNoStepNotifierAndStepErrors();
        Result result = mockResult(SKIPPED);
        jUnitReporter.handleStepResult(Mockito.mock(PickleStepTestStep.class), result);
        Assert.assertTrue(jUnitReporter.stepErrors.isEmpty());
    }

    @Test
    public void test_step_finished_adds_the_step_exeption_for_skipped_step_with_assumption_violated() {
        createNonStrictReporter();
        createDefaultRunNotifier();
        setUpNoStepNotifierAndStepErrors();
        Throwable exception = new AssumptionViolatedException("Oops");
        Result result = mockResult(SKIPPED, exception);
        jUnitReporter.handleStepResult(Mockito.mock(PickleStepTestStep.class), result);
        Assert.assertEquals(Arrays.asList(exception), jUnitReporter.stepErrors);
    }

    @Test
    public void test_step_finished_fires_assumption_failed_and_test_finished_for_pending_step_in_non_strict_mode() {
        createNonStrictReporter();
        createDefaultRunNotifier();
        Description description = Mockito.mock(Description.class);
        setUpStepNotifierAndStepErrors(description);
        Throwable exception = new PendingException();
        Result result = mockResult(PENDING, exception);
        jUnitReporter.handleStepResult(Mockito.mock(PickleStepTestStep.class), result);
        ArgumentCaptor<Failure> failureArgumentCaptor = ArgumentCaptor.forClass(Failure.class);
        Mockito.verify(runNotifier).fireTestAssumptionFailed(failureArgumentCaptor.capture());
        Mockito.verify(runNotifier).fireTestFinished(description);
        Failure failure = failureArgumentCaptor.getValue();
        Assert.assertEquals(description, failure.getDescription());
        Assert.assertEquals(exception, failure.getException());
    }

    @Test
    public void test_step_finished_fires_assumption_failed_and_test_finished_for_pending_step_in_strict_mode() {
        createStrictReporter();
        createDefaultRunNotifier();
        Description description = Mockito.mock(Description.class);
        setUpStepNotifierAndStepErrors(description);
        Throwable exception = new PendingException();
        Result result = mockResult(PENDING, exception);
        jUnitReporter.handleStepResult(Mockito.mock(PickleStepTestStep.class), result);
        ArgumentCaptor<Failure> failureArgumentCaptor = ArgumentCaptor.forClass(Failure.class);
        Mockito.verify(runNotifier).fireTestFailure(failureArgumentCaptor.capture());
        Mockito.verify(runNotifier).fireTestFinished(description);
        Failure failure = failureArgumentCaptor.getValue();
        Assert.assertEquals(description, failure.getDescription());
        Assert.assertEquals(exception, failure.getException());
    }

    @Test
    public void test_step_finished_adds_the_step_exeption_for_pending_steps() {
        createNonStrictReporter();
        createDefaultRunNotifier();
        setUpNoStepNotifierAndStepErrors();
        Throwable exception = new PendingException();
        Result result = mockResult(PENDING, exception);
        jUnitReporter.handleStepResult(Mockito.mock(PickleStepTestStep.class), result);
        Assert.assertEquals(Arrays.asList(exception), jUnitReporter.stepErrors);
    }

    @Test
    public void test_step_finished_fires_assumption_failed_and_test_finished_for_undefined_step_in_non_strict_mode() {
        createNonStrictReporter();
        createDefaultRunNotifier();
        Description description = Mockito.mock(Description.class);
        setUpStepNotifierAndStepErrors(description);
        Result result = mockResult(UNDEFINED);
        jUnitReporter.handleStepResult(Mockito.mock(PickleStepTestStep.class), result);
        ArgumentCaptor<Failure> failureArgumentCaptor = ArgumentCaptor.forClass(Failure.class);
        Mockito.verify(runNotifier).fireTestAssumptionFailed(failureArgumentCaptor.capture());
        Mockito.verify(runNotifier).fireTestFinished(description);
        Failure failure = failureArgumentCaptor.getValue();
        Assert.assertEquals(description, failure.getDescription());
        Assert.assertTrue(((failure.getException()) instanceof UndefinedThrowable));
        Assert.assertEquals("This step is undefined", failure.getException().getMessage());
    }

    @Test
    public void test_step_finished_fires_failure_and_test_finished_for_undefined_step_in_strict_mode() {
        createStrictReporter();
        createDefaultRunNotifier();
        Description description = Mockito.mock(Description.class);
        setUpStepNotifierAndStepErrors(description);
        Result result = mockResult(UNDEFINED);
        jUnitReporter.handleStepResult(Mockito.mock(PickleStepTestStep.class), result);
        ArgumentCaptor<Failure> failureArgumentCaptor = ArgumentCaptor.forClass(Failure.class);
        Mockito.verify(runNotifier).fireTestFailure(failureArgumentCaptor.capture());
        Mockito.verify(runNotifier).fireTestFinished(description);
        Failure failure = failureArgumentCaptor.getValue();
        Assert.assertEquals(description, failure.getDescription());
        Assert.assertTrue(((failure.getException()) instanceof UndefinedThrowable));
        Assert.assertEquals("This step is undefined", failure.getException().getMessage());
    }

    @Test
    public void test_step_finished_adds_a_step_exeption_for_undefined_steps() {
        createNonStrictReporter();
        createDefaultRunNotifier();
        setUpNoStepNotifierAndStepErrors();
        PickleStepTestStep testStep = mockTestStep("XX");
        Result result = mockResult(UNDEFINED);
        jUnitReporter.handleStepResult(testStep, result);
        Assert.assertFalse(jUnitReporter.stepErrors.isEmpty());
        Assert.assertEquals("The step \"XX\" is undefined", jUnitReporter.stepErrors.get(0).getMessage());
    }

    @Test
    public void test_step_finished_fires_failure_and_test_finished_for_failed_step() {
        createNonStrictReporter();
        createDefaultRunNotifier();
        Description description = Mockito.mock(Description.class);
        setUpStepNotifierAndStepErrors(description);
        Throwable exception = Mockito.mock(Throwable.class);
        Result result = mockResult(FAILED, exception);
        jUnitReporter.handleStepResult(Mockito.mock(PickleStepTestStep.class), result);
        ArgumentCaptor<Failure> failureArgumentCaptor = ArgumentCaptor.forClass(Failure.class);
        Mockito.verify(runNotifier).fireTestFailure(failureArgumentCaptor.capture());
        Mockito.verify(runNotifier).fireTestFinished(description);
        Failure failure = failureArgumentCaptor.getValue();
        Assert.assertEquals(description, failure.getDescription());
        Assert.assertEquals(exception, failure.getException());
    }

    @Test
    public void test_step_finished_adds_the_step_exeption_for_failed_steps() {
        createNonStrictReporter();
        createDefaultRunNotifier();
        setUpNoStepNotifierAndStepErrors();
        Throwable exception = new PendingException();
        Result result = mockResult(FAILED, exception);
        jUnitReporter.handleStepResult(Mockito.mock(PickleStepTestStep.class), result);
        Assert.assertEquals(Arrays.asList(exception), jUnitReporter.stepErrors);
    }

    @Test
    public void test_case_finished_fires_only_test_finished_for_passed_step() {
        createNonStrictReporter();
        Description description = Mockito.mock(Description.class);
        createRunNotifier(description);
        Result result = mockResult(PASSED);
        jUnitReporter.handleTestCaseResult(result);
        Mockito.verify(runNotifier).fireTestFinished(description);
    }

    @Test
    public void test_case_finished_fires_assumption_failed_and_test_finished_for_skipped_step() {
        createNonStrictReporter();
        Description description = Mockito.mock(Description.class);
        createRunNotifier(description);
        populateStepErrors(Collections.<Throwable>emptyList());
        Result result = mockResult(SKIPPED);
        jUnitReporter.handleTestCaseResult(result);
        ArgumentCaptor<Failure> failureArgumentCaptor = ArgumentCaptor.forClass(Failure.class);
        Mockito.verify(runNotifier).fireTestAssumptionFailed(failureArgumentCaptor.capture());
        Mockito.verify(runNotifier).fireTestFinished(description);
        Failure failure = failureArgumentCaptor.getValue();
        Assert.assertEquals(description, failure.getDescription());
        Assert.assertTrue(((failure.getException()) instanceof SkippedThrowable));
    }

    @Test
    public void test_case_finished_fires_assumption_failed_and_test_finished_for_skipped_step_with_assumption_violated() {
        createNonStrictReporter();
        Description description = Mockito.mock(Description.class);
        createRunNotifier(description);
        Throwable exception1 = Mockito.mock(AssumptionViolatedException.class);
        Throwable exception2 = Mockito.mock(AssumptionViolatedException.class);
        populateStepErrors(Arrays.asList(exception1, exception2));
        Result result = mockResult(SKIPPED);
        jUnitReporter.handleTestCaseResult(result);
        ArgumentCaptor<Failure> failureArgumentCaptor = ArgumentCaptor.forClass(Failure.class);
        Mockito.verify(runNotifier, Mockito.times(2)).fireTestAssumptionFailed(failureArgumentCaptor.capture());
        Mockito.verify(runNotifier).fireTestFinished(description);
        List<Failure> failures = failureArgumentCaptor.getAllValues();
        Assert.assertEquals(description, failures.get(0).getDescription());
        Assert.assertEquals(exception1, failures.get(0).getException());
        Assert.assertEquals(description, failures.get(1).getDescription());
        Assert.assertEquals(exception2, failures.get(1).getException());
    }

    @Test
    public void test_case_finished_fires_assumption_failed_and_test_finished_for_pending_step_in_non_strict_mode() {
        createNonStrictReporter();
        Description description = Mockito.mock(Description.class);
        createRunNotifier(description);
        Throwable exception1 = Mockito.mock(Throwable.class);
        Throwable exception2 = Mockito.mock(Throwable.class);
        populateStepErrors(Arrays.asList(exception1, exception2));
        Result result = mockResult(PENDING);
        jUnitReporter.handleTestCaseResult(result);
        ArgumentCaptor<Failure> failureArgumentCaptor = ArgumentCaptor.forClass(Failure.class);
        Mockito.verify(runNotifier, Mockito.times(2)).fireTestAssumptionFailed(failureArgumentCaptor.capture());
        Mockito.verify(runNotifier).fireTestFinished(description);
        List<Failure> failures = failureArgumentCaptor.getAllValues();
        Assert.assertEquals(description, failures.get(0).getDescription());
        Assert.assertEquals(exception1, failures.get(0).getException());
        Assert.assertEquals(description, failures.get(1).getDescription());
        Assert.assertEquals(exception2, failures.get(1).getException());
    }

    @Test
    public void test_case_finished_fires_failure_and_test_finished_for_pending_step_in_strict_mode() {
        createStrictReporter();
        Description description = Mockito.mock(Description.class);
        createRunNotifier(description);
        Throwable exception1 = Mockito.mock(Throwable.class);
        Throwable exception2 = Mockito.mock(Throwable.class);
        populateStepErrors(Arrays.asList(exception1, exception2));
        Result result = mockResult(PENDING);
        jUnitReporter.handleTestCaseResult(result);
        ArgumentCaptor<Failure> failureArgumentCaptor = ArgumentCaptor.forClass(Failure.class);
        Mockito.verify(runNotifier, Mockito.times(2)).fireTestFailure(failureArgumentCaptor.capture());
        Mockito.verify(runNotifier).fireTestFinished(description);
        List<Failure> failures = failureArgumentCaptor.getAllValues();
        Assert.assertEquals(description, failures.get(0).getDescription());
        Assert.assertEquals(exception1, failures.get(0).getException());
        Assert.assertEquals(description, failures.get(1).getDescription());
        Assert.assertEquals(exception2, failures.get(1).getException());
    }

    @Test
    public void test_case_finished_fires_assumption_failed_and_test_finished_for_undefined_step_in_non_strict_mode() {
        createNonStrictReporter();
        Description description = Mockito.mock(Description.class);
        createRunNotifier(description);
        Throwable exception1 = Mockito.mock(Throwable.class);
        Throwable exception2 = Mockito.mock(Throwable.class);
        populateStepErrors(Arrays.asList(exception1, exception2));
        Result result = mockResult(UNDEFINED);
        jUnitReporter.handleTestCaseResult(result);
        ArgumentCaptor<Failure> failureArgumentCaptor = ArgumentCaptor.forClass(Failure.class);
        Mockito.verify(runNotifier, Mockito.times(2)).fireTestAssumptionFailed(failureArgumentCaptor.capture());
        Mockito.verify(runNotifier).fireTestFinished(description);
        List<Failure> failures = failureArgumentCaptor.getAllValues();
        Assert.assertEquals(description, failures.get(0).getDescription());
        Assert.assertEquals(exception1, failures.get(0).getException());
        Assert.assertEquals(description, failures.get(1).getDescription());
        Assert.assertEquals(exception2, failures.get(1).getException());
    }

    @Test
    public void test_case_finished_fires_failure_and_test_finished_for_undefined_step_in_strict_mode() {
        createStrictReporter();
        Description description = Mockito.mock(Description.class);
        createRunNotifier(description);
        Throwable exception1 = Mockito.mock(Throwable.class);
        Throwable exception2 = Mockito.mock(Throwable.class);
        populateStepErrors(Arrays.asList(exception1, exception2));
        Result result = mockResult(UNDEFINED);
        jUnitReporter.handleTestCaseResult(result);
        ArgumentCaptor<Failure> failureArgumentCaptor = ArgumentCaptor.forClass(Failure.class);
        Mockito.verify(runNotifier, Mockito.times(2)).fireTestFailure(failureArgumentCaptor.capture());
        Mockito.verify(runNotifier).fireTestFinished(description);
        List<Failure> failures = failureArgumentCaptor.getAllValues();
        Assert.assertEquals(description, failures.get(0).getDescription());
        Assert.assertEquals(exception1, failures.get(0).getException());
        Assert.assertEquals(description, failures.get(1).getDescription());
        Assert.assertEquals(exception2, failures.get(1).getException());
    }

    @Test
    public void test_case_finished_fires_failure_and_test_finished_for_failed_step() {
        createNonStrictReporter();
        Description description = Mockito.mock(Description.class);
        createRunNotifier(description);
        Throwable exception1 = Mockito.mock(Throwable.class);
        Throwable exception2 = Mockito.mock(Throwable.class);
        populateStepErrors(Arrays.asList(exception1, exception2));
        Result result = mockResult(FAILED);
        jUnitReporter.handleTestCaseResult(result);
        ArgumentCaptor<Failure> failureArgumentCaptor = ArgumentCaptor.forClass(Failure.class);
        Mockito.verify(runNotifier, Mockito.times(2)).fireTestFailure(failureArgumentCaptor.capture());
        Mockito.verify(runNotifier).fireTestFinished(description);
        List<Failure> failures = failureArgumentCaptor.getAllValues();
        Assert.assertEquals(description, failures.get(0).getDescription());
        Assert.assertEquals(exception1, failures.get(0).getException());
        Assert.assertEquals(description, failures.get(1).getDescription());
        Assert.assertEquals(exception2, failures.get(1).getException());
    }
}

