/**
 * Copyright 2017 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.utils;


import azkaban.executor.ExecutableFlow;
import azkaban.executor.Executor;
import azkaban.executor.ExecutorLoader;
import azkaban.executor.ExecutorManagerException;
import azkaban.executor.mail.DefaultMailCreatorTest;
import azkaban.flow.Flow;
import azkaban.metrics.CommonMetrics;
import azkaban.project.Project;
import com.codahale.metrics.MetricRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.mail.internet.AddressException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class EmailerTest {
    private static final String receiveAddr = "receive@domain.com";// receiver email address


    private final List<String> receiveAddrList = new ArrayList<>();

    private Project project;

    private Props props;

    private EmailMessageCreator messageCreator;

    private EmailMessage message;

    private ExecutorLoader executorLoader;

    @Test
    public void testSendErrorEmail() throws Exception {
        final Flow flow = this.project.getFlow("jobe");
        flow.addFailureEmails(this.receiveAddrList);
        Assert.assertNotNull(flow);
        final ExecutableFlow exFlow = new ExecutableFlow(this.project, flow);
        final CommonMetrics commonMetrics = new CommonMetrics(new azkaban.metrics.MetricsManager(new MetricRegistry()));
        final Emailer emailer = new Emailer(this.props, commonMetrics, this.messageCreator, this.executorLoader);
        emailer.alertOnError(exFlow);
        Mockito.verify(this.message).addAllToAddress(this.receiveAddrList);
        Mockito.verify(this.message).setSubject("Flow 'jobe' has failed on azkaban");
        assertThat(TestUtils.readResource("errorEmail2.html", this)).isEqualToIgnoringWhitespace(this.message.getBody());
    }

    @Test
    public void alertOnFailedUpdate() throws Exception {
        final Flow flow = this.project.getFlow("jobe");
        flow.addFailureEmails(this.receiveAddrList);
        Assert.assertNotNull(flow);
        final ExecutableFlow exFlow = new ExecutableFlow(this.project, flow);
        final CommonMetrics commonMetrics = new CommonMetrics(new azkaban.metrics.MetricsManager(new MetricRegistry()));
        final Emailer emailer = new Emailer(this.props, commonMetrics, this.messageCreator, this.executorLoader);
        final Executor executor = new Executor(1, "executor1-host", 1234, true);
        final List<ExecutableFlow> executions = Arrays.asList(exFlow, exFlow);
        final ExecutorManagerException exception = DefaultMailCreatorTest.createTestStracktrace();
        emailer.alertOnFailedUpdate(executor, executions, exception);
        Mockito.verify(this.message).addAllToAddress(this.receiveAddrList);
        Mockito.verify(this.message).setSubject("Flow status could not be updated from executor1-host on azkaban");
        assertThat(TestUtils.readResource("failedUpdateMessage2.html", this)).isEqualToIgnoringWhitespace(this.message.getBody());
    }

    @Test
    public void testGetAzkabanURL() {
        final CommonMetrics commonMetrics = new CommonMetrics(new azkaban.metrics.MetricsManager(new MetricRegistry()));
        final Emailer emailer = new Emailer(this.props, commonMetrics, this.messageCreator, this.executorLoader);
        assertThat(emailer.getAzkabanURL()).isEqualTo("http://localhost:8786");
    }

    @Test
    public void testCreateEmailMessage() {
        final CommonMetrics commonMetrics = new CommonMetrics(new azkaban.metrics.MetricsManager(new MetricRegistry()));
        final Emailer emailer = new Emailer(this.props, commonMetrics, this.messageCreator, this.executorLoader);
        final EmailMessage em = emailer.createEmailMessage("subject", "text/html", this.receiveAddrList);
        Mockito.verify(this.messageCreator).createMessage();
        assertThat(this.messageCreator.createMessage()).isEqualTo(em);
        Mockito.verify(this.message).addAllToAddress(this.receiveAddrList);
        Mockito.verify(this.message).setSubject("subject");
        Mockito.verify(this.message).setMimeType("text/html");
    }

    @Test
    public void testSendEmailToInvalidAddress() throws Exception {
        Mockito.doThrow(AddressException.class).when(this.message).sendEmail();
        final Flow flow = this.project.getFlow("jobe");
        flow.addFailureEmails(this.receiveAddrList);
        final ExecutableFlow exFlow = new ExecutableFlow(this.project, flow);
        final CommonMetrics commonMetrics = Mockito.mock(CommonMetrics.class);
        final Emailer emailer = new Emailer(this.props, commonMetrics, this.messageCreator, this.executorLoader);
        emailer.alertOnError(exFlow);
        Mockito.verify(commonMetrics, Mockito.never()).markSendEmailFail();
    }
}

