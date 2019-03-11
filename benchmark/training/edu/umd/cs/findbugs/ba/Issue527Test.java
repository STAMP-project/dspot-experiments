package edu.umd.cs.findbugs.ba;


import edu.umd.cs.findbugs.AbstractIntegrationTest;
import edu.umd.cs.findbugs.SortedBugCollection;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcher;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcherBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;


public class Issue527Test extends AbstractIntegrationTest {
    @Test
    public void testSimpleLambdas() {
        performAnalysis("lambdas/Issue527.class");
        final BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("NP_NULL_ON_SOME_PATH").build();
        SortedBugCollection bugCollection = ((SortedBugCollection) (getBugCollection()));
        Assert.assertThat(bugCollection, containsExactly(2, bugTypeMatcher));
        Iterator<String> missingIter = bugCollection.missingClassIterator();
        List<String> strings = new ArrayList<>();
        missingIter.forEachRemaining(( x) -> strings.add(x));
        Assert.assertEquals(Collections.EMPTY_LIST, strings);
    }
}

