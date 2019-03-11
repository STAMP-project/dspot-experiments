/**
 * Copyright 2006-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.sample;


import ExitStatus.COMPLETED;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.sample.domain.mail.internal.TestMailErrorHandler;
import org.springframework.batch.sample.domain.mail.internal.TestMailSender;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Dan Garrette
 * @author Dave Syer
 * @since 2.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/simple-job-launcher-context.xml", "/jobs/mailJob.xml", "/job-runner-context.xml" })
public class MailJobFunctionalTests {
    private static final String email = "to@company.com";

    private static final Object[] USER1 = new Object[]{ 1, "George Washington", MailJobFunctionalTests.email };

    private static final Object[] USER2_SKIP = new Object[]{ 2, "John Adams", "FAILURE" };

    private static final Object[] USER3 = new Object[]{ 3, "Thomas Jefferson", MailJobFunctionalTests.email };

    private static final Object[] USER4_SKIP = new Object[]{ 4, "James Madison", "FAILURE" };

    private static final Object[] USER5 = new Object[]{ 5, "James Monroe", MailJobFunctionalTests.email };

    private static final Object[] USER6 = new Object[]{ 6, "John Quincy Adams", MailJobFunctionalTests.email };

    private static final Object[] USER7 = new Object[]{ 7, "Andrew Jackson", MailJobFunctionalTests.email };

    private static final Object[] USER8 = new Object[]{ 8, "Martin Van Buren", MailJobFunctionalTests.email };

    private JdbcOperations jdbcTemplate;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private TestMailErrorHandler errorHandler;

    @Autowired
    private TestMailSender mailSender;

    @Test
    public void testSkip() throws Exception {
        this.createUsers(new Object[][]{ MailJobFunctionalTests.USER1, MailJobFunctionalTests.USER2_SKIP, MailJobFunctionalTests.USER3, MailJobFunctionalTests.USER4_SKIP, MailJobFunctionalTests.USER5, MailJobFunctionalTests.USER6, MailJobFunctionalTests.USER7, MailJobFunctionalTests.USER8 });
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        Assert.assertEquals(COMPLETED, jobExecution.getExitStatus());
        List<SimpleMailMessage> receivedMessages = mailSender.getReceivedMessages();
        Assert.assertEquals(6, receivedMessages.size());
        Iterator<SimpleMailMessage> emailIter = receivedMessages.iterator();
        for (Object[] record : new Object[][]{ MailJobFunctionalTests.USER1, MailJobFunctionalTests.USER3, MailJobFunctionalTests.USER5, MailJobFunctionalTests.USER6, MailJobFunctionalTests.USER7, MailJobFunctionalTests.USER8 }) {
            SimpleMailMessage email = emailIter.next();
            Assert.assertEquals(("Hello " + (record[1])), email.getText());
        }
        Assert.assertEquals(2, this.errorHandler.getFailedMessages().size());
        Iterator<MailMessage> failureItr = this.errorHandler.getFailedMessages().iterator();
        for (Object[] record : new Object[][]{ MailJobFunctionalTests.USER2_SKIP, MailJobFunctionalTests.USER4_SKIP }) {
            SimpleMailMessage email = ((SimpleMailMessage) (failureItr.next()));
            Assert.assertEquals(("Hello " + (record[1])), email.getText());
        }
    }
}

