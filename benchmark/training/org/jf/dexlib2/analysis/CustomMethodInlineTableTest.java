/**
 * Copyright 2013, Google Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *     * Neither the name of Google Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.jf.dexlib2.analysis;


import AccessFlags.PRIVATE;
import AccessFlags.PUBLIC;
import AccessFlags.STATIC;
import Opcode.INVOKE_DIRECT;
import Opcode.INVOKE_STATIC;
import Opcode.INVOKE_VIRTUAL;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.DexFile;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.jf.dexlib2.immutable.ImmutableMethod;
import org.jf.dexlib2.immutable.ImmutableMethodImplementation;
import org.jf.dexlib2.immutable.instruction.ImmutableInstruction;
import org.junit.Assert;
import org.junit.Test;

import static ClassPath.NOT_ART;


public class CustomMethodInlineTableTest {
    @Test
    public void testCustomMethodInlineTable_Virtual() throws IOException {
        List<ImmutableInstruction> instructions = Lists.newArrayList(new org.jf.dexlib2.immutable.instruction.ImmutableInstruction35mi(Opcode.EXECUTE_INLINE, 1, 0, 0, 0, 0, 0, 0), new org.jf.dexlib2.immutable.instruction.ImmutableInstruction10x(Opcode.RETURN_VOID));
        ImmutableMethodImplementation methodImpl = new ImmutableMethodImplementation(1, instructions, null, null);
        ImmutableMethod method = new ImmutableMethod("Lblah;", "blah", null, "V", PUBLIC.getValue(), null, methodImpl);
        ClassDef classDef = new org.jf.dexlib2.immutable.ImmutableClassDef("Lblah;", PUBLIC.getValue(), "Ljava/lang/Object;", null, null, null, null, null, null, ImmutableList.of(method));
        DexFile dexFile = new org.jf.dexlib2.immutable.ImmutableDexFile(Opcodes.getDefault(), ImmutableList.of(classDef));
        ClassPathResolver resolver = new ClassPathResolver(ImmutableList.<String>of(), ImmutableList.<String>of(), ImmutableList.<String>of(), dexFile);
        ClassPath classPath = new ClassPath(resolver.getResolvedClassProviders(), false, NOT_ART);
        InlineMethodResolver inlineMethodResolver = new CustomInlineMethodResolver(classPath, "Lblah;->blah()V");
        MethodAnalyzer methodAnalyzer = new MethodAnalyzer(classPath, method, inlineMethodResolver, false);
        Instruction deodexedInstruction = methodAnalyzer.getInstructions().get(0);
        Assert.assertEquals(INVOKE_VIRTUAL, deodexedInstruction.getOpcode());
        MethodReference methodReference = ((MethodReference) (getReference()));
        Assert.assertEquals(method, methodReference);
    }

    @Test
    public void testCustomMethodInlineTable_Static() throws IOException {
        List<ImmutableInstruction> instructions = Lists.newArrayList(new org.jf.dexlib2.immutable.instruction.ImmutableInstruction35mi(Opcode.EXECUTE_INLINE, 1, 0, 0, 0, 0, 0, 0), new org.jf.dexlib2.immutable.instruction.ImmutableInstruction10x(Opcode.RETURN_VOID));
        ImmutableMethodImplementation methodImpl = new ImmutableMethodImplementation(1, instructions, null, null);
        ImmutableMethod method = new ImmutableMethod("Lblah;", "blah", null, "V", STATIC.getValue(), null, methodImpl);
        ClassDef classDef = new org.jf.dexlib2.immutable.ImmutableClassDef("Lblah;", PUBLIC.getValue(), "Ljava/lang/Object;", null, null, null, null, null, ImmutableList.of(method), null);
        DexFile dexFile = new org.jf.dexlib2.immutable.ImmutableDexFile(Opcodes.getDefault(), ImmutableList.of(classDef));
        ClassPathResolver resolver = new ClassPathResolver(ImmutableList.<String>of(), ImmutableList.<String>of(), ImmutableList.<String>of(), dexFile);
        ClassPath classPath = new ClassPath(resolver.getResolvedClassProviders(), false, NOT_ART);
        InlineMethodResolver inlineMethodResolver = new CustomInlineMethodResolver(classPath, "Lblah;->blah()V");
        MethodAnalyzer methodAnalyzer = new MethodAnalyzer(classPath, method, inlineMethodResolver, false);
        Instruction deodexedInstruction = methodAnalyzer.getInstructions().get(0);
        Assert.assertEquals(INVOKE_STATIC, deodexedInstruction.getOpcode());
        MethodReference methodReference = ((MethodReference) (getReference()));
        Assert.assertEquals(method, methodReference);
    }

    @Test
    public void testCustomMethodInlineTable_Direct() throws IOException {
        List<ImmutableInstruction> instructions = Lists.newArrayList(new org.jf.dexlib2.immutable.instruction.ImmutableInstruction35mi(Opcode.EXECUTE_INLINE, 1, 0, 0, 0, 0, 0, 0), new org.jf.dexlib2.immutable.instruction.ImmutableInstruction10x(Opcode.RETURN_VOID));
        ImmutableMethodImplementation methodImpl = new ImmutableMethodImplementation(1, instructions, null, null);
        ImmutableMethod method = new ImmutableMethod("Lblah;", "blah", null, "V", PRIVATE.getValue(), null, methodImpl);
        ClassDef classDef = new org.jf.dexlib2.immutable.ImmutableClassDef("Lblah;", PUBLIC.getValue(), "Ljava/lang/Object;", null, null, null, null, null, ImmutableList.of(method), null);
        DexFile dexFile = new org.jf.dexlib2.immutable.ImmutableDexFile(Opcodes.getDefault(), ImmutableList.of(classDef));
        ClassPathResolver resolver = new ClassPathResolver(ImmutableList.<String>of(), ImmutableList.<String>of(), ImmutableList.<String>of(), dexFile);
        ClassPath classPath = new ClassPath(resolver.getResolvedClassProviders(), false, NOT_ART);
        InlineMethodResolver inlineMethodResolver = new CustomInlineMethodResolver(classPath, "Lblah;->blah()V");
        MethodAnalyzer methodAnalyzer = new MethodAnalyzer(classPath, method, inlineMethodResolver, false);
        Instruction deodexedInstruction = methodAnalyzer.getInstructions().get(0);
        Assert.assertEquals(INVOKE_DIRECT, deodexedInstruction.getOpcode());
        MethodReference methodReference = ((MethodReference) (getReference()));
        Assert.assertEquals(method, methodReference);
    }
}

