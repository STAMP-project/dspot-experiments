package edu.umd.cs.findbugs.ba;


import edu.umd.cs.findbugs.AbstractIntegrationTest;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcher;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcherBuilder;
import org.junit.Assert;
import org.junit.Test;


public class Issue371Test extends AbstractIntegrationTest {
    @Test
    public void test() {
        performAnalysis("lambdas/Issue371.class");
        final BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().bugType("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE").build();
        Assert.assertThat(getBugCollection(), containsExactly(1, bugTypeMatcher));
    }
}

