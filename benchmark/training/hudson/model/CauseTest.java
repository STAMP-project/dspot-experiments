/**
 * The MIT License
 *
 * Copyright 2012 Jesse Glick.
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


import hudson.util.StreamTaskListener;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;


public class CauseTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Issue("JENKINS-14814")
    @Test
    public void deeplyNestedCauses() throws Exception {
        FreeStyleProject a = j.createFreeStyleProject("a");
        FreeStyleProject b = j.createFreeStyleProject("b");
        Run<?, ?> early = null;
        Run<?, ?> last = null;
        for (int i = 1; i <= 15; i++) {
            last = b.scheduleBuild2(0, new Cause.UpstreamCause(((Run<?, ?>) (a.scheduleBuild2(0, (last == null ? null : new Cause.UpstreamCause(last))).get())))).get();
            if (i == 5) {
                early = last;
            }
        }
        String buildXml = asString();
        Assert.assertTrue(("keeps full history:\n" + buildXml), buildXml.contains("<upstreamBuild>1</upstreamBuild>"));
        buildXml = asString();
        Assert.assertFalse(("too big:\n" + buildXml), buildXml.contains("<upstreamBuild>1</upstreamBuild>"));
    }

    @Issue("JENKINS-15747")
    @Test
    public void broadlyNestedCauses() throws Exception {
        FreeStyleProject a = j.createFreeStyleProject("a");
        FreeStyleProject b = j.createFreeStyleProject("b");
        FreeStyleProject c = j.createFreeStyleProject("c");
        Run<?, ?> last = null;
        for (int i = 1; i <= 10; i++) {
            Cause cause = (last == null) ? null : new Cause.UpstreamCause(last);
            Future<? extends Run<?, ?>> next1 = a.scheduleBuild2(0, cause);
            a.scheduleBuild2(0, cause);
            cause = new Cause.UpstreamCause(next1.get());
            Future<? extends Run<?, ?>> next2 = b.scheduleBuild2(0, cause);
            b.scheduleBuild2(0, cause);
            cause = new Cause.UpstreamCause(next2.get());
            Future<? extends Run<?, ?>> next3 = c.scheduleBuild2(0, cause);
            c.scheduleBuild2(0, cause);
            last = next3.get();
        }
        int count = new hudson.XmlFile(Run.XSTREAM, new java.io.File(last.getRootDir(), "build.xml")).asString().split(Pattern.quote("<hudson.model.Cause_-UpstreamCause")).length;
        Assert.assertFalse(("too big at " + count), (count > 100));
        // j.interactiveBreak();
    }

    @Issue("JENKINS-48467")
    @Test
    public void userIdCausePrintTest() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TaskListener listener = new StreamTaskListener(baos);
        // null userId - print unknown or anonymous
        Cause causeA = new Cause.UserIdCause(null);
        causeA.print(listener);
        Assert.assertEquals(baos.toString().trim(), "Started by user unknown or anonymous");
        baos.reset();
        // SYSTEM userid  - getDisplayName() should be SYSTEM
        Cause causeB = new Cause.UserIdCause();
        causeB.print(listener);
        Assert.assertThat(baos.toString(), CoreMatchers.containsString("SYSTEM"));
        baos.reset();
        // unknown userid - print unknown or anonymous
        Cause causeC = new Cause.UserIdCause("abc123");
        causeC.print(listener);
        Assert.assertEquals(baos.toString().trim(), "Started by user unknown or anonymous");
        baos.reset();
        // More or less standard operation
        // user userid  - getDisplayName() should be foo
        User user = User.getById("foo", true);
        Cause causeD = new Cause.UserIdCause(user.getId());
        causeD.print(listener);
        Assert.assertThat(baos.toString(), CoreMatchers.containsString(user.getDisplayName()));
        baos.reset();
    }
}

