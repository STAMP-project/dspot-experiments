/**
 * Copyright 2014 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.cache;


import ValueType.BYTE;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.teavm.model.BasicBlock;
import org.teavm.model.Instruction;
import org.teavm.model.Program;

import static BinaryOperation.ADD;
import static BinaryOperation.SUBTRACT;
import static NumericOperandType.INT;


public class ProgramIOTest {
    @Test
    public void emptyInstruction() {
        Program program = new Program();
        BasicBlock block = program.createBasicBlock();
        block.add(new EmptyInstruction());
        program = inputOutput(program);
        block = program.basicBlockAt(0);
        Assert.assertThat(block.instructionCount(), CoreMatchers.is(1));
        Assert.assertThat(block.getFirstInstruction(), CoreMatchers.instanceOf(EmptyInstruction.class));
    }

    @Test
    public void constants() {
        Program program = new Program();
        BasicBlock block = program.createBasicBlock();
        ClassConstantInstruction classConstInsn = new ClassConstantInstruction();
        classConstInsn.setConstant(BYTE);
        classConstInsn.setReceiver(program.createVariable());
        block.add(classConstInsn);
        NullConstantInstruction nullConstInsn = new NullConstantInstruction();
        nullConstInsn.setReceiver(program.createVariable());
        block.add(nullConstInsn);
        IntegerConstantInstruction intConsInsn = new IntegerConstantInstruction();
        intConsInsn.setReceiver(program.createVariable());
        intConsInsn.setConstant(23);
        block.add(intConsInsn);
        LongConstantInstruction longConsInsn = new LongConstantInstruction();
        longConsInsn.setReceiver(program.createVariable());
        longConsInsn.setConstant(234);
        block.add(longConsInsn);
        FloatConstantInstruction floatConsInsn = new FloatConstantInstruction();
        floatConsInsn.setReceiver(program.createVariable());
        floatConsInsn.setConstant(3.14F);
        block.add(floatConsInsn);
        DoubleConstantInstruction doubleConsInsn = new DoubleConstantInstruction();
        doubleConsInsn.setReceiver(program.createVariable());
        doubleConsInsn.setConstant(3.14159);
        block.add(doubleConsInsn);
        StringConstantInstruction stringConsInsn = new StringConstantInstruction();
        stringConsInsn.setReceiver(program.createVariable());
        stringConsInsn.setConstant("foo");
        block.add(stringConsInsn);
        program = inputOutput(program);
        block = program.basicBlockAt(0);
        Assert.assertThat(block.instructionCount(), CoreMatchers.is(7));
        Instruction insn = block.getFirstInstruction();
        Assert.assertThat(insn, CoreMatchers.instanceOf(ClassConstantInstruction.class));
        insn = insn.getNext();
        Assert.assertThat(insn, CoreMatchers.instanceOf(NullConstantInstruction.class));
        insn = insn.getNext();
        Assert.assertThat(insn, CoreMatchers.instanceOf(IntegerConstantInstruction.class));
        insn = insn.getNext();
        Assert.assertThat(insn, CoreMatchers.instanceOf(LongConstantInstruction.class));
        insn = insn.getNext();
        Assert.assertThat(insn, CoreMatchers.instanceOf(FloatConstantInstruction.class));
        insn = insn.getNext();
        Assert.assertThat(insn, CoreMatchers.instanceOf(DoubleConstantInstruction.class));
        insn = insn.getNext();
        Assert.assertThat(insn, CoreMatchers.instanceOf(StringConstantInstruction.class));
        insn = block.getFirstInstruction();
        classConstInsn = ((ClassConstantInstruction) (insn));
        Assert.assertThat(classConstInsn.getReceiver().getIndex(), CoreMatchers.is(0));
        Assert.assertThat(classConstInsn.getConstant().toString(), CoreMatchers.is(BYTE.toString()));
        insn = insn.getNext();
        nullConstInsn = ((NullConstantInstruction) (insn));
        Assert.assertThat(nullConstInsn.getReceiver().getIndex(), CoreMatchers.is(1));
        insn = insn.getNext();
        intConsInsn = ((IntegerConstantInstruction) (insn));
        Assert.assertThat(intConsInsn.getConstant(), CoreMatchers.is(23));
        Assert.assertThat(intConsInsn.getReceiver().getIndex(), CoreMatchers.is(2));
        insn = insn.getNext();
        longConsInsn = ((LongConstantInstruction) (insn));
        Assert.assertThat(longConsInsn.getConstant(), CoreMatchers.is(234L));
        Assert.assertThat(longConsInsn.getReceiver().getIndex(), CoreMatchers.is(3));
        insn = insn.getNext();
        floatConsInsn = ((FloatConstantInstruction) (insn));
        Assert.assertThat(floatConsInsn.getConstant(), CoreMatchers.is(3.14F));
        Assert.assertThat(floatConsInsn.getReceiver().getIndex(), CoreMatchers.is(4));
        insn = insn.getNext();
        doubleConsInsn = ((DoubleConstantInstruction) (insn));
        Assert.assertThat(doubleConsInsn.getConstant(), CoreMatchers.is(3.14159));
        Assert.assertThat(doubleConsInsn.getReceiver().getIndex(), CoreMatchers.is(5));
        insn = insn.getNext();
        stringConsInsn = ((StringConstantInstruction) (insn));
        Assert.assertThat(stringConsInsn.getConstant(), CoreMatchers.is("foo"));
        Assert.assertThat(stringConsInsn.getReceiver().getIndex(), CoreMatchers.is(6));
    }

    @Test
    public void binaryOperation() {
        Program program = new Program();
        BasicBlock block = program.createBasicBlock();
        IntegerConstantInstruction constInsn = new IntegerConstantInstruction();
        constInsn.setConstant(3);
        constInsn.setReceiver(program.createVariable());
        block.add(constInsn);
        constInsn = new IntegerConstantInstruction();
        constInsn.setConstant(2);
        constInsn.setReceiver(program.createVariable());
        block.add(constInsn);
        BinaryInstruction addInsn = new BinaryInstruction(ADD, INT);
        addInsn.setFirstOperand(program.variableAt(0));
        addInsn.setSecondOperand(program.variableAt(1));
        addInsn.setReceiver(program.createVariable());
        block.add(addInsn);
        BinaryInstruction subInsn = new BinaryInstruction(SUBTRACT, INT);
        subInsn.setFirstOperand(program.variableAt(2));
        subInsn.setSecondOperand(program.variableAt(0));
        subInsn.setReceiver(program.createVariable());
        block.add(subInsn);
        Assert.assertThat(block.instructionCount(), CoreMatchers.is(4));
    }
}

