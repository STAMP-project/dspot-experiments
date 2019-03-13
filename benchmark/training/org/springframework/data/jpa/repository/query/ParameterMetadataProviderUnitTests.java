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
import javax.persistence.criteria.CriteriaBuilder;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.repository.query.Parameters;


/**
 * Unit tests for {@link ParameterMetadataProvider}.
 *
 * @author Jens Schauder
 */
public class ParameterMetadataProviderUnitTests {
    // DATAJPA-863
    @Test
    public void errorMessageMentionesParametersWhenParametersAreExhausted() {
        PersistenceProvider persistenceProvider = Mockito.mock(PersistenceProvider.class);
        CriteriaBuilder builder = Mockito.mock(CriteriaBuilder.class);
        Parameters<?, ?> parameters = Mockito.mock(Parameters.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(parameters.getBindableParameters().iterator()).thenReturn(Collections.emptyListIterator());
        ParameterMetadataProvider metadataProvider = new ParameterMetadataProvider(builder, parameters, persistenceProvider);
        // 
        // 
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> metadataProvider.next(mock(.class))).withMessageContaining("parameter");
    }
}

