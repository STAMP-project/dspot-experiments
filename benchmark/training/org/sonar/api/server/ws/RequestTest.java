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
package org.sonar.api.server.ws;


import Request.Part;
import Request.StringParam;
import RuleStatus.BETA;
import RuleStatus.READY;
import WebService.NewParam;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sonar.api.rule.RuleStatus;
import org.sonar.api.server.ws.internal.PartImpl;
import org.sonar.api.server.ws.internal.ValidatingRequest;
import org.sonar.api.utils.DateUtils;


@RunWith(DataProviderRunner.class)
public class RequestTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private RequestTest.FakeRequest underTest = new RequestTest.FakeRequest();

    @Test
    public void has_param() {
        underTest.setParam("a_required_string", "foo");
        assertThat(underTest.hasParam("a_required_string")).isTrue();
        assertThat(underTest.hasParam("unknown")).isFalse();
    }

    @Test
    public void required_param_is_missing() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The 'required_param' parameter is missing");
        mandatoryParam("required_param");
    }

    @Test
    public void required_param() {
        underTest.setParam("a_required_string", "foo");
        underTest.setParam("a_required_number", "42");
        underTest.setParam("a_required_boolean", "true");
        underTest.setParam("a_required_enum", "BETA");
        assertThat(mandatoryParam("a_required_string")).isEqualTo("foo");
        assertThat(mandatoryParamAsBoolean("a_required_boolean")).isTrue();
        assertThat(mandatoryParamAsInt("a_required_number")).isEqualTo(42);
        assertThat(mandatoryParamAsLong("a_required_number")).isEqualTo(42L);
        assertThat(mandatoryParamAsEnum("a_required_enum", RuleStatus.class)).isEqualTo(BETA);
    }

    @Test
    public void maximum_length_ok() {
        String parameter = "maximum_length_param";
        defineParameterTestAction(( newParam) -> newParam.setMaximumLength(10), parameter);
        String value = Strings.repeat("X", 10);
        String param = param(parameter);
        assertThat(param).isEqualTo(value);
    }

    @Test
    public void maximum_length_not_ok() {
        String parameter = "maximum_length_param";
        defineParameterTestAction(( newParam) -> newParam.setMaximumLength(10), parameter);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(String.format("'%s' length (11) is longer than the maximum authorized (10)", parameter));
        underTest.setParam(parameter, Strings.repeat("X", 11)).param(parameter);
    }

    @Test
    public void maximum_value_ok() {
        String param = "maximum_value_param";
        defineParameterTestAction(( newParam) -> newParam.setMaximumValue(10), param);
        String expected = "10";
        String actual = param(param);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void maximum_value_not_ok() {
        String param = "maximum_value_param";
        defineParameterTestAction(( newParam) -> newParam.setMaximumValue(10), param);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(String.format("'%s' value (11) must be less than 10", param));
        underTest.setParam(param, "11").param(param);
    }

    @Test
    public void paramAsInt_throws_IAE_if_maximum_defined_and_value_not_a_number() {
        String param = "maximum_value_param";
        defineParameterTestAction(( newParam) -> newParam.setMaximumValue(10), param);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'maximum_value_param' value 'foo' cannot be parsed as an integer");
        underTest.setParam(param, "foo").paramAsInt(param);
    }

    @Test
    public void required_param_as_strings() {
        underTest.setParam("a_required_string", "foo,bar");
        assertThat(mandatoryParamAsStrings("a_required_string")).containsExactly("foo", "bar");
    }

    @Test
    public void fail_if_no_required_param_as_strings() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The 'a_required_string' parameter is missing");
        mandatoryParamAsStrings("a_required_string");
    }

    @Test
    public void multi_param() {
        assertThat(multiParam("a_required_multi_param")).isEmpty();
        underTest.setMultiParam("a_required_multi_param", Lists.newArrayList("firstValue", "secondValue", "thirdValue"));
        assertThat(multiParam("a_required_multi_param")).containsExactly("firstValue", "secondValue", "thirdValue");
    }

    @Test
    public void fail_when_multi_param_has_more_values_than_maximum_values() {
        underTest.setMultiParam("has_maximum_values", Lists.newArrayList("firstValue", "secondValue", "thirdValue"));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'has_maximum_values' can contains only 2 values, got 3");
        multiParam("has_maximum_values");
    }

    @Test
    public void mandatory_multi_param() {
        underTest.setMultiParam("a_required_multi_param", Lists.newArrayList("firstValue", "secondValue", "thirdValue"));
        List<String> result = mandatoryMultiParam("a_required_multi_param");
        assertThat(result).containsExactly("firstValue", "secondValue", "thirdValue");
    }

    @Test
    public void fail_when_no_multi_param() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The 'a_required_multi_param' parameter is missing");
        underTest.mandatoryMultiParam("a_required_multi_param");
    }

    @Test
    public void default_value_of_optional_param() {
        assertThat(underTest.param("has_default_string")).isEqualTo("the_default_string");
    }

    @Test
    public void param_as_string() {
        assertThat(underTest.setParam("a_string", "foo").param("a_string")).isEqualTo("foo");
        assertThat(underTest.setParam("a_string", " f o o \r\n ").param("a_string")).isEqualTo("f o o");
    }

    @Test
    public void null_param() {
        assertThat(underTest.param("a_string")).isNull();
        assertThat(paramAsBoolean("a_boolean")).isNull();
        assertThat(underTest.paramAsInt("a_number")).isNull();
        assertThat(underTest.paramAsLong("a_number")).isNull();
    }

    @Test
    public void paramAsInt() {
        assertThat(underTest.setParam("a_number", "123").paramAsInt("a_number")).isEqualTo(123);
        assertThat(underTest.setParam("a_number", "123").paramAsInt("a_number", 42)).isEqualTo(123);
        assertThat(underTest.setParam("a_number", null).paramAsInt("a_number", 42)).isEqualTo(123);
    }

    @Test
    public void fail_when_param_is_not_an_int() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The 'a_number' parameter cannot be parsed as an integer value: not-an-int");
        assertThat(underTest.setParam("a_number", "not-an-int").paramAsInt("a_number")).isEqualTo(123);
    }

    @Test
    public void fail_when_param_is_not_an_int_with_default_value() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The 'a_number' parameter cannot be parsed as an integer value: not_an_int");
        underTest.setParam("a_number", "not_an_int").paramAsInt("a_number", 42);
    }

    @Test
    public void param_as_long() {
        assertThat(underTest.setParam("a_number", "123").paramAsLong("a_number")).isEqualTo(123L);
        assertThat(paramAsLong("a_number", 42L)).isEqualTo(123L);
        assertThat(paramAsLong("a_number", 42L)).isEqualTo(123L);
    }

    @Test
    public void fail_when_param_is_not_a_long() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The 'a_number' parameter cannot be parsed as a long value: not_a_long");
        underTest.setParam("a_number", "not_a_long").paramAsLong("a_number");
    }

    @Test
    public void fail_when_param_is_not_a_long_with_default_value() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The 'a_number' parameter cannot be parsed as a long value: not_a_long");
        paramAsLong("a_number", 42L);
    }

    @Test
    public void param_as_boolean() {
        assertThat(paramAsBoolean("a_boolean")).isTrue();
        assertThat(paramAsBoolean("a_boolean")).isTrue();
        assertThat(paramAsBoolean("a_boolean")).isFalse();
        assertThat(paramAsBoolean("a_boolean")).isFalse();
    }

    @Test
    public void fail_if_incorrect_param_as_boolean() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Property a_boolean is not a boolean value: oui");
        paramAsBoolean("a_boolean");
    }

    @Test
    public void param_as_enum() {
        assertThat(paramAsEnum("a_enum", RuleStatus.class)).isEqualTo(BETA);
    }

    @Test
    public void param_as_enums() {
        assertThat(paramAsEnums("a_enum", RuleStatus.class)).containsOnly(BETA, READY);
        assertThat(paramAsEnums("a_enum", RuleStatus.class)).isEmpty();
    }

    @Test
    public void param_as_enums_returns_null_when_no_value() {
        assertThat(paramAsEnums("a_enum", RuleStatus.class)).isNull();
    }

    @Test
    public void fail_when_param_as_enums_has_more_values_than_maximum_values() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'has_maximum_values' can contains only 2 values, got 3");
        paramAsEnums("has_maximum_values", RuleStatus.class);
    }

    @Test
    public void param_as_date() {
        assertThat(paramAsDate("a_date")).isEqualTo(DateUtils.parseDate("2014-05-27"));
    }

    @Test
    public void getParam_of_missing_string_parameter() {
        Request.StringParam stringParam = underTest.getParam("a_string");
        assertThat(stringParam.isPresent()).isFalse();
        expectSupplierCanNotBeNullNPE(() -> stringParam.or(null));
        assertThat(stringParam.or(() -> "foo")).isEqualTo("foo");
        expectGetValueFailureWithISE(stringParam::getValue);
        Request.StringParam emptyAsNull = stringParam.emptyAsNull();
        assertThat(emptyAsNull).isSameAs(stringParam);
        assertThat(emptyAsNull.isPresent()).isFalse();
        expectSupplierCanNotBeNullNPE(() -> emptyAsNull.or(null));
        assertThat(emptyAsNull.or(() -> "bar")).isEqualTo("bar");
        expectGetValueFailureWithISE(emptyAsNull::getValue);
    }

    @Test
    public void getParam_of_existing_string_parameter_with_non_empty_value() {
        underTest.setParam("a_string", "sorry");
        Request.StringParam stringParam = underTest.getParam("a_string");
        assertThat(stringParam.isPresent()).isTrue();
        expectSupplierCanNotBeNullNPE(() -> stringParam.or(null));
        assertThat(stringParam.or(() -> "foo")).isEqualTo("sorry");
        assertThat(stringParam.getValue()).isEqualTo("sorry");
        Request.StringParam emptyAsNull = stringParam.emptyAsNull();
        assertThat(emptyAsNull).isSameAs(stringParam);
        assertThat(emptyAsNull.isPresent()).isTrue();
        expectSupplierCanNotBeNullNPE(() -> emptyAsNull.or(null));
        assertThat(emptyAsNull.or(() -> "bar")).isEqualTo("sorry");
        assertThat(emptyAsNull.getValue()).isEqualTo("sorry");
    }

    @Test
    public void getParam_of_existing_string_parameter_with_empty_value() {
        underTest.setParam("a_string", "");
        Request.StringParam stringParam = underTest.getParam("a_string");
        assertThat(stringParam.isPresent()).isTrue();
        expectSupplierCanNotBeNullNPE(() -> stringParam.or(null));
        assertThat(stringParam.or(() -> "foo")).isEqualTo("");
        assertThat(stringParam.getValue()).isEqualTo("");
        Request.StringParam emptyAsNull = stringParam.emptyAsNull();
        assertThat(emptyAsNull).isNotSameAs(stringParam);
        assertThat(emptyAsNull.isPresent()).isTrue();
        expectSupplierCanNotBeNullNPE(() -> emptyAsNull.or(null));
        assertThat(emptyAsNull.or(() -> "bar")).isEqualTo(null);
        assertThat(emptyAsNull.getValue()).isEqualTo(null);
    }

    @Test
    public void getParam_with_validation_of_missing_string_parameter() {
        Request.StringParam stringParam = getParam("a_string", ( str) -> {
            throw new IllegalStateException("validator should not be called");
        });
        assertThat(stringParam.isPresent()).isFalse();
        expectSupplierCanNotBeNullNPE(() -> stringParam.or(null));
        assertThat(stringParam.or(() -> "foo")).isEqualTo("foo");
        expectGetValueFailureWithISE(stringParam::getValue);
        Request.StringParam emptyAsNull = stringParam.emptyAsNull();
        assertThat(emptyAsNull).isSameAs(stringParam);
        assertThat(emptyAsNull.isPresent()).isFalse();
        expectSupplierCanNotBeNullNPE(() -> emptyAsNull.or(null));
        assertThat(emptyAsNull.or(() -> "bar")).isEqualTo("bar");
        expectGetValueFailureWithISE(emptyAsNull::getValue);
    }

    @Test
    public void getParam_with_validation_of_existing_string_parameter_with_non_empty_value() {
        underTest.setParam("a_string", "sorry");
        AtomicInteger calls = new AtomicInteger();
        Request.StringParam stringParam = getParam("a_string", ( str) -> calls.incrementAndGet());
        assertThat(calls.get()).isEqualTo(1);
        assertThat(stringParam.isPresent()).isTrue();
        expectSupplierCanNotBeNullNPE(() -> stringParam.or(null));
        assertThat(stringParam.or(() -> "foo")).isEqualTo("sorry");
        assertThat(stringParam.getValue()).isEqualTo("sorry");
        Request.StringParam emptyAsNull = stringParam.emptyAsNull();
        assertThat(emptyAsNull).isSameAs(stringParam);
        assertThat(emptyAsNull.isPresent()).isTrue();
        expectSupplierCanNotBeNullNPE(() -> emptyAsNull.or(null));
        assertThat(emptyAsNull.or(() -> "bar")).isEqualTo("sorry");
        assertThat(emptyAsNull.getValue()).isEqualTo("sorry");
    }

    @Test
    public void getParam_with_validation_of_existing_string_parameter_with_empty_value() {
        underTest.setParam("a_string", "");
        AtomicInteger calls = new AtomicInteger();
        Request.StringParam stringParam = getParam("a_string", ( str) -> calls.incrementAndGet());
        assertThat(calls.get()).isEqualTo(1);
        assertThat(stringParam.isPresent()).isTrue();
        expectSupplierCanNotBeNullNPE(() -> stringParam.or(null));
        assertThat(stringParam.or(() -> "foo")).isEqualTo("");
        assertThat(stringParam.getValue()).isEqualTo("");
        Request.StringParam emptyAsNull = stringParam.emptyAsNull();
        assertThat(emptyAsNull).isNotSameAs(stringParam);
        assertThat(emptyAsNull.isPresent()).isTrue();
        expectSupplierCanNotBeNullNPE(() -> emptyAsNull.or(null));
        assertThat(emptyAsNull.or(() -> "bar")).isEqualTo(null);
        assertThat(emptyAsNull.getValue()).isEqualTo(null);
    }

    @Test
    public void getParam_with_validation_of_existing_string_parameter_does_not_catch_unchecked_exception_throws_by_validator() {
        underTest.setParam("a_string", "boo");
        IllegalArgumentException expected = new IllegalArgumentException("Faking validation of parameter value failed");
        try {
            underTest.getParam("a_string", ( str) -> {
                throw expected;
            });
            fail("an IllegalStateException should have been raised");
        } catch (IllegalArgumentException e) {
            assertThat(e).isSameAs(expected);
        }
    }

    @Test
    public void getParam_of_missing_parameter_of_unspecified_type() {
        Request.Param<Object> param = underTest.getParam("a_string", ( rqt, key) -> {
            throw new IllegalStateException("retrieveAndValidate BiConsumer should not be called");
        });
        assertThat(param.isPresent()).isFalse();
        expectSupplierCanNotBeNullNPE(() -> param.or(null));
        assertThat(param.or(() -> "foo")).isEqualTo("foo");
        expectGetValueFailureWithISE(param::getValue);
    }

    @Test
    public void getParam_of_existing_parameter_of_unspecified_type_with_null_value() {
        underTest.setParam("a_string", "value in fake request actually does not matter");
        Request.Param<Object> param = underTest.getParam("a_string", ( rqt, key) -> null);
        assertThat(param.isPresent()).isTrue();
        expectSupplierCanNotBeNullNPE(() -> param.or(null));
        assertThat(param.or(() -> "foo")).isNull();
        assertThat(param.getValue()).isNull();
    }

    @Test
    public void getParam_of_existing_parameter_of_unspecified_type_with_empty_string() {
        underTest.setParam("a_string", "value in fake request actually does not matter");
        Request.Param<Object> param = underTest.getParam("a_string", ( rqt, key) -> "");
        assertThat(param.isPresent()).isTrue();
        expectSupplierCanNotBeNullNPE(() -> param.or(null));
        assertThat(param.or(() -> "foo")).isEqualTo("");
        assertThat(param.getValue()).isEqualTo("");
    }

    @Test
    public void getParam_of_existing_parameter_of_unspecified_type_with_object() {
        underTest.setParam("a_string", "value in fake request actually does not matter");
        Object value = new Object();
        Request.Param<Object> param = underTest.getParam("a_string", ( rqt, key) -> value);
        assertThat(param.isPresent()).isTrue();
        expectSupplierCanNotBeNullNPE(() -> param.or(null));
        assertThat(param.or(() -> "foo")).isSameAs(value);
        assertThat(param.getValue()).isSameAs(value);
    }

    @Test
    public void getParam_of_existing_parameter_of_unspecified_type_does_not_catch_unchecked_exception_thrown_by_BiConsumer() {
        underTest.setParam("a_string", "value in fake request actually does not matter");
        RuntimeException expected = new RuntimeException("Faking BiConsumer throwing unchecked exception");
        try {
            underTest.getParam("a_string", ( rqt, key) -> {
                throw expected;
            });
            fail("an RuntimeException should have been raised");
        } catch (RuntimeException e) {
            assertThat(e).isSameAs(expected);
        }
    }

    @Test
    public void fail_when_param_as_date_not_a_date() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The date 'polop' does not respect format 'yyyy-MM-dd'");
        paramAsDate("a_date");
    }

    @Test
    public void fail_when_param_as_datetime_not_a_datetime() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'polop' cannot be parsed as either a date or date+time");
        paramAsDateTime("a_datetime");
    }

    @Test
    public void param_as_strings() {
        assertThat(paramAsStrings("a_string")).isNull();
        assertThat(paramAsStrings("a_string")).isEmpty();
        assertThat(paramAsStrings("a_string")).containsExactly("bar");
        assertThat(paramAsStrings("a_string")).containsExactly("bar", "baz");
        assertThat(paramAsStrings("a_string")).containsExactly("bar", "baz");
    }

    @Test
    public void fail_when_param_as_strings_has_more_values_than_maximum_values() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'has_maximum_values' can contains only 2 values, got 3");
        paramAsStrings("has_maximum_values");
    }

    @Test
    public void deprecated_key() {
        assertThat(underTest.setParam("deprecated_param", "bar").param("new_param")).isEqualTo("bar");
    }

    @Test
    public void fail_if_param_is_not_defined() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("BUG - parameter 'unknown' is undefined for action 'my_action'");
        underTest.param("unknown");
    }

    @Test
    public void fail_if_multi_param_is_not_defined() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("BUG - parameter 'unknown' is undefined for action 'my_action'");
        multiParam("unknown");
    }

    @Test
    public void verify_possible_values() {
        underTest.setParam("has_possible_values", "foo");
        assertThat(underTest.param("has_possible_values")).isEqualTo("foo");
    }

    @Test
    public void fail_if_not_a_possible_value() {
        underTest.setParam("has_possible_values", "not_possible");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Value of parameter 'has_possible_values' (not_possible) must be one of: [foo, bar]");
        underTest.param("has_possible_values");
    }

    @Test
    public void param_as_input_stream() throws Exception {
        assertThat(paramAsInputStream("a_string")).isNull();
        assertThat(IOUtils.toString(paramAsInputStream("a_string"))).isEmpty();
        assertThat(IOUtils.toString(paramAsInputStream("a_string"))).isEqualTo("foo");
    }

    @Test
    public void param_as_part() {
        InputStream inputStream = Mockito.mock(InputStream.class);
        underTest.setPart("key", inputStream, "filename");
        Request.Part part = paramAsPart("key");
        assertThat(part.getInputStream()).isEqualTo(inputStream);
        assertThat(part.getFileName()).isEqualTo("filename");
        assertThat(underTest.paramAsPart("unknown")).isNull();
    }

    @Test
    public void mandatory_param_as_part() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The 'required_param' parameter is missing");
        mandatoryParamAsPart("required_param");
    }

    private static class FakeRequest extends ValidatingRequest {
        private final ListMultimap<String, String> multiParams = ArrayListMultimap.create();

        private final Map<String, String> params = new HashMap<>();

        private final Map<String, Part> parts = new HashMap<>();

        private final Map<String, String> headers = new HashMap<>();

        @Override
        public String method() {
            return "GET";
        }

        @Override
        public String getMediaType() {
            return "application/json";
        }

        @Override
        public boolean hasParam(String key) {
            return params.keySet().contains(key);
        }

        @Override
        public String getPath() {
            return null;
        }

        public RequestTest.FakeRequest setParam(String key, @Nullable
        String value) {
            if (value != null) {
                params.put(key, value);
            }
            return this;
        }

        public RequestTest.FakeRequest setMultiParam(String key, List<String> values) {
            multiParams.putAll(key, values);
            return this;
        }

        @Override
        protected String readParam(String key) {
            return params.get(key);
        }

        @Override
        public Map<String, String[]> getParams() {
            ArrayListMultimap<String, String> result = ArrayListMultimap.create(multiParams);
            params.forEach(result::put);
            return result.asMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, ( e) -> e.getValue().toArray(new String[0])));
        }

        @Override
        protected List<String> readMultiParam(String key) {
            return multiParams.get(key);
        }

        @Override
        protected InputStream readInputStreamParam(String key) {
            String param = readParam(key);
            return param == null ? null : IOUtils.toInputStream(param);
        }

        @Override
        protected Part readPart(String key) {
            return parts.get(key);
        }

        public RequestTest.FakeRequest setPart(String key, InputStream input, String fileName) {
            parts.put(key, new PartImpl(input, fileName));
            return this;
        }

        @Override
        public Optional<String> header(String name) {
            return Optional.ofNullable(headers.get(name));
        }

        public RequestTest.FakeRequest setHeader(String name, String value) {
            headers.put(name, value);
            return this;
        }
    }

    private static class FakeWs implements WebService {
        @Override
        public void define(Context context) {
            NewController controller = context.createController("my_controller");
            NewAction action = controller.createAction("my_action").setDescription("Action Description").setPost(true).setSince("5.2").setHandler(Mockito.mock(RequestHandler.class));
            action.createParam("required_param").setRequired(true);
            action.createParam("a_string");
            action.createParam("a_boolean");
            action.createParam("a_number");
            action.createParam("a_enum");
            action.createParam("a_date");
            action.createParam("a_datetime");
            action.createParam("a_required_string").setRequired(true);
            action.createParam("a_required_boolean").setRequired(true);
            action.createParam("a_required_number").setRequired(true);
            action.createParam("a_required_enum").setRequired(true);
            action.createParam("a_required_multi_param").setRequired(true);
            action.createParam("has_default_string").setDefaultValue("the_default_string");
            action.createParam("has_default_number").setDefaultValue("10");
            action.createParam("has_default_boolean").setDefaultValue("true");
            action.createParam("has_possible_values").setPossibleValues("foo", "bar");
            action.createParam("has_maximum_values").setMaxValuesAllowed(2);
            action.createParam("new_param").setDeprecatedKey("deprecated_param", "6.3");
            action.createParam("new_param_with_default_value").setDeprecatedKey("deprecated_new_param_with_default_value", "6.2").setDefaultValue("the_default_string");
            controller.done();
        }
    }
}

