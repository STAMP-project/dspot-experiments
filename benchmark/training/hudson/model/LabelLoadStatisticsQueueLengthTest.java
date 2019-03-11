package hudson.model;


import TimeScale.SEC10;
import hudson.model.labels.LabelAssignmentAction;
import hudson.model.queue.SubTask;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;


/**
 * Test that a {@link Label}'s {@link LoadStatistics#queueLength} correctly
 * reflects the queued builds.
 */
public class LabelLoadStatisticsQueueLengthTest {
    private static final String LABEL_STRING = LabelLoadStatisticsQueueLengthTest.class.getSimpleName();

    private static final String ALT_LABEL_STRING = (LabelLoadStatisticsQueueLengthTest.LABEL_STRING) + "alt";

    private static final String PROJECT_NAME = LabelLoadStatisticsQueueLengthTest.class.getSimpleName();

    private static final String PARAMETER_NAME = "parameter";

    private static final Cause CAUSE = new Cause() {
        @Override
        public String getShortDescription() {
            return "Build caused by test.";
        }
    };

    @Rule
    public JenkinsRule j = new JenkinsRule();

    /**
     * Verify that when a {@link Label} is assigned to a queued build using a
     * {@link LabelAssignmentAction}, that label's
     * {@link LoadStatistics#queueLength} reflects the number of items in the
     * queue, and continues to do so if the {@link Project}'s label is changed.
     */
    @Test
    public void queueLengthReflectsBuildableItemsAssignedLabel() throws Exception {
        final Label label = Label.get(LabelLoadStatisticsQueueLengthTest.LABEL_STRING);
        final Label altLabel = Label.get(LabelLoadStatisticsQueueLengthTest.ALT_LABEL_STRING);
        FreeStyleProject project = createTestProject();
        // Before queueing the builds the rolling queue length should be 0.
        Assert.assertTrue("Initially the rolling queue length for the label is 0.", ((label.loadStatistics.queueLength.getLatest(SEC10)) == 0.0F));
        // Add the job to the build queue several times with an assigned label.
        for (int i = 0; i < 3; i++) {
            project.scheduleBuild(0, LabelLoadStatisticsQueueLengthTest.CAUSE, new LabelLoadStatisticsQueueLengthTest.LabelAssignmentActionImpl(), new ParametersAction(new StringParameterValue(LabelLoadStatisticsQueueLengthTest.PARAMETER_NAME, String.valueOf(i))));
        }
        // Verify that the real queue length is 3.
        Assert.assertEquals("The job is queued as often as it was scheduled.", 3, j.getInstance().getQueue().getItems(project).size());
        maintainQueueAndForceRunOfLoadStatisticsUpdater(project);
        Assert.assertEquals("The job is still queued as often as it was scheduled.", 3, j.getInstance().getQueue().getItems(project).size());
        float labelQueueLength = label.loadStatistics.queueLength.getLatest(SEC10);
        Assert.assertThat("After LoadStatisticsUpdater runs, the queue length load statistic for the label is greater than 0.", labelQueueLength, Matchers.greaterThan(0.0F));
        // Assign an alternate label to the project and update the load stats.
        project.setAssignedLabel(altLabel);
        maintainQueueAndForceRunOfLoadStatisticsUpdater(project);
        // Verify that the queue length load stat continues to reflect the labels assigned to the items in the queue.
        float labelQueueLengthNew = label.loadStatistics.queueLength.getLatest(SEC10);
        Assert.assertThat(("After assigning an alternate label to the job, the queue length load statistic for the " + "queued builds should not decrease."), labelQueueLengthNew, Matchers.greaterThan(labelQueueLength));
    }

    /**
     * Verify that when a {@link Label} is assigned to a {@link Project}, that
     * label's {@link LoadStatistics#queueLength} reflects the number of items
     * in the queue scheduled for that project, and updates if the project's
     * label is changed.
     */
    @Test
    public void queueLengthReflectsJobsAssignedLabel() throws Exception {
        final Label label = Label.get(LabelLoadStatisticsQueueLengthTest.LABEL_STRING);
        final Label altLabel = Label.get(LabelLoadStatisticsQueueLengthTest.ALT_LABEL_STRING);
        FreeStyleProject project = createTestProject();
        // Assign a label to the job.
        project.setAssignedLabel(label);
        // Before queueing the builds the rolling queue lengths should be 0.
        Assert.assertTrue("Initially the rolling queue length for the label is 0.", ((label.loadStatistics.queueLength.getLatest(SEC10)) == 0.0F));
        Assert.assertTrue("Initially the rolling queue length for the alt label is 0.", ((altLabel.loadStatistics.queueLength.getLatest(SEC10)) == 0.0F));
        // Add the job to the build queue several times.
        for (int i = 0; i < 3; i++) {
            project.scheduleBuild(0, LabelLoadStatisticsQueueLengthTest.CAUSE, new ParametersAction(new StringParameterValue(LabelLoadStatisticsQueueLengthTest.PARAMETER_NAME, String.valueOf(i))));
        }
        // Verify that the real queue length is 3.
        Assert.assertEquals("The job is queued as often as it was scheduled.", 3, j.getInstance().getQueue().getItems(project).size());
        maintainQueueAndForceRunOfLoadStatisticsUpdater(project);
        float labelQueueLength = label.loadStatistics.queueLength.getLatest(SEC10);
        Assert.assertTrue("After LoadStatisticsUpdater runs, the queue length load statistic for the label is greater than 0.", (labelQueueLength > 0.0F));
        // Assign an alternate label to the job and update the load stats.
        project.setAssignedLabel(altLabel);
        maintainQueueAndForceRunOfLoadStatisticsUpdater(project);
        // Verify that the queue length load stats of the labels reflect the newly project's newly assigned label.
        float labelQueueLengthNew = label.loadStatistics.queueLength.getLatest(SEC10);
        Assert.assertTrue("After assigning an alternate label to the job, the queue length load statistic for the queued builds should decrease.", (labelQueueLengthNew < labelQueueLength));
        float altLabelQueueLength = altLabel.loadStatistics.queueLength.getLatest(SEC10);
        Assert.assertTrue("After assigning an alternate label to the job, the queue length load statistic for the alternate label should be greater than 0.", (altLabelQueueLength > 0.0F));
    }

    private static class LabelAssignmentActionImpl implements LabelAssignmentAction {
        @Override
        public String getIconFileName() {
            return null;
        }

        @Override
        public String getDisplayName() {
            return (LabelLoadStatisticsQueueLengthTest.LABEL_STRING) + " LabelAssignmentAction";
        }

        @Override
        public String getUrlName() {
            return null;
        }

        @Override
        public Label getAssignedLabel(SubTask p_task) {
            return Label.get(LabelLoadStatisticsQueueLengthTest.LABEL_STRING);
        }
    }
}

