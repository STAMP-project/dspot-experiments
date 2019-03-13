package com.querydsl.apt.domain;


import QGeneric16Test_HidaBez.hidaBez;
import QGeneric16Test_HidaBezGruppe.hidaBezGruppe.bez;
import QGeneric4Test_HidaBezGruppe.hidaBezGruppe;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import org.junit.Assert;
import org.junit.Test;


public class Generic16Test extends AbstractTest {
    @Entity
    public abstract static class HidaBez<B extends Generic16Test.HidaBez<B, G>, G extends Generic16Test.HidaBezGruppe<G, B>> extends Generic16Test.CapiBCKeyedByGrundstueck {}

    @Entity
    public abstract static class HidaBezGruppe<G extends Generic16Test.HidaBezGruppe<G, B>, B extends Generic16Test.HidaBez<B, G>> extends Generic16Test.CapiBCKeyedByGrundstueck {
        SortedSet<B> bez = new TreeSet<B>();
    }

    @MappedSuperclass
    public abstract static class CapiBCKeyedByGrundstueck extends Generic16Test.CapiBusinessClass {}

    @MappedSuperclass
    public abstract static class CapiBusinessClass implements Generic16Test.ICapiBusinessClass {}

    public interface ICapiBusinessClass extends Comparable<Generic16Test.ICapiBusinessClass> {}

    @Test
    public void test() {
        Assert.assertNotNull(hidaBez);
        Assert.assertNotNull(hidaBezGruppe);
        Assert.assertTrue(bez.getElementType().equals(Generic16Test.HidaBez.class));
    }
}

