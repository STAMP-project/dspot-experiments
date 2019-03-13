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
package org.flowable.dmn.engine.impl.el.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;


public class CollectionUtilTest {
    @Test
    public void noneOf() {
        Assertions.assertTrue(CollectionUtil.noneOf(Arrays.asList("group1", "group2"), Arrays.asList("group3", "group4")));
        Assertions.assertFalse(CollectionUtil.noneOf(Arrays.asList("group1", "group2"), Arrays.asList("group1", "group2")));
        Assertions.assertFalse(CollectionUtil.noneOf(Arrays.asList("group1", "group2"), Arrays.asList("group2", "group3")));
        Assertions.assertTrue(CollectionUtil.noneOf(Arrays.asList("group1", "group2"), "group3"));
        Assertions.assertFalse(CollectionUtil.noneOf(Arrays.asList("group1", "group2"), "group2"));
        Assertions.assertTrue(CollectionUtil.noneOf("group1, group2", "group3, group4"));
        Assertions.assertFalse(CollectionUtil.noneOf("group1, group2", "group1, group2"));
        Assertions.assertFalse(CollectionUtil.noneOf("group1, group2", "group2, group3"));
        ObjectMapper mapper = new ObjectMapper();
        Assertions.assertTrue(CollectionUtil.noneOf(mapper.valueToTree(Arrays.asList("group1", "group2")), mapper.valueToTree(Arrays.asList("group3", "group4"))));
        Assertions.assertFalse(CollectionUtil.noneOf(mapper.valueToTree(Arrays.asList("group1", "group2")), mapper.valueToTree(Arrays.asList("group1", "group2"))));
        Assertions.assertFalse(CollectionUtil.noneOf(mapper.valueToTree(Arrays.asList("group1", "group2")), mapper.valueToTree(Arrays.asList("group2", "group3"))));
    }

    @Test
    public void anyOf() {
        Assertions.assertFalse(CollectionUtil.anyOf(Arrays.asList("group1", "group2"), Arrays.asList("group3", "group4")));
        Assertions.assertTrue(CollectionUtil.anyOf(Arrays.asList("group1", "group2"), Arrays.asList("group1", "group2")));
        Assertions.assertTrue(CollectionUtil.anyOf(Arrays.asList("group1", "group2"), Arrays.asList("group2", "group3")));
        Assertions.assertFalse(CollectionUtil.anyOf(Arrays.asList("group1", "group2"), "group3"));
        Assertions.assertTrue(CollectionUtil.anyOf(Arrays.asList("group1", "group2"), "group2"));
        Assertions.assertFalse(CollectionUtil.anyOf("group1, group2", "group3, group4"));
        Assertions.assertTrue(CollectionUtil.anyOf("group1, group2", "group1, group2"));
        Assertions.assertTrue(CollectionUtil.anyOf("group1, group2", "group2, group3"));
        ObjectMapper mapper = new ObjectMapper();
        Assertions.assertFalse(CollectionUtil.anyOf(mapper.valueToTree(Arrays.asList("group1", "group2")), mapper.valueToTree(Arrays.asList("group3", "group4"))));
        Assertions.assertTrue(CollectionUtil.anyOf(mapper.valueToTree(Arrays.asList("group1", "group2")), mapper.valueToTree(Arrays.asList("group1", "group2"))));
        Assertions.assertTrue(CollectionUtil.anyOf(mapper.valueToTree(Arrays.asList("group1", "group2")), mapper.valueToTree(Arrays.asList("group2", "group3"))));
    }

    @Test
    public void notAllOf() {
        Assertions.assertTrue(CollectionUtil.notAllOf(Arrays.asList("group1", "group2"), Arrays.asList("group3", "group4")));
        Assertions.assertFalse(CollectionUtil.notAllOf(Arrays.asList("group1", "group2"), Arrays.asList("group1", "group2")));
        Assertions.assertTrue(CollectionUtil.notAllOf(Arrays.asList("group1", "group2"), Arrays.asList("group2", "group3")));
        Assertions.assertTrue(CollectionUtil.notAllOf(Arrays.asList("group1", "group2"), "group3"));
        Assertions.assertFalse(CollectionUtil.notAllOf(Arrays.asList("group1", "group2"), "group2"));
        Assertions.assertTrue(CollectionUtil.notAllOf("group1, group2", "group3, group4"));
        Assertions.assertFalse(CollectionUtil.notAllOf("group1, group2", "group1, group2"));
        Assertions.assertTrue(CollectionUtil.notAllOf("group1, group2", "group2, group3"));
        ObjectMapper mapper = new ObjectMapper();
        Assertions.assertTrue(CollectionUtil.notAllOf(mapper.valueToTree(Arrays.asList("group1", "group2")), mapper.valueToTree(Arrays.asList("group3", "group4"))));
        Assertions.assertFalse(CollectionUtil.notAllOf(mapper.valueToTree(Arrays.asList("group1", "group2")), mapper.valueToTree(Arrays.asList("group1", "group2"))));
        Assertions.assertTrue(CollectionUtil.notAllOf(mapper.valueToTree(Arrays.asList("group1", "group2")), mapper.valueToTree(Arrays.asList("group2", "group3"))));
    }

    @Test
    public void allOf() {
        Assertions.assertFalse(CollectionUtil.allOf(Arrays.asList("group1", "group2"), Arrays.asList("group3", "group4")));
        Assertions.assertTrue(CollectionUtil.allOf(Arrays.asList("group1", "group2"), Arrays.asList("group1", "group2")));
        Assertions.assertFalse(CollectionUtil.allOf(Arrays.asList("group1", "group2"), Arrays.asList("group2", "group3")));
        Assertions.assertFalse(CollectionUtil.allOf(Arrays.asList("group1", "group2"), "group3"));
        Assertions.assertTrue(CollectionUtil.allOf(Arrays.asList("group1", "group2"), "group2"));
        Assertions.assertFalse(CollectionUtil.allOf("group1, group2", "group3, group4"));
        Assertions.assertTrue(CollectionUtil.allOf("group1, group2", "group1, group2"));
        Assertions.assertFalse(CollectionUtil.allOf("group1, group2", "group2, group3"));
        ObjectMapper mapper = new ObjectMapper();
        Assertions.assertFalse(CollectionUtil.allOf(mapper.valueToTree(Arrays.asList("group1", "group2")), mapper.valueToTree(Arrays.asList("group3", "group4"))));
        Assertions.assertTrue(CollectionUtil.allOf(mapper.valueToTree(Arrays.asList("group1", "group2")), mapper.valueToTree(Arrays.asList("group1", "group2"))));
        Assertions.assertFalse(CollectionUtil.allOf(mapper.valueToTree(Arrays.asList("group1", "group2")), mapper.valueToTree(Arrays.asList("group2", "group3"))));
    }
}

