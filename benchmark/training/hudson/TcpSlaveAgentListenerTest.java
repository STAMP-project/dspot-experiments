package hudson;


import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TextPage;
import java.net.HttpURLConnection;
import java.net.URL;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;


public class TcpSlaveAgentListenerTest {
    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Test
    public void headers() throws Exception {
        WebClient wc = r.createWebClient().withThrowExceptionOnFailingStatusCode(false);
        r.getInstance().setSlaveAgentPort((-1));
        wc.assertFails("tcpSlaveAgentListener", HttpURLConnection.HTTP_NOT_FOUND);
        r.getInstance().setSlaveAgentPort(0);
        Page p = wc.goTo("tcpSlaveAgentListener", "text/plain");
        Assert.assertEquals(HttpURLConnection.HTTP_OK, p.getWebResponse().getStatusCode());
        Assert.assertThat(p.getWebResponse().getResponseHeaderValue("X-Instance-Identity"), notNullValue());
    }

    @Test
    public void diagnostics() throws Exception {
        r.getInstance().setSlaveAgentPort(0);
        int p = r.jenkins.getTcpSlaveAgentListener().getPort();
        WebClient wc = r.createWebClient();
        TextPage text = wc.getPage(new URL((("http://localhost:" + p) + "/")));
        String c = text.getContent();
        Assert.assertThat(c, containsString(Jenkins.VERSION));
        wc.setThrowExceptionOnFailingStatusCode(false);
        Page page = wc.getPage(new URL((("http://localhost:" + p) + "/xxx")));
        Assert.assertEquals(HttpURLConnection.HTTP_NOT_FOUND, page.getWebResponse().getStatusCode());
    }
}

