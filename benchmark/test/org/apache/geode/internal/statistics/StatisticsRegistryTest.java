/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.statistics;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.function.IntSupplier;
import org.apache.geode.StatisticDescriptor;
import org.apache.geode.Statistics;
import org.apache.geode.StatisticsType;
import org.apache.geode.StatisticsTypeFactory;
import org.apache.geode.internal.statistics.StatisticsRegistry.AtomicStatisticsFactory;
import org.apache.geode.internal.statistics.StatisticsRegistry.OsStatisticsFactory;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link StatisticsRegistry}.
 */
public class StatisticsRegistryTest {
    // Arbitrary values for factory method parameters
    private static final String DESCRIPTOR_NAME = "a-descriptor-name";

    private static final String DESCRIPTOR_DESCRIPTION = "a-descriptor-description";

    private static final String DESCRIPTOR_UNITS = "a-descriptor-units";

    private static final String REGISTRY_NAME = "a-registry-name";

    private static final int REGISTRY_START_TIME = 239847;

    private static final String STATISTICS_TEXT_ID = "a-text-id";

    private static final long STATISTICS_NUMERIC_ID = 9876;

    private static final int STATISTICS_OS_FLAGS = 54321;

    private static final String TYPE_NAME = "a-type-name";

    private static final String TYPE_DESCRIPTION = "a-type-description";

    private static final StatisticDescriptor[] TYPE_DESCRIPTORS = new StatisticDescriptor[]{ Mockito.mock(StatisticDescriptor.class), Mockito.mock(StatisticDescriptor.class), Mockito.mock(StatisticDescriptor.class) };

    @Mock
    private StatisticsTypeFactory typeFactory;

    @Mock
    private StatisticsType type;

    @Mock
    private AtomicStatisticsFactory atomicStatisticsFactory;

    @Mock
    private OsStatisticsFactory osStatisticsFactory;

    @Mock
    private IntSupplier pidSupplier;

    private StatisticsRegistry registry;

    @Test
    public void remembersItsName() {
        String theName = "the-name";
        StatisticsRegistry registry = new StatisticsRegistry(theName, StatisticsRegistryTest.REGISTRY_START_TIME);
        assertThat(registry.getName()).isEqualTo(theName);
    }

    @Test
    public void remembersItsStartTime() {
        int theStartTime = 374647;
        StatisticsRegistry registry = new StatisticsRegistry(StatisticsRegistryTest.REGISTRY_NAME, theStartTime);
        assertThat(registry.getStartTime()).isEqualTo(theStartTime);
    }

    @Test
    public void delegatesTypeCreationToTypeFactory() {
        StatisticsType typeCreatedByFactory = Mockito.mock(StatisticsType.class);
        Mockito.when(typeFactory.createType(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(typeCreatedByFactory);
        StatisticsType result = registry.createType(StatisticsRegistryTest.TYPE_NAME, StatisticsRegistryTest.TYPE_DESCRIPTION, StatisticsRegistryTest.TYPE_DESCRIPTORS);
        assertThat(result).isSameAs(typeCreatedByFactory);
    }

    @Test
    public void delegatesTypeCreationFromXmlToTypeFactory() throws IOException {
        Reader reader = new StringReader("<arbitrary-xml/>");
        StatisticsType[] typesCreatedByFactory = new StatisticsType[]{  };
        Mockito.when(typeFactory.createTypesFromXml(ArgumentMatchers.any())).thenReturn(typesCreatedByFactory);
        StatisticsType[] result = registry.createTypesFromXml(reader);
        assertThat(result).isSameAs(typesCreatedByFactory);
    }

    @Test
    public void delegatesTypeLookupToTypeFactory() {
        StatisticsType typeFoundByFactory = Mockito.mock(StatisticsType.class);
        Mockito.when(typeFactory.findType(ArgumentMatchers.any())).thenReturn(typeFoundByFactory);
        StatisticsType result = registry.findType(StatisticsRegistryTest.TYPE_NAME);
        assertThat(result).isSameAs(typeFoundByFactory);
    }

    @Test
    public void delegatesCreateIntCounterToTypeFactory() {
        StatisticDescriptor descriptorCreatedByFactory = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(typeFactory.createIntCounter(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(descriptorCreatedByFactory);
        StatisticDescriptor result = registry.createIntCounter(StatisticsRegistryTest.DESCRIPTOR_NAME, StatisticsRegistryTest.DESCRIPTOR_DESCRIPTION, StatisticsRegistryTest.DESCRIPTOR_UNITS);
        assertThat(result).isEqualTo(descriptorCreatedByFactory);
    }

    @Test
    public void delegatesCreateLongCounterToTypeFactory() {
        StatisticDescriptor descriptorCreatedByFactory = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(typeFactory.createLongCounter(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(descriptorCreatedByFactory);
        StatisticDescriptor result = registry.createLongCounter(StatisticsRegistryTest.DESCRIPTOR_NAME, StatisticsRegistryTest.DESCRIPTOR_DESCRIPTION, StatisticsRegistryTest.DESCRIPTOR_UNITS);
        assertThat(result).isEqualTo(descriptorCreatedByFactory);
    }

    @Test
    public void delegatesCreateDoubleCounterToTypeFactory() {
        StatisticDescriptor descriptorCreatedByFactory = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(typeFactory.createDoubleCounter(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(descriptorCreatedByFactory);
        StatisticDescriptor result = registry.createDoubleCounter(StatisticsRegistryTest.DESCRIPTOR_NAME, StatisticsRegistryTest.DESCRIPTOR_DESCRIPTION, StatisticsRegistryTest.DESCRIPTOR_UNITS);
        assertThat(result).isEqualTo(descriptorCreatedByFactory);
    }

    @Test
    public void delegatesCreateIntGaugeToTypeFactory() {
        StatisticDescriptor descriptorCreatedByFactory = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(typeFactory.createIntGauge(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(descriptorCreatedByFactory);
        StatisticDescriptor result = registry.createIntGauge(StatisticsRegistryTest.DESCRIPTOR_NAME, StatisticsRegistryTest.DESCRIPTOR_DESCRIPTION, StatisticsRegistryTest.DESCRIPTOR_UNITS);
        assertThat(result).isEqualTo(descriptorCreatedByFactory);
    }

    @Test
    public void delegatesCreateLongGaugeToTypeFactory() {
        StatisticDescriptor descriptorCreatedByFactory = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(typeFactory.createLongGauge(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(descriptorCreatedByFactory);
        StatisticDescriptor result = registry.createLongGauge(StatisticsRegistryTest.DESCRIPTOR_NAME, StatisticsRegistryTest.DESCRIPTOR_DESCRIPTION, StatisticsRegistryTest.DESCRIPTOR_UNITS);
        assertThat(result).isEqualTo(descriptorCreatedByFactory);
    }

    @Test
    public void delegatesCreateDoubleGaugeToTypeFactory() {
        StatisticDescriptor descriptorCreatedByFactory = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(typeFactory.createDoubleGauge(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(descriptorCreatedByFactory);
        StatisticDescriptor result = registry.createDoubleGauge(StatisticsRegistryTest.DESCRIPTOR_NAME, StatisticsRegistryTest.DESCRIPTOR_DESCRIPTION, StatisticsRegistryTest.DESCRIPTOR_UNITS);
        assertThat(result).isEqualTo(descriptorCreatedByFactory);
    }

    @Test
    public void delegatesCreateLargerBetterIntCounterToTypeFactory() {
        StatisticDescriptor descriptorCreatedByFactory = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(typeFactory.createIntCounter(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())).thenReturn(descriptorCreatedByFactory);
        StatisticDescriptor result = registry.createIntCounter(StatisticsRegistryTest.DESCRIPTOR_NAME, StatisticsRegistryTest.DESCRIPTOR_DESCRIPTION, StatisticsRegistryTest.DESCRIPTOR_UNITS, true);
        assertThat(result).isEqualTo(descriptorCreatedByFactory);
    }

    @Test
    public void delegatesCreateLargerBetterLongCounterToTypeFactory() {
        StatisticDescriptor descriptorCreatedByFactory = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(typeFactory.createLongCounter(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())).thenReturn(descriptorCreatedByFactory);
        StatisticDescriptor result = registry.createLongCounter(StatisticsRegistryTest.DESCRIPTOR_NAME, StatisticsRegistryTest.DESCRIPTOR_DESCRIPTION, StatisticsRegistryTest.DESCRIPTOR_UNITS, false);
        assertThat(result).isEqualTo(descriptorCreatedByFactory);
    }

    @Test
    public void delegatesCreateLargerBetterDoubleCounterToTypeFactory() {
        StatisticDescriptor descriptorCreatedByFactory = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(typeFactory.createDoubleCounter(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())).thenReturn(descriptorCreatedByFactory);
        StatisticDescriptor result = registry.createDoubleCounter(StatisticsRegistryTest.DESCRIPTOR_NAME, StatisticsRegistryTest.DESCRIPTOR_DESCRIPTION, StatisticsRegistryTest.DESCRIPTOR_UNITS, true);
        assertThat(result).isEqualTo(descriptorCreatedByFactory);
    }

    @Test
    public void delegatesCreateLargerBetterIntGaugeToTypeFactory() {
        StatisticDescriptor descriptorCreatedByFactory = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(typeFactory.createIntGauge(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())).thenReturn(descriptorCreatedByFactory);
        StatisticDescriptor result = registry.createIntGauge(StatisticsRegistryTest.DESCRIPTOR_NAME, StatisticsRegistryTest.DESCRIPTOR_DESCRIPTION, StatisticsRegistryTest.DESCRIPTOR_UNITS, false);
        assertThat(result).isEqualTo(descriptorCreatedByFactory);
    }

    @Test
    public void delegatesCreateLargerBetterLongGaugeToTypeFactory() {
        StatisticDescriptor descriptorCreatedByFactory = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(typeFactory.createLongGauge(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())).thenReturn(descriptorCreatedByFactory);
        StatisticDescriptor result = registry.createLongGauge(StatisticsRegistryTest.DESCRIPTOR_NAME, StatisticsRegistryTest.DESCRIPTOR_DESCRIPTION, StatisticsRegistryTest.DESCRIPTOR_UNITS, true);
        assertThat(result).isEqualTo(descriptorCreatedByFactory);
    }

    @Test
    public void delegatesCreateLargerBetterDoubleGaugeToTypeFactory() {
        StatisticDescriptor descriptorCreatedByFactory = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(typeFactory.createDoubleGauge(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())).thenReturn(descriptorCreatedByFactory);
        StatisticDescriptor result = registry.createDoubleGauge(StatisticsRegistryTest.DESCRIPTOR_NAME, StatisticsRegistryTest.DESCRIPTOR_DESCRIPTION, StatisticsRegistryTest.DESCRIPTOR_UNITS, false);
        assertThat(result).isEqualTo(descriptorCreatedByFactory);
    }

    @Test
    public void createsOsStatisticsViaFactory() {
        Statistics statisticsCreatedByFactory = Mockito.mock(Statistics.class);
        Mockito.when(osStatisticsFactory.create(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.any())).thenReturn(statisticsCreatedByFactory);
        Statistics result = registry.createOsStatistics(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, StatisticsRegistryTest.STATISTICS_NUMERIC_ID, StatisticsRegistryTest.STATISTICS_OS_FLAGS);
        assertThat(result).isSameAs(statisticsCreatedByFactory);
    }

    @Test
    public void createsAtomicStatisticsViaFactory() {
        Statistics statisticsCreatedByFactory = Mockito.mock(Statistics.class);
        Mockito.when(atomicStatisticsFactory.create(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(statisticsCreatedByFactory);
        Statistics result = registry.createAtomicStatistics(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, StatisticsRegistryTest.STATISTICS_NUMERIC_ID);
        assertThat(result).isSameAs(statisticsCreatedByFactory);
    }

    @Test
    public void incrementsUniqueIdForEachCreatedStatistics() {
        registry.createOsStatistics(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, 0, 0);
        Mockito.verify(osStatisticsFactory).create(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, 0L, 1, 0, registry);
        registry.createOsStatistics(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, 0, 0);
        Mockito.verify(osStatisticsFactory).create(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, 0, 2, 0, registry);
        registry.createOsStatistics(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, 0, 0);
        Mockito.verify(osStatisticsFactory).create(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, 0, 3, 0, registry);
        registry.createAtomicStatistics(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, 0);
        Mockito.verify(atomicStatisticsFactory).create(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, 0, 4, registry);
        registry.createOsStatistics(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, 0, 0);
        Mockito.verify(osStatisticsFactory).create(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, 0, 5, 0, registry);
        registry.createAtomicStatistics(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, 0);
        Mockito.verify(atomicStatisticsFactory).create(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, 0, 6, registry);
        registry.createAtomicStatistics(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, 0);
        Mockito.verify(atomicStatisticsFactory).create(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, 0, 7, registry);
    }

    @Test
    public void remembersTheStatisticsItCreates() {
        Statistics atomicStatistics1 = Mockito.mock(Statistics.class, "atomic 1");
        Statistics atomicStatistics2 = Mockito.mock(Statistics.class, "atomic 1");
        Statistics atomicStatistics3 = Mockito.mock(Statistics.class, "atomic 1");
        Statistics osStatistics1 = Mockito.mock(Statistics.class, "os 1");
        Statistics osStatistics2 = Mockito.mock(Statistics.class, "os 1");
        Statistics osStatistics3 = Mockito.mock(Statistics.class, "os 1");
        Mockito.when(osStatisticsFactory.create(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.any())).thenReturn(osStatistics1).thenReturn(osStatistics2).thenReturn(osStatistics3);
        Mockito.when(atomicStatisticsFactory.create(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(atomicStatistics1).thenReturn(atomicStatistics2).thenReturn(atomicStatistics3);
        registry.createAtomicStatistics(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, StatisticsRegistryTest.STATISTICS_NUMERIC_ID);
        registry.createOsStatistics(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, StatisticsRegistryTest.STATISTICS_NUMERIC_ID, StatisticsRegistryTest.STATISTICS_OS_FLAGS);
        registry.createOsStatistics(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, StatisticsRegistryTest.STATISTICS_NUMERIC_ID, StatisticsRegistryTest.STATISTICS_OS_FLAGS);
        registry.createAtomicStatistics(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, StatisticsRegistryTest.STATISTICS_NUMERIC_ID);
        registry.createOsStatistics(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, StatisticsRegistryTest.STATISTICS_NUMERIC_ID, StatisticsRegistryTest.STATISTICS_OS_FLAGS);
        registry.createAtomicStatistics(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, StatisticsRegistryTest.STATISTICS_NUMERIC_ID);
        assertThat(registry.getStatsList()).containsExactlyInAnyOrder(atomicStatistics1, atomicStatistics2, atomicStatistics3, osStatistics1, osStatistics2, osStatistics3);
    }

    @Test
    public void forgetsTheStatisticsItDestroys() {
        Statistics osStatistics1 = Mockito.mock(Statistics.class, "os 1");
        Statistics osStatistics2 = Mockito.mock(Statistics.class, "os 2");
        Statistics osStatistics3 = Mockito.mock(Statistics.class, "os 3");
        Mockito.when(osStatisticsFactory.create(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.any())).thenReturn(osStatistics1).thenReturn(osStatistics2).thenReturn(osStatistics3);
        Statistics atomicStatistics1 = Mockito.mock(Statistics.class, "atomic 1");
        Statistics atomicStatistics2 = Mockito.mock(Statistics.class, "atomic 2");
        Statistics atomicStatistics3 = Mockito.mock(Statistics.class, "atomic 3");
        Mockito.when(atomicStatisticsFactory.create(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(atomicStatistics1).thenReturn(atomicStatistics2).thenReturn(atomicStatistics3);
        registry.createAtomicStatistics(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, StatisticsRegistryTest.STATISTICS_NUMERIC_ID);
        registry.createOsStatistics(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, StatisticsRegistryTest.STATISTICS_NUMERIC_ID, StatisticsRegistryTest.STATISTICS_OS_FLAGS);
        registry.createOsStatistics(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, StatisticsRegistryTest.STATISTICS_NUMERIC_ID, StatisticsRegistryTest.STATISTICS_OS_FLAGS);
        registry.createAtomicStatistics(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, StatisticsRegistryTest.STATISTICS_NUMERIC_ID);
        registry.createOsStatistics(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, StatisticsRegistryTest.STATISTICS_NUMERIC_ID, StatisticsRegistryTest.STATISTICS_OS_FLAGS);
        registry.createAtomicStatistics(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, StatisticsRegistryTest.STATISTICS_NUMERIC_ID);
        registry.destroyStatistics(osStatistics2);
        registry.destroyStatistics(atomicStatistics1);
        assertThat(registry.getStatsList()).containsExactlyInAnyOrder(atomicStatistics2, atomicStatistics3, osStatistics1, osStatistics3);
    }

    @Test
    public void modificationCountStartsAtZero() {
        assertThat(registry.getStatListModCount()).isEqualTo(0);
    }

    @Test
    public void incrementsModificationCountOnEachCreationAndDestruction() {
        Statistics osStatistics = Mockito.mock(Statistics.class, "os");
        Statistics atomicStatistics = Mockito.mock(Statistics.class, "atomic");
        Mockito.when(osStatisticsFactory.create(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.any())).thenReturn(osStatistics);
        Mockito.when(atomicStatisticsFactory.create(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(atomicStatistics);
        registry.createAtomicStatistics(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, StatisticsRegistryTest.STATISTICS_NUMERIC_ID);
        assertThat(registry.getStatListModCount()).as("modification count after first modification").isEqualTo(1);
        registry.createOsStatistics(type, StatisticsRegistryTest.STATISTICS_TEXT_ID, StatisticsRegistryTest.STATISTICS_NUMERIC_ID, StatisticsRegistryTest.STATISTICS_OS_FLAGS);
        assertThat(registry.getStatListModCount()).as("modification count after second modification").isEqualTo(2);
        registry.destroyStatistics(osStatistics);
        assertThat(registry.getStatListModCount()).as("modification count after third modification").isEqualTo(3);
        registry.destroyStatistics(atomicStatistics);
        assertThat(registry.getStatListModCount()).as("modification count after fourth modification").isEqualTo(4);
    }

    @Test
    public void doesNotIncrementModificationCountWhenDestroyingUnknownStats() {
        // The stats were not created by the registry, and so are not known to the registry
        Statistics unknownStatistics = Mockito.mock(Statistics.class);
        registry.destroyStatistics(unknownStatistics);
        assertThat(registry.getStatListModCount()).isEqualTo(0);
    }

    @Test
    public void findStatisticsByUniqueId_returnsStatisticsThatMatchesUniqueId() {
        long soughtId = 44L;
        Statistics matchingStatistics = StatisticsRegistryTest.statistics(StatisticsRegistryTest.withUniqueId(soughtId));
        givenExistingStatistics(matchingStatistics);
        assertThat(registry.findStatisticsByUniqueId(soughtId)).isSameAs(matchingStatistics);
    }

    @Test
    public void findStatisticsByUniqueId_returnsNullIfNoStatisticsMatchesUniqueId() {
        assertThat(registry.findStatisticsByUniqueId(0)).isNull();
    }

    @Test
    public void statisticsExists_returnsTrue_ifStatisticsMatchesUniqueId() {
        long soughtId = 44L;
        givenExistingStatistics(StatisticsRegistryTest.statistics(StatisticsRegistryTest.withUniqueId(soughtId)));
        assertThat(registry.statisticsExists(soughtId)).isTrue();
    }

    @Test
    public void statisticsExists_returnsFalse_ifNoStatisticsMatchesUniqueId() {
        assertThat(registry.statisticsExists(99L)).isFalse();
    }

    @Test
    public void findsStatisticsByNumericId_returnsAllStatisticsThatMatchNumericId() {
        long soughtId = 44L;
        long differentId = 45L;
        Statistics matchingStatistics1 = StatisticsRegistryTest.statistics(StatisticsRegistryTest.withNumericId(soughtId));
        Statistics matchingStatistics2 = StatisticsRegistryTest.statistics(StatisticsRegistryTest.withNumericId(soughtId));
        Statistics mismatchingStatistics = StatisticsRegistryTest.statistics(StatisticsRegistryTest.withNumericId(differentId));
        givenExistingStatistics(matchingStatistics1, mismatchingStatistics, matchingStatistics2);
        Statistics[] foundStatistics = registry.findStatisticsByNumericId(soughtId);
        assertThat(foundStatistics).containsExactlyInAnyOrder(matchingStatistics1, matchingStatistics2);
    }

    @Test
    public void findStatisticsByNumericId_returnsEmptyArray_ifNoStatisticsMatchNumericId() {
        long soughtId = 44L;
        long differentId = 45L;
        givenExistingStatistics(StatisticsRegistryTest.statistics(StatisticsRegistryTest.withNumericId(differentId)), StatisticsRegistryTest.statistics(StatisticsRegistryTest.withNumericId(differentId)), StatisticsRegistryTest.statistics(StatisticsRegistryTest.withNumericId(differentId)));
        assertThat(registry.findStatisticsByNumericId(soughtId)).isEmpty();
    }

    @Test
    public void findStatisticsByTextId_returnsAllStatisticsThatMatchTextId() {
        String soughtId = "matching-id";
        String differentId = "mismatching-id";
        Statistics matchingStatistics1 = StatisticsRegistryTest.statistics(StatisticsRegistryTest.withTextId(soughtId));
        Statistics matchingStatistics2 = StatisticsRegistryTest.statistics(StatisticsRegistryTest.withTextId(soughtId));
        Statistics mismatchingStatistics = StatisticsRegistryTest.statistics(StatisticsRegistryTest.withTextId(differentId));
        givenExistingStatistics(mismatchingStatistics, matchingStatistics1, matchingStatistics2);
        Statistics[] foundStatistics = registry.findStatisticsByTextId(soughtId);
        assertThat(foundStatistics).containsExactlyInAnyOrder(matchingStatistics1, matchingStatistics2);
    }

    @Test
    public void findStatisticsByTextId_returnsEmptyArray_ifNoStatisticsMatchTextId() {
        String soughtId = "matching-id";
        String differentId = "mismatching-id";
        givenExistingStatistics(StatisticsRegistryTest.statistics(StatisticsRegistryTest.withTextId(differentId)), StatisticsRegistryTest.statistics(StatisticsRegistryTest.withTextId(differentId)), StatisticsRegistryTest.statistics(StatisticsRegistryTest.withTextId(differentId)));
        assertThat(registry.findStatisticsByTextId(soughtId)).isEmpty();
    }

    @Test
    public void findStatisticsByType_returnsAllStatisticsThatMatchType() {
        StatisticsType soughtType = Mockito.mock(StatisticsType.class, "matching type");
        StatisticsType differentType = Mockito.mock(StatisticsType.class, "mismatching type");
        Statistics matchingStatistics1 = StatisticsRegistryTest.statistics(StatisticsRegistryTest.withType(soughtType));
        Statistics matchingStatistics2 = StatisticsRegistryTest.statistics(StatisticsRegistryTest.withType(soughtType));
        Statistics mismatchingStatistics = StatisticsRegistryTest.statistics(StatisticsRegistryTest.withType(differentType));
        givenExistingStatistics(matchingStatistics2, matchingStatistics1, mismatchingStatistics);
        Statistics[] foundStatistics = registry.findStatisticsByType(soughtType);
        assertThat(foundStatistics).containsExactlyInAnyOrder(matchingStatistics1, matchingStatistics2);
    }

    @Test
    public void findStatisticsByType_returnsEmptyArray_ifNoStatisticsMatchType() {
        StatisticsType soughtType = Mockito.mock(StatisticsType.class, "matching type");
        StatisticsType differentType = Mockito.mock(StatisticsType.class, "mismatching type");
        givenExistingStatistics(StatisticsRegistryTest.statistics(StatisticsRegistryTest.withType(differentType)), StatisticsRegistryTest.statistics(StatisticsRegistryTest.withType(differentType)), StatisticsRegistryTest.statistics(StatisticsRegistryTest.withType(differentType)));
        assertThat(registry.findStatisticsByType(soughtType)).isEmpty();
    }

    @Test
    public void delegatesGetPidToPidSupplier() {
        int pidReturnedFromPidSupplier = 42;
        Mockito.when(pidSupplier.getAsInt()).thenReturn(pidReturnedFromPidSupplier);
        int result = registry.getPid();
        assertThat(result).isSameAs(pidReturnedFromPidSupplier);
    }

    @Test
    public void propagatesPidSupplierExceptionIfPidSupplierThrows() {
        Throwable thrownFromPidSupplier = new RuntimeException("thrown from pid supplier");
        Mockito.when(pidSupplier.getAsInt()).thenThrow(thrownFromPidSupplier);
        Throwable thrown = catchThrowable(() -> registry.getPid());
        assertThat(thrown).isSameAs(thrownFromPidSupplier);
    }
}

