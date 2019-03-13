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
package com.github.dozermapper.core.functional_tests.builder;


import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;


/**
 * This test is to verify that the builder with the specified mapid is being used for the objects within a collection contained within an object which is being mapped.
 * <p>
 * There appeared to be an issue in {@link MappingProcessor} for non_cumulative Sets and Lists where mapToDestObject is
 * being called without passing the configured mapId of the object being mapped resulting in the default behaviour being
 * used rather than the configuration specific by the builder that has been defined against the mapid.
 */
public class DeepMappingWithMapIdTest {
    private static final String MAP_ID_PATENT = "mapIdParent";

    private static final String MAP_ID_CHILD = "mapIdChild";

    @Test
    public void testMappingListOfObjectsWithMapId() {
        // Our src object created with a collection of children one of which will have a null field,
        // the destination will contain a matching child which does not contain a null
        // value which we don't want to overwrite.
        DeepMappingWithMapIdTest.Child srcChild1 = new DeepMappingWithMapIdTest.Child(1, "F1", null);
        DeepMappingWithMapIdTest.ParentWithChildList src = new DeepMappingWithMapIdTest.ParentWithChildList(srcChild1, new DeepMappingWithMapIdTest.Child(2, "F2", "L2"));
        DeepMappingWithMapIdTest.ParentWithChildList dest = new DeepMappingWithMapIdTest.ParentWithChildList(new DeepMappingWithMapIdTest.Child(1, "F1Origial", "L1"), new DeepMappingWithMapIdTest.Child(2, "F2Original", "L2Original"));
        // Double check to make sure that the field is null, otherwise our test would be invalid.
        Assert.assertNull(srcChild1.getLastName());
        Assert.assertEquals(new Integer(1), srcChild1.getId());
        Mapper mapper = DozerBeanMapperBuilder.create().withMappingBuilder(getParentMapping(DeepMappingWithMapIdTest.ParentWithChildList.class)).withMappingBuilder(getChildMapping()).build();
        mapper.map(src, dest, DeepMappingWithMapIdTest.MAP_ID_PATENT);
        checkResults(src, dest);
    }

    @Test
    public void testMappingSetOfObjectsWithMapId() {
        // Our src object created with a collection of children one of which will have a null field,
        // the destination will contain a matching child which does not contain a null
        // value which we don't want to overwrite.
        DeepMappingWithMapIdTest.Child srcChild1 = new DeepMappingWithMapIdTest.Child(1, "F1", null);
        DeepMappingWithMapIdTest.ParentWithChildSet src = new DeepMappingWithMapIdTest.ParentWithChildSet(srcChild1, new DeepMappingWithMapIdTest.Child(2, "F2", "L2"));
        DeepMappingWithMapIdTest.ParentWithChildSet dest = new DeepMappingWithMapIdTest.ParentWithChildSet(new DeepMappingWithMapIdTest.Child(1, "F1Origial", "L1"), new DeepMappingWithMapIdTest.Child(2, "F2Original", "L2Original"));
        // Double check to make sure that the field is null, otherwise our test would be invalid.
        Assert.assertNull(srcChild1.getLastName());
        Assert.assertEquals(new Integer(1), srcChild1.getId());
        // Perform the mapping
        Mapper mapper = DozerBeanMapperBuilder.create().withMappingBuilder(getParentMapping(DeepMappingWithMapIdTest.ParentWithChildSet.class)).withMappingBuilder(getChildMapping()).build();
        mapper.map(src, dest, DeepMappingWithMapIdTest.MAP_ID_PATENT);
        checkResults(src, dest);
    }

    public static class ParentWithChildList implements DeepMappingWithMapIdTest.Parent<List<DeepMappingWithMapIdTest.Child>> {
        /**
         * The list of children which we are expecting to be mapped with the BeanMappingBuilder defined by getChildMapping(), which should not map nulls in the child;
         */
        List<DeepMappingWithMapIdTest.Child> children = new ArrayList<>();

        public ParentWithChildList(DeepMappingWithMapIdTest.Child child1, DeepMappingWithMapIdTest.Child child2) {
            super();
            children.add(child1);
            children.add(child2);
        }

        @Override
        public List<DeepMappingWithMapIdTest.Child> getChildren() {
            return children;
        }

        @Override
        public void setChildren(List<DeepMappingWithMapIdTest.Child> children) {
            this.children = children;
        }
    }

    public static class ParentWithChildSet implements DeepMappingWithMapIdTest.Parent<Set<DeepMappingWithMapIdTest.Child>> {
        /**
         * The set of children which we are expecting to be mapped with the BeanMappingBuilder defined by
         * getChildMapping(), which should not map nulls in the child. Using a sorted set so that we can iterate over
         * the values and know they are in id order, which means both src and desc sets can easily be compared
         */
        Set<DeepMappingWithMapIdTest.Child> children = new TreeSet();

        public ParentWithChildSet(DeepMappingWithMapIdTest.Child child1, DeepMappingWithMapIdTest.Child child2) {
            super();
            children.add(child1);
            children.add(child2);
        }

        @Override
        public Set<DeepMappingWithMapIdTest.Child> getChildren() {
            return children;
        }

        @Override
        public void setChildren(Set<DeepMappingWithMapIdTest.Child> children) {
            this.children = children;
        }
    }

    public interface Parent<T extends Collection<DeepMappingWithMapIdTest.Child>> {
        T getChildren();

        void setChildren(T children);
    }

    public static class Child implements Comparable<DeepMappingWithMapIdTest.Child> {
        public Integer id;

        public String firstName;

        public String lastName;

        public Child() {
            super();
        }

        public Child(Integer id, String firstName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        @Override
        public int hashCode() {
            return getId().hashCode();
        }

        /**
         * Implemented equals so that the mapping can correctly determine if the child objects in the list represent the
         * same object. Required as per the api documentation.
         */
        @Override
        public boolean equals(Object obj) {
            boolean equals = false;
            if ((obj != null) && (this.getClass().isAssignableFrom(obj.getClass()))) {
                equals = this.getId().equals(((DeepMappingWithMapIdTest.Child) (obj)).getId());
            }
            return equals;
        }

        @Override
        public int compareTo(DeepMappingWithMapIdTest.Child child) {
            return getId().compareTo(child.getId());
        }
    }
}

