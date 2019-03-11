/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.data.jpa.repository.support;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.persistence.Entity;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.annotation.AliasFor;
import org.springframework.data.jpa.repository.query.DefaultJpaEntityMetadata;


/**
 * Unit tests for {@link DefaultJpaEntityMetadata}.
 *
 * @author Oliver Gierke
 * @author Christoph Strobl
 */
public class DefaultJpaEntityMetadataUnitTest {
    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void rejectsNullDomainType() {
        new DefaultJpaEntityMetadata(null);
    }

    @Test
    public void returnsConfiguredType() {
        DefaultJpaEntityMetadata<DefaultJpaEntityMetadataUnitTest.Foo> metadata = new DefaultJpaEntityMetadata<DefaultJpaEntityMetadataUnitTest.Foo>(DefaultJpaEntityMetadataUnitTest.Foo.class);
        Assert.assertThat(metadata.getJavaType(), CoreMatchers.is(CoreMatchers.equalTo(DefaultJpaEntityMetadataUnitTest.Foo.class)));
    }

    @Test
    public void returnsSimpleClassNameAsEntityNameByDefault() {
        DefaultJpaEntityMetadata<DefaultJpaEntityMetadataUnitTest.Foo> metadata = new DefaultJpaEntityMetadata<DefaultJpaEntityMetadataUnitTest.Foo>(DefaultJpaEntityMetadataUnitTest.Foo.class);
        Assert.assertThat(metadata.getEntityName(), CoreMatchers.is(DefaultJpaEntityMetadataUnitTest.Foo.class.getSimpleName()));
    }

    @Test
    public void returnsCustomizedEntityNameIfConfigured() {
        DefaultJpaEntityMetadata<DefaultJpaEntityMetadataUnitTest.Bar> metadata = new DefaultJpaEntityMetadata<DefaultJpaEntityMetadataUnitTest.Bar>(DefaultJpaEntityMetadataUnitTest.Bar.class);
        Assert.assertThat(metadata.getEntityName(), CoreMatchers.is("Entity"));
    }

    // DATAJPA-871
    @Test
    public void returnsCustomizedEntityNameIfConfiguredViaComposedAnnotation() {
        DefaultJpaEntityMetadata<DefaultJpaEntityMetadataUnitTest.BarWithComposedAnnotation> metadata = new DefaultJpaEntityMetadata<DefaultJpaEntityMetadataUnitTest.BarWithComposedAnnotation>(DefaultJpaEntityMetadataUnitTest.BarWithComposedAnnotation.class);
        Assert.assertThat(metadata.getEntityName(), CoreMatchers.is("Entity"));
    }

    static class Foo {}

    @Entity(name = "Entity")
    static class Bar {}

    @DefaultJpaEntityMetadataUnitTest.CustomEntityAnnotationUsingAliasFor(entityName = "Entity")
    static class BarWithComposedAnnotation {}

    @Entity
    @Retention(RetentionPolicy.RUNTIME)
    static @interface CustomEntityAnnotationUsingAliasFor {
        @AliasFor(annotation = Entity.class, attribute = "name")
        String entityName();
    }
}

