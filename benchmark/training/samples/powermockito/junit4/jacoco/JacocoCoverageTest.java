package samples.powermockito.junit4.jacoco;


import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.runtime.RuntimeData;
import org.junit.Test;


public class JacocoCoverageTest {
    public static final String[] TARGET = new String[]{ TargetTest.class.getName(), StaticMethods.class.getName(), InstanceMethods.class.getName() };

    @Test
    public void jacocoOfflineInstShouldCalculateCoverageAfterPowerMockTransformation() throws Exception {
        final RuntimeData data = new RuntimeData();
        runTargetTest(data);
        final CoverageBuilder coverageBuilder = collectCoverage(getExecutionDataStore(data));
        assertCodeCoverage(coverageBuilder);
    }
}

