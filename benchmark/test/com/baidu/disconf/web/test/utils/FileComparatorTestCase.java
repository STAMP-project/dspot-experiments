package com.baidu.disconf.web.test.utils;


import difflib.Chunk;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class FileComparatorTestCase {
    private final File original = new File("src/test/resources/files/originalFile.txt");

    private final File revised = new File("src/test/resources/files/revisedFile.txt");

    @Test
    public void shouldGetChangesBetweenFiles() {
        final FileComparator comparator = new FileComparator(original, revised);
        try {
            final List<Chunk> changesFromOriginal = comparator.getChangesFromOriginal();
            for (Chunk<String> chunk : changesFromOriginal) {
                System.out.println(chunk.toString());
            }
            Assert.assertEquals(3, changesFromOriginal.size());
            final Chunk firstChange = changesFromOriginal.get(0);
            final int firstLineOfFirstChange = (firstChange.getPosition()) + 1;
            final int firstChangeSize = firstChange.size();
            Assert.assertEquals(2, firstLineOfFirstChange);
            Assert.assertEquals(1, firstChangeSize);
            final String firstChangeText = firstChange.getLines().get(0).toString();
            Assert.assertEquals("Line 3 with changes", firstChangeText);
            final Chunk secondChange = changesFromOriginal.get(1);
            final int firstLineOfSecondChange = (secondChange.getPosition()) + 1;
            final int secondChangeSize = secondChange.size();
            Assert.assertEquals(4, firstLineOfSecondChange);
            Assert.assertEquals(2, secondChangeSize);
            final String secondChangeFirstLineText = secondChange.getLines().get(0).toString();
            final String secondChangeSecondLineText = secondChange.getLines().get(1).toString();
            Assert.assertEquals("Line 5 with changes and", secondChangeFirstLineText);
            Assert.assertEquals("a new line", secondChangeSecondLineText);
            final Chunk thirdChange = changesFromOriginal.get(2);
            final int firstLineOfThirdChange = (thirdChange.getPosition()) + 1;
            final int thirdChangeSize = thirdChange.size();
            Assert.assertEquals(11, firstLineOfThirdChange);
            Assert.assertEquals(1, thirdChangeSize);
            final String thirdChangeText = thirdChange.getLines().get(0).toString();
            Assert.assertEquals("Line 10 with changes", thirdChangeText);
        } catch (IOException ioe) {
            Assert.fail(("Error running test shouldGetChangesBetweenFiles " + (ioe.toString())));
        }
    }

    @Test
    public void shouldGetInsertsBetweenFiles() {
        final FileComparator comparator = new FileComparator(original, revised);
        try {
            final List<Chunk> insertsFromOriginal = comparator.getInsertsFromOriginal();
            Assert.assertEquals(1, insertsFromOriginal.size());
            final Chunk firstInsert = insertsFromOriginal.get(0);
            final int firstLineOfFirstInsert = (firstInsert.getPosition()) + 1;
            final int firstInsertSize = firstInsert.size();
            Assert.assertEquals(7, firstLineOfFirstInsert);
            Assert.assertEquals(1, firstInsertSize);
            final String firstInsertText = firstInsert.getLines().get(0).toString();
            Assert.assertEquals("new line 6.1", firstInsertText);
        } catch (IOException ioe) {
            Assert.fail(("Error running test shouldGetInsertsBetweenFiles " + (ioe.toString())));
        }
    }

    @Test
    public void shouldGetDeletesBetweenFiles() {
        final FileComparator comparator = new FileComparator(original, revised);
        try {
            final List<Chunk> deletesFromOriginal = comparator.getDeletesFromOriginal();
            Assert.assertEquals(1, deletesFromOriginal.size());
            final Chunk firstDelete = deletesFromOriginal.get(0);
            final int firstLineOfFirstDelete = (firstDelete.getPosition()) + 1;
            Assert.assertEquals(1, firstLineOfFirstDelete);
        } catch (IOException ioe) {
            Assert.fail(("Error running test shouldGetDeletesBetweenFiles " + (ioe.toString())));
        }
    }
}

