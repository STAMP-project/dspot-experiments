/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.data;


import Envelope.FieldName.AFTER;
import Envelope.FieldName.BEFORE;
import Envelope.FieldName.OPERATION;
import Envelope.FieldName.SOURCE;
import Schema.OPTIONAL_INT64_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import Schema.STRING_SCHEMA;
import org.junit.Test;


/**
 *
 *
 * @author Randall Hauch
 */
public class EnvelopeTest {
    @Test
    public void shouldBuildWithSimpleOptionalTypesForBeforeAndAfter() {
        Envelope env = Envelope.defineSchema().withName("someName").withRecord(OPTIONAL_STRING_SCHEMA).withSource(OPTIONAL_INT64_SCHEMA).build();
        assertThat(env.schema()).isNotNull();
        assertThat(env.schema().name()).isEqualTo("someName");
        assertThat(env.schema().doc()).isNull();
        assertThat(env.schema().version()).isNull();
        assertOptionalField(env, AFTER, OPTIONAL_STRING_SCHEMA);
        assertOptionalField(env, BEFORE, OPTIONAL_STRING_SCHEMA);
        assertOptionalField(env, SOURCE, OPTIONAL_INT64_SCHEMA);
        assertRequiredField(env, OPERATION, STRING_SCHEMA);
    }
}

