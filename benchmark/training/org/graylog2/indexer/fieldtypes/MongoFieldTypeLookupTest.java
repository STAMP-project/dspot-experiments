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


import FieldTypes.Type;
import com.google.common.collect.ImmutableSet;
import com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb;
import java.util.Collections;
import java.util.Map;
import org.graylog2.database.MongoConnectionRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;


public class MongoFieldTypeLookupTest {
    @ClassRule
    public static final InMemoryMongoDb IN_MEMORY_MONGO_DB = newInMemoryMongoDbRule().build();

    @Rule
    public MongoConnectionRule mongoRule = MongoConnectionRule.build("test");

    private IndexFieldTypesService dbService;

    private MongoFieldTypeLookup lookup;

    @Test
    public void getSingleField() {
        dbService.save(createDto("graylog_0", "abc", Collections.emptySet()));
        dbService.save(createDto("graylog_1", "xyz", Collections.emptySet()));
        dbService.save(createDto("graylog_2", "xyz", Collections.emptySet()));
        dbService.save(createDto("graylog_3", "xyz", ImmutableSet.of(FieldTypeDTO.create("yolo1", "text"))));
        final FieldTypes result = lookup.get("message").orElse(null);
        assertThat(result).isNotNull();
        assertThat(result.fieldName()).isEqualTo("message");
        assertThat(result.types()).containsOnly(Type.builder().type("string").properties(ImmutableSet.of("full-text-search")).indexNames(ImmutableSet.of("graylog_0", "graylog_1", "graylog_2", "graylog_3")).build());
    }

    @Test
    public void getMultipleFields() {
        dbService.save(createDto("graylog_0", "abc", Collections.emptySet()));
        dbService.save(createDto("graylog_1", "xyz", Collections.emptySet()));
        dbService.save(createDto("graylog_2", "xyz", ImmutableSet.of(FieldTypeDTO.create("yolo1", "boolean"))));
        dbService.save(createDto("graylog_3", "xyz", ImmutableSet.of(FieldTypeDTO.create("yolo1", "text"))));
        final Map<String, FieldTypes> result = lookup.get(ImmutableSet.of("yolo1", "timestamp"));
        assertThat(result).containsOnlyKeys("yolo1", "timestamp");
        assertThat(result.get("yolo1").fieldName()).isEqualTo("yolo1");
        assertThat(result.get("yolo1").types()).hasSize(2);
        assertThat(result.get("yolo1").types()).containsOnly(Type.builder().type("string").properties(ImmutableSet.of("full-text-search")).indexNames(ImmutableSet.of("graylog_3")).build(), Type.builder().type("boolean").properties(ImmutableSet.of("enumerable")).indexNames(ImmutableSet.of("graylog_2")).build());
        assertThat(result.get("timestamp").fieldName()).isEqualTo("timestamp");
        assertThat(result.get("timestamp").types()).hasSize(1);
        assertThat(result.get("timestamp").types()).containsOnly(Type.builder().type("date").properties(ImmutableSet.of("enumerable")).indexNames(ImmutableSet.of("graylog_0", "graylog_1", "graylog_2", "graylog_3")).build());
    }

    @Test
    public void getMultipleFieldsWithIndexScope() {
        dbService.save(createDto("graylog_0", "abc", Collections.emptySet()));
        dbService.save(createDto("graylog_1", "xyz", Collections.emptySet()));
        dbService.save(createDto("graylog_2", "xyz", ImmutableSet.of(FieldTypeDTO.create("yolo1", "boolean"))));
        dbService.save(createDto("graylog_3", "xyz", ImmutableSet.of(FieldTypeDTO.create("yolo1", "text"))));
        final Map<String, FieldTypes> result = lookup.get(ImmutableSet.of("yolo1", "timestamp"), ImmutableSet.of("graylog_1", "graylog_2"));
        assertThat(result).containsOnlyKeys("yolo1", "timestamp");
        assertThat(result.get("yolo1").fieldName()).isEqualTo("yolo1");
        assertThat(result.get("yolo1").types()).hasSize(1);
        assertThat(result.get("yolo1").types()).containsOnly(Type.builder().type("boolean").properties(ImmutableSet.of("enumerable")).indexNames(ImmutableSet.of("graylog_2")).build());
        assertThat(result.get("timestamp").fieldName()).isEqualTo("timestamp");
        assertThat(result.get("timestamp").types()).hasSize(1);
        assertThat(result.get("timestamp").types()).containsOnly(Type.builder().type("date").properties(ImmutableSet.of("enumerable")).indexNames(ImmutableSet.of("graylog_1", "graylog_2")).build());
    }
}

