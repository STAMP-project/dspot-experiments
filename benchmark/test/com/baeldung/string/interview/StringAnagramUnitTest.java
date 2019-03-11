package com.baeldung.string.interview;


import org.junit.Test;


public class StringAnagramUnitTest {
    @Test
    public void whenTestAnagrams_thenTestingCorrectly() {
        assertThat(isAnagram("car", "arc")).isTrue();
        assertThat(isAnagram("west", "stew")).isTrue();
        assertThat(isAnagram("west", "east")).isFalse();
    }
}

