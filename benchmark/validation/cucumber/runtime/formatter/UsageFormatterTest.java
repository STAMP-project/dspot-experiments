package cucumber.runtime.formatter;


import Result.Type;
import UsageFormatter.StepContainer;
import UsageFormatter.StepDuration;
import UsageFormatter.UsageStatisticStrategy;
import cucumber.api.PickleStepTestStep;
import cucumber.api.Result;
import cucumber.api.TestCase;
import cucumber.api.TestStep;
import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class UsageFormatterTest {
    @Test
    public void close() throws IOException {
        Appendable out = Mockito.mock(Appendable.class, Mockito.withSettings().extraInterfaces(Closeable.class));
        UsageFormatter usageFormatter = new UsageFormatter(out);
        usageFormatter.finishReport();
        Mockito.verify(((Closeable) (out))).close();
    }

    @Test
    public void resultWithoutSkippedSteps() {
        Appendable out = Mockito.mock(Appendable.class);
        UsageFormatter usageFormatter = new UsageFormatter(out);
        Result result = new Result(Type.FAILED, 0L, null);
        usageFormatter.handleTestStepFinished(new cucumber.api.event.TestStepFinished(0L, Mockito.mock(TestCase.class), mockTestStep(), result));
        Mockito.verifyZeroInteractions(out);
    }

    @Test
    public void resultWithStep() {
        Appendable out = Mockito.mock(Appendable.class);
        UsageFormatter usageFormatter = new UsageFormatter(out);
        TestStep testStep = mockTestStep();
        Result result = new Result(Type.PASSED, 12345L, null);
        usageFormatter.handleTestStepFinished(new cucumber.api.event.TestStepFinished(0L, Mockito.mock(TestCase.class), testStep, result));
        Map<String, List<UsageFormatter.StepContainer>> usageMap = usageFormatter.usageMap;
        Assert.assertEquals(usageMap.size(), 1);
        List<UsageFormatter.StepContainer> durationEntries = usageMap.get("stepDef");
        Assert.assertEquals(durationEntries.size(), 1);
        Assert.assertEquals(durationEntries.get(0).name, "step");
        Assert.assertEquals(durationEntries.get(0).durations.size(), 1);
        Assert.assertEquals(durationEntries.get(0).durations.get(0).duration, BigDecimal.valueOf(12345));
    }

    @Test
    public void resultWithZeroDuration() {
        Appendable out = Mockito.mock(Appendable.class);
        UsageFormatter usageFormatter = new UsageFormatter(out);
        TestStep testStep = mockTestStep();
        Result result = new Result(Type.PASSED, 0L, null);
        usageFormatter.handleTestStepFinished(new cucumber.api.event.TestStepFinished(0L, Mockito.mock(TestCase.class), testStep, result));
        Map<String, List<UsageFormatter.StepContainer>> usageMap = usageFormatter.usageMap;
        Assert.assertEquals(usageMap.size(), 1);
        List<UsageFormatter.StepContainer> durationEntries = usageMap.get("stepDef");
        Assert.assertEquals(durationEntries.size(), 1);
        Assert.assertEquals(durationEntries.get(0).name, "step");
        Assert.assertEquals(durationEntries.get(0).durations.size(), 1);
        Assert.assertEquals(durationEntries.get(0).durations.get(0).duration, BigDecimal.ZERO);
    }

    @Test
    public void resultWithNullDuration() {
        Appendable out = Mockito.mock(Appendable.class);
        UsageFormatter usageFormatter = new UsageFormatter(out);
        PickleStepTestStep testStep = mockTestStep();
        Result result = new Result(Type.PASSED, 0L, null);
        usageFormatter.handleTestStepFinished(new cucumber.api.event.TestStepFinished(0L, Mockito.mock(TestCase.class), testStep, result));
        Map<String, List<UsageFormatter.StepContainer>> usageMap = usageFormatter.usageMap;
        Assert.assertEquals(usageMap.size(), 1);
        List<UsageFormatter.StepContainer> durationEntries = usageMap.get("stepDef");
        Assert.assertEquals(durationEntries.size(), 1);
        Assert.assertEquals(durationEntries.get(0).name, "step");
        Assert.assertEquals(durationEntries.get(0).durations.size(), 1);
        Assert.assertEquals(durationEntries.get(0).durations.get(0).duration, BigDecimal.ZERO);
    }

    @Test
    public void doneWithoutUsageStatisticStrategies() throws IOException {
        StringBuffer out = new StringBuffer();
        UsageFormatter usageFormatter = new UsageFormatter(out);
        UsageFormatter.StepContainer stepContainer = new UsageFormatter.StepContainer();
        UsageFormatter.StepDuration stepDuration = new UsageFormatter.StepDuration();
        stepDuration.duration = BigDecimal.valueOf(12345678L);
        stepDuration.location = "location.feature";
        stepContainer.durations = Arrays.asList(stepDuration);
        usageFormatter.usageMap.put("aStep", Arrays.asList(stepContainer));
        usageFormatter.finishReport();
        Assert.assertTrue(out.toString().contains("0.012345678"));
    }

    @Test
    public void doneWithUsageStatisticStrategies() throws IOException {
        StringBuffer out = new StringBuffer();
        UsageFormatter usageFormatter = new UsageFormatter(out);
        UsageFormatter.StepContainer stepContainer = new UsageFormatter.StepContainer();
        UsageFormatter.StepDuration stepDuration = new UsageFormatter.StepDuration();
        stepDuration.duration = BigDecimal.valueOf(12345678L);
        stepDuration.location = "location.feature";
        stepContainer.durations = Arrays.asList(stepDuration);
        usageFormatter.usageMap.put("aStep", Arrays.asList(stepContainer));
        UsageFormatter.UsageStatisticStrategy usageStatisticStrategy = Mockito.mock(UsageStatisticStrategy.class);
        Mockito.when(usageStatisticStrategy.calculate(Arrays.asList(12345678L))).thenReturn(23456L);
        usageFormatter.addUsageStatisticStrategy("average", usageStatisticStrategy);
        usageFormatter.finishReport();
        Assert.assertTrue(out.toString().contains("0.000023456"));
        Assert.assertTrue(out.toString().contains("0.012345678"));
    }
}

