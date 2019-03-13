/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.compiler.runtime.pipeline.impl;


import Message.Level.ERROR;
import Message.Level.WARNING;
import ResourceType.DRL;
import ResourceType.XSD;
import com.sun.tools.xjc.Options;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.io.impl.BaseResource;
import org.drools.core.io.impl.InputStreamResource;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;


public class DroolsJaxbHelperTest {
    private static final String simpleXsdRelativePath = "simple.xsd";

    @Test
    public void testXsdModelInRule() {
        // DRL rule that references object created in XSD
        final String s1 = "package test; " + ((((((((((((((((("global java.util.List list; " + "rule Init when ") + "then ") + "  insert( new Sub() ); ") + "  insert( new Message() ); ") + "  insert( new Test() ); ") + "  insert( new Left() ); ") + "end\n") + "rule CheckSub when ") + " $s : Sub() ") + "then ") + "  list.add( \"Sub\" );  ") + "end\n") + "rule CheckMsg when ") + " $s : Message() ") + "then ") + "  list.add( \"Message\" );  ") + "end\n ");
        KieHelper kh = new KieHelper();
        kh.addContent(s1, DRL);
        // XSD that defines "Sub" class
        InputStream simpleXsdStream = getClass().getResourceAsStream(DroolsJaxbHelperTest.simpleXsdRelativePath);
        Assert.assertNotNull(("Could not find resource: " + (DroolsJaxbHelperTest.simpleXsdRelativePath)), simpleXsdStream);
        BaseResource xsdResource = new InputStreamResource(simpleXsdStream);
        Options xjcOptions = new Options();
        xsdResource.setConfiguration(new org.drools.core.builder.conf.impl.JaxbConfigurationImpl(xjcOptions, "test-system-id"));
        kh.addResource(xsdResource, XSD);
        // Verify that build succeeded
        Assert.assertEquals(0, kh.verify().getMessages(ERROR).size());
        Assert.assertEquals(0, kh.verify().getMessages(WARNING).size());
        // Run rules
        KieSession ks = kh.build().newKieSession();
        List list = new ArrayList();
        ks.setGlobal("list", list);
        ks.fireAllRules();
        // Verify results
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.containsAll(Arrays.asList("Sub", "Message")));
        Collection<FactHandle> fhs = ks.getFactHandles();
        Iterator<FactHandle> iter = fhs.iterator();
        DefaultFactHandle subFh = null;
        DefaultFactHandle msgFh = null;
        DefaultFactHandle leftFh = null;
        DefaultFactHandle testFh = null;
        while (iter.hasNext()) {
            DefaultFactHandle dfh = ((DefaultFactHandle) (iter.next()));
            if (dfh.getObjectClassName().equals("test.Sub")) {
                subFh = dfh;
            } else
                if (dfh.getObjectClassName().equals("test.Message")) {
                    msgFh = dfh;
                } else
                    if (dfh.getObjectClassName().equals("test.Left")) {
                        leftFh = dfh;
                    } else
                        if (dfh.getObjectClassName().equals("test.Test")) {
                            testFh = dfh;
                        } else {
                            Assert.fail(("Unexpected FH class: " + (dfh.getObjectClassName())));
                        }



        } 
        Assert.assertNotNull("No FactHandle for Sub found!", subFh);
        Assert.assertNotNull("No FactHandle for Message found!", msgFh);
        Object xsdObj = subFh.getObject();
        Class xsdClass = xsdObj.getClass();
        try {
            Method m2 = xsdClass.getMethod("getFld");
            Assert.assertNotNull(m2);
            Assert.assertEquals(String.class, m2.getReturnType());
            Assert.assertEquals(0, xsdClass.getFields().length);
            Field[] declaredFields = xsdClass.getDeclaredFields();
            Assert.assertEquals(1, declaredFields.length);
            Assert.assertEquals("fld", declaredFields[0].getName());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        // "Message" has attribute 'mixed="true"' which means only one field "content"
        xsdObj = msgFh.getObject();
        xsdClass = xsdObj.getClass();
        try {
            Method m2 = xsdClass.getMethod("getContent");
            Assert.assertNotNull(m2);
            Assert.assertEquals(List.class, m2.getReturnType());
            Assert.assertEquals(0, xsdClass.getFields().length);
            Field[] declaredFields = xsdClass.getDeclaredFields();
            Assert.assertEquals(1, declaredFields.length);
            Assert.assertEquals("content", declaredFields[0].getName());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}

