/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.classic.corpusTest;


import ch.qos.logback.classic.corpus.TextFileUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TextFileUtilTest {
    @Test
    public void smoke() throws IOException {
        String s = "When on board H.M.S. \'Beagle,\' as naturalist, I was much struck with\r\n" + (("certain facts in the distribution of the inhabitants of South America,\r\n" + "and in the geological relations of the present to the past inhabitants\r\n") + "of that continent.");
        StringReader sr = new StringReader(s);
        BufferedReader br = new BufferedReader(sr);
        List<String> wordList = TextFileUtil.toWords(br);
        Assert.assertEquals(38, wordList.size());
        Assert.assertEquals("When", wordList.get(0));
        Assert.assertEquals("'Beagle,'", wordList.get(4));
        Assert.assertEquals("of", wordList.get(17));
    }
}

