package com.querydsl.apt.domain;


import QInterfaceType2Test_UserImpl.userImpl.party;
import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Proxy;
import org.junit.Assert;
import org.junit.Test;

import static CascadeType.ALL;
import static FetchType.EAGER;


public class InterfaceType2Test {
    public interface IEntity {
        Long getId();
    }

    public interface User extends InterfaceType2Test.IEntity {
        InterfaceType2Test.Party getParty();

        String getUsername();
    }

    public interface Party extends InterfaceType2Test.IEntity {
        String getName();
    }

    @MappedSuperclass
    public static class EntityImpl {
        @Id
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    @Entity
    @Table(name = "USERS")
    @AccessType("field")
    @Proxy(proxyClass = InterfaceType2Test.User.class)
    public static class UserImpl extends InterfaceType2Test.EntityImpl implements InterfaceType2Test.User {
        @NaturalId(mutable = true)
        @Column(name = "USERNAME", nullable = false)
        private String username;

        @ManyToOne(cascade = { ALL }, fetch = EAGER, targetEntity = InterfaceType2Test.PartyImpl.class)
        @JoinColumn(name = "PARTY_ID", nullable = false)
        private InterfaceType2Test.Party party;

        @Override
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        @Override
        public InterfaceType2Test.Party getParty() {
            return party;
        }

        public void setParty(InterfaceType2Test.Party party) {
            this.party = party;
        }
    }

    @javax.persistence.Entity
    @Table(name = "PARTY")
    @AccessType("field")
    @Proxy(proxyClass = InterfaceType2Test.Party.class)
    public abstract static class PartyImpl extends InterfaceType2Test.EntityImpl implements InterfaceType2Test.Party {
        @Column(name = "NAME", nullable = false)
        private String name;

        @Override
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void test() {
        Assert.assertEquals(QInterfaceType2Test_PartyImpl.class, party.getClass());
    }
}

