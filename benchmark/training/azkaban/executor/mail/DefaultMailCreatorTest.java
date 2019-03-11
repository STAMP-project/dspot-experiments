package azkaban.executor.mail;


import Status.FAILED;
import Status.FAILED_FINISHING;
import Status.SUCCEEDED;
import azkaban.executor.ExecutableFlow;
import azkaban.executor.ExecutionOptions;
import azkaban.executor.Executor;
import azkaban.executor.ExecutorManagerException;
import azkaban.flow.Flow;
import azkaban.project.Project;
import azkaban.utils.EmailMessage;
import azkaban.utils.TestUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;


public class DefaultMailCreatorTest {
    // 2016/07/17 11:54:11 EEST
    public static final long START_TIME_MILLIS = 1468745651608L;

    // 2016/07/17 11:54:16 EEST (START_TIME_MILLIS + 5 seconds)
    public static final long END_TIME_MILLIS = (DefaultMailCreatorTest.START_TIME_MILLIS) + 5000L;

    // 2016/07/17 11:54:21 EEST (START_TIME_MILLIS + 10 seconds)
    public static final long FIXED_CURRENT_TIME_MILLIS = (DefaultMailCreatorTest.START_TIME_MILLIS) + 10000L;

    private DefaultMailCreator mailCreator;

    private Executor executor;

    private ExecutableFlow executableFlow;

    private Flow flow;

    private Project project;

    private ExecutionOptions options;

    private EmailMessage message;

    private String azkabanName;

    private String scheme;

    private String clientHostname;

    private String clientPortNumber;

    private TimeZone defaultTz;

    @Test
    public void createErrorEmail() throws Exception {
        setJobStatus(FAILED);
        this.executableFlow.setEndTime(DefaultMailCreatorTest.END_TIME_MILLIS);
        this.executableFlow.setStatus(FAILED);
        final List<ExecutableFlow> executableFlows = new ArrayList<>();
        final ExecutableFlow executableFlow1 = new ExecutableFlow(this.project, this.flow);
        executableFlow1.setExecutionId(1);
        executableFlow1.setStartTime(DefaultMailCreatorTest.START_TIME_MILLIS);
        executableFlow1.setEndTime(DefaultMailCreatorTest.END_TIME_MILLIS);
        executableFlow1.setStatus(FAILED);
        executableFlows.add(executableFlow1);
        final ExecutableFlow executableFlow2 = new ExecutableFlow(this.project, this.flow);
        executableFlow2.setExecutionId(2);
        executableFlow2.setStartTime(DefaultMailCreatorTest.START_TIME_MILLIS);
        executableFlow2.setEndTime(DefaultMailCreatorTest.END_TIME_MILLIS);
        executableFlow2.setStatus(SUCCEEDED);
        executableFlows.add(executableFlow2);
        Assert.assertTrue(this.mailCreator.createErrorEmail(this.executableFlow, executableFlows, this.message, this.azkabanName, this.scheme, this.clientHostname, this.clientPortNumber));
        Assert.assertEquals("Flow 'mail-creator-test' has failed on unit-tests", this.message.getSubject());
        assertThat(TestUtils.readResource("errorEmail.html", this)).isEqualToIgnoringWhitespace(this.message.getBody());
    }

    @Test
    public void createFirstErrorMessage() throws Exception {
        setJobStatus(FAILED);
        this.executableFlow.setStatus(FAILED_FINISHING);
        Assert.assertTrue(this.mailCreator.createFirstErrorMessage(this.executableFlow, this.message, this.azkabanName, this.scheme, this.clientHostname, this.clientPortNumber));
        Assert.assertEquals("Flow 'mail-creator-test' has encountered a failure on unit-tests", this.message.getSubject());
        assertThat(TestUtils.readResource("firstErrorMessage.html", this)).isEqualToIgnoringWhitespace(this.message.getBody());
    }

    @Test
    public void createSuccessEmail() throws Exception {
        setJobStatus(SUCCEEDED);
        this.executableFlow.setEndTime(DefaultMailCreatorTest.END_TIME_MILLIS);
        this.executableFlow.setStatus(SUCCEEDED);
        Assert.assertTrue(this.mailCreator.createSuccessEmail(this.executableFlow, this.message, this.azkabanName, this.scheme, this.clientHostname, this.clientPortNumber));
        Assert.assertEquals("Flow 'mail-creator-test' has succeeded on unit-tests", this.message.getSubject());
        assertThat(TestUtils.readResource("successEmail.html", this)).isEqualToIgnoringWhitespace(this.message.getBody());
    }

    @Test
    public void createFailedUpdateMessage() throws Exception {
        final ExecutorManagerException exception = DefaultMailCreatorTest.createTestStracktrace();
        Assert.assertTrue(this.mailCreator.createFailedUpdateMessage(Arrays.asList(this.executableFlow, this.executableFlow), this.executor, exception, this.message, this.azkabanName, this.scheme, this.clientHostname, this.clientPortNumber));
        Assert.assertEquals("Flow status could not be updated from executor1-host on unit-tests", this.message.getSubject());
        assertThat(TestUtils.readResource("failedUpdateMessage.html", this)).isEqualToIgnoringWhitespace(this.message.getBody());
    }
}

