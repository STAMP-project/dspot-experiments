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
package com.google.android.exoplayer2.metadata.scte35;


import SpliceInsertCommand.ComponentSplice;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataInputBuffer;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Test for {@link SpliceInfoDecoder}.
 */
@RunWith(RobolectricTestRunner.class)
public final class SpliceInfoDecoderTest {
    private SpliceInfoDecoder decoder;

    private MetadataInputBuffer inputBuffer;

    @Test
    public void testWrappedAroundTimeSignalCommand() {
        byte[] rawTimeSignalSection = new byte[]{ 0// table_id.
        , ((byte) (128))// section_syntax_indicator, private_indicator, reserved, section_length(4).
        , 20// section_length(8).
        , 0// protocol_version.
        , 0// encrypted_packet, encryption_algorithm, pts_adjustment(1).
        , 0, 0, 0, 0// pts_adjustment(32).
        , 0// cw_index.
        , 0// tier(8).
        , 0// tier(4), splice_command_length(4).
        , 5// splice_command_length(8).
        , 6// splice_command_type = time_signal.
        , // Start of splice_time().
        ((byte) (128))// time_specified_flag, reserved, pts_time(1).
        , 82, 3, 2, ((byte) (143))// pts_time(32). PTS for a second after playback position.
        , 0, 0, 0, 0 };// CRC_32 (ignored, check happens at extraction).

        // The playback position is 57:15:58.43 approximately.
        // With this offset, the playback position pts before wrapping is 0x451ebf851.
        Metadata metadata = feedInputBuffer(rawTimeSignalSection, 206158430208L, (-327680L));
        assertThat(metadata.length()).isEqualTo(1);
        assertThat(((TimeSignalCommand) (metadata.get(0))).playbackPositionUs).isEqualTo(SpliceInfoDecoderTest.removePtsConversionPrecisionError(206175207424L, inputBuffer.subsampleOffsetUs));
    }

    @Test
    public void test2SpliceInsertCommands() {
        byte[] rawSpliceInsertCommand1 = new byte[]{ 0// table_id.
        , ((byte) (128))// section_syntax_indicator, private_indicator, reserved, section_length(4).
        , 25// section_length(8).
        , 0// protocol_version.
        , 0// encrypted_packet, encryption_algorithm, pts_adjustment(1).
        , 0, 0, 0, 0// pts_adjustment(32).
        , 0// cw_index.
        , 0// tier(8).
        , 0// tier(4), splice_command_length(4).
        , 14// splice_command_length(8).
        , 5// splice_command_type = splice_insert.
        , // Start of splice_insert().
        0, 0, 0, 66// splice_event_id.
        , 0// splice_event_cancel_indicator, reserved.
        , 64// out_of_network_indicator, program_splice_flag, duration_flag,
        , // splice_immediate_flag, reserved.
        // start of splice_time().
        ((byte) (128))// time_specified_flag, reserved, pts_time(1).
        , 0, 0, 0, 0// PTS for playback position 3s.
        , 0, 16// unique_program_id.
        , 1// avail_num.
        , 2// avails_expected.
        , 0, 0, 0, 0 };// CRC_32 (ignored, check happens at extraction).

        Metadata metadata = feedInputBuffer(rawSpliceInsertCommand1, 2000000, 3000000);
        assertThat(metadata.length()).isEqualTo(1);
        SpliceInsertCommand command = ((SpliceInsertCommand) (metadata.get(0)));
        assertThat(command.spliceEventId).isEqualTo(66);
        assertThat(command.spliceEventCancelIndicator).isFalse();
        assertThat(command.outOfNetworkIndicator).isFalse();
        assertThat(command.programSpliceFlag).isTrue();
        assertThat(command.spliceImmediateFlag).isFalse();
        assertThat(command.programSplicePlaybackPositionUs).isEqualTo(3000000);
        assertThat(command.breakDurationUs).isEqualTo(C.TIME_UNSET);
        assertThat(command.uniqueProgramId).isEqualTo(16);
        assertThat(command.availNum).isEqualTo(1);
        assertThat(command.availsExpected).isEqualTo(2);
        byte[] rawSpliceInsertCommand2 = new byte[]{ 0// table_id.
        , ((byte) (128))// section_syntax_indicator, private_indicator, reserved, section_length(4).
        , 34// section_length(8).
        , 0// protocol_version.
        , 0// encrypted_packet, encryption_algorithm, pts_adjustment(1).
        , 0, 0, 0, 0// pts_adjustment(32).
        , 0// cw_index.
        , 0// tier(8).
        , 0// tier(4), splice_command_length(4).
        , 19// splice_command_length(8).
        , 5// splice_command_type = splice_insert.
        , // Start of splice_insert().
        ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255))// splice_event_id.
        , 0// splice_event_cancel_indicator, reserved.
        , 0// out_of_network_indicator, program_splice_flag, duration_flag,
        , // splice_immediate_flag, reserved.
        2// component_count.
        , 16// component_tag.
        , // start of splice_time().
        ((byte) (129))// time_specified_flag, reserved, pts_time(1).
        , ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255))// PTS for playback position 10s.
        , // start of splice_time().
        17// component_tag.
        , 0// time_specified_flag, reserved.
        , 0, 32// unique_program_id.
        , 1// avail_num.
        , 2// avails_expected.
        , 0, 0, 0, 0 };// CRC_32 (ignored, check happens at extraction).

        // By changing the subsample offset we force adjuster reconstruction.
        long subsampleOffset = 1000011;
        metadata = feedInputBuffer(rawSpliceInsertCommand2, 1000000, subsampleOffset);
        assertThat(metadata.length()).isEqualTo(1);
        command = ((SpliceInsertCommand) (metadata.get(0)));
        assertThat(command.spliceEventId).isEqualTo(4294967295L);
        assertThat(command.spliceEventCancelIndicator).isFalse();
        assertThat(command.outOfNetworkIndicator).isFalse();
        assertThat(command.programSpliceFlag).isFalse();
        assertThat(command.spliceImmediateFlag).isFalse();
        assertThat(command.programSplicePlaybackPositionUs).isEqualTo(C.TIME_UNSET);
        assertThat(command.breakDurationUs).isEqualTo(C.TIME_UNSET);
        List<SpliceInsertCommand.ComponentSplice> componentSplices = command.componentSpliceList;
        assertThat(componentSplices).hasSize(2);
        assertThat(componentSplices.get(0).componentTag).isEqualTo(16);
        assertThat(componentSplices.get(0).componentSplicePlaybackPositionUs).isEqualTo(1000000);
        assertThat(componentSplices.get(1).componentTag).isEqualTo(17);
        assertThat(componentSplices.get(1).componentSplicePts).isEqualTo(C.TIME_UNSET);
        assertThat(command.uniqueProgramId).isEqualTo(32);
        assertThat(command.availNum).isEqualTo(1);
        assertThat(command.availsExpected).isEqualTo(2);
    }
}

