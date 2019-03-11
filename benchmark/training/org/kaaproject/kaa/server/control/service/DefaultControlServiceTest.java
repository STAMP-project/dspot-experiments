/**
 * Copyright 2014-2016 CyberVision, Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.server.control.service;


import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.server.common.zk.gen.OperationsNodeInfo;
import org.kaaproject.kaa.server.resolve.OperationsServerResolver;


public class DefaultControlServiceTest {
    private DefaultControlService service;

    private OperationsNodeInfo node;

    @Test
    public void writeLogWithoutByteBufferTest() throws Exception {
        final String format = "Update of node {} is pushed to resolver {}";
        String beforeReplacing = node.toString();
        Method method = service.getClass().getDeclaredMethod("writeLogWithoutByteBuffer", String.class, node.getClass(), OperationsServerResolver.class);
        method.setAccessible(true);
        method.invoke(service, format, node, null);
        Assert.assertEquals("Object corrupted, some fields changed and not recover", beforeReplacing, node.toString());
    }
}

