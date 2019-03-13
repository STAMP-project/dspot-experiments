/**
 * Copyright (C) 2014 Pedro Vicente G?mez S?nchez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pedrovgs.problem53;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class CompressStringTest {
    private CompressString compressString;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullStringsAsInput() {
        compressString.compress(null);
    }

    @Test
    public void shouldNotCompressEmptyWords() {
        String wordToCompress = "";
        String result = compressString.compress(wordToCompress);
        Assert.assertEquals("", result);
    }

    @Test
    public void shouldNotCompressWordsWithJustOneChar() {
        String wordToCompress = "a";
        String result = compressString.compress(wordToCompress);
        Assert.assertEquals("a", result);
    }

    @Test
    public void shouldCompressWordsFullOfTheSameChars() {
        String wordToCompress = "aaa";
        String result = compressString.compress(wordToCompress);
        Assert.assertEquals("a3", result);
    }

    @Test
    public void shouldCompressWords() {
        String wordToCompress = "aabcccccaaa";
        String result = compressString.compress(wordToCompress);
        Assert.assertEquals("a2bc5a3", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullStringsAsInputAlternative() {
        compressString.compressAlternativeApproach(null);
    }

    @Test
    public void shouldNotCompressEmptyWordsAlternative() {
        String wordToCompress = "";
        String result = compressString.compressAlternativeApproach(wordToCompress);
        Assert.assertEquals("", result);
    }

    @Test
    public void shouldNotCompressWordsWithJustOneCharAlternative() {
        String wordToCompress = "a";
        String result = compressString.compressAlternativeApproach(wordToCompress);
        Assert.assertEquals("a", result);
    }

    @Test
    public void shouldCompressWordsFullOfTheSameCharsAlternative() {
        String wordToCompress = "aaa";
        String result = compressString.compressAlternativeApproach(wordToCompress);
        Assert.assertEquals("a3", result);
    }

    @Test
    public void shouldCompressWordsAlternative() {
        String wordToCompress = "aabcccccaaa";
        String result = compressString.compressAlternativeApproach(wordToCompress);
        Assert.assertEquals("a2bc5a3", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullStringsAsInputRecursive() {
        compressString.compressRecursive(null);
    }

    @Test
    public void shouldNotCompressEmptyWordsRecursive() {
        String wordToCompress = "";
        String result = compressString.compressRecursive(wordToCompress);
        Assert.assertEquals("", result);
    }

    @Test
    public void shouldNotCompressWordsWithJustOneCharRecursive() {
        String wordToCompress = "a";
        String result = compressString.compressRecursive(wordToCompress);
        Assert.assertEquals("a", result);
    }

    @Test
    public void shouldCompressWordsFullOfTheSameCharsRecursive() {
        String wordToCompress = "aaa";
        String result = compressString.compressRecursive(wordToCompress);
        Assert.assertEquals("a3", result);
    }

    @Test
    public void shouldCompressWordsRecursive() {
        String wordToCompress = "aabcccccaaa";
        String result = compressString.compressRecursive(wordToCompress);
        Assert.assertEquals("a2bc5a3", result);
    }
}

