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
package org.languagetool.dev.wikipedia.atom;


import java.io.IOException;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.tools.Tools;


public class AtomFeedParserTest {
    @Test
    public void testParsing() throws IOException, XMLStreamException {
        AtomFeedParser atomFeedParser = new AtomFeedParser();
        List<AtomFeedItem> items = atomFeedParser.getAtomFeedItems(Tools.getStream("/org/languagetool/dev/wikipedia/atom/feed1.xml"));
        Assert.assertThat(items.size(), CoreMatchers.is(3));
        AtomFeedItem item1 = items.get(0);
        Assert.assertThat(item1.getId(), CoreMatchers.is("//de.wikipedia.org/w/index.php?title=Peter_Bichsel&diff=125079808&oldid=125079797"));
        Assert.assertThat(item1.getTitle(), CoreMatchers.is("Peter Bichsel"));
        Assert.assertThat(item1.getDiffId(), CoreMatchers.is(125079808L));
        Assert.assertThat(item1.getOldContent().toString(), CoreMatchers.is("[}}llllllllll]"));
        Assert.assertThat(item1.getNewContent().toString(), CoreMatchers.is("[}}]"));
        AtomFeedItem item2 = items.get(1);
        Assert.assertThat(item2.getId(), CoreMatchers.is("//de.wikipedia.org/wiki/Timo_b%C3%A4cker"));
        Assert.assertThat(item2.getTitle(), CoreMatchers.is("Timo b?cker"));
        Assert.assertThat(item2.getDiffId(), CoreMatchers.is(0L));
        Assert.assertThat(item2.getOldContent().toString(), CoreMatchers.is("[]"));
        Assert.assertThat(item2.getNewContent().toString(), CoreMatchers.is("[]"));
        AtomFeedItem item3 = items.get(2);
        Assert.assertThat(item3.getId(), CoreMatchers.is("//de.wikipedia.org/w/index.php?title=Vallourec_Deutschland_GmbH&diff=125079807&oldid=124992032"));
        Assert.assertThat(item3.getTitle(), CoreMatchers.is("Vallourec Deutschland GmbH"));
        Assert.assertThat(item3.getDiffId(), CoreMatchers.is(125079807L));
        Assert.assertThat(item3.getOldContent().toString(), CoreMatchers.is("[]"));
        Assert.assertThat(item3.getNewContent().toString(), CoreMatchers.is("[* [http://www.rp-online.de/nrw/staedte/] Fehler: der Haus, * [http://www.vmtubes.com], ]"));
    }
}

