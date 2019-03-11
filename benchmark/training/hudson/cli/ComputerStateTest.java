/**
 * The MIT License
 *
 * Copyright (c) 2014 Red Hat, Inc.
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
package hudson.cli;


import Computer.CONNECT;
import Computer.DISCONNECT;
import Jenkins.READ;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.cli.CLICommandInvoker.Result;
import hudson.model.Computer;
import hudson.model.Slave;
import hudson.slaves.DumbSlave;
import hudson.slaves.OfflineCause.UserCause;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;


/**
 *
 *
 * @author ogondza
 */
public class ComputerStateTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void connect() throws Exception {
        CLICommandInvoker command = new CLICommandInvoker(j, "connect-node");
        Slave slave = j.createSlave();
        Assert.assertTrue(slave.toComputer().isOffline());
        Result result = command.authorizedTo(READ, CONNECT).invokeWithArgs(slave.getNodeName());
        Assert.assertThat(result, succeededSilently());
        slave.toComputer().waitUntilOnline();
        Assert.assertTrue(slave.toComputer().isOnline());
    }

    @Test
    public void online() throws Exception {
        CLICommandInvoker command = new CLICommandInvoker(j, "online-node");
        Slave slave = j.createSlave();
        Assert.assertTrue(slave.toComputer().isOffline());
        Result result = command.authorizedTo(READ, CONNECT).invokeWithArgs(slave.getNodeName());
        Assert.assertThat(result, succeededSilently());
        slave.toComputer().waitUntilOnline();
        Assert.assertTrue(slave.toComputer().isOnline());
    }

    @Test
    public void disconnect() throws Exception {
        CLICommandInvoker command = new CLICommandInvoker(j, "disconnect-node");
        Slave slave = j.createOnlineSlave();
        Assert.assertTrue(slave.toComputer().isOnline());
        Result result = command.authorizedTo(READ, DISCONNECT).invokeWithArgs(slave.getNodeName(), "-m", "Custom cause message");
        Assert.assertThat(result, succeededSilently());
        Assert.assertTrue(slave.toComputer().isOffline());
        UserCause cause = ((UserCause) (slave.toComputer().getOfflineCause()));
        Assert.assertThat(cause.toString(), Matchers.endsWith("Custom cause message"));
        Assert.assertThat(cause.getUser(), Matchers.equalTo(command.user()));
    }

    @Test
    public void offline() throws Exception {
        CLICommandInvoker command = new CLICommandInvoker(j, "offline-node");
        Slave slave = j.createOnlineSlave();
        Assert.assertTrue(slave.toComputer().isOnline());
        Result result = command.authorizedTo(READ, DISCONNECT).invokeWithArgs(slave.getNodeName(), "-m", "Custom cause message");
        Assert.assertThat(result, succeededSilently());
        Assert.assertTrue(slave.toComputer().isOffline());
        UserCause cause = ((UserCause) (slave.toComputer().getOfflineCause()));
        Assert.assertThat(cause.toString(), Matchers.endsWith("Custom cause message"));
        Assert.assertThat(cause.getUser(), Matchers.equalTo(command.user()));
    }

    @Test
    public void testUiForConnected() throws Exception {
        DumbSlave slave = j.createOnlineSlave();
        Computer computer = slave.toComputer();
        WebClient wc = j.createWebClient();
        assertConnected(wc, slave);
        computer.setTemporarilyOffline(true, null);
        Assert.assertTrue(computer.isTemporarilyOffline());
        assertConnected(wc, slave);
        slave.toComputer().disconnect(null);
        HtmlPage page = wc.getPage(slave);
        assertLinkDoesNotExist(page, "Disconnect");
        assertLinkDoesNotExist(page, "Script Console");
        HtmlPage script = wc.getPage(slave, "script");
        Assert.assertThat(script.getByXPath("//form[@action='script']"), Matchers.empty());
        assertLinkDoesNotExist(page, "System Information");
        HtmlPage info = wc.getPage(slave, "systemInfo");
        Assert.assertThat(info.asText(), Matchers.not(Matchers.containsString("Environment Variables")));
    }
}

