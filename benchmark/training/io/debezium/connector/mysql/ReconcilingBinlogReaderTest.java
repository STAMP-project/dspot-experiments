/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mysql;


import ReconcilingBinlogReader.OffsetLimitPredicate;
import java.util.List;
import java.util.Map;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Moira Tagle
 */
public class ReconcilingBinlogReaderTest {
    @Test
    public void haltAfterPredicateTrue() {
        List<Map<String, ?>> offsets = createOrderedOffsets(2);
        ReconcilingBinlogReader.OffsetLimitPredicate offsetLimitPredicate = new ReconcilingBinlogReader.OffsetLimitPredicate(offsets.get(1), ( x) -> true);
        SourceRecord testSourceRecord = createSourceRecordWithOffset(offsets.get(0));
        // tested record (0) is before limit (1), so we should return true.
        Assert.assertTrue(offsetLimitPredicate.accepts(testSourceRecord));
    }

    @Test
    public void haltAfterPredicateFalse() {
        List<Map<String, ?>> offsets = createOrderedOffsets(2);
        ReconcilingBinlogReader.OffsetLimitPredicate offsetLimitPredicate = new ReconcilingBinlogReader.OffsetLimitPredicate(offsets.get(0), ( x) -> true);
        SourceRecord testSourceRecord = createSourceRecordWithOffset(offsets.get(1));
        // tested record (1) is beyond limit (0), so we should return false.
        Assert.assertFalse(offsetLimitPredicate.accepts(testSourceRecord));
    }

    private final int SERVER_ID = 0;

    private final String BINLOG_FILENAME = "bin.log1";

    private final int STARTING_BINLOG_POSTION = 20;
}

