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
package org.sonar.api.server.rule;


import RuleParamType.BOOLEAN;
import RuleParamType.FLOAT;
import RuleParamType.INTEGER;
import RuleParamType.STRING;
import RuleParamType.TEXT;
import RuleStatus.BETA;
import RulesDefinition.Context;
import RulesDefinition.NewRepository;
import RulesDefinition.Param;
import RulesDefinition.Repository;
import Severity.BLOCKER;
import Severity.MAJOR;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.server.rule.RulesDefinition.NewRule;
import org.sonar.check.Priority;
import org.sonar.check.RuleProperty;


public class RulesDefinitionAnnotationLoaderTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    RulesDefinitionAnnotationLoader annotationLoader = new RulesDefinitionAnnotationLoader();

    @Test
    public void rule_with_property() {
        RulesDefinition.Repository repository = load(RulesDefinitionAnnotationLoaderTest.RuleWithProperty.class);
        assertThat(repository.rules()).hasSize(1);
        RulesDefinition.Rule rule = repository.rules().get(0);
        assertThat(rule.key()).isEqualTo("foo");
        assertThat(rule.status()).isEqualTo(BETA);
        assertThat(rule.name()).isEqualTo("bar");
        assertThat(rule.htmlDescription()).isEqualTo("Foo Bar");
        assertThat(rule.severity()).isEqualTo(BLOCKER);
        assertThat(rule.params()).hasSize(1);
        assertThat(rule.tags()).isEmpty();
        RulesDefinition.Param prop = rule.param("property");
        assertThat(prop.key()).isEqualTo("property");
        assertThat(prop.description()).isEqualTo("Ignore ?");
        assertThat(prop.defaultValue()).isEqualTo("false");
        assertThat(prop.type()).isEqualTo(STRING);
    }

    @Test
    public void override_annotation_programmatically() {
        RulesDefinition.Context context = new RulesDefinition.Context();
        RulesDefinition.NewRepository newRepository = context.createRepository("squid", "java");
        NewRule newRule = annotationLoader.loadRule(newRepository, RulesDefinitionAnnotationLoaderTest.RuleWithProperty.class);
        newRule.setName("Overridden name");
        newRule.param("property").setDefaultValue("true");
        newRule.param("property").setDescription("Overridden");
        newRepository.done();
        RulesDefinition.Repository repository = context.repository("squid");
        assertThat(repository.rules()).hasSize(1);
        RulesDefinition.Rule rule = repository.rules().get(0);
        assertThat(rule.key()).isEqualTo("foo");
        assertThat(rule.status()).isEqualTo(BETA);
        assertThat(rule.name()).isEqualTo("Overridden name");
        assertThat(rule.htmlDescription()).isEqualTo("Foo Bar");
        assertThat(rule.severity()).isEqualTo(BLOCKER);
        assertThat(rule.params()).hasSize(1);
        RulesDefinition.Param prop = rule.param("property");
        assertThat(prop.key()).isEqualTo("property");
        assertThat(prop.description()).isEqualTo("Overridden");
        assertThat(prop.defaultValue()).isEqualTo("true");
        assertThat(prop.type()).isEqualTo(STRING);
    }

    @Test
    public void rule_with_integer_property() {
        RulesDefinition.Repository repository = load(RulesDefinitionAnnotationLoaderTest.RuleWithIntegerProperty.class);
        RulesDefinition.Param prop = repository.rules().get(0).param("property");
        assertThat(prop.description()).isEqualTo("Max");
        assertThat(prop.defaultValue()).isEqualTo("12");
        assertThat(prop.type()).isEqualTo(INTEGER);
    }

    @Test
    public void rule_with_text_property() {
        RulesDefinition.Repository repository = load(RulesDefinitionAnnotationLoaderTest.RuleWithTextProperty.class);
        RulesDefinition.Param prop = repository.rules().get(0).param("property");
        assertThat(prop.description()).isEqualTo("text");
        assertThat(prop.defaultValue()).isEqualTo("Long text");
        assertThat(prop.type()).isEqualTo(TEXT);
    }

    @Test
    public void should_recognize_type() {
        assertThat(RulesDefinitionAnnotationLoader.guessType(Integer.class)).isEqualTo(INTEGER);
        assertThat(RulesDefinitionAnnotationLoader.guessType(int.class)).isEqualTo(INTEGER);
        assertThat(RulesDefinitionAnnotationLoader.guessType(Float.class)).isEqualTo(FLOAT);
        assertThat(RulesDefinitionAnnotationLoader.guessType(float.class)).isEqualTo(FLOAT);
        assertThat(RulesDefinitionAnnotationLoader.guessType(Boolean.class)).isEqualTo(BOOLEAN);
        assertThat(RulesDefinitionAnnotationLoader.guessType(boolean.class)).isEqualTo(BOOLEAN);
        assertThat(RulesDefinitionAnnotationLoader.guessType(String.class)).isEqualTo(STRING);
        assertThat(RulesDefinitionAnnotationLoader.guessType(Object.class)).isEqualTo(STRING);
    }

    @Test
    public void use_classname_when_missing_key() {
        RulesDefinition.Repository repository = load(RulesDefinitionAnnotationLoaderTest.RuleWithoutKey.class);
        assertThat(repository.rules()).hasSize(1);
        RulesDefinition.Rule rule = repository.rules().get(0);
        assertThat(rule.key()).isEqualTo(RulesDefinitionAnnotationLoaderTest.RuleWithoutKey.class.getCanonicalName());
        assertThat(rule.name()).isEqualTo("foo");
    }

    @Test
    public void rule_with_tags() {
        RulesDefinition.Repository repository = load(RulesDefinitionAnnotationLoaderTest.RuleWithTags.class);
        assertThat(repository.rules()).hasSize(1);
        RulesDefinition.Rule rule = repository.rules().get(0);
        assertThat(rule.tags()).containsOnly("misra", "clumsy");
    }

    @Test
    public void overridden_class() {
        RulesDefinition.Repository repository = load(RulesDefinitionAnnotationLoaderTest.OverridingRule.class);
        assertThat(repository.rules()).hasSize(1);
        RulesDefinition.Rule rule = repository.rules().get(0);
        assertThat(rule.key()).isEqualTo("overriding_foo");
        assertThat(rule.name()).isEqualTo("Overriding Foo");
        assertThat(rule.severity()).isEqualTo(MAJOR);
        assertThat(rule.htmlDescription()).isEqualTo("Desc of Overriding Foo");
        assertThat(rule.params()).hasSize(2);
    }

    @org.sonar.check.Rule(name = "foo", description = "Foo")
    static class RuleWithoutKey {}

    @org.sonar.check.Rule(key = "foo", name = "bar", description = "Foo Bar", priority = Priority.BLOCKER, status = "BETA")
    static class RuleWithProperty {
        @RuleProperty(description = "Ignore ?", defaultValue = "false")
        private String property;
    }

    @org.sonar.check.Rule(key = "overriding_foo", name = "Overriding Foo", description = "Desc of Overriding Foo")
    static class OverridingRule extends RulesDefinitionAnnotationLoaderTest.RuleWithProperty {
        @RuleProperty
        private String additionalProperty;
    }

    @org.sonar.check.Rule(key = "foo", name = "bar", description = "Foo Bar", priority = Priority.BLOCKER)
    static class RuleWithIntegerProperty {
        @RuleProperty(description = "Max", defaultValue = "12")
        private Integer property;
    }

    @org.sonar.check.Rule(key = "foo", name = "bar", description = "Foo Bar", priority = Priority.BLOCKER)
    static class RuleWithTextProperty {
        @RuleProperty(description = "text", defaultValue = "Long text", type = "TEXT")
        protected String property;
    }

    @org.sonar.check.Rule(key = "foo", name = "bar", description = "Foo Bar", priority = Priority.BLOCKER)
    static class RuleWithInvalidPropertyType {
        @RuleProperty(description = "text", defaultValue = "Long text", type = "INVALID")
        public String property;
    }

    @org.sonar.check.Rule(key = "foo", name = "bar", description = "Bar", tags = { "misra", "clumsy" })
    static class RuleWithTags {}
}

