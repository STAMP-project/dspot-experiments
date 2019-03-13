/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2007 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.tagging;


import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;


public class ManualTaggerTest {
    private static final String MANUAL_DICT_FILENAME = "/de/added.txt";

    @Test
    public void testTag() throws IOException {
        ManualTagger tagger = new ManualTagger(JLanguageTool.getDataBroker().getFromResourceDirAsStream(ManualTaggerTest.MANUAL_DICT_FILENAME));
        Assert.assertThat(tagger.tag("").size(), CoreMatchers.is(0));
        Assert.assertThat(tagger.tag("gibtsnicht").size(), CoreMatchers.is(0));
        Assert.assertEquals("[Enigma/EIG:NOM:SIN:MAS, Enigma/EIG:GEN:SIN:MAS, Enigma/EIG:DAT:SIN:MAS, Enigma/EIG:AKK:SIN:MAS]", tagger.tag("Enigma").toString());
        // lookup is case sensitive:
        Assert.assertThat(tagger.tag("enigma").size(), CoreMatchers.is(0));
    }
}

