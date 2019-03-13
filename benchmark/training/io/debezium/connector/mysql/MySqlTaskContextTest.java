/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mysql;


import GtidSet.UUIDSet;
import MySqlConnectorConfig.ALL_FIELDS;
import MySqlConnectorConfig.SNAPSHOT_MODE;
import SnapshotMode.NEVER;
import SnapshotMode.WHEN_NEEDED;
import Testing.Files;
import io.debezium.config.Configuration;
import io.debezium.doc.FixFor;
import io.debezium.relational.history.HistoryRecord;
import io.debezium.relational.history.HistoryRecordComparator;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Predicate;
import org.junit.Test;


/**
 *
 *
 * @author Randall Hauch
 */
public class MySqlTaskContextTest {
    protected static final Path DB_HISTORY_PATH = Files.createTestingPath("file-db-history-context.txt").toAbsolutePath();

    protected String hostname;

    protected int port;

    protected String username;

    protected String password;

    protected int serverId;

    protected String serverName;

    protected String databaseName;

    protected Configuration config;

    protected MySqlTaskContext context;

    @Test
    public void shouldCreateTaskFromConfigurationWithNeverSnapshotMode() throws Exception {
        config = build();
        context = new MySqlTaskContext(config, build(), false, null);
        context.start();
        assertThat(("" + (context.snapshotMode().getValue()))).isEqualTo(NEVER.getValue());
        assertThat(context.isSnapshotAllowedWhenNeeded()).isEqualTo(false);
        assertThat(context.isSnapshotNeverAllowed()).isEqualTo(true);
    }

    @Test
    public void shouldCreateTaskFromConfigurationWithWhenNeededSnapshotMode() throws Exception {
        config = build();
        context = new MySqlTaskContext(config, build(), false, null);
        context.start();
        assertThat(("" + (context.snapshotMode().getValue()))).isEqualTo(WHEN_NEEDED.getValue());
        assertThat(context.isSnapshotAllowedWhenNeeded()).isEqualTo(true);
        assertThat(context.isSnapshotNeverAllowed()).isEqualTo(false);
    }

    @Test
    public void shouldUseGtidSetIncludes() throws Exception {
        config = build();
        context = new MySqlTaskContext(config, build(), false, null);
        context.start();
        Predicate<String> filter = context.gtidSourceFilter();
        assertThat(filter).isNotNull();
        assertThat(filter.test("a")).isTrue();
        assertThat(filter.test("b")).isTrue();
        assertThat(filter.test("c")).isTrue();
        assertThat(filter.test("d")).isTrue();
        assertThat(filter.test("d1")).isTrue();
        assertThat(filter.test("d2")).isTrue();
        assertThat(filter.test("d1234xdgfe")).isTrue();
        assertThat(filter.test("a1")).isFalse();
        assertThat(filter.test("a2")).isFalse();
        assertThat(filter.test("b1")).isFalse();
        assertThat(filter.test("c1")).isFalse();
        assertThat(filter.test("e")).isFalse();
    }

    @Test
    public void shouldUseGtidSetIncludesLiteralUuids() throws Exception {
        String gtidStr = "036d85a9-64e5-11e6-9b48-42010af0000c:1-2," + ("7145bf69-d1ca-11e5-a588-0242ac110004:1-3200," + "7c1de3f2-3fd2-11e6-9cdc-42010af000bc:1-41");
        config = build();
        context = new MySqlTaskContext(config, build(), false, null);
        context.start();
        Predicate<String> filter = context.gtidSourceFilter();
        assertThat(filter).isNotNull();
        assertThat(filter.test("036d85a9-64e5-11e6-9b48-42010af0000c")).isTrue();
        assertThat(filter.test("7145bf69-d1ca-11e5-a588-0242ac110004")).isTrue();
        assertThat(filter.test("036d85a9-64e5-11e6-9b48-42010af0000c-extra")).isFalse();
        assertThat(filter.test("7145bf69-d1ca-11e5-a588-0242ac110004-extra")).isFalse();
        assertThat(filter.test("7c1de3f2-3fd2-11e6-9cdc-42010af000bc")).isFalse();
        GtidSet original = new GtidSet(gtidStr);
        assertThat(original.forServerWithId("036d85a9-64e5-11e6-9b48-42010af0000c")).isNotNull();
        assertThat(original.forServerWithId("7c1de3f2-3fd2-11e6-9cdc-42010af000bc")).isNotNull();
        assertThat(original.forServerWithId("7145bf69-d1ca-11e5-a588-0242ac110004")).isNotNull();
        GtidSet filtered = original.retainAll(filter);
        assertThat(filtered.forServerWithId("036d85a9-64e5-11e6-9b48-42010af0000c")).isNotNull();
        assertThat(filtered.forServerWithId("7c1de3f2-3fd2-11e6-9cdc-42010af000bc")).isNull();
        assertThat(filtered.forServerWithId("7145bf69-d1ca-11e5-a588-0242ac110004")).isNotNull();
    }

    @Test
    public void shouldUseGtidSetxcludesLiteralUuids() throws Exception {
        String gtidStr = "036d85a9-64e5-11e6-9b48-42010af0000c:1-2," + ("7145bf69-d1ca-11e5-a588-0242ac110004:1-3200," + "7c1de3f2-3fd2-11e6-9cdc-42010af000bc:1-41");
        config = build();
        context = new MySqlTaskContext(config, build(), false, null);
        context.start();
        Predicate<String> filter = context.gtidSourceFilter();
        assertThat(filter).isNotNull();
        assertThat(filter.test("036d85a9-64e5-11e6-9b48-42010af0000c")).isTrue();
        assertThat(filter.test("7145bf69-d1ca-11e5-a588-0242ac110004")).isTrue();
        assertThat(filter.test("036d85a9-64e5-11e6-9b48-42010af0000c-extra")).isTrue();
        assertThat(filter.test("7145bf69-d1ca-11e5-a588-0242ac110004-extra")).isTrue();
        assertThat(filter.test("7c1de3f2-3fd2-11e6-9cdc-42010af000bc")).isFalse();
        GtidSet original = new GtidSet(gtidStr);
        assertThat(original.forServerWithId("036d85a9-64e5-11e6-9b48-42010af0000c")).isNotNull();
        assertThat(original.forServerWithId("7c1de3f2-3fd2-11e6-9cdc-42010af000bc")).isNotNull();
        assertThat(original.forServerWithId("7145bf69-d1ca-11e5-a588-0242ac110004")).isNotNull();
        GtidSet filtered = original.retainAll(filter);
        assertThat(filtered.forServerWithId("036d85a9-64e5-11e6-9b48-42010af0000c")).isNotNull();
        assertThat(filtered.forServerWithId("7c1de3f2-3fd2-11e6-9cdc-42010af000bc")).isNull();
        assertThat(filtered.forServerWithId("7145bf69-d1ca-11e5-a588-0242ac110004")).isNotNull();
    }

    @Test
    public void shouldNotAllowBothGtidSetIncludesAndExcludes() throws Exception {
        config = build();
        context = new MySqlTaskContext(config, build(), false, null);
        boolean valid = config.validateAndRecord(ALL_FIELDS, ( msg) -> {
        });
        assertThat(valid).isFalse();
    }

    @Test
    public void shouldFilterAndMergeGtidSet() throws Exception {
        String gtidStr = "036d85a9-64e5-11e6-9b48-42010af0000c:1-2," + "7c1de3f2-3fd2-11e6-9cdc-42010af000bc:5-41";
        String availableServerGtidStr = "036d85a9-64e5-11e6-9b48-42010af0000c:1-20," + ("7145bf69-d1ca-11e5-a588-0242ac110004:1-3200," + "123e4567-e89b-12d3-a456-426655440000:1-41");
        String purgedServerGtidStr = "";
        config = build();
        context = new MySqlTaskContext(config, build(), false, null);
        context.start();
        context.source().setCompletedGtidSet(gtidStr);
        GtidSet mergedGtidSet = context.filterGtidSet(new GtidSet(availableServerGtidStr), new GtidSet(purgedServerGtidStr));
        assertThat(mergedGtidSet).isNotNull();
        GtidSet.UUIDSet uuidSet1 = mergedGtidSet.forServerWithId("036d85a9-64e5-11e6-9b48-42010af0000c");
        GtidSet.UUIDSet uuidSet2 = mergedGtidSet.forServerWithId("7145bf69-d1ca-11e5-a588-0242ac110004");
        GtidSet.UUIDSet uuidSet3 = mergedGtidSet.forServerWithId("123e4567-e89b-12d3-a456-426655440000");
        GtidSet.UUIDSet uuidSet4 = mergedGtidSet.forServerWithId("7c1de3f2-3fd2-11e6-9cdc-42010af000bc");
        assertThat(uuidSet1.getIntervals()).isEqualTo(Arrays.asList(new GtidSet.Interval(1, 2)));
        assertThat(uuidSet2.getIntervals()).isEqualTo(Arrays.asList(new GtidSet.Interval(1, 3200)));
        assertThat(uuidSet3.getIntervals()).isEqualTo(Arrays.asList(new GtidSet.Interval(1, 41)));
        assertThat(uuidSet4).isNull();
    }

    @Test
    @FixFor("DBZ-923")
    public void shouldMergeToFirstAvailableGtidSetPositions() throws Exception {
        String gtidStr = "036d85a9-64e5-11e6-9b48-42010af0000c:1-2," + "7c1de3f2-3fd2-11e6-9cdc-42010af000bc:5-41";
        String availableServerGtidStr = "036d85a9-64e5-11e6-9b48-42010af0000c:1-20," + ("7145bf69-d1ca-11e5-a588-0242ac110004:1-3200," + "123e4567-e89b-12d3-a456-426655440000:1-41");
        String purgedServerGtidStr = "7145bf69-d1ca-11e5-a588-0242ac110004:1-1234";
        config = build();
        context = new MySqlTaskContext(config, build(), false, null);
        context.start();
        context.source().setCompletedGtidSet(gtidStr);
        GtidSet mergedGtidSet = context.filterGtidSet(new GtidSet(availableServerGtidStr), new GtidSet(purgedServerGtidStr));
        assertThat(mergedGtidSet).isNotNull();
        GtidSet.UUIDSet uuidSet1 = mergedGtidSet.forServerWithId("036d85a9-64e5-11e6-9b48-42010af0000c");
        GtidSet.UUIDSet uuidSet2 = mergedGtidSet.forServerWithId("7145bf69-d1ca-11e5-a588-0242ac110004");
        GtidSet.UUIDSet uuidSet3 = mergedGtidSet.forServerWithId("123e4567-e89b-12d3-a456-426655440000");
        GtidSet.UUIDSet uuidSet4 = mergedGtidSet.forServerWithId("7c1de3f2-3fd2-11e6-9cdc-42010af000bc");
        assertThat(uuidSet1.getIntervals()).isEqualTo(Arrays.asList(new GtidSet.Interval(1, 2)));
        assertThat(uuidSet2.getIntervals()).isEqualTo(Arrays.asList(new GtidSet.Interval(1, 1234)));
        assertThat(uuidSet3).isNull();
        assertThat(uuidSet4).isNull();
    }

    @Test
    public void shouldComparePositionsWithDifferentFields() {
        String lastGtidStr = "01261278-6ade-11e6-b36a-42010af00790:1-400944168," + ((((("30efb117-e42a-11e6-ba9e-42010a28002e:1-9," + "4d1a4918-44ba-11e6-bf12-42010af0040b:1-11604379,") + "621dc2f6-803b-11e6-acc1-42010af000a4:1-7963838,") + "716ec46f-d522-11e5-bb56-0242ac110004:1-35850702,") + "c627b2bc-9647-11e6-a886-42010af0044a:1-10426868,") + "d079cbb3-750f-11e6-954e-42010af00c28:1-11544291:11544293-11885648");
        config = build();
        context = new MySqlTaskContext(config, build(), false, null);
        context.start();
        context.source().setCompletedGtidSet(lastGtidStr);
        HistoryRecordComparator comparator = context.dbSchema().historyComparator();
        String server = "mysql-server-1";
        HistoryRecord rec1 = historyRecord(server, "mysql-bin.000008", 380941551, ("01261278-6ade-11e6-b36a-42010af00790:1-378422946," + ((("4d1a4918-44ba-11e6-bf12-42010af0040b:1-11002284," + "716ec46f-d522-11e5-bb56-0242ac110004:1-34673215,") + "96c2072e-e428-11e6-9590-42010a28002d:1-3,") + "c627b2bc-9647-11e6-a886-42010af0044a:1-9541144")), 0, 0, true);
        HistoryRecord rec2 = historyRecord(server, "mysql-bin.000016", 645115324, ("01261278-6ade-11e6-b36a-42010af00790:1-400944168," + ((((("30efb117-e42a-11e6-ba9e-42010a28002e:1-9," + "4d1a4918-44ba-11e6-bf12-42010af0040b:1-11604379,") + "621dc2f6-803b-11e6-acc1-42010af000a4:1-7963838,") + "716ec46f-d522-11e5-bb56-0242ac110004:1-35850702,") + "c627b2bc-9647-11e6-a886-42010af0044a:1-10426868,") + "d079cbb3-750f-11e6-954e-42010af00c28:1-11544291:11544293-11885648")), 2, 1, false);
        assertThat(comparator.isAtOrBefore(rec1, rec2)).isTrue();
        assertThat(comparator.isAtOrBefore(rec2, rec1)).isFalse();
    }

    @Test
    public void shouldIgnoreDatabaseHistoryProperties() throws Exception {
        config = build();
        context = new MySqlTaskContext(config, build(), false, null);
        context.start();
        context.getConnectionContext().jdbc().config().forEach(( k, v) -> {
            assertThat(k).doesNotMatch("^history");
        });
    }
}

