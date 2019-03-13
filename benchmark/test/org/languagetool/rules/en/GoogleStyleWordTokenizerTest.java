/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2016 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.rules.en;


import java.util.Arrays;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class GoogleStyleWordTokenizerTest {
    @Test
    public void testTokenize() {
        GoogleStyleWordTokenizer tokenizer = new GoogleStyleWordTokenizer();
        Assert.assertThat(tokenizer.tokenize("foo bar"), Is.is(Arrays.asList("foo", " ", "bar")));
        Assert.assertThat(tokenizer.tokenize("foo-bar"), Is.is(Arrays.asList("foo", "-", "bar")));
        Assert.assertThat(tokenizer.tokenize("I'm here."), Is.is(Arrays.asList("I", "'m", " ", "here", ".")));
        Assert.assertThat(tokenizer.tokenize("I'll do that"), Is.is(Arrays.asList("I", "'ll", " ", "do", " ", "that")));
        Assert.assertThat(tokenizer.tokenize("You're here"), Is.is(Arrays.asList("You", "'re", " ", "here")));
        Assert.assertThat(tokenizer.tokenize("You've done that"), Is.is(Arrays.asList("You", "'ve", " ", "done", " ", "that")));
    }
}

