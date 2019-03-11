package org.kairosdb.datastore.cassandra;


import LegacyDataPointFactory.DATASTORE_TYPE;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.SortedMap;
import java.util.TreeMap;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.datapoints.LegacyDataPointFactory;


public class DataPointsRowKeySerializerTest {
    public static final Charset UTF8 = Charset.forName("UTF-8");

    @Test
    public void test_toByteBuffer_oldFormat() {
        String metricName = "my.gnarly.metric";
        long now = System.currentTimeMillis();
        // Build old row key buffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(metricName.getBytes(DataPointsRowKeySerializerTest.UTF8));// Metric name is put in this way for sorting purposes

        buffer.put(((byte) (0)));
        buffer.putLong(now);
        buffer.put("host=myhost:".getBytes(DataPointsRowKeySerializerTest.UTF8));
        buffer.flip();
        DataPointsRowKeySerializer serializer = new DataPointsRowKeySerializer();
        DataPointsRowKey rowKey = serializer.fromByteBuffer(buffer, "default");
        Assert.assertThat(rowKey.getMetricName(), CoreMatchers.equalTo(metricName));
        Assert.assertThat(rowKey.getDataType(), CoreMatchers.equalTo(DATASTORE_TYPE));
        Assert.assertThat(rowKey.getTimestamp(), CoreMatchers.equalTo(now));
    }

    @Test
    public void test_toByteBuffer_legacyType() {
        SortedMap<String, String> map = new TreeMap<>();
        map.put("a", "b");
        DataPointsRowKeySerializer serializer = new DataPointsRowKeySerializer();
        ByteBuffer buffer = serializer.toByteBuffer(new DataPointsRowKey("myMetric", "default", 12345L, LegacyDataPointFactory.DATASTORE_TYPE, map));
        Assert.assertThat(buffer.remaining(), CoreMatchers.equalTo(21));// This should be the size of the legacy buffer

        DataPointsRowKey rowKey = serializer.fromByteBuffer(buffer, "default");
        Assert.assertThat(rowKey.getMetricName(), CoreMatchers.equalTo("myMetric"));
        Assert.assertThat(rowKey.getDataType(), CoreMatchers.equalTo(DATASTORE_TYPE));
        Assert.assertThat(rowKey.getTimestamp(), CoreMatchers.equalTo(12345L));
    }

    @Test
    public void test_toByteBuffer_newFormat() {
        SortedMap<String, String> map = new TreeMap<>();
        map.put("a", "b");
        map.put("c", "d");
        map.put("e", "f");
        DataPointsRowKeySerializer serializer = new DataPointsRowKeySerializer();
        ByteBuffer buffer = serializer.toByteBuffer(new DataPointsRowKey("myMetric", "default", 12345L, "myDataType", map));
        DataPointsRowKey rowKey = serializer.fromByteBuffer(buffer, "default");
        Assert.assertThat(rowKey.getMetricName(), CoreMatchers.equalTo("myMetric"));
        Assert.assertThat(rowKey.getDataType(), CoreMatchers.equalTo("myDataType"));
        Assert.assertThat(rowKey.getTimestamp(), CoreMatchers.equalTo(12345L));
        Assert.assertThat(rowKey.getTags().size(), CoreMatchers.equalTo(3));
        Assert.assertThat(rowKey.getTags().get("a"), CoreMatchers.equalTo("b"));
        Assert.assertThat(rowKey.getTags().get("c"), CoreMatchers.equalTo("d"));
        Assert.assertThat(rowKey.getTags().get("e"), CoreMatchers.equalTo("f"));
    }

    @Test
    public void test_toByteBuffer_tagsWithColonEquals() {
        SortedMap<String, String> map = new TreeMap<>();
        map.put("a:a", "b:b");
        map.put("c=c", "d=d");
        map.put(":e", "f\\");
        map.put("=a=", "===");
        map.put(":a:", ":::");
        map.put("=b=", ":::");
        map.put(":b:", "===");
        map.put("=c=", "normal");
        DataPointsRowKeySerializer serializer = new DataPointsRowKeySerializer();
        ByteBuffer buffer = serializer.toByteBuffer(new DataPointsRowKey("myMetric", "default", 12345L, "myDataType", map));
        DataPointsRowKey rowKey = serializer.fromByteBuffer(buffer, "default");
        Assert.assertThat(rowKey.getMetricName(), CoreMatchers.equalTo("myMetric"));
        Assert.assertThat(rowKey.getDataType(), CoreMatchers.equalTo("myDataType"));
        Assert.assertThat(rowKey.getTimestamp(), CoreMatchers.equalTo(12345L));
        Assert.assertThat(rowKey.getTags().size(), CoreMatchers.equalTo(8));
        Assert.assertThat(rowKey.getTags().get("a:a"), CoreMatchers.equalTo("b:b"));
        Assert.assertThat(rowKey.getTags().get("c=c"), CoreMatchers.equalTo("d=d"));
        Assert.assertThat(rowKey.getTags().get(":e"), CoreMatchers.equalTo("f\\"));
        Assert.assertThat(rowKey.getTags().get("=a="), CoreMatchers.equalTo("==="));
        Assert.assertThat(rowKey.getTags().get(":a:"), CoreMatchers.equalTo(":::"));
        Assert.assertThat(rowKey.getTags().get("=b="), CoreMatchers.equalTo(":::"));
        Assert.assertThat(rowKey.getTags().get(":b:"), CoreMatchers.equalTo("==="));
        Assert.assertThat(rowKey.getTags().get("=c="), CoreMatchers.equalTo("normal"));
    }
}

