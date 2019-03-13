package org.mp4parser.muxer;


import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.junit.Test;
import org.mp4parser.muxer.builder.DefaultMp4Builder;
import org.mp4parser.muxer.builder.FragmentedMp4Builder;
import org.mp4parser.tools.RangeStartMap;


public class CencFileRoundtripTest {
    private String baseDir = CencFileRoundtripTest.class.getProtectionDomain().getCodeSource().getLocation().getFile();

    private Map<UUID, SecretKey> keys;

    private RangeStartMap<Integer, UUID> keyRotation1;

    private RangeStartMap<Integer, UUID> keyRotation2;

    private RangeStartMap<Integer, UUID> keyRotation3;

    @Test
    public void testSingleKeysStdMp4_cbc1() throws IOException {
        testMultipleKeys(new DefaultMp4Builder(), ((baseDir) + "/BBB_qpfile_10sec/BBB_fixedres_B_180x320_80.mp4"), keys, keyRotation1, "cbc1", false);
    }

    @Test
    public void testSingleKeysFragMp4_cbc1() throws IOException {
        testMultipleKeys(new FragmentedMp4Builder(), ((baseDir) + "/BBB_qpfile_10sec/BBB_fixedres_B_180x320_80.mp4"), keys, keyRotation1, "cbc1", false);
    }

    @Test
    public void testSingleKeysStdMp4_cenc() throws IOException {
        testMultipleKeys(new DefaultMp4Builder(), ((baseDir) + "/BBB_qpfile_10sec/BBB_fixedres_B_180x320_80.mp4"), keys, keyRotation1, "cenc", false);
    }

    @Test
    public void testSingleKeysFragMp4_cenc() throws IOException {
        testMultipleKeys(new FragmentedMp4Builder(), ((baseDir) + "/BBB_qpfile_10sec/BBB_fixedres_B_180x320_80.mp4"), keys, keyRotation1, "cenc", false);
    }

    @Test
    public void testClearLeadStdMp4_2_cbc1() throws IOException {
        testMultipleKeys(new DefaultMp4Builder(), ((baseDir) + "/BBB_qpfile_10sec/BBB_fixedres_B_180x320_80.mp4"), keys, keyRotation2, "cbc1", false);
    }

    @Test
    public void testClearLeadFragMp4_2_cbc1() throws IOException {
        testMultipleKeys(new FragmentedMp4Builder(), ((baseDir) + "/BBB_qpfile_10sec/BBB_fixedres_B_180x320_80.mp4"), keys, keyRotation2, "cbc1", false);
    }

    @Test
    public void testClearLeadStdMp4_2_cenc() throws IOException {
        testMultipleKeys(new DefaultMp4Builder(), ((baseDir) + "/BBB_qpfile_10sec/BBB_fixedres_B_180x320_80.mp4"), keys, keyRotation2, "cenc", false);
    }

    @Test
    public void testClearLeadFragMp4_2_cenc() throws IOException {
        testMultipleKeys(new FragmentedMp4Builder(), ((baseDir) + "/BBB_qpfile_10sec/BBB_fixedres_B_180x320_80.mp4"), keys, keyRotation2, "cenc", false);
    }

    @Test
    public void testMultipleKeysStdMp4_2_cbc1() throws IOException {
        testMultipleKeys(new DefaultMp4Builder(), ((baseDir) + "/BBB_qpfile_10sec/BBB_fixedres_B_180x320_80.mp4"), keys, keyRotation3, "cbc1", false);
    }

    @Test
    public void testMultipleKeysFragMp4_2_cbc1() throws IOException {
        testMultipleKeys(new FragmentedMp4Builder(), ((baseDir) + "/BBB_qpfile_10sec/BBB_fixedres_B_180x320_80.mp4"), keys, keyRotation3, "cbc1", false);
    }

    @Test
    public void testMultipleKeysStdMp4_2_cenc() throws IOException {
        testMultipleKeys(new DefaultMp4Builder(), ((baseDir) + "/BBB_qpfile_10sec/BBB_fixedres_B_180x320_80.mp4"), keys, keyRotation3, "cenc", false);
    }

    @Test
    public void testMultipleKeysFragMp4_2_cenc() throws IOException {
        testMultipleKeys(new FragmentedMp4Builder(), ((baseDir) + "/BBB_qpfile_10sec/BBB_fixedres_B_180x320_80.mp4"), keys, keyRotation3, "cenc", false);
    }

    @Test
    public void testMultipleKeysFragMp4_2_cenc_pseudo_encrypted() throws IOException {
        testMultipleKeys(new FragmentedMp4Builder(), ((baseDir) + "/BBB_qpfile_10sec/BBB_fixedres_B_180x320_80.mp4"), keys, keyRotation2, "cenc", true);
    }
}

