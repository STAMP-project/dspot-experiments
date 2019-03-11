/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.portal.search.suggest;


import StringPool.BLANK;
import StringPool.DOUBLE_SPACE;
import StringPool.NULL;
import StringPool.SPACE;
import StringPool.THREE_SPACES;
import com.liferay.portal.kernel.util.StringUtil;
import java.util.Arrays;
import org.junit.Test;


/**
 *
 *
 * @author Bryan Engler
 * @author Andr? de Oliveira
 */
public class DictionaryReaderTest {
    @Test
    public void testBlanks() throws Exception {
        assertEntries(Arrays.asList("one=1.0", "two=2.0", "three=3.0"), createDictionaryReader(BLANK, "one 1", BLANK, "two 2", BLANK, BLANK, "three 3", BLANK, BLANK, BLANK));
    }

    @Test
    public void testDuplicates() throws Exception {
        assertEntries(Arrays.asList("one", "two", "one", "two"), createDictionaryReader("one", "two", "one", "two"));
    }

    @Test
    public void testNull() throws Exception {
        assertEntries(Arrays.asList(NULL), createDictionaryReader(NULL));
    }

    @Test
    public void testNullUppercase() throws Exception {
        assertEntries(Arrays.asList(StringUtil.toUpperCase(NULL)), createDictionaryReader(StringUtil.toUpperCase(NULL)));
    }

    @Test
    public void testNullWithWeight() throws Exception {
        assertEntries(Arrays.asList("null=0.5"), createDictionaryReader("null 0.5"));
    }

    @Test
    public void testNumbers() throws Exception {
        assertEntries(Arrays.asList("1=1.0", "2", "3", "4=4.0"), createDictionaryReader("1 1", "2", " 3", " 4 4"));
    }

    @Test
    public void testPoint() throws Exception {
        assertEntries(Arrays.asList("after=5.0", "before=0.5"), createDictionaryReader("after 5.", "before .5"));
    }

    @Test
    public void testSpaces() throws Exception {
        assertEntries(Arrays.asList("leading=1.0", "trailing", "wide=5.0"), createDictionaryReader(" leading 1", "trailing ", "  wide   5"));
    }

    @Test
    public void testSpacesInBetween() throws Exception {
        assertEntries(Arrays.asList("too"), createDictionaryReader("too many spaces 3.14"));
    }

    @Test
    public void testSpacesOnly() throws Exception {
        assertEntries(Arrays.asList(), createDictionaryReader(SPACE, DOUBLE_SPACE, THREE_SPACES));
    }

    @Test
    public void testUppercase() throws Exception {
        assertEntries(Arrays.asList("SDK"), createDictionaryReader("SDK"));
    }

    @Test
    public void testWeightMissing() throws Exception {
        assertEntries(Arrays.asList("weightless"), createDictionaryReader("weightless"));
    }
}

