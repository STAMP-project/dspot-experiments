package com.baeldung.opencsv;


import org.junit.Test;


public class OpenCsvIntegrationTest {
    @Test
    public void positionExampleTest() {
        testReadCsv(Application.simpleSyncPositionBeanExample());
    }

    @Test
    public void namedColumnExampleTest() {
        testReadCsv(Application.namedSyncColumnBeanExample());
    }

    @Test
    public void writeCsvUsingBeanBuilderTest() {
        testWriteCsv(Application.writeSyncCsvFromBeanExample());
    }

    @Test
    public void oneByOneExampleTest() {
        testReadCsv(Application.oneByOneSyncExample());
    }

    @Test
    public void readAllExampleTest() {
        testReadCsv(Application.readAllSyncExample());
    }

    @Test
    public void csvWriterOneByOneTest() {
        testWriteCsv(Application.csvWriterSyncOneByOne());
    }

    @Test
    public void csvWriterAllTest() {
        testWriteCsv(Application.csvWriterSyncAll());
    }
}

