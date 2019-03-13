package org.apache.dubbo.common.bytecode;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;


public class ClassGeneratorTest {
    @SuppressWarnings("unchecked")
    @Test
    public void testMain() throws Exception {
        Bean b = new Bean();
        Field fname = null;
        Field[] fs = Bean.class.getDeclaredFields();
        for (Field f : fs) {
            f.setAccessible(true);
            if (f.getName().equals("name"))
                fname = f;

        }
        ClassGenerator cg = ClassGenerator.newInstance();
        cg.setClassName(((Bean.class.getName()) + "$Builder"));
        cg.addInterface(Builder.class);
        cg.addField("public static java.lang.reflect.Field FNAME;");
        cg.addMethod((("public Object getName(" + (Bean.class.getName())) + " o){ boolean[][][] bs = new boolean[0][][]; return (String)FNAME.get($1); }"));
        cg.addMethod((("public void setName(" + (Bean.class.getName())) + " o, Object name){ FNAME.set($1, $2); }"));
        cg.addDefaultConstructor();
        Class<?> cl = cg.toClass();
        cl.getField("FNAME").set(null, fname);
        System.out.println(cl.getName());
        Builder<String> builder = ((Builder<String>) (cl.newInstance()));
        System.out.println(b.getName());
        builder.setName(b, "ok");
        System.out.println(b.getName());
    }

    @Test
    public void testMain0() throws Exception {
        Bean b = new Bean();
        Field fname = null;
        Field[] fs = Bean.class.getDeclaredFields();
        for (Field f : fs) {
            f.setAccessible(true);
            if (f.getName().equals("name"))
                fname = f;

        }
        ClassGenerator cg = ClassGenerator.newInstance();
        cg.setClassName(((Bean.class.getName()) + "$Builder2"));
        cg.addInterface(Builder.class);
        cg.addField("FNAME", ((Modifier.PUBLIC) | (Modifier.STATIC)), Field.class);
        cg.addMethod((("public Object getName(" + (Bean.class.getName())) + " o){ boolean[][][] bs = new boolean[0][][]; return (String)FNAME.get($1); }"));
        cg.addMethod((("public void setName(" + (Bean.class.getName())) + " o, Object name){ FNAME.set($1, $2); }"));
        cg.addDefaultConstructor();
        Class<?> cl = cg.toClass();
        cl.getField("FNAME").set(null, fname);
        System.out.println(cl.getName());
        Builder<String> builder = ((Builder<String>) (cl.newInstance()));
        System.out.println(b.getName());
        builder.setName(b, "ok");
        System.out.println(b.getName());
    }
}

