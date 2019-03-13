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
package org.graylog2.plugin;


import DateTimeZone.UTC;
import Message.FIELD_ID;
import Message.FIELD_MESSAGE;
import Message.FIELD_SOURCE;
import Message.FIELD_STREAMS;
import Message.FIELD_TIMESTAMP;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.eaio.uuid.UUID;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.ThaiBuddhistDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.graylog2.indexer.IndexSet;
import org.graylog2.plugin.streams.Stream;
import org.graylog2.shared.SuppressForbidden;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class MessageTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private Message message;

    private DateTime originalTimestamp;

    private MetricRegistry metricRegistry;

    private Meter invalidTimestampMeter;

    @Test
    public void testAddFieldDoesOnlyAcceptAlphanumericKeys() throws Exception {
        Message m = new Message("foo", "bar", Tools.nowUTC());
        m.addField("some_thing", "bar");
        Assert.assertEquals("bar", m.getField("some_thing"));
        m = new Message("foo", "bar", Tools.nowUTC());
        m.addField("some-thing", "bar");
        Assert.assertEquals("bar", m.getField("some-thing"));
        m = new Message("foo", "bar", Tools.nowUTC());
        m.addField("somethin$g", "bar");
        Assert.assertNull(m.getField("somethin$g"));
        m = new Message("foo", "bar", Tools.nowUTC());
        m.addField("some?thing", "bar");
        Assert.assertNull(m.getField("some?thing"));
    }

    @Test
    public void testAddFieldTrimsValue() throws Exception {
        Message m = new Message("foo", "bar", Tools.nowUTC());
        m.addField("something", " bar ");
        Assert.assertEquals("bar", m.getField("something"));
        m.addField("something2", " bar");
        Assert.assertEquals("bar", m.getField("something2"));
        m.addField("something3", "bar ");
        Assert.assertEquals("bar", m.getField("something3"));
    }

    @Test
    public void testConstructorsTrimValues() throws Exception {
        final Map<String, Object> messageFields = ImmutableMap.of(FIELD_ID, new UUID().toString(), FIELD_MESSAGE, " foo ", FIELD_SOURCE, " bar ", "something", " awesome ", "something_else", " ");
        Message m = new Message(((String) (messageFields.get(FIELD_MESSAGE))), ((String) (messageFields.get(FIELD_SOURCE))), Tools.nowUTC());
        Assert.assertEquals("foo", m.getMessage());
        Assert.assertEquals("bar", m.getSource());
        Message m2 = new Message(messageFields);
        Assert.assertEquals("foo", m2.getMessage());
        Assert.assertEquals("bar", m2.getSource());
        Assert.assertEquals("awesome", m2.getField("something"));
        Assert.assertNull(m2.getField("something_else"));
    }

    @Test
    public void testAddFieldWorksWithIntegers() throws Exception {
        Message m = new Message("foo", "bar", Tools.nowUTC());
        m.addField("something", 3);
        Assert.assertEquals(3, m.getField("something"));
    }

    @Test
    public void testAddFields() throws Exception {
        final Map<String, Object> map = Maps.newHashMap();
        map.put("field1", "Foo");
        map.put("field2", 1);
        message.addFields(map);
        Assert.assertEquals("Foo", message.getField("field1"));
        Assert.assertEquals(1, message.getField("field2"));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testAddStringFields() throws Exception {
        final Map<String, String> map = Maps.newHashMap();
        map.put("field1", "Foo");
        map.put("field2", "Bar");
        message.addStringFields(map);
        Assert.assertEquals("Foo", message.getField("field1"));
        Assert.assertEquals("Bar", message.getField("field2"));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testAddLongFields() throws Exception {
        final Map<String, Long> map = Maps.newHashMap();
        map.put("field1", 10L);
        map.put("field2", 230L);
        message.addLongFields(map);
        Assert.assertEquals(10L, message.getField("field1"));
        Assert.assertEquals(230L, message.getField("field2"));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testAddDoubleFields() throws Exception {
        final Map<String, Double> map = Maps.newHashMap();
        map.put("field1", 10.0);
        map.put("field2", 230.2);
        message.addDoubleFields(map);
        Assert.assertEquals(10.0, message.getField("field1"));
        Assert.assertEquals(230.2, message.getField("field2"));
    }

    @Test
    public void testRemoveField() throws Exception {
        message.addField("foo", "bar");
        message.removeField("foo");
        Assert.assertNull(message.getField("foo"));
    }

    @Test
    public void testRemoveFieldNotDeletingReservedFields() throws Exception {
        message.removeField("message");
        message.removeField("source");
        message.removeField("timestamp");
        Assert.assertNotNull(message.getField("message"));
        Assert.assertNotNull(message.getField("source"));
        Assert.assertNotNull(message.getField("timestamp"));
    }

    @Test
    public void testGetFieldAs() throws Exception {
        message.addField("fields", Lists.newArrayList("hello"));
        Assert.assertEquals(Lists.newArrayList("hello"), message.getFieldAs(List.class, "fields"));
    }

    @Test(expected = ClassCastException.class)
    public void testGetFieldAsWithIncompatibleCast() throws Exception {
        message.addField("fields", Lists.newArrayList("hello"));
        message.getFieldAs(Map.class, "fields");
    }

    @Test
    public void testSetAndGetStreams() throws Exception {
        final Stream stream1 = Mockito.mock(Stream.class);
        final Stream stream2 = Mockito.mock(Stream.class);
        message.addStreams(Lists.newArrayList(stream2, stream1));
        // make sure all streams we've added are being returned. Internally it's a set, so don't check the order, it doesn't matter anyway.
        assertThat(message.getStreams()).containsOnly(stream1, stream2);
    }

    @Test
    public void testStreamMutators() {
        final Stream stream1 = Mockito.mock(Stream.class);
        final Stream stream2 = Mockito.mock(Stream.class);
        final Stream stream3 = Mockito.mock(Stream.class);
        assertThat(message.getStreams()).isNotNull();
        assertThat(message.getStreams()).isEmpty();
        message.addStream(stream1);
        final Set<Stream> onlyWithStream1 = message.getStreams();
        assertThat(onlyWithStream1).containsOnly(stream1);
        message.addStreams(Sets.newHashSet(stream3, stream2));
        assertThat(message.getStreams()).containsOnly(stream1, stream2, stream3);
        // getStreams is a copy and doesn't change after mutations
        assertThat(onlyWithStream1).containsOnly(stream1);
        // stream2 was assigned
        assertThat(message.removeStream(stream2)).isTrue();
        // streams2 is no longer assigned
        assertThat(message.removeStream(stream2)).isFalse();
        assertThat(message.getStreams()).containsOnly(stream1, stream3);
    }

    @Test
    public void testStreamMutatorsWithIndexSets() {
        final Stream stream1 = Mockito.mock(Stream.class);
        final Stream stream2 = Mockito.mock(Stream.class);
        final Stream stream3 = Mockito.mock(Stream.class);
        final IndexSet indexSet1 = Mockito.mock(IndexSet.class);
        final IndexSet indexSet2 = Mockito.mock(IndexSet.class);
        assertThat(message.getIndexSets()).isEmpty();
        Mockito.when(stream1.getIndexSet()).thenReturn(indexSet1);
        Mockito.when(stream2.getIndexSet()).thenReturn(indexSet1);
        Mockito.when(stream3.getIndexSet()).thenReturn(indexSet2);
        message.addStream(stream1);
        message.addStreams(Sets.newHashSet(stream2, stream3));
        assertThat(message.getIndexSets()).containsOnly(indexSet1, indexSet2);
        message.removeStream(stream3);
        assertThat(message.getIndexSets()).containsOnly(indexSet1);
        final Set<IndexSet> indexSets = message.getIndexSets();
        message.addStream(stream3);
        // getIndexSets is a copy and doesn't change after mutations
        assertThat(indexSets).containsOnly(indexSet1);
    }

    @Test
    public void testGetStreamIds() throws Exception {
        message.addField("streams", Lists.newArrayList("stream-id"));
        assertThat(message.getStreamIds()).containsOnly("stream-id");
    }

    @Test
    public void testGetAndSetFilterOut() throws Exception {
        Assert.assertFalse(message.getFilterOut());
        message.setFilterOut(true);
        Assert.assertTrue(message.getFilterOut());
        message.setFilterOut(false);
        Assert.assertFalse(message.getFilterOut());
    }

    @Test
    public void testGetId() throws Exception {
        final Pattern pattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        Assert.assertTrue(pattern.matcher(message.getId()).matches());
    }

    @Test
    public void testGetTimestamp() {
        try {
            final DateTime timestamp = message.getTimestamp();
            Assert.assertNotNull(timestamp);
            Assert.assertEquals(originalTimestamp.getZone(), timestamp.getZone());
        } catch (ClassCastException e) {
            Assert.fail(("timestamp wasn't a DateTime " + (e.getMessage())));
        }
    }

    @Test
    public void testTimestampAsDate() {
        final DateTime dateTime = new DateTime(2015, 9, 8, 0, 0, DateTimeZone.UTC);
        message.addField(FIELD_TIMESTAMP, dateTime.toDate());
        final Map<String, Object> elasticSearchObject = message.toElasticSearchObject(invalidTimestampMeter);
        final Object esTimestampFormatted = elasticSearchObject.get(FIELD_TIMESTAMP);
        Assert.assertEquals("Setting message timestamp as java.util.Date results in correct format for elasticsearch", Tools.buildElasticSearchTimeFormat(dateTime), esTimestampFormatted);
    }

    @Test
    public void testGetMessage() throws Exception {
        Assert.assertEquals("foo", message.getMessage());
    }

    @Test
    public void testGetSource() throws Exception {
        Assert.assertEquals("bar", message.getSource());
    }

    @Test
    public void testValidKeys() throws Exception {
        Assert.assertTrue(Message.validKey("foo123"));
        Assert.assertTrue(Message.validKey("foo-bar123"));
        Assert.assertTrue(Message.validKey("foo_bar123"));
        Assert.assertTrue(Message.validKey("foo.bar123"));
        Assert.assertTrue(Message.validKey("foo@bar"));
        Assert.assertTrue(Message.validKey("123"));
        Assert.assertTrue(Message.validKey(""));
        Assert.assertFalse(Message.validKey("foo bar"));
        Assert.assertFalse(Message.validKey("foo+bar"));
        Assert.assertFalse(Message.validKey("foo$bar"));
        Assert.assertFalse(Message.validKey(" "));
    }

    @Test
    public void testToElasticSearchObject() throws Exception {
        message.addField("field1", "wat");
        message.addField("field2", "that");
        message.addField(FIELD_STREAMS, Collections.singletonList("test-stream"));
        final Map<String, Object> object = message.toElasticSearchObject(invalidTimestampMeter);
        Assert.assertEquals("foo", object.get("message"));
        Assert.assertEquals("bar", object.get("source"));
        Assert.assertEquals("wat", object.get("field1"));
        Assert.assertEquals("that", object.get("field2"));
        Assert.assertEquals(Tools.buildElasticSearchTimeFormat(((DateTime) (message.getField("timestamp")))), object.get("timestamp"));
        @SuppressWarnings("unchecked")
        final Collection<String> streams = ((Collection<String>) (object.get("streams")));
        assertThat(streams).containsOnly("test-stream");
    }

    @Test
    public void testToElasticSearchObjectWithInvalidKey() throws Exception {
        message.addField("field.3", "dot");
        final Map<String, Object> object = message.toElasticSearchObject(invalidTimestampMeter);
        // Elasticsearch >=2.0 does not allow "." in keys. Make sure we replace them before writing the message.
        Assert.assertEquals("#toElasticsearchObject() should replace \".\" in keys with a \"_\"", "dot", object.get("field_3"));
        Assert.assertEquals("foo", object.get("message"));
        Assert.assertEquals("bar", object.get("source"));
        Assert.assertEquals(Tools.buildElasticSearchTimeFormat(((DateTime) (message.getField("timestamp")))), object.get("timestamp"));
        @SuppressWarnings("unchecked")
        final Collection<String> streams = ((Collection<String>) (object.get("streams")));
        assertThat(streams).isEmpty();
    }

    @Test
    public void testToElasticSearchObjectWithoutDateTimeTimestamp() throws Exception {
        message.addField("timestamp", "time!");
        final Meter errorMeter = metricRegistry.meter("test-meter");
        final Map<String, Object> object = message.toElasticSearchObject(errorMeter);
        Assert.assertNotEquals("time!", object.get("timestamp"));
        Assert.assertEquals(1, errorMeter.getCount());
    }

    @Test
    public void testToElasticSearchObjectWithStreams() throws Exception {
        final Stream stream = Mockito.mock(Stream.class);
        Mockito.when(stream.getId()).thenReturn("stream-id");
        Mockito.when(stream.getIndexSet()).thenReturn(Mockito.mock(IndexSet.class));
        message.addStream(stream);
        final Map<String, Object> object = message.toElasticSearchObject(invalidTimestampMeter);
        @SuppressWarnings("unchecked")
        final Collection<String> streams = ((Collection<String>) (object.get("streams")));
        assertThat(streams).containsOnly("stream-id");
    }

    @Test
    public void messageSizes() {
        final Meter invalidTimestampMeter = new Meter();
        final Message message = new Message("1234567890", "12345", Tools.nowUTC());
        assertThat(message.getSize()).isEqualTo(45);
        final Stream defaultStream = Mockito.mock(Stream.class);
        Mockito.when(defaultStream.getId()).thenReturn(Stream.DEFAULT_STREAM_ID);
        message.addStream(defaultStream);
        assertThat(message.getSize()).isEqualTo(53);
    }

    @Test
    public void testIsComplete() throws Exception {
        Message message = new Message("message", "source", Tools.nowUTC());
        Assert.assertTrue(message.isComplete());
        message = new Message("message", "", Tools.nowUTC());
        Assert.assertTrue(message.isComplete());
        message = new Message("message", null, Tools.nowUTC());
        Assert.assertTrue(message.isComplete());
        message = new Message("", "source", Tools.nowUTC());
        Assert.assertFalse(message.isComplete());
        message = new Message(null, "source", Tools.nowUTC());
        Assert.assertFalse(message.isComplete());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testGetValidationErrorsWithEmptyMessage() throws Exception {
        final Message message = new Message("", "source", Tools.nowUTC());
        Assert.assertEquals("message is empty, ", message.getValidationErrors());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testGetValidationErrorsWithNullMessage() throws Exception {
        final Message message = new Message(null, "source", Tools.nowUTC());
        Assert.assertEquals("message is missing, ", message.getValidationErrors());
    }

    @Test
    public void testGetFields() throws Exception {
        final Map<String, Object> fields = message.getFields();
        Assert.assertEquals(message.getId(), fields.get("_id"));
        Assert.assertEquals(message.getMessage(), fields.get("message"));
        Assert.assertEquals(message.getSource(), fields.get("source"));
        Assert.assertEquals(message.getField("timestamp"), fields.get("timestamp"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetFieldsReturnsImmutableMap() throws Exception {
        final Map<String, Object> fields = message.getFields();
        fields.put("foo", "bar");
    }

    @Test
    public void testGetFieldNames() throws Exception {
        Assert.assertTrue("Missing fields in set!", Sets.symmetricDifference(message.getFieldNames(), Sets.newHashSet("_id", "timestamp", "source", "message")).isEmpty());
        message.addField("testfield", "testvalue");
        Assert.assertTrue("Missing fields in set!", Sets.symmetricDifference(message.getFieldNames(), Sets.newHashSet("_id", "timestamp", "source", "message", "testfield")).isEmpty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetFieldNamesReturnsUnmodifiableSet() throws Exception {
        final Set<String> fieldNames = message.getFieldNames();
        fieldNames.remove("_id");
    }

    @Test
    public void testHasField() throws Exception {
        Assert.assertFalse(message.hasField("__foo__"));
        message.addField("__foo__", "bar");
        Assert.assertTrue(message.hasField("__foo__"));
    }

    @Test
    public void testDateConvertedToDateTime() {
        final Message message = new Message("", "source", Tools.nowUTC());
        final Date dateObject = DateTime.parse("2010-07-30T16:03:25Z").toDate();
        message.addField(FIELD_TIMESTAMP, dateObject);
        Assert.assertEquals(message.getTimestamp().toDate(), dateObject);
        Assert.assertEquals(message.getField(FIELD_TIMESTAMP).getClass(), DateTime.class);
    }

    @Test
    public void getStreamIdsReturnsStreamsIdsIfFieldDoesNotExist() {
        final Message message = new Message("", "source", Tools.nowUTC());
        final Stream stream = Mockito.mock(Stream.class);
        Mockito.when(stream.getId()).thenReturn("test");
        message.addStream(stream);
        assertThat(message.getStreamIds()).containsOnly("test");
    }

    @Test
    public void getStreamIdsReturnsStreamsFieldContentsIfFieldDoesExist() {
        final Message message = new Message("", "source", Tools.nowUTC());
        final Stream stream = Mockito.mock(Stream.class);
        Mockito.when(stream.getId()).thenReturn("test1");
        message.addField("streams", Collections.singletonList("test2"));
        message.addStream(stream);
        assertThat(message.getStreamIds()).containsOnly("test1", "test2");
    }

    @Test
    public void fieldTest() {
        assertThat(Message.sizeForField("", true)).isEqualTo(4);
        assertThat(Message.sizeForField("", ((byte) (1)))).isEqualTo(1);
        assertThat(Message.sizeForField("", ((char) (1)))).isEqualTo(2);
        assertThat(Message.sizeForField("", ((short) (1)))).isEqualTo(2);
        assertThat(Message.sizeForField("", 1)).isEqualTo(4);
        assertThat(Message.sizeForField("", 1L)).isEqualTo(8);
        assertThat(Message.sizeForField("", 1.0F)).isEqualTo(4);
        assertThat(Message.sizeForField("", 1.0)).isEqualTo(8);
    }

    @Test
    public void assignZonedDateTimeAsTimestamp() {
        final Message message = new Message("message", "source", Tools.nowUTC());
        message.addField(FIELD_TIMESTAMP, ZonedDateTime.of(2018, 4, 19, 12, 0, 0, 0, ZoneOffset.UTC));
        assertThat(message.getTimestamp()).isEqualTo(new DateTime(2018, 4, 19, 12, 0, 0, 0, DateTimeZone.UTC));
    }

    @Test
    public void assignOffsetDateTimeAsTimestamp() {
        final Message message = new Message("message", "source", Tools.nowUTC());
        message.addField(FIELD_TIMESTAMP, OffsetDateTime.of(2018, 4, 19, 12, 0, 0, 0, ZoneOffset.UTC));
        assertThat(message.getTimestamp()).isEqualTo(new DateTime(2018, 4, 19, 12, 0, 0, 0, DateTimeZone.UTC));
    }

    @Test
    @SuppressForbidden("Intentionally using system default time zone")
    public void assignLocalDateTimeAsTimestamp() {
        final Message message = new Message("message", "source", Tools.nowUTC());
        message.addField(FIELD_TIMESTAMP, LocalDateTime.of(2018, 4, 19, 12, 0, 0, 0));
        final DateTimeZone defaultTimeZone = DateTimeZone.getDefault();
        assertThat(message.getTimestamp()).isEqualTo(new DateTime(2018, 4, 19, 12, 0, 0, 0, defaultTimeZone).withZone(UTC));
    }

    @Test
    @SuppressForbidden("Intentionally using system default time zone")
    public void assignLocalDateAsTimestamp() {
        final Message message = new Message("message", "source", Tools.nowUTC());
        message.addField(FIELD_TIMESTAMP, LocalDate.of(2018, 4, 19));
        final DateTimeZone defaultTimeZone = DateTimeZone.getDefault();
        assertThat(message.getTimestamp()).isEqualTo(new DateTime(2018, 4, 19, 0, 0, 0, 0, defaultTimeZone).withZone(UTC));
    }

    @Test
    public void assignInstantAsTimestamp() {
        final Message message = new Message("message", "source", Tools.nowUTC());
        message.addField(FIELD_TIMESTAMP, Instant.ofEpochMilli(1524139200000L));
        assertThat(message.getTimestamp()).isEqualTo(new DateTime(2018, 4, 19, 12, 0, 0, 0, DateTimeZone.UTC));
    }

    @Test
    public void assignUnsupportedTemporalTypeAsTimestamp() {
        final Message message = new Message("message", "source", Tools.nowUTC());
        message.addField(FIELD_TIMESTAMP, ThaiBuddhistDate.of(0, 4, 19));
        assertThat(message.getTimestamp()).isGreaterThan(new DateTime(2018, 4, 19, 0, 0, 0, 0, DateTimeZone.UTC));
    }
}

