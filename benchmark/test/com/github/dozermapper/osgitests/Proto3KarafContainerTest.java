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
package com.github.dozermapper.osgitests;


import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.DozerModule;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.osgitests.support.PaxExamTestSupport;
import com.github.dozermapper.osgitestsmodel.Activator;
import com.github.dozermapper.protobuf.ProtobufSupportModule;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class Proto3KarafContainerTest extends CoreKarafContainerTest {
    @Test
    @Override
    public void canGetBundleFromDozerCore() {
        super.canGetBundleFromDozerCore();
        Bundle proto = getBundle(bundleContext, "com.github.dozermapper.dozer-proto3");
        Assert.assertNotNull(proto);
        Assert.assertEquals(Bundle.ACTIVE, proto.getState());
    }

    @Test
    public void canResolveProtobufSupportModule() {
        Mapper mapper = DozerBeanMapperBuilder.create().withXmlMapping(() -> getLocalResource("mappings/mapping.xml")).withClassLoader(new com.github.dozermapper.core.osgi.OSGiClassLoader(Activator.getBundleContext())).build();
        Assert.assertNotNull(mapper);
        Assert.assertNotNull(mapper.getMappingMetadata());
        ServiceReference<DozerModule> dozerModuleServiceReference = bundleContext.getServiceReference(DozerModule.class);
        Assert.assertNotNull(dozerModuleServiceReference);
        DozerModule dozerModule = bundleContext.getService(dozerModuleServiceReference);
        Assert.assertNotNull(dozerModule);
        Assert.assertTrue((dozerModule instanceof ProtobufSupportModule));
    }
}

