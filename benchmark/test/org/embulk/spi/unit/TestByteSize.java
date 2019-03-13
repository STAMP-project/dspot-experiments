package org.embulk.spi.unit;


import org.junit.Test;


public class TestByteSize {
    @Test
    public void testUnitPatterns() {
        TestByteSize.assertByteSize(42L, "42");
        TestByteSize.assertByteSize(42L, "42B");
        TestByteSize.assertByteSize((42L * (1L << 10)), "42KB");
        TestByteSize.assertByteSize((42L * (1L << 20)), "42MB");
        TestByteSize.assertByteSize((42L * (1L << 30)), "42GB");
        TestByteSize.assertByteSize((42L * (1L << 40)), "42TB");
        TestByteSize.assertByteSize((42L * (1L << 50)), "42PB");
        TestByteSize.assertByteSize(42L, "42 B");
        TestByteSize.assertByteSize((42L * (1L << 10)), "42 KB");
    }

    @Test
    public void testUnknownUnits() {
        TestByteSize.assertInvalidByteSize("42XB");
        TestByteSize.assertInvalidByteSize("42 XB");
    }

    @Test
    public void testInvalidPatterns() {
        TestByteSize.assertInvalidByteSize(" 42");
        TestByteSize.assertInvalidByteSize("42  B");
        TestByteSize.assertInvalidByteSize("42 B ");
        TestByteSize.assertInvalidByteSize("42B ");
        TestByteSize.assertInvalidByteSize("42  KB");
        TestByteSize.assertInvalidByteSize("42 KB ");
        TestByteSize.assertInvalidByteSize("42KB ");
    }

    @Test
    public void testInvalidValues() {
        TestByteSize.assertInvalidByteSize("9223372036854775KB");
    }

    @Test
    public void testToString() {
        TestByteSize.assertByteSizeString("42B", "42 B");
        TestByteSize.assertByteSizeString("42KB", "42 KB");
        TestByteSize.assertByteSizeString("42MB", "42 MB");
        TestByteSize.assertByteSizeString("42GB", "42 GB");
        TestByteSize.assertByteSizeString("42TB", "42 TB");
        TestByteSize.assertByteSizeString("42PB", "42 PB");
        TestByteSize.assertByteSizeString("42.20KB", "42.2 KB");
        TestByteSize.assertByteSizeString("42.33KB", "42.33KB");
    }
}

