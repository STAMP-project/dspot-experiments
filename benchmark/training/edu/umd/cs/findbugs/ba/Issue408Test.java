package edu.umd.cs.findbugs.ba;


import edu.umd.cs.findbugs.AbstractIntegrationTest;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcher;
import edu.umd.cs.findbugs.test.matcher.BugInstanceMatcherBuilder;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 *
 *
 * @see <a href="https://github.com/spotbugs/spotbugs/issues/408">#408</a>
 * @since 3.1
 */
public class Issue408Test extends AbstractIntegrationTest {
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void testSingleClass() {
        Assume.assumeThat(System.getProperty("java.specification.version"), Matchers.is("9"));
        expected.expect(AssertionError.class);
        expected.expectMessage("Analysis failed with exception");
        expected.expectCause(Matchers.is(CoreMatchers.instanceOf(IOException.class)));
        performAnalysis("../java9/module-info.class");
    }

    @Test
    public void testFewClasses() {
        Assume.assumeThat(System.getProperty("java.specification.version"), Matchers.is("9"));
        performAnalysis("../java9/module-info.class", "../java9/Issue408.class");
        BugInstanceMatcher bugTypeMatcher = new BugInstanceMatcherBuilder().build();
        Assert.assertThat(getBugCollection(), containsExactly(0, bugTypeMatcher));
    }
}

