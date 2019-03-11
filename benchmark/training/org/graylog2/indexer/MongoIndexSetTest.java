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
package org.graylog2.indexer;


import Health.Status.GREEN;
import SetIndexReadOnlyAndCalculateRangeJob.Factory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.graylog2.audit.AuditEventSender;
import org.graylog2.indexer.indexset.IndexSetConfig;
import org.graylog2.indexer.indices.Indices;
import org.graylog2.indexer.indices.jobs.SetIndexReadOnlyAndCalculateRangeJob;
import org.graylog2.indexer.ranges.IndexRangeService;
import org.graylog2.indexer.retention.strategies.NoopRetentionStrategy;
import org.graylog2.indexer.retention.strategies.NoopRetentionStrategyConfig;
import org.graylog2.indexer.rotation.strategies.MessageCountRotationStrategy;
import org.graylog2.indexer.rotation.strategies.MessageCountRotationStrategyConfig;
import org.graylog2.plugin.system.NodeId;
import org.graylog2.shared.system.activities.ActivityWriter;
import org.graylog2.system.jobs.SystemJobConcurrencyException;
import org.graylog2.system.jobs.SystemJobManager;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class MongoIndexSetTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private Indices indices;

    @Mock
    private AuditEventSender auditEventSender;

    @Mock
    private NodeId nodeId;

    @Mock
    private IndexRangeService indexRangeService;

    @Mock
    private SystemJobManager systemJobManager;

    @Mock
    private Factory jobFactory;

    @Mock
    private ActivityWriter activityWriter;

    private final IndexSetConfig config = IndexSetConfig.create("Test", "Test", true, "graylog", 1, 0, MessageCountRotationStrategy.class.getCanonicalName(), MessageCountRotationStrategyConfig.createDefault(), NoopRetentionStrategy.class.getCanonicalName(), NoopRetentionStrategyConfig.createDefault(), ZonedDateTime.of(2016, 11, 8, 0, 0, 0, 0, ZoneOffset.UTC), "standard", "index-template", 1, false);

    private MongoIndexSet mongoIndexSet;

    @Test
    public void testExtractIndexNumber() {
        assertThat(mongoIndexSet.extractIndexNumber("graylog_0")).contains(0);
        assertThat(mongoIndexSet.extractIndexNumber("graylog_4")).contains(4);
        assertThat(mongoIndexSet.extractIndexNumber("graylog_52")).contains(52);
    }

    @Test
    public void testExtractIndexNumberWithMalformedFormatReturnsEmptyOptional() {
        assertThat(mongoIndexSet.extractIndexNumber("graylog2_hunderttausend")).isEmpty();
    }

    @Test
    public void testBuildIndexName() {
        Assert.assertEquals("graylog_0", mongoIndexSet.buildIndexName(0));
        Assert.assertEquals("graylog_1", mongoIndexSet.buildIndexName(1));
        Assert.assertEquals("graylog_9001", mongoIndexSet.buildIndexName(9001));
    }

    @Test
    public void nullIndexerDoesNotThrow() {
        final Map<String, Set<String>> deflectorIndices = mongoIndexSet.getAllIndexAliases();
        assertThat(deflectorIndices).isEmpty();
    }

    @Test
    public void nullIndexerDoesNotThrowOnIndexName() {
        final String[] indicesNames = mongoIndexSet.getManagedIndices();
        assertThat(indicesNames).isEmpty();
    }

    @Test
    public void testIsDeflectorAlias() {
        Assert.assertTrue(mongoIndexSet.isWriteIndexAlias("graylog_deflector"));
        Assert.assertFalse(mongoIndexSet.isWriteIndexAlias("graylog_foobar"));
        Assert.assertFalse(mongoIndexSet.isWriteIndexAlias("graylog_123"));
        Assert.assertFalse(mongoIndexSet.isWriteIndexAlias("HAHA"));
    }

    @Test
    public void testIsGraylogIndex() {
        Assert.assertTrue(mongoIndexSet.isGraylogDeflectorIndex("graylog_1"));
        Assert.assertTrue(mongoIndexSet.isManagedIndex("graylog_1"));
        Assert.assertTrue(mongoIndexSet.isGraylogDeflectorIndex("graylog_42"));
        Assert.assertTrue(mongoIndexSet.isManagedIndex("graylog_42"));
        Assert.assertTrue(mongoIndexSet.isGraylogDeflectorIndex("graylog_100000000"));
        Assert.assertTrue(mongoIndexSet.isManagedIndex("graylog_100000000"));
        // The restored archive indices should NOT be taken into account when getting the new deflector number.
        Assert.assertFalse(mongoIndexSet.isGraylogDeflectorIndex("graylog_42_restored_archive"));
        Assert.assertTrue(mongoIndexSet.isManagedIndex("graylog_42_restored_archive"));
        Assert.assertFalse(mongoIndexSet.isGraylogDeflectorIndex("graylog_42_restored_archive123"));
        Assert.assertFalse(mongoIndexSet.isManagedIndex("graylog_42_restored_archive123"));
        Assert.assertFalse(mongoIndexSet.isGraylogDeflectorIndex("graylog_42_restored_archive_123"));
        Assert.assertFalse(mongoIndexSet.isManagedIndex("graylog_42_restored_archive_123"));
        Assert.assertFalse(mongoIndexSet.isGraylogDeflectorIndex(null));
        Assert.assertFalse(mongoIndexSet.isManagedIndex(null));
        Assert.assertFalse(mongoIndexSet.isGraylogDeflectorIndex(""));
        Assert.assertFalse(mongoIndexSet.isManagedIndex(""));
        Assert.assertFalse(mongoIndexSet.isGraylogDeflectorIndex("graylog_deflector"));
        Assert.assertFalse(mongoIndexSet.isManagedIndex("graylog_deflector"));
        Assert.assertFalse(mongoIndexSet.isGraylogDeflectorIndex("graylog2beta_1"));
        Assert.assertFalse(mongoIndexSet.isManagedIndex("graylog2beta_1"));
        Assert.assertFalse(mongoIndexSet.isGraylogDeflectorIndex("graylog_1_suffix"));
        Assert.assertFalse(mongoIndexSet.isManagedIndex("graylog_1_suffix"));
        Assert.assertFalse(mongoIndexSet.isGraylogDeflectorIndex("HAHA"));
        Assert.assertFalse(mongoIndexSet.isManagedIndex("HAHA"));
    }

    @Test
    public void getNewestTargetNumber() throws NoTargetIndexException {
        final Map<String, Set<String>> indexNameAliases = ImmutableMap.of("graylog_1", Collections.emptySet(), "graylog_2", Collections.emptySet(), "graylog_3", Collections.singleton("graylog_deflector"), "graylog_4_restored_archive", Collections.emptySet());
        Mockito.when(indices.getIndexNamesAndAliases(ArgumentMatchers.anyString())).thenReturn(indexNameAliases);
        final MongoIndexSet mongoIndexSet = new MongoIndexSet(config, indices, nodeId, indexRangeService, auditEventSender, systemJobManager, jobFactory, activityWriter);
        final int number = mongoIndexSet.getNewestIndexNumber();
        Assert.assertEquals(3, number);
    }

    @Test
    public void getAllGraylogIndexNames() {
        final Map<String, Set<String>> indexNameAliases = ImmutableMap.of("graylog_1", Collections.emptySet(), "graylog_2", Collections.emptySet(), "graylog_3", Collections.emptySet(), "graylog_4_restored_archive", Collections.emptySet(), "graylog_5", Collections.singleton("graylog_deflector"));
        Mockito.when(indices.getIndexNamesAndAliases(ArgumentMatchers.anyString())).thenReturn(indexNameAliases);
        final MongoIndexSet mongoIndexSet = new MongoIndexSet(config, indices, nodeId, indexRangeService, auditEventSender, systemJobManager, jobFactory, activityWriter);
        final String[] allGraylogIndexNames = mongoIndexSet.getManagedIndices();
        assertThat(allGraylogIndexNames).containsExactlyElementsOf(indexNameAliases.keySet());
    }

    @Test
    public void getAllGraylogDeflectorIndices() {
        final Map<String, Set<String>> indexNameAliases = ImmutableMap.of("graylog_1", Collections.emptySet(), "graylog_2", Collections.emptySet(), "graylog_3", Collections.emptySet(), "graylog_4_restored_archive", Collections.emptySet(), "graylog_5", Collections.singleton("graylog_deflector"));
        Mockito.when(indices.getIndexNamesAndAliases(ArgumentMatchers.anyString())).thenReturn(indexNameAliases);
        final MongoIndexSet mongoIndexSet = new MongoIndexSet(config, indices, nodeId, indexRangeService, auditEventSender, systemJobManager, jobFactory, activityWriter);
        final Map<String, Set<String>> deflectorIndices = mongoIndexSet.getAllIndexAliases();
        assertThat(deflectorIndices).containsOnlyKeys("graylog_1", "graylog_2", "graylog_3", "graylog_5");
    }

    @Test
    public void testCleanupAliases() throws Exception {
        final MongoIndexSet mongoIndexSet = new MongoIndexSet(config, indices, nodeId, indexRangeService, auditEventSender, systemJobManager, jobFactory, activityWriter);
        mongoIndexSet.cleanupAliases(ImmutableSet.of("graylog_2", "graylog_3", "foobar"));
        Mockito.verify(indices).removeAliases("graylog_deflector", ImmutableSet.of("graylog_2", "foobar"));
    }

    @Test
    public void cycleThrowsRuntimeExceptionIfIndexCreationFailed() {
        final Map<String, Set<String>> indexNameAliases = ImmutableMap.of();
        Mockito.when(indices.getIndexNamesAndAliases(ArgumentMatchers.anyString())).thenReturn(indexNameAliases);
        Mockito.when(indices.create("graylog_0", mongoIndexSet)).thenReturn(false);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Could not create new target index <graylog_0>.");
        final MongoIndexSet mongoIndexSet = new MongoIndexSet(config, indices, nodeId, indexRangeService, auditEventSender, systemJobManager, jobFactory, activityWriter);
        mongoIndexSet.cycle();
    }

    @Test
    public void cycleAddsUnknownDeflectorRange() {
        final String newIndexName = "graylog_1";
        final Map<String, Set<String>> indexNameAliases = ImmutableMap.of("graylog_0", Collections.singleton("graylog_deflector"));
        Mockito.when(indices.getIndexNamesAndAliases(ArgumentMatchers.anyString())).thenReturn(indexNameAliases);
        Mockito.when(indices.create(newIndexName, mongoIndexSet)).thenReturn(true);
        Mockito.when(indices.waitForRecovery(newIndexName)).thenReturn(GREEN);
        final MongoIndexSet mongoIndexSet = new MongoIndexSet(config, indices, nodeId, indexRangeService, auditEventSender, systemJobManager, jobFactory, activityWriter);
        mongoIndexSet.cycle();
        Mockito.verify(indexRangeService, Mockito.times(1)).createUnknownRange(newIndexName);
    }

    @Test
    public void cycleSetsOldIndexToReadOnly() throws SystemJobConcurrencyException {
        final String newIndexName = "graylog_1";
        final String oldIndexName = "graylog_0";
        final Map<String, Set<String>> indexNameAliases = ImmutableMap.of(oldIndexName, Collections.singleton("graylog_deflector"));
        Mockito.when(indices.getIndexNamesAndAliases(ArgumentMatchers.anyString())).thenReturn(indexNameAliases);
        Mockito.when(indices.create(newIndexName, mongoIndexSet)).thenReturn(true);
        Mockito.when(indices.waitForRecovery(newIndexName)).thenReturn(GREEN);
        final SetIndexReadOnlyAndCalculateRangeJob rangeJob = Mockito.mock(SetIndexReadOnlyAndCalculateRangeJob.class);
        Mockito.when(jobFactory.create(oldIndexName)).thenReturn(rangeJob);
        final MongoIndexSet mongoIndexSet = new MongoIndexSet(config, indices, nodeId, indexRangeService, auditEventSender, systemJobManager, jobFactory, activityWriter);
        mongoIndexSet.cycle();
        Mockito.verify(jobFactory, Mockito.times(1)).create(oldIndexName);
        Mockito.verify(systemJobManager, Mockito.times(1)).submitWithDelay(rangeJob, 30L, TimeUnit.SECONDS);
    }

    @Test
    public void cycleSwitchesIndexAliasToNewTarget() {
        final String oldIndexName = (config.indexPrefix()) + "_0";
        final String newIndexName = (config.indexPrefix()) + "_1";
        final String deflector = "graylog_deflector";
        final Map<String, Set<String>> indexNameAliases = ImmutableMap.of(oldIndexName, Collections.singleton(deflector));
        Mockito.when(indices.getIndexNamesAndAliases(ArgumentMatchers.anyString())).thenReturn(indexNameAliases);
        Mockito.when(indices.create(newIndexName, mongoIndexSet)).thenReturn(true);
        Mockito.when(indices.waitForRecovery(newIndexName)).thenReturn(GREEN);
        final MongoIndexSet mongoIndexSet = new MongoIndexSet(config, indices, nodeId, indexRangeService, auditEventSender, systemJobManager, jobFactory, activityWriter);
        mongoIndexSet.cycle();
        Mockito.verify(indices, Mockito.times(1)).cycleAlias(deflector, newIndexName, oldIndexName);
    }

    @Test
    public void cyclePointsIndexAliasToInitialTarget() {
        final String indexName = (config.indexPrefix()) + "_0";
        final Map<String, Set<String>> indexNameAliases = ImmutableMap.of();
        Mockito.when(indices.getIndexNamesAndAliases(ArgumentMatchers.anyString())).thenReturn(indexNameAliases);
        Mockito.when(indices.create(indexName, mongoIndexSet)).thenReturn(true);
        Mockito.when(indices.waitForRecovery(indexName)).thenReturn(GREEN);
        final MongoIndexSet mongoIndexSet = new MongoIndexSet(config, indices, nodeId, indexRangeService, auditEventSender, systemJobManager, jobFactory, activityWriter);
        mongoIndexSet.cycle();
        Mockito.verify(indices, Mockito.times(1)).cycleAlias("graylog_deflector", indexName);
    }
}

