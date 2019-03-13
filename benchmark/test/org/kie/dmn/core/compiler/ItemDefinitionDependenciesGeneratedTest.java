/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.dmn.core.compiler;


import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.dmn.model.api.ItemDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class ItemDefinitionDependenciesGeneratedTest {
    private final Logger logger = LoggerFactory.getLogger(ItemDefinitionDependenciesGeneratedTest.class);

    private static final int NUMBER_OF_BASE_ITEM_DEFINITIONS = 5;

    private static final int LEVELS_OF_DEPENDENCIES = 3;

    private static final String ITEM_DEFINITION_NAME_BASE = "ItemDefinition";

    private static final String TEST_NS = "https://www.drools.org/";

    @Parameterized.Parameter
    public static List<ItemDefinition> itemDefinitions;

    @Test
    public void testOrdering() {
        logger.trace("Item definitions:");
        ItemDefinitionDependenciesGeneratedTest.itemDefinitions.forEach(( itemDefinition) -> {
            logger.trace(itemDefinition.getName());
            itemDefinition.getItemComponent().forEach(( dependency) -> logger.trace(dependency.getName()));
        });
        final List<ItemDefinition> orderedList = orderingStrategy(ItemDefinitionDependenciesGeneratedTest.itemDefinitions);
        for (final ItemDefinition itemDefinition : ItemDefinitionDependenciesGeneratedTest.itemDefinitions) {
            assertOrdering(itemDefinition, orderedList);
        }
    }
}

