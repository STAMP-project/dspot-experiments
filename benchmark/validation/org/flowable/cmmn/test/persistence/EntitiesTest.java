/**
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
package org.flowable.cmmn.test.persistence;


import EntityDependencyOrder.DELETE_ORDER;
import EntityDependencyOrder.INSERT_ORDER;
import TableDataManagerImpl.entityToTableNameMap;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class EntitiesTest {
    @Test
    public void verifyMappedEntitiesExist() {
        Set<String> mappedResources = getMappedResources();
        Assert.assertTrue(((mappedResources.size()) > 0));
        for (String mappedResource : mappedResources) {
            getAndAssertEntityInterfaceClass(mappedResource);
            getAndAssertEntityImplClass(mappedResource);
        }
    }

    @Test
    public void verifyEntitiesInEntityDependencyOrder() {
        Set<String> mappedResources = getMappedResources();
        for (String mappedResource : mappedResources) {
            Assert.assertTrue(("No insert entry in EntityDependencyOrder for " + mappedResource), INSERT_ORDER.contains(getAndAssertEntityImplClass(mappedResource)));
            Assert.assertTrue(("No delete entry in EntityDependencyOrder for " + mappedResource), DELETE_ORDER.contains(getAndAssertEntityImplClass(mappedResource)));
        }
    }

    @Test
    public void verifyEntitiesInTableDataManager() {
        Set<String> mappedResources = getMappedResources();
        for (String mappedResource : mappedResources) {
            Assert.assertTrue(("No entry in TableDataManagerImpl for " + mappedResource), entityToTableNameMap.containsKey(getAndAssertEntityInterfaceClass(mappedResource)));
        }
    }
}

