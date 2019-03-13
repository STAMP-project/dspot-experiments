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
package hudson.logging;


import LogRecorder.Target;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;


public class LogRecorderTest {
    @Issue("JENKINS-17983")
    @Test
    public void targetIncludes() {
        Assert.assertTrue(LogRecorderTest.includes("hudson", "hudson"));
        Assert.assertFalse(LogRecorderTest.includes("hudson", "hudsone"));
        Assert.assertFalse(LogRecorderTest.includes("hudson", "hudso"));
        Assert.assertTrue(LogRecorderTest.includes("hudson", "hudson.model.Hudson"));
        Assert.assertFalse(LogRecorderTest.includes("hudson", "jenkins.model.Jenkins"));
        Assert.assertTrue(LogRecorderTest.includes("", "hudson.model.Hudson"));
    }

    @Test
    public void targetMatches() {
        Assert.assertTrue(LogRecorderTest.matches("hudson", "hudson"));
        Assert.assertFalse(LogRecorderTest.matches("hudson", "hudson", Level.FINE));
        Assert.assertNull(LogRecorderTest.matches("hudson", "hudsone"));
        Assert.assertNull(LogRecorderTest.matches("hudson", "hudso"));
        Assert.assertTrue(LogRecorderTest.matches("hudson", "hudson.model.Hudson"));
        Assert.assertFalse(LogRecorderTest.matches("hudson", "hudson.model.Hudson", Level.FINE));
        Assert.assertNull(LogRecorderTest.matches("hudson", "jenkins.model.Jenkins"));
        Assert.assertTrue(LogRecorderTest.matches("", "hudson.model.Hudson"));
        Assert.assertFalse(LogRecorderTest.matches("", "hudson.model.Hudson", Level.FINE));
    }

    @Test
    public void testClearing() throws IOException {
        LogRecorder lr = new LogRecorder("foo");
        LogRecorder.Target t = new LogRecorder.Target("", Level.FINE);
        lr.targets.add(t);
        LogRecord record = LogRecorderTest.createLogRecord("jenkins", Level.INFO, "message");
        lr.handler.publish(record);
        Assert.assertEquals(lr.handler.getView().get(0), record);
        Assert.assertEquals(1, lr.handler.getView().size());
        lr.doClear();
        Assert.assertEquals(0, lr.handler.getView().size());
    }

    @Test
    public void testSpecificExclusion() {
        LogRecorder lr = new LogRecorder("foo");
        LogRecorder.Target targetLevel0 = new LogRecorder.Target("", Level.FINE);
        LogRecorder.Target targetLevel1 = new LogRecorder.Target("foo", Level.INFO);
        LogRecorder.Target targetLevel2 = new LogRecorder.Target("foo.bar", Level.SEVERE);
        lr.targets.add(targetLevel1);
        lr.targets.add(targetLevel2);
        lr.targets.add(targetLevel0);
        Assert.assertEquals(lr.orderedTargets()[0], targetLevel2);
        Assert.assertEquals(lr.orderedTargets()[1], targetLevel1);
        Assert.assertEquals(lr.orderedTargets()[2], targetLevel0);
        LogRecord r1 = LogRecorderTest.createLogRecord("baz", Level.INFO, "visible");
        LogRecord r2 = LogRecorderTest.createLogRecord("foo", Level.FINE, "hidden");
        LogRecord r3 = LogRecorderTest.createLogRecord("foo.bar", Level.INFO, "hidden");
        LogRecord r4 = LogRecorderTest.createLogRecord("foo.bar.baz", Level.INFO, "hidden");
        LogRecord r5 = LogRecorderTest.createLogRecord("foo.bar.baz", Level.SEVERE, "visible");
        LogRecord r6 = LogRecorderTest.createLogRecord("foo", Level.INFO, "visible");
        lr.handler.publish(r1);
        lr.handler.publish(r2);
        lr.handler.publish(r3);
        lr.handler.publish(r4);
        lr.handler.publish(r5);
        lr.handler.publish(r6);
        Assert.assertTrue(lr.handler.getView().contains(r1));
        Assert.assertFalse(lr.handler.getView().contains(r2));
        Assert.assertFalse(lr.handler.getView().contains(r3));
        Assert.assertFalse(lr.handler.getView().contains(r4));
        Assert.assertTrue(lr.handler.getView().contains(r5));
        Assert.assertTrue(lr.handler.getView().contains(r6));
    }

    @Test
    public void autocompletionTest() throws Exception {
        List<String> loggers = Arrays.asList("com.company.whatever.Foo", "com.foo.Bar", "com.foo.Baz", "org.example.app.Main", "org.example.app.impl.xml.Parser", "org.example.app.impl.xml.Validator");
        Set<String> candidates = LogRecorder.getAutoCompletionCandidates(loggers);
        LogRecorderTest.isCandidate(candidates, "com");
        LogRecorderTest.isCandidate(candidates, "com.company.whatever.Foo");
        LogRecorderTest.isCandidate(candidates, "com.foo");
        LogRecorderTest.isCandidate(candidates, "com.foo.Bar");
        LogRecorderTest.isCandidate(candidates, "com.foo.Baz");
        LogRecorderTest.isCandidate(candidates, "org.example.app");
        LogRecorderTest.isCandidate(candidates, "org.example.app.Main");
        LogRecorderTest.isCandidate(candidates, "org.example.app.impl.xml");
        LogRecorderTest.isCandidate(candidates, "org.example.app.impl.xml.Parser");
        LogRecorderTest.isCandidate(candidates, "org.example.app.impl.xml.Validator");
        LogRecorderTest.isNotCandidate(candidates, "org");
        LogRecorderTest.isNotCandidate(candidates, "org.example");
        Assert.assertEquals("expected number of items", 10, candidates.size());
    }
}

