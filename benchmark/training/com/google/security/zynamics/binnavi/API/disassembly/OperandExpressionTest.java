/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.security.zynamics.binnavi.API.disassembly;


import ExpressionType.Register;
import ReferenceType.CALL_VIRTUAL;
import ReferenceType.DATA;
import com.google.security.zynamics.binnavi.disassembly.MockOperandTreeNode;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class OperandExpressionTest {
    @Test
    public void testConstructor() {
        final MockOperandTreeNode node = new MockOperandTreeNode();
        final OperandExpression expression = new OperandExpression(node);
        Assert.assertEquals(Register, expression.getType());
        Assert.assertEquals("Mock Replacement", expression.getReplacement());
        Assert.assertEquals("Mock Value", expression.toString());
    }

    @Test
    public void testReferences() throws CouldntDeleteException, CouldntSaveDataException {
        final MockOperandExpressionListener listener = new MockOperandExpressionListener();
        final MockOperandTreeNode node = new MockOperandTreeNode();
        final OperandExpression expression = new OperandExpression(node);
        expression.addListener(listener);
        final Reference reference1 = expression.addReference(new Address(291), CALL_VIRTUAL);
        final Reference reference2 = expression.addReference(new Address(292), DATA);
        final List<Reference> references = expression.getReferences();
        Assert.assertEquals(2, references.size());
        Assert.assertEquals(reference1, references.get(0));
        Assert.assertEquals(reference2, references.get(1));
        Assert.assertEquals("addedReference;addedReference;", listener.events);
        expression.deleteReference(reference1);
        Assert.assertEquals(1, expression.getReferences().size());
        Assert.assertEquals("addedReference;addedReference;removedReference;", listener.events);
        expression.removeListener(listener);
    }
}

