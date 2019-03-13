/**
 * The MIT License
 *
 * Copyright (c) 2010, InfraDNA, Inc.
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
package hudson.model.labels;


import hudson.Functions;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.FreeStyleProject.DescriptorImpl;
import hudson.model.Label;
import hudson.model.Node.Mode;
import hudson.slaves.DumbSlave;
import hudson.slaves.RetentionStrategy;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.SequenceLock;
import org.jvnet.hudson.test.TestBuilder;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class LabelExpressionTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    /**
     * Verifies the queueing behavior in the presence of the expression.
     */
    @Test
    public void queueBehavior1() throws Exception {
        DumbSlave w32 = j.createSlave("win 32bit", null);
        DumbSlave w64 = j.createSlave("win 64bit", null);
        j.createSlave("linux 32bit", null);
        final SequenceLock seq = new SequenceLock();
        FreeStyleProject p1 = j.createFreeStyleProject();
        p1.getBuildersList().add(new TestBuilder() {
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
                seq.phase(0);// first, make sure the w32 agent is occupied

                seq.phase(2);
                seq.done();
                return true;
            }
        });
        p1.setAssignedLabel(j.jenkins.getLabel("win && 32bit"));
        FreeStyleProject p2 = j.createFreeStyleProject();
        p2.setAssignedLabel(j.jenkins.getLabel("win && 32bit"));
        FreeStyleProject p3 = j.createFreeStyleProject();
        p3.setAssignedLabel(j.jenkins.getLabel("win"));
        Future<FreeStyleBuild> f1 = p1.scheduleBuild2(0);
        seq.phase(1);// we schedule p2 build after w32 agent is occupied

        Future<FreeStyleBuild> f2 = p2.scheduleBuild2(0);
        Thread.sleep(1000);// time window to ensure queue has tried to assign f2 build

        // p3 is tied to 'win', so even though p1 is busy, this should still go ahead and complete
        FreeStyleBuild b3 = j.assertBuildStatusSuccess(p3.scheduleBuild2(0));
        Assert.assertSame(w64, b3.getBuiltOn());
        seq.phase(3);// once we confirm that p3 build is over, we let p1 proceed

        // p1 should have been built on w32
        FreeStyleBuild b1 = j.assertBuildStatusSuccess(f1);
        Assert.assertSame(w32, b1.getBuiltOn());
        // and so is p2
        FreeStyleBuild b2 = j.assertBuildStatusSuccess(f2);
        Assert.assertSame(w32, b2.getBuiltOn());
    }

    /**
     * Push the build around to different nodes via the assignment
     * to make sure it gets where we need it to.
     */
    @Test
    public void queueBehavior2() throws Exception {
        DumbSlave s = j.createSlave("win", null);
        FreeStyleProject p = j.createFreeStyleProject();
        p.setAssignedLabel(j.jenkins.getLabel("!win"));
        FreeStyleBuild b = j.assertBuildStatusSuccess(p.scheduleBuild2(0));
        Assert.assertSame(j.jenkins, b.getBuiltOn());
        p.setAssignedLabel(j.jenkins.getLabel("win"));
        b = j.assertBuildStatusSuccess(p.scheduleBuild2(0));
        Assert.assertSame(s, b.getBuiltOn());
        p.setAssignedLabel(j.jenkins.getLabel("!win"));
        b = j.assertBuildStatusSuccess(p.scheduleBuild2(0));
        Assert.assertSame(j.jenkins, b.getBuiltOn());
    }

    /**
     * Make sure we can reset the label of an existing slave.
     */
    @Test
    public void setLabelString() throws Exception {
        DumbSlave s = j.createSlave("foo", "", null);
        Assert.assertSame(s.getLabelString(), "");
        s.setLabelString("bar");
        Assert.assertSame(s.getLabelString(), "bar");
    }

    /**
     * Tests the expression parser.
     */
    @Test
    public void parser1() throws Exception {
        parseAndVerify("foo", "foo");
        parseAndVerify("32bit.dot", "32bit.dot");
        parseAndVerify("foo||bar", "foo || bar");
        // user-given parenthesis is preserved
        parseAndVerify("foo||bar&&zot", "foo||bar&&zot");
        parseAndVerify("foo||(bar&&zot)", "foo||(bar&&zot)");
        parseAndVerify("(foo||bar)&&zot", "(foo||bar)&&zot");
        parseAndVerify("foo->bar", "foo ->\tbar");
        parseAndVerify("!foo<->bar", "!foo <-> bar");
    }

    @Issue("JENKINS-8537")
    @Test
    public void parser2() throws Exception {
        parseAndVerify("aaa&&bbb&&ccc", "aaa&&bbb&&ccc");
    }

    @Test
    public void parserError() throws Exception {
        parseShouldFail("foo bar");
        parseShouldFail("foo (bar)");
    }

    @Test
    public void laxParsing() {
        // this should parse as an atom
        LabelAtom l = ((LabelAtom) (j.jenkins.getLabel("lucene.zones.apache.org (Solaris 10)")));
        Assert.assertEquals(l.getName(), "lucene.zones.apache.org (Solaris 10)");
        Assert.assertEquals(l.getExpression(), "\"lucene.zones.apache.org (Solaris 10)\"");
    }

    @Test
    public void dataCompatibilityWithHostNameWithWhitespace() throws Exception {
        Assume.assumeFalse("Windows can't have paths with colons, skipping", Functions.isWindows());
        DumbSlave slave = new DumbSlave("abc def (xyz) : test", "dummy", j.createTmpDir().getPath(), "1", Mode.NORMAL, "", j.createComputerLauncher(null), RetentionStrategy.NOOP, Collections.EMPTY_LIST);
        j.jenkins.addNode(slave);
        FreeStyleProject p = j.createFreeStyleProject();
        p.setAssignedLabel(j.jenkins.getLabel("abc def"));
        Assert.assertEquals("abc def", p.getAssignedLabel().getName());
        Assert.assertEquals("\"abc def\"", p.getAssignedLabel().getExpression());
        // expression should be persisted, not the name
        Field f = AbstractProject.class.getDeclaredField("assignedNode");
        f.setAccessible(true);
        Assert.assertEquals("\"abc def\"", f.get(p));
        // but if the name is set, we'd still like to parse it
        f.set(p, "a:b c");
        Assert.assertEquals("a:b c", p.getAssignedLabel().getName());
    }

    @Test
    public void quote() {
        Label l = j.jenkins.getLabel("\"abc\\\\\\\"def\"");
        Assert.assertEquals("abc\\\"def", l.getName());
        l = j.jenkins.getLabel("label1||label2");// create label expression

        l = j.jenkins.getLabel("\"label1||label2\"");
        Assert.assertEquals("label1||label2", l.getName());
    }

    /**
     * The name should have parenthesis at the right place to preserve the tree structure.
     */
    @Test
    public void composite() {
        LabelAtom x = j.jenkins.getLabelAtom("x");
        Assert.assertEquals("!!x", x.not().not().getName());
        Assert.assertEquals("(x||x)&&x", x.or(x).and(x).getName());
        Assert.assertEquals("x&&x||x", x.and(x).or(x).getName());
    }

    @Test
    public void dash() {
        j.jenkins.getLabelAtom("solaris-x86");
    }

    @Test
    public void formValidation() throws Exception {
        j.executeOnServer(new Callable<Object>() {
            public Object call() throws Exception {
                DescriptorImpl d = j.jenkins.getDescriptorByType(DescriptorImpl.class);
                Label l = j.jenkins.getLabel("foo");
                DumbSlave s = j.createSlave(l);
                String msg = d.doCheckLabel(null, "goo").renderHtml();
                Assert.assertTrue(msg.contains("foo"));
                Assert.assertTrue(msg.contains("goo"));
                msg = d.doCheckLabel(null, "master && goo").renderHtml();
                Assert.assertTrue(msg.contains("foo"));
                Assert.assertTrue(msg.contains("goo"));
                return null;
            }
        });
    }

    @Test
    public void parseLabel() throws Exception {
        Set<LabelAtom> result = Label.parse("one two three");
        String[] expected = new String[]{ "one", "two", "three" };
        for (String e : expected) {
            Assert.assertTrue(result.contains(new LabelAtom(e)));
        }
        Assert.assertEquals(result.size(), expected.length);
    }
}

