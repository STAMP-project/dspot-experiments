package com.codahale.metrics;


import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class FixedNameCsvFileProviderTest {
    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private File dataDirectory;

    @Test
    public void testGetFile() {
        FixedNameCsvFileProvider provider = new FixedNameCsvFileProvider();
        File file = provider.getFile(dataDirectory, "test");
        assertThat(file.getParentFile()).isEqualTo(dataDirectory);
        assertThat(file.getName()).isEqualTo("test.csv");
    }

    @Test
    public void testGetFileSanitize() {
        FixedNameCsvFileProvider provider = new FixedNameCsvFileProvider();
        File file = provider.getFile(dataDirectory, "/myfake/uri");
        assertThat(file.getParentFile()).isEqualTo(dataDirectory);
        assertThat(file.getName()).isEqualTo("myfake.uri.csv");
    }
}

