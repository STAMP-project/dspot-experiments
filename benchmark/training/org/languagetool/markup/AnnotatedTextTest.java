/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2017 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.markup;


import AnnotatedText.MetaDataKey.DocumentTitle;
import AnnotatedText.MetaDataKey.EmailToAddress;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.tools.ContextTools;


public class AnnotatedTextTest {
    @Test
    public void test() {
        AnnotatedText text = // text:
        // hello <b>user!</b>
        new AnnotatedTextBuilder().addGlobalMetaData("foo", "val").addGlobalMetaData(EmailToAddress, "Foo Bar <foo@foobar.org>").addText("hello ").addMarkup("<b>").addText("user!").addMarkup("</b>").build();
        Assert.assertThat(text.getGlobalMetaData("foo", ""), Is.is("val"));
        Assert.assertThat(text.getGlobalMetaData("non-existing-key", "xxx"), Is.is("xxx"));
        Assert.assertThat(text.getGlobalMetaData(EmailToAddress, "xxx"), Is.is("Foo Bar <foo@foobar.org>"));
        Assert.assertThat(text.getGlobalMetaData(DocumentTitle, "default-title"), Is.is("default-title"));
        Assert.assertThat(text.getPlainText(), Is.is("hello user!"));
        Assert.assertThat(text.getOriginalTextPositionFor(0), Is.is(0));
        Assert.assertThat(text.getOriginalTextPositionFor(5), Is.is(5));
        Assert.assertThat(text.getOriginalTextPositionFor(6), Is.is(9));
        Assert.assertThat(text.getOriginalTextPositionFor(7), Is.is(10));
        // Example:
        // hello user!
        // ^ = position 8
        // hello <b>user!</b>
        // ^ = position 11
        Assert.assertThat(text.getOriginalTextPositionFor(8), Is.is(11));
    }

    @Test
    public void testIgnoreInterpretAs() {
        // https://github.com/languagetool-org/languagetool/issues/1393
        AnnotatedText text = new AnnotatedTextBuilder().addText("hello ").addMarkup("<p>", "\n\n").addText("more xxxx text!").build();
        Assert.assertThat(text.getPlainText(), Is.is("hello \n\nmore xxxx text!"));
        Assert.assertThat(text.getTextWithMarkup(), Is.is("hello <p>more xxxx text!"));
        ContextTools contextTools = new ContextTools();
        contextTools.setErrorMarkerStart("#");
        contextTools.setErrorMarkerEnd("#");
        contextTools.setEscapeHtml(false);
        Assert.assertThat(contextTools.getContext(14, 18, text.getTextWithMarkup()), Is.is("hello <p>more #xxxx# text!"));
    }
}

