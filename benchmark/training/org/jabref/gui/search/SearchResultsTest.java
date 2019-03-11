package org.jabref.gui.search;


import org.assertj.swing.core.ComponentFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


@Tag("GUITest")
public class SearchResultsTest extends AssertJSwingJUnitTestCase {
    private FrameFixture frameFixture;

    @Test
    public void testSearchFieldQuery() {
        frameFixture.menuItemWithPath("Search", "Search").click();
        JTextComponentFixture searchField = frameFixture.textBox();
        ComponentFinder finder = robot().finder();
        /* BasePanel panel = finder.findByType(BasePanel.class);
        Collection<BibEntry> entries = panel.getDatabase().getEntries();

        searchField.deleteText().enterText("");
        Assert.assertEquals(19, entries.size());

        searchField.deleteText().enterText("entrytype=article");
        Assert.assertFalse(entries.stream().noneMatch(entry -> entry.isSearchHit()));
        Assert.assertEquals(5, entries.stream().filter(entry -> entry.isSearchHit()).count());

        searchField.deleteText().enterText("entrytype=proceedings");
        Assert.assertFalse(entries.stream().noneMatch(entry -> entry.isSearchHit()));
        Assert.assertEquals(13, entries.stream().filter(entry -> entry.isSearchHit()).count());

        searchField.deleteText().enterText("entrytype=book");
        Assert.assertFalse(entries.stream().noneMatch(entry -> entry.isSearchHit()));
        Assert.assertEquals(1, entries.stream().filter(entry -> entry.isSearchHit()).count());
         */
    }

    @Test
    public void testSeachWithoutResults() {
        /* frameFixture.menuItemWithPath("Search", "Search").click();
        JTextComponentFixture searchField = frameFixture.textBox();
        ComponentFinder finder = robot().finder();
        BasePanel panel = finder.findByType(BasePanel.class);
        Collection<BibEntry> entries = panel.getDatabase().getEntries();

        searchField.deleteText().enterText("asdf");
        Assert.assertTrue(entries.stream().noneMatch(entry -> entry.isSearchHit()));
         */
    }

    @Test
    public void testSearchInvalidQuery() {
        /* frameFixture.menuItemWithPath("Search", "Search").click();
        JTextComponentFixture searchField = frameFixture.textBox();
        ComponentFinder finder = robot().finder();
        BasePanel panel = finder.findByType(BasePanel.class);
        Collection<BibEntry> entries = panel.getDatabase().getEntries();

        searchField.deleteText().enterText("asdf[");
        Assert.assertTrue(entries.stream().noneMatch(entry -> entry.isSearchHit()));
         */
    }
}

