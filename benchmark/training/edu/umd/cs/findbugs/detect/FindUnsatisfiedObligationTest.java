package edu.umd.cs.findbugs.detect;


import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.test.SpotBugsRule;
import java.nio.file.Paths;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class FindUnsatisfiedObligationTest {
    @Rule
    public SpotBugsRule spotbugs = new SpotBugsRule();

    /**
     *
     *
     * @see <a href="https://github.com/spotbugs/spotbugs/issues/60">GitHub
    issue</a>
     */
    @Test
    public void testIssue60() {
        BugCollection bugCollection = spotbugs.performAnalysis(Paths.get("../spotbugsTestCases/build/classes/java/main/lambdas/Issue60.class"));
        Assert.assertThat(bugCollection, Is.is(emptyIterable()));
    }
}

