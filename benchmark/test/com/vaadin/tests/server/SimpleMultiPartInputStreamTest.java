package com.vaadin.tests.server;


import org.junit.Test;


public class SimpleMultiPartInputStreamTest {
    @Test
    public void testSingleByteBoundaryAtEnd() throws Exception {
        checkBoundaryDetection(("xyz123" + (SimpleMultiPartInputStreamTest.getFullBoundary("a"))), "a", "xyz123");
    }

    @Test
    public void testSingleByteBoundaryInMiddle() throws Exception {
        checkBoundaryDetection((("xyz" + (SimpleMultiPartInputStreamTest.getFullBoundary("a"))) + "123"), "a", "xyz");
    }

    @Test
    public void testCorrectBoundaryAtEnd() throws Exception {
        checkBoundaryDetection(("xyz123" + (SimpleMultiPartInputStreamTest.getFullBoundary("abc"))), "abc", "xyz123");
    }

    @Test
    public void testCorrectBoundaryNearEnd() throws Exception {
        checkBoundaryDetection((("xyz123" + (SimpleMultiPartInputStreamTest.getFullBoundary("abc"))) + "de"), "abc", "xyz123");
    }

    @Test
    public void testCorrectBoundaryAtBeginning() throws Exception {
        checkBoundaryDetection(((SimpleMultiPartInputStreamTest.getFullBoundary("abc")) + "xyz123"), "abc", "");
    }

    @Test
    public void testRepeatingCharacterBoundary() throws Exception {
        checkBoundaryDetection(((SimpleMultiPartInputStreamTest.getFullBoundary("aa")) + "xyz123"), "aa", "");
        checkBoundaryDetection((("axyz" + (SimpleMultiPartInputStreamTest.getFullBoundary("aa"))) + "123"), "aa", "axyz");
        checkBoundaryDetection(("xyz123" + (SimpleMultiPartInputStreamTest.getFullBoundary("aa"))), "aa", "xyz123");
    }

    /**
     * Note, the boundary in this test is invalid. Boundary strings don't
     * contain CR/LF.
     */
    // public void testRepeatingNewlineBoundary() throws Exception {
    // checkBoundaryDetection("1234567890" + getFullBoundary("\n\n")
    // + "1234567890", "\n\n", "");
    // }
    @Test
    public void testRepeatingStringBoundary() throws Exception {
        checkBoundaryDetection(((SimpleMultiPartInputStreamTest.getFullBoundary("abab")) + "xyz123"), "abab", "");
        checkBoundaryDetection((("abaxyz" + (SimpleMultiPartInputStreamTest.getFullBoundary("abab"))) + "123"), "abab", "abaxyz");
        checkBoundaryDetection(("xyz123" + (SimpleMultiPartInputStreamTest.getFullBoundary("abab"))), "abab", "xyz123");
    }

    @Test
    public void testOverlappingBoundary() throws Exception {
        checkBoundaryDetection((("abc" + (SimpleMultiPartInputStreamTest.getFullBoundary("abcabd"))) + "xyz123"), "abcabd", "abc");
        checkBoundaryDetection((("xyzabc" + (SimpleMultiPartInputStreamTest.getFullBoundary("abcabd"))) + "123"), "abcabd", "xyzabc");
        checkBoundaryDetection(("xyz123abc" + (SimpleMultiPartInputStreamTest.getFullBoundary("abcabd"))), "abcabd", "xyz123abc");
    }
}

