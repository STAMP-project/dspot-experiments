package org.jabref.logic.util.strings;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class StringLengthComparatorTest {
    private StringLengthComparator slc;

    @Test
    public void test() {
        Assertions.assertEquals((-1), slc.compare("AAA", "AA"));
        Assertions.assertEquals(0, slc.compare("AA", "AA"));
        Assertions.assertEquals(1, slc.compare("AA", "AAA"));
    }
}

