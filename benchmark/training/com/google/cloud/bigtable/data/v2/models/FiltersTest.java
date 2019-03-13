/**
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigtable.data.v2.models;


import com.google.bigtable.v2.ColumnRange;
import com.google.bigtable.v2.RowFilter;
import com.google.bigtable.v2.RowFilter.Chain;
import com.google.bigtable.v2.RowFilter.Condition;
import com.google.bigtable.v2.RowFilter.Interleave;
import com.google.bigtable.v2.TimestampRange;
import com.google.bigtable.v2.ValueRange;
import com.google.protobuf.ByteString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class FiltersTest {
    @Test
    public void chainTest() {
        RowFilter actualProto = Filters.FILTERS.chain().filter(Filters.FILTERS.key().regex(".*")).filter(Filters.FILTERS.key().sample(0.5)).filter(Filters.FILTERS.chain().filter(Filters.FILTERS.family().regex("hi$")).filter(Filters.FILTERS.pass())).toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setChain(Chain.newBuilder().addFilters(RowFilter.newBuilder().setRowKeyRegexFilter(ByteString.copyFromUtf8(".*"))).addFilters(RowFilter.newBuilder().setRowSampleFilter(0.5)).addFilters(RowFilter.newBuilder().setChain(Chain.newBuilder().addFilters(RowFilter.newBuilder().setFamilyNameRegexFilter("hi$")).addFilters(RowFilter.newBuilder().setPassAllFilter(true))))).build();
        assertThat(actualProto).isEqualTo(expectedFilter);
    }

    @Test
    public void chainEmptyTest() {
        RowFilter actualProto = Filters.FILTERS.chain().toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setPassAllFilter(true).build();
        assertThat(actualProto).isEqualTo(expectedFilter);
    }

    @Test
    public void chainSingleTest() {
        RowFilter actualProto = Filters.FILTERS.chain().filter(Filters.FILTERS.key().regex(".*")).toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setRowKeyRegexFilter(ByteString.copyFromUtf8(".*")).build();
        assertThat(actualProto).isEqualTo(expectedFilter);
    }

    @Test
    public void interleaveTest() {
        RowFilter actualProto = Filters.FILTERS.interleave().filter(Filters.FILTERS.key().regex(".*")).filter(Filters.FILTERS.key().sample(0.5)).filter(Filters.FILTERS.interleave().filter(Filters.FILTERS.family().regex("hi$")).filter(Filters.FILTERS.pass())).toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setInterleave(Interleave.newBuilder().addFilters(RowFilter.newBuilder().setRowKeyRegexFilter(ByteString.copyFromUtf8(".*"))).addFilters(RowFilter.newBuilder().setRowSampleFilter(0.5)).addFilters(RowFilter.newBuilder().setInterleave(Interleave.newBuilder().addFilters(RowFilter.newBuilder().setFamilyNameRegexFilter("hi$")).addFilters(RowFilter.newBuilder().setPassAllFilter(true).build())))).build();
        assertThat(actualProto).isEqualTo(expectedFilter);
    }

    @Test
    public void interleaveEmptyTest() {
        RowFilter actualProto = Filters.FILTERS.chain().toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setPassAllFilter(true).build();
        assertThat(actualProto).isEqualTo(expectedFilter);
    }

    @Test
    public void interleaveSingleTest() {
        RowFilter actualProto = Filters.FILTERS.interleave().filter(Filters.FILTERS.key().regex(".*")).toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setRowKeyRegexFilter(ByteString.copyFromUtf8(".*")).build();
        assertThat(actualProto).isEqualTo(expectedFilter);
    }

    @Test
    public void conditionTest() {
        RowFilter actualFilter = Filters.FILTERS.condition(Filters.FILTERS.key().regex(".*")).then(Filters.FILTERS.label("true")).otherwise(Filters.FILTERS.label("false")).toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setCondition(Condition.newBuilder().setPredicateFilter(RowFilter.newBuilder().setRowKeyRegexFilter(ByteString.copyFromUtf8(".*"))).setTrueFilter(RowFilter.newBuilder().setApplyLabelTransformer("true")).setFalseFilter(RowFilter.newBuilder().setApplyLabelTransformer("false"))).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void keyRegexTest() {
        RowFilter actualFilter = Filters.FILTERS.key().regex(ByteString.copyFromUtf8(".*")).toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setRowKeyRegexFilter(ByteString.copyFromUtf8(".*")).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void keyRegexStringTest() {
        RowFilter actualFilter = Filters.FILTERS.key().regex(".*").toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setRowKeyRegexFilter(ByteString.copyFromUtf8(".*")).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void keyExactMatchTest() {
        RowFilter actualFilter = Filters.FILTERS.key().exactMatch(ByteString.copyFromUtf8(".*")).toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setRowKeyRegexFilter(ByteString.copyFromUtf8("\\.\\*")).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void keyExactMatchStringTest() {
        RowFilter actualFilter = Filters.FILTERS.key().exactMatch(".*").toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setRowKeyRegexFilter(ByteString.copyFromUtf8("\\.\\*")).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void keySampleTest() {
        RowFilter actualFilter = Filters.FILTERS.key().sample(0.3).toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setRowSampleFilter(0.3).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void familyRegexTest() {
        RowFilter actualFilter = Filters.FILTERS.family().regex("^hi").toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setFamilyNameRegexFilter("^hi").build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void familyExactMatchTest() {
        RowFilter actualFilter = Filters.FILTERS.family().exactMatch("^hi").toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setFamilyNameRegexFilter("\\^hi").build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void qualifierRegexTest() {
        RowFilter actualFilter = Filters.FILTERS.qualifier().regex(ByteString.copyFromUtf8("^hi")).toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setColumnQualifierRegexFilter(ByteString.copyFromUtf8("^hi")).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void qualifierRegexStringTest() {
        RowFilter actualFilter = Filters.FILTERS.qualifier().regex("^hi").toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setColumnQualifierRegexFilter(ByteString.copyFromUtf8("^hi")).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void qualifierExactMatchTest() {
        RowFilter actualFilter = Filters.FILTERS.qualifier().exactMatch(ByteString.copyFromUtf8("^hi")).toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setColumnQualifierRegexFilter(ByteString.copyFromUtf8("\\^hi")).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void qualifierExactStringMatchTest() {
        RowFilter actualFilter = Filters.FILTERS.qualifier().exactMatch("^hi").toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setColumnQualifierRegexFilter(ByteString.copyFromUtf8("\\^hi")).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void qualifierRangeInFamilyClosedOpen() {
        RowFilter actualFilter = Filters.FILTERS.qualifier().rangeWithinFamily("family").startClosed("begin").endOpen("end").toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setColumnRangeFilter(ColumnRange.newBuilder().setFamilyName("family").setStartQualifierClosed(ByteString.copyFromUtf8("begin")).setEndQualifierOpen(ByteString.copyFromUtf8("end"))).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void qualifierRangeInFamilyOpenClosed() {
        RowFilter actualFilter = Filters.FILTERS.qualifier().rangeWithinFamily("family").startOpen("begin").endClosed("end").toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setColumnRangeFilter(ColumnRange.newBuilder().setFamilyName("family").setStartQualifierOpen(ByteString.copyFromUtf8("begin")).setEndQualifierClosed(ByteString.copyFromUtf8("end"))).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void qualifierRangeRange() {
        RowFilter actualFilter = Filters.FILTERS.qualifier().rangeWithinFamily("family").startClosed("begin").endOpen("end").toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setColumnRangeFilter(ColumnRange.newBuilder().setFamilyName("family").setStartQualifierClosed(ByteString.copyFromUtf8("begin")).setEndQualifierOpen(ByteString.copyFromUtf8("end"))).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void timestampRange() {
        RowFilter actualFilter = Filters.FILTERS.timestamp().range().startClosed(1000L).endOpen(30000L).toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setTimestampRangeFilter(TimestampRange.newBuilder().setStartTimestampMicros(1000L).setEndTimestampMicros(30000L)).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void timestampOpenClosedFakeRange() {
        RowFilter actualFilter = Filters.FILTERS.timestamp().range().startOpen(1000L).endClosed(30000L).toProto();
        // open start & closed end are faked in the client by incrementing the query
        RowFilter expectedFilter = RowFilter.newBuilder().setTimestampRangeFilter(TimestampRange.newBuilder().setStartTimestampMicros((1000L + 1)).setEndTimestampMicros((30000L + 1))).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void valueRegex() {
        RowFilter actualFilter = Filters.FILTERS.value().regex("some[0-9]regex").toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setValueRegexFilter(ByteString.copyFromUtf8("some[0-9]regex")).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void valueExactMatch() {
        RowFilter actualFilter = Filters.FILTERS.value().exactMatch(ByteString.copyFromUtf8("some[0-9]regex")).toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setValueRegexFilter(ByteString.copyFromUtf8("some\\[0\\-9\\]regex")).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void valueExactStringMatch() {
        RowFilter actualFilter = Filters.FILTERS.value().exactMatch("some[0-9]regex").toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setValueRegexFilter(ByteString.copyFromUtf8("some\\[0\\-9\\]regex")).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void valueRangeClosedOpen() {
        RowFilter actualFilter = Filters.FILTERS.value().range().startClosed("begin").endOpen("end").toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setValueRangeFilter(ValueRange.newBuilder().setStartValueClosed(ByteString.copyFromUtf8("begin")).setEndValueOpen(ByteString.copyFromUtf8("end"))).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void valueRangeOpenClosed() {
        RowFilter actualFilter = Filters.FILTERS.value().range().startOpen("begin").endClosed("end").toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setValueRangeFilter(ValueRange.newBuilder().setStartValueOpen(ByteString.copyFromUtf8("begin")).setEndValueClosed(ByteString.copyFromUtf8("end"))).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void valueStripTest() {
        RowFilter actualFilter = Filters.FILTERS.value().strip().toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setStripValueTransformer(true).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void offsetCellsPerRowTest() {
        RowFilter actualFilter = Filters.FILTERS.offset().cellsPerRow(10).toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setCellsPerRowOffsetFilter(10).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void limitCellsPerRowTest() {
        RowFilter actualFilter = Filters.FILTERS.limit().cellsPerRow(10).toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setCellsPerRowLimitFilter(10).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void limitCellsPerColumnTest() {
        RowFilter actualFilter = Filters.FILTERS.limit().cellsPerColumn(10).toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setCellsPerColumnLimitFilter(10).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void fromProtoTest() {
        RowFilter inner = RowFilter.newBuilder().setRowSampleFilter(0.5).build();
        RowFilter actualFilter = Filters.FILTERS.fromProto(inner).toProto();
        assertThat(actualFilter).isEqualTo(inner);
    }

    @Test
    public void passTest() {
        RowFilter actualFilter = Filters.FILTERS.pass().toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setPassAllFilter(true).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void blockTest() {
        RowFilter actualFilter = Filters.FILTERS.block().toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setBlockAllFilter(true).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void sinkTest() {
        RowFilter actualFilter = Filters.FILTERS.sink().toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setSink(true).build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }

    @Test
    public void labelTest() {
        RowFilter actualFilter = Filters.FILTERS.label("my-label").toProto();
        RowFilter expectedFilter = RowFilter.newBuilder().setApplyLabelTransformer("my-label").build();
        assertThat(actualFilter).isEqualTo(expectedFilter);
    }
}

