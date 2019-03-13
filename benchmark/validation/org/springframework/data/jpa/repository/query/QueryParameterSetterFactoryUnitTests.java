/**
 * Copyright 2017-2019 the original author or authors.
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
package org.springframework.data.jpa.repository.query;


import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.jpa.repository.query.StringQuery.ParameterBinding;


/**
 * Unit tests for {@link QueryParameterSetterFactory}.
 *
 * @author Jens Schauder
 * @author Mark Paluch
 */
public class QueryParameterSetterFactoryUnitTests {
    JpaParameters parameters = Mockito.mock(JpaParameters.class, Mockito.RETURNS_DEEP_STUBS);

    ParameterBinding binding = Mockito.mock(ParameterBinding.class);

    QueryParameterSetterFactory setterFactory;

    // DATAJPA-1058
    @Test
    public void noExceptionWhenQueryDoesNotContainNamedParameters() {
        setterFactory.create(binding, DeclaredQuery.of("QueryStringWithOutNamedParameter"));
    }

    // DATAJPA-1058
    @Test
    public void exceptionWhenQueryContainNamedParametersAndMethodParametersAreNotNamed() {
        // 
        // 
        // 
        // 
        Assertions.assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> setterFactory.create(binding, DeclaredQuery.of("QueryStringWith :NamedParameter"))).withMessageContaining("Java 8").withMessageContaining("@Param").withMessageContaining("-parameters");
    }

    // DATAJPA-1281
    @Test
    public void exceptionWhenCriteriaQueryContainsInsufficientAmountOfParameters() {
        // no parameter present in the criteria query
        List<ParameterMetadataProvider.ParameterMetadata<?>> metadata = Collections.emptyList();
        QueryParameterSetterFactory setterFactory = QueryParameterSetterFactory.forCriteriaQuery(parameters, metadata);
        // one argument present in the method signature
        Mockito.when(binding.getRequiredPosition()).thenReturn(1);
        // 
        // 
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> setterFactory.create(binding, DeclaredQuery.of("QueryStringWith :NamedParameter"))).withMessage("At least 1 parameter(s) provided but only 0 parameter(s) present in query.");
    }

    // DATAJPA-1281
    @Test
    public void exceptionWhenBasicQueryContainsInsufficientAmountOfParameters() {
        // no parameter present in the criteria query
        QueryParameterSetterFactory setterFactory = QueryParameterSetterFactory.basic(parameters);
        // one argument present in the method signature
        Mockito.when(binding.getRequiredPosition()).thenReturn(1);
        // 
        // 
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> setterFactory.create(binding, DeclaredQuery.of("QueryStringWith ?1"))).withMessage("At least 1 parameter(s) provided but only 0 parameter(s) present in query.");
    }
}

