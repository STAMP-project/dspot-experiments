package com.hannesdorfmann.fragmentargs.processor;


import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import junit.framework.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Hannes Dorfmann
 */
public class CompareModifierUtilsTest {
    Element a;

    Element b;

    Set<Modifier> aModifiers;

    Set<Modifier> bModifiers;

    @Test
    public void aPublicBnot() {
        aModifiers.add(Modifier.PUBLIC);
        bModifiers.add(Modifier.PRIVATE);
        Assert.assertEquals((-1), ModifierUtils.compareModifierVisibility(a, b));
    }

    @Test
    public void aDefaultBnot() {
        bModifiers.add(Modifier.PRIVATE);
        Assert.assertEquals((-1), ModifierUtils.compareModifierVisibility(a, b));
    }

    @Test
    public void aProtectedBnot() {
        aModifiers.add(Modifier.PROTECTED);
        bModifiers.add(Modifier.PRIVATE);
        Assert.assertEquals((-1), ModifierUtils.compareModifierVisibility(a, b));
    }

    @Test
    public void bPublicAnot() {
        bModifiers.add(Modifier.PUBLIC);
        aModifiers.add(Modifier.PRIVATE);
        Assert.assertEquals(1, ModifierUtils.compareModifierVisibility(a, b));
    }

    @Test
    public void bDefaultAnot() {
        aModifiers.add(Modifier.PRIVATE);
        Assert.assertEquals(1, ModifierUtils.compareModifierVisibility(a, b));
    }

    @Test
    public void bProtectedAnot() {
        aModifiers.add(Modifier.PRIVATE);
        bModifiers.add(Modifier.PROTECTED);
        Assert.assertEquals(1, ModifierUtils.compareModifierVisibility(a, b));
    }

    @Test
    public void samePrivate() {
        aModifiers.add(Modifier.PRIVATE);
        bModifiers.add(Modifier.PRIVATE);
        Assert.assertEquals(0, ModifierUtils.compareModifierVisibility(a, b));
    }

    @Test
    public void sameProtected() {
        aModifiers.add(Modifier.PRIVATE);
        bModifiers.add(Modifier.PRIVATE);
        Assert.assertEquals(0, ModifierUtils.compareModifierVisibility(a, b));
    }

    @Test
    public void sameDefault() {
        Assert.assertEquals(0, ModifierUtils.compareModifierVisibility(a, b));
    }

    @Test
    public void samePublic() {
        aModifiers.add(Modifier.PUBLIC);
        bModifiers.add(Modifier.PUBLIC);
        Assert.assertEquals(0, ModifierUtils.compareModifierVisibility(a, b));
    }
}

