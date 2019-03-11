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
package jenkins.model;


import hudson.Util;
import hudson.model.AbstractItem;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Actionable;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.InvisibleAction;
import hudson.model.ProminentProjectAction;
import hudson.model.queue.FoldableAction;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockFolder;
import org.jvnet.hudson.test.TestExtension;


public class TransientActionFactoryTest {
    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Test
    public void addedToAbstractItem() throws Exception {
        Assert.assertNotNull(r.createFolder("d").getAction(TransientActionFactoryTest.MyAction.class));
        Assert.assertNotNull(r.createFreeStyleProject().getAction(TransientActionFactoryTest.MyAction.class));
    }

    @TestExtension("addedToAbstractItem")
    public static class TestItemFactory extends TransientActionFactory<AbstractItem> {
        @Override
        public Class<AbstractItem> type() {
            return AbstractItem.class;
        }

        @Override
        public Class<TransientActionFactoryTest.MyAction> actionType() {
            return TransientActionFactoryTest.MyAction.class;
        }

        @Override
        public Collection<? extends TransientActionFactoryTest.MyAction> createFor(AbstractItem i) {
            return Collections.singleton(new TransientActionFactoryTest.MyAction());
        }
    }

    private static class MyAction implements Action {
        @Override
        public String getIconFileName() {
            return null;
        }

        @Override
        public String getDisplayName() {
            return null;
        }

        @Override
        public String getUrlName() {
            return null;
        }
    }

    @Test
    public void laziness() throws Exception {
        FreeStyleProject p = r.createFreeStyleProject();
        // testing getAction(Class)
        Assert.assertNull(p.getAction(FoldableAction.class));
        Assert.assertEquals(0, TransientActionFactoryTest.LazyFactory.count);
        Assert.assertNotNull(p.getAction(ProminentProjectAction.class));
        Assert.assertEquals(1, TransientActionFactoryTest.LazyFactory.count);
        Assert.assertNotNull(p.getAction(TransientActionFactoryTest.MyProminentProjectAction.class));
        Assert.assertEquals(2, TransientActionFactoryTest.LazyFactory.count);
        TransientActionFactoryTest.LazyFactory.count = 0;
        // getAllActions
        List<? extends Action> allActions = p.getAllActions();
        Assert.assertEquals(1, TransientActionFactoryTest.LazyFactory.count);
        Assert.assertThat(Util.filter(allActions, FoldableAction.class), Matchers.<FoldableAction>iterableWithSize(0));
        Assert.assertThat(Util.filter(allActions, ProminentProjectAction.class), Matchers.<ProminentProjectAction>iterableWithSize(1));
        Assert.assertThat(Util.filter(allActions, TransientActionFactoryTest.MyProminentProjectAction.class), Matchers.<TransientActionFactoryTest.MyProminentProjectAction>iterableWithSize(1));
        TransientActionFactoryTest.LazyFactory.count = 0;
        // getActions(Class)
        Assert.assertThat(p.getActions(FoldableAction.class), Matchers.<FoldableAction>iterableWithSize(0));
        Assert.assertEquals(0, TransientActionFactoryTest.LazyFactory.count);
        Assert.assertThat(p.getActions(ProminentProjectAction.class), Matchers.<ProminentProjectAction>iterableWithSize(1));
        Assert.assertEquals(1, TransientActionFactoryTest.LazyFactory.count);
        Assert.assertThat(p.getActions(TransientActionFactoryTest.MyProminentProjectAction.class), Matchers.<TransientActionFactoryTest.MyProminentProjectAction>iterableWithSize(1));
        Assert.assertEquals(2, TransientActionFactoryTest.LazyFactory.count);
        TransientActionFactoryTest.LazyFactory.count = 0;
        // different context type
        MockFolder d = r.createFolder("d");
        Assert.assertNull(d.getAction(FoldableAction.class));
        Assert.assertNull(d.getAction(ProminentProjectAction.class));
        allActions = d.getAllActions();
        Assert.assertThat(Util.filter(allActions, FoldableAction.class), Matchers.<FoldableAction>iterableWithSize(0));
        Assert.assertThat(Util.filter(allActions, ProminentProjectAction.class), Matchers.<ProminentProjectAction>iterableWithSize(0));
        Assert.assertThat(d.getActions(FoldableAction.class), Matchers.<FoldableAction>iterableWithSize(0));
        Assert.assertThat(d.getActions(ProminentProjectAction.class), Matchers.<ProminentProjectAction>iterableWithSize(0));
        Assert.assertEquals(0, TransientActionFactoryTest.LazyFactory.count);
    }

    @SuppressWarnings("rawtypes")
    @TestExtension("laziness")
    public static class LazyFactory extends TransientActionFactory<AbstractProject> {
        static int count;

        @Override
        public Class<AbstractProject> type() {
            return AbstractProject.class;
        }

        @Override
        public Class<? extends Action> actionType() {
            return ProminentProjectAction.class;
        }

        @Override
        public Collection<? extends Action> createFor(AbstractProject p) {
            (TransientActionFactoryTest.LazyFactory.count)++;
            return Collections.singleton(new TransientActionFactoryTest.MyProminentProjectAction());
        }
    }

    @Test
    public void compatibility() throws Exception {
        FreeStyleProject p = r.createFreeStyleProject();
        // testing getAction(Class)
        Assert.assertNull(p.getAction(FoldableAction.class));
        Assert.assertEquals(1, TransientActionFactoryTest.OldFactory.count);
        Assert.assertNotNull(p.getAction(ProminentProjectAction.class));
        Assert.assertEquals(2, TransientActionFactoryTest.OldFactory.count);
        TransientActionFactoryTest.OldFactory.count = 0;
        // getAllActions
        List<? extends Action> allActions = p.getAllActions();
        Assert.assertEquals(1, TransientActionFactoryTest.OldFactory.count);
        Assert.assertThat(Util.filter(allActions, FoldableAction.class), Matchers.<FoldableAction>iterableWithSize(0));
        Assert.assertThat(Util.filter(allActions, ProminentProjectAction.class), Matchers.<ProminentProjectAction>iterableWithSize(1));
        TransientActionFactoryTest.OldFactory.count = 0;
        // getActions(Class)
        Assert.assertThat(p.getActions(FoldableAction.class), Matchers.<FoldableAction>iterableWithSize(0));
        Assert.assertEquals(1, TransientActionFactoryTest.OldFactory.count);
        Assert.assertThat(p.getActions(ProminentProjectAction.class), Matchers.<ProminentProjectAction>iterableWithSize(1));
        Assert.assertEquals(2, TransientActionFactoryTest.OldFactory.count);
    }

    @TestExtension("compatibility")
    public static class OldFactory extends TransientActionFactory<FreeStyleProject> {
        static int count;

        @Override
        public Class<FreeStyleProject> type() {
            return FreeStyleProject.class;
        }

        @Override
        public Collection<? extends Action> createFor(FreeStyleProject p) {
            (TransientActionFactoryTest.OldFactory.count)++;
            return Collections.singleton(new TransientActionFactoryTest.MyProminentProjectAction());
        }
    }

    @Issue("JENKINS-51584")
    @Test
    public void transientActionsAreNotPersistedOnQueueItems() throws Exception {
        FreeStyleProject p = r.createFreeStyleProject();
        FreeStyleBuild build = r.buildAndAssertSuccess(p);
        // MyProminentProjectAction is only added via the TransientActionFactory and should never be persisted.
        Assert.assertThat(Util.filter(build.getActions(), TransientActionFactoryTest.MyProminentProjectAction.class), Matchers.is(Matchers.empty()));
        Assert.assertThat(Util.filter(build.getAllActions(), TransientActionFactoryTest.MyProminentProjectAction.class), Matchers.hasSize(1));
    }

    @TestExtension("transientActionsAreNotPersistedOnQueueItems")
    public static class AllFactory extends TransientActionFactory<Actionable> {
        @Override
        public Class<Actionable> type() {
            return Actionable.class;
        }

        @Nonnull
        @Override
        public Collection<? extends Action> createFor(@Nonnull
        Actionable target) {
            return Collections.singleton(new TransientActionFactoryTest.MyProminentProjectAction());
        }
    }

    private static class MyProminentProjectAction extends InvisibleAction implements ProminentProjectAction {
        private String allocation;

        public MyProminentProjectAction() {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            new Exception("MyProminentProjectAction allocated at: ").printStackTrace(pw);
            allocation = sw.toString();
        }

        public String toString() {
            return allocation;
        }
    }
}

