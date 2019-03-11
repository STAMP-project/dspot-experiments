package edu.umd.cs.findbugs.detect;


import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.test.SpotBugsRule;
import java.nio.file.Paths;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class PreconditionsCheckNotNullCanIgnoreReturnValueTest {
    @Rule
    public SpotBugsRule spotbugs = new SpotBugsRule();

    @Test
    public void testDoNotWarnOnCanIgnoreReturnValue() {
        BugCollection bugCollection = spotbugs.performAnalysis(Paths.get("../spotbugsTestCases/build/classes/java/main/bugPatterns/RV_RETURN_VALUE_IGNORED_Guava_Preconditions.class"));
        Assert.assertThat(bugCollection, Is.is(emptyIterable()));
    }
}

