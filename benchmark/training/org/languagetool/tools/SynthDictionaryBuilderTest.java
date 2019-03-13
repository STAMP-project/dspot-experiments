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
package org.languagetool.tools;


import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class SynthDictionaryBuilderTest extends DictionaryBuilderTestHelper {
    private static final String INFO = "fsa.dict.separator=+\n" + ("fsa.dict.encoding=cp1251\n" + "fsa.dict.encoder=SUFFIX");

    @Test
    public void testSynthBuilder() throws Exception {
        Path inputFile = Files.createTempFile("dictTest", ".txt");
        Path infoFile = Files.createTempFile("dictTest", "_synth.info");
        Path outFile = Files.createTempFile("dictTest", ".dict");
        try {
            Files.write(inputFile, Arrays.asList("word\tlemma\ttag"));
            Files.write(infoFile, Arrays.asList(SynthDictionaryBuilderTest.INFO));
            SynthDictionaryBuilder.main(new String[]{ "-i", inputFile.toAbsolutePath().toString(), "-info", infoFile.toAbsolutePath().toString(), "-o", outFile.toAbsolutePath().toString() });
            Assert.assertTrue(((outFile.toFile().length()) >= 40));
        } finally {
            inputFile.toFile().deleteOnExit();
            infoFile.toFile().deleteOnExit();
            outFile.toFile().deleteOnExit();
        }
    }
}

