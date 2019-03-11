/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.beans;


import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.geode.security.GemFireSecurityException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static FileUploader.STAGED_DIR_PREFIX;


public class FileUploaderTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private FileUploader fileUploader;

    private List<String> files;

    // this is to make sure that the naming convention of the fileuploader is MBean compliant
    @Test
    public void fileUploaderAndInterfaceInTheSamePackage() {
        String fileUploaderClassName = FileUploader.class.getName();
        String parentName = FileUploader.class.getInterfaces()[0].getName();
        assertThat((fileUploaderClassName + "MBean")).isEqualTo(parentName);
    }

    @Test
    public void deleteFileNotInTheUploadedDir() throws IOException {
        File file = temporaryFolder.newFile("a.jar");
        files.add(file.getAbsolutePath());
        assertThatThrownBy(() -> fileUploader.deleteFiles(files)).isInstanceOf(GemFireSecurityException.class);
    }

    @Test
    public void deleteFilesInUploadedDir() throws IOException {
        File dir = temporaryFolder.newFolder(((STAGED_DIR_PREFIX) + "test"));
        File file = new File(dir, "test.txt");
        FileUtils.writeStringToFile(file, "test", "UTF-8");
        assertThat(file).exists();
        files.add(file.getAbsolutePath());
        fileUploader.deleteFiles(files);
        // assertThat both parent dir and file are deleted
        assertThat(file).doesNotExist();
        assertThat(dir).doesNotExist();
    }
}

