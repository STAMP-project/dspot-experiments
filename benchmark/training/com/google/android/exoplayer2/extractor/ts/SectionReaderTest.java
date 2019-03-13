/**
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.extractor.ts;


import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Test for {@link SectionReader}.
 */
@RunWith(RobolectricTestRunner.class)
public final class SectionReaderTest {
    private byte[] packetPayload;

    private SectionReaderTest.CustomSectionPayloadReader payloadReader;

    private SectionReader reader;

    @Test
    public void testSingleOnePacketSection() {
        packetPayload[0] = 3;
        insertTableSection(4, ((byte) (99)), 3);
        reader.consume(new ParsableByteArray(packetPayload), TsPayloadReader.FLAG_PAYLOAD_UNIT_START_INDICATOR);
        assertThat(payloadReader.parsedTableIds).isEqualTo(Collections.singletonList(99));
    }

    @Test
    public void testHeaderSplitAcrossPackets() {
        packetPayload[0] = 3;// The first packet includes a pointer_field.

        insertTableSection(4, ((byte) (100)), 3);// This section header spreads across both packets.

        ParsableByteArray firstPacket = new ParsableByteArray(packetPayload, 5);
        reader.consume(firstPacket, TsPayloadReader.FLAG_PAYLOAD_UNIT_START_INDICATOR);
        assertThat(payloadReader.parsedTableIds).isEmpty();
        ParsableByteArray secondPacket = new ParsableByteArray(packetPayload);
        secondPacket.setPosition(5);
        /* flags= */
        reader.consume(secondPacket, 0);
        assertThat(payloadReader.parsedTableIds).isEqualTo(Collections.singletonList(100));
    }

    @Test
    public void testFiveSectionsInTwoPackets() {
        packetPayload[0] = 0;// The first packet includes a pointer_field.

        insertTableSection(1, ((byte) (101)), 10);
        insertTableSection(14, ((byte) (102)), 10);
        insertTableSection(27, ((byte) (103)), 10);
        packetPayload[40] = 0;// The second packet includes a pointer_field.

        insertTableSection(41, ((byte) (104)), 10);
        insertTableSection(54, ((byte) (105)), 10);
        ParsableByteArray firstPacket = new ParsableByteArray(packetPayload, 40);
        reader.consume(firstPacket, TsPayloadReader.FLAG_PAYLOAD_UNIT_START_INDICATOR);
        assertThat(payloadReader.parsedTableIds).isEqualTo(Arrays.asList(101, 102, 103));
        ParsableByteArray secondPacket = new ParsableByteArray(packetPayload);
        secondPacket.setPosition(40);
        reader.consume(secondPacket, TsPayloadReader.FLAG_PAYLOAD_UNIT_START_INDICATOR);
        assertThat(payloadReader.parsedTableIds).isEqualTo(Arrays.asList(101, 102, 103, 104, 105));
    }

    @Test
    public void testLongSectionAcrossFourPackets() {
        packetPayload[0] = 13;// The first packet includes a pointer_field.

        insertTableSection(1, ((byte) (106)), 10);// First section. Should be skipped.

        // Second section spread across four packets. Should be consumed.
        insertTableSection(14, ((byte) (107)), 300);
        packetPayload[300] = 17;// The third packet includes a pointer_field.

        // Third section, at the payload start of the fourth packet. Should be consumed.
        insertTableSection(318, ((byte) (108)), 10);
        ParsableByteArray firstPacket = new ParsableByteArray(packetPayload, 100);
        reader.consume(firstPacket, TsPayloadReader.FLAG_PAYLOAD_UNIT_START_INDICATOR);
        assertThat(payloadReader.parsedTableIds).isEmpty();
        ParsableByteArray secondPacket = new ParsableByteArray(packetPayload, 200);
        secondPacket.setPosition(100);
        /* flags= */
        reader.consume(secondPacket, 0);
        assertThat(payloadReader.parsedTableIds).isEmpty();
        ParsableByteArray thirdPacket = new ParsableByteArray(packetPayload, 300);
        thirdPacket.setPosition(200);
        /* flags= */
        reader.consume(thirdPacket, 0);
        assertThat(payloadReader.parsedTableIds).isEmpty();
        ParsableByteArray fourthPacket = new ParsableByteArray(packetPayload);
        fourthPacket.setPosition(300);
        reader.consume(fourthPacket, TsPayloadReader.FLAG_PAYLOAD_UNIT_START_INDICATOR);
        assertThat(payloadReader.parsedTableIds).isEqualTo(Arrays.asList(107, 108));
    }

    @Test
    public void testSeek() {
        packetPayload[0] = 13;// The first packet includes a pointer_field.

        insertTableSection(1, ((byte) (109)), 10);// First section. Should be skipped.

        // Second section spread across four packets. Should be consumed.
        insertTableSection(14, ((byte) (110)), 300);
        packetPayload[300] = 17;// The third packet includes a pointer_field.

        // Third section, at the payload start of the fourth packet. Should be consumed.
        insertTableSection(318, ((byte) (111)), 10);
        ParsableByteArray firstPacket = new ParsableByteArray(packetPayload, 100);
        reader.consume(firstPacket, TsPayloadReader.FLAG_PAYLOAD_UNIT_START_INDICATOR);
        assertThat(payloadReader.parsedTableIds).isEmpty();
        ParsableByteArray secondPacket = new ParsableByteArray(packetPayload, 200);
        secondPacket.setPosition(100);
        /* flags= */
        reader.consume(secondPacket, 0);
        assertThat(payloadReader.parsedTableIds).isEmpty();
        ParsableByteArray thirdPacket = new ParsableByteArray(packetPayload, 300);
        thirdPacket.setPosition(200);
        /* flags= */
        reader.consume(thirdPacket, 0);
        assertThat(payloadReader.parsedTableIds).isEmpty();
        reader.seek();
        ParsableByteArray fourthPacket = new ParsableByteArray(packetPayload);
        fourthPacket.setPosition(300);
        reader.consume(fourthPacket, TsPayloadReader.FLAG_PAYLOAD_UNIT_START_INDICATOR);
        assertThat(payloadReader.parsedTableIds).isEqualTo(Collections.singletonList(111));
    }

    @Test
    public void testCrcChecks() {
        byte[] correctCrcPat = new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (176)), ((byte) (13)), ((byte) (0)), ((byte) (1)), ((byte) (193)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (1)), ((byte) (225)), ((byte) (0)), ((byte) (232)), ((byte) (249)), ((byte) (94)), ((byte) (125)) };
        byte[] incorrectCrcPat = Arrays.copyOf(correctCrcPat, correctCrcPat.length);
        // Crc field is incorrect, and should not be passed to the payload reader.
        (incorrectCrcPat[16])--;
        reader.consume(new ParsableByteArray(correctCrcPat), TsPayloadReader.FLAG_PAYLOAD_UNIT_START_INDICATOR);
        assertThat(payloadReader.parsedTableIds).isEqualTo(Collections.singletonList(0));
        reader.consume(new ParsableByteArray(incorrectCrcPat), TsPayloadReader.FLAG_PAYLOAD_UNIT_START_INDICATOR);
        assertThat(payloadReader.parsedTableIds).isEqualTo(Collections.singletonList(0));
    }

    // Internal classes.
    private static final class CustomSectionPayloadReader implements SectionPayloadReader {
        List<Integer> parsedTableIds;

        @Override
        public void init(TimestampAdjuster timestampAdjuster, ExtractorOutput extractorOutput, TsPayloadReader.TrackIdGenerator idGenerator) {
            parsedTableIds = new ArrayList<>();
        }

        @Override
        public void consume(ParsableByteArray sectionData) {
            parsedTableIds.add(sectionData.readUnsignedByte());
        }
    }
}

