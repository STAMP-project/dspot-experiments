package com.alibaba.json.test;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.asm.ClassWriter;
import com.alibaba.fastjson.asm.MethodVisitor;
import com.alibaba.fastjson.asm.Opcodes;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.json.test.benchmark.encode.EishayEncode;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import junit.framework.TestCase;


public class TestASM extends TestCase implements Opcodes {
    // public void test_0() throws Exception {
    // 
    // ClassWriter cw = new ClassWriter(0);
    // cw.visit(V1_1, ACC_PUBLIC, "Example", null, "java/lang/Object", null);
    // 
    // MethodVisitor mw = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
    // mw.visitVarInsn(ALOAD, 0);
    // mw.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
    // mw.visitInsn(RETURN);
    // mw.visitMaxs(1, 1);
    // mw.visitEnd();
    // mw = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
    // mw.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
    // mw.visitLdcInsn("Hello world!");
    // mw.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
    // mw.visitInsn(RETURN);
    // mw.visitMaxs(2, 2);
    // mw.visitEnd();
    // byte[] code = cw.toByteArray();
    // FileOutputStream fos = new FileOutputStream("Example.class");
    // fos.write(code);
    // fos.close();
    // 
    // MyClassLoader loader = new MyClassLoader();
    // 
    // Class exampleClass = loader.defineClassF("Example", code, 0, code.length);
    // exampleClass.getMethods()[0].invoke(null, new Object[] { null });
    // }
    public void test_asm() throws Exception {
        String text = JSON.toJSONString(EishayEncode.mediaContent);
        System.out.println(text);
    }

    public void test_1() throws Exception {
        ClassWriter cw = new ClassWriter();
        cw.visit(V1_5, ((ACC_PUBLIC) + (ACC_SUPER)), "DateSerializer", "java/lang/Object", new String[]{ "com/alibaba/fastjson/serializer/ObjectSerializer" });
        MethodVisitor mw = new com.alibaba.fastjson.asm.MethodWriter(cw, ACC_PUBLIC, "<init>", "()V", null, null);
        mw.visitVarInsn(ALOAD, 0);
        mw.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        mw.visitInsn(RETURN);
        mw.visitMaxs(1, 1);
        mw.visitEnd();
        mw = new com.alibaba.fastjson.asm.MethodWriter(cw, ACC_PUBLIC, "write", "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;)V", null, new String[]{ "java/io/IOException" });
        mw.visitVarInsn(ALOAD, 1);// serializer

        mw.visitMethodInsn(INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JSONSerializer", "getWriter", "()Lcom/alibaba/fastjson/serializer/SerializeWriter;");
        mw.visitVarInsn(ASTORE, 3);// out

        mw.visitVarInsn(ALOAD, 2);// obj

        mw.visitTypeInsn(CHECKCAST, getCastType(TestASM.Entity.class));// serializer

        mw.visitVarInsn(ASTORE, 4);// obj

        mw.visitVarInsn(ALOAD, 3);// out

        mw.visitLdcInsn("{");
        mw.visitMethodInsn(INVOKEVIRTUAL, getCastType(SerializeWriter.class), "writeString", "(Ljava/lang/String;)V");
        mw.visitVarInsn(ALOAD, 3);// out

        mw.visitLdcInsn("\"id\":");
        mw.visitMethodInsn(INVOKEVIRTUAL, getCastType(SerializeWriter.class), "write", "(Ljava/lang/String;)V");
        mw.visitVarInsn(ALOAD, 3);// out

        mw.visitVarInsn(ALOAD, 4);// entity

        mw.visitMethodInsn(INVOKEVIRTUAL, getCastType(TestASM.Entity.class), "getId", "()I");
        mw.visitMethodInsn(INVOKEVIRTUAL, getCastType(SerializeWriter.class), "writeInt", "(I)V");
        mw.visitVarInsn(ALOAD, 3);// out

        mw.visitLdcInsn("\",name\":");
        mw.visitMethodInsn(INVOKEVIRTUAL, getCastType(SerializeWriter.class), "write", "(Ljava/lang/String;)V");
        mw.visitVarInsn(ALOAD, 3);// out

        mw.visitVarInsn(ALOAD, 4);// entity

        mw.visitMethodInsn(INVOKEVIRTUAL, getCastType(TestASM.Entity.class), "getName", "()Ljava/lang/String;");
        mw.visitMethodInsn(INVOKEVIRTUAL, getCastType(SerializeWriter.class), "writeString", "(Ljava/lang/String;)V");
        mw.visitInsn(RETURN);
        mw.visitMaxs(3, 16);
        mw.visitEnd();
        byte[] code = cw.toByteArray();
        FileOutputStream fos = new FileOutputStream("Example.class");
        fos.write(code);
        fos.close();
        TestASM.MyClassLoader loader = new TestASM.MyClassLoader(ObjectSerializer.class.getClassLoader());
        Class<?> exampleClass = loader.defineClassF("DateSerializer", code, 0, code.length);
        Method[] methods = exampleClass.getMethods();
        Object instance = exampleClass.newInstance();
        SerializeWriter out = new SerializeWriter();
        JSONSerializer serializer = new JSONSerializer(out);
        TestASM.Entity obj = new TestASM.Entity();
        methods[0].invoke(instance, serializer, obj);
        System.out.println(out.toString());
    }

    public static class Entity {
        private int id;

        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class MyClassLoader extends ClassLoader {
        public MyClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class<?> defineClassF(String name, byte[] b, int off, int len) throws ClassFormatError {
            return defineClass(name, b, off, len, null);
        }
    }

    public static class Foo {
        public void execute() {
            System.out.println("Hello World");
        }
    }
}

