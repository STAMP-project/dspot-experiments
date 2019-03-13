/**
 * Copyright 2014-2019 the original author or authors.
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


import StoredProcedureAttributes.SYNTHETIC_OUTPUT_PARAMETER_NAME;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.annotation.AliasFor;
import org.springframework.data.jpa.domain.sample.User;
import org.springframework.data.repository.query.Param;


/**
 * Unit tests for {@link StoredProcedureAttributeSource}.
 *
 * @author Thomas Darimont
 * @author Oliver Gierke
 * @author Christoph Strobl
 * @author Diego Diez
 * @since 1.6
 */
@RunWith(MockitoJUnitRunner.class)
public class StoredProcedureAttributeSourceUnitTests {
    StoredProcedureAttributeSource creator;

    @Mock
    JpaEntityMetadata<User> entityMetadata;

    // DATAJPA-455
    @Test
    public void shouldCreateStoredProcedureAttributesFromProcedureMethodWithImplicitProcedureName() {
        StoredProcedureAttributes attr = creator.createFrom(StoredProcedureAttributeSourceUnitTests.method("plus1inout", Integer.class), entityMetadata);
        Assert.assertThat(attr.getProcedureName(), CoreMatchers.is("plus1inout"));
        Assert.assertThat(attr.getOutputParameterType(), CoreMatchers.is(typeCompatibleWith(Integer.class)));
        Assert.assertThat(attr.getOutputParameterName(), CoreMatchers.is(SYNTHETIC_OUTPUT_PARAMETER_NAME));
    }

    // DATAJPA-455
    @Test
    public void shouldCreateStoredProcedureAttributesFromProcedureMethodWithExplictName() {
        StoredProcedureAttributes attr = creator.createFrom(StoredProcedureAttributeSourceUnitTests.method("explicitlyNamedPlus1inout", Integer.class), entityMetadata);
        Assert.assertThat(attr.getProcedureName(), CoreMatchers.is("plus1inout"));
        Assert.assertThat(attr.getOutputParameterType(), CoreMatchers.is(typeCompatibleWith(Integer.class)));
        Assert.assertThat(attr.getOutputParameterName(), CoreMatchers.is(SYNTHETIC_OUTPUT_PARAMETER_NAME));
    }

    // DATAJPA-455
    @Test
    public void shouldCreateStoredProcedureAttributesFromProcedureMethodWithExplictProcedureNameValue() {
        StoredProcedureAttributes attr = creator.createFrom(StoredProcedureAttributeSourceUnitTests.method("explicitlyNamedPlus1inout", Integer.class), entityMetadata);
        Assert.assertThat(attr.getProcedureName(), CoreMatchers.is("plus1inout"));
        Assert.assertThat(attr.getOutputParameterType(), CoreMatchers.is(typeCompatibleWith(Integer.class)));
        Assert.assertThat(attr.getOutputParameterName(), CoreMatchers.is(SYNTHETIC_OUTPUT_PARAMETER_NAME));
    }

    // DATAJPA-455
    @Test
    public void shouldCreateStoredProcedureAttributesFromProcedureMethodWithExplictProcedureNameAlias() {
        StoredProcedureAttributes attr = creator.createFrom(StoredProcedureAttributeSourceUnitTests.method("explicitPlus1inoutViaProcedureNameAlias", Integer.class), entityMetadata);
        Assert.assertThat(attr.getProcedureName(), CoreMatchers.is("plus1inout"));
        Assert.assertThat(attr.getOutputParameterType(), CoreMatchers.is(typeCompatibleWith(Integer.class)));
        Assert.assertThat(attr.getOutputParameterName(), CoreMatchers.is(SYNTHETIC_OUTPUT_PARAMETER_NAME));
    }

    // DATAJPA-1297
    @Test
    public void shouldCreateStoredProcedureAttributesFromProcedureMethodWithExplictProcedureNameAliasAndOutputParameterName() {
        StoredProcedureAttributes attr = creator.createFrom(StoredProcedureAttributeSourceUnitTests.method("explicitPlus1inoutViaProcedureNameAliasAndOutputParameterName", Integer.class), entityMetadata);
        Assert.assertThat(attr.getProcedureName(), CoreMatchers.is("plus1inout"));
        Assert.assertThat(attr.getOutputParameterType(), CoreMatchers.is(typeCompatibleWith(Integer.class)));
        Assert.assertThat(attr.getOutputParameterName(), CoreMatchers.is("res"));
    }

    // DATAJPA-455
    @Test
    public void shouldCreateStoredProcedureAttributesFromProcedureMethodBackedWithExplicitlyNamedProcedure() {
        StoredProcedureAttributes attr = creator.createFrom(StoredProcedureAttributeSourceUnitTests.method("entityAnnotatedCustomNamedProcedurePlus1IO", Integer.class), entityMetadata);
        Assert.assertThat(attr.getProcedureName(), CoreMatchers.is("User.plus1IO"));
        Assert.assertThat(attr.getOutputParameterType(), CoreMatchers.is(typeCompatibleWith(Integer.class)));
        Assert.assertThat(attr.getOutputParameterName(), CoreMatchers.is("res"));
    }

    // DATAJPA-455
    @Test
    public void shouldCreateStoredProcedureAttributesFromProcedureMethodBackedWithImplicitlyNamedProcedure() {
        StoredProcedureAttributes attr = creator.createFrom(StoredProcedureAttributeSourceUnitTests.method("plus1", Integer.class), entityMetadata);
        Assert.assertThat(attr.getProcedureName(), CoreMatchers.is("User.plus1"));
        Assert.assertThat(attr.getOutputParameterType(), CoreMatchers.is(typeCompatibleWith(Integer.class)));
        Assert.assertThat(attr.getOutputParameterName(), CoreMatchers.is("res"));
    }

    // DATAJPA-871
    @Test
    public void aliasedStoredProcedure() {
        StoredProcedureAttributes attr = creator.createFrom(StoredProcedureAttributeSourceUnitTests.method("plus1inoutWithComposedAnnotationOverridingProcedureName", Integer.class), entityMetadata);
        Assert.assertThat(attr.getProcedureName(), CoreMatchers.is(CoreMatchers.equalTo("plus1inout")));
        Assert.assertThat(attr.getOutputParameterType(), CoreMatchers.is(typeCompatibleWith(Integer.class)));
        Assert.assertThat(attr.getOutputParameterName(), CoreMatchers.is(SYNTHETIC_OUTPUT_PARAMETER_NAME));
    }

    // DATAJPA-871
    @Test
    public void aliasedStoredProcedure2() {
        StoredProcedureAttributes attr = creator.createFrom(StoredProcedureAttributeSourceUnitTests.method("plus1inoutWithComposedAnnotationOverridingName", Integer.class), entityMetadata);
        Assert.assertThat(attr.getProcedureName(), CoreMatchers.is(CoreMatchers.equalTo("User.plus1")));
        Assert.assertThat(attr.getOutputParameterType(), CoreMatchers.is(typeCompatibleWith(Integer.class)));
        Assert.assertThat(attr.getOutputParameterName(), CoreMatchers.is(CoreMatchers.equalTo("res")));
    }

    /**
     *
     *
     * @author Thomas Darimont
     */
    static interface DummyRepository {
        /**
         * Explicitly mapped to a procedure with name "plus1inout" in database.
         */
        // DATAJPA-455
        @Procedure("plus1inout")
        Integer explicitlyNamedPlus1inout(Integer arg);

        /**
         * Explicitly mapped to a procedure with name "plus1inout" in database via alias.
         */
        // DATAJPA-455
        @Procedure(procedureName = "plus1inout")
        Integer explicitPlus1inoutViaProcedureNameAlias(Integer arg);

        /**
         * Explicitly mapped to a procedure with name "plus1inout" in database via alias and explicityly named ouput parameter.
         */
        // DATAJPA-1297
        @Procedure(procedureName = "plus1inout", outputParameterName = "res")
        Integer explicitPlus1inoutViaProcedureNameAliasAndOutputParameterName(Integer arg);

        /**
         * Implicitly mapped to a procedure with name "plus1inout" in database via alias.
         */
        // DATAJPA-455
        @Procedure
        Integer plus1inout(Integer arg);

        /**
         * Explicitly mapped to named stored procedure "User.plus1IO" in {@link EntityManager}.
         */
        // DATAJPA-455
        @Procedure(name = "User.plus1IO")
        Integer entityAnnotatedCustomNamedProcedurePlus1IO(@Param("arg")
        Integer arg);

        /**
         * Implicitly mapped to named stored procedure "User.plus1" in {@link EntityManager}.
         */
        // DATAJPA-455
        @Procedure
        Integer plus1(@Param("arg")
        Integer arg);

        @StoredProcedureAttributeSourceUnitTests.ComposedProcedureUsingAliasFor(explicitProcedureName = "plus1inout")
        Integer plus1inoutWithComposedAnnotationOverridingProcedureName(Integer arg);

        @StoredProcedureAttributeSourceUnitTests.ComposedProcedureUsingAliasFor(emProcedureName = "User.plus1")
        Integer plus1inoutWithComposedAnnotationOverridingName(Integer arg);
    }

    @Procedure
    @Retention(RetentionPolicy.RUNTIME)
    static @interface ComposedProcedureUsingAliasFor {
        @AliasFor(annotation = Procedure.class, attribute = "value")
        String dbProcedureName() default "";

        @AliasFor(annotation = Procedure.class, attribute = "procedureName")
        String explicitProcedureName() default "";

        @AliasFor(annotation = Procedure.class, attribute = "name")
        String emProcedureName() default "";

        @AliasFor(annotation = Procedure.class, attribute = "outputParameterName")
        String outParamName() default "";
    }
}

