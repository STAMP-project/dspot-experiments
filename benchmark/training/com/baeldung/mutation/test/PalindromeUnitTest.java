package com.baeldung.mutation.test;


import com.baeldung.testing.mutation.Palindrome;
import org.junit.Assert;
import org.junit.Test;


public class PalindromeUnitTest {
    @Test
    public void whenEmptyString_thanAccept() {
        Palindrome palindromeTester = new Palindrome();
        Assert.assertTrue(palindromeTester.isPalindrome("noon"));
    }

    @Test
    public void whenPalindrom_thanAccept() {
        Palindrome palindromeTester = new Palindrome();
        Assert.assertTrue(palindromeTester.isPalindrome("noon"));
    }

    @Test
    public void whenNotPalindrom_thanReject() {
        Palindrome palindromeTester = new Palindrome();
        Assert.assertFalse(palindromeTester.isPalindrome("box"));
    }

    @Test
    public void whenNearPalindrom_thanReject() {
        Palindrome palindromeTester = new Palindrome();
        Assert.assertFalse(palindromeTester.isPalindrome("neon"));
    }
}

