/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.core.command.runtime.rule;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.drools.core.common.DisconnectedFactHandle;
import org.drools.core.xml.jaxb.util.JaxbListWrapper;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;


public class CommandSerializationTest {
    private Class<?>[] annotatedJaxbClasses = new Class<?>[]{ JaxbListWrapper.class };

    // TESTS ----------------------------------------------------------------------------------------------------------------------
    @Test
    public void updateCommandTest() throws Exception {
        DisconnectedFactHandle discFactHandle = new DisconnectedFactHandle(2, 3, 4, 5L, "entry-point-id", "str-obj", true);
        DisconnectedFactHandle copyDiscFactHandle = roundTrip(discFactHandle);
        verifyDisconnectedFactHandle(discFactHandle, copyDiscFactHandle);
        UpdateCommand cmd = new UpdateCommand(discFactHandle, "new-str-object");
        UpdateCommand copyCmd = roundTrip(cmd);
        verifyDisconnectedFactHandle(discFactHandle, copyCmd.getHandle());
        Assert.assertEquals("entry point", cmd.getEntryPoint(), copyCmd.getEntryPoint());
        Assert.assertEquals("object", cmd.getObject(), copyCmd.getObject());
    }

    @Test
    public void insertObjectCommandTest() throws Exception {
        InsertObjectCommand cmd = new InsertObjectCommand("obj", "out-id");
        cmd.setReturnObject(false);
        cmd.setEntryPoint("entry-point");
        InsertObjectCommand copyCmd = roundTrip(cmd);
        Assert.assertEquals("object", cmd.getObject(), copyCmd.getObject());
        Assert.assertEquals("out id", cmd.getOutIdentifier(), copyCmd.getOutIdentifier());
        Assert.assertEquals("return obj", cmd.isReturnObject(), copyCmd.isReturnObject());
        Assert.assertEquals("entry point", cmd.getEntryPoint(), copyCmd.getEntryPoint());
        Assert.assertEquals("disconnected", cmd.isDisconnected(), copyCmd.isDisconnected());
    }

    @Test
    public void insertObjectCommandListTest() throws Exception {
        List<String> objectList = new ArrayList<String>();
        objectList.add("obj");
        InsertObjectCommand cmd = new InsertObjectCommand(objectList, "out-id");
        InsertObjectCommand copyCmd = roundTrip(cmd);
        Assert.assertNotNull(copyCmd);
        Assert.assertThat(copyCmd.getObject(), Is.is(IsInstanceOf.instanceOf(List.class)));
        Assert.assertEquals("object", cmd.getObject(), copyCmd.getObject());
    }

    @Test
    public void insertObjectCommandEmptyListTest() throws Exception {
        List<String> objectList = new ArrayList<String>();
        objectList.add("one-element");
        InsertObjectCommand cmd = new InsertObjectCommand(objectList, "out-id");
        // test list with 1 element
        InsertObjectCommand copyCmd = roundTrip(cmd);
        Assert.assertNotNull(copyCmd);
        Assert.assertThat(copyCmd.getObject(), Is.is(IsInstanceOf.instanceOf(List.class)));
        Assert.assertEquals("object", cmd.getObject(), copyCmd.getObject());
        // test empty list
        objectList.clear();
        copyCmd = roundTrip(cmd);
        Assert.assertNotNull(copyCmd);
        Assert.assertThat(copyCmd.getObject(), Is.is(IsInstanceOf.instanceOf(List.class)));
        Assert.assertEquals("object", cmd.getObject(), copyCmd.getObject());
    }

    private static final Random random = new Random();
}

