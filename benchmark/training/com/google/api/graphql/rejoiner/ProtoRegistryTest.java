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


import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import graphql.schema.GraphQLType;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link com.google.api.graphql.rejoiner.ProtoRegistry}.
 */
@RunWith(JUnit4.class)
public final class ProtoRegistryTest {
    @Test
    public void protoRegistryShouldIncludeAllProtoTypesFromFile() {
        Set<GraphQLType> graphQLTypes = ProtoRegistry.newBuilder().add(TestProto.getDescriptor()).build().listTypes();
        assertThat(FluentIterable.from(graphQLTypes).transform(ProtoRegistryTest.GET_NAME)).containsExactly("javatests_com_google_api_graphql_rejoiner_proto_Proto1", "javatests_com_google_api_graphql_rejoiner_proto_Proto2", "javatests_com_google_api_graphql_rejoiner_proto_Proto1_InnerProto", "javatests_com_google_api_graphql_rejoiner_proto_Proto2_TestEnum", "javatests_com_google_api_graphql_rejoiner_proto_Proto2_NestedProto", "javatests_com_google_api_graphql_rejoiner_proto_TestEnumWithComments", "Input_javatests_com_google_api_graphql_rejoiner_proto_Proto1", "Input_javatests_com_google_api_graphql_rejoiner_proto_Proto2", "Input_javatests_com_google_api_graphql_rejoiner_proto_Proto1_InnerProto", "Input_javatests_com_google_api_graphql_rejoiner_proto_Proto2_NestedProto");
    }

    private static final Function<GraphQLType, String> GET_NAME = ( type) -> type.getName();
}

