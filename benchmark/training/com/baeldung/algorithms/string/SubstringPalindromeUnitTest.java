package com.baeldung.algorithms.string;


import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class SubstringPalindromeUnitTest {
    private static final String INPUT_BUBBLE = "bubble";

    private static final String INPUT_CIVIC = "civic";

    private static final String INPUT_INDEED = "indeed";

    private static final String INPUT_ABABAC = "ababac";

    Set<String> EXPECTED_PALINDROME_BUBBLE = new HashSet<String>() {
        {
            add("b");
            add("u");
            add("l");
            add("e");
            add("bb");
            add("bub");
        }
    };

    Set<String> EXPECTED_PALINDROME_CIVIC = new HashSet<String>() {
        {
            add("civic");
            add("ivi");
            add("i");
            add("c");
            add("v");
        }
    };

    Set<String> EXPECTED_PALINDROME_INDEED = new HashSet<String>() {
        {
            add("i");
            add("n");
            add("d");
            add("e");
            add("ee");
            add("deed");
        }
    };

    Set<String> EXPECTED_PALINDROME_ABABAC = new HashSet<String>() {
        {
            add("a");
            add("b");
            add("c");
            add("aba");
            add("bab");
            add("ababa");
        }
    };

    private SubstringPalindrome palindrome = new SubstringPalindrome();

    @Test
    public void whenUsingManachersAlgorithm_thenFindsAllPalindromes() {
        Assert.assertEquals(EXPECTED_PALINDROME_BUBBLE, palindrome.findAllPalindromesUsingManachersAlgorithm(SubstringPalindromeUnitTest.INPUT_BUBBLE));
        Assert.assertEquals(EXPECTED_PALINDROME_INDEED, palindrome.findAllPalindromesUsingManachersAlgorithm(SubstringPalindromeUnitTest.INPUT_INDEED));
        Assert.assertEquals(EXPECTED_PALINDROME_CIVIC, palindrome.findAllPalindromesUsingManachersAlgorithm(SubstringPalindromeUnitTest.INPUT_CIVIC));
        Assert.assertEquals(EXPECTED_PALINDROME_ABABAC, palindrome.findAllPalindromesUsingManachersAlgorithm(SubstringPalindromeUnitTest.INPUT_ABABAC));
    }

    @Test
    public void whenUsingCenterApproach_thenFindsAllPalindromes() {
        Assert.assertEquals(EXPECTED_PALINDROME_BUBBLE, palindrome.findAllPalindromesUsingCenter(SubstringPalindromeUnitTest.INPUT_BUBBLE));
        Assert.assertEquals(EXPECTED_PALINDROME_INDEED, palindrome.findAllPalindromesUsingCenter(SubstringPalindromeUnitTest.INPUT_INDEED));
        Assert.assertEquals(EXPECTED_PALINDROME_CIVIC, palindrome.findAllPalindromesUsingCenter(SubstringPalindromeUnitTest.INPUT_CIVIC));
        Assert.assertEquals(EXPECTED_PALINDROME_ABABAC, palindrome.findAllPalindromesUsingCenter(SubstringPalindromeUnitTest.INPUT_ABABAC));
    }

    @Test
    public void whenUsingBruteForceApproach_thenFindsAllPalindromes() {
        Assert.assertEquals(EXPECTED_PALINDROME_BUBBLE, palindrome.findAllPalindromesUsingBruteForceApproach(SubstringPalindromeUnitTest.INPUT_BUBBLE));
        Assert.assertEquals(EXPECTED_PALINDROME_INDEED, palindrome.findAllPalindromesUsingBruteForceApproach(SubstringPalindromeUnitTest.INPUT_INDEED));
        Assert.assertEquals(EXPECTED_PALINDROME_CIVIC, palindrome.findAllPalindromesUsingBruteForceApproach(SubstringPalindromeUnitTest.INPUT_CIVIC));
        Assert.assertEquals(EXPECTED_PALINDROME_ABABAC, palindrome.findAllPalindromesUsingBruteForceApproach(SubstringPalindromeUnitTest.INPUT_ABABAC));
    }
}

