/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.shell;


import com.google.devtools.build.lib.util.OS;
import com.google.devtools.build.runfiles.Runfiles;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests {@link Command} execution under load.
 */
@RunWith(JUnit4.class)
public class LoadTest {
    private File tempFile;

    @Test
    public void testLoad() throws Throwable {
        Runfiles runfiles = Runfiles.create();
        String catBin = "io_bazel/src/test/java/com/google/devtools/build/lib/shell/cat_file";
        if ((OS.getCurrent()) == (OS.WINDOWS)) {
            catBin += ".exe";
        }
        catBin = runfiles.rlocation(catBin);
        final Command command = new Command(new String[]{ catBin, tempFile.getAbsolutePath() });
        Thread[] threads = new Thread[10];
        List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<Throwable>());
        for (int i = 0; i < (threads.length); i++) {
            threads[i] = new Thread(new LoadTest.LoadThread(command, exceptions));
        }
        for (int i = 0; i < (threads.length); i++) {
            threads[i].start();
        }
        for (int i = 0; i < (threads.length); i++) {
            threads[i].join();
        }
        if (!(exceptions.isEmpty())) {
            for (Throwable t : exceptions) {
                t.printStackTrace();
            }
            throw exceptions.get(0);
        }
    }

    private static final class LoadThread implements Runnable {
        private final Command command;

        private final List<Throwable> exception;

        private LoadThread(Command command, List<Throwable> exception) {
            this.command = command;
            this.exception = exception;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 20; i++) {
                    command.execute();
                }
            } catch (Throwable t) {
                exception.add(t);
            }
        }
    }
}

