/**
 * The MIT License
 *
 * Copyright 2013 Jesse Glick.
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


import CLICommandInvoker.Result;
import Jenkins.READ;
import Jenkins.RUN_SCRIPTS;
import org.apache.tools.ant.filters.StringInputStream;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;


public class GroovyshCommandTest {
    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Issue("JENKINS-17929")
    @Test
    public void authentication() throws Exception {
        CLICommandInvoker.Result result = new CLICommandInvoker(r, new GroovyshCommand()).authorizedTo(READ, RUN_SCRIPTS).withStdin(new StringInputStream("println(jenkins.model.Jenkins.instance.getClass().name)\n:quit\n")).invoke();
        Assert.assertThat(result, succeeded());
        Assert.assertThat(result, hasNoErrorOutput());
        Assert.assertThat(result.stdout(), CoreMatchers.containsString("hudson.model.Hudson"));
    }
}

