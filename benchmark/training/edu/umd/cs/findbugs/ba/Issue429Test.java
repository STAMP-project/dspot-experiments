package edu.umd.cs.findbugs.ba;


import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.test.SpotBugsRule;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcher;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcherBuilder;
import java.nio.file.Paths;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;


/**
 * Unit test to reproduce <a href="https://github.com/spotbugs/spotbugs/issues/429">#429</a>.
 */
public class Issue429Test {
    @Rule
    public SpotBugsRule spotbugs = new SpotBugsRule();

    @Test
    public void test() {
        String property = System.getProperty("AUX_CLASSPATH");
        Assume.assumeThat(property, Matchers.notNullValue());
        for (String path : property.split(",")) {
            spotbugs.addAuxClasspathEntry(Paths.get(path));
        }
        BugCollection bugCollection = spotbugs.performAnalysis(Paths.get("../spotbugsTestCases/build/classes/java/main/ghIssues/Issue429.class"));
        BugInstanceMatcher matcher = new BugInstanceMatcherBuilder().bugType("RV_RETURN_VALUE_IGNORED").build();
        Assert.assertThat(bugCollection, containsExactly(0, matcher));
    }
}

