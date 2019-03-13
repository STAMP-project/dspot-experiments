/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;


import ApexLanguageModule.TERSE_NAME;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


public class ApexCpdTest {
    private File testdir;

    @Test
    public void testIssue427() throws IOException {
        CPDConfiguration configuration = new CPDConfiguration();
        configuration.setMinimumTileSize(10);
        configuration.setLanguage(LanguageFactory.createLanguage(TERSE_NAME));
        CPD cpd = new CPD(configuration);
        cpd.add(new File(testdir, "SFDCEncoder.cls"));
        cpd.add(new File(testdir, "SFDCEncoderConstants.cls"));
        cpd.go();
        Iterator<Match> matches = cpd.getMatches();
        int duplications = 0;
        while (matches.hasNext()) {
            matches.next();
            duplications++;
        } 
        Assert.assertEquals(1, duplications);
        Match firstDuplication = cpd.getMatches().next();
        Assert.assertTrue(firstDuplication.getSourceCodeSlice().startsWith("global with sharing class SFDCEncoder"));
    }
}

