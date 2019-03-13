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
package soot.jimple;


import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import soot.G;
import soot.Main;


/**
 *
 *
 * @author Alex Betra,
 */
public class MethodHandleTest {
    @Test
    public void testConstant() throws Throwable {
        // First generate a classfile with a MethodHnadle
        ClassWriter cv = new ClassWriter(((ClassWriter.COMPUTE_FRAMES) | (ClassWriter.COMPUTE_MAXS)));
        cv.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC, "HelloMethodHandles", null, Type.getInternalName(Object.class), null);
        MethodVisitor mv = cv.visitMethod(((Opcodes.ACC_STATIC) | (Opcodes.ACC_PUBLIC)), "getSquareRoot", Type.getMethodDescriptor(Type.getType(MethodHandle.class)), null, null);
        mv.visitCode();
        mv.visitLdcInsn(new Handle(Opcodes.H_INVOKESTATIC, Type.getInternalName(Math.class), "sqrt", Type.getMethodDescriptor(Type.DOUBLE_TYPE, Type.DOUBLE_TYPE), false));
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitEnd();
        cv.visitEnd();
        File tempDir = Files.createTempDir();
        File classFile = new File(tempDir, "HelloMethodHandles.class");
        Files.write(cv.toByteArray(), classFile);
        G.reset();
        String[] commandLine = new String[]{ "-pp", "-cp", tempDir.getAbsolutePath(), "-O", "HelloMethodHandles" };
        System.out.println(("Command Line: " + (Arrays.toString(commandLine))));
        Main.main(commandLine);
        Class<?> clazz = validateClassFile("HelloMethodHandles");
        MethodHandle methodHandle = ((MethodHandle) (clazz.getMethod("getSquareRoot").invoke(null)));
        Assert.assertThat(((Double) (methodHandle.invoke(16.0))), CoreMatchers.equalTo(4.0));
    }

    @Test
    public void testInvoke() throws IOException, ClassNotFoundException {
        // First generate a classfile with a MethodHnadle
        ClassWriter cv = new ClassWriter(((ClassWriter.COMPUTE_FRAMES) | (ClassWriter.COMPUTE_MAXS)));
        cv.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC, "UniformDistribution", null, Type.getInternalName(Object.class), null);
        MethodVisitor mv = cv.visitMethod(((Opcodes.ACC_STATIC) | (Opcodes.ACC_PUBLIC)), "sample", /* rng method */
        /* max */
        Type.getMethodDescriptor(Type.DOUBLE_TYPE, Type.getType(MethodHandle.class), Type.DOUBLE_TYPE), null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);// load MethodHandle

        mv.visitInsn(Opcodes.ACONST_NULL);// null string... (just to test signatures with class names)

        // Call MethodHandle.invoke() with polymorphic signature: ()D
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(MethodHandle.class), "invoke", Type.getMethodDescriptor(Type.DOUBLE_TYPE, Type.getType(String.class)), false);
        mv.visitVarInsn(Opcodes.DLOAD, 1);
        mv.visitInsn(Opcodes.DMUL);
        mv.visitInsn(Opcodes.DRETURN);
        mv.visitEnd();
        cv.visitEnd();
        File tempDir = Files.createTempDir();
        File classFile = new File(tempDir, "UniformDistribution.class");
        Files.write(cv.toByteArray(), classFile);
        G.reset();
        String[] commandLine = new String[]{ "-pp", "-cp", tempDir.getAbsolutePath(), "-O", "UniformDistribution" };
        System.out.println(("Command Line: " + (Arrays.toString(commandLine))));
        Main.main(commandLine);
        validateClassFile("UniformDistribution");
    }
}

