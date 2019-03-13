package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import org.junit.Test;


public class Issue521 extends v4Test {
    @Test
    public void whiteSpaceControlShouldWorkOnElse() throws IOException {
        shouldCompileTo("\n{{~#if bold~}}\n<b>\n{{else}}\n<i>\n{{~/if~}}\n", v4Test.$("hash", v4Test.$("bold", true)), "<b>\n");
        shouldCompileTo("\n{{~#if bold~}}\n<b>\n{{else}}\n<i>\n{{~/if~}}\n", v4Test.$("hash", v4Test.$("bold", false)), "\n<i>");
        shouldCompileTo("\n{{~#if bold~}}\n<b>\n{{~else~}}\n<i>\n{{~/if~}}\n", v4Test.$("hash", v4Test.$("bold", true)), "<b>");
        shouldCompileTo("\n{{~#if bold~}}\n<b>\n{{~else~}}\n<i>\n{{~/if~}}\n", v4Test.$("hash", v4Test.$("bold", false)), "<i>");
        shouldCompileTo("\n{{~#if bold~}}\n<b>\n{{~^~}}\n<i>\n{{~/if~}}\n", v4Test.$("hash", v4Test.$("bold", true)), "<b>");
        shouldCompileTo("\n{{~#if bold~}}\n<b>\n{{~^~}}\n<i>\n{{~/if~}}\n", v4Test.$("hash", v4Test.$("bold", false)), "<i>");
    }
}

