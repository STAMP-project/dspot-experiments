package com.shekhargulati.random;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


public class RandomProblem002Test {
    @Test
    public void shouldGiveTrueWhenPatternExistInText() throws Exception {
        String text = Files.lines(Paths.get("src", "test", "resources", "book.txt")).collect(Collectors.joining());
        boolean patternExists = RandomProblem002.patternExistInText(text, "prudent");
        Assert.assertTrue(patternExists);
    }

    @Test
    public void shouldGiveFalseWhenPatternDoesNotExistInText() throws Exception {
        String text = Files.lines(Paths.get("src", "test", "resources", "book.txt")).collect(Collectors.joining());
        boolean patternExists = RandomProblem002.patternExistInText(text, "awesome");
        Assert.assertFalse(patternExists);
    }
}

