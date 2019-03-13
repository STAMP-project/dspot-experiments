/**
 * Copyright (c) 2013-2015 Chris Newland.
 * Licensed under https://github.com/AdoptOpenJDK/jitwatch/blob/master/LICENSE-BSD
 * Instructions: https://github.com/AdoptOpenJDK/jitwatch/wiki
 */
package org.adoptopenjdk.jitwatch.test;


import org.adoptopenjdk.jitwatch.model.bytecode.InnerClassRelationship;
import org.junit.Assert;
import org.junit.Test;


public class TestInnerClassRelationship {
    @Test
    public void testInnerClassRelationshipParse() {
        String line1 = "#10= #8 of #32; //Inner1=class TestInner$Inner1 of class TestInner";
        String line2 = "#12= #6 of #8; //Inner2=class TestInner$Inner1$Inner2 of class TestInner$Inner1";
        String line3 = "As if by magic, the shopkeeper appeared.";
        InnerClassRelationship icr1 = InnerClassRelationship.parse(line1);
        InnerClassRelationship icr2 = InnerClassRelationship.parse(line2);
        InnerClassRelationship icr3 = InnerClassRelationship.parse(line3);
        Assert.assertEquals("TestInner", icr1.getParentClass());
        Assert.assertEquals("TestInner$Inner1", icr1.getChildClass());
        Assert.assertEquals("TestInner$Inner1", icr2.getParentClass());
        Assert.assertEquals("TestInner$Inner1$Inner2", icr2.getChildClass());
        Assert.assertNull(icr3);
    }

    @Test
    public void testInnerClassNameFinder() {
        String line1 = "public #13= #6 of #10; //Cow=class PolymorphismTest$Cow of class PolymorphismTest";
        String line2 = "public #15= #4 of #10; //Cat=class PolymorphismTest$Cat of class PolymorphismTest";
        String line3 = "public #16= #2 of #10; //Dog=class PolymorphismTest$Dog of class PolymorphismTest";
        String line4 = "public static #18= #17 of #10; //Animal=class PolymorphismTest$Animal of class PolymorphismTest";
        String line5 = "foo";
        Assert.assertEquals("PolymorphismTest$Cow", InnerClassRelationship.parse(line1).getChildClass());
        Assert.assertEquals("PolymorphismTest$Cat", InnerClassRelationship.parse(line2).getChildClass());
        Assert.assertEquals("PolymorphismTest$Dog", InnerClassRelationship.parse(line3).getChildClass());
        Assert.assertEquals("PolymorphismTest$Animal", InnerClassRelationship.parse(line4).getChildClass());
        Assert.assertNull(InnerClassRelationship.parse(line5));
    }
}

