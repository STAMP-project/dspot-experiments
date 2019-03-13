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


import C.TRACK_TYPE_UNKNOWN;
import RuntimeEnvironment.application;
import TsPayloadReader.Factory;
import TsPayloadReader.Flags;
import android.util.SparseArray;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader.EsInfo;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import com.google.android.exoplayer2.testutil.ExtractorAsserts;
import com.google.android.exoplayer2.testutil.FakeExtractorInput;
import com.google.android.exoplayer2.testutil.FakeExtractorOutput;
import com.google.android.exoplayer2.testutil.FakeTrackOutput;
import com.google.android.exoplayer2.testutil.TestUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import java.io.ByteArrayOutputStream;
import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static TsExtractor.MODE_MULTI_PMT;


/**
 * Unit test for {@link TsExtractor}.
 */
@RunWith(RobolectricTestRunner.class)
public final class TsExtractorTest {
    private static final int TS_PACKET_SIZE = 188;

    private static final int TS_SYNC_BYTE = 71;// First byte of each TS packet.


    @Test
    public void testSample() throws Exception {
        ExtractorAsserts.assertBehavior(TsExtractor::new, "ts/sample.ts");
    }

    @Test
    public void testStreamWithJunkData() throws Exception {
        Random random = new Random(0);
        byte[] fileData = TestUtil.getByteArray(application, "ts/sample.ts");
        ByteArrayOutputStream out = new ByteArrayOutputStream(((fileData.length) * 2));
        int bytesLeft = fileData.length;
        TsExtractorTest.writeJunkData(out, ((random.nextInt(((TsExtractorTest.TS_PACKET_SIZE) - 1))) + 1));
        out.write(fileData, 0, ((TsExtractorTest.TS_PACKET_SIZE) * 5));
        bytesLeft -= (TsExtractorTest.TS_PACKET_SIZE) * 5;
        for (int i = (TsExtractorTest.TS_PACKET_SIZE) * 5; i < (fileData.length); i += 5 * (TsExtractorTest.TS_PACKET_SIZE)) {
            TsExtractorTest.writeJunkData(out, random.nextInt(TsExtractorTest.TS_PACKET_SIZE));
            int length = Math.min((5 * (TsExtractorTest.TS_PACKET_SIZE)), bytesLeft);
            out.write(fileData, i, length);
            bytesLeft -= length;
        }
        out.write(TsExtractorTest.TS_SYNC_BYTE);
        TsExtractorTest.writeJunkData(out, ((random.nextInt(((TsExtractorTest.TS_PACKET_SIZE) - 1))) + 1));
        fileData = out.toByteArray();
        ExtractorAsserts.assertOutput(TsExtractor::new, "ts/sample.ts", fileData, application);
    }

    @Test
    public void testCustomPesReader() throws Exception {
        TsExtractorTest.CustomTsPayloadReaderFactory factory = new TsExtractorTest.CustomTsPayloadReaderFactory(true, false);
        TsExtractor tsExtractor = new TsExtractor(MODE_MULTI_PMT, new TimestampAdjuster(0), factory);
        FakeExtractorInput input = new FakeExtractorInput.Builder().setData(TestUtil.getByteArray(application, "ts/sample.ts")).setSimulateIOErrors(false).setSimulateUnknownLength(false).setSimulatePartialReads(false).build();
        FakeExtractorOutput output = new FakeExtractorOutput();
        tsExtractor.init(output);
        PositionHolder seekPositionHolder = new PositionHolder();
        int readResult = Extractor.RESULT_CONTINUE;
        while (readResult != (Extractor.RESULT_END_OF_INPUT)) {
            readResult = tsExtractor.read(input, seekPositionHolder);
            if (readResult == (Extractor.RESULT_SEEK)) {
                input.setPosition(((int) (seekPositionHolder.position)));
            }
        } 
        TsExtractorTest.CustomEsReader reader = factory.esReader;
        assertThat(reader.packetsRead).isEqualTo(2);
        TrackOutput trackOutput = reader.getTrackOutput();
        assertThat((trackOutput == /* PID of audio track. */
        (output.trackOutputs.get(257)))).isTrue();
        assertThat(((FakeTrackOutput) (trackOutput)).format).isEqualTo(Format.createTextSampleFormat("1/257", "mime", null, 0, 0, "und", null, 0));
    }

    @Test
    public void testCustomInitialSectionReader() throws Exception {
        TsExtractorTest.CustomTsPayloadReaderFactory factory = new TsExtractorTest.CustomTsPayloadReaderFactory(false, true);
        TsExtractor tsExtractor = new TsExtractor(MODE_MULTI_PMT, new TimestampAdjuster(0), factory);
        FakeExtractorInput input = new FakeExtractorInput.Builder().setData(TestUtil.getByteArray(application, "ts/sample_with_sdt.ts")).setSimulateIOErrors(false).setSimulateUnknownLength(false).setSimulatePartialReads(false).build();
        tsExtractor.init(new FakeExtractorOutput());
        PositionHolder seekPositionHolder = new PositionHolder();
        int readResult = Extractor.RESULT_CONTINUE;
        while (readResult != (Extractor.RESULT_END_OF_INPUT)) {
            readResult = tsExtractor.read(input, seekPositionHolder);
            if (readResult == (Extractor.RESULT_SEEK)) {
                input.setPosition(((int) (seekPositionHolder.position)));
            }
        } 
        assertThat(factory.sdtReader.consumedSdts).isEqualTo(2);
    }

    private static final class CustomTsPayloadReaderFactory implements TsPayloadReader.Factory {
        private final boolean provideSdtReader;

        private final boolean provideCustomEsReader;

        private final Factory defaultFactory;

        private TsExtractorTest.CustomEsReader esReader;

        private TsExtractorTest.SdtSectionReader sdtReader;

        public CustomTsPayloadReaderFactory(boolean provideCustomEsReader, boolean provideSdtReader) {
            this.provideCustomEsReader = provideCustomEsReader;
            this.provideSdtReader = provideSdtReader;
            defaultFactory = new DefaultTsPayloadReaderFactory();
        }

        @Override
        public SparseArray<TsPayloadReader> createInitialPayloadReaders() {
            if (provideSdtReader) {
                assertThat(sdtReader).isNull();
                SparseArray<TsPayloadReader> mapping = new SparseArray();
                sdtReader = new TsExtractorTest.SdtSectionReader();
                mapping.put(17, new SectionReader(sdtReader));
                return mapping;
            } else {
                return defaultFactory.createInitialPayloadReaders();
            }
        }

        @Override
        public TsPayloadReader createPayloadReader(int streamType, EsInfo esInfo) {
            if ((provideCustomEsReader) && (streamType == 3)) {
                esReader = new TsExtractorTest.CustomEsReader(esInfo.language);
                return new PesReader(esReader);
            } else {
                return defaultFactory.createPayloadReader(streamType, esInfo);
            }
        }
    }

    private static final class CustomEsReader implements ElementaryStreamReader {
        private final String language;

        private TrackOutput output;

        public int packetsRead = 0;

        public CustomEsReader(String language) {
            this.language = language;
        }

        @Override
        public void seek() {
        }

        @Override
        public void createTracks(ExtractorOutput extractorOutput, TrackIdGenerator idGenerator) {
            idGenerator.generateNewId();
            output = extractorOutput.track(idGenerator.getTrackId(), TRACK_TYPE_UNKNOWN);
            output.format(Format.createTextSampleFormat(idGenerator.getFormatId(), "mime", null, 0, 0, language, null, 0));
        }

        @Override
        public void packetStarted(long pesTimeUs, @TsPayloadReader.Flags
        int flags) {
        }

        @Override
        public void consume(ParsableByteArray data) {
        }

        @Override
        public void packetFinished() {
            (packetsRead)++;
        }

        public TrackOutput getTrackOutput() {
            return output;
        }
    }

    private static final class SdtSectionReader implements SectionPayloadReader {
        private int consumedSdts;

        @Override
        public void init(TimestampAdjuster timestampAdjuster, ExtractorOutput extractorOutput, TrackIdGenerator idGenerator) {
            // Do nothing.
        }

        @Override
        public void consume(ParsableByteArray sectionData) {
            // table_id(8), section_syntax_indicator(1), reserved_future_use(1), reserved(2),
            // section_length(12), transport_stream_id(16), reserved(2), version_number(5),
            // current_next_indicator(1), section_number(8), last_section_number(8),
            // original_network_id(16), reserved_future_use(8)
            sectionData.skipBytes(11);
            // Start of the service loop.
            /* arbitrary service id */
            assertThat(sectionData.readUnsignedShort()).isEqualTo(21862);
            // reserved_future_use(6), EIT_schedule_flag(1), EIT_present_following_flag(1)
            sectionData.skipBytes(1);
            // Assert there is only one service.
            // Remove running_status(3), free_CA_mode(1) from the descriptors_loop_length with the mask.
            assertThat(((sectionData.readUnsignedShort()) & 4095)).isEqualTo(sectionData.bytesLeft());
            while ((sectionData.bytesLeft()) > 0) {
                int descriptorTag = sectionData.readUnsignedByte();
                int descriptorLength = sectionData.readUnsignedByte();
                /* service descriptor */
                if (descriptorTag == 72) {
                    assertThat(sectionData.readUnsignedByte()).isEqualTo(1);// Service type: Digital TV.

                    int serviceProviderNameLength = sectionData.readUnsignedByte();
                    assertThat(sectionData.readString(serviceProviderNameLength)).isEqualTo("Some provider");
                    int serviceNameLength = sectionData.readUnsignedByte();
                    assertThat(sectionData.readString(serviceNameLength)).isEqualTo("Some Channel");
                } else {
                    sectionData.skipBytes(descriptorLength);
                }
            } 
            (consumedSdts)++;
        }
    }
}

