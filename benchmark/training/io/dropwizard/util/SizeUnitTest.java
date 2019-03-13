package io.dropwizard.util;


import SizeUnit.BYTES;
import SizeUnit.GIGABYTES;
import SizeUnit.KILOBYTES;
import SizeUnit.MEGABYTES;
import SizeUnit.TERABYTES;
import org.junit.jupiter.api.Test;


public class SizeUnitTest {
    // BYTES
    @Test
    public void oneByteInBytes() throws Exception {
        assertThat(BYTES.convert(1, BYTES)).isEqualTo(1);
        assertThat(BYTES.toBytes(1)).isEqualTo(1);
    }

    @Test
    public void oneByteInKilobytes() throws Exception {
        assertThat(KILOBYTES.convert(1, BYTES)).isZero();
        assertThat(BYTES.toKilobytes(1)).isZero();
    }

    @Test
    public void oneByteInMegabytes() throws Exception {
        assertThat(MEGABYTES.convert(1, BYTES)).isZero();
        assertThat(BYTES.toMegabytes(1)).isZero();
    }

    @Test
    public void oneByteInGigabytes() throws Exception {
        assertThat(GIGABYTES.convert(1, BYTES)).isZero();
        assertThat(BYTES.toGigabytes(1)).isZero();
    }

    @Test
    public void oneByteInTerabytes() throws Exception {
        assertThat(TERABYTES.convert(1, BYTES)).isZero();
        assertThat(BYTES.toTerabytes(1)).isZero();
    }

    // KILOBYTES
    @Test
    public void oneKilobyteInBytes() throws Exception {
        assertThat(BYTES.convert(1, KILOBYTES)).isEqualTo(1024);
        assertThat(KILOBYTES.toBytes(1)).isEqualTo(1024);
    }

    @Test
    public void oneKilobyteInKilobytes() throws Exception {
        assertThat(KILOBYTES.convert(1, KILOBYTES)).isEqualTo(1);
        assertThat(KILOBYTES.toKilobytes(1)).isEqualTo(1L);
    }

    @Test
    public void oneKilobyteInMegabytes() throws Exception {
        assertThat(MEGABYTES.convert(1, KILOBYTES)).isZero();
        assertThat(KILOBYTES.toMegabytes(1)).isZero();
    }

    @Test
    public void oneKilobyteInGigabytes() throws Exception {
        assertThat(GIGABYTES.convert(1, KILOBYTES)).isZero();
        assertThat(KILOBYTES.toGigabytes(1)).isZero();
    }

    @Test
    public void oneKilobyteInTerabytes() throws Exception {
        assertThat(TERABYTES.convert(1, KILOBYTES)).isZero();
        assertThat(KILOBYTES.toTerabytes(1)).isZero();
    }

    // MEGABYTES
    @Test
    public void oneMegabyteInBytes() throws Exception {
        assertThat(BYTES.convert(1, MEGABYTES)).isEqualTo(1048576);
        assertThat(MEGABYTES.toBytes(1)).isEqualTo(1048576L);
    }

    @Test
    public void oneMegabyteInKilobytes() throws Exception {
        assertThat(KILOBYTES.convert(1, MEGABYTES)).isEqualTo(1024);
        assertThat(MEGABYTES.toKilobytes(1)).isEqualTo(1024);
    }

    @Test
    public void oneMegabyteInMegabytes() throws Exception {
        assertThat(MEGABYTES.convert(1, MEGABYTES)).isEqualTo(1);
        assertThat(MEGABYTES.toMegabytes(1)).isEqualTo(1);
    }

    @Test
    public void oneMegabyteInGigabytes() throws Exception {
        assertThat(GIGABYTES.convert(1, MEGABYTES)).isZero();
        assertThat(MEGABYTES.toGigabytes(1)).isZero();
    }

    @Test
    public void oneMegabyteInTerabytes() throws Exception {
        assertThat(TERABYTES.convert(1, MEGABYTES)).isZero();
        assertThat(MEGABYTES.toTerabytes(1)).isZero();
    }

    // GIGABYTES
    @Test
    public void oneGigabyteInBytes() throws Exception {
        assertThat(BYTES.convert(1, GIGABYTES)).isEqualTo(1073741824);
        assertThat(GIGABYTES.toBytes(1)).isEqualTo(1073741824);
    }

    @Test
    public void oneGigabyteInKilobytes() throws Exception {
        assertThat(KILOBYTES.convert(1, GIGABYTES)).isEqualTo(1048576);
        assertThat(GIGABYTES.toKilobytes(1)).isEqualTo(1048576);
    }

    @Test
    public void oneGigabyteInMegabytes() throws Exception {
        assertThat(MEGABYTES.convert(1, GIGABYTES)).isEqualTo(1024);
        assertThat(GIGABYTES.toMegabytes(1)).isEqualTo(1024);
    }

    @Test
    public void oneGigabyteInGigabytes() throws Exception {
        assertThat(GIGABYTES.convert(1, GIGABYTES)).isEqualTo(1L);
        assertThat(GIGABYTES.toGigabytes(1)).isEqualTo(1L);
    }

    @Test
    public void oneGigabyteInTerabytes() throws Exception {
        assertThat(TERABYTES.convert(1, GIGABYTES)).isZero();
        assertThat(GIGABYTES.toTerabytes(1)).isZero();
    }

    // TERABYTES
    @Test
    public void oneTerabyteInBytes() throws Exception {
        assertThat(BYTES.convert(1, TERABYTES)).isEqualTo(1099511627776L);
        assertThat(TERABYTES.toBytes(1)).isEqualTo(1099511627776L);
    }

    @Test
    public void oneTerabyteInKilobytes() throws Exception {
        assertThat(KILOBYTES.convert(1, TERABYTES)).isEqualTo(1073741824L);
        assertThat(TERABYTES.toKilobytes(1)).isEqualTo(1073741824L);
    }

    @Test
    public void oneTerabyteInMegabytes() throws Exception {
        assertThat(MEGABYTES.convert(1, TERABYTES)).isEqualTo(1048576);
        assertThat(TERABYTES.toMegabytes(1)).isEqualTo(1048576L);
    }

    @Test
    public void oneTerabyteInGigabytes() throws Exception {
        assertThat(GIGABYTES.convert(1, TERABYTES)).isEqualTo(1024);
        assertThat(TERABYTES.toGigabytes(1)).isEqualTo(1024);
    }

    @Test
    public void oneTerabyteInTerabytes() throws Exception {
        assertThat(TERABYTES.convert(1, TERABYTES)).isEqualTo(1);
        assertThat(TERABYTES.toTerabytes(1)).isEqualTo(1);
    }
}

