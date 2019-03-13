/**
 * The MIT License
 *
 * Copyright (c) 2013 Red Hat, Inc.
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
package hudson.model;


import Computer.CONFIGURE;
import Computer.CONNECT;
import Computer.EXTENDED_READ;
import hudson.security.AccessDeniedException2;
import hudson.security.GlobalMatrixAuthorizationStrategy;
import java.io.OutputStream;
import org.acegisecurity.context.SecurityContext;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author ogondza
 */
public class ComputerConfigDotXmlTest {
    @Rule
    public final JenkinsRule rule = new JenkinsRule();

    @Mock
    private StaplerRequest req;

    @Mock
    private StaplerResponse rsp;

    private Computer computer;

    private SecurityContext oldSecurityContext;

    @Test(expected = AccessDeniedException2.class)
    public void configXmlGetShouldFailForUnauthorized() throws Exception {
        Mockito.when(req.getMethod()).thenReturn("GET");
        rule.jenkins.setAuthorizationStrategy(new GlobalMatrixAuthorizationStrategy());
        computer.doConfigDotXml(req, rsp);
    }

    @Test(expected = AccessDeniedException2.class)
    public void configXmlPostShouldFailForUnauthorized() throws Exception {
        Mockito.when(req.getMethod()).thenReturn("POST");
        rule.jenkins.setAuthorizationStrategy(new GlobalMatrixAuthorizationStrategy());
        computer.doConfigDotXml(req, rsp);
    }

    @Test
    public void configXmlGetShouldYieldNodeConfiguration() throws Exception {
        Mockito.when(req.getMethod()).thenReturn("GET");
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        rule.jenkins.setAuthorizationStrategy(auth);
        EXTENDED_READ.setEnabled(true);
        auth.add(EXTENDED_READ, "user");
        final OutputStream outputStream = captureOutput();
        computer.doConfigDotXml(req, rsp);
        final String out = outputStream.toString();
        MatcherAssert.assertThat(out, Matchers.startsWith("<?xml version=\"1.1\" encoding=\"UTF-8\"?>"));
        MatcherAssert.assertThat(out, Matchers.containsString("<name>slave0</name>"));
        MatcherAssert.assertThat(out, Matchers.containsString("<description>dummy</description>"));
    }

    @Test
    public void configXmlPostShouldUpdateNodeConfiguration() throws Exception {
        Mockito.when(req.getMethod()).thenReturn("POST");
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        rule.jenkins.setAuthorizationStrategy(auth);
        auth.add(CONFIGURE, "user");
        Mockito.when(req.getInputStream()).thenReturn(xmlNode("node.xml"));
        computer.doConfigDotXml(req, rsp);
        final Node updatedSlave = rule.jenkins.getNode("SlaveFromXML");
        MatcherAssert.assertThat(updatedSlave.getNodeName(), Matchers.equalTo("SlaveFromXML"));
        MatcherAssert.assertThat(updatedSlave.getNumExecutors(), Matchers.equalTo(42));
    }

    @Test
    @Issue("SECURITY-343")
    public void emptyNodeMonitorDataWithoutConnect() throws Exception {
        rule.jenkins.setAuthorizationStrategy(new GlobalMatrixAuthorizationStrategy());
        Assert.assertTrue(computer.getMonitorData().isEmpty());
    }

    @Test
    @Issue("SECURITY-343")
    public void populatedNodeMonitorDataWithConnect() throws Exception {
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        rule.jenkins.setAuthorizationStrategy(auth);
        auth.add(CONNECT, "user");
        Assert.assertFalse(computer.getMonitorData().isEmpty());
    }
}

