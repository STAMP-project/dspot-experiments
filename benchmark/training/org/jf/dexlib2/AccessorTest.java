/**
 * Copyright 2012, Google Inc.
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
package org.jf.dexlib2;


import SyntheticAccessorResolver.ADD_ASSIGNMENT;
import SyntheticAccessorResolver.AND_ASSIGNMENT;
import SyntheticAccessorResolver.AccessedMember;
import SyntheticAccessorResolver.DIV_ASSIGNMENT;
import SyntheticAccessorResolver.MUL_ASSIGNMENT;
import SyntheticAccessorResolver.OR_ASSIGNMENT;
import SyntheticAccessorResolver.POSTFIX_DECREMENT;
import SyntheticAccessorResolver.POSTFIX_INCREMENT;
import SyntheticAccessorResolver.PREFIX_DECREMENT;
import SyntheticAccessorResolver.PREFIX_INCREMENT;
import SyntheticAccessorResolver.REM_ASSIGNMENT;
import SyntheticAccessorResolver.SHL_ASSIGNMENT;
import SyntheticAccessorResolver.SHR_ASSIGNMENT;
import SyntheticAccessorResolver.SUB_ASSIGNMENT;
import SyntheticAccessorResolver.USHR_ASSIGNMENT;
import SyntheticAccessorResolver.XOR_ASSIGNMENT;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.Assert;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.DexFile;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.MethodImplementation;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.jf.dexlib2.util.SyntheticAccessorResolver;
import org.junit.Test;

import static Opcode.INVOKE_STATIC;
import static Opcode.INVOKE_STATIC_RANGE;


public class AccessorTest {
    private Pattern accessorMethodPattern = Pattern.compile("([a-zA-Z]*)_([a-zA-Z]*)");

    private static final Map<String, Integer> operationTypes;

    static {
        ImmutableMap.Builder<String, Integer> builder = ImmutableMap.builder();
        builder.put("postinc", POSTFIX_INCREMENT);
        builder.put("preinc", PREFIX_INCREMENT);
        builder.put("postdec", POSTFIX_DECREMENT);
        builder.put("predec", PREFIX_DECREMENT);
        builder.put("add", ADD_ASSIGNMENT);
        builder.put("sub", SUB_ASSIGNMENT);
        builder.put("mul", MUL_ASSIGNMENT);
        builder.put("div", DIV_ASSIGNMENT);
        builder.put("rem", REM_ASSIGNMENT);
        builder.put("and", AND_ASSIGNMENT);
        builder.put("or", OR_ASSIGNMENT);
        builder.put("xor", XOR_ASSIGNMENT);
        builder.put("shl", SHL_ASSIGNMENT);
        builder.put("shr", SHR_ASSIGNMENT);
        builder.put("ushr", USHR_ASSIGNMENT);
        operationTypes = builder.build();
    }

    @Test
    public void testAccessors() throws IOException {
        URL url = AccessorTest.class.getClassLoader().getResource("accessorTest.dex");
        Assert.assertNotNull(url);
        DexFile f = DexFileFactory.loadDexFile(url.getFile(), Opcodes.getDefault());
        SyntheticAccessorResolver sar = new SyntheticAccessorResolver(f.getOpcodes(), f.getClasses());
        ClassDef accessorTypesClass = null;
        ClassDef accessorsClass = null;
        for (ClassDef classDef : f.getClasses()) {
            String className = classDef.getType();
            if (className.equals("Lorg/jf/dexlib2/AccessorTypes;")) {
                accessorTypesClass = classDef;
            } else
                if (className.equals("Lorg/jf/dexlib2/AccessorTypes$Accessors;")) {
                    accessorsClass = classDef;
                }

        }
        Assert.assertNotNull(accessorTypesClass);
        Assert.assertNotNull(accessorsClass);
        for (Method method : accessorsClass.getMethods()) {
            Matcher m = accessorMethodPattern.matcher(method.getName());
            if (!(m.matches())) {
                continue;
            }
            String type = m.group(1);
            String operation = m.group(2);
            MethodImplementation methodImpl = method.getImplementation();
            Assert.assertNotNull(methodImpl);
            for (Instruction instruction : methodImpl.getInstructions()) {
                Opcode opcode = instruction.getOpcode();
                if ((opcode == (INVOKE_STATIC)) || (opcode == (INVOKE_STATIC_RANGE))) {
                    MethodReference accessorMethod = ((MethodReference) (getReference()));
                    SyntheticAccessorResolver.AccessedMember accessedMember = sar.getAccessedMember(accessorMethod);
                    Assert.assertNotNull(String.format("Could not resolve accessor for %s_%s", type, operation), accessedMember);
                    int operationType = AccessorTest.operationTypes.get(operation);
                    Assert.assertEquals(operationType, accessedMember.accessedMemberType);
                    Assert.assertEquals(String.format("%s_val", type), getName());
                }
            }
        }
    }
}

