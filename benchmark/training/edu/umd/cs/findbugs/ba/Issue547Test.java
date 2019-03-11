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


public class Issue547Test extends AbstractIntegrationTest {
    @Test
    public void testLambdas() {
        performAnalysis("lambdas/Issue547.class");
        final BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().build();
        SortedBugCollection bugCollection = ((SortedBugCollection) (getBugCollection()));
        Assert.assertThat(bugCollection, containsExactly(0, bugTypeMatcher));
        Iterator<String> missingIter = bugCollection.missingClassIterator();
        List<String> strings = new ArrayList<>();
        missingIter.forEachRemaining(( x) -> strings.add(x));
        Assert.assertEquals(Collections.EMPTY_LIST, strings);
    }
}

