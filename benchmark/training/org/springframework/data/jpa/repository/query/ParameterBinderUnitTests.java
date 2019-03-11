/**
 * Copyright 2008-2019 the original author or authors.
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


import TemporalType.DATE;
import TemporalType.TIMESTAMP;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.persistence.Embeddable;
import javax.persistence.Parameter;
import javax.persistence.Query;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.query.Param;


/**
 * Unit test for {@link ParameterBinder}.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Jens Schauder
 */
@RunWith(MockitoJUnitRunner.class)
public class ParameterBinderUnitTests {
    public static final int MAX_PARAMETERS = 1;

    private Method valid;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Query query;

    private Method useIndexedParameters;

    static class User {}

    interface SampleRepository {
        ParameterBinderUnitTests.User useIndexedParameters(String lastname);

        ParameterBinderUnitTests.User valid(@Param("username")
        String username);

        ParameterBinderUnitTests.User validWithPageable(@Param("username")
        String username, Pageable pageable);

        ParameterBinderUnitTests.User validWithSort(@Param("username")
        String username, Sort sort);

        ParameterBinderUnitTests.User validWithDefaultTemporalTypeParameter(@Temporal
        Date registerDate);

        ParameterBinderUnitTests.User validWithCustomTemporalTypeParameter(@Temporal(TIMESTAMP)
        Date registerDate);

        ParameterBinderUnitTests.User invalidWithTemporalTypeParameter(@Temporal
        String registerDate);

        List<ParameterBinderUnitTests.User> validWithVarArgs(Integer... ids);

        ParameterBinderUnitTests.User optionalParameter(Optional<String> name);

        @Query("select x from User where name = :name")
        ParameterBinderUnitTests.User withQuery(String name, String other);
    }

    @Test
    public void bindWorksWithNullForSort() throws Exception {
        Method validWithSort = ParameterBinderUnitTests.SampleRepository.class.getMethod("validWithSort", String.class, Sort.class);
        Object[] values = new Object[]{ "foo", null };
        ParameterBinderFactory.createBinder(new JpaParameters(validWithSort)).bind(query, values);
        Mockito.verify(query).setParameter(ArgumentMatchers.eq(1), ArgumentMatchers.eq("foo"));
    }

    @Test
    public void bindWorksWithNullForPageable() throws Exception {
        Method validWithPageable = ParameterBinderUnitTests.SampleRepository.class.getMethod("validWithPageable", String.class, Pageable.class);
        Object[] values = new Object[]{ "foo", null };
        ParameterBinderFactory.createBinder(new JpaParameters(validWithPageable)).bind(query, values);
        Mockito.verify(query).setParameter(ArgumentMatchers.eq(1), ArgumentMatchers.eq("foo"));
    }

    @Test
    public void usesIndexedParametersIfNoParamAnnotationPresent() throws Exception {
        Object[] values = new Object[]{ "foo" };
        ParameterBinderFactory.createBinder(new JpaParameters(useIndexedParameters)).bind(query, values);
        Mockito.verify(query).setParameter(ArgumentMatchers.eq(1), ArgumentMatchers.any());
    }

    @Test
    public void usesParameterNameIfAnnotated() throws Exception {
        Mockito.when(query.setParameter(ArgumentMatchers.eq("username"), ArgumentMatchers.any())).thenReturn(query);
        Parameter parameter = Mockito.mock(Parameter.class);
        Mockito.when(parameter.getName()).thenReturn("username");
        Mockito.when(query.getParameters()).thenReturn(Collections.singleton(parameter));
        Object[] values = new Object[]{ "foo" };
        ParameterBinderFactory.createBinder(new JpaParameters(valid)).bind(query, values);
        Mockito.verify(query).setParameter(ArgumentMatchers.eq("username"), ArgumentMatchers.any());
    }

    @Test
    public void bindsEmbeddableCorrectly() throws Exception {
        Method method = getClass().getMethod("findByEmbeddable", ParameterBinderUnitTests.SampleEmbeddable.class);
        JpaParameters parameters = new JpaParameters(method);
        ParameterBinderUnitTests.SampleEmbeddable embeddable = new ParameterBinderUnitTests.SampleEmbeddable();
        Object[] values = new Object[]{ embeddable };
        ParameterBinderFactory.createBinder(parameters).bind(query, values);
        Mockito.verify(query).setParameter(1, embeddable);
    }

    // DATAJPA-107
    @Test
    public void shouldSetTemporalQueryParameterToDate() throws Exception {
        Method method = ParameterBinderUnitTests.SampleRepository.class.getMethod("validWithDefaultTemporalTypeParameter", Date.class);
        JpaParameters parameters = new JpaParameters(method);
        Date date = new Date();
        Object[] values = new Object[]{ date };
        ParameterBinderFactory.createBinder(parameters).bind(query, values);
        Mockito.verify(query).setParameter(ArgumentMatchers.eq(1), ArgumentMatchers.eq(date), Collections.eq(DATE));
    }

    // DATAJPA-107
    @Test
    public void shouldSetTemporalQueryParameterToTimestamp() throws Exception {
        Method method = ParameterBinderUnitTests.SampleRepository.class.getMethod("validWithCustomTemporalTypeParameter", Date.class);
        JpaParameters parameters = new JpaParameters(method);
        Date date = new Date();
        Object[] values = new Object[]{ date };
        ParameterBinderFactory.createBinder(parameters).bind(query, values);
        Mockito.verify(query).setParameter(ArgumentMatchers.eq(1), ArgumentMatchers.eq(date), Collections.eq(TIMESTAMP));
    }

    // DATAJPA-107
    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfIsAnnotatedWithTemporalParamAndParameterTypeIsNotDate() throws Exception {
        Method method = ParameterBinderUnitTests.SampleRepository.class.getMethod("invalidWithTemporalTypeParameter", String.class);
        JpaParameters parameters = new JpaParameters(method);
        ParameterBinderFactory.createBinder(parameters);
    }

    // DATAJPA-461
    @Test
    public void shouldAllowBindingOfVarArgsAsIs() throws Exception {
        Method method = ParameterBinderUnitTests.SampleRepository.class.getMethod("validWithVarArgs", Integer[].class);
        JpaParameters parameters = new JpaParameters(method);
        Integer[] ids = new Integer[]{ 1, 2, 3 };
        Object[] values = new Object[]{ ids };
        ParameterBinderFactory.createBinder(parameters).bind(query, values);
        Mockito.verify(query).setParameter(ArgumentMatchers.eq(1), ArgumentMatchers.eq(ids));
    }

    // DATAJPA-809
    @Test
    public void unwrapsOptionalParameter() throws Exception {
        Method method = ParameterBinderUnitTests.SampleRepository.class.getMethod("optionalParameter", Optional.class);
        JpaParameters parameters = new JpaParameters(method);
        Object[] values = new Object[]{ Optional.of("Foo") };
        ParameterBinderFactory.createBinder(parameters).bind(query, values);
        Mockito.verify(query).setParameter(ArgumentMatchers.eq(1), ArgumentMatchers.eq("Foo"));
    }

    // DATAJPA-1172
    @Test
    public void doesNotBindExcessParameters() throws Exception {
        Method method = ParameterBinderUnitTests.SampleRepository.class.getMethod("withQuery", String.class, String.class);
        Object[] values = new Object[]{ "foo", "superfluous" };
        ParameterBinderFactory.createBinder(new JpaParameters(method)).bind(query, values);
        Mockito.verify(query).setParameter(ArgumentMatchers.eq(1), ArgumentMatchers.any());
        Mockito.verify(query, Mockito.never()).setParameter(ArgumentMatchers.eq(2), ArgumentMatchers.any());
    }

    @SuppressWarnings("unused")
    static class SampleEntity {
        private ParameterBinderUnitTests.SampleEmbeddable embeddable;
    }

    @Embeddable
    @SuppressWarnings("unused")
    public static class SampleEmbeddable {
        private String foo;

        private String bar;
    }
}

