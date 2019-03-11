package edu.umd.cs.findbugs.detect;


import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.SortedBugCollection;
import edu.umd.cs.findbugs.test.SpotBugsRule;
import java.nio.file.Paths;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class ResolveMethodReferencesTest {
    @Rule
    public SpotBugsRule spotbugs = new SpotBugsRule();

    /**
     *
     *
     * @see <a href="https://github.com/spotbugs/spotbugs/issues/338">GitHub
    issue</a>
     */
    @Test
    public void testIssue338() {
        BugCollection bugCollection = spotbugs.performAnalysis(Paths.get("../spotbugsTestCases/build/classes/java/main/lambdas/Issue338.class"));
        Assert.assertThat(bugCollection, Is.is(emptyIterable()));
        Assert.assertThat(bugCollection, Matchers.instanceOf(SortedBugCollection.class));
        Assert.assertThat(missingClassIterator().hasNext(), Is.is(false));
    }
}

