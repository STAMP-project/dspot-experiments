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
package org.graylog2.indexer.fieldtypes;


import com.google.common.collect.ImmutableSet;
import com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb;
import java.util.Collections;
import org.graylog2.database.MongoConnectionRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;


public class IndexFieldTypesServiceTest {
    @ClassRule
    public static final InMemoryMongoDb IN_MEMORY_MONGO_DB = newInMemoryMongoDbRule().build();

    @Rule
    public MongoConnectionRule mongoRule = MongoConnectionRule.build("test");

    private IndexFieldTypesService dbService;

    @Test
    public void saveGetDeleteStream() {
        final IndexFieldTypesDTO newDto1 = createDto("graylog_0", Collections.emptySet());
        final IndexFieldTypesDTO newDto2 = createDto("graylog_1", Collections.emptySet());
        final IndexFieldTypesDTO savedDto1 = dbService.save(newDto1);
        final IndexFieldTypesDTO savedDto2 = dbService.save(newDto2);
        final IndexFieldTypesDTO dto1 = dbService.get(savedDto1.id()).orElse(null);
        assertThat(dto1).as("check that saving the DTO worked").isNotNull();
        assertThat(dto1.id()).isNotBlank();
        assertThat(dto1.indexName()).isEqualTo("graylog_0");
        assertThat(dto1.fields()).containsOnly(FieldTypeDTO.create("message", "text"), FieldTypeDTO.create("source", "text"), FieldTypeDTO.create("timestamp", "date"), FieldTypeDTO.create("http_method", "keyword"), FieldTypeDTO.create("http_status", "long"));
        final IndexFieldTypesDTO dto2 = dbService.get(savedDto2.indexName()).orElse(null);
        assertThat(dto2).as("check that get by index_name works").isNotNull().extracting("indexName").containsOnly("graylog_1");
        assertThat(dbService.findAll().size()).as("check that all entries are returned as a stream").isEqualTo(2);
        dbService.delete(dto1.id());
        assertThat(dbService.get(dto1.id())).as("check that delete works").isNotPresent();
    }

    @Test
    public void upsert() {
        final IndexFieldTypesDTO newDto1 = createDto("graylog_0", Collections.emptySet());
        final IndexFieldTypesDTO newDto2 = createDto("graylog_1", Collections.emptySet());
        assertThat(dbService.findAll().size()).isEqualTo(0);
        final IndexFieldTypesDTO upsertedDto1 = dbService.upsert(newDto1).orElse(null);
        final IndexFieldTypesDTO upsertedDto2 = dbService.upsert(newDto2).orElse(null);
        assertThat(upsertedDto1).isNotNull();
        assertThat(upsertedDto2).isNotNull();
        assertThat(upsertedDto1.indexName()).isEqualTo("graylog_0");
        assertThat(upsertedDto2.indexName()).isEqualTo("graylog_1");
        assertThat(dbService.findAll().size()).isEqualTo(2);
        assertThat(dbService.upsert(newDto1)).isNotPresent();
        assertThat(dbService.upsert(newDto2)).isNotPresent();
        assertThat(dbService.findAll().size()).isEqualTo(2);
    }

    @Test
    public void streamForIndexSet() {
        final IndexFieldTypesDTO newDto1 = createDto("graylog_0", "abc", Collections.emptySet());
        final IndexFieldTypesDTO newDto2 = createDto("graylog_1", "xyz", Collections.emptySet());
        final IndexFieldTypesDTO newDto3 = createDto("graylog_2", "xyz", Collections.emptySet());
        final IndexFieldTypesDTO savedDto1 = dbService.save(newDto1);
        final IndexFieldTypesDTO savedDto2 = dbService.save(newDto2);
        final IndexFieldTypesDTO savedDto3 = dbService.save(newDto3);
        assertThat(dbService.findForIndexSet("abc").size()).isEqualTo(1);
        assertThat(dbService.findForIndexSet("xyz").size()).isEqualTo(2);
        assertThat(dbService.findForIndexSet("abc")).first().isEqualTo(savedDto1);
        assertThat(dbService.findForIndexSet("xyz").toArray()).containsExactly(savedDto2, savedDto3);
    }

    @Test
    public void streamForFieldNames() throws Exception {
        dbService.save(createDto("graylog_0", "abc", Collections.emptySet()));
        dbService.save(createDto("graylog_1", "xyz", Collections.emptySet()));
        dbService.save(createDto("graylog_2", "xyz", Collections.emptySet()));
        dbService.save(createDto("graylog_3", "xyz", ImmutableSet.of(FieldTypeDTO.create("yolo1", "text"))));
        assertThat(dbService.findForFieldNames(ImmutableSet.of()).size()).isEqualTo(0);
        assertThat(dbService.findForFieldNames(ImmutableSet.of("message")).size()).isEqualTo(4);
        assertThat(dbService.findForFieldNames(ImmutableSet.of("message", "yolo_1")).size()).isEqualTo(4);
        assertThat(dbService.findForFieldNames(ImmutableSet.of("yolo1")).size()).isEqualTo(1);
        assertThat(dbService.findForFieldNames(ImmutableSet.of("source")).size()).isEqualTo(4);
        assertThat(dbService.findForFieldNames(ImmutableSet.of("source", "non-existent")).size()).isEqualTo(4);
        assertThat(dbService.findForFieldNames(ImmutableSet.of("non-existent")).size()).isEqualTo(0);
        assertThat(dbService.findForFieldNames(ImmutableSet.of("non-existent", "yolo1")).size()).isEqualTo(1);
    }

    @Test
    public void streamForFieldNamesAndIndices() throws Exception {
        dbService.save(createDto("graylog_0", "abc", Collections.emptySet()));
        dbService.save(createDto("graylog_1", "xyz", Collections.emptySet()));
        dbService.save(createDto("graylog_2", "xyz", Collections.emptySet()));
        dbService.save(createDto("graylog_3", "xyz", ImmutableSet.of(FieldTypeDTO.create("yolo1", "text"))));
        assertThat(dbService.findForFieldNamesAndIndices(ImmutableSet.of(), ImmutableSet.of()).size()).isEqualTo(0);
        assertThat(dbService.findForFieldNamesAndIndices(ImmutableSet.of("message"), ImmutableSet.of("graylog_1")).size()).isEqualTo(1);
        assertThat(dbService.findForFieldNamesAndIndices(ImmutableSet.of("message", "yolo1"), ImmutableSet.of("graylog_1", "graylog_3")).size()).isEqualTo(2);
        assertThat(dbService.findForFieldNamesAndIndices(ImmutableSet.of("message", "yolo1"), ImmutableSet.of("graylog_1", "graylog_3", "graylog_0")).size()).isEqualTo(3);
    }
}

