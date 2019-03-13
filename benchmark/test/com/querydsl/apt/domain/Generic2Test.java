package com.querydsl.apt.domain;


import QGeneric2Test_Foo.foo;
import java.io.Serializable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import org.junit.Test;


public class Generic2Test extends AbstractTest {
    public static class Range<T extends Comparable<? super T>> {
        private T min;

        private T max;

        public T getMin() {
            return min;
        }

        public void setMin(T min) {
            this.min = min;
        }

        public T getMax() {
            return max;
        }

        public void setMax(T max) {
            this.max = max;
        }
    }

    @MappedSuperclass
    public abstract static class BaseEntity<T extends Comparable<? super T>> implements Serializable {
        @Embedded
        private Generic2Test.Range<T> range;

        public Generic2Test.Range<T> getRange() {
            return range;
        }

        public void setRange(Generic2Test.Range<T> range) {
            this.range = range;
        }
    }

    @Entity
    public static class Foo extends Generic2Test.BaseEntity<Integer> {}

    @Test
    public void test() throws NoSuchFieldException {
        start(QGeneric2Test_Foo.class, foo);
        assertPresent("range");
        match(QGeneric2Test_Range.class, "range");
    }
}

