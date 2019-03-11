package org.jabref.logic.layout;


import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * The test class LayoutEntryTest test the net.sf.jabref.export.layout.LayoutEntry. Indirectly the
 * net.sf.jabref.export.layout.Layout is tested too.
 * <p/>
 * The LayoutEntry creates a human readable String assigned with HTML formatters. To test the Highlighting Feature, an
 * instance of LayoutEntry will be instantiated via Layout and LayoutHelper. With these instance the doLayout() Method
 * is called several times for each test case. To simulate a search, a BibEntry will be created, which will be used by
 * LayoutEntry.
 *
 * There are five test cases: - The shown result text has no words which should be highlighted. - There is one word
 * which will be highlighted ignoring case sensitivity. - There are two words which will be highlighted ignoring case
 * sensitivity. - There is one word which will be highlighted case sensitivity. - There are more words which will be
 * highlighted case sensitivity.
 */
public class LayoutEntryTest {
    private BibEntry mBTE;

    @Test
    public void testParseMethodCalls() {
        Assertions.assertEquals(1, LayoutEntry.parseMethodsCalls("bla").size());
        Assertions.assertEquals("bla", LayoutEntry.parseMethodsCalls("bla").get(0).get(0));
        Assertions.assertEquals(1, LayoutEntry.parseMethodsCalls("bla,").size());
        Assertions.assertEquals("bla", LayoutEntry.parseMethodsCalls("bla,").get(0).get(0));
        Assertions.assertEquals(1, LayoutEntry.parseMethodsCalls("_bla.bla.blub,").size());
        Assertions.assertEquals("_bla.bla.blub", LayoutEntry.parseMethodsCalls("_bla.bla.blub,").get(0).get(0));
        Assertions.assertEquals(2, LayoutEntry.parseMethodsCalls("bla,foo").size());
        Assertions.assertEquals("bla", LayoutEntry.parseMethodsCalls("bla,foo").get(0).get(0));
        Assertions.assertEquals("foo", LayoutEntry.parseMethodsCalls("bla,foo").get(1).get(0));
        Assertions.assertEquals(2, LayoutEntry.parseMethodsCalls("bla(\"test\"),foo(\"fark\")").size());
        Assertions.assertEquals("bla", LayoutEntry.parseMethodsCalls("bla(\"test\"),foo(\"fark\")").get(0).get(0));
        Assertions.assertEquals("foo", LayoutEntry.parseMethodsCalls("bla(\"test\"),foo(\"fark\")").get(1).get(0));
        Assertions.assertEquals("test", LayoutEntry.parseMethodsCalls("bla(\"test\"),foo(\"fark\")").get(0).get(1));
        Assertions.assertEquals("fark", LayoutEntry.parseMethodsCalls("bla(\"test\"),foo(\"fark\")").get(1).get(1));
        Assertions.assertEquals(2, LayoutEntry.parseMethodsCalls("bla(test),foo(fark)").size());
        Assertions.assertEquals("bla", LayoutEntry.parseMethodsCalls("bla(test),foo(fark)").get(0).get(0));
        Assertions.assertEquals("foo", LayoutEntry.parseMethodsCalls("bla(test),foo(fark)").get(1).get(0));
        Assertions.assertEquals("test", LayoutEntry.parseMethodsCalls("bla(test),foo(fark)").get(0).get(1));
        Assertions.assertEquals("fark", LayoutEntry.parseMethodsCalls("bla(test),foo(fark)").get(1).get(1));
    }
}

