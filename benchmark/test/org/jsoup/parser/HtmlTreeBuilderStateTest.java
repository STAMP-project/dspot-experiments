package org.jsoup.parser;


import java.util.Arrays;
import org.jsoup.parser.HtmlTreeBuilderState.Constants;
import org.junit.Assert;
import org.junit.Test;


public class HtmlTreeBuilderStateTest {
    @Test
    public void ensureArraysAreSorted() {
        String[][] arrays = new String[][]{ Constants.InBodyStartToHead, Constants.InBodyStartPClosers, Constants.Headings, Constants.InBodyStartPreListing, Constants.InBodyStartLiBreakers, Constants.DdDt, Constants.Formatters, Constants.InBodyStartApplets, Constants.InBodyStartEmptyFormatters, Constants.InBodyStartMedia, Constants.InBodyStartInputAttribs, Constants.InBodyStartOptions, Constants.InBodyStartRuby, Constants.InBodyStartDrop, Constants.InBodyEndClosers, Constants.InBodyEndAdoptionFormatters, Constants.InBodyEndTableFosters, Constants.InCellNames, Constants.InCellBody, Constants.InCellTable, Constants.InCellCol };
        for (String[] array : arrays) {
            String[] copy = Arrays.copyOf(array, array.length);
            Arrays.sort(array);
            Assert.assertArrayEquals(array, copy);
        }
    }
}

