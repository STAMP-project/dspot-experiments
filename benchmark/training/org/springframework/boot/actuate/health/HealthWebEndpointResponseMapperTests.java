/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.actuate.health;


import HttpStatus.NOT_FOUND;
import HttpStatus.SERVICE_UNAVAILABLE;
import ShowDetails.ALWAYS;
import ShowDetails.NEVER;
import ShowDetails.WHEN_AUTHORIZED;
import Status.DOWN;
import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;


/**
 * Tests for {@link HealthWebEndpointResponseMapper}.
 *
 * @author Stephane Nicoll
 */
public class HealthWebEndpointResponseMapperTests {
    private final HealthStatusHttpMapper statusHttpMapper = new HealthStatusHttpMapper();

    private Set<String> authorizedRoles = Collections.singleton("ACTUATOR");

    @Test
    public void mapDetailsWithDisableDetailsDoesNotInvokeSupplier() {
        HealthWebEndpointResponseMapper mapper = createMapper(NEVER);
        Supplier<Health> supplier = mockSupplier();
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        WebEndpointResponse<Health> response = mapper.mapDetails(supplier, securityContext);
        assertThat(response.getStatus()).isEqualTo(NOT_FOUND.value());
        Mockito.verifyZeroInteractions(supplier);
        Mockito.verifyZeroInteractions(securityContext);
    }

    @Test
    public void mapDetailsWithUnauthorizedUserDoesNotInvokeSupplier() {
        HealthWebEndpointResponseMapper mapper = createMapper(WHEN_AUTHORIZED);
        Supplier<Health> supplier = mockSupplier();
        SecurityContext securityContext = mockSecurityContext("USER");
        WebEndpointResponse<Health> response = mapper.mapDetails(supplier, securityContext);
        assertThat(response.getStatus()).isEqualTo(NOT_FOUND.value());
        assertThat(response.getBody()).isNull();
        Mockito.verifyZeroInteractions(supplier);
        Mockito.verify(securityContext).isUserInRole("ACTUATOR");
    }

    @Test
    public void mapDetailsWithAuthorizedUserInvokeSupplier() {
        HealthWebEndpointResponseMapper mapper = createMapper(WHEN_AUTHORIZED);
        Supplier<Health> supplier = mockSupplier();
        BDDMockito.given(supplier.get()).willReturn(Health.down().build());
        SecurityContext securityContext = mockSecurityContext("ACTUATOR");
        WebEndpointResponse<Health> response = mapper.mapDetails(supplier, securityContext);
        assertThat(response.getStatus()).isEqualTo(SERVICE_UNAVAILABLE.value());
        assertThat(response.getBody().getStatus()).isEqualTo(DOWN);
        Mockito.verify(supplier).get();
        Mockito.verify(securityContext).isUserInRole("ACTUATOR");
    }

    @Test
    public void mapDetailsWithUnavailableHealth() {
        HealthWebEndpointResponseMapper mapper = createMapper(ALWAYS);
        Supplier<Health> supplier = mockSupplier();
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        WebEndpointResponse<Health> response = mapper.mapDetails(supplier, securityContext);
        assertThat(response.getStatus()).isEqualTo(NOT_FOUND.value());
        assertThat(response.getBody()).isNull();
        Mockito.verify(supplier).get();
        Mockito.verifyZeroInteractions(securityContext);
    }
}

