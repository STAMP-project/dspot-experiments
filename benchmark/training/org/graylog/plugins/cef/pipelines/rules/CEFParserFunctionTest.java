/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog.plugins.cef.pipelines.rules;


import CEFParserFunction.USE_FULL_NAMES;
import CEFParserFunction.VALUE;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Map;
import org.antlr.v4.runtime.CommonToken;
import org.graylog.plugins.pipelineprocessor.EvaluationContext;
import org.graylog.plugins.pipelineprocessor.ast.expressions.Expression;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import org.graylog2.plugin.Message;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;


public class CEFParserFunctionTest {
    private CEFParserFunction function;

    @Test
    public void evaluate_returns_null_for_missing_CEF_string() throws Exception {
        final FunctionArgs functionArgs = new FunctionArgs(function, Collections.emptyMap());
        final Message message = new Message("__dummy", "__dummy", DateTime.parse("2010-07-30T16:03:25Z"));
        final EvaluationContext evaluationContext = new EvaluationContext(message);
        final CEFParserResult result = function.evaluate(functionArgs, evaluationContext);
        Assert.assertNull(result);
    }

    @Test
    public void evaluate_returns_null_for_empty_CEF_string() throws Exception {
        final Map<String, Expression> arguments = Collections.singletonMap(VALUE, new org.graylog.plugins.pipelineprocessor.ast.expressions.StringExpression(new CommonToken(0), ""));
        final FunctionArgs functionArgs = new FunctionArgs(function, arguments);
        final Message message = new Message("__dummy", "__dummy", DateTime.parse("2010-07-30T16:03:25Z"));
        final EvaluationContext evaluationContext = new EvaluationContext(message);
        final CEFParserResult result = function.evaluate(functionArgs, evaluationContext);
        Assert.assertNull(result);
    }

    @Test
    public void evaluate_returns_null_for_invalid_CEF_string() throws Exception {
        final Map<String, Expression> arguments = ImmutableMap.of(VALUE, new org.graylog.plugins.pipelineprocessor.ast.expressions.StringExpression(new CommonToken(0), "CEF:0|Foobar"), USE_FULL_NAMES, new org.graylog.plugins.pipelineprocessor.ast.expressions.BooleanExpression(new CommonToken(0), false));
        final FunctionArgs functionArgs = new FunctionArgs(function, arguments);
        final Message message = new Message("__dummy", "__dummy", DateTime.parse("2010-07-30T16:03:25Z"));
        final EvaluationContext evaluationContext = new EvaluationContext(message);
        final CEFParserResult result = function.evaluate(functionArgs, evaluationContext);
        Assert.assertNull(result);
    }

    @Test
    public void evaluate_returns_result_for_valid_CEF_string() throws Exception {
        final Map<String, Expression> arguments = ImmutableMap.of(VALUE, new org.graylog.plugins.pipelineprocessor.ast.expressions.StringExpression(new CommonToken(0), "CEF:0|vendor|product|1.0|id|name|low|dvc=example.com msg=Foobar"), USE_FULL_NAMES, new org.graylog.plugins.pipelineprocessor.ast.expressions.BooleanExpression(new CommonToken(0), false));
        final FunctionArgs functionArgs = new FunctionArgs(function, arguments);
        final Message message = new Message("__dummy", "__dummy", DateTime.parse("2010-07-30T16:03:25Z"));
        final EvaluationContext evaluationContext = new EvaluationContext(message);
        final CEFParserResult result = function.evaluate(functionArgs, evaluationContext);
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.get("cef_version"));
        Assert.assertEquals("vendor", result.get("device_vendor"));
        Assert.assertEquals("product", result.get("device_product"));
        Assert.assertEquals("1.0", result.get("device_version"));
        Assert.assertEquals("id", result.get("device_event_class_id"));
        Assert.assertEquals("low", result.get("severity"));
        Assert.assertEquals("example.com", result.get("dvc"));
        Assert.assertEquals("Foobar", result.get("msg"));
    }

    @Test
    public void evaluate_returns_result_for_valid_CEF_string_with_short_names_if_useFullNames_parameter_is_missing() throws Exception {
        final Map<String, Expression> arguments = Collections.singletonMap(VALUE, new org.graylog.plugins.pipelineprocessor.ast.expressions.StringExpression(new CommonToken(0), "CEF:0|vendor|product|1.0|id|name|low|dvc=example.com msg=Foobar"));
        final FunctionArgs functionArgs = new FunctionArgs(function, arguments);
        final Message message = new Message("__dummy", "__dummy", DateTime.parse("2010-07-30T16:03:25Z"));
        final EvaluationContext evaluationContext = new EvaluationContext(message);
        final CEFParserResult result = function.evaluate(functionArgs, evaluationContext);
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.get("cef_version"));
        Assert.assertEquals("vendor", result.get("device_vendor"));
        Assert.assertEquals("product", result.get("device_product"));
        Assert.assertEquals("1.0", result.get("device_version"));
        Assert.assertEquals("id", result.get("device_event_class_id"));
        Assert.assertEquals("low", result.get("severity"));
        Assert.assertEquals("example.com", result.get("dvc"));
        Assert.assertEquals("Foobar", result.get("msg"));
    }

    @Test
    public void evaluate_returns_result_for_valid_CEF_string_with_full_names() throws Exception {
        final CEFParserFunction function = new CEFParserFunction(new MetricRegistry());
        final Map<String, Expression> arguments = ImmutableMap.of(VALUE, new org.graylog.plugins.pipelineprocessor.ast.expressions.StringExpression(new CommonToken(0), "CEF:0|vendor|product|1.0|id|name|low|dvc=example.com msg=Foobar"), USE_FULL_NAMES, new org.graylog.plugins.pipelineprocessor.ast.expressions.BooleanExpression(new CommonToken(0), true));
        final FunctionArgs functionArgs = new FunctionArgs(function, arguments);
        final Message message = new Message("__dummy", "__dummy", DateTime.parse("2010-07-30T16:03:25Z"));
        final EvaluationContext evaluationContext = new EvaluationContext(message);
        final CEFParserResult result = function.evaluate(functionArgs, evaluationContext);
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.get("cef_version"));
        Assert.assertEquals("vendor", result.get("device_vendor"));
        Assert.assertEquals("product", result.get("device_product"));
        Assert.assertEquals("1.0", result.get("device_version"));
        Assert.assertEquals("id", result.get("device_event_class_id"));
        Assert.assertEquals("low", result.get("severity"));
        Assert.assertEquals("example.com", result.get("deviceAddress"));
        Assert.assertEquals("Foobar", result.get("message"));
    }

    @Test
    public void evaluate_returns_result_without_message_field() throws Exception {
        final Map<String, Expression> arguments = ImmutableMap.of(VALUE, new org.graylog.plugins.pipelineprocessor.ast.expressions.StringExpression(new CommonToken(0), "CEF:0|vendor|product|1.0|id|name|low|dvc=example.com"), USE_FULL_NAMES, new org.graylog.plugins.pipelineprocessor.ast.expressions.BooleanExpression(new CommonToken(0), false));
        final FunctionArgs functionArgs = new FunctionArgs(function, arguments);
        final Message message = new Message("__dummy", "__dummy", DateTime.parse("2010-07-30T16:03:25Z"));
        final EvaluationContext evaluationContext = new EvaluationContext(message);
        final CEFParserResult result = function.evaluate(functionArgs, evaluationContext);
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.get("cef_version"));
        Assert.assertEquals("vendor", result.get("device_vendor"));
        Assert.assertEquals("product", result.get("device_product"));
        Assert.assertEquals("1.0", result.get("device_version"));
        Assert.assertEquals("id", result.get("device_event_class_id"));
        Assert.assertEquals("low", result.get("severity"));
        Assert.assertEquals("example.com", result.get("dvc"));
        Assert.assertFalse(result.containsKey("message"));
    }
}

