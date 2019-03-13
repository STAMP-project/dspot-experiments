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
package com.github.dozermapper.spring.functional_tests;


import com.github.dozermapper.core.DozerBeanMapper;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.spring.functional_tests.support.ReferencingBean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/springNameSpace.xml")
public class NamespaceTest {
    @Autowired
    ApplicationContext context;

    @Test
    public void shouldRegisterMapper() {
        Mapper beanMapper = context.getBean("beanMapper", Mapper.class);
        ReferencingBean referencingBean = context.getBean(ReferencingBean.class);
        Assert.assertNotNull(beanMapper);
        Assert.assertNotNull(referencingBean);
        Mapper mapper = referencingBean.getMapper();
        Assert.assertNotNull(mapper);
        Assert.assertSame(beanMapper, mapper);
    }

    @Test
    public void shouldApplyMappingFiles() {
        DozerBeanMapper beanMapper = context.getBean("beanMapperWithMappingFiles", DozerBeanMapper.class);
        Assert.assertTrue(beanMapper.getMappingFiles().stream().anyMatch(( file) -> file.contains("/mappings/")));
    }
}

