/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.rest.util;


import Schema.BOOLEAN_SCHEMA;
import Schema.FLOAT64_SCHEMA;
import Schema.INT32_SCHEMA;
import Schema.INT64_SCHEMA;
import Schema.STRING_SCHEMA;
import io.confluent.ksql.rest.entity.FieldInfo;
import java.util.List;
import java.util.Optional;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class EntityUtilTest {
    @Test
    public void shouldBuildCorrectIntegerField() {
        shouldBuildCorrectPrimitiveField(INT32_SCHEMA, "INTEGER");
    }

    @Test
    public void shouldBuildCorrectBigintField() {
        shouldBuildCorrectPrimitiveField(INT64_SCHEMA, "BIGINT");
    }

    @Test
    public void shouldBuildCorrectDoubleField() {
        shouldBuildCorrectPrimitiveField(FLOAT64_SCHEMA, "DOUBLE");
    }

    @Test
    public void shouldBuildCorrectStringField() {
        shouldBuildCorrectPrimitiveField(STRING_SCHEMA, "STRING");
    }

    @Test
    public void shouldBuildCorrectBooleanField() {
        shouldBuildCorrectPrimitiveField(BOOLEAN_SCHEMA, "BOOLEAN");
    }

    @Test
    public void shouldBuildCorrectMapField() {
        final Schema schema = SchemaBuilder.struct().field("field", SchemaBuilder.map(STRING_SCHEMA, INT32_SCHEMA)).build();
        final List<FieldInfo> entity = EntityUtil.buildSourceSchemaEntity(schema);
        Assert.assertThat(entity.size(), CoreMatchers.equalTo(1));
        Assert.assertThat(entity.get(0).getName(), CoreMatchers.equalTo("field"));
        Assert.assertThat(entity.get(0).getSchema().getTypeName(), CoreMatchers.equalTo("MAP"));
        Assert.assertThat(entity.get(0).getSchema().getFields(), CoreMatchers.equalTo(Optional.empty()));
        Assert.assertThat(entity.get(0).getSchema().getMemberSchema().get().getTypeName(), CoreMatchers.equalTo("INTEGER"));
    }

    @Test
    public void shouldBuildCorrectArrayField() {
        final Schema schema = SchemaBuilder.struct().field("field", SchemaBuilder.array(SchemaBuilder.INT64_SCHEMA)).build();
        final List<FieldInfo> entity = EntityUtil.buildSourceSchemaEntity(schema);
        Assert.assertThat(entity.size(), CoreMatchers.equalTo(1));
        Assert.assertThat(entity.get(0).getName(), CoreMatchers.equalTo("field"));
        Assert.assertThat(entity.get(0).getSchema().getTypeName(), CoreMatchers.equalTo("ARRAY"));
        Assert.assertThat(entity.get(0).getSchema().getFields(), CoreMatchers.equalTo(Optional.empty()));
        Assert.assertThat(entity.get(0).getSchema().getMemberSchema().get().getTypeName(), CoreMatchers.equalTo("BIGINT"));
    }

    @Test
    public void shouldBuildCorrectStructField() {
        final Schema schema = SchemaBuilder.struct().field("field", SchemaBuilder.struct().field("innerField", STRING_SCHEMA).build()).build();
        final List<FieldInfo> entity = EntityUtil.buildSourceSchemaEntity(schema);
        Assert.assertThat(entity.size(), CoreMatchers.equalTo(1));
        Assert.assertThat(entity.get(0).getName(), CoreMatchers.equalTo("field"));
        Assert.assertThat(entity.get(0).getSchema().getTypeName(), CoreMatchers.equalTo("STRUCT"));
        Assert.assertThat(entity.get(0).getSchema().getFields().get().size(), CoreMatchers.equalTo(1));
        final FieldInfo inner = entity.get(0).getSchema().getFields().get().get(0);
        Assert.assertThat(inner.getSchema().getTypeName(), CoreMatchers.equalTo("STRING"));
        Assert.assertThat(entity.get(0).getSchema().getMemberSchema(), CoreMatchers.equalTo(Optional.empty()));
    }

    @Test
    public void shouldBuildMiltipleFieldsCorrectly() {
        final Schema schema = SchemaBuilder.struct().field("field1", INT32_SCHEMA).field("field2", INT64_SCHEMA).build();
        final List<FieldInfo> entity = EntityUtil.buildSourceSchemaEntity(schema);
        Assert.assertThat(entity.size(), CoreMatchers.equalTo(2));
        Assert.assertThat(entity.get(0).getName(), CoreMatchers.equalTo("field1"));
        Assert.assertThat(entity.get(0).getSchema().getTypeName(), CoreMatchers.equalTo("INTEGER"));
        Assert.assertThat(entity.get(1).getName(), CoreMatchers.equalTo("field2"));
        Assert.assertThat(entity.get(1).getSchema().getTypeName(), CoreMatchers.equalTo("BIGINT"));
    }
}

