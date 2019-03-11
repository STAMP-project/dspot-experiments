package com.baeldung.grep;


import java.io.File;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.unix4j.Unix4j;
import org.unix4j.line.Line;


public class GrepWithUnix4JIntegrationTest {
    private File fileToGrep;

    @Test
    public void whenGrepWithSimpleString_thenCorrect() {
        int expectedLineCount = 4;
        // grep "NINETEEN" dictionary.txt
        List<Line> lines = Unix4j.grep("NINETEEN", fileToGrep).toLineList();
        Assert.assertEquals(expectedLineCount, lines.size());
    }

    @Test
    public void whenInverseGrepWithSimpleString_thenCorrect() {
        int expectedLineCount = 178687;
        // grep -v "NINETEEN" dictionary.txt
        List<Line> lines = grep(Options.v, "NINETEEN", fileToGrep).toLineList();
        Assert.assertEquals(expectedLineCount, lines.size());
    }

    @Test
    public void whenGrepWithRegex_thenCorrect() {
        int expectedLineCount = 151;
        // grep -c ".*?NINE.*?" dictionary.txt
        String patternCount = grep(Options.c, ".*?NINE.*?", fileToGrep).cut(fields, ":", 1).toStringResult();
        Assert.assertEquals(expectedLineCount, Integer.parseInt(patternCount));
    }
}

