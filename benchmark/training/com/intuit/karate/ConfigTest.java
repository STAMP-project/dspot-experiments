package com.intuit.karate;


import com.intuit.karate.core.FeatureContext;
import com.intuit.karate.core.ScenarioContext;
import java.nio.file.Path;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author pthomas3
 */
public class ConfigTest {
    @Test
    public void testSettingVariableViaKarateConfig() {
        Path featureDir = FileUtils.getPathContaining(getClass());
        FeatureContext featureContext = FeatureContext.forWorkingDir(featureDir.toFile());
        CallContext callContext = new CallContext(null, true);
        ScenarioContext ctx = new ScenarioContext(featureContext, callContext, null);
        ScriptValue value = Script.evalJsExpression("someConfig", ctx);
        Assert.assertEquals("someValue", value.getValue());
    }
}

