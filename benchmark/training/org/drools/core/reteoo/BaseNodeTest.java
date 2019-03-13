/**
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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
package org.drools.core.reteoo;


import RuleBasePartitionId.MAIN_PARTITION;
import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.UpdateContext;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.PropagationContext;
import org.junit.Assert;
import org.junit.Test;


public class BaseNodeTest {
    @Test
    public void testBaseNode() {
        BaseNodeTest.MockBaseNode node = new BaseNodeTest.MockBaseNode(10);
        Assert.assertEquals(10, getId());
        node = new BaseNodeTest.MockBaseNode(155);
        Assert.assertEquals(155, getId());
    }

    class MockBaseNode extends BaseNode {
        private static final long serialVersionUID = 510L;

        public MockBaseNode() {
        }

        public MockBaseNode(final int id) {
            super(id, MAIN_PARTITION, false);
        }

        public void ruleAttached() {
        }

        public void attach(BuildContext context) {
        }

        public void updateNewNode(final InternalWorkingMemory workingMemory, final PropagationContext context) {
        }

        protected boolean doRemove(final RuleRemovalContext context, final ReteooBuilder builder) {
            return true;
        }

        public boolean isInUse() {
            return true;
        }

        @Override
        public ObjectTypeNode getObjectTypeNode() {
            return null;
        }

        @Override
        public void networkUpdated(UpdateContext updateContext) {
        }

        public short getType() {
            return 0;
        }
    }
}

