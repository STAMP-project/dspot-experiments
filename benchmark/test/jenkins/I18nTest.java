/**
 * The MIT License
 *
 * Copyright (c) 2015, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jenkins;


import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import java.io.IOException;
import net.sf.json.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.xml.sax.SAXException;


/**
 *
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class I18nTest {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void test_baseName_unspecified() throws IOException, SAXException {
        JSONObject response = jenkinsRule.getJSON("i18n/resourceBundle").getJSONObject();
        Assert.assertEquals("error", response.getString("status"));
        Assert.assertEquals("Mandatory parameter 'baseName' not specified.", response.getString("message"));
    }

    @Test
    public void test_baseName_unknown() throws IOException, SAXException {
        try {
            JSONObject response = jenkinsRule.getJSON("i18n/resourceBundle?baseName=com.acme.XyzWhatever").getJSONObject();
        } catch (FailingHttpStatusCodeException e) {
            Assert.assertNotNull(e);
            Assert.assertTrue(e.getMessage().contains("com.acme.XyzWhatever"));
        }
    }

    @Issue("JENKINS-35270")
    @Test
    public void test_baseName_plugin() throws Exception {
        installDetachedPlugin("credentials");
        installDetachedPlugin("ssh-credentials");
        installDetachedPlugin("ssh-slaves");
        JSONObject response = jenkinsRule.getJSON("i18n/resourceBundle?baseName=hudson.plugins.sshslaves.Messages").getJSONObject();
        Assert.assertEquals(response.toString(), "ok", response.getString("status"));
        JSONObject data = response.getJSONObject("data");
        Assert.assertEquals("The launch timeout must be a number.", data.getString("SSHConnector.LaunchTimeoutMustBeANumber"));
    }

    @Test
    public void test_valid() throws IOException, SAXException {
        JSONObject response = jenkinsRule.getJSON("i18n/resourceBundle?baseName=hudson.logging.Messages&language=de").getJSONObject();
        Assert.assertEquals("ok", response.getString("status"));
        JSONObject data = response.getJSONObject("data");
        Assert.assertEquals("Initialisiere Log-Rekorder", data.getString("LogRecorderManager.init"));
    }

    // variant testing
    @Issue("JENKINS-39034")
    @Test
    public void test_valid_region_variant() throws IOException, SAXException {
        JSONObject response = jenkinsRule.getJSON("i18n/resourceBundle?baseName=jenkins.i18n.Messages&language=en_AU_variant").getJSONObject();
        Assert.assertEquals("ok", response.getString("status"));
        JSONObject data = response.getJSONObject("data");
        Assert.assertEquals("value_au_variant", data.getString("Key"));
    }

    // country testing with delimiter '-' instead of '_'
    @Issue("JENKINS-39034")
    @Test
    public void test_valid_region() throws IOException, SAXException {
        JSONObject response = jenkinsRule.getJSON("i18n/resourceBundle?baseName=jenkins.i18n.Messages&language=en-AU").getJSONObject();
        Assert.assertEquals("ok", response.getString("status"));
        JSONObject data = response.getJSONObject("data");
        Assert.assertEquals("value_au", data.getString("Key"));
    }

    // fallthrough to default language if variant does not exit
    @Issue("JENKINS-39034")
    @Test
    public void test_valid_fallback() throws IOException, SAXException {
        JSONObject response = jenkinsRule.getJSON("i18n/resourceBundle?baseName=jenkins.i18n.Messages&language=en_NZ_variant").getJSONObject();
        Assert.assertEquals("ok", response.getString("status"));
        JSONObject data = response.getJSONObject("data");
        Assert.assertEquals("value", data.getString("Key"));
    }
}

