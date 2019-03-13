package com.pushtorefresh.storio3;


import com.pushtorefresh.storio3.internal.TypeMapping;
import com.pushtorefresh.storio3.internal.TypeMappingFinderImpl;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.junit.Test;
import org.mockito.Mockito;


public class TypeMappingFinderImplTest {
    interface InterfaceEntity {}

    interface DescendantInterface extends TypeMappingFinderImplTest.InterfaceEntity {}

    static class ClassEntity {}

    static class DescendantClass extends TypeMappingFinderImplTest.ClassEntity implements TypeMappingFinderImplTest.InterfaceEntity {}

    @Test
    public void shouldReturnNullIfDirectTypeMappingIsEmpty() {
        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(Collections.<Class<?>, TypeMapping<?>>emptyMap());
        assertThat(typeMappingFinder.findTypeMapping(TypeMappingFinderImplTest.ClassEntity.class)).isNull();
    }

    @Test
    public void shouldReturnNullIfDirectTypeMappingIsNull() {
        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(null);
        assertThat(typeMappingFinder.findTypeMapping(TypeMappingFinderImplTest.ClassEntity.class)).isNull();
    }

    @Test
    public void shouldReturnNullIfNoTypeMappingRegisteredForType() {
        class ClassWithoutTypeMapping {}
        // noinspection unchecked
        final TypeMapping<TypeMappingFinderImplTest.ClassEntity> typeMapping = Mockito.mock(TypeMapping.class);
        Map<Class<?>, TypeMapping<?>> directTypeMapping = Collections.<Class<?>, TypeMapping<?>>singletonMap(TypeMappingFinderImplTest.ClassEntity.class, typeMapping);
        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(directTypeMapping);
        assertThat(typeMappingFinder.findTypeMapping(TypeMappingFinderImplTest.ClassEntity.class)).isSameAs(typeMapping);
        assertThat(typeMappingFinder.findTypeMapping(ClassWithoutTypeMapping.class)).isNull();
    }

    @Test
    public void directTypeMappingShouldWork() {
        // noinspection unchecked
        final TypeMapping<TypeMappingFinderImplTest.ClassEntity> typeMapping = Mockito.mock(TypeMapping.class);
        Map<Class<?>, TypeMapping<?>> directTypeMapping = Collections.<Class<?>, TypeMapping<?>>singletonMap(TypeMappingFinderImplTest.ClassEntity.class, typeMapping);
        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(directTypeMapping);
        assertThat(typeMappingFinder.findTypeMapping(TypeMappingFinderImplTest.ClassEntity.class)).isSameAs(typeMapping);
    }

    @Test
    public void indirectTypeMappingShouldWork() {
        // noinspection unchecked
        final TypeMapping<TypeMappingFinderImplTest.ClassEntity> typeMapping = Mockito.mock(TypeMapping.class);
        Map<Class<?>, TypeMapping<?>> directTypeMapping = Collections.<Class<?>, TypeMapping<?>>singletonMap(TypeMappingFinderImplTest.ClassEntity.class, typeMapping);
        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(directTypeMapping);
        // Direct type mapping should still work
        assertThat(typeMappingFinder.findTypeMapping(TypeMappingFinderImplTest.ClassEntity.class)).isSameAs(typeMapping);
        // Indirect type mapping should give same type mapping as for parent class
        assertThat(typeMappingFinder.findTypeMapping(TypeMappingFinderImplTest.DescendantClass.class)).isSameAs(typeMapping);
    }

    @Test
    public void indirectTypeMappingShouldReturnFromCache() {
        // noinspection unchecked
        final TypeMapping<TypeMappingFinderImplTest.ClassEntity> typeMapping = Mockito.mock(TypeMapping.class);
        Map<Class<?>, TypeMapping<?>> directTypeMapping = Mockito.spy(new HashMap<Class<?>, TypeMapping<?>>(1));
        directTypeMapping.put(TypeMappingFinderImplTest.ClassEntity.class, typeMapping);
        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(directTypeMapping);
        // Indirect type mapping should give same type mapping as for parent class
        assertThat(typeMappingFinder.findTypeMapping(TypeMappingFinderImplTest.DescendantClass.class)).isSameAs(typeMapping);
        // The second call
        assertThat(typeMappingFinder.findTypeMapping(TypeMappingFinderImplTest.DescendantClass.class)).isSameAs(typeMapping);
        // Should call only once, the second return time from cache
        Mockito.verify(typeMappingFinder.directTypeMapping()).get(TypeMappingFinderImplTest.ClassEntity.class);
    }

    @Test
    public void typeMappingShouldWorkInCaseOfMoreConcreteTypeMapping() {
        // noinspection unchecked
        final TypeMapping<TypeMappingFinderImplTest.ClassEntity> typeMapping = Mockito.mock(TypeMapping.class);
        // noinspection unchecked
        final TypeMapping<TypeMappingFinderImplTest.DescendantClass> subclassTypeMapping = Mockito.mock(TypeMapping.class);
        Map<Class<?>, TypeMapping<?>> directTypeMapping = new HashMap<Class<?>, TypeMapping<?>>(2);
        directTypeMapping.put(TypeMappingFinderImplTest.ClassEntity.class, typeMapping);
        directTypeMapping.put(TypeMappingFinderImplTest.DescendantClass.class, subclassTypeMapping);
        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(directTypeMapping);
        // Parent class should have its own type mapping
        assertThat(typeMappingFinder.findTypeMapping(TypeMappingFinderImplTest.ClassEntity.class)).isSameAs(typeMapping);
        // Child class should have its own type mapping
        assertThat(typeMappingFinder.findTypeMapping(TypeMappingFinderImplTest.DescendantClass.class)).isSameAs(subclassTypeMapping);
    }

    @Test
    public void typeMappingShouldFindIndirectTypeMappingInCaseOfComplexInheritance() {
        // Good test case ? inheritance with AutoValue/AutoParcel
        class Entity {}
        class AutoValue_Entity extends Entity {}
        class ConcreteEntity extends Entity {}
        class AutoValue_ConcreteEntity extends ConcreteEntity {}
        // noinspection unchecked
        final TypeMapping<Entity> entityTypeMapping = Mockito.mock(TypeMapping.class);
        // noinspection unchecked
        final TypeMapping<ConcreteEntity> concreteEntityTypeMapping = Mockito.mock(TypeMapping.class);
        Map<Class<?>, TypeMapping<?>> directTypeMapping = new HashMap<Class<?>, TypeMapping<?>>(2);
        directTypeMapping.put(Entity.class, entityTypeMapping);
        directTypeMapping.put(ConcreteEntity.class, concreteEntityTypeMapping);
        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(directTypeMapping);
        // Direct type mapping for Entity should work
        assertThat(typeMappingFinder.findTypeMapping(Entity.class)).isSameAs(entityTypeMapping);
        // Direct type mapping for ConcreteEntity should work
        assertThat(typeMappingFinder.findTypeMapping(ConcreteEntity.class)).isSameAs(concreteEntityTypeMapping);
        // Indirect type mapping for AutoValue_Entity should get type mapping for Entity
        assertThat(typeMappingFinder.findTypeMapping(AutoValue_Entity.class)).isSameAs(entityTypeMapping);
        // Indirect type mapping for AutoValue_ConcreteEntity should get type mapping for ConcreteEntity, not for Entity!
        assertThat(typeMappingFinder.findTypeMapping(AutoValue_ConcreteEntity.class)).isSameAs(concreteEntityTypeMapping);
    }

    @Test
    public void typeMappingShouldFindInterface() {
        // noinspection unchecked
        final TypeMapping<TypeMappingFinderImplTest.InterfaceEntity> typeMapping = Mockito.mock(TypeMapping.class);
        Map<Class<?>, TypeMapping<?>> directTypeMapping = Collections.<Class<?>, TypeMapping<?>>singletonMap(TypeMappingFinderImplTest.InterfaceEntity.class, typeMapping);
        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(directTypeMapping);
        assertThat(typeMappingFinder.findTypeMapping(TypeMappingFinderImplTest.InterfaceEntity.class)).isSameAs(typeMapping);
    }

    @Test
    public void typeMappingShouldFindIndirectTypeMappingForClassThatDirectlyImplementsKnownInterface() {
        // noinspection unchecked
        final TypeMapping<TypeMappingFinderImplTest.InterfaceEntity> typeMapping = Mockito.mock(TypeMapping.class);
        Map<Class<?>, TypeMapping<?>> directTypeMapping = Collections.<Class<?>, TypeMapping<?>>singletonMap(TypeMappingFinderImplTest.InterfaceEntity.class, typeMapping);
        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(directTypeMapping);
        class ConcreteEntity implements TypeMappingFinderImplTest.InterfaceEntity {}
        assertThat(typeMappingFinder.findTypeMapping(ConcreteEntity.class)).isSameAs(typeMapping);
        // Just to make sure that we don't return this type mapping for all classes.
        assertThat(typeMappingFinder.findTypeMapping(Random.class)).isNull();
    }

    @Test
    public void typeMappingShouldFindIndirectTypeMappingForClassThatIndirectlyImplementsKnownInterface() {
        // noinspection unchecked
        final TypeMapping<TypeMappingFinderImplTest.InterfaceEntity> typeMapping = Mockito.mock(TypeMapping.class);
        Map<Class<?>, TypeMapping<?>> directTypeMapping = Collections.<Class<?>, TypeMapping<?>>singletonMap(TypeMappingFinderImplTest.InterfaceEntity.class, typeMapping);
        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(directTypeMapping);
        class ConcreteEntity implements TypeMappingFinderImplTest.DescendantInterface {}
        assertThat(typeMappingFinder.findTypeMapping(ConcreteEntity.class)).isSameAs(typeMapping);
    }

    @Test
    public void typeMappingShouldFindIndirectTypeMappingForClassThatIndirectlyImplementsKnownInterfaceAndExtendsUnknownClass() {
        // noinspection unchecked
        final TypeMapping<TypeMappingFinderImplTest.InterfaceEntity> typeMapping = Mockito.mock(TypeMapping.class);
        Map<Class<?>, TypeMapping<?>> directTypeMapping = Collections.<Class<?>, TypeMapping<?>>singletonMap(TypeMappingFinderImplTest.InterfaceEntity.class, typeMapping);
        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(directTypeMapping);
        class ConcreteEntity extends TypeMappingFinderImplTest.ClassEntity implements TypeMappingFinderImplTest.DescendantInterface {}
        assertThat(typeMappingFinder.findTypeMapping(ConcreteEntity.class)).isSameAs(typeMapping);
    }

    @Test
    public void typeMappingShouldFindIndirectTypeMappingForClassThatHasParentThatImplementsKnownInterface() {
        // noinspection unchecked
        final TypeMapping<TypeMappingFinderImplTest.InterfaceEntity> typeMapping = Mockito.mock(TypeMapping.class);
        Map<Class<?>, TypeMapping<?>> directTypeMapping = Collections.<Class<?>, TypeMapping<?>>singletonMap(TypeMappingFinderImplTest.InterfaceEntity.class, typeMapping);
        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(directTypeMapping);
        class ConcreteEntity implements TypeMappingFinderImplTest.InterfaceEntity {}
        class Parent_ConcreteEntity extends ConcreteEntity {}
        assertThat(typeMappingFinder.findTypeMapping(Parent_ConcreteEntity.class)).isSameAs(typeMapping);
    }

    @Test
    public void shouldPreferDirectTypeMappingToIndirectOfInterface() {
        class ConcreteEntity implements TypeMappingFinderImplTest.InterfaceEntity {}
        // noinspection unchecked
        final TypeMapping<TypeMappingFinderImplTest.InterfaceEntity> indirectTypeMapping = Mockito.mock(TypeMapping.class);
        // noinspection unchecked
        final TypeMapping<ConcreteEntity> directTypeMapping = Mockito.mock(TypeMapping.class);
        Map<Class<?>, TypeMapping<?>> mappingMap = new HashMap<Class<?>, TypeMapping<?>>(2);
        mappingMap.put(TypeMappingFinderImplTest.InterfaceEntity.class, indirectTypeMapping);
        mappingMap.put(ConcreteEntity.class, directTypeMapping);
        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(mappingMap);
        assertThat(typeMappingFinder.findTypeMapping(ConcreteEntity.class)).isSameAs(directTypeMapping);
    }
}

