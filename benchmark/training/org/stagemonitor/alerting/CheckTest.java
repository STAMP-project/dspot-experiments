package org.stagemonitor.alerting;


import CheckResult.Status.CRITICAL;
import CheckResult.Status.ERROR;
import CheckResult.Status.OK;
import CheckResult.Status.WARN;
import MetricValueType.MEAN;
import Threshold.Operator;
import Threshold.Operator.GREATER_EQUAL;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.alerting.check.Check;
import org.stagemonitor.alerting.check.CheckResult;
import org.stagemonitor.core.util.JsonUtils;


public class CheckTest {
    private Check check;

    @Test
    public void testCheckOK() throws Exception {
        Assert.assertEquals(0, check.check(name("test").build(), Collections.singletonMap("value", 0.0)).size());
    }

    @Test
    public void testCheckWarn() throws Exception {
        CheckResult result = check.check(name("test").build(), Collections.singletonMap("value", 1.5)).iterator().next();
        Assert.assertEquals("test value <= 1.0 is false", result.getFailingExpression());
        Assert.assertEquals(1.5, result.getCurrentValue(), 0);
        Assert.assertEquals(WARN, result.getStatus());
    }

    @Test
    public void testCheckError() throws Exception {
        CheckResult result = check.check(name("test").build(), Collections.singletonMap("value", 2.5)).iterator().next();
        Assert.assertEquals("test value <= 2.0 is false", result.getFailingExpression());
        Assert.assertEquals(2.5, result.getCurrentValue(), 0);
        Assert.assertEquals(ERROR, result.getStatus());
    }

    @Test
    public void testCheckCritical() throws Exception {
        CheckResult result = check.check(name("test").build(), Collections.singletonMap("value", 3.5)).iterator().next();
        Assert.assertEquals("test value <= 3.0 is false", result.getFailingExpression());
        Assert.assertEquals(3.5, result.getCurrentValue(), 0);
        Assert.assertEquals(CRITICAL, result.getStatus());
    }

    @Test
    public void testCheckCriticalFromJson() throws Exception {
        Check checkFromJson = JsonUtils.getMapper().readValue(("{\"id\":\"50d3063f-437f-431c-bbf5-601ea0943cdf\"," + (((((((("\"name\":null," + "\"target\":null,") + "\"alertAfterXFailures\":1,") + "\"thresholds\":{") + "\"ERROR\":[{\"valueType\":\"VALUE\",\"operator\":\"LESS_EQUAL\",\"thresholdValue\":2.0}],") + // WARN is not in list
        // CRITICAL is last in list
        "\"CRITICAL\":[{\"valueType\":\"VALUE\",\"operator\":\"LESS_EQUAL\",\"thresholdValue\":3.0}]") + "},") + "\"application\":null,") + "\"active\":true}")), Check.class);
        CheckResult result = checkFromJson.check(name("test").build(), Collections.singletonMap("value", 3.5)).iterator().next();
        Assert.assertEquals(CRITICAL, result.getStatus());
        Assert.assertEquals("test value <= 3.0 is false", result.getFailingExpression());
        Assert.assertEquals(3.5, result.getCurrentValue(), 0);
    }

    @Test
    public void testGetMostSevereStatus() {
        Assert.assertEquals(OK, CheckResult.getMostSevereStatus(Collections.<CheckResult>emptyList()));
    }

    @Test
    public void testJson() throws Exception {
        Check check = new Check();
        check.setName("Test Timer");
        check.setTarget(name("timer").tag("foo", "bar").tag("qux", "quux").build());
        check.setAlertAfterXFailures(2);
        check.getWarn().add(new org.stagemonitor.alerting.check.Threshold("mean", Operator.GREATER_EQUAL, 3));
        final String json = JsonUtils.toJson(check);
        final Check checkFromJson = JsonUtils.getMapper().readValue(json, Check.class);
        Assert.assertEquals("Test Timer", checkFromJson.getName());
        Assert.assertEquals(name("timer").tag("foo", "bar").tag("qux", "quux").build(), checkFromJson.getTarget());
        Assert.assertEquals(2, checkFromJson.getAlertAfterXFailures());
        Assert.assertEquals(1, checkFromJson.getWarn().size());
        Assert.assertEquals(MEAN, checkFromJson.getWarn().get(0).getValueType());
        Assert.assertEquals(GREATER_EQUAL, checkFromJson.getWarn().get(0).getOperator());
        Assert.assertEquals(3, checkFromJson.getWarn().get(0).getThresholdValue(), 0);
        Assert.assertEquals(0, checkFromJson.getError().size());
        Assert.assertEquals(0, checkFromJson.getCritical().size());
        Assert.assertEquals(json, JsonUtils.toJson(checkFromJson));
    }
}

