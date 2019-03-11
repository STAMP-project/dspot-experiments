package com.querydsl.apt.domain;


import java.io.Serializable;
import org.junit.Test;

import static DiscriminatorType.STRING;


public class Generic9Test {
    // CommonOrganizationalUnit<?,?,?> parent2;
    // 
    // CommonOrganizationalUnit<T,E,P> parent3;
    @MappedSuperclass
    public abstract static class CommonOrganizationalUnit<T extends Generic9Test.EntityLocalized, E extends Generic9Test.TenantPreference, P extends Generic9Test.CommonOrganizationalUnit<?, ?, ?>> extends Generic9Test.LocalizableEntity<T> implements Serializable , Comparable<Generic9Test.CommonOrganizationalUnit<T, E, P>> {
        P parent;
    }

    @MappedSuperclass
    public abstract static class ProductionSurface<T extends Generic9Test.EntityLocalized, E extends Generic9Test.TenantPreference, P extends Generic9Test.CommonOrganizationalUnit<?, ?, ?>> extends Generic9Test.CommonOrganizationalUnit<T, E, P> implements Serializable {}

    // @Entity
    // public class Building extends ProductionSurface<BuildingLocalized, BuildingPreference, Site> {
    // 
    // }
    @MappedSuperclass
    public abstract static class EntityLocalized extends Generic9Test.CommonEntity {}

    @Entity
    public static class Preference {}

    @Entity
    @Table(name = "preference")
    @DiscriminatorColumn(name = "discriminator", discriminatorType = STRING)
    public abstract static class TenantPreference extends Generic9Test.Preference {}

    @MappedSuperclass
    public abstract static class CommonEntity {}

    @MappedSuperclass
    public abstract static class LocalizableEntity<T extends Generic9Test.EntityLocalized> extends Generic9Test.CommonEntity {}

    @Test
    public void test() {
        new QGeneric9Test_CommonOrganizationalUnit("test");
    }
}

