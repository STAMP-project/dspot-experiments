package hudson.model;


import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static Result.FAILURE;
import static Result.SUCCESS;
import static Result.UNSTABLE;


/**
 * Unit test for {@link Job}.
 */
@SuppressWarnings("rawtypes")
public class SimpleJobTest {
    @Rule
    public JenkinsRule rule = new JenkinsRule();

    @Test
    public void testGetEstimatedDuration() throws IOException {
        final SortedMap<Integer, SimpleJobTest.TestBuild> runs = new TreeMap<Integer, SimpleJobTest.TestBuild>();
        Job project = createMockProject(runs);
        SimpleJobTest.TestBuild previousPreviousBuild = new SimpleJobTest.TestBuild(project, SUCCESS, 20, null);
        runs.put(3, previousPreviousBuild);
        SimpleJobTest.TestBuild previousBuild = new SimpleJobTest.TestBuild(project, SUCCESS, 15, previousPreviousBuild);
        runs.put(2, previousBuild);
        SimpleJobTest.TestBuild lastBuild = new SimpleJobTest.TestBuild(project, SUCCESS, 42, previousBuild);
        runs.put(1, lastBuild);
        // without assuming to know too much about the internal calculation
        // we can only assume that the result is between the maximum and the minimum
        Assert.assertTrue(("Expected < 42, but was " + (project.getEstimatedDuration())), ((project.getEstimatedDuration()) < 42));
        Assert.assertTrue(("Expected > 15, but was " + (project.getEstimatedDuration())), ((project.getEstimatedDuration()) > 15));
    }

    @Test
    public void testGetEstimatedDurationWithOneRun() throws IOException {
        final SortedMap<Integer, SimpleJobTest.TestBuild> runs = new TreeMap<Integer, SimpleJobTest.TestBuild>();
        Job project = createMockProject(runs);
        SimpleJobTest.TestBuild lastBuild = new SimpleJobTest.TestBuild(project, SUCCESS, 42, null);
        runs.put(1, lastBuild);
        Assert.assertEquals(42, project.getEstimatedDuration());
    }

    @Test
    public void testGetEstimatedDurationWithNoRuns() throws IOException {
        final SortedMap<Integer, SimpleJobTest.TestBuild> runs = new TreeMap<Integer, SimpleJobTest.TestBuild>();
        Job project = createMockProject(runs);
        Assert.assertEquals((-1), project.getEstimatedDuration());
    }

    @Test
    public void testGetEstimatedDurationIfPrevious3BuildsFailed() throws IOException {
        final SortedMap<Integer, SimpleJobTest.TestBuild> runs = new TreeMap<Integer, SimpleJobTest.TestBuild>();
        Job project = createMockProject(runs);
        SimpleJobTest.TestBuild prev5Build = new SimpleJobTest.TestBuild(project, UNSTABLE, 1, null);
        runs.put(6, prev5Build);
        SimpleJobTest.TestBuild prev4Build = new SimpleJobTest.TestBuild(project, SUCCESS, 1, prev5Build);
        runs.put(5, prev4Build);
        SimpleJobTest.TestBuild prev3Build = new SimpleJobTest.TestBuild(project, SUCCESS, 1, prev4Build);
        runs.put(4, prev3Build);
        SimpleJobTest.TestBuild previous2Build = new SimpleJobTest.TestBuild(project, FAILURE, 50, prev3Build);
        runs.put(3, previous2Build);
        SimpleJobTest.TestBuild previousBuild = new SimpleJobTest.TestBuild(project, FAILURE, 50, previous2Build);
        runs.put(2, previousBuild);
        SimpleJobTest.TestBuild lastBuild = new SimpleJobTest.TestBuild(project, FAILURE, 50, previousBuild);
        runs.put(1, lastBuild);
        // failed builds must not be used, if there are successfulBuilds available.
        Assert.assertEquals(1, project.getEstimatedDuration());
    }

    @Test
    public void testGetEstimatedDurationIfNoSuccessfulBuildTakeDurationOfFailedBuild() throws IOException {
        final SortedMap<Integer, SimpleJobTest.TestBuild> runs = new TreeMap<Integer, SimpleJobTest.TestBuild>();
        Job project = createMockProject(runs);
        SimpleJobTest.TestBuild lastBuild = new SimpleJobTest.TestBuild(project, FAILURE, 50, null);
        runs.put(1, lastBuild);
        Assert.assertEquals(50, project.getEstimatedDuration());
    }

    @SuppressWarnings("unchecked")
    private static class TestBuild extends Run {
        public TestBuild(Job project, Result result, long duration, SimpleJobTest.TestBuild previousBuild) throws IOException {
            super(project);
            this.result = result;
            this.duration = duration;
            this.previousBuild = previousBuild;
        }

        @Override
        public int compareTo(Run o) {
            return 0;
        }

        @Override
        public Result getResult() {
            return result;
        }

        @Override
        public boolean isBuilding() {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private class TestJob extends Job implements TopLevelItem {
        int i;

        private final SortedMap<Integer, SimpleJobTest.TestBuild> runs;

        public TestJob(SortedMap<Integer, SimpleJobTest.TestBuild> runs) {
            super(rule.jenkins, "name");
            this.runs = runs;
            i = 1;
        }

        @Override
        public int assignBuildNumber() throws IOException {
            return (i)++;
        }

        @Override
        public SortedMap<Integer, ? extends Run> _getRuns() {
            return runs;
        }

        @Override
        public boolean isBuildable() {
            return true;
        }

        @Override
        protected void removeRun(Run run) {
        }

        public TopLevelItemDescriptor getDescriptor() {
            throw new AssertionError();
        }
    }
}

