/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.impl.persistence;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.function.Predicate;
import org.ehcache.impl.serialization.TransientStateHolder;
import org.ehcache.spi.persistence.StateHolder;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * FileBasedStateRepositoryTest
 */
public class FileBasedStateRepositoryTest {
    private static String HOLDER_FILE_NAME = "holder-0-myHolder.bin";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testHolderSave() throws Exception {
        File directory = folder.newFolder("testSave");
        FileBasedStateRepository stateRepository = new FileBasedStateRepository(directory);
        String holderName = "myHolder";
        StateHolder<Long, String> myHolder = stateRepository.getPersistentStateHolder(holderName, Long.class, String.class, ( c) -> true, null);
        myHolder.putIfAbsent(42L, "TheAnswer!");
        stateRepository.close();
        try (FileInputStream fis = new FileInputStream(new File(directory, FileBasedStateRepositoryTest.HOLDER_FILE_NAME));ObjectInputStream ois = new ObjectInputStream(fis)) {
            String name = ((String) (ois.readObject()));
            Assert.assertThat(name, Matchers.is(holderName));
            FileBasedStateRepository.Tuple loadedTuple = ((FileBasedStateRepository.Tuple) (ois.readObject()));
            Assert.assertThat(loadedTuple.index, Matchers.is(0));
            @SuppressWarnings("unchecked")
            StateHolder<Long, String> stateHolder = ((StateHolder<Long, String>) (loadedTuple.holder));
            Assert.assertThat(stateHolder, Matchers.is(myHolder));
        }
    }

    @Test
    public void testHolderLoad() throws Exception {
        File directory = folder.newFolder("testLoad");
        String holderName = "myHolder";
        StateHolder<Long, String> map = new TransientStateHolder<>();
        map.putIfAbsent(42L, "Again? That's not even funny anymore!!");
        try (FileOutputStream fos = new FileOutputStream(new File(directory, FileBasedStateRepositoryTest.HOLDER_FILE_NAME));ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(holderName);
            oos.writeObject(new FileBasedStateRepository.Tuple(0, map));
        }
        FileBasedStateRepository stateRepository = new FileBasedStateRepository(directory);
        StateHolder<Long, String> myHolder = stateRepository.getPersistentStateHolder(holderName, Long.class, String.class, ( c) -> true, null);
        Assert.assertThat(myHolder, Matchers.is(map));
    }

    @Test
    public void testIndexProperlySetAfterLoad() throws Exception {
        File directory = folder.newFolder("testIndexAfterLoad");
        String holderName = "myHolder";
        try (FileOutputStream fos = new FileOutputStream(new File(directory, FileBasedStateRepositoryTest.HOLDER_FILE_NAME));ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(holderName);
            oos.writeObject(new FileBasedStateRepository.Tuple(0, new TransientStateHolder<Long, String>()));
        }
        FileBasedStateRepository stateRepository = new FileBasedStateRepository(directory);
        stateRepository.getPersistentStateHolder("otherHolder", Long.class, Long.class, ( c) -> true, null);
        stateRepository.close();
        File[] files = directory.listFiles(( dir, name) -> (name.contains("otherHolder")) && (name.contains("-1-")));
        Assert.assertThat(files.length, Matchers.is(1));
    }
}

