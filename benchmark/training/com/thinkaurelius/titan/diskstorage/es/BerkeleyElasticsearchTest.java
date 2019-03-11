package com.thinkaurelius.titan.diskstorage.es;


import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.example.GraphOfTheGodsFactory;
import com.thinkaurelius.titan.graphdb.TitanIndexTest;
import com.thinkaurelius.titan.util.system.IOUtils;
import java.io.File;
import org.junit.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class BerkeleyElasticsearchTest extends TitanIndexTest {
    public BerkeleyElasticsearchTest() {
        super(true, true, true);
    }

    /**
     * Test {@link com.thinkaurelius.titan.example.GraphOfTheGodsFactory#create(String)}.
     */
    @Test
    public void testGraphOfTheGodsFactoryCreate() {
        File bdbtmp = new File("target/gotgfactory");
        IOUtils.deleteDirectory(bdbtmp, true);
        TitanGraph gotg = GraphOfTheGodsFactory.create(bdbtmp.getPath());
        TitanIndexTest.assertGraphOfTheGods(gotg);
        gotg.close();
    }
}

