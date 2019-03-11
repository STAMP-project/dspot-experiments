/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.indexer.ranges;


import DateTimeZone.UTC;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb;
import java.util.SortedSet;
import org.bson.types.ObjectId;
import org.graylog2.database.MongoConnectionRule;
import org.graylog2.database.NotFoundException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class LegacyMongoIndexRangeServiceTest {
    @ClassRule
    public static final InMemoryMongoDb IN_MEMORY_MONGO_DB = newInMemoryMongoDbRule().build();

    private static final DateTime EPOCH = new DateTime(0L, DateTimeZone.UTC);

    @Rule
    public MongoConnectionRule mongoRule = MongoConnectionRule.build("test");

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private LegacyMongoIndexRangeService indexRangeService;

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testGetExistingIndexRange() throws Exception {
        final IndexRange indexRange = indexRangeService.get("graylog_0");
        final DateTime end = new DateTime(2015, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);
        final IndexRange expected = MongoIndexRange.create(new ObjectId("56250da2d400000000000001"), "graylog_0", LegacyMongoIndexRangeServiceTest.EPOCH, end, end, 0);
        assertThat(indexRange).isEqualTo(expected);
    }

    @Test(expected = NotFoundException.class)
    @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testGetNonExistingIndexRange() throws Exception {
        indexRangeService.get("does-not-exist");
    }

    @Test(expected = NotFoundException.class)
    @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testGetInvalidIndexRange() throws Exception {
        indexRangeService.get("invalid");
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testGetIncompleteIndexRange() throws Exception {
        final IndexRange indexRange = indexRangeService.get("graylog_99");
        final DateTime end = new DateTime(2015, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);
        final IndexRange expected = MongoIndexRange.create(new ObjectId("56250da2d400000000000099"), "graylog_99", LegacyMongoIndexRangeServiceTest.EPOCH, end, LegacyMongoIndexRangeServiceTest.EPOCH, 0);
        assertThat(indexRange).isEqualTo(expected);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFind() throws Exception {
        indexRangeService.find(new DateTime(0L, DateTimeZone.UTC), DateTime.now(UTC));
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testFindAll() throws Exception {
        final SortedSet<IndexRange> indexRanges = indexRangeService.findAll();
        final DateTime end0 = new DateTime(2015, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);
        final DateTime end1 = new DateTime(2015, 1, 2, 0, 0, 0, 0, DateTimeZone.UTC);
        final DateTime end2 = new DateTime(2015, 1, 3, 0, 0, 0, 0, DateTimeZone.UTC);
        final DateTime end99 = new DateTime(2015, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);
        assertThat(indexRanges).containsExactly(MongoIndexRange.create(new ObjectId("56250da2d400000000000001"), "graylog_0", LegacyMongoIndexRangeServiceTest.EPOCH, end0, end0, 0), MongoIndexRange.create(new ObjectId("56250da2d400000000000099"), "graylog_99", LegacyMongoIndexRangeServiceTest.EPOCH, end99, LegacyMongoIndexRangeServiceTest.EPOCH, 0), MongoIndexRange.create(new ObjectId("56250da2d400000000000002"), "graylog_1", LegacyMongoIndexRangeServiceTest.EPOCH, end1, end1, 1), MongoIndexRange.create(new ObjectId("56250da2d400000000000003"), "graylog_2", LegacyMongoIndexRangeServiceTest.EPOCH, end2, end2, 2));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSave() throws Exception {
        indexRangeService.save(((IndexRange) (null)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCalculateRange() throws Exception {
        indexRangeService.calculateRange("graylog_0");
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testDelete() throws Exception {
        assertThat(indexRangeService.findAll()).hasSize(4);
        indexRangeService.delete("graylog_1");
        assertThat(indexRangeService.findAll()).hasSize(3);
    }
}

