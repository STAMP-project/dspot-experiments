/**
 * -
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vall?e-Rai and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package soot.baf;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import soot.IntType;
import soot.LongType;
import soot.Type;
import soot.Unit;
import soot.VoidType;
import soot.baf.internal.BNopInst;
import soot.util.backend.ASMBackendUtils;


@PrepareForTest(ASMBackendUtils.class)
@RunWith(PowerMockRunner.class)
public class ASMBackendMockingTest {
    private MethodVisitor mv;

    private BafASMBackend sut;

    @Test
    public void testNOPInst() {
        Inst inst = new BNopInst();
        sut.generateInstruction(mv, inst);
        Mockito.verify(mv).visitInsn(Opcodes.NOP);
    }

    @Test
    public void testJSRInst() {
        Unit target = mock(Inst.class);
        Inst inst = new soot.baf.internal.BJSRInst(target);
        Label label = mock(Label.class);
        when(sut.getBranchTargetLabel(target)).thenReturn(label);
        sut.generateInstruction(mv, inst);
        Mockito.verify(mv).visitJumpInsn(Opcodes.JSR, label);
    }

    @Test
    public void testSwapInst() {
        Type fromType = mock(Type.class);
        Type toType = mock(Type.class);
        Inst inst = new soot.baf.internal.BSwapInst(fromType, toType);
        sut.generateInstruction(mv, inst);
        Mockito.verify(mv).visitInsn(Opcodes.SWAP);
    }

    @Test
    public void testPop2Inst() {
        Type type = mock(LongType.class);
        PopInst inst = new soot.baf.internal.BPopInst(type);
        sut.generateInstruction(mv, inst);
        Mockito.verify(mv).visitInsn(Opcodes.POP2);
    }

    @Test
    public void testDup2Inst1() {
        Type aOp1Type = mock(IntType.class);
        Type aOp2Type = mock(IntType.class);
        Inst inst = new soot.baf.internal.BDup2Inst(aOp1Type, aOp2Type);
        sut.generateInstruction(mv, inst);
        Mockito.verify(mv).visitInsn(Opcodes.DUP2);
    }

    @Test
    public void testDup2Inst2() {
        Type aOp1Type = mock(LongType.class);
        Type aOp2Type = mock(LongType.class);
        Inst inst = new soot.baf.internal.BDup2Inst(aOp1Type, aOp2Type);
        sut.generateInstruction(mv, inst);
        Mockito.verify(mv, Mockito.times(2)).visitInsn(Opcodes.DUP2);
    }

    @Test
    public void testDup2Inst3() {
        Type aOp1Type = mock(LongType.class);
        Type aOp2Type = mock(IntType.class);
        Inst inst = new soot.baf.internal.BDup2Inst(aOp1Type, aOp2Type);
        sut.generateInstruction(mv, inst);
        InOrder inOrder = Mockito.inOrder(mv);
        inOrder.verify(mv).visitInsn(Opcodes.DUP2);
        inOrder.verify(mv).visitInsn(Opcodes.DUP);
    }

    @Test
    public void testDup2Inst4() {
        Type aOp1Type = mock(IntType.class);
        Type aOp2Type = mock(LongType.class);
        Inst inst = new soot.baf.internal.BDup2Inst(aOp1Type, aOp2Type);
        sut.generateInstruction(mv, inst);
        InOrder inOrder = Mockito.inOrder(mv);
        inOrder.verify(mv).visitInsn(Opcodes.DUP);
        inOrder.verify(mv).visitInsn(Opcodes.DUP2);
    }

    @Test
    public void testDup_x1Inst1() {
        Type aOpType = mock(IntType.class);
        Type aUnderType = mock(IntType.class);
        Inst inst = new soot.baf.internal.BDup1_x1Inst(aOpType, aUnderType);
        sut.generateInstruction(mv, inst);
        InOrder inOrder = Mockito.inOrder(mv);
        inOrder.verify(mv).visitInsn(Opcodes.DUP_X1);
    }

    @Test
    public void testDup_x1Inst2() {
        Type aOpType = mock(IntType.class);
        Type aUnderType = mock(LongType.class);
        Inst inst = new soot.baf.internal.BDup1_x1Inst(aOpType, aUnderType);
        sut.generateInstruction(mv, inst);
        InOrder inOrder = Mockito.inOrder(mv);
        inOrder.verify(mv).visitInsn(Opcodes.DUP_X2);
    }

    @Test
    public void testDup_x1Inst3() {
        Type aOpType = mock(LongType.class);
        Type aUnderType = mock(IntType.class);
        Inst inst = new soot.baf.internal.BDup1_x1Inst(aOpType, aUnderType);
        sut.generateInstruction(mv, inst);
        InOrder inOrder = Mockito.inOrder(mv);
        inOrder.verify(mv).visitInsn(Opcodes.DUP2_X1);
    }

    @Test
    public void testDup_x1Inst4() {
        Type aOpType = mock(LongType.class);
        Type aUnderType = mock(LongType.class);
        Inst inst = new soot.baf.internal.BDup1_x1Inst(aOpType, aUnderType);
        sut.generateInstruction(mv, inst);
        InOrder inOrder = Mockito.inOrder(mv);
        inOrder.verify(mv).visitInsn(Opcodes.DUP2_X2);
    }

    @Test
    public void testDup_x2Inst1() {
        Type aOpType = mock(IntType.class);
        Type aUnder1Type = mock(IntType.class);
        Type aUnder2Type = mock(IntType.class);
        Inst inst = new soot.baf.internal.BDup1_x2Inst(aOpType, aUnder1Type, aUnder2Type);
        sut.generateInstruction(mv, inst);
        InOrder inOrder = Mockito.inOrder(mv);
        inOrder.verify(mv).visitInsn(Opcodes.DUP_X2);
    }

    @Test(expected = RuntimeException.class)
    public void testDup_x2Inst2() {
        Type aOpType = mock(IntType.class);
        Type aUnder1Type = mock(LongType.class);
        Type aUnder2Type = mock(IntType.class);
        Inst inst = new soot.baf.internal.BDup1_x2Inst(aOpType, aUnder1Type, aUnder2Type);
        sut.generateInstruction(mv, inst);
    }

    @Test
    public void testDup_x2Inst3() {
        Type aOpType = mock(LongType.class);
        Type aUnder1Type = mock(IntType.class);
        Type aUnder2Type = mock(IntType.class);
        Inst inst = new soot.baf.internal.BDup1_x2Inst(aOpType, aUnder1Type, aUnder2Type);
        sut.generateInstruction(mv, inst);
        Mockito.verify(mv).visitInsn(Opcodes.DUP2_X2);
    }

    @Test(expected = RuntimeException.class)
    public void testDup_x2Inst4() {
        Type aOpType = mock(LongType.class);
        Type aUnder1Type = mock(LongType.class);
        Type aUnder2Type = mock(IntType.class);
        Inst inst = new soot.baf.internal.BDup1_x2Inst(aOpType, aUnder1Type, aUnder2Type);
        sut.generateInstruction(mv, inst);
    }

    @Test(expected = RuntimeException.class)
    public void testDup2_x1Inst1() {
        Type aOp1Type = mock(IntType.class);
        Type aOp2Type = mock(LongType.class);
        Type aUnderType = mock(IntType.class);
        Inst inst = new soot.baf.internal.BDup2_x1Inst(aOp1Type, aOp2Type, aUnderType);
        sut.generateInstruction(mv, inst);
    }

    @Test
    public void testDup2_x1Inst2() {
        Type aOp1Type = mock(IntType.class);
        Type aOp2Type = mock(IntType.class);
        Type aUnderType = mock(IntType.class);
        Inst inst = new soot.baf.internal.BDup2_x1Inst(aOp1Type, aOp2Type, aUnderType);
        sut.generateInstruction(mv, inst);
        InOrder inOrder = Mockito.inOrder(mv);
        inOrder.verify(mv).visitInsn(Opcodes.DUP2_X1);
    }

    @Test
    public void testDup2_x1Inst3() {
        Type aOp1Type = mock(IntType.class);
        Type aOp2Type = mock(IntType.class);
        Type aUnderType = mock(LongType.class);
        Inst inst = new soot.baf.internal.BDup2_x1Inst(aOp1Type, aOp2Type, aUnderType);
        sut.generateInstruction(mv, inst);
        InOrder inOrder = Mockito.inOrder(mv);
        inOrder.verify(mv).visitInsn(Opcodes.DUP2_X2);
    }

    @Test(expected = RuntimeException.class)
    public void testDup2_x2Inst1() {
        Type aOp1Type = mock(LongType.class);
        Type aOp2Type = mock(IntType.class);
        Type aUnder1Type = mock(IntType.class);
        Type aUnder2Type = mock(IntType.class);
        Inst inst = new soot.baf.internal.BDup2_x2Inst(aOp1Type, aOp2Type, aUnder1Type, aUnder2Type);
        sut.generateInstruction(mv, inst);
    }

    @Test(expected = RuntimeException.class)
    public void testDup2_x2Inst2() {
        Type aOp1Type = mock(IntType.class);
        Type aOp2Type = mock(IntType.class);
        Type aUnder1Type = mock(IntType.class);
        Type aUnder2Type = mock(LongType.class);
        Inst inst = new soot.baf.internal.BDup2_x2Inst(aOp1Type, aOp2Type, aUnder1Type, aUnder2Type);
        sut.generateInstruction(mv, inst);
    }

    @Test
    public void testDup2_x2Inst3() {
        Type aOp1Type = mock(IntType.class);
        Type aOp2Type = mock(IntType.class);
        Type aUnder1Type = mock(IntType.class);
        Type aUnder2Type = mock(IntType.class);
        Inst inst = new soot.baf.internal.BDup2_x2Inst(aOp1Type, aOp2Type, aUnder1Type, aUnder2Type);
        sut.generateInstruction(mv, inst);
        Mockito.verify(mv).visitInsn(Opcodes.DUP2_X2);
    }

    @Test(expected = RuntimeException.class)
    public void testDup2_x2Inst4() {
        Type aOp1Type = mock(IntType.class);
        Type aOp2Type = mock(VoidType.class);
        Type aUnder1Type = mock(IntType.class);
        Type aUnder2Type = mock(IntType.class);
        Inst inst = new soot.baf.internal.BDup2_x2Inst(aOp1Type, aOp2Type, aUnder1Type, aUnder2Type);
        sut.generateInstruction(mv, inst);
    }
}

