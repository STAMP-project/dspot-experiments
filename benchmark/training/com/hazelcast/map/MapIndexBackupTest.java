/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.map;


import com.hazelcast.core.MapLoader;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapIndexBackupTest extends HazelcastTestSupport {
    // Issue: https://github.com/hazelcast/hazelcast/issues/6840
    @Test
    public void backupsShouldNotBeIndexedWhenThereIsNoMigration() {
        backupsShouldNotBeIndexed(false);
    }

    // Issue: https://github.com/hazelcast/hazelcast/issues/6840
    @Test
    public void backupsShouldNotBeIndexedWhenThereIsMigration() {
        backupsShouldNotBeIndexed(true);
    }

    public static class Book implements Serializable {
        private long id;

        private String title;

        private String author;

        private int year;

        private Book() {
        }

        Book(long id, String title, String author, int year) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.year = year;
        }

        public long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }

        public int getYear() {
            return year;
        }
    }

    private static class BookMapLoader implements MapLoader<Integer, MapIndexBackupTest.Book> {
        @Override
        public MapIndexBackupTest.Book load(Integer key) {
            return loadAll(Collections.singleton(key)).get(key);
        }

        @Override
        public Map<Integer, MapIndexBackupTest.Book> loadAll(Collection<Integer> keys) {
            Map<Integer, MapIndexBackupTest.Book> map = new TreeMap<Integer, MapIndexBackupTest.Book>();
            for (int key : keys) {
                map.put(key, new MapIndexBackupTest.Book(key, String.valueOf(key), String.valueOf((key % 7)), (1800 + (key % 200))));
            }
            return map;
        }

        @Override
        public Iterable<Integer> loadAllKeys() {
            List<Integer> keys = new ArrayList<Integer>(2000);
            for (int i = 0; i < 2000; i++) {
                keys.add(i);
            }
            return keys;
        }
    }
}

