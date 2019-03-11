package net.librec.increment;


import Configuration.Resource;
import net.librec.BaseTestCase;
import net.librec.conf.Configuration;
import net.librec.data.DataModel;
import net.librec.increment.rating.IncrementalBiasedMFRecommender;
import org.junit.Test;


/**
 *
 *
 * @author logicxin
 */
public class IncrementalMFTestCase extends BaseTestCase {
    @Test
    public void unitTesting() throws Exception {
        String configFilePath = "rec/increment/rating/biasedmf-test.properties";
        Configuration conf = new Configuration();
        Configuration.Resource resource = new Configuration.Resource(configFilePath);
        conf.addResource(resource);
        // build data model
        DataModel dataModel = new net.librec.data.model.TextDataModel(conf);
        dataModel.buildDataModel();
        // set recommendation context
        // RecommenderContext context = new RecommenderContext(conf, dataModel);
        // training
        IncrementalBiasedMFRecommender recommender = new IncrementalBiasedMFRecommender();
        // recommender.setContext(context);
        // recommender.trainModel();
        // List<Entry<Integer, Double>> ratedItems = new ArrayList<>();
        // ratedItems.add(new SimpleEntry<>(0, 0.4d));
        // List<Integer> candidateItems = new ArrayList<>();
        // candidateItems.add(0);
        // candidateItems.add(1);
        // 
        // List<Entry<Integer, Double>> result = recommender.scoreItems(ratedItems, candidateItems);
    }
}

