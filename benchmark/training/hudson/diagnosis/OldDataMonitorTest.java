/**
 * The MIT License
 *
 * Copyright 2014 Jesse Glick.
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
package hudson.diagnosis;


import BuildReference.DefaultHolderFactory.MODE_PROPERTY;
import OldDataMonitor.changeListener;
import hudson.XmlFile;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.InvisibleAction;
import hudson.model.Saveable;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MemoryAssert;
import org.kohsuke.stapler.Stapler;


public class OldDataMonitorTest {
    static {
        // To make memory run faster:
        System.setProperty(MODE_PROPERTY, "weak");
    }

    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Issue("JENKINS-19544")
    @Test
    public void memory() throws Exception {
        FreeStyleProject p = r.createFreeStyleProject("p");
        FreeStyleBuild b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));
        b.addAction(new OldDataMonitorTest.BadAction2());
        b.save();
        r.jenkins.getQueue().clearLeftItems();
        p._getRuns().purgeCache();
        b = p.getBuildByNumber(1);
        Assert.assertEquals(Collections.singleton(b), OldDataMonitor.get(r.jenkins).getData().keySet());
        WeakReference<?> ref = new WeakReference<Object>(b);
        b = null;
        MemoryAssert.assertGC(ref);
    }

    /**
     * Note that this doesn't actually run slowly, it just ensures that
     * the {@link OldDataMonitor#changeListener's onChange()} can complete
     * while {@link OldDataMonitor#doDiscard(org.kohsuke.stapler.StaplerRequest, org.kohsuke.stapler.StaplerResponse)}
     * is still running.
     */
    // Test timeout indicates JENKINS-24763 exists
    @Issue("JENKINS-24763")
    @Test
    public void slowDiscard() throws IOException, InterruptedException, ExecutionException {
        final OldDataMonitor oldDataMonitor = OldDataMonitor.get(r.jenkins);
        final CountDownLatch ensureEntry = new CountDownLatch(1);
        final CountDownLatch preventExit = new CountDownLatch(1);
        Saveable slowSavable = new Saveable() {
            @Override
            public void save() throws IOException {
                try {
                    ensureEntry.countDown();
                    preventExit.await();
                } catch (InterruptedException e) {
                }
            }
        };
        OldDataMonitor.report(slowSavable, ((String) (null)));
        ExecutorService executors = Executors.newSingleThreadExecutor();
        Future<Void> discardFuture = executors.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                oldDataMonitor.doDiscard(Stapler.getCurrentRequest(), Stapler.getCurrentResponse());
                return null;
            }
        });
        ensureEntry.await();
        // test will hang here due to JENKINS-24763
        File xml = File.createTempFile("OldDataMonitorTest.slowDiscard", "xml");
        xml.deleteOnExit();
        changeListener.onChange(new Saveable() {
            public void save() throws IOException {
            }
        }, new XmlFile(xml));
        preventExit.countDown();
        discardFuture.get();
    }

    @Issue("JENKINS-26718")
    @Test
    public void unlocatableRun() throws Exception {
        OldDataMonitor odm = OldDataMonitor.get(r.jenkins);
        FreeStyleProject p = r.createFreeStyleProject();
        FreeStyleBuild build = r.buildAndAssertSuccess(p);
        p.delete();
        OldDataMonitor.report(build, ((String) (null)));
        Assert.assertEquals(Collections.singleton(build), odm.getData().keySet());
        odm.doDiscard(null, null);
        Assert.assertEquals(Collections.emptySet(), odm.getData().keySet());
    }

    public static final class BadAction extends InvisibleAction {
        private Object writeReplace() {
            throw new IllegalStateException("broken");
        }
    }

    public static final class BadAction2 extends InvisibleAction {
        private Object readResolve() {
            throw new IllegalStateException("broken");
        }
    }
}

