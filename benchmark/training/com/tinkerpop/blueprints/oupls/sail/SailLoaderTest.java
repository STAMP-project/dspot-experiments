package com.tinkerpop.blueprints.oupls.sail;


import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailConnection;


/**
 *
 *
 * @author Joshua Shinavier (http://fortytwo.net)
 */
/* @Test
public void testFormatExtensions() throws Exception {
Set<String> extensions = new HashSet<String>();
for (RDFFormat f : RDFFormat.values()) {
System.out.println("" + f);
extensions.addAll(f.getFileExtensions());
}

for (String ext : extensions) {
System.out.println(ext);
}
}//
 */
public class SailLoaderTest {
    @Test
    public void testAll() throws Exception {
        KeyIndexableGraph g = new TinkerGraph();
        Sail sail = new GraphSail(g);
        sail.initialize();
        try {
            SailLoader loader = new SailLoader(sail);
            File f = resourceToFile("graph-example-sail-test.trig");
            SailConnection sc = sail.getConnection();
            try {
                sc.begin();
                Assert.assertEquals(0, sc.size());
                loader.load(f);
                sc.rollback();
                Assert.assertEquals(29, sc.size());
            } finally {
                sc.close();
            }
        } finally {
            sail.shutDown();
        }
    }
}

