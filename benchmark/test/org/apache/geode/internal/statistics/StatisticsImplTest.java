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


import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import org.apache.geode.StatisticsType;
import org.apache.geode.internal.statistics.StatisticsImpl.StatisticsLogger;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link StatisticsImpl}.
 */
public class StatisticsImplTest {
    // arbitrary values for constructing a StatisticsImpl
    private static final String ANY_TEXT_ID = null;

    private static final long ANY_NUMERIC_ID = 0;

    private static final long ANY_UNIQUE_ID = 0;

    private static final int ANY_OS_STAT_FLAGS = 0;

    private StatisticsManager statisticsManager;

    private StatisticsTypeImpl statisticsType;

    private StatisticsImpl statistics;

    @Test
    public void invokeIntSuppliersShouldUpdateStats() {
        IntSupplier intSupplier = Mockito.mock(IntSupplier.class);
        Mockito.when(intSupplier.getAsInt()).thenReturn(23);
        statistics.setIntSupplier(4, intSupplier);
        assertThat(statistics.invokeSuppliers()).isEqualTo(0);
        Mockito.verify(intSupplier).getAsInt();
        assertThat(statistics.getInt(4)).isEqualTo(23);
    }

    @Test
    public void invokeLongSuppliersShouldUpdateStats() {
        LongSupplier longSupplier = Mockito.mock(LongSupplier.class);
        Mockito.when(longSupplier.getAsLong()).thenReturn(23L);
        statistics.setLongSupplier(4, longSupplier);
        assertThat(statistics.invokeSuppliers()).isEqualTo(0);
        Mockito.verify(longSupplier).getAsLong();
        assertThat(statistics.getLong(4)).isEqualTo(23L);
    }

    @Test
    public void invokeDoubleSuppliersShouldUpdateStats() {
        DoubleSupplier doubleSupplier = Mockito.mock(DoubleSupplier.class);
        Mockito.when(doubleSupplier.getAsDouble()).thenReturn(23.3);
        statistics.setDoubleSupplier(4, doubleSupplier);
        assertThat(statistics.invokeSuppliers()).isEqualTo(0);
        Mockito.verify(doubleSupplier).getAsDouble();
        assertThat(statistics.getDouble(4)).isEqualTo(23.3);
    }

    @Test
    public void getSupplierCountShouldReturnCorrectCount() {
        IntSupplier intSupplier = Mockito.mock(IntSupplier.class);
        statistics.setIntSupplier(4, intSupplier);
        assertThat(statistics.getSupplierCount()).isEqualTo(1);
    }

    @Test
    public void invokeSuppliersShouldCatchSupplierErrorsAndReturnCount() {
        IntSupplier throwingSupplier = Mockito.mock(IntSupplier.class);
        Mockito.when(throwingSupplier.getAsInt()).thenThrow(NullPointerException.class);
        statistics.setIntSupplier(4, throwingSupplier);
        assertThat(statistics.invokeSuppliers()).isEqualTo(1);
        Mockito.verify(throwingSupplier).getAsInt();
    }

    @Test
    public void invokeSuppliersShouldLogErrorOnlyOnce() {
        StatisticsLogger statisticsLogger = Mockito.mock(StatisticsLogger.class);
        statistics = new StatisticsImplTest.SimpleStatistics(statisticsType, StatisticsImplTest.ANY_TEXT_ID, StatisticsImplTest.ANY_NUMERIC_ID, StatisticsImplTest.ANY_UNIQUE_ID, StatisticsImplTest.ANY_OS_STAT_FLAGS, statisticsManager, statisticsLogger);
        IntSupplier throwingSupplier = Mockito.mock(IntSupplier.class);
        Mockito.when(throwingSupplier.getAsInt()).thenThrow(NullPointerException.class);
        statistics.setIntSupplier(4, throwingSupplier);
        assertThat(statistics.invokeSuppliers()).isEqualTo(1);
        // String message, Object p0, Object p1, Object p2
        Mockito.verify(statisticsLogger).logWarning(ArgumentMatchers.anyString(), ArgumentMatchers.isNull(), ArgumentMatchers.anyInt(), ArgumentMatchers.isA(NullPointerException.class));
        assertThat(statistics.invokeSuppliers()).isEqualTo(1);
        // Make sure the logger isn't invoked again
        Mockito.verify(statisticsLogger).logWarning(ArgumentMatchers.anyString(), ArgumentMatchers.isNull(), ArgumentMatchers.anyInt(), ArgumentMatchers.isA(NullPointerException.class));
    }

    @Test
    public void badSupplierParamShouldThrowError() {
        IntSupplier intSupplier = Mockito.mock(IntSupplier.class);
        Mockito.when(intSupplier.getAsInt()).thenReturn(23);
        Throwable thrown = catchThrowable(() -> statistics.setIntSupplier(23, intSupplier));
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void nonEmptyTextId_usesGivenTextId() {
        String nonEmptyTextId = "non-empty-text-id";
        statistics = new StatisticsImplTest.SimpleStatistics(statisticsType, nonEmptyTextId, StatisticsImplTest.ANY_NUMERIC_ID, StatisticsImplTest.ANY_UNIQUE_ID, StatisticsImplTest.ANY_OS_STAT_FLAGS, statisticsManager);
        assertThat(statistics.getTextId()).isEqualTo(nonEmptyTextId);
    }

    @Test
    public void nullTextId_usesNameFromStatisticsManager() {
        String nameFromStatisticsManager = "statistics-manager-name";
        Mockito.when(statisticsManager.getName()).thenReturn(nameFromStatisticsManager);
        String nullTextId = null;
        statistics = new StatisticsImplTest.SimpleStatistics(statisticsType, nullTextId, StatisticsImplTest.ANY_NUMERIC_ID, StatisticsImplTest.ANY_UNIQUE_ID, StatisticsImplTest.ANY_OS_STAT_FLAGS, statisticsManager);
        assertThat(statistics.getTextId()).isEqualTo(nameFromStatisticsManager);
    }

    @Test
    public void emptyTextId_usesNameFromStatisticsManager() {
        String nameFromStatisticsManager = "statistics-manager-name";
        Mockito.when(statisticsManager.getName()).thenReturn(nameFromStatisticsManager);
        String emptyTextId = "";
        statistics = new StatisticsImplTest.SimpleStatistics(statisticsType, emptyTextId, StatisticsImplTest.ANY_NUMERIC_ID, StatisticsImplTest.ANY_UNIQUE_ID, StatisticsImplTest.ANY_OS_STAT_FLAGS, statisticsManager);
        assertThat(statistics.getTextId()).isEqualTo(nameFromStatisticsManager);
    }

    @Test
    public void positiveNumericId_usesGivenNumericId() {
        int positiveNumericId = 21;
        statistics = new StatisticsImplTest.SimpleStatistics(statisticsType, StatisticsImplTest.ANY_TEXT_ID, positiveNumericId, StatisticsImplTest.ANY_UNIQUE_ID, StatisticsImplTest.ANY_OS_STAT_FLAGS, statisticsManager);
        assertThat(statistics.getNumericId()).isEqualTo(positiveNumericId);
    }

    @Test
    public void negativeNumericId_usesGivenNumericId() {
        int negativeNumericId = -21;
        statistics = new StatisticsImplTest.SimpleStatistics(statisticsType, StatisticsImplTest.ANY_TEXT_ID, negativeNumericId, StatisticsImplTest.ANY_UNIQUE_ID, StatisticsImplTest.ANY_OS_STAT_FLAGS, statisticsManager);
        assertThat(statistics.getNumericId()).isEqualTo(negativeNumericId);
    }

    @Test
    public void zeroNumericId_usesPidFromStatisticsManager() {
        int pidFromStatisticsManager = 42;
        Mockito.when(statisticsManager.getPid()).thenReturn(pidFromStatisticsManager);
        int zeroNumericId = 0;
        statistics = new StatisticsImplTest.SimpleStatistics(statisticsType, StatisticsImplTest.ANY_TEXT_ID, zeroNumericId, StatisticsImplTest.ANY_UNIQUE_ID, StatisticsImplTest.ANY_OS_STAT_FLAGS, statisticsManager);
        assertThat(statistics.getNumericId()).isEqualTo(pidFromStatisticsManager);
    }

    private static class SimpleStatistics extends StatisticsImpl {
        private final Map<Number, Number> values = new HashMap<>();

        SimpleStatistics(StatisticsType type, String textId, long numericId, long uniqueId, int osStatFlags, StatisticsManager statisticsManager) {
            super(type, textId, numericId, uniqueId, osStatFlags, statisticsManager);
        }

        SimpleStatistics(StatisticsType type, String textId, long numericId, long uniqueId, int osStatFlags, StatisticsManager statisticsManager, StatisticsLogger statisticsLogger) {
            super(type, textId, numericId, uniqueId, osStatFlags, statisticsManager, statisticsLogger);
        }

        @Override
        public boolean isAtomic() {
            return false;
        }

        @Override
        protected void _setInt(int offset, int value) {
            values.put(offset, value);
        }

        @Override
        protected void _setLong(int offset, long value) {
            values.put(offset, value);
        }

        @Override
        protected void _setDouble(int offset, double value) {
            values.put(offset, value);
        }

        @Override
        protected int _getInt(int offset) {
            return ((int) (values.get(offset)));
        }

        @Override
        protected long _getLong(int offset) {
            return ((long) (values.get(offset)));
        }

        @Override
        protected double _getDouble(int offset) {
            return ((double) (values.get(offset)));
        }

        @Override
        protected void _incInt(int offset, int delta) {
            values.put(offset, (((int) (values.get(delta))) + 1));
        }

        @Override
        protected void _incLong(int offset, long delta) {
            values.put(offset, (((long) (values.get(delta))) + 1));
        }

        @Override
        protected void _incDouble(int offset, double delta) {
            values.put(offset, (((double) (values.get(delta))) + 1));
        }
    }
}

