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
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.osgi.Activator;
import com.github.dozermapper.osgitests.support.PaxExamTestSupport;
import com.github.dozermapper.osgitestsmodel.Person;
import java.util.Dictionary;
import junit.framework.TestCase;
import org.apache.felix.utils.manifest.Clause;
import org.apache.felix.utils.manifest.Parser;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.Bundle;


public abstract class AbstractDozerCoreOsgiContainerTest extends PaxExamTestSupport {
    @Test
    public void canGetBundleFromDozerCore() {
        Assert.assertNotNull(bundleContext);
        Assert.assertNotNull(Activator.getContext());
        Assert.assertNotNull(Activator.getBundle());
        Bundle core = getBundle(bundleContext, "com.github.dozermapper.dozer-core");
        Assert.assertNotNull(core);
        Assert.assertEquals(Bundle.ACTIVE, core.getState());
        for (Bundle current : bundleContext.getBundles()) {
            PaxExamTestSupport.LOG.info("Bundle: {}", current.getSymbolicName());
            // Ignore any Karaf bundles
            if ((current.getSymbolicName().startsWith("org.apache.karaf")) || (current.getSymbolicName().startsWith("org.jline"))) {
                continue;
            }
            Assert.assertEquals(current.getSymbolicName(), Bundle.ACTIVE, current.getState());
        }
    }

    @Test
    public void canConstructDozerBeanMapper() {
        Mapper mapper = DozerBeanMapperBuilder.create().withXmlMapping(() -> getLocalResource("mappings/mapping.xml")).withClassLoader(new com.github.dozermapper.core.osgi.OSGiClassLoader(com.github.dozermapper.osgitestsmodel.Activator.getBundleContext())).build();
        Assert.assertNotNull(mapper);
        Assert.assertNotNull(mapper.getMappingMetadata());
    }

    @Test
    public void canMapUsingXML() {
        Mapper mapper = DozerBeanMapperBuilder.create().withXmlMapping(() -> getLocalResource("mappings/mapping.xml")).withClassLoader(new com.github.dozermapper.core.osgi.OSGiClassLoader(com.github.dozermapper.osgitestsmodel.Activator.getBundleContext())).build();
        Assert.assertNotNull(mapper);
        Assert.assertNotNull(mapper.getMappingMetadata());
        Person answer = mapper.map(new Person("bob"), Person.class);
        Assert.assertNotNull(answer);
        Assert.assertNotNull(answer.getName());
        Assert.assertEquals("bob", answer.getName());
    }

    @Test
    public void canResolveAllImports() {
        Bundle core = getBundle(bundleContext, "com.github.dozermapper.dozer-core");
        Assert.assertNotNull(core);
        Assert.assertEquals(Bundle.ACTIVE, core.getState());
        Dictionary<String, String> headers = core.getHeaders();
        Assert.assertNotNull(headers);
        Clause[] clauses = Parser.parseHeader(headers.get("Import-Package"));
        for (Clause clause : clauses) {
            PaxExamTestSupport.LOG.info("Package: {}", clause.getName());
            TestCase.assertTrue(checkPackage(clause.getName()));
        }
    }
}

