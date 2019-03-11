/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.security.examples.spi;


import AtnProviderSync.AtnAnnot;
import AtnProviderSync.AtnObject;
import EndpointConfig.AnnotationScope.CLASS;
import SecurityResponse.SecurityStatus.ABSTAIN;
import SecurityResponse.SecurityStatus.FAILURE;
import io.helidon.common.CollectionsHelper;
import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import io.helidon.security.AuthenticationResponse;
import io.helidon.security.EndpointConfig;
import io.helidon.security.ProviderRequest;
import io.helidon.security.Security;
import io.helidon.security.SecurityContext;
import io.helidon.security.SecurityEnvironment;
import java.lang.annotation.Annotation;
import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Unit test for {@link AtnProviderSync}.
 */
public class AtnProviderSyncTest {
    private static final String VALUE = "aValue";

    private static final int SIZE = 16;

    @Test
    public void testAbstain() {
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.user()).thenReturn(Optional.empty());
        Mockito.when(context.service()).thenReturn(Optional.empty());
        SecurityEnvironment se = SecurityEnvironment.create();
        EndpointConfig ep = EndpointConfig.create();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.securityContext()).thenReturn(context);
        Mockito.when(request.env()).thenReturn(se);
        Mockito.when(request.endpointConfig()).thenReturn(ep);
        AtnProviderSync provider = new AtnProviderSync();
        AuthenticationResponse response = provider.syncAuthenticate(request);
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(ABSTAIN));
    }

    @Test
    public void testAnnotationSuccess() {
        AtnProviderSync.AtnAnnot annot = new AtnProviderSync.AtnAnnot() {
            @Override
            public String value() {
                return AtnProviderSyncTest.VALUE;
            }

            @Override
            public int size() {
                return AtnProviderSyncTest.SIZE;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return AtnAnnot.class;
            }
        };
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.user()).thenReturn(Optional.empty());
        Mockito.when(context.service()).thenReturn(Optional.empty());
        SecurityEnvironment se = SecurityEnvironment.create();
        EndpointConfig ep = EndpointConfig.builder().annotations(CLASS, CollectionsHelper.mapOf(AtnAnnot.class, CollectionsHelper.listOf(annot))).build();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.securityContext()).thenReturn(context);
        Mockito.when(request.env()).thenReturn(se);
        Mockito.when(request.endpointConfig()).thenReturn(ep);
        testSuccess(request);
    }

    @Test
    public void testCustomObjectSuccess() {
        AtnProviderSync.AtnObject obj = new AtnProviderSync.AtnObject();
        obj.setSize(AtnProviderSyncTest.SIZE);
        obj.setValue(AtnProviderSyncTest.VALUE);
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.user()).thenReturn(Optional.empty());
        Mockito.when(context.service()).thenReturn(Optional.empty());
        SecurityEnvironment se = SecurityEnvironment.create();
        EndpointConfig ep = EndpointConfig.builder().customObject(AtnObject.class, obj).build();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.securityContext()).thenReturn(context);
        Mockito.when(request.env()).thenReturn(se);
        Mockito.when(request.endpointConfig()).thenReturn(ep);
        testSuccess(request);
    }

    @Test
    public void testConfigSuccess() {
        Config config = Config.create(ConfigSources.create(CollectionsHelper.mapOf("value", AtnProviderSyncTest.VALUE, "size", String.valueOf(AtnProviderSyncTest.SIZE))));
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.user()).thenReturn(Optional.empty());
        Mockito.when(context.service()).thenReturn(Optional.empty());
        SecurityEnvironment se = SecurityEnvironment.create();
        EndpointConfig ep = EndpointConfig.builder().config("atn-object", config).build();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.securityContext()).thenReturn(context);
        Mockito.when(request.env()).thenReturn(se);
        Mockito.when(request.endpointConfig()).thenReturn(ep);
        testSuccess(request);
    }

    @Test
    public void testFailure() {
        Config config = Config.create(ConfigSources.create(CollectionsHelper.mapOf("atn-object.size", String.valueOf(AtnProviderSyncTest.SIZE))));
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.user()).thenReturn(Optional.empty());
        Mockito.when(context.service()).thenReturn(Optional.empty());
        SecurityEnvironment se = SecurityEnvironment.create();
        EndpointConfig ep = EndpointConfig.builder().config("atn-object", config).build();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.securityContext()).thenReturn(context);
        Mockito.when(request.env()).thenReturn(se);
        Mockito.when(request.endpointConfig()).thenReturn(ep);
        AtnProviderSync provider = new AtnProviderSync();
        AuthenticationResponse response = provider.syncAuthenticate(request);
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(FAILURE));
    }

    @Test
    public void integrationTest() {
        Security security = Security.builder().addProvider(new AtnProviderSync()).build();
        // this part is usually done by container integration component
        // in Jersey you have access to security context through annotations
        // in Web server you have access to security context through context
        SecurityContext context = security.createContext("unit-test");
        context.endpointConfig(EndpointConfig.builder().customObject(AtnObject.class, AtnObject.from(AtnProviderSyncTest.VALUE, AtnProviderSyncTest.SIZE)));
        AuthenticationResponse response = context.authenticate();
        validateResponse(response);
    }
}

