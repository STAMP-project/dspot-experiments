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
package org.apache.camel.component.aws.ec2;


import EC2Operations.createAndRunInstances;
import EC2Operations.createTags;
import EC2Operations.deleteTags;
import EC2Operations.describeInstances;
import EC2Operations.describeInstancesStatus;
import EC2Operations.monitorInstances;
import EC2Operations.rebootInstances;
import EC2Operations.startInstances;
import EC2Operations.stopInstances;
import EC2Operations.terminateInstances;
import EC2Operations.unmonitorInstances;
import org.junit.Assert;
import org.junit.Test;


public class EC2OperationsTest {
    @Test
    public void supportedOperationCount() {
        Assert.assertEquals(11, EC2Operations.values().length);
    }

    @Test
    public void valueOf() {
        Assert.assertEquals(createAndRunInstances, EC2Operations.valueOf("createAndRunInstances"));
        Assert.assertEquals(startInstances, EC2Operations.valueOf("startInstances"));
        Assert.assertEquals(stopInstances, EC2Operations.valueOf("stopInstances"));
        Assert.assertEquals(terminateInstances, EC2Operations.valueOf("terminateInstances"));
        Assert.assertEquals(describeInstances, EC2Operations.valueOf("describeInstances"));
        Assert.assertEquals(describeInstancesStatus, EC2Operations.valueOf("describeInstancesStatus"));
        Assert.assertEquals(rebootInstances, EC2Operations.valueOf("rebootInstances"));
        Assert.assertEquals(monitorInstances, EC2Operations.valueOf("monitorInstances"));
        Assert.assertEquals(unmonitorInstances, EC2Operations.valueOf("unmonitorInstances"));
        Assert.assertEquals(createTags, EC2Operations.valueOf("createTags"));
        Assert.assertEquals(deleteTags, EC2Operations.valueOf("deleteTags"));
    }

    @Test
    public void testToString() {
        Assert.assertEquals(createAndRunInstances.toString(), "createAndRunInstances");
        Assert.assertEquals(startInstances.toString(), "startInstances");
        Assert.assertEquals(stopInstances.toString(), "stopInstances");
        Assert.assertEquals(terminateInstances.toString(), "terminateInstances");
        Assert.assertEquals(describeInstances.toString(), "describeInstances");
        Assert.assertEquals(describeInstancesStatus.toString(), "describeInstancesStatus");
        Assert.assertEquals(rebootInstances.toString(), "rebootInstances");
        Assert.assertEquals(monitorInstances.toString(), "monitorInstances");
        Assert.assertEquals(unmonitorInstances.toString(), "unmonitorInstances");
        Assert.assertEquals(createTags.toString(), "createTags");
        Assert.assertEquals(deleteTags.toString(), "deleteTags");
    }
}

