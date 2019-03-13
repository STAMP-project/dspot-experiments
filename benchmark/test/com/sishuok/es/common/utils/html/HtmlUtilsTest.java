/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.common.utils.html;


import org.junit.Assert;
import org.junit.Test;


/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-5-27 ??2:12
 * <p>Version: 1.0
 */
public class HtmlUtilsTest {
    @Test
    public void testHtml2Text() {
        String html = "<a>??</a>&lt;a&gt;??&lt;/a&gt;";
        Assert.assertEquals("????", HtmlUtils.text(html));
    }

    @Test
    public void testHtml2TextWithMaxLength() {
        String html = "<a>??</a>&lt;a&gt;??&lt;/a&gt;";
        Assert.assertEquals("????", HtmlUtils.text(html, 2));
    }

    @Test
    public void testRemoveUnSafeTag() {
        String html = "<a onclick='alert(1)' onBlur='alert(1)'>??</a><script>alert(1)</script><Script>alert(1)</SCRIPT>";
        Assert.assertEquals("<a>??</a>", HtmlUtils.removeUnSafeTag(html));
    }

    @Test
    public void testRemoveTag() {
        String html = "<a onclick='alert(1)' onBlur='alert(1)'>??</a><A>1</a>";
        Assert.assertEquals("", HtmlUtils.removeTag(html, "a"));
    }
}

