package jenkins.security;


import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;


/**
 * Tests about the behavior expected setting different values in the escape-by-default directive and the
 * CustomJellyContext.ESCAPE_BY_DEFAULT field.
 */
public class Security857Test {
    private static String EVIDENCE = "<script> alert";

    @Rule
    public JenkinsRule j = new JenkinsRule();

    /**
     * Test that a jelly is escaped right thanks to the CustomJellyContext.ESCAPE_BY_DEFAULT field. Its default value is true.
     *
     * @throws Exception
     * 		
     */
    @Issue("SECURITY-857")
    @Test
    public void testJellyEscapingTrue() throws Exception {
        testJelly(true);
    }

    /**
     * Test that a jelly is not escaped when the escape-by-default='false' directive is set in it.
     *
     * @throws Exception
     * 		
     */
    @Issue("SECURITY-857")
    @Test
    public void testJellyEscapingFalse() throws Exception {
        testJelly(false);
    }

    /**
     * Test that a jelly is escaped when the escape-by-default='true' directive is set in it.
     *
     * @throws Exception
     * 		
     */
    @Issue("SECURITY-857")
    @Test
    public void testJellyEscapingDefault() throws Exception {
        testJelly(null);
    }
}

