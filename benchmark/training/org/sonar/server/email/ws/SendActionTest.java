/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.email.ws;


import WebService.Action;
import org.apache.commons.mail.EmailException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.server.ws.WebService;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.notification.email.EmailNotificationChannel;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;


public class SendActionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    private EmailNotificationChannel emailNotificationChannel = Mockito.mock(EmailNotificationChannel.class);

    private WsActionTester ws = new WsActionTester(new SendAction(userSession, emailNotificationChannel));

    @Test
    public void send_test_email() throws Exception {
        logInAsSystemAdministrator();
        executeRequest("john@doo.com", "Test Message from SonarQube", "This is a test message from SonarQube at http://localhost:9000");
        Mockito.verify(emailNotificationChannel).sendTestEmail("john@doo.com", "Test Message from SonarQube", "This is a test message from SonarQube at http://localhost:9000");
    }

    @Test
    public void does_not_fail_when_subject_param_is_missing() throws Exception {
        logInAsSystemAdministrator();
        executeRequest("john@doo.com", null, "This is a test message from SonarQube at http://localhost:9000");
        Mockito.verify(emailNotificationChannel).sendTestEmail("john@doo.com", null, "This is a test message from SonarQube at http://localhost:9000");
    }

    @Test
    public void fail_when_to_param_is_missing() {
        logInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        executeRequest(null, "Test Message from SonarQube", "This is a test message from SonarQube at http://localhost:9000");
    }

    @Test
    public void fail_when_message_param_is_missing() {
        logInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        executeRequest("john@doo.com", "Test Message from SonarQube", null);
    }

    @Test
    public void throw_ForbiddenException_if_not_system_administrator() {
        userSession.logIn().setNonSystemAdministrator();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        ws.newRequest().execute();
    }

    @Test
    public void fail_with_BadRequestException_when_EmailException_is_generated() throws Exception {
        logInAsSystemAdministrator();
        IllegalArgumentException exception1 = new IllegalArgumentException("root cause");
        IllegalArgumentException exception2 = new IllegalArgumentException("parent cause", exception1);
        IllegalArgumentException exception3 = new IllegalArgumentException("child cause", exception2);
        EmailException emailException = new EmailException("last message", exception3);
        Mockito.doThrow(emailException).when(emailNotificationChannel).sendTestEmail(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        try {
            executeRequest("john@doo.com", "Test Message from SonarQube", "This is a test message from SonarQube at http://localhost:9000");
            Assert.fail();
        } catch (BadRequestException e) {
            assertThat(e.errors()).containsExactly("root cause", "parent cause", "child cause", "last message");
        }
    }

    @Test
    public void test_ws_definition() {
        WebService.Action action = ws.getDef();
        assertThat(action).isNotNull();
        assertThat(action.isInternal()).isTrue();
        assertThat(action.isPost()).isTrue();
        assertThat(action.responseExampleAsString()).isNull();
        assertThat(action.params()).hasSize(3);
    }
}

