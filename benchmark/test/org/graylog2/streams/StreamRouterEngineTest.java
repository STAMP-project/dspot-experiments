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
package org.graylog2.streams;


import Stream.MatchingType.AND;
import Stream.MatchingType.OR;
import StreamRouterEngine.StreamTestMatch;
import StreamRuleType.CONTAINS;
import StreamRuleType.EXACT;
import StreamRuleType.GREATER;
import StreamRuleType.PRESENCE;
import StreamRuleType.REGEX;
import StreamRuleType.SMALLER;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.inject.Provider;
import org.bson.types.ObjectId;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.streams.Stream;
import org.graylog2.plugin.streams.StreamRule;
import org.graylog2.streams.matchers.StreamRuleMock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class StreamRouterEngineTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private StreamFaultManager streamFaultManager;

    @Mock
    private Stream defaultStream;

    private Provider<Stream> defaultStreamProvider;

    private StreamMetrics streamMetrics;

    @Test
    public void testGetStreams() throws Exception {
        final StreamMock stream = getStreamMock("test");
        final StreamRouterEngine engine = newEngine(Lists.newArrayList(stream));
        Assert.assertEquals(Lists.newArrayList(stream), engine.getStreams());
    }

    @Test
    public void testPresenceMatch() throws Exception {
        final StreamMock stream = getStreamMock("test");
        final StreamRuleMock rule = new StreamRuleMock(ImmutableMap.of("_id", new ObjectId(), "field", "testfield", "type", PRESENCE.toInteger(), "stream_id", stream.getId()));
        stream.setStreamRules(Lists.newArrayList(rule));
        final StreamRouterEngine engine = newEngine(Lists.newArrayList(stream));
        final Message message = getMessage();
        // Without testfield in the message.
        Assert.assertTrue(engine.match(message).isEmpty());
        // With field in the message.
        message.addField("testfield", "testvalue");
        Assert.assertEquals(Lists.newArrayList(stream), engine.match(message));
    }

    @Test
    public void testRemoveFromAllMessages() throws Exception {
        final StreamMock stream = getStreamMock("test");
        final StreamRuleMock rule = new StreamRuleMock(ImmutableMap.of("_id", new ObjectId(), "field", "testfield", "type", PRESENCE.toInteger(), "stream_id", stream.getId()));
        stream.setRemoveMatchesFromDefaultStream(true);
        stream.setStreamRules(Collections.singletonList(rule));
        final StreamRouterEngine engine = newEngine(Collections.singletonList(stream));
        final Message message = getMessage();
        message.addStream(defaultStream);
        assertThat(message.getStreams()).containsExactly(defaultStream);
        // Without testfield in the message.
        assertThat(engine.match(message)).isEmpty();
        // With field in the message.
        message.addField("testfield", "testvalue");
        assertThat(engine.match(message)).containsExactly(stream);
        assertThat(message.getStreams()).doesNotContain(defaultStream);
    }

    @Test
    public void testExactMatch() throws Exception {
        final StreamMock stream = getStreamMock("test");
        final StreamRuleMock rule = new StreamRuleMock(ImmutableMap.of("_id", new ObjectId(), "field", "testfield", "value", "testvalue", "type", EXACT.toInteger(), "stream_id", stream.getId()));
        stream.setStreamRules(Lists.newArrayList(rule));
        final StreamRouterEngine engine = newEngine(Lists.newArrayList(stream));
        final Message message = getMessage();
        // With wrong value for field.
        message.addField("testfield", "no-testvalue");
        Assert.assertTrue(engine.match(message).isEmpty());
        // With matching value for field.
        message.addField("testfield", "testvalue");
        Assert.assertEquals(Lists.newArrayList(stream), engine.match(message));
    }

    @Test
    public void testContainsMatch() throws Exception {
        final StreamMock stream = getStreamMock("test");
        final StreamRuleMock rule = new StreamRuleMock(ImmutableMap.of("_id", new ObjectId(), "field", "testfield", "value", "testvalue", "type", CONTAINS.toInteger(), "stream_id", stream.getId()));
        stream.setStreamRules(Lists.newArrayList(rule));
        final StreamRouterEngine engine = newEngine(Lists.newArrayList(stream));
        final Message message = getMessage();
        // Without the field
        Assert.assertTrue(engine.match(message).isEmpty());
        // With wrong value for field.
        message.addField("testfield", "no-foobar");
        Assert.assertTrue(engine.match(message).isEmpty());
        // With matching value for field.
        message.addField("testfield", "hello testvalue");
        Assert.assertEquals(Lists.newArrayList(stream), engine.match(message));
    }

    @Test
    public void testInvertedContainsMatch() throws Exception {
        final StreamMock stream = getStreamMock("test");
        final StreamRuleMock rule = new StreamRuleMock(ImmutableMap.<String, Object>builder().put("_id", new ObjectId()).put("field", "testfield").put("inverted", true).put("value", "testvalue").put("type", CONTAINS.toInteger()).put("stream_id", stream.getId()).build());
        stream.setStreamRules(Lists.newArrayList(rule));
        final StreamRouterEngine engine = newEngine(Lists.newArrayList(stream));
        final Message message = getMessage();
        // Without the field
        Assert.assertEquals(Lists.newArrayList(stream), engine.match(message));
        // Without the matching value in the field
        message.addField("testfield", "no-foobar");
        Assert.assertEquals(Lists.newArrayList(stream), engine.match(message));
        // With matching value in the field.
        message.addField("testfield", "hello testvalue");
        Assert.assertTrue(engine.match(message).isEmpty());
    }

    @Test
    public void testGreaterMatch() throws Exception {
        final StreamMock stream = getStreamMock("test");
        final StreamRuleMock rule = new StreamRuleMock(ImmutableMap.of("_id", new ObjectId(), "field", "testfield", "value", "1", "type", GREATER.toInteger(), "stream_id", stream.getId()));
        stream.setStreamRules(Lists.newArrayList(rule));
        final StreamRouterEngine engine = newEngine(Lists.newArrayList(stream));
        final Message message = getMessage();
        // With smaller value.
        message.addField("testfield", "1");
        Assert.assertTrue(engine.match(message).isEmpty());
        // With greater value.
        message.addField("testfield", "2");
        Assert.assertEquals(Lists.newArrayList(stream), engine.match(message));
    }

    @Test
    public void testSmallerMatch() throws Exception {
        final StreamMock stream = getStreamMock("test");
        final StreamRuleMock rule = new StreamRuleMock(ImmutableMap.of("_id", new ObjectId(), "field", "testfield", "value", "5", "type", SMALLER.toInteger(), "stream_id", stream.getId()));
        stream.setStreamRules(Lists.newArrayList(rule));
        final StreamRouterEngine engine = newEngine(Lists.newArrayList(stream));
        final Message message = getMessage();
        // With bigger value.
        message.addField("testfield", "5");
        Assert.assertTrue(engine.match(message).isEmpty());
        // With smaller value.
        message.addField("testfield", "2");
        Assert.assertEquals(Lists.newArrayList(stream), engine.match(message));
    }

    @Test
    public void testRegexMatch() throws Exception {
        final StreamMock stream = getStreamMock("test");
        final StreamRuleMock rule = new StreamRuleMock(ImmutableMap.of("_id", new ObjectId(), "field", "testfield", "value", "^test", "type", REGEX.toInteger(), "stream_id", stream.getId()));
        stream.setStreamRules(Lists.newArrayList(rule));
        final StreamRouterEngine engine = newEngine(Lists.newArrayList(stream));
        final Message message = getMessage();
        // With non-matching value.
        message.addField("testfield", "notestvalue");
        Assert.assertTrue(engine.match(message).isEmpty());
        // With matching value.
        message.addField("testfield", "testvalue");
        Assert.assertEquals(Lists.newArrayList(stream), engine.match(message));
    }

    @Test
    public void testMultipleRulesMatch() throws Exception {
        final StreamMock stream = getStreamMock("test");
        final StreamRuleMock rule1 = new StreamRuleMock(ImmutableMap.of("_id", new ObjectId(), "field", "testfield1", "type", PRESENCE.toInteger(), "stream_id", stream.getId()));
        final StreamRuleMock rule2 = new StreamRuleMock(ImmutableMap.of("_id", new ObjectId(), "field", "testfield2", "value", "^test", "type", REGEX.toInteger(), "stream_id", stream.getId()));
        stream.setStreamRules(Lists.newArrayList(rule1, rule2));
        final StreamRouterEngine engine = newEngine(Lists.newArrayList(stream));
        // Without testfield1 and testfield2 in the message.
        final Message message1 = getMessage();
        Assert.assertTrue(engine.match(message1).isEmpty());
        // With testfield1 but no-matching testfield2 in the message.
        final Message message2 = getMessage();
        message2.addField("testfield1", "testvalue");
        message2.addField("testfield2", "no-testvalue");
        Assert.assertTrue(engine.match(message2).isEmpty());
        // With testfield1 and matching testfield2 in the message.
        final Message message3 = getMessage();
        message3.addField("testfield1", "testvalue");
        message3.addField("testfield2", "testvalue2");
        Assert.assertEquals(Lists.newArrayList(stream), engine.match(message3));
    }

    @Test
    public void testMultipleStreamsMatch() throws Exception {
        final StreamMock stream1 = getStreamMock("test1");
        final StreamMock stream2 = getStreamMock("test2");
        final StreamRuleMock rule1 = new StreamRuleMock(ImmutableMap.of("_id", new ObjectId(), "field", "testfield1", "type", PRESENCE.toInteger(), "stream_id", stream1.getId()));
        final StreamRuleMock rule2 = new StreamRuleMock(ImmutableMap.of("_id", new ObjectId(), "field", "testfield2", "value", "^test", "type", REGEX.toInteger(), "stream_id", stream1.getId()));
        final StreamRuleMock rule3 = new StreamRuleMock(ImmutableMap.of("_id", new ObjectId(), "field", "testfield3", "value", "testvalue3", "type", EXACT.toInteger(), "stream_id", stream2.getId()));
        stream1.setStreamRules(Lists.newArrayList(rule1, rule2));
        stream2.setStreamRules(Lists.newArrayList(rule3));
        final StreamRouterEngine engine = newEngine(Lists.newArrayList(stream1, stream2));
        // Without testfield1 and testfield2 in the message.
        final Message message1 = getMessage();
        Assert.assertTrue(engine.match(message1).isEmpty());
        // With testfield1 and matching testfield2 in the message.
        final Message message2 = getMessage();
        message2.addField("testfield1", "testvalue");
        message2.addField("testfield2", "testvalue2");
        Assert.assertEquals(Lists.newArrayList(stream1), engine.match(message2));
        // With testfield1, matching testfield2 and matching testfield3 in the message.
        final Message message3 = getMessage();
        message3.addField("testfield1", "testvalue");
        message3.addField("testfield2", "testvalue2");
        message3.addField("testfield3", "testvalue3");
        final List<Stream> match = engine.match(message3);
        Assert.assertTrue(match.contains(stream1));
        Assert.assertTrue(match.contains(stream2));
        Assert.assertEquals(2, match.size());
        // With matching testfield3 in the message.
        final Message message4 = getMessage();
        message4.addField("testfield3", "testvalue3");
        Assert.assertEquals(Lists.newArrayList(stream2), engine.match(message4));
    }

    @Test
    public void testInvertedRulesMatch() throws Exception {
        final StreamMock stream = getStreamMock("test");
        final StreamRuleMock rule1 = new StreamRuleMock(ImmutableMap.of("_id", new ObjectId(), "field", "testfield1", "value", "1", "type", PRESENCE.toInteger(), "stream_id", stream.getId()));
        final StreamRuleMock rule2 = new StreamRuleMock(ImmutableMap.of("_id", new ObjectId(), "field", "testfield2", "inverted", true, "type", PRESENCE.toInteger(), "stream_id", stream.getId()));
        stream.setStreamRules(Lists.newArrayList(rule1, rule2));
        final StreamRouterEngine engine = newEngine(Lists.newArrayList(stream));
        // Without testfield1 and testfield2 in the message.
        final Message message1 = getMessage();
        Assert.assertTrue(engine.match(message1).isEmpty());
        // With testfield1 and testfield2 in the message.
        final Message message2 = getMessage();
        message2.addField("testfield1", "testvalue");
        message2.addField("testfield2", "testvalue");
        Assert.assertTrue(engine.match(message2).isEmpty());
        // With testfield1 and not testfield2 in the message.
        final Message message3 = getMessage();
        message3.addField("testfield1", "testvalue");
        Assert.assertEquals(Lists.newArrayList(stream), engine.match(message3));
        // With testfield2 in the message.
        final Message message4 = getMessage();
        message4.addField("testfield2", "testvalue");
        Assert.assertTrue(engine.match(message4).isEmpty());
    }

    @Test
    public void testTestMatch() throws Exception {
        final StreamMock stream = getStreamMock("test");
        final StreamRuleMock rule1 = new StreamRuleMock(ImmutableMap.of("_id", new ObjectId(), "field", "testfield1", "type", PRESENCE.toInteger(), "stream_id", stream.getId()));
        final StreamRuleMock rule2 = new StreamRuleMock(ImmutableMap.of("_id", new ObjectId(), "field", "testfield2", "value", "^test", "type", REGEX.toInteger(), "stream_id", stream.getId()));
        stream.setStreamRules(Lists.newArrayList(rule1, rule2));
        final StreamRouterEngine engine = newEngine(Lists.newArrayList(stream));
        // Without testfield1 and testfield2 in the message.
        final Message message1 = getMessage();
        final StreamRouterEngine.StreamTestMatch testMatch1 = engine.testMatch(message1).get(0);
        final Map<StreamRule, Boolean> matches1 = testMatch1.getMatches();
        Assert.assertFalse(testMatch1.isMatched());
        Assert.assertFalse(matches1.get(rule1));
        Assert.assertFalse(matches1.get(rule2));
        // With testfield1 but no-matching testfield2 in the message.
        final Message message2 = getMessage();
        message2.addField("testfield1", "testvalue");
        message2.addField("testfield2", "no-testvalue");
        final StreamRouterEngine.StreamTestMatch testMatch2 = engine.testMatch(message2).get(0);
        final Map<StreamRule, Boolean> matches2 = testMatch2.getMatches();
        Assert.assertFalse(testMatch2.isMatched());
        Assert.assertTrue(matches2.get(rule1));
        Assert.assertFalse(matches2.get(rule2));
        // With testfield1 and matching testfield2 in the message.
        final Message message3 = getMessage();
        message3.addField("testfield1", "testvalue");
        message3.addField("testfield2", "testvalue2");
        final StreamRouterEngine.StreamTestMatch testMatch3 = engine.testMatch(message3).get(0);
        final Map<StreamRule, Boolean> matches3 = testMatch3.getMatches();
        Assert.assertTrue(testMatch3.isMatched());
        Assert.assertTrue(matches3.get(rule1));
        Assert.assertTrue(matches3.get(rule2));
    }

    @Test
    public void testOrTestMatch() throws Exception {
        final StreamMock stream = getStreamMock("test", OR);
        final StreamRuleMock rule1 = new StreamRuleMock(ImmutableMap.of("_id", new ObjectId(), "field", "testfield1", "type", PRESENCE.toInteger(), "stream_id", stream.getId()));
        final StreamRuleMock rule2 = new StreamRuleMock(ImmutableMap.of("_id", new ObjectId(), "field", "testfield2", "value", "^test", "type", REGEX.toInteger(), "stream_id", stream.getId()));
        stream.setStreamRules(Lists.newArrayList(rule1, rule2));
        final StreamRouterEngine engine = newEngine(Lists.newArrayList(stream));
        // Without testfield1 and testfield2 in the message.
        final Message message1 = getMessage();
        final StreamRouterEngine.StreamTestMatch testMatch1 = engine.testMatch(message1).get(0);
        final Map<StreamRule, Boolean> matches1 = testMatch1.getMatches();
        Assert.assertFalse(testMatch1.isMatched());
        Assert.assertFalse(matches1.get(rule1));
        Assert.assertFalse(matches1.get(rule2));
        // With testfield1 but no-matching testfield2 in the message.
        final Message message2 = getMessage();
        message2.addField("testfield1", "testvalue");
        message2.addField("testfield2", "no-testvalue");
        final StreamRouterEngine.StreamTestMatch testMatch2 = engine.testMatch(message2).get(0);
        final Map<StreamRule, Boolean> matches2 = testMatch2.getMatches();
        Assert.assertTrue(testMatch2.isMatched());
        Assert.assertTrue(matches2.get(rule1));
        Assert.assertFalse(matches2.get(rule2));
        // With testfield1 and matching testfield2 in the message.
        final Message message3 = getMessage();
        message3.addField("testfield1", "testvalue");
        message3.addField("testfield2", "testvalue2");
        final StreamRouterEngine.StreamTestMatch testMatch3 = engine.testMatch(message3).get(0);
        final Map<StreamRule, Boolean> matches3 = testMatch3.getMatches();
        Assert.assertTrue(testMatch3.isMatched());
        Assert.assertTrue(matches3.get(rule1));
        Assert.assertTrue(matches3.get(rule2));
    }

    @Test
    public void testGetFingerprint() {
        final StreamMock stream1 = getStreamMock("test");
        final StreamRuleMock rule1 = new StreamRuleMock(ImmutableMap.of("_id", new ObjectId(), "field", "testfield1", "type", PRESENCE.toInteger(), "stream_id", stream1.getId()));
        final StreamRuleMock rule2 = new StreamRuleMock(ImmutableMap.of("_id", new ObjectId(), "field", "testfield2", "value", "^test", "type", REGEX.toInteger(), "stream_id", stream1.getId()));
        stream1.setStreamRules(Lists.newArrayList(rule1, rule2));
        final StreamMock stream2 = getStreamMock("test");
        final StreamRuleMock rule3 = new StreamRuleMock(ImmutableMap.of("_id", new ObjectId(), "field", "testfield", "value", "^test", "type", REGEX.toInteger(), "stream_id", stream2.getId()));
        stream2.setStreamRules(Lists.newArrayList(rule3));
        final StreamRouterEngine engine1 = newEngine(Lists.newArrayList(stream1));
        final StreamRouterEngine engine2 = newEngine(Lists.newArrayList(stream1));
        final StreamRouterEngine engine3 = newEngine(Lists.newArrayList(stream2));
        Assert.assertEquals(engine1.getFingerprint(), engine2.getFingerprint());
        Assert.assertNotEquals(engine1.getFingerprint(), engine3.getFingerprint());
    }

    @Test
    public void testOrMatching() {
        final String dummyField = "dummyField";
        final String dummyValue = "dummyValue";
        final Stream stream = Mockito.mock(Stream.class);
        Mockito.when(stream.getMatchingType()).thenReturn(OR);
        final StreamRule streamRule1 = getStreamRuleMock("StreamRule1Id", EXACT, dummyField, dummyValue);
        final StreamRule streamRule2 = getStreamRuleMock("StreamRule2Id", EXACT, dummyField, ("not" + dummyValue));
        Mockito.when(stream.getStreamRules()).thenReturn(Lists.newArrayList(streamRule1, streamRule2));
        final Message message = Mockito.mock(Message.class);
        Mockito.when(message.getField(ArgumentMatchers.eq(dummyField))).thenReturn(dummyValue);
        final StreamRouterEngine engine = newEngine(Lists.newArrayList(stream));
        final List<Stream> result = engine.match(message);
        assertThat(result).hasSize(1);
        assertThat(result).contains(stream);
    }

    @Test
    public void testOrMatchingShouldNotMatch() {
        final String dummyField = "dummyField";
        final String dummyValue = "dummyValue";
        final Stream stream = Mockito.mock(Stream.class);
        Mockito.when(stream.getMatchingType()).thenReturn(OR);
        final StreamRule streamRule1 = getStreamRuleMock("StreamRule1Id", EXACT, dummyField, ("not" + dummyValue));
        final StreamRule streamRule2 = getStreamRuleMock("StreamRule2Id", EXACT, dummyField, ("alsoNot" + dummyValue));
        Mockito.when(stream.getStreamRules()).thenReturn(Lists.newArrayList(streamRule1, streamRule2));
        final Message message = Mockito.mock(Message.class);
        Mockito.when(message.getField(ArgumentMatchers.eq(dummyField))).thenReturn(dummyValue);
        final StreamRouterEngine engine = newEngine(Lists.newArrayList(stream));
        final List<Stream> result = engine.match(message);
        assertThat(result).isEmpty();
    }

    @Test
    public void testMultipleStreamWithDifferentMatching() {
        final String dummyField = "dummyField";
        final String dummyValue = "dummyValue";
        final StreamRule streamRule1 = getStreamRuleMock("StreamRule1Id", EXACT, dummyField, dummyValue);
        final StreamRule streamRule2 = getStreamRuleMock("StreamRule2Id", EXACT, dummyField, ("not" + dummyValue));
        final Stream stream1 = Mockito.mock(Stream.class);
        Mockito.when(stream1.getId()).thenReturn("Stream1Id");
        Mockito.when(stream1.getMatchingType()).thenReturn(OR);
        Mockito.when(stream1.getStreamRules()).thenReturn(Lists.newArrayList(streamRule1, streamRule2));
        final Stream stream2 = Mockito.mock(Stream.class);
        Mockito.when(stream2.getId()).thenReturn("Stream2Id");
        Mockito.when(stream2.getMatchingType()).thenReturn(AND);
        Mockito.when(stream2.getStreamRules()).thenReturn(Lists.newArrayList(streamRule1, streamRule2));
        final Message message = Mockito.mock(Message.class);
        Mockito.when(message.getField(ArgumentMatchers.eq(dummyField))).thenReturn(dummyValue);
        final StreamRouterEngine engine = newEngine(Lists.newArrayList(stream1, stream2));
        final List<Stream> result = engine.match(message);
        assertThat(result).hasSize(1);
        assertThat(result).contains(stream1);
        assertThat(result).doesNotContain(stream2);
    }

    @Test
    public void testAndStreamWithMultipleRules() {
        final String dummyField = "dummyField";
        final String dummyValue = "dummyValue";
        final StreamRule streamRule1 = getStreamRuleMock("StreamRule1Id", EXACT, dummyField, dummyValue);
        final StreamRule streamRule2 = getStreamRuleMock("StreamRule2Id", EXACT, dummyField, dummyValue);
        final Stream stream = Mockito.mock(Stream.class);
        Mockito.when(stream.getId()).thenReturn("Stream1Id");
        Mockito.when(stream.getMatchingType()).thenReturn(OR);
        Mockito.when(stream.getStreamRules()).thenReturn(Lists.newArrayList(streamRule1, streamRule2));
        final Message message = Mockito.mock(Message.class);
        Mockito.when(message.getField(ArgumentMatchers.eq(dummyField))).thenReturn(dummyValue);
        final StreamRouterEngine engine = newEngine(Lists.newArrayList(stream));
        final List<Stream> result = engine.match(message);
        assertThat(result).hasSize(1);
        assertThat(result).contains(stream);
    }

    @Test
    public void testEmptyStreamRulesNonMatch() {
        final Stream stream = Mockito.mock(Stream.class);
        Mockito.when(stream.getStreamRules()).thenReturn(Collections.emptyList());
        final Message message = Mockito.mock(Message.class);
        final StreamRouterEngine engine = newEngine(Lists.newArrayList(stream));
        final List<Stream> result = engine.match(message);
        assertThat(result).isEmpty();
        assertThat(result).doesNotContain(stream);
    }

    @Test
    public void issue1396() throws Exception {
        final StreamMock stream = getStreamMock("GitHub issue #1396");
        stream.setMatchingType(AND);
        final StreamRuleMock rule1 = new StreamRuleMock(ImmutableMap.<String, Object>builder().put("_id", new ObjectId()).put("field", "custom1").put("value", "value1").put("type", EXACT.toInteger()).put("inverted", false).put("stream_id", stream.getId()).build());
        final StreamRuleMock rule2 = new StreamRuleMock(ImmutableMap.<String, Object>builder().put("_id", new ObjectId()).put("field", "custom2").put("value", "value2").put("type", EXACT.toInteger()).put("inverted", false).put("stream_id", stream.getId()).build());
        stream.setStreamRules(Lists.newArrayList(rule1, rule2));
        final StreamRouterEngine engine = newEngine(Lists.newArrayList(stream));
        final Message message1 = getMessage();
        message1.addFields(ImmutableMap.of("custom1", "value1"));
        Assert.assertTrue("Message without \"custom2\" should not match conditions", engine.match(message1).isEmpty());
        final Message message2 = getMessage();
        message2.addFields(ImmutableMap.of("custom1", "value1", "custom2", "value2"));
        Assert.assertEquals("Message with \"custom1\" and \"custom2\" should match conditions", Lists.newArrayList(stream), engine.match(message2));
    }
}

