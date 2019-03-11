/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.optimizer.dag;


import org.apache.flink.api.common.operators.SemanticProperties;
import org.apache.flink.api.common.operators.SingleInputSemanticProperties;
import org.apache.flink.api.common.operators.base.GroupReduceOperatorBase;
import org.apache.flink.api.common.operators.util.FieldSet;
import org.apache.flink.configuration.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class GroupReduceNodeTest {
    @Test
    public void testGetSemanticProperties() {
        SingleInputSemanticProperties origProps = new SingleInputSemanticProperties();
        origProps.addForwardedField(0, 1);
        origProps.addForwardedField(2, 2);
        origProps.addForwardedField(3, 4);
        origProps.addForwardedField(6, 0);
        origProps.addReadFields(new FieldSet(0, 2, 4, 7));
        GroupReduceOperatorBase<?, ?, ?> op = Mockito.mock(GroupReduceOperatorBase.class);
        Mockito.when(op.getSemanticProperties()).thenReturn(origProps);
        Mockito.when(op.getKeyColumns(0)).thenReturn(new int[]{ 3, 2 });
        Mockito.when(op.getParameters()).thenReturn(new Configuration());
        GroupReduceNode node = new GroupReduceNode(op);
        SemanticProperties filteredProps = node.getSemanticPropertiesForLocalPropertyFiltering();
        Assert.assertTrue(((filteredProps.getForwardingTargetFields(0, 0).size()) == 0));
        Assert.assertTrue(((filteredProps.getForwardingTargetFields(0, 2).size()) == 1));
        Assert.assertTrue(filteredProps.getForwardingTargetFields(0, 2).contains(2));
        Assert.assertTrue(((filteredProps.getForwardingTargetFields(0, 3).size()) == 1));
        Assert.assertTrue(filteredProps.getForwardingTargetFields(0, 3).contains(4));
        Assert.assertTrue(((filteredProps.getForwardingTargetFields(0, 6).size()) == 0));
        Assert.assertTrue(((filteredProps.getForwardingSourceField(0, 1)) < 0));
        Assert.assertTrue(((filteredProps.getForwardingSourceField(0, 2)) == 2));
        Assert.assertTrue(((filteredProps.getForwardingSourceField(0, 4)) == 3));
        Assert.assertTrue(((filteredProps.getForwardingSourceField(0, 0)) < 0));
        Assert.assertTrue(((filteredProps.getReadFields(0).size()) == 4));
        Assert.assertTrue(filteredProps.getReadFields(0).contains(0));
        Assert.assertTrue(filteredProps.getReadFields(0).contains(2));
        Assert.assertTrue(filteredProps.getReadFields(0).contains(4));
        Assert.assertTrue(filteredProps.getReadFields(0).contains(7));
    }
}

