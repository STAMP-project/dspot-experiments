/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.docs;


import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.ehcache.config.Configuration;
import org.ehcache.config.ResourceType;
import org.ehcache.xml.multi.XmlMultiConfiguration;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.hamcrest.collection.IsMapContaining;
import org.hamcrest.core.AllOf;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.ehcache.config.ResourceType.Core.HEAP;
import static org.ehcache.config.ResourceType.Core.OFFHEAP;


public class MultiGettingStarted {
    @Test
    public void multipleConfigurations() {
        // tag::multipleManagers[]
        XmlMultiConfiguration multipleConfiguration = // <1>
        XmlMultiConfiguration.from(getClass().getResource("/configs/docs/multi/multiple-managers.xml")).build();// <2>

        Configuration fooConfiguration = multipleConfiguration.configuration("foo-manager");// <3>

        // end::multipleManagers[]
        Assert.assertThat(MultiGettingStarted.resourceMap(multipleConfiguration.identities().stream().collect(Collectors.toMap(Function.identity(), multipleConfiguration::configuration))), AllOf.allOf(IsMapContaining.hasEntry(Is.is("foo-manager"), IsMapContaining.hasEntry(Is.is("foo"), IsIterableContainingInAnyOrder.containsInAnyOrder(HEAP, OFFHEAP))), IsMapContaining.hasEntry(Is.is("bar-manager"), IsMapContaining.hasEntry(Is.is("bar"), IsIterableContainingInAnyOrder.containsInAnyOrder(HEAP, OFFHEAP)))));
    }

    @Test
    public void multipleVariants() {
        // tag::multipleVariants[]
        XmlMultiConfiguration variantConfiguration = XmlMultiConfiguration.from(getClass().getResource("/configs/docs/multi/multiple-variants.xml")).build();
        Configuration fooConfiguration = variantConfiguration.configuration("foo-manager", "offheap");// <1>

        // end::multipleVariants[]
        Assert.assertThat(MultiGettingStarted.resourceMap(variantConfiguration.identities().stream().collect(Collectors.toMap(Function.identity(), ( i) -> variantConfiguration.configuration(i, "offheap")))), AllOf.allOf(IsMapContaining.hasEntry(Is.is("foo-manager"), IsMapContaining.hasEntry(Is.is("foo"), IsIterableContainingInAnyOrder.containsInAnyOrder(HEAP, OFFHEAP))), IsMapContaining.hasEntry(Is.is("bar-manager"), IsMapContaining.hasEntry(Is.is("bar"), IsIterableContainingInAnyOrder.containsInAnyOrder(HEAP)))));
        Assert.assertThat(MultiGettingStarted.resourceMap(variantConfiguration.identities().stream().collect(Collectors.toMap(Function.identity(), ( i) -> variantConfiguration.configuration(i, "heap")))), AllOf.allOf(IsMapContaining.hasEntry(Is.is("foo-manager"), IsMapContaining.hasEntry(Is.is("foo"), IsIterableContainingInAnyOrder.containsInAnyOrder(HEAP))), IsMapContaining.hasEntry(Is.is("bar-manager"), IsMapContaining.hasEntry(Is.is("bar"), IsIterableContainingInAnyOrder.containsInAnyOrder(HEAP)))));
    }

    @Test
    public void multipleRetrieval() {
        XmlMultiConfiguration multipleConfiguration = XmlMultiConfiguration.from(getClass().getResource("/configs/docs/multi/multiple-managers.xml")).build();
        XmlMultiConfiguration variantConfiguration = XmlMultiConfiguration.from(getClass().getResource("/configs/docs/multi/multiple-variants.xml")).build();
        // tag::multipleRetrieval[]
        Map<String, Configuration> allConfigurations = // <1>
        multipleConfiguration.identities().stream().collect(Collectors.toMap(( i) -> i, ( i) -> multipleConfiguration.configuration(i)));// <2>

        Map<String, Configuration> offheapConfigurations = variantConfiguration.identities().stream().collect(Collectors.toMap(( i) -> i, ( i) -> variantConfiguration.configuration(i, "offheap")));// <3>

        // end::multipleRetrieval[]
        Assert.assertThat(MultiGettingStarted.resourceMap(allConfigurations), AllOf.allOf(IsMapContaining.hasEntry(Is.is("foo-manager"), IsMapContaining.hasEntry(Is.is("foo"), IsIterableContainingInAnyOrder.containsInAnyOrder(HEAP, OFFHEAP))), IsMapContaining.hasEntry(Is.is("bar-manager"), IsMapContaining.hasEntry(Is.is("bar"), IsIterableContainingInAnyOrder.containsInAnyOrder(HEAP, OFFHEAP)))));
        Assert.assertThat(MultiGettingStarted.resourceMap(offheapConfigurations), AllOf.allOf(IsMapContaining.hasEntry(Is.is("foo-manager"), IsMapContaining.hasEntry(Is.is("foo"), IsIterableContainingInAnyOrder.containsInAnyOrder(HEAP, OFFHEAP))), IsMapContaining.hasEntry(Is.is("bar-manager"), IsMapContaining.hasEntry(Is.is("bar"), IsIterableContainingInAnyOrder.containsInAnyOrder(HEAP)))));
    }

    @Test
    public void building() {
        XmlMultiConfiguration sourceConfiguration = XmlMultiConfiguration.from(getClass().getResource("/configs/docs/multi/multiple-variants.xml")).build();
        Configuration barConfiguration = sourceConfiguration.configuration("bar-manager");
        Configuration heapConfiguration = sourceConfiguration.configuration("foo-manager", "heap");
        Configuration offheapConfiguration = sourceConfiguration.configuration("foo-manager", "offheap");
        // tag::building[]
        XmlMultiConfiguration multiConfiguration = // <3>
        // <2>
        // <1>
        XmlMultiConfiguration.fromNothing().withManager("bar", barConfiguration).withManager("foo").variant("heap", heapConfiguration).variant("offheap", offheapConfiguration).build();// <4>

        // end::building[]
        // tag::modifying[]
        XmlMultiConfiguration modified = // <2>
        // <1>
        XmlMultiConfiguration.from(multiConfiguration).withManager("foo").build();
        // end::modifying[]
        // tag::rendering[]
        String xmlString = multiConfiguration.asRenderedDocument();// <1>

        Document xmlDocument = multiConfiguration.asDocument();// <2>

        // end::rendering[]
    }
}

