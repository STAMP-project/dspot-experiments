/**
 * The MIT License
 *
 * Copyright (c) Red Hat, Inc.
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


import Queue.Saver;
import hudson.ExtensionList;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.model.Statement;
import org.jvnet.hudson.test.RestartableJenkinsRule;


public class QueueRestartTest {
    @Rule
    public RestartableJenkinsRule j = new RestartableJenkinsRule();

    @Test
    public void persistQueueOnRestart() {
        j.addStep(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Saver.DELAY_SECONDS = (24 * 60) * 60;// Avoid period save as we are after the explicit one

                scheduleSomeBuild();
                assertBuildIsScheduled();
            }
        });
        j.addStep(new Statement() {
            @Override
            public void evaluate() {
                assertBuildIsScheduled();
            }
        });
    }

    @Test
    public void persistQueueOnCrash() {
        j.addStepWithDirtyShutdown(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Saver.DELAY_SECONDS = 0;// Call Queue#save() on every queue modification simulating time has passed before crash

                scheduleSomeBuild();
                assertBuildIsScheduled();
                // Save have no delay though is not synchronous
                ExtensionList.lookup(Saver.class).get(0).getNextSave().get(3, TimeUnit.SECONDS);
                Assert.assertTrue("queue.xml does not exist", j.j.jenkins.getQueue().getXMLQueueFile().exists());
            }
        });
        j.addStep(new Statement() {
            @Override
            public void evaluate() {
                assertBuildIsScheduled();
            }
        });
    }
}

