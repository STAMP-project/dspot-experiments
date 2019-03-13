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


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


public class RecursiveSelfMappingTest extends AbstractFunctionalTest {
    private RecursiveSelfMappingTest.ContainerBean c1;

    @Test
    public void testRecursiveSelfMapping() {
        testMapping(c1, "mappings/selfreference_recursive.xml");
    }

    @Test
    public void testRecursiveSelfMapping_Iterate() {
        testMapping(c1, "mappings/selfreference_recursive_iterate.xml");
    }

    public static class SelfReferencingBean {
        private Long id;

        private RecursiveSelfMappingTest.SelfReferencingBean oneToOne;

        private RecursiveSelfMappingTest.DetailBean detailBean;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public RecursiveSelfMappingTest.SelfReferencingBean getOneToOne() {
            return oneToOne;
        }

        public void setOneToOne(RecursiveSelfMappingTest.SelfReferencingBean oneToOne) {
            this.oneToOne = oneToOne;
        }

        public RecursiveSelfMappingTest.DetailBean getDetailBean() {
            return detailBean;
        }

        public void setDetailBean(RecursiveSelfMappingTest.DetailBean detailBean) {
            this.detailBean = detailBean;
        }
    }

    public static class DetailBean {
        private RecursiveSelfMappingTest.SelfReferencingBean oneToOne;

        private Long id;

        public RecursiveSelfMappingTest.SelfReferencingBean getOneToOne() {
            return oneToOne;
        }

        public void setOneToOne(RecursiveSelfMappingTest.SelfReferencingBean oneToOne) {
            this.oneToOne = oneToOne;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    public static class ContainerBean {
        private List<RecursiveSelfMappingTest.SelfReferencingBean> beans = new ArrayList<>();

        public List<RecursiveSelfMappingTest.SelfReferencingBean> getBeans() {
            return beans;
        }

        public void setBeans(List<RecursiveSelfMappingTest.SelfReferencingBean> beans) {
            this.beans = beans;
        }

        public void add(RecursiveSelfMappingTest.SelfReferencingBean bean) {
            this.beans.add(bean);
        }
    }

    // For the sake of controlling mapping direction
    public static class ContainerBean2 extends RecursiveSelfMappingTest.ContainerBean {}
}

