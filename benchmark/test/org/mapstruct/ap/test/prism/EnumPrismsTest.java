/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.prism;


import org.junit.Test;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.MappingInheritanceStrategy;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.ap.internal.prism.CollectionMappingStrategyPrism;
import org.mapstruct.ap.internal.prism.InjectionStrategyPrism;
import org.mapstruct.ap.internal.prism.MappingInheritanceStrategyPrism;
import org.mapstruct.ap.internal.prism.NullValueCheckStrategyPrism;
import org.mapstruct.ap.internal.prism.NullValueMappingStrategyPrism;
import org.mapstruct.ap.internal.prism.ReportingPolicyPrism;


/**
 * Test for manually created prisms on enumeration types
 *
 * @author Andreas Gudian
 */
public class EnumPrismsTest {
    @Test
    public void collectionMappingStrategyPrismIsCorrect() {
        assertThat(EnumPrismsTest.namesOf(CollectionMappingStrategy.values())).isEqualTo(EnumPrismsTest.namesOf(CollectionMappingStrategyPrism.values()));
    }

    @Test
    public void mappingInheritanceStrategyPrismIsCorrect() {
        assertThat(EnumPrismsTest.namesOf(MappingInheritanceStrategy.values())).isEqualTo(EnumPrismsTest.namesOf(MappingInheritanceStrategyPrism.values()));
    }

    @Test
    public void nullValueCheckStrategyPrismIsCorrect() {
        assertThat(EnumPrismsTest.namesOf(NullValueCheckStrategy.values())).isEqualTo(EnumPrismsTest.namesOf(NullValueCheckStrategyPrism.values()));
    }

    @Test
    public void nullValueMappingStrategyPrismIsCorrect() {
        assertThat(EnumPrismsTest.namesOf(NullValueMappingStrategy.values())).isEqualTo(EnumPrismsTest.namesOf(NullValueMappingStrategyPrism.values()));
    }

    @Test
    public void reportingPolicyPrismIsCorrect() {
        assertThat(EnumPrismsTest.namesOf(ReportingPolicy.values())).isEqualTo(EnumPrismsTest.namesOf(ReportingPolicyPrism.values()));
    }

    @Test
    public void injectionStrategyPrismIsCorrect() {
        assertThat(EnumPrismsTest.namesOf(InjectionStrategy.values())).isEqualTo(EnumPrismsTest.namesOf(InjectionStrategyPrism.values()));
    }
}

