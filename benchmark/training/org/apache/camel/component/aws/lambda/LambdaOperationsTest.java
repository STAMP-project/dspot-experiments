/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.aws.lambda;


import LambdaOperations.createEventSourceMapping;
import LambdaOperations.createFunction;
import LambdaOperations.deleteEventSourceMapping;
import LambdaOperations.deleteFunction;
import LambdaOperations.getFunction;
import LambdaOperations.invokeFunction;
import LambdaOperations.listEventSourceMapping;
import LambdaOperations.listFunctions;
import LambdaOperations.listTags;
import LambdaOperations.listVersions;
import LambdaOperations.publishVersion;
import LambdaOperations.tagResource;
import LambdaOperations.untagResource;
import LambdaOperations.updateFunction;
import org.junit.Assert;
import org.junit.Test;


public class LambdaOperationsTest {
    @Test
    public void supportedOperationCount() {
        Assert.assertEquals(14, LambdaOperations.values().length);
    }

    @Test
    public void valueOf() {
        Assert.assertEquals(createFunction, LambdaOperations.valueOf("createFunction"));
        Assert.assertEquals(getFunction, LambdaOperations.valueOf("getFunction"));
        Assert.assertEquals(listFunctions, LambdaOperations.valueOf("listFunctions"));
        Assert.assertEquals(invokeFunction, LambdaOperations.valueOf("invokeFunction"));
        Assert.assertEquals(deleteFunction, LambdaOperations.valueOf("deleteFunction"));
        Assert.assertEquals(updateFunction, LambdaOperations.valueOf("updateFunction"));
        Assert.assertEquals(createEventSourceMapping, LambdaOperations.valueOf("createEventSourceMapping"));
        Assert.assertEquals(deleteEventSourceMapping, LambdaOperations.valueOf("deleteEventSourceMapping"));
        Assert.assertEquals(listEventSourceMapping, LambdaOperations.valueOf("listEventSourceMapping"));
        Assert.assertEquals(listTags, LambdaOperations.valueOf("listTags"));
        Assert.assertEquals(tagResource, LambdaOperations.valueOf("tagResource"));
        Assert.assertEquals(untagResource, LambdaOperations.valueOf("untagResource"));
        Assert.assertEquals(publishVersion, LambdaOperations.valueOf("publishVersion"));
        Assert.assertEquals(listVersions, LambdaOperations.valueOf("listVersions"));
    }

    @Test
    public void testToString() {
        Assert.assertEquals(createFunction.toString(), "createFunction");
        Assert.assertEquals(getFunction.toString(), "getFunction");
        Assert.assertEquals(listFunctions.toString(), "listFunctions");
        Assert.assertEquals(invokeFunction.toString(), "invokeFunction");
        Assert.assertEquals(deleteFunction.toString(), "deleteFunction");
        Assert.assertEquals(updateFunction.toString(), "updateFunction");
        Assert.assertEquals(createEventSourceMapping.toString(), "createEventSourceMapping");
        Assert.assertEquals(deleteEventSourceMapping.toString(), "deleteEventSourceMapping");
        Assert.assertEquals(listEventSourceMapping.toString(), "listEventSourceMapping");
        Assert.assertEquals(listTags.toString(), "listTags");
        Assert.assertEquals(tagResource.toString(), "tagResource");
        Assert.assertEquals(untagResource.toString(), "untagResource");
        Assert.assertEquals(publishVersion.toString(), "publishVersion");
        Assert.assertEquals(listVersions.toString(), "listVersions");
    }
}

