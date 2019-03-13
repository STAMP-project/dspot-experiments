package com.querydsl.apt.domain;


import com.querydsl.core.annotations.QueryInit;
import java.util.Collection;
import org.junit.Test;

import static CascadeType.PERSIST;
import static QDeepInitializationTest_Parent.parent;


public class DeepInitializationTest {
    @MappedSuperclass
    public abstract static class AbstractEntity implements Cloneable {
        @Id
        @Column(name = "ID")
        @GeneratedValue(generator = "SEQUENCE")
        private long id;

        public long getId() {
            return id;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    @Entity
    @SequenceGenerator(name = "SEQUENCE", sequenceName = "PARENT_SEQUENCE")
    public static class Parent extends DeepInitializationTest.AbstractEntity {
        @JoinColumn(name = "FK_PARENT_ID", nullable = false)
        @OneToMany(cascade = PERSIST)
        @QueryInit("subChild.*")
        private Collection<DeepInitializationTest.Child> children;

        public Parent() {
        }

        public Collection<DeepInitializationTest.Child> getChildren() {
            return children;
        }

        public void setChildren(Collection<DeepInitializationTest.Child> children) {
            this.children = children;
        }
    }

    @Entity
    @SequenceGenerator(name = "SEQUENCE", sequenceName = "CHILD_SEQUENCE")
    public static class Child extends DeepInitializationTest.AbstractEntity {
        @OneToOne
        @JoinColumn(name = "FK_SUBCHILD_ID", referencedColumnName = "ID")
        private DeepInitializationTest.SubChild subChild;

        public Child() {
        }

        public DeepInitializationTest.SubChild getSubChild() {
            return subChild;
        }

        public void setSubChild(DeepInitializationTest.SubChild subChild) {
            this.subChild = subChild;
        }
    }

    @Entity
    @SequenceGenerator(name = "SEQUENCE", sequenceName = "SUBCHILD_SEQUENCE")
    public static class SubChild extends DeepInitializationTest.AbstractEntity {
        @Embedded
        private DeepInitializationTest.MyEmbeddable myEmbeddable;

        public SubChild() {
        }

        public DeepInitializationTest.MyEmbeddable getMyEmbeddable() {
            return myEmbeddable;
        }

        public void setMyEmbeddable(DeepInitializationTest.MyEmbeddable myEmbeddable) {
            this.myEmbeddable = myEmbeddable;
        }
    }

    @Embeddable
    public static class MyEmbeddable {
        private String number;

        public MyEmbeddable() {
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }
    }

    @Test
    public void init_via_parent() {
        QDeepInitializationTest_Parent parent = parent;
        parent.children.any().subChild.myEmbeddable.number.eq("Test");
    }
}

