/**
 * The MIT License
 *
 * Copyright 2017 CloudBees, Inc.
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


import hudson.XmlFile;
import java.io.File;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.model.Statement;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.RestartableJenkinsRule;


public class RunActionTest {
    @Rule
    public RestartableJenkinsRule rr = new RestartableJenkinsRule();

    @Issue("JENKINS-45892")
    @Test
    public void badSerialization() {
        rr.addStep(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                FreeStyleProject p = rr.j.createFreeStyleProject("p");
                FreeStyleBuild b1 = rr.j.buildAndAssertSuccess(p);
                FreeStyleBuild b2 = rr.j.buildAndAssertSuccess(p);
                b2.addAction(new RunActionTest.BadAction(b1));
                b2.save();
                String text = new XmlFile(new File(b2.getRootDir(), "build.xml")).asString();
                Assert.assertThat(text, not(containsString("<owner class=\"build\">")));
                Assert.assertThat(text, containsString("<id>p#1</id>"));
            }
        });
        rr.addStep(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                FreeStyleProject p = rr.j.jenkins.getItemByFullName("p", FreeStyleProject.class);
                Assert.assertEquals(p.getBuildByNumber(1), p.getBuildByNumber(2).getAction(RunActionTest.BadAction.class).owner);
            }
        });
    }

    static class BadAction extends InvisibleAction {
        final Run<?, ?> owner;// oops, should have been transient and used RunAction2


        BadAction(Run<?, ?> owner) {
            this.owner = owner;
        }
    }
}

