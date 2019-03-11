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


import PropertyType.BOOLEAN;
import PropertyType.FLOAT;
import PropertyType.INTEGER;
import PropertyType.STRING;
import PropertyType.TEXT;
import Rule.STATUS_READY;
import RulePriority.BLOCKER;
import RulePriority.MAJOR;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.SonarException;
import org.sonar.check.Priority;
import org.sonar.check.RuleProperty;

import static Rule.STATUS_READY;


public class AnnotationRuleParserTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void rule_with_property() {
        List<Rule> rules = parseAnnotatedClass(AnnotationRuleParserTest.RuleWithProperty.class);
        assertThat(rules).hasSize(1);
        Rule rule = rules.get(0);
        assertThat(rule.getKey()).isEqualTo("foo");
        assertThat(rule.getName()).isEqualTo("bar");
        assertThat(rule.getDescription()).isEqualTo("Foo Bar");
        assertThat(rule.getSeverity()).isEqualTo(BLOCKER);
        assertThat(rule.getStatus()).isEqualTo(STATUS_READY);
        assertThat(rule.getParams()).hasSize(1);
        RuleParam prop = rule.getParam("property");
        assertThat(prop.getKey()).isEqualTo("property");
        assertThat(prop.getDescription()).isEqualTo("Ignore ?");
        assertThat(prop.getDefaultValue()).isEqualTo("false");
        assertThat(prop.getType()).isEqualTo(STRING.name());
    }

    @Test
    public void rule_with_integer_property() {
        List<Rule> rules = parseAnnotatedClass(AnnotationRuleParserTest.RuleWithIntegerProperty.class);
        RuleParam prop = rules.get(0).getParam("property");
        assertThat(prop.getDescription()).isEqualTo("Max");
        assertThat(prop.getDefaultValue()).isEqualTo("12");
        assertThat(prop.getType()).isEqualTo(INTEGER.name());
    }

    @Test
    public void rule_with_text_property() {
        List<Rule> rules = parseAnnotatedClass(AnnotationRuleParserTest.RuleWithTextProperty.class);
        RuleParam prop = rules.get(0).getParam("property");
        assertThat(prop.getDescription()).isEqualTo("text");
        assertThat(prop.getDefaultValue()).isEqualTo("Long text");
        assertThat(prop.getType()).isEqualTo(TEXT.name());
    }

    @Test
    public void should_reject_invalid_property_types() {
        exception.expect(SonarException.class);
        exception.expectMessage("Invalid property type [INVALID]");
        parseAnnotatedClass(AnnotationRuleParserTest.RuleWithInvalidPropertyType.class);
    }

    @Test
    public void should_recognize_type() {
        assertThat(AnnotationRuleParser.guessType(Integer.class)).isEqualTo(INTEGER);
        assertThat(AnnotationRuleParser.guessType(int.class)).isEqualTo(INTEGER);
        assertThat(AnnotationRuleParser.guessType(Float.class)).isEqualTo(FLOAT);
        assertThat(AnnotationRuleParser.guessType(float.class)).isEqualTo(FLOAT);
        assertThat(AnnotationRuleParser.guessType(Boolean.class)).isEqualTo(BOOLEAN);
        assertThat(AnnotationRuleParser.guessType(boolean.class)).isEqualTo(BOOLEAN);
        assertThat(AnnotationRuleParser.guessType(String.class)).isEqualTo(STRING);
        assertThat(AnnotationRuleParser.guessType(Object.class)).isEqualTo(STRING);
    }

    @Test
    public void rule_without_name_nor_description() {
        List<Rule> rules = parseAnnotatedClass(AnnotationRuleParserTest.RuleWithoutNameNorDescription.class);
        assertThat(rules).hasSize(1);
        Rule rule = rules.get(0);
        assertThat(rule.getKey()).isEqualTo("foo");
        assertThat(rule.getSeverity()).isEqualTo(MAJOR);
        assertThat(rule.getName()).isNull();
        assertThat(rule.getDescription()).isNull();
    }

    @Test
    public void rule_without_key() {
        List<Rule> rules = parseAnnotatedClass(AnnotationRuleParserTest.RuleWithoutKey.class);
        assertThat(rules).hasSize(1);
        Rule rule = rules.get(0);
        assertThat(rule.getKey()).isEqualTo(AnnotationRuleParserTest.RuleWithoutKey.class.getCanonicalName());
        assertThat(rule.getName()).isEqualTo("foo");
        assertThat(rule.getDescription()).isNull();
        assertThat(rule.getSeverity()).isEqualTo(MAJOR);
    }

    @Test
    public void overridden_rule() {
        List<Rule> rules = parseAnnotatedClass(AnnotationRuleParserTest.OverridingRule.class);
        assertThat(rules).hasSize(1);
        Rule rule = rules.get(0);
        assertThat(rule.getKey()).isEqualTo("overriding_foo");
        assertThat(rule.getName()).isEqualTo("Overriding Foo");
        assertThat(rule.getDescription()).isNull();
        assertThat(rule.getSeverity()).isEqualTo(MAJOR);
        assertThat(rule.getParams()).hasSize(2);
    }

    @org.sonar.check.Rule(name = "foo")
    static class RuleWithoutKey {}

    @org.sonar.check.Rule(key = "foo")
    static class RuleWithoutNameNorDescription {}

    @org.sonar.check.Rule(key = "foo", name = "bar", description = "Foo Bar", status = STATUS_READY, priority = Priority.BLOCKER)
    static class RuleWithProperty {
        @RuleProperty(description = "Ignore ?", defaultValue = "false")
        private String property;
    }

    @org.sonar.check.Rule(key = "overriding_foo", name = "Overriding Foo")
    static class OverridingRule extends AnnotationRuleParserTest.RuleWithProperty {
        @RuleProperty
        private String additionalProperty;
    }

    @org.sonar.check.Rule(key = "foo", name = "bar", description = "Foo Bar", status = Rule.STATUS_READY, priority = Priority.BLOCKER)
    static class RuleWithIntegerProperty {
        @RuleProperty(description = "Max", defaultValue = "12")
        private Integer property;
    }

    @org.sonar.check.Rule(key = "foo", name = "bar", description = "Foo Bar", status = Rule.STATUS_READY, priority = Priority.BLOCKER)
    static class RuleWithTextProperty {
        @RuleProperty(description = "text", defaultValue = "Long text", type = "TEXT")
        protected String property;
    }

    @org.sonar.check.Rule(key = "foo", name = "bar", description = "Foo Bar", status = Rule.STATUS_READY, priority = Priority.BLOCKER)
    static class RuleWithInvalidPropertyType {
        @RuleProperty(description = "text", defaultValue = "Long text", type = "INVALID")
        public String property;
    }
}

