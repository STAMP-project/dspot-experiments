package org.jsoup.parser;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

import static HtmlTreeBuilder.TagSearchButton;
import static HtmlTreeBuilder.TagSearchEndTags;
import static HtmlTreeBuilder.TagSearchList;
import static HtmlTreeBuilder.TagSearchSelectScope;
import static HtmlTreeBuilder.TagSearchSpecial;
import static HtmlTreeBuilder.TagSearchTableScope;
import static HtmlTreeBuilder.TagsSearchInScope;


public class HtmlTreeBuilderTest {
    @Test
    public void ensureSearchArraysAreSorted() {
        String[][] arrays = new String[][]{ TagsSearchInScope, TagSearchList, TagSearchButton, TagSearchTableScope, TagSearchSelectScope, TagSearchEndTags, TagSearchSpecial };
        for (String[] array : arrays) {
            String[] copy = Arrays.copyOf(array, array.length);
            Arrays.sort(array);
            Assert.assertArrayEquals(array, copy);
        }
    }
}

