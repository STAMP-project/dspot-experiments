package com.insightfullogic.java8.examples.chapter4;


import org.junit.Assert;
import org.junit.Test;


// END concrete_beats_closer_default
public class TestDefaultSubClassing {
    // BEGIN parent_default_used
    @Test
    public void parentDefaultUsed() {
        Parent parent = new ParentImpl();
        parent.welcome();
        Assert.assertEquals("Parent: Hi!", parent.getLastMessage());
    }

    // END parent_default_used
    // BEGIN child_override_default
    @Test
    public void childOverrideDefault() {
        Child child = new ChildImpl();
        child.welcome();
        Assert.assertEquals("Child: Hi!", child.getLastMessage());
    }

    // END child_override_default
    // BEGIN concrete_beats_default
    @Test
    public void concreteBeatsDefault() {
        Parent parent = new OverridingParent();
        parent.welcome();
        Assert.assertEquals("Class Parent: Hi!", parent.getLastMessage());
    }

    // END concrete_beats_default
    // BEGIN concrete_beats_closer_default
    @Test
    public void concreteBeatsCloserDefault() {
        Child child = new OverridingChild();
        child.welcome();
        Assert.assertEquals("Class Parent: Hi!", child.getLastMessage());
    }
}

