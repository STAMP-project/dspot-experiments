/**
 * Copyright 2017 Google LLC
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.api.graphql.rejoiner;


import Scalars.GraphQLString;
import com.google.api.graphql.rejoiner.Annotations.ExtraTypes;
import com.google.api.graphql.rejoiner.Annotations.GraphModifications;
import com.google.api.graphql.rejoiner.Annotations.Mutations;
import com.google.api.graphql.rejoiner.Annotations.Queries;
import com.google.api.graphql.rejoiner.Greetings.GreetingsRequest;
import com.google.api.graphql.rejoiner.Greetings.GreetingsResponse;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.protobuf.Descriptors.FileDescriptor;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link SchemaModule}.
 */
@RunWith(JUnit4.class)
public final class SchemaModuleTest {
    private static final Key<Set<GraphQLFieldDefinition>> QUERY_KEY = Key.get(new com.google.inject.TypeLiteral<Set<GraphQLFieldDefinition>>() {}, Queries.class);

    private static final Key<Set<GraphQLFieldDefinition>> MUTATION_KEY = Key.get(new com.google.inject.TypeLiteral<Set<GraphQLFieldDefinition>>() {}, Mutations.class);

    private static final Key<Set<FileDescriptor>> EXTRA_TYPE_KEY = Key.get(new com.google.inject.TypeLiteral<Set<FileDescriptor>>() {}, ExtraTypes.class);

    private static final Key<Set<TypeModification>> MODIFICATION_KEY = Key.get(new com.google.inject.TypeLiteral<Set<TypeModification>>() {}, GraphModifications.class);

    @Test
    public void schemaModuleShouldProvideEmpty() {
        Injector injector = Guice.createInjector(new SchemaModule() {});
        assertThat(injector.getInstance(SchemaModuleTest.QUERY_KEY)).isNotNull();
        assertThat(injector.getInstance(SchemaModuleTest.MUTATION_KEY)).isNotNull();
        assertThat(injector.getInstance(SchemaModuleTest.EXTRA_TYPE_KEY)).isNotNull();
        assertThat(injector.getInstance(SchemaModuleTest.MODIFICATION_KEY)).isNotNull();
    }

    @Test
    public void schemaModuleShouldProvideQueryFields() {
        Injector injector = Guice.createInjector(new SchemaModule() {
            @Query
            GraphQLFieldDefinition greeting = GraphQLFieldDefinition.newFieldDefinition().name("greeting").type(GraphQLString).staticValue("hello world").build();
        });
        assertThat(injector.getInstance(SchemaModuleTest.QUERY_KEY)).hasSize(1);
        assertThat(injector.getInstance(SchemaModuleTest.MUTATION_KEY)).isEmpty();
        assertThat(injector.getInstance(SchemaModuleTest.EXTRA_TYPE_KEY)).isEmpty();
        assertThat(injector.getInstance(SchemaModuleTest.MODIFICATION_KEY)).isEmpty();
    }

    @Test
    public void schemaModuleShouldProvideQueryAndMutationFields() {
        Injector injector = Guice.createInjector(new SchemaModule() {
            @Query
            GraphQLFieldDefinition greeting = GraphQLFieldDefinition.newFieldDefinition().name("queryField").type(GraphQLString).staticValue("hello world").build();

            @Mutation
            GraphQLFieldDefinition mutationField = GraphQLFieldDefinition.newFieldDefinition().name("mutationField").type(GraphQLString).staticValue("hello world").build();

            @Query("queryMethod")
            GreetingsResponse queryMethod(GreetingsRequest request) {
                return GreetingsResponse.newBuilder().setId(request.getId()).build();
            }

            @Mutation("mutationMethod")
            ListenableFuture<GreetingsResponse> mutationMethod(GreetingsRequest request) {
                return Futures.immediateFuture(GreetingsResponse.newBuilder().setId(request.getId()).build());
            }
        });
        assertThat(injector.getInstance(SchemaModuleTest.QUERY_KEY)).hasSize(2);
        assertThat(injector.getInstance(SchemaModuleTest.MUTATION_KEY)).hasSize(2);
        assertThat(injector.getInstance(SchemaModuleTest.EXTRA_TYPE_KEY)).hasSize(1);
        assertThat(injector.getInstance(SchemaModuleTest.MODIFICATION_KEY)).isEmpty();
    }

    @Test
    public void schemaModuleShouldNamespaceQueriesAndMutations() {
        @Namespace("namespace")
        class NamespacedSchemaModule extends SchemaModule {
            @Query
            GraphQLFieldDefinition greeting = GraphQLFieldDefinition.newFieldDefinition().name("queryField").type(GraphQLString).staticValue("hello world").build();

            @Mutation
            GraphQLFieldDefinition mutationField = GraphQLFieldDefinition.newFieldDefinition().name("mutationField").type(GraphQLString).staticValue("hello world").build();

            @Query("queryMethod")
            GreetingsResponse queryMethod(GreetingsRequest request) {
                return GreetingsResponse.newBuilder().setId(request.getId()).build();
            }

            @Mutation("mutationMethod")
            ListenableFuture<GreetingsResponse> mutationMethod(GreetingsRequest request) {
                return Futures.immediateFuture(GreetingsResponse.newBuilder().setId(request.getId()).build());
            }
        }
        Injector injector = Guice.createInjector(new NamespacedSchemaModule());
        assertThat(injector.getInstance(SchemaModuleTest.QUERY_KEY)).hasSize(1);
        assertThat(injector.getInstance(SchemaModuleTest.MUTATION_KEY)).hasSize(1);
        assertThat(injector.getInstance(SchemaModuleTest.EXTRA_TYPE_KEY)).hasSize(1);
        assertThat(injector.getInstance(SchemaModuleTest.MODIFICATION_KEY)).isEmpty();
    }

    @Test
    public void schemaModuleShouldApplyArgs() {
        Injector injector = Guice.createInjector(new SchemaModule() {
            @Mutation("mutationMethodWithArgs")
            ListenableFuture<GreetingsResponse> mutationMethod(GreetingsRequest request, @Arg("showDeleted")
            Boolean showDeleted) {
                return Futures.immediateFuture(GreetingsResponse.newBuilder().setId(request.getId()).build());
            }
        });
        assertThat(injector.getInstance(SchemaModuleTest.MUTATION_KEY)).hasSize(1);
        List<GraphQLArgument> arguments = injector.getInstance(SchemaModuleTest.MUTATION_KEY).iterator().next().getArguments();
        assertThat(arguments).hasSize(2);
        assertThat(arguments.stream().map(( argument) -> argument.getName()).collect(ImmutableList.toImmutableList(ImmutableList))).containsExactly("input", "showDeleted");
    }

    @Test
    public void schemaShouldValidateWhenIncludingDataFetchingEnvironment() throws Exception {
        Injector injector = Guice.createInjector(new SchemaModule() {
            @Query("hello")
            ListenableFuture<GreetingsResponse> querySnippets(GreetingsRequest request, DataFetchingEnvironment environment) {
                return Futures.immediateFuture(GreetingsResponse.newBuilder().setId(request.getId()).build());
            }
        });
        validateSchema(injector);
    }

    @Test
    public void schemaShouldValidateWhenProvidingJustRequest() throws Exception {
        Injector injector = Guice.createInjector(new SchemaModule() {
            @Query("hello")
            ListenableFuture<GreetingsResponse> querySnippets(GreetingsRequest request) {
                return Futures.immediateFuture(GreetingsResponse.newBuilder().setId(request.getId()).build());
            }
        });
        validateSchema(injector);
    }

    @Test
    public void schemaShouldValidateWhenInjectingParameterUsingGuice() throws Exception {
        Injector injector = Guice.createInjector(new SchemaModule() {
            @Query("hello")
            ListenableFuture<GreetingsResponse> querySnippets(GreetingsRequest request) {
                return Futures.immediateFuture(GreetingsResponse.newBuilder().setId(request.getId()).build());
            }
        });
        validateSchema(injector);
    }

    @Test(expected = CreationException.class)
    public void schemaModuleShouldFailIfWrongTypeIsAnnotated() {
        Guice.createInjector(new SchemaModule() {
            @Query
            String greeting = "hi";
        });
    }
}

