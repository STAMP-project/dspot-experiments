package cn.hutool.core.clone;


import org.junit.Assert;
import org.junit.Test;


/**
 * ??????
 *
 * @author Looly
 */
public class CloneTest {
    @Test
    public void cloneTest() {
        // ??Cloneable??
        CloneTest.Cat cat = new CloneTest.Cat();
        CloneTest.Cat cat2 = cat.clone();
        Assert.assertEquals(cat, cat2);
        // ??CloneSupport?
        CloneTest.Dog dog = new CloneTest.Dog();
        CloneTest.Dog dog2 = dog.clone();
        Assert.assertEquals(dog, dog2);
    }

    // ------------------------------------------------------------------------------- private Class for test
    /**
     * ????????Cloneable??
     *
     * @author Looly
     */
    private static class Cat implements Cloneable<CloneTest.Cat> {
        private String name = "miaomiao";

        private int age = 2;

        @Override
        public CloneTest.Cat clone() {
            try {
                return ((CloneTest.Cat) (super.clone()));
            } catch (CloneNotSupportedException e) {
                throw new CloneRuntimeException(e);
            }
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + (age);
            result = (prime * result) + ((name) == null ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if ((getClass()) != (obj.getClass())) {
                return false;
            }
            CloneTest.Cat other = ((CloneTest.Cat) (obj));
            if ((age) != (other.age)) {
                return false;
            }
            if ((name) == null) {
                if ((other.name) != null) {
                    return false;
                }
            } else
                if (!(name.equals(other.name))) {
                    return false;
                }

            return true;
        }
    }

    /**
     * ????????CloneSupport?
     *
     * @author Looly
     */
    private static class Dog extends CloneSupport<CloneTest.Dog> {
        private String name = "wangwang";

        private int age = 3;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + (age);
            result = (prime * result) + ((name) == null ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if ((getClass()) != (obj.getClass())) {
                return false;
            }
            CloneTest.Dog other = ((CloneTest.Dog) (obj));
            if ((age) != (other.age)) {
                return false;
            }
            if ((name) == null) {
                if ((other.name) != null) {
                    return false;
                }
            } else
                if (!(name.equals(other.name))) {
                    return false;
                }

            return true;
        }
    }
}

