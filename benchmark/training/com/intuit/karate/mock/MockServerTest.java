package com.intuit.karate.mock;


import com.intuit.karate.KarateOptions;
import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author pthomas3
 */
@KarateOptions(tags = "~@ignore")
public class MockServerTest {
    private static FeatureServer server;

    public static final byte[] testBytes = new byte[]{ 15, 98, -45, 0, 0, 7, -124, 75, 12, 26, 0, 9 };

    @Test
    public void testServer() {
        // will run all features in 'this' package
        String karateOutputPath = "target/surefire-reports";
        Results results = Runner.parallel(getClass(), 1, karateOutputPath);
        MockServerTest.generateReport(karateOutputPath);
        Assert.assertTrue("there are scenario failures", ((results.getFailCount()) == 0));
    }
}

