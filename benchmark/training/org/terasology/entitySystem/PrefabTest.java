/**
 * Copyright 2013 MovingBlocks
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
package org.terasology.entitySystem;


import MappedContainerComponent.Cont;
import Side.BACK;
import Side.BOTTOM;
import Side.FRONT;
import Side.LEFT;
import Side.RIGHT;
import Side.TOP;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.entitySystem.stubs.ListOfEnumsComponent;
import org.terasology.entitySystem.stubs.ListOfObjectComponent;
import org.terasology.entitySystem.stubs.MappedContainerComponent;
import org.terasology.entitySystem.stubs.OrderedMapTestComponent;
import org.terasology.entitySystem.stubs.StringComponent;


/**
 *
 */
public class PrefabTest {
    private static final Logger logger = LoggerFactory.getLogger(PrefabTest.class);

    private PrefabManager prefabManager;

    @Test
    public void testGetSimplePrefab() {
        Prefab prefab = prefabManager.getPrefab("unittest:simple");
        Assert.assertNotNull(prefab);
        Assert.assertEquals("unittest:simple", prefab.getName());
    }

    @Test
    public void testPrefabHasDefinedComponents() {
        Prefab prefab = prefabManager.getPrefab("unittest:withComponent");
        Assert.assertTrue(prefab.hasComponent(StringComponent.class));
    }

    @Test
    public void testPrefabHasDefinedComponentsWithOrderedMap() {
        Prefab prefab = prefabManager.getPrefab("unittest:withComponentContainingOrderedMap");
        Assert.assertTrue(prefab.hasComponent(OrderedMapTestComponent.class));
        OrderedMapTestComponent component = prefab.getComponent(OrderedMapTestComponent.class);
        Assert.assertNotNull(component);
        Map<String, Long> orderedMap = component.orderedMap;
        Set<String> keySet = orderedMap.keySet();
        List<String> keyList = new ArrayList<>(keySet);
        Assert.assertEquals(4, keyList.size());
        Assert.assertEquals("one", keyList.get(0));
        Assert.assertEquals("two", keyList.get(1));
        Assert.assertEquals("three", keyList.get(2));
        Assert.assertEquals("four", keyList.get(3));
        Assert.assertEquals(Long.valueOf(1), orderedMap.get("one"));
        Assert.assertEquals(Long.valueOf(2), orderedMap.get("two"));
        Assert.assertEquals(Long.valueOf(3), orderedMap.get("three"));
        Assert.assertEquals(Long.valueOf(4), orderedMap.get("four"));
    }

    @Test
    public void testPrefabInheritsFromParent() {
        Prefab prefab = prefabManager.getPrefab("unittest:inheritsComponent");
        Assert.assertTrue(prefab.hasComponent(StringComponent.class));
    }

    @Test
    public void testPrefabTransitiveInheritance() {
        Prefab prefab = prefabManager.getPrefab("unittest:multilevelInheritance");
        Assert.assertTrue(prefab.hasComponent(StringComponent.class));
    }

    @Test
    public void testPrefabWithCollectionOfMappedContainers() {
        Prefab prefab = prefabManager.getPrefab("unittest:withCollectionOfMappedContainers");
        MappedContainerComponent mappedContainer = prefab.getComponent(MappedContainerComponent.class);
        Assert.assertNotNull(mappedContainer);
        Assert.assertNotNull(mappedContainer.containers);
        Assert.assertEquals(1, mappedContainer.containers.size());
        MappedContainerComponent.Cont cont = mappedContainer.containers.iterator().next();
        Assert.assertNotNull(cont);
        Assert.assertEquals("a", cont.value);
    }

    @Test
    public void testPrefabWithListOfMappedContainers() {
        Prefab prefab = prefabManager.getPrefab("unittest:withListContainer");
        ListOfObjectComponent mappedContainer = prefab.getComponent(ListOfObjectComponent.class);
        Assert.assertEquals(2, mappedContainer.elements.size());
        Assert.assertEquals("returnHome", mappedContainer.elements.get(1).id);
    }

    @Test
    public void testPrefabWithListOfEnums() {
        Prefab prefab = prefabManager.getPrefab("unittest:withListEnumContainer");
        ListOfEnumsComponent mappedContainer = prefab.getComponent(ListOfEnumsComponent.class);
        Assert.assertEquals(6, mappedContainer.elements.size());
        Assert.assertEquals(TOP, mappedContainer.elements.get(0));
        Assert.assertEquals(LEFT, mappedContainer.elements.get(1));
        Assert.assertEquals(RIGHT, mappedContainer.elements.get(2));
        Assert.assertEquals(FRONT, mappedContainer.elements.get(3));
        Assert.assertEquals(BACK, mappedContainer.elements.get(4));
        Assert.assertEquals(BOTTOM, mappedContainer.elements.get(5));
    }
}

