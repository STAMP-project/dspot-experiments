/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util;


import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


public class StringUtilTest {
    @Test
    public void decamelizes_matcher() throws Exception {
        Assert.assertEquals("<Sentence with strong language>", StringUtil.decamelizeMatcher("SentenceWithStrongLanguage"));
        Assert.assertEquals("<W e i r d o 1>", StringUtil.decamelizeMatcher("WEIRDO1"));
        Assert.assertEquals("<_>", StringUtil.decamelizeMatcher("_"));
        Assert.assertEquals("<Has exactly 3 elements>", StringUtil.decamelizeMatcher("HasExactly3Elements"));
        Assert.assertEquals("<custom argument matcher>", StringUtil.decamelizeMatcher(""));
    }

    @Test
    public void joins_empty_list() throws Exception {
        assertThat(StringUtil.join()).isEmpty();
        assertThat(StringUtil.join("foo", Collections.emptyList())).isEmpty();
    }

    @Test
    public void joins_single_line() throws Exception {
        assertThat(StringUtil.join("line1")).hasLineCount(2);
    }

    @Test
    public void joins_two_lines() throws Exception {
        assertThat(StringUtil.join("line1", "line2")).hasLineCount(3);
    }

    @Test
    public void join_has_preceeding_linebreak() throws Exception {
        assertThat(StringUtil.join("line1")).isEqualTo("\nline1");
    }

    @Test
    public void removes_first_line() throws Exception {
        assertThat(StringUtil.removeFirstLine("line1\nline2")).isEqualTo("line2");
    }

    @Test
    public void joins_with_line_prefix() throws Exception {
        Assert.assertEquals(("Hey!\n" + (" - a\n" + " - b")), StringUtil.join("Hey!\n", " - ", Arrays.asList("a", "b")));
    }
}

