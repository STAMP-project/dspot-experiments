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


import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.criteria.ParameterExpression;
import lombok.Value;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.data.jpa.repository.query.QueryParameterSetter.NamedOrIndexedQueryParameterSetter;


/**
 * Unit tests fir {@link NamedOrIndexedQueryParameterSetter}.
 *
 * @author Jens Schauder
 * @author Oliver Gierke
 */
public class NamedOrIndexedQueryParameterSetterUnitTests {
    static final String EXCEPTION_MESSAGE = "mock exception";

    Function<Object[], Object> firstValueExtractor = ( args) -> args[0];

    Object[] methodArguments = new Object[]{ new Date() };

    List<TemporalType.TemporalType> temporalTypes = Arrays.asList(null, TIME);

    List<Parameter<?>> parameters = // 
    // 
    // 
    // 
    Arrays.<Parameter<?>>asList(Mockito.mock(ParameterExpression.class), new NamedOrIndexedQueryParameterSetterUnitTests.ParameterImpl("name", null), new NamedOrIndexedQueryParameterSetterUnitTests.ParameterImpl(null, 1));

    SoftAssertions softly = new SoftAssertions();

    // DATAJPA-1233
    @Test
    public void strictErrorHandlingThrowsExceptionForAllVariationsOfParameters() {
        Query query = NamedOrIndexedQueryParameterSetterUnitTests.mockExceptionThrowingQueryWithNamedParameters();
        for (Parameter parameter : parameters) {
            for (TemporalType.TemporalType temporalType : temporalTypes) {
                NamedOrIndexedQueryParameterSetter setter = // 
                // 
                // 
                // 
                new NamedOrIndexedQueryParameterSetter(firstValueExtractor, parameter, temporalType);
                // 
                // 
                // 
                // 
                // 
                // 
                softly.assertThatThrownBy(() -> setter.setParameter(query, methodArguments, STRICT)).describedAs("p-type: %s, p-name: %s, p-position: %s, temporal: %s", parameter.getClass(), parameter.getName(), parameter.getPosition(), temporalType).hasMessage(NamedOrIndexedQueryParameterSetterUnitTests.EXCEPTION_MESSAGE);
            }
        }
        softly.assertAll();
    }

    // DATAJPA-1233
    @Test
    public void lenientErrorHandlingThrowsNoExceptionForAllVariationsOfParameters() {
        Query query = NamedOrIndexedQueryParameterSetterUnitTests.mockExceptionThrowingQueryWithNamedParameters();
        for (Parameter<?> parameter : parameters) {
            for (TemporalType.TemporalType temporalType : temporalTypes) {
                NamedOrIndexedQueryParameterSetter setter = // 
                // 
                // 
                // 
                new NamedOrIndexedQueryParameterSetter(firstValueExtractor, parameter, temporalType);
                // 
                // 
                // 
                // 
                // 
                // 
                softly.assertThatCode(() -> setter.setParameter(query, methodArguments, LENIENT)).describedAs("p-type: %s, p-name: %s, p-position: %s, temporal: %s", parameter.getClass(), parameter.getName(), parameter.getPosition(), temporalType).doesNotThrowAnyException();
            }
        }
        softly.assertAll();
    }

    /**
     * setParameter should be called in the lenient case even if the number of parameters seems to suggest that it fails,
     * since the index might not be continuous due to missing parts of count queries compared to the main query. This
     * happens when a parameter gets used in the ORDER BY clause which gets stripped of for the count query.
     */
    // DATAJPA-1233
    @Test
    public void lenientSetsParameterWhenSuccessIsUnsure() {
        Query query = Mockito.mock(Query.class);
        for (TemporalType.TemporalType temporalType : temporalTypes) {
            NamedOrIndexedQueryParameterSetter setter = // 
            // 
            // parameter position is beyond number of parametes in query (0)
            // 
            new NamedOrIndexedQueryParameterSetter(firstValueExtractor, new NamedOrIndexedQueryParameterSetterUnitTests.ParameterImpl(null, 11), temporalType);
            setter.setParameter(query, methodArguments, LENIENT);
            if (temporalType == null) {
                Mockito.verify(query).setParameter(ArgumentMatchers.eq(11), ArgumentMatchers.any(Date.class));
            } else {
                Mockito.verify(query).setParameter(ArgumentMatchers.eq(11), ArgumentMatchers.any(Date.class), ArgumentMatchers.eq(temporalType));
            }
        }
        softly.assertAll();
    }

    /**
     * This scenario happens when the only (name) parameter is part of an ORDER BY clause and gets stripped of for the
     * count query. Then the count query has no named parameter but the parameter provided has a {@literal null} position.
     */
    // DATAJPA-1233
    @Test
    public void parameterNotSetWhenSuccessImpossible() {
        Query query = Mockito.mock(Query.class);
        for (TemporalType.TemporalType temporalType : temporalTypes) {
            NamedOrIndexedQueryParameterSetter setter = // 
            // 
            // no position (and no name) makes a success of a setParameter impossible
            // 
            new NamedOrIndexedQueryParameterSetter(firstValueExtractor, new NamedOrIndexedQueryParameterSetterUnitTests.ParameterImpl(null, null), temporalType);
            setter.setParameter(query, methodArguments, LENIENT);
            if (temporalType == null) {
                Mockito.verify(query, Mockito.never()).setParameter(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Date.class));
            } else {
                Mockito.verify(query, Mockito.never()).setParameter(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Date.class), ArgumentMatchers.eq(temporalType));
            }
        }
        softly.assertAll();
    }

    @Value
    private static class ParameterImpl implements Parameter<Object> {
        String name;

        Integer position;

        @Override
        public Class<Object> getParameterType() {
            return Object.class;
        }
    }
}

