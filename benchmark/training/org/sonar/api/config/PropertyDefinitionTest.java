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
package org.sonar.api.config;


import PropertyDefinition.Builder;
import PropertyType.BOOLEAN;
import PropertyType.FLOAT;
import PropertyType.INTEGER;
import PropertyType.LICENSE;
import PropertyType.LONG;
import PropertyType.PASSWORD;
import PropertyType.PROPERTY_SET;
import PropertyType.REGULAR_EXPRESSION;
import PropertyType.SINGLE_SELECT_LIST;
import PropertyType.STRING;
import Qualifiers.MODULE;
import Qualifiers.PROJECT;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.PropertyField;
import org.sonar.api.PropertyType;
import org.sonar.api.utils.AnnotationUtils;


public class PropertyDefinitionTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void should_override_toString() {
        PropertyDefinition def = PropertyDefinition.builder("hello").build();
        assertThat(def.toString()).isEqualTo("hello");
    }

    @Test
    public void should_create_property() {
        PropertyDefinition def = PropertyDefinition.builder("hello").name("Hello").defaultValue("world").category("categ").options("de", "en").description("desc").type(FLOAT).onlyOnQualifiers(MODULE).multiValues(true).propertySetKey("set").build();
        assertThat(def.key()).isEqualTo("hello");
        assertThat(def.name()).isEqualTo("Hello");
        assertThat(def.defaultValue()).isEqualTo("world");
        assertThat(def.category()).isEqualTo("categ");
        assertThat(def.options()).containsOnly("de", "en");
        assertThat(def.description()).isEqualTo("desc");
        assertThat(def.type()).isEqualTo(FLOAT);
        assertThat(def.global()).isFalse();
        assertThat(def.qualifiers()).containsOnly(MODULE);
        assertThat(def.multiValues()).isTrue();
        assertThat(def.propertySetKey()).isEqualTo("set");
        assertThat(def.fields()).isEmpty();
    }

    @Test
    public void should_create_from_annotation() {
        Properties props = AnnotationUtils.getAnnotation(PropertyDefinitionTest.Init.class, Properties.class);
        Property prop = props.value()[0];
        PropertyDefinition def = PropertyDefinition.create(prop);
        assertThat(def.key()).isEqualTo("hello");
        assertThat(def.name()).isEqualTo("Hello");
        assertThat(def.defaultValue()).isEqualTo("world");
        assertThat(def.category()).isEqualTo("categ");
        assertThat(def.options()).containsOnly("de", "en");
        assertThat(def.description()).isEqualTo("desc");
        assertThat(def.type()).isEqualTo(FLOAT);
        assertThat(def.global()).isFalse();
        assertThat(def.qualifiers()).containsOnly(PROJECT, MODULE);
        assertThat(def.multiValues()).isTrue();
        assertThat(def.propertySetKey()).isEqualTo("set");
        assertThat(def.fields()).isEmpty();
    }

    @Test
    public void should_create_hidden_property() {
        PropertyDefinition def = PropertyDefinition.builder("hello").name("Hello").hidden().build();
        assertThat(def.key()).isEqualTo("hello");
        assertThat(def.qualifiers()).isEmpty();
        assertThat(def.global()).isFalse();
    }

    @Test
    public void should_create_property_with_default_values() {
        PropertyDefinition def = PropertyDefinition.builder("hello").name("Hello").build();
        assertThat(def.key()).isEqualTo("hello");
        assertThat(def.name()).isEqualTo("Hello");
        assertThat(def.defaultValue()).isEmpty();
        assertThat(def.category()).isEmpty();
        assertThat(def.options()).isEmpty();
        assertThat(def.description()).isEmpty();
        assertThat(def.type()).isEqualTo(STRING);
        assertThat(def.global()).isTrue();
        assertThat(def.qualifiers()).isEmpty();
        assertThat(def.multiValues()).isFalse();
        assertThat(def.propertySetKey()).isEmpty();
        assertThat(def.fields()).isEmpty();
    }

    @Test
    public void should_create_from_annotation_default_values() {
        Properties props = AnnotationUtils.getAnnotation(PropertyDefinitionTest.DefaultValues.class, Properties.class);
        Property prop = props.value()[0];
        PropertyDefinition def = PropertyDefinition.create(prop);
        assertThat(def.key()).isEqualTo("hello");
        assertThat(def.name()).isEqualTo("Hello");
        assertThat(def.defaultValue()).isEmpty();
        assertThat(def.category()).isEmpty();
        assertThat(def.options()).isEmpty();
        assertThat(def.description()).isEmpty();
        assertThat(def.type()).isEqualTo(STRING);
        assertThat(def.global()).isTrue();
        assertThat(def.qualifiers()).isEmpty();
        assertThat(def.multiValues()).isFalse();
        assertThat(def.propertySetKey()).isEmpty();
        assertThat(def.fields()).isEmpty();
    }

    @Test
    public void should_support_property_sets() {
        PropertyDefinition def = PropertyDefinition.builder("hello").name("Hello").fields(PropertyFieldDefinition.build("first").name("First").description("Description").options("A", "B").build(), PropertyFieldDefinition.build("second").name("Second").type(INTEGER).indicativeSize(5).build()).build();
        assertThat(def.type()).isEqualTo(PROPERTY_SET);
        assertThat(def.fields()).hasSize(2);
        assertThat(def.fields().get(0).key()).isEqualTo("first");
        assertThat(def.fields().get(0).name()).isEqualTo("First");
        assertThat(def.fields().get(0).description()).isEqualTo("Description");
        assertThat(def.fields().get(0).type()).isEqualTo(STRING);
        assertThat(def.fields().get(0).options()).containsOnly("A", "B");
        assertThat(def.fields().get(0).indicativeSize()).isEqualTo(20);
        assertThat(def.fields().get(1).key()).isEqualTo("second");
        assertThat(def.fields().get(1).name()).isEqualTo("Second");
        assertThat(def.fields().get(1).type()).isEqualTo(INTEGER);
        assertThat(def.fields().get(1).options()).isEmpty();
        assertThat(def.fields().get(1).indicativeSize()).isEqualTo(5);
    }

    @Test
    public void should_support_property_sets_from_annotation() {
        Properties props = AnnotationUtils.getAnnotation(PropertyDefinitionTest.WithPropertySet.class, Properties.class);
        Property prop = props.value()[0];
        PropertyDefinition def = PropertyDefinition.create(prop);
        assertThat(def.type()).isEqualTo(PROPERTY_SET);
        assertThat(def.fields()).hasSize(2);
        assertThat(def.fields().get(0).key()).isEqualTo("first");
        assertThat(def.fields().get(0).name()).isEqualTo("First");
        assertThat(def.fields().get(0).description()).isEqualTo("Description");
        assertThat(def.fields().get(0).type()).isEqualTo(STRING);
        assertThat(def.fields().get(0).options()).containsOnly("A", "B");
        assertThat(def.fields().get(0).indicativeSize()).isEqualTo(20);
        assertThat(def.fields().get(1).key()).isEqualTo("second");
        assertThat(def.fields().get(1).name()).isEqualTo("Second");
        assertThat(def.fields().get(1).type()).isEqualTo(INTEGER);
        assertThat(def.fields().get(1).options()).isEmpty();
        assertThat(def.fields().get(1).indicativeSize()).isEqualTo(5);
    }

    @Test
    public void should_validate_string() {
        PropertyDefinition def = PropertyDefinition.builder("foo").name("foo").type(STRING).build();
        assertThat(def.validate(null).isValid()).isTrue();
        assertThat(def.validate("").isValid()).isTrue();
        assertThat(def.validate("   ").isValid()).isTrue();
        assertThat(def.validate("foo").isValid()).isTrue();
    }

    @Test
    public void should_validate_boolean() {
        PropertyDefinition def = PropertyDefinition.builder("foo").name("foo").type(BOOLEAN).build();
        assertThat(def.validate(null).isValid()).isTrue();
        assertThat(def.validate("").isValid()).isTrue();
        assertThat(def.validate("   ").isValid()).isTrue();
        assertThat(def.validate("true").isValid()).isTrue();
        assertThat(def.validate("false").isValid()).isTrue();
        assertThat(def.validate("foo").isValid()).isFalse();
        assertThat(def.validate("foo").getErrorKey()).isEqualTo("notBoolean");
    }

    @Test
    public void should_validate_integer() {
        PropertyDefinition def = PropertyDefinition.builder("foo").name("foo").type(INTEGER).build();
        assertThat(def.validate(null).isValid()).isTrue();
        assertThat(def.validate("").isValid()).isTrue();
        assertThat(def.validate("   ").isValid()).isTrue();
        assertThat(def.validate("123456").isValid()).isTrue();
        assertThat(def.validate("foo").isValid()).isFalse();
        assertThat(def.validate("foo").getErrorKey()).isEqualTo("notInteger");
    }

    @Test
    public void validate_long() {
        PropertyDefinition def = PropertyDefinition.builder("foo").name("foo").type(LONG).build();
        assertThat(def.validate(null).isValid()).isTrue();
        assertThat(def.validate("").isValid()).isTrue();
        assertThat(def.validate("   ").isValid()).isTrue();
        assertThat(def.validate("123456").isValid()).isTrue();
        assertThat(def.validate("foo").isValid()).isFalse();
        assertThat(def.validate("foo").getErrorKey()).isEqualTo("notInteger");
    }

    @Test
    public void should_validate_float() {
        PropertyDefinition def = PropertyDefinition.builder("foo").name("foo").type(FLOAT).build();
        assertThat(def.validate(null).isValid()).isTrue();
        assertThat(def.validate("").isValid()).isTrue();
        assertThat(def.validate("   ").isValid()).isTrue();
        assertThat(def.validate("123456").isValid()).isTrue();
        assertThat(def.validate("3.14").isValid()).isTrue();
        assertThat(def.validate("foo").isValid()).isFalse();
        assertThat(def.validate("foo").getErrorKey()).isEqualTo("notFloat");
    }

    @Test
    public void validate_regular_expression() {
        PropertyDefinition def = PropertyDefinition.builder("foo").name("foo").type(REGULAR_EXPRESSION).build();
        assertThat(def.validate(null).isValid()).isTrue();
        assertThat(def.validate("").isValid()).isTrue();
        assertThat(def.validate("   ").isValid()).isTrue();
        assertThat(def.validate("[a-zA-Z]").isValid()).isTrue();
        assertThat(def.validate("[a-zA-Z").isValid()).isFalse();
        assertThat(def.validate("[a-zA-Z").getErrorKey()).isEqualTo("notRegexp");
    }

    @Test
    public void should_validate_single_select_list() {
        PropertyDefinition def = PropertyDefinition.builder("foo").name("foo").type(SINGLE_SELECT_LIST).options("de", "en").build();
        assertThat(def.validate(null).isValid()).isTrue();
        assertThat(def.validate("").isValid()).isTrue();
        assertThat(def.validate("   ").isValid()).isTrue();
        assertThat(def.validate("de").isValid()).isTrue();
        assertThat(def.validate("en").isValid()).isTrue();
        assertThat(def.validate("fr").isValid()).isFalse();
        assertThat(def.validate("fr").getErrorKey()).isEqualTo("notInOptions");
    }

    @Test
    public void should_auto_detect_password_type() {
        PropertyDefinition def = PropertyDefinition.builder("scm.password.secured").name("SCM password").build();
        assertThat(def.key()).isEqualTo("scm.password.secured");
        assertThat(def.type()).isEqualTo(PASSWORD);
    }

    @Test
    public void PropertyDef() {
        PropertyDefinition def = PropertyDefinition.builder("views.license.secured").name("Views license").build();
        assertThat(def.key()).isEqualTo("views.license.secured");
        assertThat(def.type()).isEqualTo(LICENSE);
    }

    @Test
    public void should_not_authorise_empty_key() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Key must be set");
        PropertyDefinition.builder(null).build();
    }

    @Test
    public void should_not_authorize_defining_on_qualifiers_and_hidden() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot be hidden and defining qualifiers on which to display");
        PropertyDefinition.builder("foo").name("foo").onQualifiers(PROJECT).hidden().build();
    }

    @Test
    public void should_not_authorize_defining_ony_on_qualifiers_and_hidden() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot be hidden and defining qualifiers on which to display");
        PropertyDefinition.builder("foo").name("foo").onlyOnQualifiers(PROJECT).hidden().build();
    }

    @Test
    public void should_not_authorize_defining_on_qualifiers_and_only_on_qualifiers() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot define both onQualifiers and onlyOnQualifiers");
        PropertyDefinition.builder("foo").name("foo").onQualifiers(MODULE).onlyOnQualifiers(PROJECT).build();
    }

    private static final Set<String> ALLOWED_QUALIFIERS = ImmutableSet.of("TRK", "VW", "BRC", "SVW");

    private static final Set<String> NOT_ALLOWED_QUALIFIERS = ImmutableSet.of("FIL", "DIR", "UTS", "", randomAlphabetic(3));

    @Test
    public void onQualifiers_with_varargs_parameter_fails_with_IAE_when_qualifier_is_not_supported() {
        PropertyDefinitionTest.failsWithIAEForUnsupportedQualifiers(( builder, qualifier) -> builder.onQualifiers(qualifier));
        PropertyDefinitionTest.failsWithIAEForUnsupportedQualifiers(( builder, qualifier) -> builder.onQualifiers("TRK", qualifier, "BRC"));
    }

    @Test
    public void onQualifiers_with_list_parameter_fails_with_IAE_when_qualifier_is_not_supported() {
        PropertyDefinitionTest.failsWithIAEForUnsupportedQualifiers(( builder, qualifier) -> builder.onQualifiers(Collections.singletonList(qualifier)));
        PropertyDefinitionTest.failsWithIAEForUnsupportedQualifiers(( builder, qualifier) -> builder.onQualifiers(Arrays.asList("TRK", qualifier, "BRC")));
    }

    @Test
    public void onlyOnQualifiers_with_varargs_parameter_fails_with_IAE_when_qualifier_is_not_supported() {
        PropertyDefinitionTest.failsWithIAEForUnsupportedQualifiers(( builder, qualifier) -> builder.onlyOnQualifiers(qualifier));
        PropertyDefinitionTest.failsWithIAEForUnsupportedQualifiers(( builder, qualifier) -> builder.onlyOnQualifiers("TRK", qualifier, "BRC"));
    }

    @Test
    public void onlyOnQualifiers_with_list_parameter_fails_with_IAE_when_qualifier_is_not_supported() {
        PropertyDefinitionTest.failsWithIAEForUnsupportedQualifiers(( builder, qualifier) -> builder.onlyOnQualifiers(Collections.singletonList(qualifier)));
        PropertyDefinitionTest.failsWithIAEForUnsupportedQualifiers(( builder, qualifier) -> builder.onlyOnQualifiers(Arrays.asList("TRK", qualifier, "BRC")));
    }

    @Test
    public void onQualifiers_with_varargs_parameter_fails_with_NPE_when_qualifier_is_null() {
        PropertyDefinitionTest.failsWithNPEForNullQualifiers(( builder) -> builder.onQualifiers(((String) (null))));
        PropertyDefinitionTest.failsWithNPEForNullQualifiers(( builder) -> builder.onQualifiers("TRK", null, "BRC"));
    }

    @Test
    public void onQualifiers_with_list_parameter_fails_with_NPE_when_qualifier_is_null() {
        PropertyDefinitionTest.failsWithNPEForNullQualifiers(( builder) -> builder.onQualifiers(Collections.singletonList(null)));
        PropertyDefinitionTest.failsWithNPEForNullQualifiers(( builder) -> builder.onlyOnQualifiers("TRK", null, "BRC"));
    }

    @Test
    public void onlyOnQualifiers_with_varargs_parameter_fails_with_NPE_when_qualifier_is_null() {
        PropertyDefinitionTest.failsWithNPEForNullQualifiers(( builder) -> builder.onlyOnQualifiers(((String) (null))));
        PropertyDefinitionTest.failsWithNPEForNullQualifiers(( builder) -> builder.onlyOnQualifiers("TRK", null, "BRC"));
    }

    @Test
    public void onlyOnQualifiers_with_list_parameter_fails_with_NPE_when_qualifier_is_null() {
        PropertyDefinitionTest.failsWithNPEForNullQualifiers(( builder) -> builder.onlyOnQualifiers(Collections.singletonList(null)));
        PropertyDefinitionTest.failsWithNPEForNullQualifiers(( builder) -> builder.onlyOnQualifiers(Arrays.asList("TRK", null, "BRC")));
    }

    @Test
    public void onQualifiers_with_varargs_parameter_accepts_supported_qualifiers() {
        PropertyDefinitionTest.acceptsSupportedQualifiers(( builder, qualifier) -> builder.onQualifiers(qualifier));
    }

    @Test
    public void onQualifiers_with_list_parameter_accepts_supported_qualifiers() {
        PropertyDefinitionTest.acceptsSupportedQualifiers(( builder, qualifier) -> builder.onQualifiers(Collections.singletonList(qualifier)));
    }

    @Test
    public void onlyOnQualifiers_with_varargs_parameter_accepts_supported_qualifiers() {
        PropertyDefinitionTest.acceptsSupportedQualifiers(( builder, qualifier) -> builder.onlyOnQualifiers(qualifier));
    }

    @Test
    public void onlyOnQualifiers_with_list_parameter_accepts_supported_qualifiers() {
        PropertyDefinitionTest.acceptsSupportedQualifiers(( builder, qualifier) -> builder.onlyOnQualifiers(Collections.singletonList(qualifier)));
    }

    @Properties(@Property(key = "hello", name = "Hello", defaultValue = "world", description = "desc", options = { "de", "en" }, category = "categ", type = PropertyType.FLOAT, global = false, project = true, module = true, multiValues = true, propertySetKey = "set"))
    static class Init {}

    @Properties(@Property(key = "hello", name = "Hello", fields = { @PropertyField(key = "first", name = "First", description = "Description", options = { "A", "B" }), @PropertyField(key = "second", name = "Second", type = PropertyType.INTEGER, indicativeSize = 5) }))
    static class WithPropertySet {}

    @Properties(@Property(key = "hello", name = "Hello"))
    static class DefaultValues {}
}

