/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.core.joran.replay;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * The Fruit* code is intended to test Joran's replay capability
 */
public class FruitConfigurationTest {
    FruitContext fruitContext = new FruitContext();

    @Test
    public void fruit1() throws Exception {
        List<FruitShell> fsList = doFirstPart("fruit1.xml");
        Assert.assertNotNull(fsList);
        Assert.assertEquals(1, fsList.size());
        FruitShell fs0 = fsList.get(0);
        Assert.assertNotNull(fs0);
        Assert.assertEquals("fs0", fs0.getName());
        Fruit fruit0 = fs0.fruitFactory.buildFruit();
        Assert.assertTrue((fruit0 instanceof Fruit));
        Assert.assertEquals("blue", fruit0.getName());
    }

    @Test
    public void fruit2() throws Exception {
        List<FruitShell> fsList = doFirstPart("fruit2.xml");
        Assert.assertNotNull(fsList);
        Assert.assertEquals(2, fsList.size());
        FruitShell fs0 = fsList.get(0);
        Assert.assertNotNull(fs0);
        Assert.assertEquals("fs0", fs0.getName());
        Fruit fruit0 = fs0.fruitFactory.buildFruit();
        Assert.assertTrue((fruit0 instanceof Fruit));
        Assert.assertEquals("blue", fruit0.getName());
        FruitShell fs1 = fsList.get(1);
        Assert.assertNotNull(fs1);
        Assert.assertEquals("fs1", fs1.getName());
        Fruit fruit1 = fs1.fruitFactory.buildFruit();
        Assert.assertTrue((fruit1 instanceof WeightytFruit));
        Assert.assertEquals("orange", fruit1.getName());
        Assert.assertEquals(1.2, ((WeightytFruit) (fruit1)).getWeight(), 0.01);
    }

    @Test
    public void withSubst() throws Exception {
        List<FruitShell> fsList = doFirstPart("fruitWithSubst.xml");
        Assert.assertNotNull(fsList);
        Assert.assertEquals(1, fsList.size());
        FruitShell fs0 = fsList.get(0);
        Assert.assertNotNull(fs0);
        Assert.assertEquals("fs0", fs0.getName());
        int oldCount = FruitFactory.count;
        Fruit fruit0 = fs0.fruitFactory.buildFruit();
        Assert.assertTrue((fruit0 instanceof WeightytFruit));
        Assert.assertEquals(("orange-" + oldCount), fruit0.getName());
        Assert.assertEquals(1.2, ((WeightytFruit) (fruit0)).getWeight(), 0.01);
    }
}

