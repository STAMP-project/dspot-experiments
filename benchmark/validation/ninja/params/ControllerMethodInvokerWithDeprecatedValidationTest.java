/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.params;


import NinjaConstant.NINJA_STRICT_ARGUMENT_EXTRACTORS;
import com.google.inject.Inject;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import ninja.Context;
import ninja.Result;
import ninja.RoutingException;
import ninja.exceptions.BadRequestException;
import ninja.i18n.Lang;
import ninja.session.FlashScope;
import ninja.session.Session;
import ninja.utils.NinjaProperties;
import org.hamcrest.Matchers;
import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * ControllerMethodInvokerTest.
 */
@RunWith(MockitoJUnitRunner.class)
public class ControllerMethodInvokerWithDeprecatedValidationTest {
    @Mock
    private ControllerMethodInvokerWithDeprecatedValidationTest.MockController mockController;

    @Mock
    private Context context;

    @Mock
    private Session session;

    @Mock
    private FlashScope flash;

    private NinjaProperties ninjaProperties;

    private Lang lang;

    private Validation validation;

    @Test
    public void noParameterMethodShouldBeInvoked() throws Exception {
        create("noParameter").invoke(mockController, context);
        Mockito.verify(mockController).noParameter();
    }

    @Test
    public void contextShouldBePassed() throws Exception {
        create("context").invoke(mockController, context);
        Mockito.verify(mockController).context(context);
    }

    @Test
    public void sessionShouldBePassed() throws Exception {
        create("session").invoke(mockController, context);
        Mockito.verify(mockController).session(session);
    }

    @Test
    public void flashArgumentShouldBePassed() throws Exception {
        create("flash").invoke(mockController, context);
        Mockito.verify(mockController).flash(flash);
    }

    @Test
    public void paramAnnotatedArgumentShouldBePassed() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("value");
        create("param").invoke(mockController, context);
        Mockito.verify(mockController).param("value");
    }

    @Test
    public void pathParamAnnotatedArgumentShouldBePassed() throws Exception {
        Mockito.when(context.getPathParameter("param1")).thenReturn("value");
        create("pathParam").invoke(mockController, context);
        Mockito.verify(mockController).pathParam("value");
    }

    @Test
    public void sessionParamAnnotatedArgumentShouldBePassed() throws Exception {
        Mockito.when(session.get("param1")).thenReturn("value");
        create("sessionParam").invoke(mockController, context);
        Mockito.verify(mockController).sessionParam("value");
    }

    @Test
    public void attributeAnnotatedArgumentShouldBePassed() throws Exception {
        ControllerMethodInvokerWithDeprecatedValidationTest.Dep dep = new ControllerMethodInvokerWithDeprecatedValidationTest.Dep("dep");
        Mockito.when(context.getAttribute("param1", ControllerMethodInvokerWithDeprecatedValidationTest.Dep.class)).thenReturn(dep);
        create("attribute").invoke(mockController, context);
        Mockito.verify(mockController).attribute(dep);
    }

    @Test
    public void headerAnnotatedArgumentShouldBePassed() throws Exception {
        Mockito.when(context.getHeader("param1")).thenReturn("value");
        create("header").invoke(mockController, context);
        Mockito.verify(mockController).header("value");
    }

    @Test
    public void headerAnnotatedArgumentShouldHandleNull() throws Exception {
        Mockito.when(context.getHeader("param1")).thenReturn(null);
        create("header").invoke(mockController, context);
        Mockito.verify(mockController).header(null);
    }

    @Test
    public void headersAnnotatedArgumentShouldReturnNull() throws Exception {
        Mockito.when(context.getHeaders("param1")).thenReturn(new ArrayList<String>());
        create("headers").invoke(mockController, context);
        Mockito.verify(mockController).headers(null);
    }

    @Test
    public void headersAnnotatedArgumentShouldBePassed() throws Exception {
        Mockito.when(context.getHeaders("param1")).thenReturn(Arrays.asList("a", "b", "c"));
        create("headers").invoke(mockController, context);
        Mockito.verify(mockController).headers(new String[]{ "a", "b", "c" });
    }

    @Test
    public void headersAnnotatedArgumentShouldHandleNull() throws Exception {
        Mockito.when(context.getHeader("param1")).thenReturn(null);
        create("headers").invoke(mockController, context);
        Mockito.verify(mockController).headers(null);
    }

    @Test
    public void integerParamShouldBeParsedToInteger() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("20");
        create("integerParam").invoke(mockController, context);
        Mockito.verify(mockController).integerParam(20);
    }

    @Test
    public void integerParamShouldHandleNull() throws Exception {
        create("integerParam").invoke(mockController, context);
        Mockito.verify(mockController).integerParam(null);
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void integerValidationShouldWork() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("blah");
        create("integerParam").invoke(mockController, context);
        Mockito.verify(mockController).integerParam(null);
        Assert.assertTrue(validation.hasFieldViolation("param1"));
    }

    @Test
    public void intParamShouldBeParsedToInteger() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("20");
        create("intParam").invoke(mockController, context);
        Mockito.verify(mockController).intParam(20);
    }

    @Test
    public void intParamShouldHandleNull() throws Exception {
        create("intParam").invoke(mockController, context);
        Mockito.verify(mockController).intParam(0);
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void intValidationShouldWork() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("blah");
        create("intParam").invoke(mockController, context);
        Mockito.verify(mockController).intParam(0);
        Assert.assertTrue(validation.hasFieldViolation("param1"));
    }

    @Test
    public void shortParamShouldBeParsedToShort() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("20");
        create("shortParam").invoke(mockController, context);
        Mockito.verify(mockController).shortParam(((short) (20)));
    }

    @Test
    public void shortParamShouldHandleNull() throws Exception {
        create("shortParam").invoke(mockController, context);
        Mockito.verify(mockController).shortParam(null);
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void shortValidationShouldWork() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("blah");
        create("shortParam").invoke(mockController, context);
        Mockito.verify(mockController).shortParam(null);
        Assert.assertTrue(validation.hasFieldViolation("param1"));
    }

    @Test
    public void primShortParamShouldBeParsedToShort() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("20");
        create("primShortParam").invoke(mockController, context);
        Mockito.verify(mockController).primShortParam(((short) (20)));
    }

    @Test
    public void primShortParamShouldHandleNull() throws Exception {
        create("primShortParam").invoke(mockController, context);
        Mockito.verify(mockController).primShortParam(((short) (0)));
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void primShortValidationShouldWork() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("blah");
        create("primShortParam").invoke(mockController, context);
        Mockito.verify(mockController).primShortParam(((short) (0)));
        Assert.assertTrue(validation.hasFieldViolation("param1"));
    }

    @Test
    public void characterParamShouldBeParsedToCharacter() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("ABC");
        create("characterParam").invoke(mockController, context);
        Mockito.verify(mockController).characterParam('A');
    }

    @Test
    public void characterParamShouldHandleNull() throws Exception {
        create("characterParam").invoke(mockController, context);
        Mockito.verify(mockController).characterParam(null);
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void charParamShouldBeParsedToCharacter() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("ABC");
        create("charParam").invoke(mockController, context);
        Mockito.verify(mockController).charParam('A');
    }

    @Test
    public void charParamShouldHandleNull() throws Exception {
        create("charParam").invoke(mockController, context);
        Mockito.verify(mockController).charParam('\u0000');
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void byteParamShouldBeParsedToByte() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("20");
        create("byteParam").invoke(mockController, context);
        Mockito.verify(mockController).byteParam(((byte) (20)));
    }

    @Test
    public void byteParamShouldHandleNull() throws Exception {
        create("byteParam").invoke(mockController, context);
        Mockito.verify(mockController).byteParam(null);
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void byteValidationShouldWork() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("blah");
        create("byteParam").invoke(mockController, context);
        Mockito.verify(mockController).byteParam(null);
        Assert.assertTrue(validation.hasFieldViolation("param1"));
    }

    @Test
    public void primByteParamShouldBeParsedToByte() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("20");
        create("primByteParam").invoke(mockController, context);
        Mockito.verify(mockController).primByteParam(((byte) (20)));
    }

    @Test
    public void primByteParamShouldHandleNull() throws Exception {
        create("primByteParam").invoke(mockController, context);
        Mockito.verify(mockController).primByteParam(((byte) (0)));
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void primByteValidationShouldWork() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("blah");
        create("primByteParam").invoke(mockController, context);
        Mockito.verify(mockController).primByteParam(((byte) (0)));
        Assert.assertTrue(validation.hasFieldViolation("param1"));
    }

    @Test
    public void booleanParamShouldBeParsedToBoolean() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("true");
        create("booleanParam").invoke(mockController, context);
        Mockito.verify(mockController).booleanParam(true);
    }

    @Test
    public void booleanParamShouldHandleNull() throws Exception {
        create("booleanParam").invoke(mockController, context);
        Mockito.verify(mockController).booleanParam(null);
        Assert.assertFalse(validation.hasViolations());
    }

    @Test(expected = BadRequestException.class)
    public void booleanParamShouldHandleNullInStrictMode() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn(null);
        Mockito.when(ninjaProperties.getBooleanWithDefault(NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        create("booleanParam").invoke(mockController, context);
    }

    @Test(expected = BadRequestException.class)
    public void booleanParamShouldHandleWrongInputForBooleanInStrictMode() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("test");
        Mockito.when(ninjaProperties.getBooleanWithDefault(NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        create("booleanParam").invoke(mockController, context);
    }

    @Test
    public void primBooleanParamShouldBeParsedToBoolean() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("true");
        create("primBooleanParam").invoke(mockController, context);
        Mockito.verify(mockController).primBooleanParam(true);
    }

    @Test
    public void primBooleanParamShouldHandleNull() throws Exception {
        create("primBooleanParam").invoke(mockController, context);
        Mockito.verify(mockController).primBooleanParam(false);
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void booleanParamWithOptionalShouldHandleWrongInputForBooleanInStrictMode() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("test");
        Mockito.when(ninjaProperties.getBooleanWithDefault(NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        create("booleanParamWithOptional").invoke(mockController, context);
        Mockito.verify(mockController).booleanParamWithOptional(Optional.empty());
    }

    @Test
    public void booleanParamWithOptionalShouldHandleTrueInStrictMode() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("true");
        Mockito.when(ninjaProperties.getBooleanWithDefault(NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        create("booleanParamWithOptional").invoke(mockController, context);
        Mockito.verify(mockController).booleanParamWithOptional(Optional.of(Boolean.TRUE));
    }

    @Test
    public void booleanParamWithOptionalShouldHandleFalseInStrictMode() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("false");
        Mockito.when(ninjaProperties.getBooleanWithDefault(NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        create("booleanParamWithOptional").invoke(mockController, context);
        Mockito.verify(mockController).booleanParamWithOptional(Optional.of(Boolean.FALSE));
    }

    @Test
    public void longParamShouldBeParsedToLong() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("20");
        create("longParam").invoke(mockController, context);
        Mockito.verify(mockController).longParam(20L);
    }

    @Test
    public void longParamShouldHandleNull() throws Exception {
        create("longParam").invoke(mockController, context);
        Mockito.verify(mockController).longParam(null);
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void longValidationShouldWork() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("blah");
        create("longParam").invoke(mockController, context);
        Mockito.verify(mockController).longParam(null);
        Assert.assertTrue(validation.hasFieldViolation("param1"));
    }

    @Test
    public void primLongParamShouldBeParsedToLong() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("20");
        create("primLongParam").invoke(mockController, context);
        Mockito.verify(mockController).primLongParam(20);
    }

    @Test
    public void primLongParamShouldHandleNull() throws Exception {
        create("primLongParam").invoke(mockController, context);
        Mockito.verify(mockController).primLongParam(0);
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void primLongValidationShouldWork() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("blah");
        create("primLongParam").invoke(mockController, context);
        Mockito.verify(mockController).primLongParam(0L);
        Assert.assertTrue(validation.hasFieldViolation("param1"));
    }

    @Test
    public void floatParamShouldBeParsedToFloat() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("3.14");
        create("floatParam").invoke(mockController, context);
        Mockito.verify(mockController).floatParam(3.14F);
    }

    @Test
    public void floatParamShouldHandleNull() throws Exception {
        create("floatParam").invoke(mockController, context);
        Mockito.verify(mockController).floatParam(null);
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void floatValidationShouldWork() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("blah");
        create("floatParam").invoke(mockController, context);
        Mockito.verify(mockController).floatParam(null);
        Assert.assertTrue(validation.hasFieldViolation("param1"));
    }

    @Test
    public void primFloatParamShouldBeParsedToFloat() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("3.14");
        create("primFloatParam").invoke(mockController, context);
        Mockito.verify(mockController).primFloatParam(3.14F);
    }

    @Test
    public void primFloatParamShouldHandleNull() throws Exception {
        create("primFloatParam").invoke(mockController, context);
        Mockito.verify(mockController).primFloatParam(0);
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void primFloatValidationShouldWork() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("blah");
        create("primFloatParam").invoke(mockController, context);
        Mockito.verify(mockController).primFloatParam(0);
        Assert.assertTrue(validation.hasFieldViolation("param1"));
    }

    @Test
    public void doubleParamShouldBeParsedToDouble() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("3.14");
        create("doubleParam").invoke(mockController, context);
        Mockito.verify(mockController).doubleParam(3.14);
    }

    @Test
    public void doubleParamShouldHandleNull() throws Exception {
        create("doubleParam").invoke(mockController, context);
        Mockito.verify(mockController).doubleParam(null);
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void doubleValidationShouldWork() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("blah");
        create("doubleParam").invoke(mockController, context);
        Mockito.verify(mockController).doubleParam(null);
        Assert.assertTrue(validation.hasFieldViolation("param1"));
    }

    @Test
    public void primDoubleParamShouldBeParsedToDouble() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("3.14");
        create("primDoubleParam").invoke(mockController, context);
        Mockito.verify(mockController).primDoubleParam(3.14);
    }

    @Test
    public void primDoubleParamShouldHandleNull() throws Exception {
        create("primDoubleParam").invoke(mockController, context);
        Mockito.verify(mockController).primDoubleParam(0);
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void primDoubleValidationShouldWork() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("blah");
        create("primDoubleParam").invoke(mockController, context);
        Mockito.verify(mockController).primDoubleParam(0);
        Assert.assertTrue(validation.hasFieldViolation("param1"));
    }

    @Test
    public void enumParamShouldBeParsedToEnumCaseSensitive() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("Red");
        create("enumParam").invoke(mockController, context);
        Mockito.verify(mockController).enumParam(ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow.Red);
    }

    @Test
    public void enumParamShouldBeParsedToEnumCaseInsensitive() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("red");
        create("enumParam").invoke(mockController, context);
        Mockito.verify(mockController).enumParam(ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow.Red);
    }

    @Test
    public void enumParamShouldHandleNull() throws Exception {
        create("enumParam").invoke(mockController, context);
        Mockito.verify(mockController).enumParam(null);
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void enumParamValidationShouldWork() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("blah");
        create("enumParam").invoke(mockController, context);
        Mockito.verify(mockController).enumParam(null);
        Assert.assertTrue(validation.hasFieldViolation("param1"));
    }

    @Test
    public void enumCsvParamSingleShouldBeParsed() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("Red");
        create("enumCsvParam").invoke(mockController, context);
        Mockito.verify(mockController).enumCsvParam(new ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow[]{ ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow.Red });
    }

    @Test
    public void enumCsvParamMultipleShouldBeParsed() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("Red,Orange,Yellow");
        create("enumCsvParam").invoke(mockController, context);
        Mockito.verify(mockController).enumCsvParam(new ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow[]{ ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow.Red, ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow.Orange, ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow.Yellow });
    }

    @Test
    public void enumCsvParamShouldReturnNull() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("");
        create("enumCsvParam").invoke(mockController, context);
        Mockito.verify(mockController).enumCsvParam(null);
        Assert.assertFalse(validation.hasFieldViolation("param1"));
    }

    @Test
    public void enumCsvParamValidationShouldWork() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("White,Black");
        create("enumCsvParam").invoke(mockController, context);
        Mockito.verify(mockController).enumCsvParam(null);
        Assert.assertTrue(validation.hasFieldViolation("param1"));
    }

    @Test
    public void enumArrayParamSingleShouldBeParsed() throws Exception {
        Mockito.when(context.getParameterValues("param1")).thenReturn(Arrays.asList("Blue"));
        create("enumArrayParam").invoke(mockController, context);
        Mockito.verify(mockController).enumArrayParam(new ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow[]{ ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow.Blue });
    }

    @Test
    public void enumArrayParamMultipleShouldBeParsed() throws Exception {
        Mockito.when(context.getParameterValues("param1")).thenReturn(Arrays.asList("Blue", "Indigo", "Violet"));
        create("enumArrayParam").invoke(mockController, context);
        Mockito.verify(mockController).enumArrayParam(new ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow[]{ ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow.Blue, ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow.Indigo, ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow.Violet });
    }

    @Test
    public void enumArrayParamShouldReturnNull() throws Exception {
        Mockito.when(context.getParameterValues("param1")).thenReturn(new ArrayList<String>());
        create("enumArrayParam").invoke(mockController, context);
        Mockito.verify(mockController).enumArrayParam(null);
        Assert.assertFalse(validation.hasFieldViolation("param1"));
    }

    @Test
    public void enumArrayParamValidationShouldWork() throws Exception {
        Mockito.when(context.getParameterValues("param1")).thenReturn(Arrays.asList("White", "Black"));
        create("enumArrayParam").invoke(mockController, context);
        Mockito.verify(mockController).enumArrayParam(null);
        Assert.assertTrue(validation.hasFieldViolation("param1"));
    }

    @Test
    public void customDateFormatParamShouldBeParsedToDate() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("15/01/2015");
        create("dateParam", ControllerMethodInvokerWithDeprecatedValidationTest.DateParamParser.class).invoke(mockController, context);
        Mockito.verify(mockController).dateParam(new LocalDateTime(2015, 1, 15, 0, 0).toDate());
    }

    @Test
    public void customDateFormatParamWithOptionalShouldBeParsedToDate() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("15/01/2015");
        create("dateParamWithOptional", ControllerMethodInvokerWithDeprecatedValidationTest.DateParamParser.class).invoke(mockController, context);
        Mockito.verify(mockController).dateParamWithOptional(Optional.of(new LocalDateTime(2015, 1, 15, 0, 0).toDate()));
    }

    @Test
    public void customDateFormatParamShouldHandleNull() throws Exception {
        create("dateParam", ControllerMethodInvokerWithDeprecatedValidationTest.DateParamParser.class).invoke(mockController, context);
        Mockito.verify(mockController).dateParam(null);
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void customDateFormatParamWithOptionalShouldHandleEmpty() throws Exception {
        create("dateParamWithOptional", ControllerMethodInvokerWithDeprecatedValidationTest.DateParamParser.class).invoke(mockController, context);
        Mockito.verify(mockController).dateParamWithOptional(Optional.empty());
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void customDateFormatValidationShouldWork() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("blah");
        create("dateParam", ControllerMethodInvokerWithDeprecatedValidationTest.DateParamParser.class).invoke(mockController, context);
        Mockito.verify(mockController).dateParam(null);
        Assert.assertTrue(validation.hasFieldViolation("param1"));
    }

    @Test(expected = RoutingException.class)
    public void needingInjectionParamParserNotBinded() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("hello");
        create("needingInjectionParamParser").invoke(mockController, context);
        Mockito.verify(mockController).needingInjectionParamParser(new ControllerMethodInvokerWithDeprecatedValidationTest.Dep("hello_hello"));
    }

    @Test
    public void needingInjectionParamParser() throws Exception {
        Mockito.when(context.getParameter("param1")).thenReturn("hello");
        create("needingInjectionParamParser", ControllerMethodInvokerWithDeprecatedValidationTest.NeedingInjectionParamParser.class).invoke(mockController, context);
        Mockito.verify(mockController).needingInjectionParamParser(new ControllerMethodInvokerWithDeprecatedValidationTest.Dep("hello_hello"));
    }

    @Test
    public void needingInjectionParamParserArray() throws Exception {
        Mockito.when(context.getParameterValues("param1")).thenReturn(Arrays.asList("hello1", "hello2"));
        create("needingInjectionParamParserArray", ControllerMethodInvokerWithDeprecatedValidationTest.NeedingInjectionParamParser.class).invoke(mockController, context);
        Mockito.verify(mockController).needingInjectionParamParserArray(new ControllerMethodInvokerWithDeprecatedValidationTest.Dep[]{ new ControllerMethodInvokerWithDeprecatedValidationTest.Dep("hello_hello1"), new ControllerMethodInvokerWithDeprecatedValidationTest.Dep("hello_hello2") });
    }

    @Test
    public void customArgumentExtractorWithNoArgsShouldBeInstantiated() {
        create("noArgArgumentExtractor").invoke(mockController, context);
        Mockito.verify(mockController).noArgArgumentExtractor("noargs");
    }

    @Test
    public void customArgumentExtractorWithClassArgShouldBeInstantiated() {
        create("classArgArgumentExtractor").invoke(mockController, context);
        Mockito.verify(mockController).classArgArgumentExtractor("java.lang.String");
    }

    @Test
    public void customArgumentExtractorWithGuiceShouldBeInstantiated() {
        create("guiceArgumentExtractor", new ControllerMethodInvokerWithDeprecatedValidationTest.Dep("dep")).invoke(mockController, context);
        Mockito.verify(mockController).guiceArgumentExtractor("dep:bar:java.lang.String");
    }

    @Test
    public void customArgumentExtractorWithOptionalAndGuiceShouldBeInstantiated() {
        create("guiceArgumentExtractorWithOptional", new ControllerMethodInvokerWithDeprecatedValidationTest.Dep("dep")).invoke(mockController, context);
        Mockito.verify(mockController).guiceArgumentExtractorWithOptional(Optional.of("dep:bar:java.lang.String"));
    }

    @Test
    public void multipleDifferentExtractorsShouldWorkFine() {
        Mockito.when(context.getParameter("param1")).thenReturn("value");
        Mockito.when(context.getPathParameter("param2")).thenReturn("20");
        create("multiple").invoke(mockController, context);
        Mockito.verify(mockController).multiple("value", 20, context, session);
    }

    @Test
    public void validationShouldFailWhenBadRequest() {
        create("required").invoke(mockController, context);
        Mockito.verify(mockController).required(null);
        Assert.assertTrue(validation.hasFieldViolation("param1"));
    }

    @Test
    public void validationShouldPassWhenGoodRequest() {
        Mockito.when(context.getParameter("param1")).thenReturn("value");
        create("required").invoke(mockController, context);
        Mockito.verify(mockController).required("value");
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void optionalSessionParam() {
        Mockito.when(session.get("param1")).thenReturn("value");
        create("optionalSessionParam").invoke(mockController, context);
        Mockito.verify(mockController).optionalSessionParam(Optional.of("value"));
    }

    @Test
    public void optionalSessionParamEmpty() {
        Mockito.when(session.get("param1")).thenReturn(null);
        create("optionalSessionParam").invoke(mockController, context);
        Mockito.verify(mockController).optionalSessionParam(Optional.empty());
    }

    @Test(expected = BadRequestException.class)
    public void sessionParamStrictModeWorks() {
        Mockito.when(ninjaProperties.getBooleanWithDefault(NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        Mockito.when(session.get("param1")).thenReturn(null);
        create("sessionParam").invoke(mockController, context);
    }

    @Test
    public void optionalAttribute() {
        ControllerMethodInvokerWithDeprecatedValidationTest.Dep dep = new ControllerMethodInvokerWithDeprecatedValidationTest.Dep("dep");
        Mockito.when(context.getAttribute("param1", ControllerMethodInvokerWithDeprecatedValidationTest.Dep.class)).thenReturn(dep);
        create("optionalAttribute").invoke(mockController, context);
        Mockito.verify(mockController).optionalAttribute(Optional.of(dep));
    }

    @Test
    public void optionalAttributeEmpty() {
        Mockito.when(context.getAttribute("param1", ControllerMethodInvokerWithDeprecatedValidationTest.Dep.class)).thenReturn(null);
        create("optionalAttribute").invoke(mockController, context);
        Mockito.verify(mockController).optionalAttribute(Optional.empty());
    }

    @Test(expected = BadRequestException.class)
    public void attributeStrictModeWorks() {
        Mockito.when(ninjaProperties.getBooleanWithDefault(NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        Mockito.when(context.getAttribute("param1", ControllerMethodInvokerWithDeprecatedValidationTest.Dep.class)).thenReturn(null);
        create("attribute").invoke(mockController, context);
    }

    @Test
    public void optionalHeader() {
        Mockito.when(context.getHeader("param1")).thenReturn("value");
        create("optionalHeader").invoke(mockController, context);
        Mockito.verify(mockController).optionalHeader(Optional.of("value"));
    }

    @Test
    public void optionalHeaderEmpty() {
        Mockito.when(context.getHeader("param1")).thenReturn(null);
        create("optionalHeader").invoke(mockController, context);
        Mockito.verify(mockController).optionalHeader(Optional.empty());
    }

    @Test(expected = BadRequestException.class)
    public void headerStrictModeWorks() {
        Mockito.when(ninjaProperties.getBooleanWithDefault(NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        Mockito.when(context.getHeader("param1")).thenReturn(null);
        create("header").invoke(mockController, context);
    }

    @Test
    public void optionalParam() {
        Mockito.when(context.getParameter("param1")).thenReturn("value");
        create("optionalParam").invoke(mockController, context);
        Mockito.verify(mockController).optionalParam(Optional.of("value"));
    }

    @Test
    public void optionalParamEmpty() {
        Mockito.when(context.getParameter("param1")).thenReturn(null);
        create("optionalParam").invoke(mockController, context);
        Mockito.verify(mockController).optionalParam(Optional.empty());
    }

    @Test(expected = BadRequestException.class)
    public void paramStrictModeWorks() {
        Mockito.when(ninjaProperties.getBooleanWithDefault(NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        Mockito.when(context.getParameter("param1")).thenReturn(null);
        create("param").invoke(mockController, context);
    }

    @Test
    public void optionalIntegerParam() {
        Mockito.when(context.getParameter("param1")).thenReturn("1");
        create("optionalIntegerParam").invoke(mockController, context);
        Mockito.verify(mockController).optionalIntegerParam(Optional.of(1));
    }

    @Test
    public void optionalIntegerParamEmpty() {
        Mockito.when(context.getParameter("param1")).thenReturn(null);
        create("optionalIntegerParam").invoke(mockController, context);
        Mockito.verify(mockController).optionalIntegerParam(Optional.empty());
    }

    @Test(expected = BadRequestException.class)
    public void integerParamStrictModeWorks() {
        Mockito.when(ninjaProperties.getBooleanWithDefault(NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        Mockito.when(context.getParameter("param1")).thenReturn(null);
        create("integerParam").invoke(mockController, context);
    }

    @Test
    public void optionalLongParam() {
        Mockito.when(context.getParameter("param1")).thenReturn("1");
        create("optionalLongParam").invoke(mockController, context);
        Mockito.verify(mockController).optionalLongParam(Optional.of(1L));
    }

    @Test
    public void optionalLongParamEmpty() {
        Mockito.when(context.getParameter("param1")).thenReturn(null);
        create("optionalLongParam").invoke(mockController, context);
        Mockito.verify(mockController).optionalLongParam(Optional.empty());
    }

    @Test(expected = BadRequestException.class)
    public void longParamEmptyStrictModeWorks() {
        Mockito.when(ninjaProperties.getBooleanWithDefault(NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        Mockito.when(context.getParameter("param1")).thenReturn(null);
        create("longParam").invoke(mockController, context);
    }

    @Test
    public void optionalShortParam() {
        Mockito.when(context.getParameter("param1")).thenReturn("1");
        create("optionalShortParam").invoke(mockController, context);
        Mockito.verify(mockController).optionalShortParam(Optional.of(new Short("1")));
    }

    @Test
    public void optionalShortParamEmpty() {
        Mockito.when(context.getParameter("param1")).thenReturn(null);
        create("optionalShortParam").invoke(mockController, context);
        Mockito.verify(mockController).optionalShortParam(Optional.empty());
    }

    @Test(expected = BadRequestException.class)
    public void shortParamEmptyStrictModeWorks() {
        Mockito.when(ninjaProperties.getBooleanWithDefault(NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        Mockito.when(context.getParameter("param1")).thenReturn(null);
        create("shortParam").invoke(mockController, context);
    }

    @Test
    public void optionalEnumParam() {
        Mockito.when(context.getParameter("param1")).thenReturn("red");
        create("optionalEnumParam").invoke(mockController, context);
        Mockito.verify(mockController).optionalEnumParam(Optional.of(ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow.Red));
    }

    @Test
    public void optionalEnumParamEmpty() {
        Mockito.when(context.getParameter("param1")).thenReturn(null);
        create("optionalEnumParam").invoke(mockController, context);
        Mockito.verify(mockController).optionalEnumParam(Optional.empty());
    }

    @Test(expected = BadRequestException.class)
    public void rainbowParamStrictModeWorks() {
        Mockito.when(ninjaProperties.getBooleanWithDefault(NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        Mockito.when(context.getParameter("param1")).thenReturn(null);
        create("enumParam").invoke(mockController, context);
    }

    @Captor
    ArgumentCaptor<Optional<ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow[]>> argumentCaptor;

    @Test
    public void optionalEnumArrayParam() {
        Mockito.when(context.getParameter("param1")).thenReturn("Red,Orange,Yellow");
        create("optionalEnumArrayParam").invoke(mockController, context);
        Mockito.verify(mockController).optionalEnumArrayParam(argumentCaptor.capture());
        ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow[] rainbows = argumentCaptor.getValue().get();
        Assert.assertThat(rainbows, Matchers.arrayContaining(ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow.Red, ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow.Orange, ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow.Yellow));
    }

    @Test
    public void optionalEnumArrayParamEmpty() {
        Mockito.when(context.getParameter("param1")).thenReturn(null);
        create("optionalEnumArrayParam").invoke(mockController, context);
        Mockito.verify(mockController).optionalEnumArrayParam(Optional.empty());
    }

    @Test(expected = BadRequestException.class)
    public void rainbowArrayParamEmptyStrictModeWorks() {
        Mockito.when(ninjaProperties.getBooleanWithDefault(NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        Mockito.when(context.getParameter("param1")).thenReturn(null);
        create("enumArrayParam").invoke(mockController, context);
    }

    @Test
    public void optionalDateParam() {
        Mockito.when(context.getParameter("param1")).thenReturn("15/01/2015");
        create("optionalDateParam", ControllerMethodInvokerWithDeprecatedValidationTest.DateParamParser.class).invoke(mockController, context);
        Mockito.verify(mockController).optionalDateParam(Optional.of(new LocalDateTime(2015, 1, 15, 0, 0).toDate()));
    }

    @Test
    public void optionalDateParamEmpty() {
        Mockito.when(context.getParameter("param1")).thenReturn(null);
        create("optionalDateParam", ControllerMethodInvokerWithDeprecatedValidationTest.DateParamParser.class).invoke(mockController, context);
        Mockito.verify(mockController).optionalDateParam(Optional.empty());
    }

    @Test(expected = BadRequestException.class)
    public void dateParamStrictModeWorks() {
        Mockito.when(ninjaProperties.getBooleanWithDefault(NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        Mockito.when(context.getParameter("param1")).thenReturn(null);
        create("dateParam", ControllerMethodInvokerWithDeprecatedValidationTest.DateParamParser.class).invoke(mockController, context);
    }

    @Test
    public void optionalBody() {
        Object body = new Object();
        Mockito.when(context.parseBody(Object.class)).thenReturn(body);
        create("optionalBody").invoke(mockController, context);
        Mockito.verify(mockController).optionalBody(Optional.of(body));
    }

    @Test
    public void optionalBodyEmpty() {
        Mockito.when(context.parseBody(Object.class)).thenReturn(null);
        create("optionalBody").invoke(mockController, context);
        Mockito.verify(mockController).optionalBody(Optional.empty());
    }

    @Test(expected = BadRequestException.class)
    public void bodyEmptyStrictModeWorks() {
        Mockito.when(ninjaProperties.getBooleanWithDefault(NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        Mockito.when(context.parseBody(Object.class)).thenReturn(null);
        create("body").invoke(mockController, context);
    }

    @Test
    public void validationShouldBeAppliedInCorrectOrderPreFail() {
        create("requiredInt").invoke(mockController, context);
        Mockito.verify(mockController).requiredInt(0);
        Assert.assertTrue(validation.hasFieldViolation("param1"));
        Assert.assertEquals(1, validation.getFieldViolations("param1").size());
        Assert.assertEquals("validation.required.violation", validation.getFieldViolations("param1").get(0).constraintViolation.getMessageKey());
    }

    @Test
    public void validationWithOptionalShouldBeAppliedInCorrectOrderPreFail() {
        create("requiredIntWithOptional").invoke(mockController, context);
        Mockito.verify(mockController).requiredIntWithOptional(Optional.empty());
        Assert.assertTrue(validation.hasFieldViolation("param1"));
    }

    @Test
    public void validationShouldBeAppliedInCorrectOrderPostFail() {
        Mockito.when(context.getParameter("param1")).thenReturn("5");
        create("requiredInt").invoke(mockController, context);
        Mockito.verify(mockController).requiredInt(5);
        Assert.assertTrue(validation.hasFieldViolation("param1"));
        Assert.assertEquals(1, validation.getFieldViolations("param1").size());
        Assert.assertEquals("validation.number.min.violation", validation.getFieldViolations("param1").get(0).constraintViolation.getMessageKey());
    }

    @Test
    public void validationWithOptionalShouldBeAppliedInCorrectOrderPostFail() {
        Mockito.when(context.getParameter("param1")).thenReturn("5");
        create("requiredIntWithOptional").invoke(mockController, context);
        Mockito.verify(mockController).requiredIntWithOptional(Optional.of(5));
        Assert.assertTrue(validation.hasFieldViolation("param1"));
    }

    @Test
    public void validationShouldBeAppliedInCorrectOrderPass() {
        Mockito.when(context.getParameter("param1")).thenReturn("20");
        create("requiredInt").invoke(mockController, context);
        Mockito.verify(mockController).requiredInt(20);
        Assert.assertFalse(validation.hasViolations());
    }

    @Test
    public void validationWithOptionalShouldBeAppliedInCorrectOrderPass() {
        Mockito.when(context.getParameter("param1")).thenReturn("20");
        create("requiredIntWithOptional").invoke(mockController, context);
        Mockito.verify(mockController).requiredIntWithOptional(Optional.of(20));
        Assert.assertFalse(validation.hasViolations());
    }

    @Test(expected = RoutingException.class)
    public void invalidValidatorShouldBeFlagged() {
        create("badValidator").invoke(mockController, context);
    }

    @Test(expected = RoutingException.class)
    public void invalidValidatorWithOptionalShouldBeFlagged() {
        create("badValidatorWithOptional").invoke(mockController, context);
    }

    @Test(expected = RoutingException.class)
    public void tooManyBodiesShouldBeFlagged() {
        create("tooManyBodies").invoke(mockController, context);
    }

    @Test
    public void bodyShouldBeParsedIntoLeftOverParameter() {
        Object body = new Object();
        Mockito.when(context.parseBody(Object.class)).thenReturn(body);
        create("body").invoke(mockController, context);
        Mockito.verify(mockController).body(body);
    }

    @Test
    public void bodyWithOptionalShouldBeParsedIntoLeftOverParameter() {
        Object body = new Object();
        Mockito.when(context.parseBody(Object.class)).thenReturn(body);
        create("bodyWithOptional").invoke(mockController, context);
        Mockito.verify(mockController).bodyWithOptional(Optional.of(body));
    }

    @Test
    public void bodyWithOptionalShouldBeEmptyIfNoBodyPresent() {
        Mockito.when(context.parseBody(Object.class)).thenReturn(null);
        create("bodyWithOptional").invoke(mockController, context);
        Mockito.verify(mockController).bodyWithOptional(Optional.empty());
    }

    // JSR303Validation(@Pattern(regexp = "[a-z]*") String param1,
    // @Length(min = 5, max = 10) String param2, @Min(3) @Max(10) int param3);
    @Test
    public void validationPassed() {
        validateJSR303(buildDto("regex", "length", 5));
        doCheckValidationPassed(context);
    }

    @Test
    public void validationWithOptionalPassed() {
        validateJSR303WithOptional(buildDto("regex", "length", 5));
        doCheckValidationPassed(context);
    }

    @Test
    public void validationFailedRegex() {
        validateJSR303(buildDto("regex!!!", "length", 5));
        docheckValidationFailedRegex(context);
    }

    @Test
    public void validationWithOptionalFailedRegex() {
        validateJSR303WithOptional(buildDto("regex!!!", "length", 5));
        docheckValidationFailedRegex(context);
    }

    @Test
    public void validationFailedLength() {
        validateJSR303(buildDto("regex", "length - too long", 5));
        doCheckValidationFailedLength(context);
    }

    @Test
    public void validationWithOptionalFailedLength() {
        validateJSR303WithOptional(buildDto("regex", "length - too long", 5));
        doCheckValidationFailedLength(context);
    }

    @Test
    public void validationFailedRange() {
        validateJSR303(buildDto("regex", "length", 25));
        doCheckValidationFailedRange(context);
    }

    @Test
    public void validationWithOptionalFailedRange() {
        validateJSR303WithOptional(buildDto("regex", "length", 25));
        doCheckValidationFailedRange(context);
    }

    @Test
    public void validationFailedTranslationFr() {
        Mockito.when(this.context.getAcceptLanguage()).thenReturn("fr");
        validateJSR303(buildDto("regex", "length - too long", 5));
        doCheckValidationFailedTranslationFr(context);
    }

    @Test
    public void validationWithOptionalFailedTranslationFr() {
        Mockito.when(this.context.getAcceptLanguage()).thenReturn("fr");
        validateJSR303WithOptional(buildDto("regex", "length - too long", 5));
        doCheckValidationFailedTranslationFr(context);
    }

    @Test
    public void validationFailedTranslationEn() {
        Mockito.when(this.context.getAcceptLanguage()).thenReturn("en");
        validateJSR303(buildDto("regex", "length - too long", 5));
        doCheckValidationFailedTranslationEn(context);
    }

    @Test
    public void validationWithOptionalFailedTranslationEn() {
        Mockito.when(this.context.getAcceptLanguage()).thenReturn("en");
        validateJSR303WithOptional(buildDto("regex", "length - too long", 5));
        doCheckValidationFailedTranslationEn(context);
    }

    @Test
    public void validationFailedWithThreeFields() {
        validateJSR303(buildDto("regex!!!", "length is now tooooo loooong", 25));
        doCheckValidationFailedWithThreeFields(context);
    }

    @Test
    public void validationWithOptionalFailedWithThreeFields() {
        validateJSR303WithOptional(buildDto("regex!!!", "length is now tooooo loooong", 25));
        doCheckValidationFailedWithThreeFields(context);
    }

    @Test
    public void validationFailedWithTwoAnnotations() {
        validateJSR303(buildDto("regex!!! which is also too long", "length", 5));
        doValidationFailedWithTwoAnnotations(context);
    }

    @Test
    public void validationWithOptionalFailedWithTwoAnnotations() {
        validateJSR303WithOptional(buildDto("regex!!! which is also too long", "length", 5));
        doValidationFailedWithTwoAnnotations(context);
    }

    @Test
    public void validationWithNullObject() {
        validateJSR303(null);
        Assert.assertFalse(context.getValidation().hasViolations());
        validateJSR303WithOptional(null);
        Assert.assertFalse(context.getValidation().hasViolations());
        validateJSR303WithRequired(null);
        Assert.assertTrue(context.getValidation().hasViolations());
    }

    public enum Rainbow {

        Red,
        Orange,
        Yellow,
        Green,
        Blue,
        Indigo,
        Violet;}

    // Custom argument extractors for testing different instantiation paths
    public interface MockController {
        public Result noParameter();

        public Result context(Context context);

        public Result session(Session session);

        public Result flash(FlashScope flash);

        public Result param(@Param("param1")
        String param1);

        public Result pathParam(@PathParam("param1")
        String param1);

        public Result sessionParam(@SessionParam("param1")
        String param1);

        public Result attribute(@Attribute("param1")
        ControllerMethodInvokerWithDeprecatedValidationTest.Dep param1);

        public Result header(@Header("param1")
        String param1);

        public Result headers(@Headers("param1")
        String[] param1);

        public Result integerParam(@Param("param1")
        Integer param1);

        public Result intParam(@Param("param1")
        int param1);

        public Result shortParam(@Param("param1")
        Short param1);

        public Result primShortParam(@Param("param1")
        short param1);

        public Result characterParam(@Param("param1")
        Character param1);

        public Result charParam(@Param("param1")
        char param1);

        public Result byteParam(@Param("param1")
        Byte param1);

        public Result primByteParam(@Param("param1")
        byte param1);

        public Result booleanParam(@Param("param1")
        Boolean param1);

        public Result booleanParamWithOptional(@Param("param1")
        Optional<Boolean> param1);

        public Result primBooleanParam(@Param("param1")
        boolean param1);

        public Result longParam(@Param("param1")
        Long param1);

        public Result primLongParam(@Param("param1")
        long param1);

        public Result floatParam(@Param("param1")
        Float param1);

        public Result primFloatParam(@Param("param1")
        float param1);

        public Result doubleParam(@Param("param1")
        Double param1);

        public Result primDoubleParam(@Param("param1")
        double param1);

        public Result enumParam(@Param("param1")
        ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow param1);

        public Result enumCsvParam(@Param("param1")
        ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow[] param1);

        public Result enumArrayParam(@Params("param1")
        ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow[] param1);

        public Result noArgArgumentExtractor(@ControllerMethodInvokerWithDeprecatedValidationTest.NoArg
        String param1);

        public Result classArgArgumentExtractor(@ControllerMethodInvokerWithDeprecatedValidationTest.ClassArg
        String param1);

        public Result guiceArgumentExtractor(@ControllerMethodInvokerWithDeprecatedValidationTest.GuiceAnnotation(foo = "bar")
        String param1);

        public Result guiceArgumentExtractorWithOptional(@ControllerMethodInvokerWithDeprecatedValidationTest.GuiceAnnotation(foo = "bar")
        Optional<String> param1);

        public Result multiple(@Param("param1")
        String param1, @PathParam("param2")
        int param2, Context context, Session session);

        public Result required(@Param("param1")
        @Required
        String param1);

        public Result optionalSessionParam(@SessionParam("param1")
        Optional<String> param1);

        public Result optionalAttribute(@Attribute("param1")
        Optional<ControllerMethodInvokerWithDeprecatedValidationTest.Dep> param1);

        public Result optionalHeader(@Header("param1")
        Optional<String> param1);

        public Result optionalHeaders(@Headers("param1")
        Optional<String[]> param1);

        public Result optionalParam(@Param("param1")
        Optional<String> param1);

        public Result optionalIntegerParam(@Param("param1")
        Optional<Integer> param1);

        public Result optionalLongParam(@Param("param1")
        Optional<Long> param1);

        public Result optionalShortParam(@Param("param1")
        Optional<Short> param1);

        public Result optionalEnumParam(@Param("param1")
        Optional<ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow> param1);

        public Result optionalEnumArrayParam(@Param("param1")
        Optional<ControllerMethodInvokerWithDeprecatedValidationTest.Rainbow[]> param1);

        public Result optionalDateParam(@Param("param1")
        Optional<Date> param1);

        public Result optionalBody(Optional<Object> body);

        public Result requiredInt(@Param("param1")
        @Required
        @NumberValue(min = 10)
        int param1);

        public Result requiredIntWithOptional(@Param("param1")
        @Required
        @NumberValue(min = 10)
        Optional<Integer> param1);

        public Result badValidator(@Param("param1")
        @NumberValue(min = 10)
        String param1);

        public Result badValidatorWithOptional(@Param("param1")
        @NumberValue(min = 10)
        Optional<String> param1);

        public Result body(Object body);

        public Result bodyWithOptional(Optional<Object> body);

        public Result tooManyBodies(Object body1, Object body2);

        public Result JSR303Validation(@JSR303Validation
        ControllerMethodInvokerWithDeprecatedValidationTest.Dto dto, Validation validation);

        public Result JSR303ValidationWithOptional(@JSR303Validation
        Optional<ControllerMethodInvokerWithDeprecatedValidationTest.Dto> dto, Validation validation);

        public Result JSR303ValidationWithRequired(@Required
        @JSR303Validation
        ControllerMethodInvokerWithDeprecatedValidationTest.Dto dto, Validation validation);

        public Result dateParam(@Param("param1")
        Date param1);

        public Result dateParamWithOptional(@Param("param1")
        Optional<Date> param1);

        public Result needingInjectionParamParser(@Param("param1")
        ControllerMethodInvokerWithDeprecatedValidationTest.Dep param1);

        public Result needingInjectionParamParserArray(@Params("param1")
        ControllerMethodInvokerWithDeprecatedValidationTest.Dep[] paramsArray);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @WithArgumentExtractor(ControllerMethodInvokerWithDeprecatedValidationTest.NoArgArgumentExtractor.class)
    public @interface NoArg {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @WithArgumentExtractor(ControllerMethodInvokerWithDeprecatedValidationTest.ClassArgArgumentExtractor.class)
    public @interface ClassArg {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @WithArgumentExtractor(ControllerMethodInvokerWithDeprecatedValidationTest.GuiceArgumentExtractor.class)
    public @interface GuiceAnnotation {
        String foo();
    }

    public static class NoArgArgumentExtractor implements ArgumentExtractor<String> {
        @Override
        public String extract(Context context) {
            return "noargs";
        }

        @Override
        public Class<String> getExtractedType() {
            return String.class;
        }

        @Override
        public String getFieldName() {
            return null;
        }
    }

    public static class ClassArgArgumentExtractor implements ArgumentExtractor<String> {
        private final Class<?> clazz;

        public ClassArgArgumentExtractor(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public String extract(Context context) {
            return clazz.getName();
        }

        @Override
        public Class<String> getExtractedType() {
            return String.class;
        }

        @Override
        public String getFieldName() {
            return null;
        }
    }

    public static class DateParamParser implements ParamParser<Date> {
        public static final String DATE_FORMAT = "dd/MM/yyyy";

        public static final String KEY = "validation.is.date.violation";

        public static final String MESSAGE = "{0} must be a valid date";

        @Override
        public Date parseParameter(String field, String parameterValue, Validation validation) {
            try {
                return parameterValue == null ? null : new SimpleDateFormat(ControllerMethodInvokerWithDeprecatedValidationTest.DateParamParser.DATE_FORMAT).parse(parameterValue);
            } catch (ParseException e) {
                validation.addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(ControllerMethodInvokerWithDeprecatedValidationTest.DateParamParser.KEY, field, ControllerMethodInvokerWithDeprecatedValidationTest.DateParamParser.MESSAGE, parameterValue));
                return null;
            }
        }

        @Override
        public Class<Date> getParsedType() {
            return Date.class;
        }
    }

    public static class NeedingInjectionParamParser implements ParamParser<ControllerMethodInvokerWithDeprecatedValidationTest.Dep> {
        // In a real application, you can also use @Named as each properties is binded by its name
        @Inject
        NinjaProperties properties;

        @Override
        public ControllerMethodInvokerWithDeprecatedValidationTest.Dep parseParameter(String field, String parameterValue, Validation validation) {
            return new ControllerMethodInvokerWithDeprecatedValidationTest.Dep((((properties.get("needingInjectionParamParser.value")) + "_") + parameterValue));
        }

        @Override
        public Class<ControllerMethodInvokerWithDeprecatedValidationTest.Dep> getParsedType() {
            return ControllerMethodInvokerWithDeprecatedValidationTest.Dep.class;
        }
    }

    /**
     * Argument extractor that has a complex constructor for Guice. It depends on some
     * other dependency (dep), plus the annotation that was on the parameter, and the
     * class of the parameter.
     */
    public static class GuiceArgumentExtractor implements ArgumentExtractor<String> {
        private final ControllerMethodInvokerWithDeprecatedValidationTest.Dep dep;

        private final ControllerMethodInvokerWithDeprecatedValidationTest.GuiceAnnotation annot;

        private final Class clazz;

        @Inject
        public GuiceArgumentExtractor(ControllerMethodInvokerWithDeprecatedValidationTest.Dep dep, ControllerMethodInvokerWithDeprecatedValidationTest.GuiceAnnotation annot, ArgumentClassHolder holder) {
            this.dep = dep;
            this.annot = annot;
            this.clazz = holder.getArgumentClass();
        }

        @Override
        public String extract(Context context) {
            return ((((dep.value()) + ":") + (annot.foo())) + ":") + (clazz.getName());
        }

        @Override
        public Class<String> getExtractedType() {
            return String.class;
        }

        @Override
        public String getFieldName() {
            return null;
        }
    }

    public static class Dep {
        private final String value;

        public Dep(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + ((value) == null ? 0 : value.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj)
                return true;

            if (obj == null)
                return false;

            if ((getClass()) != (obj.getClass()))
                return false;

            ControllerMethodInvokerWithDeprecatedValidationTest.Dep other = ((ControllerMethodInvokerWithDeprecatedValidationTest.Dep) (obj));
            if ((value) == null) {
                if ((other.value) != null)
                    return false;

            } else
                if (!(value.equals(other.value)))
                    return false;


            return true;
        }
    }

    public class Dto {
        @Size(min = 1, max = 10)
        @Pattern(regexp = "[a-z]*")
        public String regex;

        @Size(min = 5, max = 10)
        public String length;

        @Min(3)
        @Max(10)
        public int range;
    }
}

