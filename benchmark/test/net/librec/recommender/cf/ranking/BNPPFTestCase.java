package net.librec.recommender.cf.ranking;


import Configuration.Resource;
import java.io.IOException;
import net.librec.BaseTestCase;
import net.librec.common.LibrecException;
import net.librec.conf.Configuration;
import net.librec.job.RecommenderJob;
import org.junit.Test;


/**
 * BNPPF Test Case corresponds to BNPPFRecommender
 * {@link net.librec.recommender.cf.ranking.BNPPFRecommeder}
 *
 * @author Sun Yatong
 */
public class BNPPFTestCase extends BaseTestCase {
    /**
     * test the whole process of BPoissMF recommendation
     *
     * @throws ClassNotFoundException
     * 		
     * @throws LibrecException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testRecommender() throws IOException, ClassNotFoundException, LibrecException {
        Configuration.Resource resource = new Configuration.Resource("rec/cf/ranking/bnppf-test.properties");
        conf.addResource(resource);
        RecommenderJob job = new RecommenderJob(conf);
        job.runJob();
    }
}

