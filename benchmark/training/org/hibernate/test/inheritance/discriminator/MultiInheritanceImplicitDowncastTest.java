/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.test.inheritance.discriminator;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Basic;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Christian Beikov
 */
public class MultiInheritanceImplicitDowncastTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testQueryingSingle() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            final String base = "from PolymorphicPropertyBase p left join ";
            s.createQuery((base + "p.base b left join b.relation1 ")).getResultList();
            s.createQuery((base + "p.base b left join b.relation2 ")).getResultList();
            s.createQuery((base + "p.baseEmbeddable.embeddedRelation1 b left join b.relation1")).getResultList();
            s.createQuery((base + "p.baseEmbeddable.embeddedRelation2 b left join b.relation2")).getResultList();
            s.createQuery((base + "p.baseEmbeddable.embeddedBase b left join b.relation1")).getResultList();
            s.createQuery((base + "p.baseEmbeddable.embeddedBase b left join b.relation2")).getResultList();
        });
    }

    @Test
    public void testQueryingMultiple() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            final String base = "from PolymorphicPropertyBase p left join ";
            s.createQuery((base + "p.base b left join b.relation1 left join b.relation2")).getResultList();
            s.createQuery((base + "p.base b left join b.relation2 left join b.relation1")).getResultList();
            s.createQuery((base + "p.baseEmbeddable.embeddedBase b left join b.relation1 left join b.relation2")).getResultList();
            s.createQuery((base + "p.baseEmbeddable.embeddedBase b left join b.relation2 left join b.relation1")).getResultList();
        });
    }

    @Test
    public void testMultiJoinAddition1() {
        testMultiJoinAddition("from PolymorphicPropertyBase p left join p.base b left join b.relation1");
    }

    @Test
    public void testMultiJoinAddition2() {
        testMultiJoinAddition("from PolymorphicPropertyBase p left join p.base b left join b.relation2");
    }

    @MappedSuperclass
    public abstract static class BaseEmbeddable<T extends MultiInheritanceImplicitDowncastTest.PolymorphicBase> implements Serializable {
        private static final long serialVersionUID = 1L;

        private String someName;

        private T embeddedBase;

        public BaseEmbeddable() {
        }

        public String getSomeName() {
            return someName;
        }

        public void setSomeName(String someName) {
            this.someName = someName;
        }

        @ManyToOne(fetch = FetchType.LAZY)
        public T getEmbeddedBase() {
            return embeddedBase;
        }

        public void setEmbeddedBase(T embeddedBase) {
            this.embeddedBase = embeddedBase;
        }
    }

    @Embeddable
    public abstract static class Embeddable1 extends MultiInheritanceImplicitDowncastTest.BaseEmbeddable<MultiInheritanceImplicitDowncastTest.PolymorphicSub1> {
        private static final long serialVersionUID = 1L;

        private String someName1;

        private MultiInheritanceImplicitDowncastTest.PolymorphicSub1 embeddedRelation1;

        public Embeddable1() {
        }

        public String getSomeName1() {
            return someName1;
        }

        public void setSomeName1(String someName1) {
            this.someName1 = someName1;
        }

        @ManyToOne(fetch = FetchType.LAZY)
        public MultiInheritanceImplicitDowncastTest.PolymorphicSub1 getEmbeddedRelation1() {
            return embeddedRelation1;
        }

        public void setEmbeddedRelation1(MultiInheritanceImplicitDowncastTest.PolymorphicSub1 embeddedRelation1) {
            this.embeddedRelation1 = embeddedRelation1;
        }
    }

    @Embeddable
    public abstract static class Embeddable2 extends MultiInheritanceImplicitDowncastTest.BaseEmbeddable<MultiInheritanceImplicitDowncastTest.PolymorphicSub2> {
        private static final long serialVersionUID = 1L;

        private String someName2;

        private MultiInheritanceImplicitDowncastTest.PolymorphicSub2 embeddedRelation2;

        public Embeddable2() {
        }

        public String getSomeName2() {
            return someName2;
        }

        public void setSomeName2(String someName2) {
            this.someName2 = someName2;
        }

        @ManyToOne(fetch = FetchType.LAZY)
        public MultiInheritanceImplicitDowncastTest.PolymorphicSub2 getEmbeddedRelation2() {
            return embeddedRelation2;
        }

        public void setEmbeddedRelation2(MultiInheritanceImplicitDowncastTest.PolymorphicSub2 embeddedRelation2) {
            this.embeddedRelation2 = embeddedRelation2;
        }
    }

    @Entity(name = "IntIdEntity")
    public static class IntIdEntity implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer id;

        private String name;

        public IntIdEntity() {
        }

        public IntIdEntity(String name) {
            this.name = name;
        }

        @Id
        @GeneratedValue
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @Basic(optional = false)
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + ((id) == null ? 0 : id.hashCode());
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
            MultiInheritanceImplicitDowncastTest.IntIdEntity other = ((MultiInheritanceImplicitDowncastTest.IntIdEntity) (obj));
            if ((id) == null) {
                if ((other.id) != null) {
                    return false;
                }
            } else
                if (!(id.equals(other.id))) {
                    return false;
                }

            return true;
        }
    }

    @Embeddable
    public static class NameObject implements Serializable {
        private String primaryName;

        private String secondaryName;

        private MultiInheritanceImplicitDowncastTest.IntIdEntity intIdEntity;

        public NameObject() {
        }

        public NameObject(String primaryName, String secondaryName) {
            this.primaryName = primaryName;
            this.secondaryName = secondaryName;
        }

        public String getPrimaryName() {
            return primaryName;
        }

        public void setPrimaryName(String primaryName) {
            this.primaryName = primaryName;
        }

        public String getSecondaryName() {
            return secondaryName;
        }

        public void setSecondaryName(String secondaryName) {
            this.secondaryName = secondaryName;
        }

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "name_object_int_id_entity")
        public MultiInheritanceImplicitDowncastTest.IntIdEntity getIntIdEntity() {
            return intIdEntity;
        }

        public void setIntIdEntity(MultiInheritanceImplicitDowncastTest.IntIdEntity intIdEntity) {
            this.intIdEntity = intIdEntity;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof MultiInheritanceImplicitDowncastTest.NameObject)) {
                return false;
            }
            MultiInheritanceImplicitDowncastTest.NameObject that = ((MultiInheritanceImplicitDowncastTest.NameObject) (o));
            if ((primaryName) != null ? !(primaryName.equals(that.primaryName)) : (that.primaryName) != null) {
                return false;
            }
            return (secondaryName) != null ? secondaryName.equals(that.secondaryName) : (that.secondaryName) == null;
        }

        @Override
        public int hashCode() {
            int result = ((primaryName) != null) ? primaryName.hashCode() : 0;
            result = (31 * result) + ((secondaryName) != null ? secondaryName.hashCode() : 0);
            return result;
        }
    }

    @Entity(name = "PolymorphicBase")
    @Inheritance(strategy = InheritanceType.JOINED)
    public abstract static class PolymorphicBase implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long id;

        private String name;

        private MultiInheritanceImplicitDowncastTest.PolymorphicBase parent;

        private List<MultiInheritanceImplicitDowncastTest.PolymorphicBase> list = new ArrayList<MultiInheritanceImplicitDowncastTest.PolymorphicBase>();

        private Set<MultiInheritanceImplicitDowncastTest.PolymorphicBase> children = new HashSet<MultiInheritanceImplicitDowncastTest.PolymorphicBase>();

        private Map<String, MultiInheritanceImplicitDowncastTest.PolymorphicBase> map = new HashMap<String, MultiInheritanceImplicitDowncastTest.PolymorphicBase>();

        public PolymorphicBase() {
        }

        @Id
        @GeneratedValue
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @ManyToOne(fetch = FetchType.LAZY, optional = true)
        public MultiInheritanceImplicitDowncastTest.PolymorphicBase getParent() {
            return parent;
        }

        public void setParent(MultiInheritanceImplicitDowncastTest.PolymorphicBase parent) {
            this.parent = parent;
        }

        @OneToMany
        @OrderColumn(name = "list_idx", nullable = false)
        @JoinTable(name = "polymorphic_list")
        public List<MultiInheritanceImplicitDowncastTest.PolymorphicBase> getList() {
            return list;
        }

        public void setList(List<MultiInheritanceImplicitDowncastTest.PolymorphicBase> list) {
            this.list = list;
        }

        @OneToMany(mappedBy = "parent")
        public Set<MultiInheritanceImplicitDowncastTest.PolymorphicBase> getChildren() {
            return children;
        }

        public void setChildren(Set<MultiInheritanceImplicitDowncastTest.PolymorphicBase> children) {
            this.children = children;
        }

        @OneToMany
        @JoinTable(name = "polymorphic_map")
        @MapKeyColumn(length = 20, nullable = false)
        public Map<String, MultiInheritanceImplicitDowncastTest.PolymorphicBase> getMap() {
            return map;
        }

        public void setMap(Map<String, MultiInheritanceImplicitDowncastTest.PolymorphicBase> map) {
            this.map = map;
        }
    }

    @Entity(name = "PolymorphicPropertyBase")
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    @DiscriminatorColumn(name = "PROP_TYPE")
    public abstract static class PolymorphicPropertyBase implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long id;

        private String name;

        public PolymorphicPropertyBase() {
        }

        @Id
        @GeneratedValue
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @MappedSuperclass
    public abstract static class PolymorphicPropertyMapBase<T extends MultiInheritanceImplicitDowncastTest.PolymorphicBase, E extends MultiInheritanceImplicitDowncastTest.BaseEmbeddable> extends MultiInheritanceImplicitDowncastTest.PolymorphicPropertyBase {
        private static final long serialVersionUID = 1L;

        private T base;

        private E baseEmbeddable;

        public PolymorphicPropertyMapBase() {
        }

        @ManyToOne(fetch = FetchType.LAZY)
        public T getBase() {
            return base;
        }

        public void setBase(T base) {
            this.base = base;
        }

        @Embedded
        public E getBaseEmbeddable() {
            return baseEmbeddable;
        }

        public void setBaseEmbeddable(E baseEmbeddable) {
            this.baseEmbeddable = baseEmbeddable;
        }
    }

    @Entity(name = "PolymorphicPropertySub1")
    @AssociationOverrides({ @AssociationOverride(name = "base", joinColumns = @JoinColumn(name = "base_sub_1")) })
    public static class PolymorphicPropertySub1 extends MultiInheritanceImplicitDowncastTest.PolymorphicPropertyMapBase<MultiInheritanceImplicitDowncastTest.PolymorphicSub1, MultiInheritanceImplicitDowncastTest.Embeddable1> {
        private static final long serialVersionUID = 1L;

        public PolymorphicPropertySub1() {
        }
    }

    @Entity(name = "PolymorphicPropertySub2")
    @AssociationOverrides({ @AssociationOverride(name = "base", joinColumns = @JoinColumn(name = "base_sub_2")) })
    public static class PolymorphicPropertySub2 extends MultiInheritanceImplicitDowncastTest.PolymorphicPropertyMapBase<MultiInheritanceImplicitDowncastTest.PolymorphicSub2, MultiInheritanceImplicitDowncastTest.Embeddable2> {
        private static final long serialVersionUID = 1L;

        public PolymorphicPropertySub2() {
        }
    }

    @Entity(name = "PolymorphicSub1")
    public static class PolymorphicSub1 extends MultiInheritanceImplicitDowncastTest.PolymorphicBase {
        private static final long serialVersionUID = 1L;

        private MultiInheritanceImplicitDowncastTest.IntIdEntity relation1;

        private MultiInheritanceImplicitDowncastTest.PolymorphicBase parent1;

        private MultiInheritanceImplicitDowncastTest.NameObject embeddable1;

        private Integer sub1Value;

        public PolymorphicSub1() {
        }

        @ManyToOne(fetch = FetchType.LAZY)
        public MultiInheritanceImplicitDowncastTest.IntIdEntity getRelation1() {
            return relation1;
        }

        public void setRelation1(MultiInheritanceImplicitDowncastTest.IntIdEntity relation1) {
            this.relation1 = relation1;
        }

        @ManyToOne(fetch = FetchType.LAZY)
        public MultiInheritanceImplicitDowncastTest.PolymorphicBase getParent1() {
            return parent1;
        }

        public void setParent1(MultiInheritanceImplicitDowncastTest.PolymorphicBase parent1) {
            this.parent1 = parent1;
        }

        @Embedded
        public MultiInheritanceImplicitDowncastTest.NameObject getEmbeddable1() {
            return embeddable1;
        }

        public void setEmbeddable1(MultiInheritanceImplicitDowncastTest.NameObject embeddable1) {
            this.embeddable1 = embeddable1;
        }

        public Integer getSub1Value() {
            return sub1Value;
        }

        public void setSub1Value(Integer sub1Value) {
            this.sub1Value = sub1Value;
        }
    }

    @Entity(name = "PolymorphicSub2")
    public static class PolymorphicSub2 extends MultiInheritanceImplicitDowncastTest.PolymorphicBase {
        private static final long serialVersionUID = 1L;

        private MultiInheritanceImplicitDowncastTest.IntIdEntity relation2;

        private MultiInheritanceImplicitDowncastTest.PolymorphicBase parent2;

        private MultiInheritanceImplicitDowncastTest.NameObject embeddable2;

        private Integer sub2Value;

        public PolymorphicSub2() {
        }

        @ManyToOne(fetch = FetchType.LAZY)
        public MultiInheritanceImplicitDowncastTest.IntIdEntity getRelation2() {
            return relation2;
        }

        public void setRelation2(MultiInheritanceImplicitDowncastTest.IntIdEntity relation2) {
            this.relation2 = relation2;
        }

        @ManyToOne(fetch = FetchType.LAZY)
        public MultiInheritanceImplicitDowncastTest.PolymorphicBase getParent2() {
            return parent2;
        }

        public void setParent2(MultiInheritanceImplicitDowncastTest.PolymorphicBase parent1) {
            this.parent2 = parent1;
        }

        @Embedded
        public MultiInheritanceImplicitDowncastTest.NameObject getEmbeddable2() {
            return embeddable2;
        }

        public void setEmbeddable2(MultiInheritanceImplicitDowncastTest.NameObject embeddable1) {
            this.embeddable2 = embeddable1;
        }

        public Integer getSub2Value() {
            return sub2Value;
        }

        public void setSub2Value(Integer sub2Value) {
            this.sub2Value = sub2Value;
        }
    }
}

