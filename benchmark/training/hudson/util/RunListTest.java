/**
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi
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


import hudson.model.Run;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.Issue;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Ignacio Albors
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class RunListTest {
    // RunList for byTimestamp tests
    private RunList rlist;

    @PrepareForTest({ Run.class })
    @Test
    public void byTimestampAllRuns() {
        setUpByTimestampRuns();
        RunList<Run> tested = rlist.byTimestamp(0, 400);
        Assert.assertEquals(2, tested.toArray().length);
    }

    @Issue("JENKINS-21159")
    @PrepareForTest({ Run.class })
    @Test
    public void byTimestampFirstRun() {
        setUpByTimestampRuns();
        // Only r1
        RunList<Run> tested = rlist.byTimestamp(150, 250);
        Assert.assertEquals(1, tested.toArray().length);
        Assert.assertEquals(1, tested.getFirstBuild().getNumber());
    }

    @PrepareForTest({ Run.class })
    @Test
    public void byTimestampLastRun() {
        setUpByTimestampRuns();
        // Only r2
        RunList<Run> tested = rlist.byTimestamp(250, 350);
        Assert.assertEquals(1, tested.toArray().length);
        Assert.assertEquals(2, tested.getFirstBuild().getNumber());
    }
}

