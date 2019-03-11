package org.embulk.standards.preview;


import org.embulk.test.TestingEmbulk;
import org.junit.Rule;
import org.junit.Test;


public class TestFilePreview {
    private static final String RESOURCE_NAME_PREFIX = "org/embulk/standards/preview/file/test/";

    @Rule
    public TestingEmbulk embulk = TestingEmbulk.builder().build();

    @Test
    public void testSimple() throws Exception {
        TestFilePreview.assertPreviewedRecords(embulk, "test_simple_load.yml", "test_simple.csv", "test_simple_previewed.csv");
    }

    @Test
    public void changePreviewSampleBufferBytes() throws Exception {
        TestFilePreview.assertPreviewedRecords(embulk, "test_sample_buffer_bytes_load.yml", "test_sample_buffer_bytes_exec.yml", "test_sample_buffer_bytes.csv", "test_sample_buffer_bytes_previewed.csv");
    }
}

