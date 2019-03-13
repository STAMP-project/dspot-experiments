/**
 * The MIT License
 *
 * Copyright (c) 2018 CloudBees, Inc.
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


import hudson.PluginManager;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;

import static Matcher.failedWith;
import static Matcher.succeeded;


public class EnablePluginCommandTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    @Issue("JENKINS-52822")
    public void enableSinglePlugin() throws IOException {
        String name = "token-macro";
        PluginManager m = j.getPluginManager();
        Assert.assertThat(m.getPlugin(name), is(nullValue()));
        Assert.assertThat(installTestPlugin(name), succeeded());
        assertPluginEnabled(name);
        disablePlugin(name);
        assertPluginDisabled(name);
        Assert.assertThat(enablePlugins(name), succeeded());
        assertPluginEnabled(name);
        assertJenkinsNotInQuietMode();
    }

    @Test
    @Issue("JENKINS-52822")
    public void enableInvalidPluginFails() {
        Assert.assertThat(enablePlugins("foobar"), failedWith(3));
        assertJenkinsNotInQuietMode();
    }

    @Test
    @Issue("JENKINS-52822")
    public void enableDependerEnablesDependee() throws IOException {
        installTestPlugin("dependee");
        installTestPlugin("depender");
        disablePlugin("depender");
        disablePlugin("dependee");
        Assert.assertThat(enablePlugins("depender"), succeeded());
        assertPluginEnabled("depender");
        assertPluginEnabled("dependee");
        assertJenkinsNotInQuietMode();
    }

    @Test
    @Issue("JENKINS-52950")
    public void enableNoPluginsWithRestartIsNoOp() {
        assumeNotWindows();
        String name = "variant";
        Assert.assertThat(installTestPlugin(name), succeeded());
        Assert.assertThat(enablePlugins("-restart", name), succeeded());
        assertJenkinsNotInQuietMode();
    }
}

