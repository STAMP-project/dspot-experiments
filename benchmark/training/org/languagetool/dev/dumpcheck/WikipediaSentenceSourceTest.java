/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2013 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.dev.dumpcheck;


import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLStreamException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.language.English;


public class WikipediaSentenceSourceTest {
    @Test
    public void testWikipediaSource() throws IOException, XMLStreamException {
        InputStream stream = WikipediaSentenceSourceTest.class.getResourceAsStream("/org/languagetool/dev/wikipedia/wikipedia-en.xml");
        WikipediaSentenceSource source = new WikipediaSentenceSource(stream, new English());
        Assert.assertTrue(source.hasNext());
        MatcherAssert.assertThat(source.next().getText(), CoreMatchers.is("This is the first document."));
        MatcherAssert.assertThat(source.next().getText(), CoreMatchers.is("It has three sentences."));
        MatcherAssert.assertThat(source.next().getText(), CoreMatchers.is("Here's the last sentence."));
        MatcherAssert.assertThat(source.next().getText(), CoreMatchers.is("This is the second document."));
        MatcherAssert.assertThat(source.next().getText(), CoreMatchers.is("It has two sentences."));
        Assert.assertFalse(source.hasNext());
    }
}

