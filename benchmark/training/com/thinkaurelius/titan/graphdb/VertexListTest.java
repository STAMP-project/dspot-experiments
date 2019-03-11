package com.thinkaurelius.titan.graphdb;


import com.google.common.collect.Iterables;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanVertex;
import com.thinkaurelius.titan.graphdb.query.vertex.VertexArrayList;
import com.thinkaurelius.titan.graphdb.query.vertex.VertexLongList;
import com.thinkaurelius.titan.graphdb.transaction.StandardTitanTx;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class VertexListTest {
    @Test
    public void testLists() {
        int num = 13;
        TitanGraph g = TitanFactory.open("inmemory");
        StandardTitanTx tx = ((StandardTitanTx) (g.newTransaction()));
        VertexLongList vll = new VertexLongList(tx);
        VertexArrayList val = new VertexArrayList(tx);
        for (int i = 0; i < num; i++) {
            TitanVertex v = tx.addVertex();
            vll.add(v);
            val.add(v);
        }
        Assert.assertEquals(num, Iterables.size(vll));
        Assert.assertEquals(num, Iterables.size(val));
        vll.sort();
        val.sort();
        Assert.assertTrue(vll.isSorted());
        Assert.assertTrue(val.isSorted());
        for (Iterable<TitanVertex> iterable : new Iterable[]{ val, vll }) {
            Iterator<TitanVertex> iter = iterable.iterator();
            TitanVertex previous = null;
            for (int i = 0; i < num; i++) {
                TitanVertex next = iter.next();
                if (previous != null)
                    Assert.assertTrue(((previous.longId()) < (next.longId())));

                previous = next;
            }
            try {
                iter.next();
                Assert.fail();
            } catch (NoSuchElementException ex) {
            }
        }
        tx.commit();
        g.close();
    }
}

