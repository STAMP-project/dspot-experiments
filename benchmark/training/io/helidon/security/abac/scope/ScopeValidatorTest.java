/**
 * Copyright (c) 2018, 2019 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.security.abac.scope;


import EndpointConfig.AnnotationScope.METHOD;
import Errors.Collector;
import ScopeValidator.Scope;
import ScopeValidator.Scopes;
import ScopeValidator.ScopesConfig;
import io.helidon.common.CollectionsHelper;
import io.helidon.common.Errors;
import io.helidon.security.EndpointConfig;
import io.helidon.security.Grant;
import io.helidon.security.Principal;
import io.helidon.security.ProviderRequest;
import io.helidon.security.Subject;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Unit test for {@link ScopeValidator}.
 */
public class ScopeValidatorTest {
    @Test
    public void testScopesAndPermit() {
        ScopeValidator validator = ScopeValidator.create();
        ScopeValidator.Scope annot = Mockito.mock(Scope.class);
        Mockito.when(annot.value()).thenReturn("calendar_get");
        ScopeValidator.Scope annotTwo = Mockito.mock(Scope.class);
        Mockito.when(annotTwo.value()).thenReturn("calendar_update");
        ScopeValidator.Scopes scopes = Mockito.mock(Scopes.class);
        Mockito.when(scopes.value()).thenReturn(new ScopeValidator.Scope[]{ annot, annotTwo });
        EndpointConfig ep = Mockito.mock(EndpointConfig.class);
        Mockito.when(ep.combineAnnotations(Scopes.class, METHOD)).thenReturn(CollectionsHelper.listOf(scopes));
        ScopeValidator.ScopesConfig sConfig = validator.fromAnnotations(ep);
        Errors.Collector collector = Errors.collector();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.subject()).thenReturn(Optional.of(Subject.builder().principal(Principal.create("myAdmin")).addGrant(Grant.builder().type("scope").name("calendar_get").build()).addGrant(Grant.builder().type("scope").name("calendar_update").build()).build()));
        Mockito.when(request.service()).thenReturn(Optional.empty());
        validator.validate(sConfig, collector, request);
        collector.collect().checkValid();
    }

    @Test
    public void testScopesAndDeny() {
        ScopeValidator validator = ScopeValidator.create();
        ScopeValidator.Scope annot = Mockito.mock(Scope.class);
        Mockito.when(annot.value()).thenReturn("calendar_get");
        ScopeValidator.Scope annotTwo = Mockito.mock(Scope.class);
        Mockito.when(annotTwo.value()).thenReturn("calendar_update");
        ScopeValidator.Scopes scopes = Mockito.mock(Scopes.class);
        Mockito.when(scopes.value()).thenReturn(new ScopeValidator.Scope[]{ annot, annotTwo });
        EndpointConfig ep = Mockito.mock(EndpointConfig.class);
        Mockito.when(ep.combineAnnotations(Scopes.class, METHOD)).thenReturn(CollectionsHelper.listOf(scopes));
        ScopeValidator.ScopesConfig sConfig = validator.fromAnnotations(ep);
        Errors.Collector collector = Errors.collector();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.subject()).thenReturn(Optional.of(Subject.builder().principal(Principal.create("myAdmin")).addGrant(Grant.builder().type("scope").name("calendar_get").build()).build()));
        Mockito.when(request.service()).thenReturn(Optional.empty());
        validator.validate(sConfig, collector, request);
        if (collector.collect().isValid()) {
            Assertions.fail("User does not have calendar_update scope, so this should have failed");
        }
    }

    @Test
    public void testScopesOrPermit() {
        ScopeValidator validator = ScopeValidator.builder().useOrOperator(true).build();
        ScopeValidator.Scope annot = Mockito.mock(Scope.class);
        Mockito.when(annot.value()).thenReturn("calendar_get");
        ScopeValidator.Scope annotTwo = Mockito.mock(Scope.class);
        Mockito.when(annotTwo.value()).thenReturn("calendar_update");
        ScopeValidator.Scopes scopes = Mockito.mock(Scopes.class);
        Mockito.when(scopes.value()).thenReturn(new ScopeValidator.Scope[]{ annot, annotTwo });
        EndpointConfig ep = Mockito.mock(EndpointConfig.class);
        Mockito.when(ep.combineAnnotations(Scopes.class, METHOD)).thenReturn(CollectionsHelper.listOf(scopes));
        ScopeValidator.ScopesConfig sConfig = validator.fromAnnotations(ep);
        Errors.Collector collector = Errors.collector();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.subject()).thenReturn(Optional.of(Subject.builder().principal(Principal.create("myAdmin")).addGrant(Grant.builder().type("scope").name("calendar_get").build()).build()));
        Mockito.when(request.service()).thenReturn(Optional.empty());
        validator.validate(sConfig, collector, request);
        collector.collect().checkValid();
    }

    @Test
    public void testScopesOrDeny() {
        ScopeValidator validator = ScopeValidator.builder().useOrOperator(true).build();
        ScopeValidator.Scope annot = Mockito.mock(Scope.class);
        Mockito.when(annot.value()).thenReturn("calendar_get");
        ScopeValidator.Scope annotTwo = Mockito.mock(Scope.class);
        Mockito.when(annotTwo.value()).thenReturn("calendar_update");
        ScopeValidator.Scopes scopes = Mockito.mock(Scopes.class);
        Mockito.when(scopes.value()).thenReturn(new ScopeValidator.Scope[]{ annot, annotTwo });
        EndpointConfig ep = Mockito.mock(EndpointConfig.class);
        Mockito.when(ep.combineAnnotations(Scopes.class, METHOD)).thenReturn(CollectionsHelper.listOf(scopes));
        ScopeValidator.ScopesConfig sConfig = validator.fromAnnotations(ep);
        Errors.Collector collector = Errors.collector();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.subject()).thenReturn(Optional.of(Subject.builder().principal(Principal.create("myAdmin")).addGrant(Grant.builder().type("scope").name("calendar_other").build()).build()));
        Mockito.when(request.service()).thenReturn(Optional.empty());
        validator.validate(sConfig, collector, request);
        if (collector.collect().isValid()) {
            Assertions.fail("User does not have any of the required scopes, should have failed");
        }
    }
}

