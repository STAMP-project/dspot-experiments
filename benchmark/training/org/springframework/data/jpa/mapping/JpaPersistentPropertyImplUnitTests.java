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
package org.springframework.data.jpa.mapping;


import java.util.Collections;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.annotation.AccessType.Type;
import org.springframework.data.annotation.Version;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;


/**
 * Unit tests for {@link JpaPersistentPropertyImpl}.
 *
 * @author Oliver Gierke
 * @author Greg Turnquist
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class JpaPersistentPropertyImplUnitTests {
    @Mock
    Metamodel model;

    JpaMetamodelMappingContext context;

    JpaPersistentEntity<?> entity;

    // DATAJPA-284
    @Test
    public void considersOneToOneMappedPropertyAnAssociation() {
        JpaPersistentProperty property = entity.getRequiredPersistentProperty("other");
        Assert.assertThat(property.isAssociation(), is(true));
    }

    // DATAJPA-376
    @Test
    public void considersJpaTransientFieldsAsTransient() {
        Assert.assertThat(entity.getPersistentProperty("transientProp"), is(nullValue()));
    }

    // DATAJPA-484
    @Test
    public void considersEmbeddableAnEntity() {
        Assert.assertThat(context.getPersistentEntity(JpaPersistentPropertyImplUnitTests.SampleEmbeddable.class), is(notNullValue()));
    }

    // DATAJPA-484
    @Test
    public void doesNotConsiderAnEmbeddablePropertyAnAssociation() {
        Assert.assertThat(entity.getRequiredPersistentProperty("embeddable").isAssociation(), is(false));
    }

    // DATAJPA-484
    @Test
    public void doesNotConsiderAnEmbeddedPropertyAnAssociation() {
        Assert.assertThat(entity.getRequiredPersistentProperty("embedded").isAssociation(), is(false));
    }

    // DATAJPA-619
    @Test
    public void considersPropertyLevelAccessTypeDefinitions() {
        Assert.assertThat(getProperty(JpaPersistentPropertyImplUnitTests.PropertyLevelPropertyAccess.class, "field").usePropertyAccess(), is(false));
        Assert.assertThat(getProperty(JpaPersistentPropertyImplUnitTests.PropertyLevelPropertyAccess.class, "property").usePropertyAccess(), is(true));
    }

    // DATAJPA-619
    @Test
    public void propertyLevelAccessTypeTrumpsTypeLevelDefinition() {
        Assert.assertThat(getProperty(JpaPersistentPropertyImplUnitTests.PropertyLevelDefinitionTrumpsTypeLevelOne.class, "field").usePropertyAccess(), is(false));
        Assert.assertThat(getProperty(JpaPersistentPropertyImplUnitTests.PropertyLevelDefinitionTrumpsTypeLevelOne.class, "property").usePropertyAccess(), is(true));
        Assert.assertThat(getProperty(JpaPersistentPropertyImplUnitTests.PropertyLevelDefinitionTrumpsTypeLevelOne2.class, "field").usePropertyAccess(), is(false));
        Assert.assertThat(getProperty(JpaPersistentPropertyImplUnitTests.PropertyLevelDefinitionTrumpsTypeLevelOne2.class, "property").usePropertyAccess(), is(true));
    }

    // DATAJPA-619
    @Test
    public void considersJpaAccessDefinitionAnnotations() {
        Assert.assertThat(getProperty(JpaPersistentPropertyImplUnitTests.TypeLevelPropertyAccess.class, "id").usePropertyAccess(), is(true));
    }

    // DATAJPA-619
    @Test
    public void springDataAnnotationTrumpsJpaIfBothOnTypeLevel() {
        Assert.assertThat(getProperty(JpaPersistentPropertyImplUnitTests.CompetingTypeLevelAnnotations.class, "id").usePropertyAccess(), is(false));
    }

    // DATAJPA-619
    @Test
    public void springDataAnnotationTrumpsJpaIfBothOnPropertyLevel() {
        Assert.assertThat(getProperty(JpaPersistentPropertyImplUnitTests.CompetingPropertyLevelAnnotations.class, "id").usePropertyAccess(), is(false));
    }

    // DATAJPA-605
    @Test
    public void detectsJpaVersionAnnotation() {
        Assert.assertThat(getProperty(JpaPersistentPropertyImplUnitTests.JpaVersioned.class, "version").isVersionProperty(), is(true));
    }

    // DATAJPA-664
    @Test
    @SuppressWarnings("rawtypes")
    public void considersTargetEntityTypeForPropertyType() {
        JpaPersistentProperty property = getProperty(JpaPersistentPropertyImplUnitTests.SpecializedAssociation.class, "api");
        Assert.assertThat(property.getType(), is(typeCompatibleWith(JpaPersistentPropertyImplUnitTests.Api.class)));
        Assert.assertThat(property.getActualType(), is(typeCompatibleWith(JpaPersistentPropertyImplUnitTests.Implementation.class)));
        Iterable<? extends TypeInformation<?>> entityType = property.getPersistentEntityTypes();
        Assert.assertThat(entityType.iterator().hasNext(), is(true));
        Assert.assertThat(entityType.iterator().next(), is(((TypeInformation) (ClassTypeInformation.from(JpaPersistentPropertyImplUnitTests.Implementation.class)))));
    }

    // DATAJPA-716
    @Test
    public void considersNonUpdateablePropertyNotWriteable() {
        Assert.assertThat(getProperty(JpaPersistentPropertyImplUnitTests.WithReadOnly.class, "name").isWritable(), is(false));
        Assert.assertThat(getProperty(JpaPersistentPropertyImplUnitTests.WithReadOnly.class, "updatable").isWritable(), is(true));
    }

    // DATAJPA-904
    @Test
    public void isEntityWorksEvenWithManagedTypeWithNullJavaType() {
        ManagedType<?> managedType = Mockito.mock(ManagedType.class);
        Mockito.doReturn(Collections.singleton(managedType)).when(model).getManagedTypes();
        Assert.assertThat(getProperty(JpaPersistentPropertyImplUnitTests.Sample.class, "other").isEntity(), is(false));
    }

    // DATAJPA-1064
    @Test
    public void simplePropertyIsNotConsideredAnAssociation() {
        JpaPersistentEntityImpl<?> entity = context.getRequiredPersistentEntity(JpaPersistentPropertyImplUnitTests.WithReadOnly.class);
        JpaPersistentProperty property = entity.getRequiredPersistentProperty("updatable");
        Assert.assertThat(property.isAssociation()).isFalse();
    }

    static class Sample {
        @OneToOne
        JpaPersistentPropertyImplUnitTests.Sample other;

        @Transient
        String transientProp;

        JpaPersistentPropertyImplUnitTests.SampleEmbeddable embeddable;

        @Embedded
        JpaPersistentPropertyImplUnitTests.SampleEmbedded embedded;
    }

    @Embeddable
    static class SampleEmbeddable {}

    static class SampleEmbedded {}

    @Access(AccessType.PROPERTY)
    static class TypeLevelPropertyAccess {
        private String id;

        public String getId() {
            return id;
        }
    }

    static class PropertyLevelPropertyAccess {
        String field;

        String property;

        /**
         *
         *
         * @return the property
         */
        @org.springframework.data.annotation.AccessType(Type.PROPERTY)
        public String getProperty() {
            return property;
        }
    }

    @Access(AccessType.FIELD)
    static class PropertyLevelDefinitionTrumpsTypeLevelOne {
        String field;

        String property;

        /**
         *
         *
         * @return the property
         */
        @org.springframework.data.annotation.AccessType(Type.PROPERTY)
        public String getProperty() {
            return property;
        }
    }

    @org.springframework.data.annotation.AccessType(Type.PROPERTY)
    static class PropertyLevelDefinitionTrumpsTypeLevelOne2 {
        @Access(AccessType.FIELD)
        String field;

        String property;

        /**
         *
         *
         * @return the property
         */
        public String getProperty() {
            return property;
        }
    }

    @org.springframework.data.annotation.AccessType(Type.FIELD)
    @Access(AccessType.PROPERTY)
    static class CompetingTypeLevelAnnotations {
        private String id;

        public String getId() {
            return id;
        }
    }

    @org.springframework.data.annotation.AccessType(Type.FIELD)
    @Access(AccessType.PROPERTY)
    static class CompetingPropertyLevelAnnotations {
        private String id;

        @org.springframework.data.annotation.AccessType(Type.FIELD)
        @Access(AccessType.PROPERTY)
        public String getId() {
            return id;
        }
    }

    static class SpringDataVersioned {
        @Version
        long version;
    }

    static class JpaVersioned {
        @javax.persistence.Version
        long version;
    }

    static class SpecializedAssociation {
        @ManyToOne(targetEntity = JpaPersistentPropertyImplUnitTests.Implementation.class)
        JpaPersistentPropertyImplUnitTests.Api api;
    }

    static interface Api {}

    static class Implementation {}

    static class WithReadOnly {
        @Column(updatable = false)
        String name;

        String updatable;
    }
}

