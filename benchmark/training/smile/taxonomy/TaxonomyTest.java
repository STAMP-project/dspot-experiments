/**
 * *****************************************************************************
 * Copyright (c) 2010 Haifeng Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package smile.taxonomy;


import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class TaxonomyTest {
    Taxonomy instance = null;

    Concept a;

    Concept b;

    Concept c;

    Concept d;

    Concept e;

    Concept f;

    Concept ad;

    public TaxonomyTest() {
        instance = new Taxonomy("MyTaxo");
    }

    /**
     * Test of lowestCommonAncestor method, of class Taxonomy.
     */
    @Test
    public void testLowestCommonAncestor() {
        System.out.println("lowestCommonAncestor");
        Concept result = instance.lowestCommonAncestor("A", "B");
        Assert.assertEquals(a, result);
        result = instance.lowestCommonAncestor("E", "B");
        Assert.assertEquals(instance.getRoot(), result);
    }

    /**
     * Test of getPathToRoot method, of class Taxonomy.
     */
    @Test
    public void testGetPathToRoot() {
        System.out.println("getPathToRoot");
        LinkedList<Concept> expResult = new LinkedList<>();
        expResult.addFirst(instance.getRoot());
        expResult.addFirst(ad);
        expResult.addFirst(a);
        expResult.addFirst(c);
        expResult.addFirst(f);
        List<Concept> result = f.getPathToRoot();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getPathFromRoot method, of class Taxonomy.
     */
    @Test
    public void testGetPathFromRoot() {
        System.out.println("getPathToRoot");
        LinkedList<Concept> expResult = new LinkedList<>();
        expResult.add(instance.getRoot());
        expResult.add(ad);
        expResult.add(a);
        expResult.add(c);
        expResult.add(f);
        List<Concept> result = f.getPathFromRoot();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of distance method, of class TaxonomicDistance.
     */
    @Test
    public void testDistance() {
        System.out.println("distance");
        TaxonomicDistance td = new TaxonomicDistance(instance);
        Assert.assertEquals(2.0, td.d("A", "F"), 1.0E-9);
        Assert.assertEquals(5.0, td.d("E", "F"), 1.0E-9);
    }
}

