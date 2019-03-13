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
package org.sonar.api.rules;


import Rule.STATUS_BETA;
import Rule.STATUS_DEPRECATED;
import Rule.STATUS_READY;
import Rule.STATUS_REMOVED;
import RulePriority.BLOCKER;
import RulePriority.MAJOR;
import org.junit.Assert;
import org.junit.Test;
import org.sonar.api.utils.SonarException;


public class RuleTest {
    @Test
    public void description_should_be_cleaned() {
        Rule rule = Rule.create().setDescription("    my description         ");
        Assert.assertEquals("my description", rule.getDescription());
        rule.setDescription(null);
        assertThat(rule.getDescription()).isNull();
    }

    @Test
    public void should_remove_new_line_characters_in_name_with_setter() {
        Rule rule = Rule.create();
        for (String example : getExamplesContainingNewLineCharacter()) {
            rule.setName(example);
            assertThat(rule.getName()).isEqualTo("test");
        }
    }

    @Test
    public void should_remove_new_line_characters_in_name_with_first_constructor() {
        Rule rule;
        for (String example : getExamplesContainingNewLineCharacter()) {
            rule = new Rule(null, null).setName(example);
            assertThat(rule.getName()).isEqualTo("test");
        }
    }

    @Test
    public void should_remove_new_line_characters_in_name_with_second_constructor() {
        Rule rule;
        for (String example : getExamplesContainingNewLineCharacter()) {
            rule = new Rule(null, null).setName(example);
            assertThat(rule.getName()).isEqualTo("test");
        }
    }

    @Test
    public void default_priority_is_major() {
        Rule rule = Rule.create();
        assertThat(rule.getSeverity()).isEqualTo(MAJOR);
        rule = new Rule("name", "key");
        assertThat(rule.getSeverity()).isEqualTo(MAJOR);
        rule.setSeverity(BLOCKER);
        assertThat(rule.getSeverity()).isEqualTo(BLOCKER);
        rule.setSeverity(null);
        assertThat(rule.getSeverity()).isEqualTo(MAJOR);
    }

    @Test(expected = SonarException.class)
    public void should_not_authorize_unkown_status() {
        Rule.create().setStatus("Unknown");
    }

    @Test
    public void should_set_valid_status() {
        Rule rule = Rule.create().setStatus(STATUS_DEPRECATED);
        assertThat(rule.getStatus()).isEqualTo(STATUS_DEPRECATED);
        rule = Rule.create().setStatus(STATUS_REMOVED);
        assertThat(rule.getStatus()).isEqualTo(STATUS_REMOVED);
        rule = Rule.create().setStatus(STATUS_BETA);
        assertThat(rule.getStatus()).isEqualTo(STATUS_BETA);
        rule = Rule.create().setStatus(STATUS_READY);
        assertThat(rule.getStatus()).isEqualTo(STATUS_READY);
    }

    @Test
    public void testTags() {
        Rule rule = Rule.create();
        assertThat(rule.getTags()).isEmpty();
        assertThat(rule.getSystemTags()).isEmpty();
        rule.setTags(new String[]{ "tag1", "tag2" });
        assertThat(rule.getTags()).containsOnly("tag1", "tag2");
        assertThat(rule.getSystemTags()).isEmpty();
    }
}

