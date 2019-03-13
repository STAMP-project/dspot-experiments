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
package hudson.model.queue;


import hudson.model.Computer;
import hudson.model.Executor;
import hudson.model.Queue.JobOffer;
import hudson.model.Queue.Task;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;
import org.mockito.Mockito;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class LoadPredictorTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @TestExtension
    public static class LoadPredictorImpl extends LoadPredictor {
        @Override
        public Iterable<FutureLoad> predict(MappingWorksheet plan, Computer computer, long start, long end) {
            return Arrays.asList(new FutureLoad((start + 5000), (end - (start + 5000)), 1));
        }
    }

    /**
     * Makes sure that {@link LoadPredictor} is taken into account when building {@link MappingWorksheet}.
     * The scenario is:
     *
     * - a computer with 1 executor, idle.
     * - a future load of size 1 is predicted
     * - hence the consideration of the current task at hand shall fail, as it'll collide with the estimated future load.
     */
    @Test
    public void test1() throws Exception {
        Task t = Mockito.mock(Task.class);
        Mockito.when(t.getEstimatedDuration()).thenReturn(10000L);
        Mockito.when(t.getSubTasks()).thenReturn(((Collection) (Arrays.asList(t))));
        Computer c = createMockComputer(1);
        JobOffer o = createMockOffer(c.getExecutors().get(0));
        MappingWorksheet mw = new MappingWorksheet(wrap(t), Arrays.asList(o));
        // the test load predictor should have pushed down the executor count to 0
        Assert.assertTrue(mw.executors.isEmpty());
        Assert.assertEquals(1, mw.works.size());
    }

    /**
     * Test scenario is:
     *
     * - a computer with two executors, one is building something now
     * - a future load of size 1 is predicted but it'll start after the currently building something is completed.
     * - hence the currently available executor should be considered available (unlike in test1)
     */
    @Test
    public void test2() throws Exception {
        Task t = Mockito.mock(Task.class);
        Mockito.when(t.getEstimatedDuration()).thenReturn(10000L);
        Mockito.when(t.getSubTasks()).thenReturn(((Collection) (Arrays.asList(t))));
        Computer c = createMockComputer(2);
        Executor e = c.getExecutors().get(0);
        Mockito.when(e.isIdle()).thenReturn(false);
        Mockito.when(e.getEstimatedRemainingTimeMillis()).thenReturn(300L);
        JobOffer o = createMockOffer(c.getExecutors().get(1));
        MappingWorksheet mw = new MappingWorksheet(wrap(t), Arrays.asList(o));
        // since the currently busy executor will free up before a future predicted load starts,
        // we should have a valid executor remain in the queue
        Assert.assertEquals(1, mw.executors.size());
        Assert.assertEquals(1, mw.works.size());
    }
}

