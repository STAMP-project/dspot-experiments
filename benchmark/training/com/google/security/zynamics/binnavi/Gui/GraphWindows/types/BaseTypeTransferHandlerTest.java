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
package com.google.security.zynamics.binnavi.Gui.GraphWindows.types;


import ExpressionType.EXPRESSION_LIST;
import ExpressionType.IMMEDIATE_FLOAT;
import ExpressionType.IMMEDIATE_INTEGER;
import ExpressionType.MEMDEREF;
import ExpressionType.OPERATOR;
import ExpressionType.REGISTER;
import ExpressionType.SIZE_PREFIX;
import ExpressionType.SYMBOL;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.disassembly.INaviOperandTreeNode;
import com.google.security.zynamics.binnavi.disassembly.types.BaseType;
import com.google.security.zynamics.binnavi.disassembly.types.TypeManager;
import com.google.security.zynamics.binnavi.disassembly.types.TypeSubstitution;
import com.google.security.zynamics.binnavi.yfileswrap.Gui.GraphWindows.types.DragAndDropSupportWrapper;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import com.google.security.zynamics.zylib.disassembly.IAddress;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for {@link BaseTypeTransferHandler}.
 *
 *  Since we are testing a class instance that derives from a Java API (TransferHandler) we cannot
 * easily create stubs for all dependencies. Specifically, the argument to importData cannot be
 * stubbed since it's a final class. We pass null instead and stub the corresponding dependencies.
 */
@RunWith(JUnit4.class)
public class BaseTypeTransferHandlerTest {
    private DragAndDropSupportWrapper wrapper;

    private TypeManager manager;

    private INaviOperandTreeNode node;

    private BaseType baseType;

    private static final int POSITION = 1;

    private static final int OFFSET = 0;

    private static final IAddress ADDRESS = new CAddress(291);

    @Test
    public void testImportData_DropNodeHasNegativeAddend() {
        Mockito.when(wrapper.acceptDrop()).thenReturn(true);
        Mockito.when(node.determineAddendValue()).thenReturn(((long) (-1)));
        Assert.assertFalse(importData(null));
    }

    @Test
    public void testImportData_DropNodeHasNoSibling() {
        Mockito.when(wrapper.acceptDrop()).thenReturn(true);
        Mockito.when(node.hasAddendSibling()).thenReturn(false);
        Assert.assertFalse(importData(null));
    }

    @Test
    public void testImportData_DropNodeNull() {
        Mockito.when(wrapper.acceptDrop()).thenReturn(true);
        Mockito.when(wrapper.determineDropNode()).thenReturn(null);
        Assert.assertFalse(importData(null));
    }

    @Test
    public void testImportData_OperandNull() {
        Mockito.when(wrapper.acceptDrop()).thenReturn(true);
        Mockito.when(wrapper.determineDropNode()).thenReturn(null);
        Assert.assertFalse(importData(null));
    }

    @Test
    public void testImportData_OperandTypeNotRegister() {
        Mockito.when(wrapper.acceptDrop()).thenReturn(true);
        Mockito.when(wrapper.determineDropNode()).thenReturn(node);
        Mockito.when(node.getType()).thenReturn(EXPRESSION_LIST, IMMEDIATE_FLOAT, IMMEDIATE_INTEGER, MEMDEREF, OPERATOR, SIZE_PREFIX, SYMBOL);
        final BaseTypeTransferHandler handler = new BaseTypeTransferHandler(manager, wrapper);
        Assert.assertFalse(handler.importData(null));
        Assert.assertFalse(handler.importData(null));
        Assert.assertFalse(handler.importData(null));
        Assert.assertFalse(handler.importData(null));
        Assert.assertFalse(handler.importData(null));
        Assert.assertFalse(handler.importData(null));
        Assert.assertFalse(handler.importData(null));
    }

    @Test
    public void testImportData_SingleExpression() throws CouldntSaveDataException {
        Mockito.when(wrapper.acceptDrop()).thenReturn(true);
        Mockito.when(wrapper.determineDropNode()).thenReturn(node);
        Mockito.when(node.getType()).thenReturn(REGISTER);
        Mockito.when(node.getOperandPosition()).thenReturn(BaseTypeTransferHandlerTest.POSITION);
        Mockito.when(node.getInstructionAddress()).thenReturn(BaseTypeTransferHandlerTest.ADDRESS);
        Mockito.when(node.hasAddendSibling()).thenReturn(false);
        Mockito.when(node.getChildren()).thenReturn(new ArrayList<INaviOperandTreeNode>());
        final BaseTypeTransferHandler handler = new BaseTypeTransferHandler(manager, wrapper);
        handler.importData(null);
        Mockito.verify(manager).createTypeSubstitution(node, baseType, BaseTypeTransferHandlerTest.POSITION, BaseTypeTransferHandlerTest.OFFSET, BaseTypeTransferHandlerTest.ADDRESS);
    }

    @Test
    public void testImportData_SkipsImport() {
        Mockito.when(wrapper.acceptDrop()).thenReturn(false);
        Assert.assertFalse(importData(null));
    }

    @Test
    public void testImportData_SubstitutionCreated() throws CouldntSaveDataException {
        initSubstitutionStubs();
        final BaseTypeTransferHandler handler = new BaseTypeTransferHandler(manager, wrapper);
        handler.importData(null);
        Mockito.verify(manager).createTypeSubstitution(node, baseType, BaseTypeTransferHandlerTest.POSITION, BaseTypeTransferHandlerTest.OFFSET, BaseTypeTransferHandlerTest.ADDRESS);
    }

    @Test
    public void testImportData_SubstitutionUpdated() throws CouldntSaveDataException {
        initSubstitutionStubs();
        final TypeSubstitution substitution = Mockito.mock(TypeSubstitution.class);
        Mockito.when(node.getTypeSubstitution()).thenReturn(substitution);
        final BaseTypeTransferHandler handler = new BaseTypeTransferHandler(manager, wrapper);
        handler.importData(null);
        Mockito.verify(manager).updateTypeSubstitution(node, substitution, baseType, new ArrayList<com.google.security.zynamics.binnavi.disassembly.types.TypeMember>(), BaseTypeTransferHandlerTest.OFFSET);
    }
}

