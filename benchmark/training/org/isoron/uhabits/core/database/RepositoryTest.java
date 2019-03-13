/**
 * Copyright (C) 2017 ?linson Santos Xavier <isoron@gmail.com>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isoron.uhabits.core.database;


import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.isoron.uhabits.core.BaseUnitTest;
import org.isoron.uhabits.core.Database;
import org.junit.Assert;
import org.junit.Test;


public class RepositoryTest extends BaseUnitTest {
    private Repository<ThingRecord> repository;

    private Database db;

    @Test
    public void testFind() throws Exception {
        db.execute(("insert into tests(id, color_number, name, score) " + "values (10, 20, 'hello', 8.0)"));
        ThingRecord record = repository.find(10L);
        Assert.assertNotNull(record);
        MatcherAssert.assertThat(record.id, IsEqual.equalTo(10L));
        MatcherAssert.assertThat(record.color, IsEqual.equalTo(20));
        MatcherAssert.assertThat(record.name, IsEqual.equalTo("hello"));
        MatcherAssert.assertThat(record.score, IsEqual.equalTo(8.0));
    }

    @Test
    public void testSave_withId() throws Exception {
        ThingRecord record = new ThingRecord();
        record.id = 50L;
        record.color = 10;
        record.name = "hello";
        record.score = 5.0;
        repository.save(record);
        MatcherAssert.assertThat(record, IsEqual.equalTo(repository.find(50L)));
        record.name = "world";
        record.score = 128.0;
        repository.save(record);
        MatcherAssert.assertThat(record, IsEqual.equalTo(repository.find(50L)));
    }

    @Test
    public void testSave_withNull() throws Exception {
        ThingRecord record = new ThingRecord();
        record.color = 50;
        record.name = null;
        record.score = 12.0;
        repository.save(record);
        ThingRecord retrieved = repository.find(record.id);
        Assert.assertNotNull(retrieved);
        Assert.assertNull(retrieved.name);
        MatcherAssert.assertThat(record, IsEqual.equalTo(retrieved));
    }

    @Test
    public void testSave_withoutId() throws Exception {
        ThingRecord r1 = new ThingRecord();
        r1.color = 10;
        r1.name = "hello";
        r1.score = 16.0;
        repository.save(r1);
        ThingRecord r2 = new ThingRecord();
        r2.color = 20;
        r2.name = "world";
        r2.score = 2.0;
        repository.save(r2);
        MatcherAssert.assertThat(r1.id, IsEqual.equalTo(1L));
        MatcherAssert.assertThat(r2.id, IsEqual.equalTo(2L));
    }

    @Test
    public void testRemove() throws Exception {
        ThingRecord rec1 = new ThingRecord();
        rec1.color = 10;
        rec1.name = "hello";
        rec1.score = 16.0;
        repository.save(rec1);
        ThingRecord rec2 = new ThingRecord();
        rec2.color = 20;
        rec2.name = "world";
        rec2.score = 32.0;
        repository.save(rec2);
        long id = rec1.id;
        MatcherAssert.assertThat(rec1, IsEqual.equalTo(repository.find(id)));
        MatcherAssert.assertThat(rec2, IsEqual.equalTo(repository.find(rec2.id)));
        repository.remove(rec1);
        MatcherAssert.assertThat(rec1.id, IsEqual.equalTo(null));
        Assert.assertNull(repository.find(id));
        MatcherAssert.assertThat(rec2, IsEqual.equalTo(repository.find(rec2.id)));
        repository.remove(rec1);// should have no effect

        Assert.assertNull(repository.find(id));
    }
}

