/**
 * Copyright 2012-2018 Chronicle Map Contributors
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
package net.openhft.chronicle.map;


import com.google.common.io.Files;
import java.io.File;
import java.net.URL;
import net.openhft.chronicle.set.Builder;
import org.junit.Assert;
import org.junit.Test;


public class ChronicleMap3_12IntegerKeyCompatibilityTest {
    @Test
    public void testWithChecksums() throws Exception {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL fileUrl = cl.getResource("chronicle-map-3-12-with-checksums.dat");
        File file = new File(fileUrl.toURI());
        File persistenceFile = Builder.getPersistenceFile();
        Files.copy(file, persistenceFile);
        try (ChronicleMap<Integer, String> map = ChronicleMap.of(Integer.class, String.class).averageValue("1").entries(1).recoverPersistedTo(persistenceFile, false)) {
            Assert.assertEquals(2, map.size());
            Assert.assertEquals("1", map.get(1));
            Assert.assertEquals("-1", map.get((-1)));
        }
    }

    @Test
    public void testNoChecksums() throws Exception {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL fileUrl = cl.getResource("chronicle-map-3-12-no-checksums.dat");
        File file = new File(fileUrl.toURI());
        File persistenceFile = Builder.getPersistenceFile();
        Files.copy(file, persistenceFile);
        try (ChronicleMap<Integer, String> map = ChronicleMap.of(Integer.class, String.class).averageValue("1").entries(1).checksumEntries(false).recoverPersistedTo(persistenceFile, false)) {
            Assert.assertEquals(2, map.size());
            Assert.assertEquals("1", map.get(1));
            Assert.assertEquals("-1", map.get((-1)));
        }
    }
}

