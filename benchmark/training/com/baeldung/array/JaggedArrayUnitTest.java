package com.baeldung.array;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import org.junit.Assert;
import org.junit.Test;


public class JaggedArrayUnitTest {
    private JaggedArray obj = new JaggedArray();

    @Test
    public void whenInitializedUsingShortHandForm_thenCorrect() {
        Assert.assertArrayEquals(new int[][]{ new int[]{ 1, 2 }, new int[]{ 3, 4, 5 }, new int[]{ 6, 7, 8, 9 } }, obj.shortHandFormInitialization());
    }

    @Test
    public void whenInitializedWithDeclarationAndThenInitalization_thenCorrect() {
        Assert.assertArrayEquals(new int[][]{ new int[]{ 1, 2 }, new int[]{ 3, 4, 5 }, new int[]{ 6, 7, 8, 9 } }, obj.declarationAndThenInitialization());
    }

    @Test
    public void whenInitializedWithDeclarationAndThenInitalizationUsingUserInputs_thenCorrect() {
        InputStream is = new ByteArrayInputStream("1 2 3 4 5 6 7 8 9".getBytes());
        System.setIn(is);
        Assert.assertArrayEquals(new int[][]{ new int[]{ 1, 2 }, new int[]{ 3, 4, 5 }, new int[]{ 6, 7, 8, 9 } }, obj.declarationAndThenInitializationUsingUserInputs());
        System.setIn(System.in);
    }

    @Test
    public void givenJaggedArrayAndAnIndex_thenReturnArrayAtGivenIndex() {
        int[][] jaggedArr = new int[][]{ new int[]{ 1, 2 }, new int[]{ 3, 4, 5 }, new int[]{ 6, 7, 8, 9 } };
        Assert.assertArrayEquals(new int[]{ 1, 2 }, obj.getElementAtGivenIndex(jaggedArr, 0));
        Assert.assertArrayEquals(new int[]{ 3, 4, 5 }, obj.getElementAtGivenIndex(jaggedArr, 1));
        Assert.assertArrayEquals(new int[]{ 6, 7, 8, 9 }, obj.getElementAtGivenIndex(jaggedArr, 2));
    }

    @Test
    public void givenJaggedArray_whenUsingArraysAPI_thenVerifyPrintedElements() {
        int[][] jaggedArr = new int[][]{ new int[]{ 1, 2 }, new int[]{ 3, 4, 5 }, new int[]{ 6, 7, 8, 9 } };
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        obj.printElements(jaggedArr);
        Assert.assertEquals("[1, 2][3, 4, 5][6, 7, 8, 9]", outContent.toString().replace("\r", "").replace("\n", ""));
        System.setOut(System.out);
    }
}

