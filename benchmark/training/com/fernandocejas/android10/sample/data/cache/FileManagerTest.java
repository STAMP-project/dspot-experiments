/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fernandocejas.android10.sample.data.cache;


import com.fernandocejas.android10.sample.data.ApplicationTestCase;
import java.io.File;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class FileManagerTest extends ApplicationTestCase {
    private FileManager fileManager;

    @Test
    public void testWriteToFile() {
        File fileToWrite = createDummyFile();
        String fileContent = "content";
        fileManager.writeToFile(fileToWrite, fileContent);
        MatcherAssert.assertThat(fileToWrite.exists(), CoreMatchers.is(true));
    }

    @Test
    public void testFileContent() {
        File fileToWrite = createDummyFile();
        String fileContent = "content\n";
        fileManager.writeToFile(fileToWrite, fileContent);
        String expectedContent = fileManager.readFileContent(fileToWrite);
        MatcherAssert.assertThat(expectedContent, CoreMatchers.is(CoreMatchers.equalTo(fileContent)));
    }
}

