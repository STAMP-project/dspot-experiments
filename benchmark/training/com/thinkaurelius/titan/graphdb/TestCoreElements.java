package com.thinkaurelius.titan.graphdb;


import Cardinality.LIST;
import Cardinality.SET;
import Cardinality.SINGLE;
import Direction.BOTH;
import Direction.OUT;
import Multiplicity.MANY2ONE;
import Multiplicity.MULTI;
import Multiplicity.ONE2ONE;
import Multiplicity.SIMPLE;
import Order.ASC;
import Order.DESC;
import com.thinkaurelius.titan.core.Multiplicity;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests classes, enums and other non-interfaces in the core package
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class TestCoreElements {
    @Test
    public void testMultiplicityCardinality() {
        Assert.assertEquals(MULTI, Multiplicity.convert(LIST));
        Assert.assertEquals(SIMPLE, Multiplicity.convert(SET));
        Assert.assertEquals(MANY2ONE, Multiplicity.convert(SINGLE));
        Assert.assertEquals(MULTI.getCardinality(), LIST);
        Assert.assertEquals(SIMPLE.getCardinality(), SET);
        Assert.assertEquals(MANY2ONE.getCardinality(), SINGLE);
        Assert.assertFalse(MULTI.isConstrained());
        Assert.assertTrue(SIMPLE.isConstrained());
        Assert.assertTrue(ONE2ONE.isConstrained());
        Assert.assertTrue(ONE2ONE.isConstrained(BOTH));
        Assert.assertTrue(SIMPLE.isConstrained(BOTH));
        Assert.assertFalse(MULTI.isUnique(OUT));
        Assert.assertTrue(MANY2ONE.isUnique(OUT));
    }

    @Test
    public void testOrder() {
        Assert.assertTrue(((ASC.modulateNaturalOrder("A".compareTo("B"))) < 0));
        Assert.assertTrue(((DESC.modulateNaturalOrder("A".compareTo("B"))) > 0));
    }
}

