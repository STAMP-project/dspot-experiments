/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2014 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.server;


import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Ignore;
import org.junit.Test;
import org.languagetool.Language;
import org.languagetool.Languages;
import org.languagetool.tools.StringTools;


/**
 * Test HTTP server access from multiple threads with multiple languages.
 * Sends random texts but doesn't really check the results.
 */
@Ignore("for interactive use; requires local Tatoeba data")
public class HTTPServerMultiLangLoadTest extends HTTPServerLoadTest {
    private static final String DATA_PATH = "/home/fabian/Downloads/tatoeba";

    static final int MIN_TEXT_LENGTH = 10;

    static final int MAX_TEXT_LENGTH = 5000;

    static final int MAX_SLEEP_MILLIS = 1;

    long totalTimes = 0;

    long totalChars = 0;

    final Map<Language, String> langCodeToText = new HashMap<>();

    final Random random = new Random(1234);

    final AtomicInteger counter = new AtomicInteger();

    @Test
    @Override
    public void testHTTPServer() throws Exception {
        File dir = new File(HTTPServerMultiLangLoadTest.DATA_PATH);
        List<Language> languages = new ArrayList<>();
        languages.addAll(Languages.get());
        for (Language language : languages) {
            File file = new File(dir, (("tatoeba-" + (language.getShortCode())) + ".txt"));
            if (!(file.exists())) {
                System.err.println((("No data found for " + language) + ", language will not be tested"));
            } else {
                String content = StringTools.readerToString(new FileReader(file));
                langCodeToText.put(language, content);
                System.err.println(((("Using " + (content.length())) + " bytes of data for ") + language));
            }
        }
        if ((langCodeToText.size()) == 0) {
            throw new RuntimeException(("No input data found in " + dir));
        }
        System.out.println((("Testing " + (langCodeToText.keySet().size())) + " languages and variants"));
        // super.testHTTPServer();  // start server in this JVM
        super.doTest();// assume server has been started manually in its own JVM

    }
}

