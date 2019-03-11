package com.querydsl.apt.domain;


import QQueryInit6Test_Content.content.container.packaging;
import QQueryInit6Test_Content.content.container.packaging.id;
import com.querydsl.core.annotations.PropertyType;
import com.querydsl.core.annotations.QueryInit;
import com.querydsl.core.annotations.QueryType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.proxy.HibernateProxy;
import org.junit.Assert;
import org.junit.Test;

import static CascadeType.ALL;
import static FetchType.LAZY;
import static InheritanceType.TABLE_PER_CLASS;


public class QueryInit6Test {
    @Entity(name = QueryInit6Test.Component.NAME)
    @Inheritance(strategy = TABLE_PER_CLASS)
    public abstract static class Component implements Serializable {
        public static final String NAME = "Component";

        @Id
        protected String id;

        @ManyToOne(fetch = LAZY)
        @JoinColumn(name = "parent_id")
        private QueryInit6Test.Component parent;

        @QueryType(PropertyType.ENTITY)
        @QueryInit("*")
        @Transient
        public QueryInit6Test.Container getContainer() {
            QueryInit6Test.Component temp = this.parent;
            if ((this.parent) instanceof HibernateProxy) {
                temp = ((QueryInit6Test.Component) (getHibernateLazyInitializer().getImplementation()));
            }
            if (temp instanceof QueryInit6Test.Container) {
                return ((QueryInit6Test.Container) (temp));
            } else {
                if (!(temp.isRoot())) {
                    return temp.getParent().getContainer();
                } else {
                    return null;
                }
            }
        }

        @OneToMany(mappedBy = "parent", cascade = ALL, fetch = LAZY)
        private Set<QueryInit6Test.Component> children;

        protected Component() {
        }

        protected Component(String id, QueryInit6Test.Component parent) {
            this.id = id;
            this.parent = parent;
            this.children = new HashSet<QueryInit6Test.Component>();
        }

        @Transient
        public boolean isRoot() {
            return (parent) == null;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Set<QueryInit6Test.Component> getChildren() {
            return children;
        }

        public void setChildren(Set<QueryInit6Test.Component> children) {
            this.children = children;
        }

        public QueryInit6Test.Component getParent() {
            return parent;
        }

        public void setParent(QueryInit6Test.Component parent) {
            this.parent = parent;
        }
    }

    @Entity(name = QueryInit6Test.Content.NAME)
    public static class Content extends QueryInit6Test.Component {
        public static final String NAME = "Content";

        @Column(name = "quantity")
        private long quantity;

        public Content() {
            super(null, null);
        }

        public Content(String id, QueryInit6Test.Component parent) {
            super(id, parent);
            this.quantity = 0;
        }

        @Override
        public String toString() {
            return ((("Content [id=" + (id)) + " qty=") + (quantity)) + "]";
        }

        public long getQuantity() {
            return quantity;
        }

        public void setQuantity(long quantity) {
            this.quantity = quantity;
        }
    }

    @Entity(name = QueryInit6Test.Container.NAME)
    public static class Container extends QueryInit6Test.Component {
        public static final String NAME = "Container";

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "packaging_id")
        private QueryInit6Test.Packaging packaging;

        public Container() {
            super(null, null);
        }

        public Container(String id, QueryInit6Test.Component parent) {
            super(id, parent);
            this.packaging = null;
        }

        public QueryInit6Test.Packaging getPackaging() {
            return packaging;
        }

        public void setPackaging(QueryInit6Test.Packaging packaging) {
            this.packaging = packaging;
        }
    }

    @Entity(name = QueryInit6Test.Packaging.NAME)
    public static class Packaging implements Serializable {
        public static final String NAME = "Packaging";

        @Id
        private String id;

        @Column(name = "description")
        private String description;

        public Packaging() {
        }

        public Packaging(String id, String description) {
            this.id = id;
            this.description = description;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    @Test
    public void test() {
        Assert.assertNotNull(packaging);
        Assert.assertNotNull(id);
    }
}

