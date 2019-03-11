package com.baeldung.flink;


import TimeCharacteristic.EventTime;
import com.baeldung.flink.model.Backup;
import com.baeldung.flink.model.InputMessage;
import com.baeldung.flink.operator.BackupAggregator;
import com.baeldung.flink.operator.InputMessageTimestampAssigner;
import com.baeldung.flink.schema.BackupSerializationSchema;
import com.baeldung.flink.schema.InputMessageDeserializationSchema;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.collections.ListUtils;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Test;


public class BackupCreatorIntegrationTest {
    public static ObjectMapper mapper;

    @Test
    public void givenProperJson_whenDeserializeIsInvoked_thenProperObjectIsReturned() throws IOException {
        InputMessage message = new InputMessage("Me", "User", LocalDateTime.now(), "Test Message");
        byte[] messageSerialized = BackupCreatorIntegrationTest.mapper.writeValueAsBytes(message);
        DeserializationSchema<InputMessage> deserializationSchema = new InputMessageDeserializationSchema();
        InputMessage messageDeserialized = deserializationSchema.deserialize(messageSerialized);
        Assert.assertEquals(message, messageDeserialized);
    }

    @Test
    public void givenMultipleInputMessagesFromDifferentDays_whenBackupCreatorIsUser_thenMessagesAreGroupedProperly() throws Exception {
        LocalDateTime currentTime = LocalDateTime.now();
        InputMessage message = new InputMessage("Me", "User", currentTime, "First TestMessage");
        InputMessage secondMessage = new InputMessage("Me", "User", currentTime.plusHours(1), "First TestMessage");
        InputMessage thirdMessage = new InputMessage("Me", "User", currentTime.plusHours(2), "First TestMessage");
        InputMessage fourthMessage = new InputMessage("Me", "User", currentTime.plusHours(3), "First TestMessage");
        InputMessage fifthMessage = new InputMessage("Me", "User", currentTime.plusHours(25), "First TestMessage");
        InputMessage sixthMessage = new InputMessage("Me", "User", currentTime.plusHours(26), "First TestMessage");
        List<InputMessage> firstBackupMessages = Arrays.asList(message, secondMessage, thirdMessage, fourthMessage);
        List<InputMessage> secondBackupMessages = Arrays.asList(fifthMessage, sixthMessage);
        List<InputMessage> inputMessages = ListUtils.union(firstBackupMessages, secondBackupMessages);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(EventTime);
        env.setParallelism(1);
        DataStreamSource<InputMessage> testDataSet = env.fromCollection(inputMessages);
        BackupCreatorIntegrationTest.CollectingSink sink = new BackupCreatorIntegrationTest.CollectingSink();
        testDataSet.assignTimestampsAndWatermarks(new InputMessageTimestampAssigner()).timeWindowAll(Time.hours(24)).aggregate(new BackupAggregator()).addSink(sink);
        env.execute();
        Awaitility.await().until(() -> (sink.backups.size()) == 2);
        Assert.assertEquals(2, sink.backups.size());
        Assert.assertEquals(firstBackupMessages, sink.backups.get(0).getInputMessages());
        Assert.assertEquals(secondBackupMessages, sink.backups.get(1).getInputMessages());
    }

    @Test
    public void givenProperBackupObject_whenSerializeIsInvoked_thenObjectIsProperlySerialized() throws IOException {
        InputMessage message = new InputMessage("Me", "User", LocalDateTime.now(), "Test Message");
        List<InputMessage> messages = Arrays.asList(message);
        Backup backup = new Backup(messages, LocalDateTime.now());
        byte[] backupSerialized = BackupCreatorIntegrationTest.mapper.writeValueAsBytes(backup);
        SerializationSchema<Backup> serializationSchema = new BackupSerializationSchema();
        byte[] backupProcessed = serializationSchema.serialize(backup);
        Assert.assertEquals(backupSerialized, backupProcessed);
    }

    private static class CollectingSink implements SinkFunction<Backup> {
        public static List<Backup> backups = new ArrayList<>();

        @Override
        public synchronized void invoke(Backup value, Context context) throws Exception {
            BackupCreatorIntegrationTest.CollectingSink.backups.add(value);
        }
    }
}

