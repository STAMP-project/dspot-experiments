package com.querydsl.codegen;


import com.mysema.codegen.model.Type;
import org.junit.Test;


public class GenericTest {
    public abstract static class CapiBCKeyedByGrundstueck {}

    public abstract static class HidaBez<B extends GenericTest.HidaBez<B, G>, G extends GenericTest.HidaBezGruppe<G, B>> extends GenericTest.CapiBCKeyedByGrundstueck {}

    public abstract static class HidaBezGruppe<G extends GenericTest.HidaBezGruppe<G, B>, B extends GenericTest.HidaBez<B, G>> extends GenericTest.CapiBCKeyedByGrundstueck {}

    private TypeFactory typeFactory = new TypeFactory();

    @Test
    public void hidaBez() {
        Type type = typeFactory.getEntityType(GenericTest.HidaBez.class);
        // System.out.println(type.getGenericName(true));
    }

    @Test
    public void hidaBezGruppe() {
        Type type = typeFactory.getEntityType(GenericTest.HidaBezGruppe.class);
        // System.out.println(type.getGenericName(true));
    }
}

