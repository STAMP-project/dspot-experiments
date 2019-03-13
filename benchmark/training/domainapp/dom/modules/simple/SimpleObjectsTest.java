/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package domainapp.dom.modules.simple;


import Mode.INTERFACES_AND_CLASSES;
import com.google.common.collect.Lists;
import java.util.List;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.auto.Mock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 * Test for SimpleObjects
 */
public class SimpleObjectsTest {
    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(INTERFACES_AND_CLASSES);

    @Mock
    DomainObjectContainer mockContainer;

    SimpleObjects simpleObjects;

    @Test
    public void testCreate() throws Exception {
        // given
        final SimpleObject simpleObject = new SimpleObject();
        final Sequence seq = context.sequence("create");
        context.checking(new Expectations() {
            {
                oneOf(mockContainer).newTransientInstance(SimpleObject.class);
                inSequence(seq);
                will(returnValue(simpleObject));
                oneOf(mockContainer).persistIfNotAlready(simpleObject);
                inSequence(seq);
            }
        });
        // when
        String objectName = "Foobar";
        final SimpleObject obj = simpleObjects.create(objectName);
        // then
        Assert.assertEquals(simpleObject, obj);
        Assert.assertEquals(objectName, obj.getName());
    }

    @Test
    public void testListAll() throws Exception {
        // given
        final List<SimpleObject> all = Lists.newArrayList();
        context.checking(new Expectations() {
            {
                oneOf(mockContainer).allInstances(SimpleObject.class);
                will(returnValue(all));
            }
        });
        // when
        final List<SimpleObject> list = simpleObjects.listAll();
        // then
        Assert.assertEquals(all, list);
    }
}

