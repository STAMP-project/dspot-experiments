/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.javaee7.jca.filewatch;


import java.io.File;
import org.javaee7.jca.filewatch.event.FileEvent;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Robert Panzer (robert.panzer@me.com)
 * @author Bartosz Majsak (bartosz.majsak@gmail.com)
 */
@RunWith(Arquillian.class)
public class FileWatcherTest {
    private static FileEvent observedFileEvent;

    @Test
    @InSequence(1)
    public void should_react_on_new_text_file_arriving_in_the_folder() throws Exception {
        // given
        File tempFile = new File("/tmp", "test.txt");
        tempFile.createNewFile();
        tempFile.deleteOnExit();
        System.out.println(("Test created text file: " + (tempFile.getName())));
        // when
        await().atMost(ONE_MINUTE).with().pollInterval(FIVE_HUNDRED_MILLISECONDS).until(fileEventObserved());
        System.out.println(("Test received CDI event " + (FileWatcherTest.observedFileEvent)));
        // then
        assertThat(tempFile.getName()).isEqualTo(FileWatcherTest.observedFileEvent.getFile().getName());
        assertThat(FileEvent.Type.CREATED).isEqualTo(FileWatcherTest.observedFileEvent.getType());
    }

    @Test
    @InSequence(2)
    public void should_react_on_new_pdf_file_arriving_in_the_folder() throws Exception {
        // given
        File tempFile = new File("/tmp", (("pdf-test-creation" + (System.currentTimeMillis())) + ".pdf"));
        tempFile.createNewFile();
        tempFile.deleteOnExit();
        System.out.println(("Test created PDF file: " + (tempFile.getName())));
        // when
        await().atMost(ONE_MINUTE).with().pollInterval(FIVE_HUNDRED_MILLISECONDS).until(fileEventObserved());
        System.out.println(("Test received CDI event " + (FileWatcherTest.observedFileEvent)));
        // then
        assertThat(tempFile.getName()).isEqualTo(FileWatcherTest.observedFileEvent.getFile().getName());
        assertThat(CREATED).isEqualTo(FileWatcherTest.observedFileEvent.getType());
    }

    @Test
    @InSequence(3)
    public void should_react_on_deletion_of_existing_text_file() throws Exception {
        // given
        File tempFile = new File("/tmp", "test.txt");
        tempFile.delete();
        System.out.println(("Test deleted text file: " + (tempFile.getName())));
        // when
        await().atMost(ONE_MINUTE).with().pollInterval(FIVE_HUNDRED_MILLISECONDS).until(fileEventObserved());
        System.out.println(("Test received CDI event " + (FileWatcherTest.observedFileEvent)));
        // then
        assertThat(tempFile.getName()).isEqualTo(FileWatcherTest.observedFileEvent.getFile().getName());
        assertThat(DELETED).isEqualTo(FileWatcherTest.observedFileEvent.getType());
    }
}

