/**
 * The MIT License
 *
 * Copyright (c) 2010, Kohsuke Kawaguchi
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
package hudson.util;


import com.google.common.base.Joiner;
import hudson.Functions;
import hudson.model.Slave;
import java.io.StringWriter;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Email;
import org.jvnet.hudson.test.JenkinsRule;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class ArgumentListBuilder2Test {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    /**
     * Makes sure {@link RemoteLauncher} properly masks arguments.
     */
    @Test
    @Email("http://n4.nabble.com/Password-masking-when-running-commands-on-a-slave-tp1753033p1753033.html")
    public void slaveMask() throws Exception {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("java");
        args.addMasked("-version");
        Slave s = j.createSlave();
        s.toComputer().connect(false).get();
        StringWriter out = new StringWriter();
        Assert.assertEquals(0, s.createLauncher(new StreamTaskListener(out)).launch().cmds(args).join());
        System.out.println(out);
        Assert.assertTrue(out.toString().contains("$ java ********"));
    }

    @Test
    public void ensureArgumentsArePassedViaCmdExeUnmodified() throws Exception {
        Assume.assumeTrue(Functions.isWindows());
        String[] specials = new String[]{ "~", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "+", "{", "}", "[", "]", ":", ";", "\"", "'", "\\", "|", "<", ">", ",", ".", "/", "?", " " };
        String out = echoArgs(specials);
        String expected = String.format("%n%s", Joiner.on(" ").join(specials));
        MatcherAssert.assertThat(out, Matchers.containsString(expected));
    }
}

