/**
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.functional_tests;


import com.github.dozermapper.core.vo.direction.ContentItemGroup;
import com.github.dozermapper.core.vo.direction.ContentItemGroupDTO;
import com.github.dozermapper.core.vo.direction.ContentItemGroupDefault;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;


public class InheritanceDirectionTest extends AbstractFunctionalTest {
    @Test
    public void testInheritanceDirection_Child() {
        ContentItemGroupDefault parentItem = newInstance(ContentItemGroupDefault.class);
        parentItem.setId("A");
        ContentItemGroupDefault childItem = newInstance(ContentItemGroupDefault.class);
        childItem.setId("B");
        childItem.setParentGroup(parentItem);
        parentItem.addChildGroup(childItem);
        ContentItemGroupDTO resultChild = mapper.map(childItem, ContentItemGroupDTO.class);
        Assert.assertNotNull(resultChild);
        Assert.assertEquals("B", resultChild.getId());
        Assert.assertNull(resultChild.getChildGroups());
        ContentItemGroupDTO parentResult = resultChild.getParentGroup();
        Assert.assertEquals("A", parentResult.getId());
        Assert.assertTrue(parentResult.getChildGroups().contains(resultChild));
    }

    @Test
    public void testInheritanceDirection_Parent() {
        ContentItemGroupDefault parentItem = newInstance(ContentItemGroupDefault.class);
        parentItem.setId("A");
        ContentItemGroupDefault childItem = newInstance(ContentItemGroupDefault.class);
        childItem.setId("B");
        childItem.setParentGroup(parentItem);
        parentItem.addChildGroup(childItem);
        ContentItemGroupDTO resultParent = mapper.map(parentItem, ContentItemGroupDTO.class);
        ContentItemGroupDTO resultChild = ((ContentItemGroupDTO) (resultParent.getChildGroups().iterator().next()));
        Assert.assertNotNull(resultChild);
        Assert.assertEquals("B", resultChild.getId());
        Assert.assertNull(resultChild.getChildGroups());
        Assert.assertEquals("A", resultParent.getId());
        Assert.assertTrue(resultParent.getChildGroups().contains(resultChild));
    }

    @Test
    public void testInheritanceDirection_Reverse() {
        ContentItemGroupDTO parent = newInstance(ContentItemGroupDTO.class);
        parent.setId("A");
        ContentItemGroupDTO child = newInstance(ContentItemGroupDTO.class);
        child.setId("B");
        child.setParentGroup(parent);
        HashSet<ContentItemGroupDTO> childGroups = newInstance(HashSet.class);
        childGroups.add(child);
        parent.setChildGroups(childGroups);
        ContentItemGroup result = mapper.map(parent, ContentItemGroupDefault.class);
        Assert.assertNotNull(result);
        ContentItemGroup childResult = ((ContentItemGroup) (result.getChildGroups().iterator().next()));
        Assert.assertEquals(result, childResult.getParentGroup());
        Assert.assertEquals("A", result.getId());
        Assert.assertEquals("B", childResult.getId());
    }
}

