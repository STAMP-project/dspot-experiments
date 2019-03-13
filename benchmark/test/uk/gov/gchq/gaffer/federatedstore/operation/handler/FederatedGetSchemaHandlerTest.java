/**
 * Copyright 2017-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.federatedstore.operation.handler;


import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.AccumuloProperties;
import uk.gov.gchq.gaffer.commonutil.JsonAssert;
import uk.gov.gchq.gaffer.federatedstore.FederatedStore;
import uk.gov.gchq.gaffer.federatedstore.operation.AddGraph;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.serialisation.implementation.StringSerialiser;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.StoreProperties;
import uk.gov.gchq.gaffer.store.library.HashMapGraphLibrary;
import uk.gov.gchq.gaffer.store.operation.GetSchema;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition;
import uk.gov.gchq.gaffer.store.schema.TypeDefinition;
import uk.gov.gchq.gaffer.user.User;
import uk.gov.gchq.koryphe.impl.binaryoperator.StringConcat;


public class FederatedGetSchemaHandlerTest {
    private FederatedGetSchemaHandler handler;

    private FederatedStore fStore;

    private Context context;

    private User user;

    private StoreProperties properties;

    private AccumuloProperties accProperties;

    private static final String ACC_PROP_ID = "accProp";

    private static final String EDGE_SCHEMA_ID = "edgeSchema";

    private final String TEST_FED_STORE = "testFedStore";

    private final HashMapGraphLibrary library = new HashMapGraphLibrary();

    private final Schema STRING_SCHEMA = new Schema.Builder().type("string", new TypeDefinition.Builder().clazz(String.class).serialiser(new StringSerialiser()).aggregateFunction(new StringConcat()).build()).build();

    @Test
    public void shouldReturnSchema() throws OperationException {
        library.addProperties(FederatedGetSchemaHandlerTest.ACC_PROP_ID, accProperties);
        fStore.setGraphLibrary(library);
        final Schema edgeSchema = new Schema.Builder().edge("edge", new SchemaEdgeDefinition.Builder().source("string").destination("string").directed(DIRECTED_EITHER).property("prop1", "string").build()).vertexSerialiser(new StringSerialiser()).type(DIRECTED_EITHER, Boolean.class).merge(STRING_SCHEMA).build();
        library.addSchema(FederatedGetSchemaHandlerTest.EDGE_SCHEMA_ID, edgeSchema);
        fStore.execute(Operation.asOperationChain(new AddGraph.Builder().graphId("schema").parentPropertiesId(FederatedGetSchemaHandlerTest.ACC_PROP_ID).parentSchemaIds(Lists.newArrayList(FederatedGetSchemaHandlerTest.EDGE_SCHEMA_ID)).build()), context);
        final GetSchema operation = new GetSchema.Builder().compact(true).build();
        // When
        final Schema result = handler.doOperation(operation, context, fStore);
        // Then
        Assert.assertNotNull(result);
        JsonAssert.assertEquals(edgeSchema.toJson(true), result.toJson(true));
    }

    @Test
    public void shouldReturnSchemaOnlyForEnabledGraphs() throws OperationException {
        library.addProperties(FederatedGetSchemaHandlerTest.ACC_PROP_ID, accProperties);
        fStore.setGraphLibrary(library);
        final Schema edgeSchema1 = new Schema.Builder().edge("edge", new SchemaEdgeDefinition.Builder().source("string").destination("string").property("prop1", "string").directed(DIRECTED_EITHER).build()).vertexSerialiser(new StringSerialiser()).type(DIRECTED_EITHER, Boolean.class).merge(STRING_SCHEMA).build();
        library.addSchema("edgeSchema1", edgeSchema1);
        final Schema edgeSchema2 = new Schema.Builder().edge("edge", new SchemaEdgeDefinition.Builder().source("string").destination("string").property("prop2", "string").directed(DIRECTED_EITHER).build()).vertexSerialiser(new StringSerialiser()).type(DIRECTED_EITHER, Boolean.class).merge(STRING_SCHEMA).build();
        library.addSchema("edgeSchema2", edgeSchema2);
        fStore.execute(Operation.asOperationChain(new AddGraph.Builder().graphId("schemaEnabled").parentPropertiesId(FederatedGetSchemaHandlerTest.ACC_PROP_ID).parentSchemaIds(Lists.newArrayList("edgeSchema1")).disabledByDefault(false).build()), context);
        fStore.execute(Operation.asOperationChain(new AddGraph.Builder().graphId("schemaDisabled").parentPropertiesId(FederatedGetSchemaHandlerTest.ACC_PROP_ID).parentSchemaIds(Lists.newArrayList("edgeSchema2")).disabledByDefault(true).build()), context);
        final GetSchema operation = new GetSchema.Builder().compact(true).build();
        // When
        final Schema result = handler.doOperation(operation, context, fStore);
        // Then
        Assert.assertNotNull(result);
        JsonAssert.assertEquals(edgeSchema1.toJson(true), result.toJson(true));
    }

    @Test
    public void shouldThrowExceptionForANullOperation() throws OperationException {
        library.addProperties(FederatedGetSchemaHandlerTest.ACC_PROP_ID, accProperties);
        fStore.setGraphLibrary(library);
        final GetSchema operation = null;
        try {
            handler.doOperation(operation, context, fStore);
        } catch (final OperationException e) {
            Assert.assertTrue(e.getMessage().contains("Operation cannot be null"));
        }
    }
}

