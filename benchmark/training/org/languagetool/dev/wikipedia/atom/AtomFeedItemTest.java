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
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class AtomFeedItemTest {
    @Test
    public void testModifiedContent() throws IOException {
        AtomFeedItem item = getSummary("summary1.txt");
        Assert.assertThat(item.getOldContent().size(), CoreMatchers.is(1));
        Assert.assertThat(item.getOldContent().get(0), CoreMatchers.is("}}added"));
        Assert.assertThat(item.getNewContent().size(), CoreMatchers.is(1));
        Assert.assertThat(item.getNewContent().get(0), CoreMatchers.is("}}"));
    }

    @Test
    public void testAddedParagraphs() throws IOException {
        AtomFeedItem item = getSummary("summary2.txt");
        Assert.assertThat(item.getOldContent().size(), CoreMatchers.is(0));// some content was added, so there's no old version

        Assert.assertThat(item.getNewContent().size(), CoreMatchers.is(3));
        Assert.assertThat(item.getNewContent().get(0), CoreMatchers.is("* [http://www.rp-online.de/nrw/staedte/]"));
        Assert.assertThat(item.getNewContent().get(1), CoreMatchers.is("* [http://www.vmtubes.com]"));
        Assert.assertThat(item.getNewContent().get(2), CoreMatchers.is(""));
    }

    @Test
    public void testDeletedParagraphs() throws IOException {
        AtomFeedItem item = getSummary("summary3.txt");
        Assert.assertThat(item.getOldContent().size(), CoreMatchers.is(3));
        Assert.assertThat(item.getOldContent().get(0), CoreMatchers.is("* [http://www.rp-online.de/nrw/staedte/]"));
        Assert.assertThat(item.getOldContent().get(1), CoreMatchers.is("* [http://www.vmtubes.com]"));
        Assert.assertThat(item.getOldContent().get(2), CoreMatchers.is(""));
        Assert.assertThat(item.getNewContent().size(), CoreMatchers.is(0));// some content was deleted, so there's no new version

    }

    @Test
    public void testAddedTableLine() throws IOException {
        // The table changes we get may be incomplete tables, so Sweble cannot filter
        // them and we'd be left with Mediawiki syntax without filtering...
        AtomFeedItem item = getSummary("summary-table.txt");
        Assert.assertThat(item.getOldContent().size(), CoreMatchers.is(0));
        Assert.assertThat(item.getNewContent().size(), CoreMatchers.is(1));
        Assert.assertThat(item.getNewContent().get(0), CoreMatchers.is("Besetzung"));// was "!Besetzung" in XML

    }
}

