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
package org.graylog.plugins.pipelineprocessor.functions;


import Stream.DEFAULT_STREAM_ID;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.eventbus.EventBus;
import com.google.common.net.InetAddresses;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.graylog.plugins.pipelineprocessor.BaseParserTest;
import org.graylog.plugins.pipelineprocessor.EvaluationContext;
import org.graylog.plugins.pipelineprocessor.ast.Rule;
import org.graylog.plugins.pipelineprocessor.functions.ips.IpAddress;
import org.graylog.plugins.pipelineprocessor.functions.messages.StreamCacheService;
import org.graylog.plugins.pipelineprocessor.parser.ParseException;
import org.graylog2.plugin.InstantMillisProvider;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.Tools;
import org.graylog2.plugin.streams.Stream;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class FunctionsSnippetsTest extends BaseParserTest {
    public static final DateTime GRAYLOG_EPOCH = DateTime.parse("2010-07-30T16:03:25Z");

    private static final EventBus eventBus = new EventBus();

    private static StreamCacheService streamCacheService;

    private static Stream otherStream;

    @Test
    public void jsonpath() {
        final String json = "{\n" + ((((((((((((((((((((((((((((((((((("    \"store\": {\n" + "        \"book\": [\n") + "            {\n") + "                \"category\": \"reference\",\n") + "                \"author\": \"Nigel Rees\",\n") + "                \"title\": \"Sayings of the Century\",\n") + "                \"price\": 8.95\n") + "            },\n") + "            {\n") + "                \"category\": \"fiction\",\n") + "                \"author\": \"Evelyn Waugh\",\n") + "                \"title\": \"Sword of Honour\",\n") + "                \"price\": 12.99\n") + "            },\n") + "            {\n") + "                \"category\": \"fiction\",\n") + "                \"author\": \"Herman Melville\",\n") + "                \"title\": \"Moby Dick\",\n") + "                \"isbn\": \"0-553-21311-3\",\n") + "                \"price\": 8.99\n") + "            },\n") + "            {\n") + "                \"category\": \"fiction\",\n") + "                \"author\": \"J. R. R. Tolkien\",\n") + "                \"title\": \"The Lord of the Rings\",\n") + "                \"isbn\": \"0-395-19395-8\",\n") + "                \"price\": 22.99\n") + "            }\n") + "        ],\n") + "        \"bicycle\": {\n") + "            \"color\": \"red\",\n") + "            \"price\": 19.95\n") + "        }\n") + "    },\n") + "    \"expensive\": 10\n") + "}");
        final Rule rule = parser.parseRule(ruleForTest(), false);
        final Message message = evaluateRule(rule, new Message(json, "test", Tools.nowUTC()));
        assertThat(message.hasField("author_first")).isTrue();
        assertThat(message.getField("author_first")).isEqualTo("Nigel Rees");
        assertThat(message.hasField("author_last")).isTrue();
        assertThat(message.hasField("this_should_exist")).isTrue();
    }

    @Test
    public void json() {
        final String flatJson = "{\"str\":\"foobar\",\"int\":42,\"float\":2.5,\"bool\":true,\"array\":[1,2,3]}";
        final String nestedJson = "{\n" + ((((((((((((("    \"store\": {\n" + "        \"book\": {\n") + "            \"category\": \"reference\",\n") + "            \"author\": \"Nigel Rees\",\n") + "            \"title\": \"Sayings of the Century\",\n") + "            \"price\": 8.95\n") + "        },\n") + "        \"bicycle\": {\n") + "            \"color\": \"red\",\n") + "            \"price\": 19.95\n") + "        }\n") + "    },\n") + "    \"expensive\": 10\n") + "}");
        final Rule rule = parser.parseRule(ruleForTest(), false);
        final Message message = new Message("JSON", "test", Tools.nowUTC());
        message.addField("flat_json", flatJson);
        message.addField("nested_json", nestedJson);
        final Message evaluatedMessage = evaluateRule(rule, message);
        assertThat(evaluatedMessage.getField("message")).isEqualTo("JSON");
        assertThat(evaluatedMessage.getField("flat_json")).isEqualTo(flatJson);
        assertThat(evaluatedMessage.getField("nested_json")).isEqualTo(nestedJson);
        assertThat(evaluatedMessage.getField("str")).isEqualTo("foobar");
        assertThat(evaluatedMessage.getField("int")).isEqualTo(42);
        assertThat(evaluatedMessage.getField("float")).isEqualTo(2.5);
        assertThat(evaluatedMessage.getField("bool")).isEqualTo(true);
        assertThat(evaluatedMessage.getField("array")).isEqualTo(Arrays.asList(1, 2, 3));
        assertThat(evaluatedMessage.getField("store")).isInstanceOf(Map.class);
        assertThat(evaluatedMessage.getField("expensive")).isEqualTo(10);
    }

    @Test
    public void substring() {
        final Rule rule = parser.parseRule(ruleForTest(), false);
        evaluateRule(rule);
        assertThat(BaseParserTest.actionsTriggered.get()).isTrue();
    }

    @Test
    public void dates() {
        final InstantMillisProvider clock = new InstantMillisProvider(FunctionsSnippetsTest.GRAYLOG_EPOCH);
        DateTimeUtils.setCurrentMillisProvider(clock);
        try {
            final Rule rule;
            try {
                rule = parser.parseRule(ruleForTest(), false);
            } catch (ParseException e) {
                fail("Should not fail to parse", e);
                return;
            }
            final Message message = evaluateRule(rule);
            assertThat(BaseParserTest.actionsTriggered.get()).isTrue();
            assertThat(message).isNotNull();
            assertThat(message).isNotEmpty();
            assertThat(message.hasField("year")).isTrue();
            assertThat(message.getField("year")).isEqualTo(2010);
            assertThat(message.getField("timezone")).isEqualTo("UTC");
            // Date parsing locales
            assertThat(message.getField("german_year")).isEqualTo(1983);
            assertThat(message.getField("german_month")).isEqualTo(7);
            assertThat(message.getField("german_day")).isEqualTo(24);
            assertThat(message.getField("english_year")).isEqualTo(1983);
            assertThat(message.getField("english_month")).isEqualTo(7);
            assertThat(message.getField("english_day")).isEqualTo(24);
            assertThat(message.getField("french_year")).isEqualTo(1983);
            assertThat(message.getField("french_month")).isEqualTo(7);
            assertThat(message.getField("french_day")).isEqualTo(24);
            assertThat(message.getField("ts_hour")).isEqualTo(16);
            assertThat(message.getField("ts_minute")).isEqualTo(3);
            assertThat(message.getField("ts_second")).isEqualTo(25);
        } finally {
            DateTimeUtils.setCurrentMillisSystem();
        }
    }

    @Test
    public void datesUnixTimestamps() {
        final Rule rule = parser.parseRule(ruleForTest(), false);
        evaluateRule(rule);
        assertThat(BaseParserTest.actionsTriggered.get()).isTrue();
    }

    @Test
    public void digests() {
        final Rule rule = parser.parseRule(ruleForTest(), false);
        evaluateRule(rule);
        assertThat(BaseParserTest.actionsTriggered.get()).isTrue();
    }

    @Test
    public void encodings() {
        final Rule rule = parser.parseRule(ruleForTest(), false);
        evaluateRule(rule);
        assertThat(BaseParserTest.actionsTriggered.get()).isTrue();
    }

    @Test
    public void regexMatch() {
        final Rule rule = parser.parseRule(ruleForTest(), false);
        final Message message = evaluateRule(rule);
        Assert.assertNotNull(message);
        Assert.assertTrue(message.hasField("matched_regex"));
        Assert.assertTrue(message.hasField("group_1"));
        assertThat(((String) (message.getField("named_group")))).isEqualTo("cd.e");
    }

    @Test
    public void regexReplace() {
        final Rule rule = parser.parseRule(ruleForTest(), false);
        evaluateRule(rule);
        assertThat(BaseParserTest.actionsTriggered.get()).isTrue();
    }

    @Test
    public void strings() {
        final Rule rule = parser.parseRule(ruleForTest(), false);
        final Message message = evaluateRule(rule);
        assertThat(BaseParserTest.actionsTriggered.get()).isTrue();
        assertThat(message).isNotNull();
        assertThat(message.getField("has_xyz")).isInstanceOf(Boolean.class);
        assertThat(((boolean) (message.getField("has_xyz")))).isFalse();
        assertThat(message.getField("string_literal")).isInstanceOf(String.class);
        assertThat(((String) (message.getField("string_literal")))).isEqualTo("abcd\\.e\tfg\u03a9\u00f3");
    }

    @Test
    public void split() {
        final Rule rule = parser.parseRule(ruleForTest(), false);
        final Message message = evaluateRule(rule);
        assertThat(BaseParserTest.actionsTriggered.get()).isTrue();
        assertThat(message).isNotNull();
        assertThat(message.getField("limit_0")).asList().isNotEmpty().containsExactly("foo", "bar", "baz");
        assertThat(message.getField("limit_1")).asList().isNotEmpty().containsExactly("foo:bar:baz");
        assertThat(message.getField("limit_2")).asList().isNotEmpty().containsExactly("foo", "bar|baz");
    }

    @Test
    public void ipMatching() {
        final Rule rule = parser.parseRule(ruleForTest(), false);
        final Message in = new Message("test", "test", Tools.nowUTC());
        in.addField("ip", "192.168.1.20");
        final Message message = evaluateRule(rule, in);
        assertThat(BaseParserTest.actionsTriggered.get()).isTrue();
        assertThat(message).isNotNull();
        assertThat(message.getField("ip_anon")).isEqualTo("192.168.1.0");
        assertThat(message.getField("ipv6_anon")).isEqualTo("2001:db8::");
    }

    @Test
    public void evalErrorSuppressed() {
        final Rule rule = parser.parseRule(ruleForTest(), false);
        final Message message = new Message("test", "test", Tools.nowUTC());
        message.addField("this_field_was_set", true);
        final EvaluationContext context = contextForRuleEval(rule, message);
        assertThat(context).isNotNull();
        assertThat(context.hasEvaluationErrors()).isFalse();
        assertThat(BaseParserTest.actionsTriggered.get()).isTrue();
    }

    @Test
    public void newlyCreatedMessage() {
        final Message message = new Message("test", "test", Tools.nowUTC());
        message.addField("foo", "bar");
        message.addStream(Mockito.mock(Stream.class));
        final Rule rule = parser.parseRule(ruleForTest(), false);
        final EvaluationContext context = contextForRuleEval(rule, message);
        final Message origMessage = context.currentMessage();
        final Message newMessage = Iterables.getOnlyElement(context.createdMessages());
        assertThat(origMessage).isNotSameAs(newMessage);
        assertThat(newMessage.getMessage()).isEqualTo("new");
        assertThat(newMessage.getSource()).isEqualTo("synthetic");
        assertThat(newMessage.getStreams()).isEmpty();
        assertThat(newMessage.hasField("removed_again")).isFalse();
        assertThat(newMessage.getFieldAs(Boolean.class, "has_source")).isTrue();
        assertThat(newMessage.getFieldAs(String.class, "only_in")).isEqualTo("new message");
        assertThat(newMessage.getFieldAs(String.class, "multi")).isEqualTo("new message");
        assertThat(newMessage.getFieldAs(String.class, "foo")).isNull();
    }

    @Test
    public void clonedMessage() {
        final Message message = new Message("test", "test", Tools.nowUTC());
        message.addField("foo", "bar");
        message.addStream(Mockito.mock(Stream.class));
        final Rule rule = parser.parseRule(ruleForTest(), false);
        final EvaluationContext context = contextForRuleEval(rule, message);
        final Message origMessage = context.currentMessage();
        final Message clonedMessage = Iterables.get(context.createdMessages(), 0);
        final Message otherMessage = Iterables.get(context.createdMessages(), 1);
        assertThat(origMessage).isNotSameAs(clonedMessage);
        assertThat(clonedMessage).isNotNull();
        assertThat(clonedMessage.getMessage()).isEqualTo(origMessage.getMessage());
        assertThat(clonedMessage.getSource()).isEqualTo(origMessage.getSource());
        assertThat(clonedMessage.getTimestamp()).isEqualTo(origMessage.getTimestamp());
        assertThat(clonedMessage.getStreams()).isEqualTo(origMessage.getStreams());
        assertThat(clonedMessage.hasField("removed_again")).isFalse();
        assertThat(clonedMessage.getFieldAs(Boolean.class, "has_source")).isTrue();
        assertThat(clonedMessage.getFieldAs(String.class, "only_in")).isEqualTo("new message");
        assertThat(clonedMessage.getFieldAs(String.class, "multi")).isEqualTo("new message");
        assertThat(clonedMessage.getFieldAs(String.class, "foo")).isEqualTo("bar");
        assertThat(otherMessage).isNotNull();
        assertThat(otherMessage.getMessage()).isEqualTo("foo");
        assertThat(otherMessage.getSource()).isEqualTo("source");
    }

    @Test
    public void clonedMessageWithInvalidTimestamp() {
        final Message message = new Message("test", "test", Tools.nowUTC());
        message.addField("timestamp", "foobar");
        final Rule rule = parser.parseRule(ruleForTest(), false);
        final EvaluationContext context = contextForRuleEval(rule, message);
        final Message origMessage = context.currentMessage();
        final Message clonedMessage = Iterables.get(context.createdMessages(), 0);
        assertThat(origMessage).isNotEqualTo(clonedMessage);
        assertThat(origMessage.getField("timestamp")).isNotInstanceOf(DateTime.class);
        assertThat(clonedMessage).isNotNull();
        assertThat(clonedMessage.getMessage()).isEqualTo(origMessage.getMessage());
        assertThat(clonedMessage.getSource()).isEqualTo(origMessage.getSource());
        assertThat(clonedMessage.getStreams()).isEqualTo(origMessage.getStreams());
        assertThat(clonedMessage.getTimestamp()).isNotNull();
        assertThat(clonedMessage.getField("gl2_original_timestamp")).isEqualTo(origMessage.getField("timestamp"));
    }

    @Test
    public void grok() {
        final Rule rule = parser.parseRule(ruleForTest(), false);
        final Message message = evaluateRule(rule);
        assertThat(message).isNotNull();
        assertThat(message.getFieldCount()).isEqualTo(5);
        assertThat(message.getTimestamp()).isEqualTo(DateTime.parse("2015-07-31T10:05:36.773Z"));
        // named captures only
        assertThat(message.hasField("num")).isTrue();
        assertThat(message.hasField("BASE10NUM")).isFalse();
    }

    @Test
    public void urls() {
        final Rule rule = parser.parseRule(ruleForTest(), false);
        final Message message = evaluateRule(rule);
        assertThat(BaseParserTest.actionsTriggered.get()).isTrue();
        assertThat(message).isNotNull();
        assertThat(message.getField("protocol")).isEqualTo("https");
        assertThat(message.getField("user_info")).isEqualTo("admin:s3cr31");
        assertThat(message.getField("host")).isEqualTo("some.host.with.lots.of.subdomains.com");
        assertThat(message.getField("port")).isEqualTo(9999);
        assertThat(message.getField("file")).isEqualTo("/path1/path2/three?q1=something&with_spaces=hello%20graylog&equal=can=containanotherone");
        assertThat(message.getField("fragment")).isEqualTo("anchorstuff");
        assertThat(message.getField("query")).isEqualTo("q1=something&with_spaces=hello%20graylog&equal=can=containanotherone");
        assertThat(message.getField("q1")).isEqualTo("something");
        assertThat(message.getField("with_spaces")).isEqualTo("hello graylog");
        assertThat(message.getField("equal")).isEqualTo("can=containanotherone");
        assertThat(message.getField("authority")).isEqualTo("admin:s3cr31@some.host.with.lots.of.subdomains.com:9999");
    }

    @Test
    public void syslog() {
        final Rule rule = parser.parseRule(ruleForTest(), false);
        final Message message = evaluateRule(rule);
        assertThat(BaseParserTest.actionsTriggered.get()).isTrue();
        assertThat(message).isNotNull();
        assertThat(message.getField("level0")).isEqualTo("Emergency");
        assertThat(message.getField("level1")).isEqualTo("Alert");
        assertThat(message.getField("level2")).isEqualTo("Critical");
        assertThat(message.getField("level3")).isEqualTo("Error");
        assertThat(message.getField("level4")).isEqualTo("Warning");
        assertThat(message.getField("level5")).isEqualTo("Notice");
        assertThat(message.getField("level6")).isEqualTo("Informational");
        assertThat(message.getField("level7")).isEqualTo("Debug");
        assertThat(message.getField("facility0")).isEqualTo("kern");
        assertThat(message.getField("facility1")).isEqualTo("user");
        assertThat(message.getField("facility2")).isEqualTo("mail");
        assertThat(message.getField("facility3")).isEqualTo("daemon");
        assertThat(message.getField("facility4")).isEqualTo("auth");
        assertThat(message.getField("facility5")).isEqualTo("syslog");
        assertThat(message.getField("facility6")).isEqualTo("lpr");
        assertThat(message.getField("facility7")).isEqualTo("news");
        assertThat(message.getField("facility8")).isEqualTo("uucp");
        assertThat(message.getField("facility9")).isEqualTo("clock");
        assertThat(message.getField("facility10")).isEqualTo("authpriv");
        assertThat(message.getField("facility11")).isEqualTo("ftp");
        assertThat(message.getField("facility12")).isEqualTo("ntp");
        assertThat(message.getField("facility13")).isEqualTo("log audit");
        assertThat(message.getField("facility14")).isEqualTo("log alert");
        assertThat(message.getField("facility15")).isEqualTo("cron");
        assertThat(message.getField("facility16")).isEqualTo("local0");
        assertThat(message.getField("facility17")).isEqualTo("local1");
        assertThat(message.getField("facility18")).isEqualTo("local2");
        assertThat(message.getField("facility19")).isEqualTo("local3");
        assertThat(message.getField("facility20")).isEqualTo("local4");
        assertThat(message.getField("facility21")).isEqualTo("local5");
        assertThat(message.getField("facility22")).isEqualTo("local6");
        assertThat(message.getField("facility23")).isEqualTo("local7");
        assertThat(message.getField("prio1_facility")).isEqualTo(0);
        assertThat(message.getField("prio1_level")).isEqualTo(0);
        assertThat(message.getField("prio2_facility")).isEqualTo(20);
        assertThat(message.getField("prio2_level")).isEqualTo(5);
        assertThat(message.getField("prio3_facility")).isEqualTo("kern");
        assertThat(message.getField("prio3_level")).isEqualTo("Emergency");
        assertThat(message.getField("prio4_facility")).isEqualTo("local4");
        assertThat(message.getField("prio4_level")).isEqualTo("Notice");
    }

    @Test
    public void ipMatchingIssue28() {
        final Rule rule = parser.parseRule(ruleForTest(), false);
        final Message in = new Message("some message", "somehost.graylog.org", Tools.nowUTC());
        evaluateRule(rule, in);
        assertThat(BaseParserTest.actionsTriggered.get()).isFalse();
    }

    @Test
    public void fieldRenaming() {
        final Rule rule = parser.parseRule(ruleForTest(), false);
        final Message in = new Message("some message", "somehost.graylog.org", Tools.nowUTC());
        in.addField("field_a", "fieldAContent");
        in.addField("field_b", "not deleted");
        final Message message = evaluateRule(rule, in);
        assertThat(message.hasField("field_1")).isFalse();
        assertThat(message.hasField("field_2")).isTrue();
        assertThat(message.hasField("field_b")).isTrue();
    }

    @Test
    public void comparisons() {
        final Rule rule = parser.parseRule(ruleForTest(), false);
        final EvaluationContext context = contextForRuleEval(rule, new Message("", "", Tools.nowUTC()));
        assertThat(context.hasEvaluationErrors()).isFalse();
        assertThat(evaluateRule(rule)).isNotNull();
        assertThat(BaseParserTest.actionsTriggered.get()).isTrue();
    }

    @Test
    public void conversions() {
        final Rule rule = parser.parseRule(ruleForTest(), false);
        final EvaluationContext context = contextForRuleEval(rule, new Message("test", "test", Tools.nowUTC()));
        assertThat(context.evaluationErrors()).isEmpty();
        final Message message = context.currentMessage();
        Assert.assertNotNull(message);
        assertThat(message.getField("string_1")).isEqualTo("1");
        assertThat(message.getField("string_2")).isEqualTo("2");
        // special case, Message doesn't allow adding fields with empty string values
        assertThat(message.hasField("string_3")).isFalse();
        assertThat(message.getField("string_4")).isEqualTo("default");
        assertThat(message.getField("string_5")).isEqualTo("false");
        assertThat(message.getField("string_6")).isEqualTo("42");
        assertThat(message.getField("string_7")).isEqualTo("23.42");
        assertThat(message.getField("long_1")).isEqualTo(1L);
        assertThat(message.getField("long_2")).isEqualTo(2L);
        assertThat(message.getField("long_3")).isEqualTo(0L);
        assertThat(message.getField("long_4")).isEqualTo(1L);
        assertThat(message.getField("long_5")).isEqualTo(23L);
        assertThat(message.getField("long_6")).isEqualTo(23L);
        assertThat(message.getField("long_7")).isEqualTo(1L);
        assertThat(message.getField("long_min1")).isEqualTo(Long.MIN_VALUE);
        assertThat(message.getField("long_min2")).isEqualTo(1L);
        assertThat(message.getField("long_max1")).isEqualTo(Long.MAX_VALUE);
        assertThat(message.getField("long_max2")).isEqualTo(1L);
        assertThat(message.getField("double_1")).isEqualTo(1.0);
        assertThat(message.getField("double_2")).isEqualTo(2.0);
        assertThat(message.getField("double_3")).isEqualTo(0.0);
        assertThat(message.getField("double_4")).isEqualTo(1.0);
        assertThat(message.getField("double_5")).isEqualTo(23.0);
        assertThat(message.getField("double_6")).isEqualTo(23.0);
        assertThat(message.getField("double_7")).isEqualTo(23.42);
        assertThat(message.getField("double_min1")).isEqualTo(Double.MIN_VALUE);
        assertThat(message.getField("double_min2")).isEqualTo(0.0);
        assertThat(message.getField("double_max1")).isEqualTo(Double.MAX_VALUE);
        assertThat(message.getField("double_inf1")).isEqualTo(Double.POSITIVE_INFINITY);
        assertThat(message.getField("double_inf2")).isEqualTo(Double.NEGATIVE_INFINITY);
        assertThat(message.getField("double_inf3")).isEqualTo(Double.POSITIVE_INFINITY);
        assertThat(message.getField("double_inf4")).isEqualTo(Double.NEGATIVE_INFINITY);
        assertThat(message.getField("bool_1")).isEqualTo(true);
        assertThat(message.getField("bool_2")).isEqualTo(false);
        assertThat(message.getField("bool_3")).isEqualTo(false);
        assertThat(message.getField("bool_4")).isEqualTo(true);
        // the is wrapped in our own class for safety in rules
        assertThat(message.getField("ip_1")).isEqualTo(new IpAddress(InetAddresses.forString("127.0.0.1")));
        assertThat(message.getField("ip_2")).isEqualTo(new IpAddress(InetAddresses.forString("127.0.0.1")));
        assertThat(message.getField("ip_3")).isEqualTo(new IpAddress(InetAddresses.forString("0.0.0.0")));
        assertThat(message.getField("ip_4")).isEqualTo(new IpAddress(InetAddresses.forString("::1")));
        assertThat(message.getField("map_1")).isEqualTo(Collections.singletonMap("foo", "bar"));
        assertThat(message.getField("map_2")).isEqualTo(Collections.emptyMap());
        assertThat(message.getField("map_3")).isEqualTo(Collections.emptyMap());
        assertThat(message.getField("map_4")).isEqualTo(Collections.emptyMap());
        assertThat(message.getField("map_5")).isEqualTo(Collections.emptyMap());
        assertThat(message.getField("map_6")).isEqualTo(Collections.emptyMap());
    }

    @Test
    public void fieldPrefixSuffix() {
        final Rule rule = parser.parseRule(ruleForTest(), false);
        final Message message = evaluateRule(rule);
        assertThat(message).isNotNull();
        assertThat(message.getField("field")).isEqualTo("1");
        assertThat(message.getField("prae_field_sueff")).isEqualTo("2");
        assertThat(message.getField("field_sueff")).isEqualTo("3");
        assertThat(message.getField("prae_field")).isEqualTo("4");
        assertThat(message.getField("pre_field1_suff")).isEqualTo("5");
        assertThat(message.getField("pre_field2_suff")).isEqualTo("6");
        assertThat(message.getField("pre_field1")).isEqualTo("7");
        assertThat(message.getField("pre_field2")).isEqualTo("8");
        assertThat(message.getField("field1_suff")).isEqualTo("9");
        assertThat(message.getField("field2_suff")).isEqualTo("10");
    }

    @Test
    public void keyValue() {
        final Rule rule = parser.parseRule(ruleForTest(), true);
        final EvaluationContext context = contextForRuleEval(rule, new Message("", "", Tools.nowUTC()));
        assertThat(context).isNotNull();
        assertThat(context.evaluationErrors()).isEmpty();
        final Message message = context.currentMessage();
        assertThat(message).isNotNull();
        assertThat(message.getField("a")).isEqualTo("1,4");
        assertThat(message.getField("b")).isEqualTo("2");
        assertThat(message.getField("c")).isEqualTo("3");
        assertThat(message.getField("d")).isEqualTo("44");
        assertThat(message.getField("e")).isEqualTo("4");
        assertThat(message.getField("f")).isEqualTo("1");
        assertThat(message.getField("g")).isEqualTo("3");
        assertThat(message.getField("h")).isEqualTo("3=:3");
        assertThat(message.hasField("i")).isFalse();
        assertThat(message.getField("dup_first")).isEqualTo("1");
        assertThat(message.getField("dup_last")).isEqualTo("2");
    }

    @Test
    public void keyValueFailure() {
        final Rule rule = parser.parseRule(ruleForTest(), true);
        final EvaluationContext context = contextForRuleEval(rule, new Message("", "", Tools.nowUTC()));
        assertThat(context.hasEvaluationErrors()).isTrue();
    }

    @Test
    public void timezones() {
        final InstantMillisProvider clock = new InstantMillisProvider(FunctionsSnippetsTest.GRAYLOG_EPOCH);
        DateTimeUtils.setCurrentMillisProvider(clock);
        try {
            final Rule rule = parser.parseRule(ruleForTest(), true);
            evaluateRule(rule);
            assertThat(BaseParserTest.actionsTriggered.get()).isTrue();
        } finally {
            DateTimeUtils.setCurrentMillisSystem();
        }
    }

    @Test
    public void dateArithmetic() {
        final InstantMillisProvider clock = new InstantMillisProvider(FunctionsSnippetsTest.GRAYLOG_EPOCH);
        DateTimeUtils.setCurrentMillisProvider(clock);
        try {
            final Rule rule = parser.parseRule(ruleForTest(), true);
            final Message message = evaluateRule(rule);
            assertThat(BaseParserTest.actionsTriggered.get()).isTrue();
            assertThat(message).isNotNull();
            assertThat(message.getField("interval")).isInstanceOf(Duration.class).matches(( o) -> ((Duration) (o)).isEqual(Duration.standardDays(1)), "Exactly one day difference");
            assertThat(message.getField("years")).isEqualTo(Period.years(2));
            assertThat(message.getField("months")).isEqualTo(Period.months(2));
            assertThat(message.getField("weeks")).isEqualTo(Period.weeks(2));
            assertThat(message.getField("days")).isEqualTo(Period.days(2));
            assertThat(message.getField("hours")).isEqualTo(Period.hours(2));
            assertThat(message.getField("minutes")).isEqualTo(Period.minutes(2));
            assertThat(message.getField("seconds")).isEqualTo(Period.seconds(2));
            assertThat(message.getField("millis")).isEqualTo(Period.millis(2));
            assertThat(message.getField("period")).isEqualTo(Period.parse("P1YT1M"));
            assertThat(message.getFieldAs(DateTime.class, "long_time_ago")).matches(( date) -> date.plus(Period.years(10000)).equals(GRAYLOG_EPOCH));
            assertThat(message.getTimestamp()).isEqualTo(FunctionsSnippetsTest.GRAYLOG_EPOCH.plusHours(1));
        } finally {
            DateTimeUtils.setCurrentMillisSystem();
        }
    }

    @Test
    public void routeToStream() {
        final Rule rule = parser.parseRule(ruleForTest(), true);
        final Message message = evaluateRule(rule);
        assertThat(message).isNotNull();
        assertThat(message.getStreams()).isNotEmpty();
        assertThat(message.getStreams().size()).isEqualTo(2);
        FunctionsSnippetsTest.streamCacheService.updateStreams(ImmutableSet.of("id"));
        final Message message2 = evaluateRule(rule);
        assertThat(message2).isNotNull();
        assertThat(message2.getStreams().size()).isEqualTo(2);
    }

    @Test
    public void routeToStreamRemoveDefault() {
        final Rule rule = parser.parseRule(ruleForTest(), true);
        final Message message = evaluateRule(rule);
        assertThat(message).isNotNull();
        assertThat(message.getStreams()).isNotEmpty();
        assertThat(message.getStreams().size()).isEqualTo(1);
        FunctionsSnippetsTest.streamCacheService.updateStreams(ImmutableSet.of(DEFAULT_STREAM_ID));
        final Message message2 = evaluateRule(rule);
        assertThat(message2).isNotNull();
        assertThat(message2.getStreams().size()).isEqualTo(1);
    }

    @Test
    public void removeFromStream() {
        final Rule rule = parser.parseRule(ruleForTest(), true);
        final Message message = evaluateRule(rule, ( msg) -> msg.addStream(FunctionsSnippetsTest.otherStream));
        assertThat(message).isNotNull();
        assertThat(message.getStreams()).containsOnly(BaseParserTest.defaultStream);
    }

    @Test
    public void removeFromStreamRetainDefault() {
        final Rule rule = parser.parseRule(ruleForTest(), true);
        final Message message = evaluateRule(rule, ( msg) -> msg.addStream(FunctionsSnippetsTest.otherStream));
        assertThat(message).isNotNull();
        assertThat(message.getStreams()).containsOnly(BaseParserTest.defaultStream);
    }

    @Test
    public void int2ipv4() {
        final Rule rule = parser.parseRule(ruleForTest(), true);
        evaluateRule(rule);
        assertThat(BaseParserTest.actionsTriggered.get()).isTrue();
    }
}

