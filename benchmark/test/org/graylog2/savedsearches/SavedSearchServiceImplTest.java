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
package org.graylog2.savedsearches;


import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.graylog2.database.MongoConnectionRule;
import org.graylog2.database.NotFoundException;
import org.graylog2.plugin.Tools;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;


public class SavedSearchServiceImplTest {
    @ClassRule
    public static final InMemoryMongoDb IN_MEMORY_MONGO_DB = newInMemoryMongoDbRule().build();

    @Rule
    public MongoConnectionRule mongoRule = MongoConnectionRule.build("test");

    private SavedSearchService savedSearchService;

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    public void testAllEmptyCollection() throws Exception {
        final List<SavedSearch> savedSearches = this.savedSearchService.all();
        Assert.assertNotNull("Returned list should not be null", savedSearches);
        Assert.assertEquals("Returned list should contain exactly one saved search", 0, savedSearches.size());
    }

    @Test
    @UsingDataSet(locations = "savedSearchSingleDocument.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testAllSingleDocumentInCollection() throws Exception {
        final List<SavedSearch> savedSearches = this.savedSearchService.all();
        Assert.assertNotNull("Returned list should not be null", savedSearches);
        Assert.assertEquals("Returned list should contain exactly one saved search", 1, savedSearches.size());
    }

    @Test
    @UsingDataSet(locations = { "savedSearchSingleDocument.json", "savedSearchSingleDocument2.json" }, loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testAllMultipleDocumentsInCollection() throws Exception {
        final List<SavedSearch> savedSearches = this.savedSearchService.all();
        Assert.assertNotNull("Returned list should not be null", savedSearches);
        Assert.assertEquals("Returned list should contain exactly one saved search", 2, savedSearches.size());
    }

    @Test
    @UsingDataSet(locations = { "savedSearchSingleDocument.json", "savedSearchSingleDocument2.json" }, loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testLoad() throws Exception {
        final String id1 = "5400deadbeefdeadbeefaffe";
        final SavedSearch savedSearch = savedSearchService.load(id1);
        Assert.assertNotNull("Should return a saved search", savedSearch);
        Assert.assertEquals("Should return the saved search with the correct id", id1, savedSearch.getId());
        final String id2 = "54e3deadbeefdeadbeefaffe";
        final SavedSearch savedSearch2 = savedSearchService.load(id2);
        Assert.assertNotNull("Should return a saved search", savedSearch2);
        Assert.assertEquals("Should return the saved search with the correct id", id2, savedSearch2.getId());
    }

    @Test(expected = NotFoundException.class)
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    public void testLoadNonexisting() throws Exception {
        savedSearchService.load("54e3deadbeefdeadbeefaffe");
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    public void testLoadInvalidId() throws Exception {
        savedSearchService.load("foobar");
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
    public void testCreate() throws Exception {
        final String title = "Example Title";
        final Map<String, Object> query = Collections.emptyMap();
        final String creatorUserId = "someuser";
        final DateTime createdAt = Tools.nowUTC();
        final SavedSearch savedSearch = savedSearchService.create(title, query, creatorUserId, createdAt);
        Assert.assertNotNull("Should have returned a SavedSearch", savedSearch);
        Assert.assertEquals("Collection should still be empty", 0, savedSearchService.all().size());
    }
}

