package com.baeldung.stream;


import com.codepoetics.protonpack.Indexed;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class StreamIndicesUnitTest {
    @Test
    public void whenCalled_thenReturnListOfEvenIndexedStrings() {
        String[] names = new String[]{ "Afrim", "Bashkim", "Besim", "Lulzim", "Durim", "Shpetim" };
        List<String> expectedResult = Arrays.asList("Afrim", "Besim", "Durim");
        List<String> actualResult = StreamIndices.getEvenIndexedStrings(names);
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void whenCalled_thenReturnListOfEvenIndexedStringsVersionTwo() {
        String[] names = new String[]{ "Afrim", "Bashkim", "Besim", "Lulzim", "Durim", "Shpetim" };
        List<String> expectedResult = Arrays.asList("Afrim", "Besim", "Durim");
        List<String> actualResult = StreamIndices.getEvenIndexedStrings(names);
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void whenCalled_thenReturnListOfOddStrings() {
        String[] names = new String[]{ "Afrim", "Bashkim", "Besim", "Lulzim", "Durim", "Shpetim" };
        List<String> expectedResult = Arrays.asList("Bashkim", "Lulzim", "Shpetim");
        List<String> actualResult = StreamIndices.getOddIndexedStrings(names);
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void givenList_whenCalled_thenReturnListOfEvenIndexedStrings() {
        List<String> names = Arrays.asList("Afrim", "Bashkim", "Besim", "Lulzim", "Durim", "Shpetim");
        List<Indexed<String>> expectedResult = Arrays.asList(Indexed.index(0, "Afrim"), Indexed.index(2, "Besim"), Indexed.index(4, "Durim"));
        List<Indexed<String>> actualResult = StreamIndices.getEvenIndexedStrings(names);
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void givenList_whenCalled_thenReturnListOfOddIndexedStrings() {
        List<String> names = Arrays.asList("Afrim", "Bashkim", "Besim", "Lulzim", "Durim", "Shpetim");
        List<Indexed<String>> expectedResult = Arrays.asList(Indexed.index(1, "Bashkim"), Indexed.index(3, "Lulzim"), Indexed.index(5, "Shpetim"));
        List<Indexed<String>> actualResult = StreamIndices.getOddIndexedStrings(names);
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void whenCalled_thenReturnListOfOddStringsVersionTwo() {
        String[] names = new String[]{ "Afrim", "Bashkim", "Besim", "Lulzim", "Durim", "Shpetim" };
        List<String> expectedResult = Arrays.asList("Bashkim", "Lulzim", "Shpetim");
        List<String> actualResult = StreamIndices.getOddIndexedStringsVersionTwo(names);
        Assert.assertEquals(expectedResult, actualResult);
    }
}

