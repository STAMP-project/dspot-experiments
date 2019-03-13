package jenkins.util;


import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;


public class UrlHelperTest {
    @Test
    @Issue("JENKINS-31661")
    public void regularCases() {
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://www.google.com"));
        // trailing slash is optional
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://www.google.com/"));
        // path is allowed
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://www.google.com/jenkins"));
        // port is allowed to be precised
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://www.google.com:8080"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://www.google.com:8080/jenkins"));
        // http or https are only valid schemes
        Assert.assertTrue(UrlHelper.isValidRootUrl("https://www.google.com:8080/jenkins"));
        // also with their UPPERCASE equivalent
        Assert.assertTrue(UrlHelper.isValidRootUrl("HTTP://www.google.com:8080/jenkins"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("HTTPS://www.google.com:8080/jenkins"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://localhost:8080/jenkins"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://localhost:8080/jenkins/"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://my_server:8080/jenkins"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://MY_SERVER_IN_PRIVATE_NETWORK:8080/jenkins"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://jenkins"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://j"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://j.io"));
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://jenkins::"));
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://jenkins::80"));
        // scheme must be correctly spelled (missing :)
        Assert.assertFalse(UrlHelper.isValidRootUrl("http//jenkins"));
        // scheme is mandatory
        Assert.assertFalse(UrlHelper.isValidRootUrl("com."));
        // spaces are forbidden
        Assert.assertFalse(UrlHelper.isValidRootUrl("http:// "));
        // examples not passing with a simple `new URL(url).toURI()` check
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://jenkins//context"));
        Assert.assertFalse(UrlHelper.isValidRootUrl("http:/jenkins"));
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://.com"));
        Assert.assertFalse(UrlHelper.isValidRootUrl("http:/:"));
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://..."));
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://::::@example.com"));
        Assert.assertFalse(UrlHelper.isValidRootUrl("ftp://jenkins"));
    }

    @Test
    public void fragmentIsForbidden() {
        // this url will be used as a root url and so will be concatenated with other part, fragment part is not allowed
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://jenkins#fragment"));
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://jenkins.com#fragment"));
    }

    @Test
    public void queryIsForbidden() {
        // this url will be used as a root url and so will be concatenated with other part, query part is not allowed
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://jenkins?param=test"));
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://jenkins.com?param=test"));
    }

    @Test
    public void otherCharactersAreForbidden() {
        // other characters are not allowed
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://jenk@ins.com"));
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://jenk(ins.com"));
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://jenk)ins.com"));
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://jenk[ins.com"));
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://jenk]ins.com"));
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://jenk%ins.com"));
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://jenk$ins.com"));
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://jenk!ins.com"));
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://jenk?ins.com"));
    }

    @Test
    public void ipv4Allowed() {
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://172.52.125.12"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://172.52.125.12/jenkins"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://172.52.125.12:8080"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://172.52.125.12:8080/jenkins"));
    }

    @Test
    public void ipv6Allowed() {
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://[FEDC:BA98:7654:3210:FEDC:BA98:7654:3210]"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://[FEDC:0000:0000:3210:FEDC:BA98:7654:3210]"));
        // 0000 can be reduced to 0
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://[FEDC:0:0:3210:FEDC:BA98:7654:3210]"));
        // an unique sequence of multiple fragments with 0's could be omitted completely
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://[FEDC::3210:FEDC:BA98:7654:3210]"));
        // but only one sequence
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://[2001::85a3::ac1f]"));
        // port and path are still allowed
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://[FEDC:0:0:3210:FEDC:BA98:7654:3210]:8001/jenkins"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://[FEDC:0:0:3210:FEDC:BA98:7654:3210]:8001"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://[FEDC:0:0:3210:FEDC:BA98:7654:3210]/jenkins"));
        // dashes are not allowed inside ipv6
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://[FEDC:0:0:32-10:FEDC:BA98:7654:3210]:8001/jenkins"));
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://[FEDC:0:0:3210:-FEDC:BA98:7654:3210]:8001/jenkins"));
    }

    @Test
    @Issue("JENKINS-51064")
    public void withCustomDomain() {
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://my-server:8080/jenkins"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://jenkins.internal/"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://jenkins.otherDomain/"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://my-server.domain:8080/jenkins"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://my-ser_ver.do_m-ain:8080/jenkins"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://my-ser_ver.do_m-ain:8080/jenkins"));
        // forbidden to start or end domain with - or .
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://-jenkins.com"));
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://jenkins.com-"));
        Assert.assertFalse(UrlHelper.isValidRootUrl("http://.jenkins.com"));
        // allowed to have multiple dots in chain
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://jen..kins.com"));
    }

    @Test
    public void multipleConsecutiveDashesAreAllowed() {
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://jenk--ins.internal/"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://www.go-----ogle.com/"));
        // even with subdomain being just a dash
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://www.go.-.--.--ogle.com/"));
    }

    @Test
    @Issue("JENKINS-51158")
    public void trailingDotsAreAccepted() {
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://jenkins.internal./"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://jenkins.internal......./"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://my-server.domain.:8080/jenkins"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://my-server.domain......:8080/jenkins"));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://jenkins.com."));
        Assert.assertTrue(UrlHelper.isValidRootUrl("http://jenkins.com......"));
    }
}

