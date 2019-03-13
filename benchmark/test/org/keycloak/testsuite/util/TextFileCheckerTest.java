/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.util;


import java.io.File;
import java.io.FileWriter;
import org.junit.Test;


/**
 *
 *
 * @author hmlnarik
 */
public class TextFileCheckerTest {
    private TextFileChecker tfc;

    private File tempFile;

    @Test
    public void testFileChecker() throws Exception {
        try (FileWriter fw = new FileWriter(tempFile)) {
            assertCheckedOutputIs();
            fw.write("Hello, Dolly\n");
            fw.flush();
            assertCheckedOutputIs("Hello, Dolly");
            fw.write("Well, hello, Dolly\n");
            fw.flush();
            assertCheckedOutputIs("Hello, Dolly", "Well, hello, Dolly");
            fw.write("It\'s so nice to have you back where you belong\n");
            fw.write("You\'re lookin\' swell, Dolly\n");
            fw.flush();
            assertCheckedOutputIs("Hello, Dolly", "Well, hello, Dolly", "It's so nice to have you back where you belong", "You're lookin' swell, Dolly");
            tfc.updateLastCheckedPositionsOfAllFilesToEndOfFile();
            fw.write("I can tell, Dolly\n");
            fw.write("You\'re still glowin\', you\'re still crowin\'\n");
            fw.flush();
            assertCheckedOutputIs("I can tell, Dolly", "You're still glowin', you're still crowin'");
            tfc.updateLastCheckedPositionsOfAllFilesToEndOfFile();
            assertCheckedOutputIs();
        }
    }
}

