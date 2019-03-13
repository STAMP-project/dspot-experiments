package io.dropwizard.servlets.assets;


import org.junit.jupiter.api.Test;


public class ByteRangeTest {
    private static final int RESOURCE_LENGTH = 10000;

    @Test
    public void firstBytes() {
        final ByteRange actual = ByteRange.parse("0-499", ByteRangeTest.RESOURCE_LENGTH);
        assertThat(actual.getStart()).isEqualTo(0);
        assertThat(actual.getEnd()).isEqualTo(499);
    }

    @Test
    public void secondBytes() {
        final ByteRange actual = ByteRange.parse("500-999", ByteRangeTest.RESOURCE_LENGTH);
        assertThat(actual.getStart()).isEqualTo(500);
        assertThat(actual.getEnd()).isEqualTo(999);
    }

    @Test
    public void finalBytes() {
        final ByteRange actual = ByteRange.parse("-500", ByteRangeTest.RESOURCE_LENGTH);
        assertThat(actual.getStart()).isEqualTo(9500);
        assertThat(actual.getEnd()).isEqualTo(9999);
    }

    @Test
    public void noEndBytes() {
        final ByteRange actual = ByteRange.parse("9500-", ByteRangeTest.RESOURCE_LENGTH);
        assertThat(actual.getStart()).isEqualTo(9500);
        assertThat(actual.getEnd()).isEqualTo(9999);
    }

    @Test
    public void startBytes() {
        final ByteRange actual = ByteRange.parse("9500", ByteRangeTest.RESOURCE_LENGTH);
        assertThat(actual.getStart()).isEqualTo(9500);
        assertThat(actual.getEnd()).isEqualTo(9999);
    }

    @Test
    public void tooManyBytes() {
        final ByteRange actual = ByteRange.parse("9000-20000", ByteRangeTest.RESOURCE_LENGTH);
        assertThat(actual.getStart()).isEqualTo(9000);
        assertThat(actual.getEnd()).isEqualTo(9999);
    }
}

