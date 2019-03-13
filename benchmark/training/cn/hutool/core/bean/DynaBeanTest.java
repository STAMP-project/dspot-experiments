package cn.hutool.core.bean;


import org.junit.Assert;
import org.junit.Test;


/**
 * {@link DynaBean}????
 *
 * @author Looly
 */
public class DynaBeanTest {
    @Test
    public void beanTest() {
        DynaBeanTest.User user = new DynaBeanTest.User();
        DynaBean bean = DynaBean.create(user);
        bean.set("name", "??");
        bean.set("age", 12);
        String name = bean.get("name");
        Assert.assertEquals(user.getName(), name);
        int age = bean.get("age");
        Assert.assertEquals(user.getAge(), age);
        // ??????
        DynaBean bean2 = new DynaBean(bean);
        DynaBeanTest.User user2 = bean2.getBean();
        Assert.assertEquals(user, user2);
        // ??????
        Object invoke = bean2.invoke("testMethod");
        Assert.assertEquals("test for ??", invoke);
    }

    public static class User {
        private String name;

        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String testMethod() {
            return "test for " + (this.name);
        }

        @Override
        public String toString() {
            return ((("User [name=" + (name)) + ", age=") + (age)) + "]";
        }
    }
}

