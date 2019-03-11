/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;


import org.junit.Assert;
import org.junit.Test;


public class LanguageFactoryTest {
    @Test
    public void testSimple() {
        Assert.assertTrue(((LanguageFactory.createLanguage("Cpddummy")) instanceof CpddummyLanguage));
        Assert.assertTrue(((LanguageFactory.createLanguage("not_existing_language")) instanceof AnyLanguage));
    }
}

