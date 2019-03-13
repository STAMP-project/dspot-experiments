package com.baeldung.junit5;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;


public class ConditionalExecutionUnitTest {
    @Test
    @EnabledIfSystemProperty(named = "os.arch", matches = ".*64.*")
    public void whenRunningTestsOn64BitArchitectures_thenTestIsDisabled() {
        Integer value = 5;// result of an algorithm

        Assertions.assertNotEquals(0, value, "The result cannot be 0");
    }

    @Test
    @DisabledIfSystemProperty(named = "ci-server", matches = "true")
    public void whenRunningTestsOnCIServer_thenTestIsDisabled() {
        Integer value = 5;// result of an algorithm

        Assertions.assertNotEquals(0, value, "The result cannot be 0");
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "ENV", matches = "staging-server")
    public void whenRunningTestsStagingServer_thenTestIsEnabled() {
        char[] expected = new char[]{ 'J', 'u', 'p', 'i', 't', 'e', 'r' };
        char[] actual = "Jupiter".toCharArray();
        Assertions.assertArrayEquals(expected, actual, "Arrays should be equal");
    }

    @Test
    @DisabledIfEnvironmentVariable(named = "ENV", matches = ".*development.*")
    public void whenRunningTestsDevelopmentEnvironment_thenTestIsDisabled() {
        char[] expected = new char[]{ 'J', 'u', 'p', 'i', 't', 'e', 'r' };
        char[] actual = "Jupiter".toCharArray();
        Assertions.assertArrayEquals(expected, actual, "Arrays should be equal");
    }
}

