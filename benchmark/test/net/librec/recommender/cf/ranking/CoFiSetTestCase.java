/**
 * Copyright (C) 2016 LibRec
 *
 * This file is part of LibRec.
 * LibRec is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LibRec is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LibRec. If not, see <http://www.gnu.org/licenses/>.
 */
package net.librec.recommender.cf.ranking;


import Configuration.Resource;
import java.io.IOException;
import net.librec.BaseTestCase;
import net.librec.common.LibrecException;
import net.librec.conf.Configuration;
import net.librec.job.RecommenderJob;
import org.junit.Test;


/**
 * CoFiSet Test Case corresponds to CoFiSetRecommender
 * {@link net.librec.recommender.cf.ranking.CoFiSetRecommender}
 *
 * @author SunYatong
 */
public class CoFiSetTestCase extends BaseTestCase {
    /**
     * Test the whole process of CoFiSet recommendation.
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
        Configuration.Resource resource = new Configuration.Resource("rec/cf/ranking/cofiset-test.properties");
        conf.addResource(resource);
        RecommenderJob job = new RecommenderJob(conf);
        job.runJob();
    }
}

