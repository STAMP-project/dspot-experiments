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
package org.sonar.server.issue.notification;


import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.notifications.Notification;
import org.sonar.plugins.emailnotifications.api.EmailMessage;
import org.sonar.server.l18n.I18nRule;

import static Metric.RULE_TYPE;
import static MyNewIssuesNotification.MY_NEW_ISSUES_NOTIF_TYPE;


public class MyNewIssuesEmailTemplateTest {
    @Rule
    public I18nRule i18n = new I18nRule().put("issue.type.BUG", "Bug").put("issue.type.CODE_SMELL", "Code Smell").put("issue.type.VULNERABILITY", "Vulnerability");

    private MapSettings settings = new MapSettings().setProperty("sonar.core.serverBaseURL", "http://nemo.sonarsource.org");

    private MyNewIssuesEmailTemplate underTest = new MyNewIssuesEmailTemplate(new org.sonar.api.config.EmailSettings(settings.asConfig()), i18n);

    @Test
    public void no_format_if_not_the_correct_notif() {
        Notification notification = new Notification("new-issues");
        EmailMessage message = underTest.format(notification);
        assertThat(message).isNull();
    }

    @Test
    public void format_email_with_all_fields_filled() {
        Notification notification = newNotification(32);
        addTags(notification);
        addRules(notification);
        addComponents(notification);
        EmailMessage message = underTest.format(notification);
        // TODO datetime to be completed when test is isolated from JVM timezone
        assertThat(message.getMessage()).startsWith(("Project: Struts\n" + (((((((((((((((((("\n" + "32 new issues (new debt: 1d3h)\n") + "\n") + "    Type\n") + "        Bug: 1    Vulnerability: 3    Code Smell: 0\n") + "\n") + "    Rules\n") + "        Rule the Universe (Clojure): 42\n") + "        Rule the World (Java): 5\n") + "\n") + "    Tags\n") + "        oscar: 3\n") + "        cesar: 10\n") + "\n") + "    Most impacted files\n") + "        /path/to/file: 3\n") + "        /path/to/directory: 7\n") + "\n") + "More details at: http://nemo.sonarsource.org/project/issues?id=org.apache%3Astruts&assignees=lo.gin&createdAt=2010-05-18")));
    }

    @Test
    public void message_id() {
        Notification notification = newNotification(32);
        EmailMessage message = underTest.format(notification);
        assertThat(message.getMessageId()).isEqualTo("my-new-issues/org.apache:struts");
    }

    @Test
    public void subject() {
        Notification notification = newNotification(32);
        EmailMessage message = underTest.format(notification);
        assertThat(message.getSubject()).isEqualTo("You have 32 new issues on project Struts");
    }

    @Test
    public void subject_on_branch() {
        Notification notification = newNotification(32).setFieldValue("branch", "feature1");
        EmailMessage message = underTest.format(notification);
        assertThat(message.getSubject()).isEqualTo("You have 32 new issues on project Struts (feature1)");
    }

    @Test
    public void format_email_with_no_assignees_tags_nor_components() {
        Notification notification = newNotification(32).setFieldValue("codePeriodVersion", "52.0");
        EmailMessage message = underTest.format(notification);
        // TODO datetime to be completed when test is isolated from JVM timezone
        assertThat(message.getMessage()).startsWith(("Project: Struts\n" + ((((((("Version: 52.0\n" + "\n") + "32 new issues (new debt: 1d3h)\n") + "\n") + "    Type\n") + "        Bug: 1    Vulnerability: 3    Code Smell: 0\n") + "\n") + "More details at: http://nemo.sonarsource.org/project/issues?id=org.apache%3Astruts&assignees=lo.gin&createdAt=2010-05-18")));
    }

    @Test
    public void format_email_with_issue_on_branch() {
        Notification notification = newNotification(32).setFieldValue("codePeriodVersion", "52.0").setFieldValue("branch", "feature1");
        EmailMessage message = underTest.format(notification);
        // TODO datetime to be completed when test is isolated from JVM timezone
        assertThat(message.getMessage()).startsWith(("Project: Struts\n" + (((((((("Branch: feature1\n" + "Version: 52.0\n") + "\n") + "32 new issues (new debt: 1d3h)\n") + "\n") + "    Type\n") + "        Bug: 1    Vulnerability: 3    Code Smell: 0\n") + "\n") + "More details at: http://nemo.sonarsource.org/project/issues?id=org.apache%3Astruts&assignees=lo.gin&branch=feature1&createdAt=2010-05-18")));
    }

    @Test
    public void format_email_supports_single_issue() {
        Notification notification = newNotification(1);
        EmailMessage message = underTest.format(notification);
        assertThat(message.getSubject()).isEqualTo("You have 1 new issue on project Struts");
        assertThat(message.getMessage()).contains("1 new issue (new debt: 1d3h)\n");
    }

    @Test
    public void format_supports_null_version() {
        Notification notification = newNotification(32).setFieldValue("branch", "feature1");
        EmailMessage message = underTest.format(notification);
        // TODO datetime to be completed when test is isolated from JVM timezone
        assertThat(message.getMessage()).startsWith(("Project: Struts\n" + ((((((("Branch: feature1\n" + "\n") + "32 new issues (new debt: 1d3h)\n") + "\n") + "    Type\n") + "        Bug: 1    Vulnerability: 3    Code Smell: 0\n") + "\n") + "More details at: http://nemo.sonarsource.org/project/issues?id=org.apache%3Astruts&assignees=lo.gin&branch=feature1&createdAt=2010-05-18")));
    }

    @Test
    public void do_not_add_footer_when_properties_missing() {
        Notification notification = new Notification(MY_NEW_ISSUES_NOTIF_TYPE).setFieldValue(((RULE_TYPE) + ".count"), "32").setFieldValue("projectName", "Struts");
        EmailMessage message = underTest.format(notification);
        assertThat(message.getMessage()).doesNotContain("See it");
    }
}

