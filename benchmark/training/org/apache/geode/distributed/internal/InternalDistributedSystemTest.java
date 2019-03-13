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
package org.apache.geode.distributed.internal;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;
import org.apache.geode.StatisticDescriptor;
import org.apache.geode.Statistics;
import org.apache.geode.StatisticsType;
import org.apache.geode.internal.statistics.StatisticsManager;
import org.apache.geode.internal.statistics.StatisticsManagerFactory;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link InternalDistributedSystem}.
 */
public class InternalDistributedSystemTest {
    private static final String STATISTIC_NAME = "statistic-name";

    private static final String STATISTIC_DESCRIPTION = "statistic-description";

    private static final String STATISTIC_UNITS = "statistic-units";

    private static final StatisticsType STATISTICS_TYPE = Mockito.mock(StatisticsType.class);

    private static final String STATISTICS_TEXT_ID = "statistics-text-id";

    private static final long STATISTICS_NUMERIC_ID = 2349;

    @Mock(name = "autowiredDistributionManager")
    private DistributionManager distributionManager;

    @Mock(name = "autowiredStatisticsManagerFactory")
    private StatisticsManagerFactory statisticsManagerFactory;

    @Mock(name = "autowiredStatisticsManager")
    private StatisticsManager statisticsManager;

    private InternalDistributedSystem internalDistributedSystem;

    @Test
    public void createsStatisticsManagerViaFactory() {
        StatisticsManagerFactory statisticsManagerFactory = Mockito.mock(StatisticsManagerFactory.class, "statisticsManagerFactory");
        StatisticsManager statisticsManagerCreatedByFactory = Mockito.mock(StatisticsManager.class, "statisticsManagerCreatedByFactory");
        Mockito.when(statisticsManagerFactory.create(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(false))).thenReturn(statisticsManagerCreatedByFactory);
        InternalDistributedSystem result = InternalDistributedSystem.newInstanceForTesting(distributionManager, new Properties(), statisticsManagerFactory);
        assertThat(result.getStatisticsManager()).isSameAs(statisticsManagerCreatedByFactory);
    }

    @Test
    public void delegatesCreateTypeToStatisticsManager() {
        String typeName = "type-name";
        String typeDescription = "type-description";
        StatisticDescriptor[] descriptors = new StatisticDescriptor[]{  };
        StatisticsType typeReturnedByManager = Mockito.mock(StatisticsType.class);
        Mockito.when(statisticsManager.createType(typeName, typeDescription, descriptors)).thenReturn(typeReturnedByManager);
        StatisticsType result = internalDistributedSystem.createType(typeName, typeDescription, descriptors);
        assertThat(result).isSameAs(typeReturnedByManager);
    }

    @Test
    public void delegatesCreateTypeFromXmlToStatisticsManager() throws IOException {
        Reader reader = new StringReader("<arbitrary-xml/>");
        StatisticsType[] typesReturnedByManager = new StatisticsType[]{  };
        Mockito.when(statisticsManager.createTypesFromXml(ArgumentMatchers.same(reader))).thenReturn(typesReturnedByManager);
        StatisticsType[] result = internalDistributedSystem.createTypesFromXml(reader);
        assertThat(result).isSameAs(typesReturnedByManager);
    }

    @Test
    public void delegatesFindTypeToStatisticsManager() {
        String soughtTypeName = "the-name";
        StatisticsType typeReturnedByManager = Mockito.mock(StatisticsType.class);
        Mockito.when(statisticsManager.findType(soughtTypeName)).thenReturn(typeReturnedByManager);
        StatisticsType result = internalDistributedSystem.findType(soughtTypeName);
        assertThat(result).isSameAs(typeReturnedByManager);
    }

    @Test
    public void delegatesCreateIntCounterToStatisticsManager() {
        StatisticDescriptor descriptorReturnedByManager = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(statisticsManager.createIntCounter(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS)).thenReturn(descriptorReturnedByManager);
        StatisticDescriptor result = internalDistributedSystem.createIntCounter(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS);
        assertThat(result).isSameAs(descriptorReturnedByManager);
    }

    @Test
    public void delegatesCreateLongCounterToStatisticsManager() {
        StatisticDescriptor descriptorReturnedByManager = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(statisticsManager.createLongCounter(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS)).thenReturn(descriptorReturnedByManager);
        StatisticDescriptor result = internalDistributedSystem.createLongCounter(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS);
        assertThat(result).isSameAs(descriptorReturnedByManager);
    }

    @Test
    public void delegatesCreateDoubleCounterToStatisticsManager() {
        StatisticDescriptor descriptorReturnedByManager = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(statisticsManager.createDoubleCounter(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS)).thenReturn(descriptorReturnedByManager);
        StatisticDescriptor result = internalDistributedSystem.createDoubleCounter(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS);
        assertThat(result).isSameAs(descriptorReturnedByManager);
    }

    @Test
    public void delegatesCreateIntGaugeToStatisticsManager() {
        StatisticDescriptor descriptorReturnedByManager = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(statisticsManager.createIntGauge(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS)).thenReturn(descriptorReturnedByManager);
        StatisticDescriptor result = internalDistributedSystem.createIntGauge(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS);
        assertThat(result).isSameAs(descriptorReturnedByManager);
    }

    @Test
    public void delegatesCreateLongGaugeToStatisticsManager() {
        StatisticDescriptor descriptorReturnedByManager = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(statisticsManager.createLongGauge(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS)).thenReturn(descriptorReturnedByManager);
        StatisticDescriptor result = internalDistributedSystem.createLongGauge(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS);
        assertThat(result).isSameAs(descriptorReturnedByManager);
    }

    @Test
    public void delegatesCreateDoubleGaugeToStatisticsManager() {
        StatisticDescriptor descriptorReturnedByManager = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(statisticsManager.createDoubleGauge(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS)).thenReturn(descriptorReturnedByManager);
        StatisticDescriptor result = internalDistributedSystem.createDoubleGauge(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS);
        assertThat(result).isSameAs(descriptorReturnedByManager);
    }

    @Test
    public void delegatesCreateLargerBetterIntCounterToStatisticsManager() {
        StatisticDescriptor descriptorReturnedByManager = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(statisticsManager.createIntCounter(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS, false)).thenReturn(descriptorReturnedByManager);
        StatisticDescriptor result = internalDistributedSystem.createIntCounter(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS, false);
        assertThat(result).isSameAs(descriptorReturnedByManager);
    }

    @Test
    public void delegatesCreateLargerBetterLongCounterToStatisticsManager() {
        StatisticDescriptor descriptorReturnedByManager = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(statisticsManager.createLongCounter(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS, false)).thenReturn(descriptorReturnedByManager);
        StatisticDescriptor result = internalDistributedSystem.createLongCounter(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS, false);
        assertThat(result).isSameAs(descriptorReturnedByManager);
    }

    @Test
    public void delegatesCreateLargerBetterDoubleCounterToStatisticsManager() {
        StatisticDescriptor descriptorReturnedByManager = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(statisticsManager.createDoubleCounter(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS, false)).thenReturn(descriptorReturnedByManager);
        StatisticDescriptor result = internalDistributedSystem.createDoubleCounter(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS, false);
        assertThat(result).isSameAs(descriptorReturnedByManager);
    }

    @Test
    public void delegatesCreateLargerBetterIntGaugeToStatisticsManager() {
        StatisticDescriptor descriptorReturnedByManager = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(statisticsManager.createIntGauge(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS, false)).thenReturn(descriptorReturnedByManager);
        StatisticDescriptor result = internalDistributedSystem.createIntGauge(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS, false);
        assertThat(result).isSameAs(descriptorReturnedByManager);
    }

    @Test
    public void delegatesCreateLargerBetterLongGaugeToStatisticsManager() {
        StatisticDescriptor descriptorReturnedByManager = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(statisticsManager.createLongGauge(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS, false)).thenReturn(descriptorReturnedByManager);
        StatisticDescriptor result = internalDistributedSystem.createLongGauge(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS, false);
        assertThat(result).isSameAs(descriptorReturnedByManager);
    }

    @Test
    public void delegatesCreateLargerBetterDoubleGaugeToStatisticsManager() {
        StatisticDescriptor descriptorReturnedByManager = Mockito.mock(StatisticDescriptor.class);
        Mockito.when(statisticsManager.createDoubleGauge(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS, false)).thenReturn(descriptorReturnedByManager);
        StatisticDescriptor result = internalDistributedSystem.createDoubleGauge(InternalDistributedSystemTest.STATISTIC_NAME, InternalDistributedSystemTest.STATISTIC_DESCRIPTION, InternalDistributedSystemTest.STATISTIC_UNITS, false);
        assertThat(result).isSameAs(descriptorReturnedByManager);
    }

    @Test
    public void delegatesCreateStatisticsToStatisticsManager() {
        Statistics statisticsReturnedByManager = Mockito.mock(Statistics.class);
        Mockito.when(statisticsManager.createStatistics(InternalDistributedSystemTest.STATISTICS_TYPE)).thenReturn(statisticsReturnedByManager);
        Statistics result = internalDistributedSystem.createStatistics(InternalDistributedSystemTest.STATISTICS_TYPE);
        assertThat(result).isSameAs(statisticsReturnedByManager);
    }

    @Test
    public void delegatesCreateStatisticsWithTextIdToStatisticsManager() {
        Statistics statisticsReturnedByManager = Mockito.mock(Statistics.class);
        Mockito.when(statisticsManager.createStatistics(InternalDistributedSystemTest.STATISTICS_TYPE, InternalDistributedSystemTest.STATISTICS_TEXT_ID)).thenReturn(statisticsReturnedByManager);
        Statistics result = internalDistributedSystem.createStatistics(InternalDistributedSystemTest.STATISTICS_TYPE, InternalDistributedSystemTest.STATISTICS_TEXT_ID);
        assertThat(result).isSameAs(statisticsReturnedByManager);
    }

    @Test
    public void delegatesCreateStatisticsWithNumericIdToStatisticsManager() {
        Statistics statisticsReturnedByManager = Mockito.mock(Statistics.class);
        Mockito.when(statisticsManager.createStatistics(InternalDistributedSystemTest.STATISTICS_TYPE, InternalDistributedSystemTest.STATISTICS_TEXT_ID, InternalDistributedSystemTest.STATISTICS_NUMERIC_ID)).thenReturn(statisticsReturnedByManager);
        Statistics result = internalDistributedSystem.createStatistics(InternalDistributedSystemTest.STATISTICS_TYPE, InternalDistributedSystemTest.STATISTICS_TEXT_ID, InternalDistributedSystemTest.STATISTICS_NUMERIC_ID);
        assertThat(result).isSameAs(statisticsReturnedByManager);
    }

    @Test
    public void delegatesCreateAtomicStatisticsToStatisticsManager() {
        Statistics statisticsReturnedByManager = Mockito.mock(Statistics.class);
        Mockito.when(statisticsManager.createAtomicStatistics(InternalDistributedSystemTest.STATISTICS_TYPE)).thenReturn(statisticsReturnedByManager);
        Statistics result = internalDistributedSystem.createAtomicStatistics(InternalDistributedSystemTest.STATISTICS_TYPE);
        assertThat(result).isSameAs(statisticsReturnedByManager);
    }

    @Test
    public void delegatesCreateAtomicStatisticsWithTextIdToStatisticsManager() {
        Statistics statisticsReturnedByManager = Mockito.mock(Statistics.class);
        Mockito.when(statisticsManager.createAtomicStatistics(InternalDistributedSystemTest.STATISTICS_TYPE, InternalDistributedSystemTest.STATISTICS_TEXT_ID)).thenReturn(statisticsReturnedByManager);
        Statistics result = internalDistributedSystem.createAtomicStatistics(InternalDistributedSystemTest.STATISTICS_TYPE, InternalDistributedSystemTest.STATISTICS_TEXT_ID);
        assertThat(result).isSameAs(statisticsReturnedByManager);
    }

    @Test
    public void delegatesCreateAtomicStatisticsWithNumericIdToStatisticsManager() {
        Statistics statisticsReturnedByManager = Mockito.mock(Statistics.class);
        Mockito.when(statisticsManager.createAtomicStatistics(InternalDistributedSystemTest.STATISTICS_TYPE, InternalDistributedSystemTest.STATISTICS_TEXT_ID, InternalDistributedSystemTest.STATISTICS_NUMERIC_ID)).thenReturn(statisticsReturnedByManager);
        Statistics result = internalDistributedSystem.createAtomicStatistics(InternalDistributedSystemTest.STATISTICS_TYPE, InternalDistributedSystemTest.STATISTICS_TEXT_ID, InternalDistributedSystemTest.STATISTICS_NUMERIC_ID);
        assertThat(result).isSameAs(statisticsReturnedByManager);
    }

    @Test
    public void delegatesFindStatisticsByTypeToStatisticsManager() {
        Statistics[] statisticsReturnedByManager = new Statistics[]{  };
        Mockito.when(statisticsManager.findStatisticsByType(InternalDistributedSystemTest.STATISTICS_TYPE)).thenReturn(statisticsReturnedByManager);
        Statistics[] result = internalDistributedSystem.findStatisticsByType(InternalDistributedSystemTest.STATISTICS_TYPE);
        assertThat(result).isSameAs(statisticsReturnedByManager);
    }

    @Test
    public void delegatesFindStatisticsByTextIdToStatisticsManager() {
        Statistics[] statisticsReturnedByManager = new Statistics[]{  };
        String soughtTextId = "sought-text-id";
        Mockito.when(statisticsManager.findStatisticsByTextId(soughtTextId)).thenReturn(statisticsReturnedByManager);
        Statistics[] result = internalDistributedSystem.findStatisticsByTextId(soughtTextId);
        assertThat(result).isSameAs(statisticsReturnedByManager);
    }

    @Test
    public void delegatesFindStatisticsByNumericIdToStatisticsManager() {
        Statistics[] statisticsReturnedByManager = new Statistics[]{  };
        long soughtNumericId = 836282;
        Mockito.when(statisticsManager.findStatisticsByNumericId(soughtNumericId)).thenReturn(statisticsReturnedByManager);
        Statistics[] result = internalDistributedSystem.findStatisticsByNumericId(soughtNumericId);
        assertThat(result).isSameAs(statisticsReturnedByManager);
    }
}

