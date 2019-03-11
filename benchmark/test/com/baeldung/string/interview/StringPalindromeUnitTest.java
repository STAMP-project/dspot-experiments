package com.baeldung.string.interview;


import org.junit.Test;


public class StringPalindromeUnitTest {
    @Test
    public void givenIsPalindromeMethod_whenCheckingString_thenFindIfPalindrome() {
        assertThat(isPalindrome("madam")).isTrue();
        assertThat(isPalindrome("radar")).isTrue();
        assertThat(isPalindrome("level")).isTrue();
        assertThat(isPalindrome("baeldung")).isFalse();
    }
}

