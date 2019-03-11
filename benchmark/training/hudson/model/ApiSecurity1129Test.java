package hudson.model;


import HttpServletResponse.SC_BAD_REQUEST;
import HttpServletResponse.SC_OK;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;


// TODO after the security fix, it could be merged inside ApiTest
public class ApiSecurity1129Test {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    /**
     * Test the wrapper parameter for the api/xml urls to avoid XSS.
     *
     * @throws Exception
     * 		See {@link #checkWrapperParam(String, Integer, String)}
     */
    @Issue("SECURITY-1129")
    @Test
    public void wrapperXss() throws Exception {
        String wrapper = "html%20xmlns=\"http://www.w3.org/1999/xhtml\"><script>alert(%27XSS%20Detected%27)</script></html><!--";
        checkWrapperParam(wrapper, SC_BAD_REQUEST, Messages.Api_WrapperParamInvalid());
    }

    /**
     * Test the wrapper parameter for the api/xml urls with a bad name.
     *
     * @throws Exception
     * 		See {@link #checkWrapperParam(String, Integer, String)}
     */
    @Issue("SECURITY-1129")
    @Test
    public void wrapperBadName() throws Exception {
        String wrapper = "-badname";
        checkWrapperParam(wrapper, SC_BAD_REQUEST, Messages.Api_WrapperParamInvalid());
    }

    /**
     * Test thw erapper parameter with a good name, to ensure the security fix doesn't break anything.
     *
     * @throws Exception
     * 		See {@link #checkWrapperParam(String, Integer, String)}
     */
    @Issue("SECURITY-1129")
    @Test
    public void wrapperGoodName() throws Exception {
        String wrapper = "__GoodName-..-OK";
        checkWrapperParam(wrapper, SC_OK, null);
    }
}

