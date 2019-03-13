package com.mongodb.hadoop;


import com.mongodb.MongoClientURI;
import com.mongodb.hadoop.examples.treasury.TreasuryYieldXMLConfig;
import com.mongodb.hadoop.mapred.output.MongoOutputCommitter;
import com.mongodb.hadoop.splitter.MultiMongoCollectionSplitter;
import com.mongodb.hadoop.testutils.MapReduceJob;
import com.mongodb.hadoop.util.MongoClientURIBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;


public class TestStandalone extends TreasuryTest {
    private static final Log LOG = LogFactory.getLog(TestStandalone.class);

    private final MongoClientURI inputUri2;

    public TestStandalone() {
        inputUri2 = authCheck(new MongoClientURIBuilder().collection("mongo_hadoop", "yield_historical.in2")).build();
    }

    @Test
    public void testBasicInputSource() {
        TestStandalone.LOG.info("testing basic input source");
        MapReduceJob treasuryJob = new MapReduceJob(TreasuryYieldXMLConfig.class.getName()).jar(TreasuryTest.JOBJAR_PATH).param("mongo.input.notimeout", "true").inputUris(getInputUri()).outputUri(getOutputUri());
        if (isHadoopV1()) {
            treasuryJob.outputCommitter(MongoOutputCommitter.class);
        }
        treasuryJob.execute(isRunTestInVm());
        compareResults(getClient(getInputUri()).getDB(getOutputUri().getDatabase()).getCollection(getOutputUri().getCollection()), getReference());
    }

    @Test
    public void testTreasuryJsonConfig() {
        mongoImport("yield_historical.in3", TreasuryTest.TREASURY_JSON_PATH);
        MapReduceJob treasuryJob = new MapReduceJob("com.mongodb.hadoop.examples.treasury.TreasuryYieldXMLConfig").jar(TreasuryTest.JOBJAR_PATH).param(MONGO_SPLITTER_CLASS, MultiMongoCollectionSplitter.class.getName()).param(MULTI_COLLECTION_CONF_KEY, collectionSettings().toString()).outputUri(getOutputUri());
        if (isHadoopV1()) {
            treasuryJob.outputCommitter(MongoOutputCommitter.class);
        }
        treasuryJob.execute(isRunTestInVm());
        compareDoubled(getClient(getInputUri()).getDB(getOutputUri().getDatabase()).getCollection(getOutputUri().getCollection()));
    }

    @Test
    public void testMultipleCollectionSupport() {
        mongoImport(getInputUri().getCollection(), TreasuryTest.TREASURY_JSON_PATH);
        mongoImport(inputUri2.getCollection(), TreasuryTest.TREASURY_JSON_PATH);
        MapReduceJob treasuryJob = new MapReduceJob("com.mongodb.hadoop.examples.treasury.TreasuryYieldXMLConfig").jar(TreasuryTest.JOBJAR_PATH).param(MONGO_SPLITTER_CLASS, MultiMongoCollectionSplitter.class.getName()).inputUris(getInputUri(), inputUri2).outputUri(getOutputUri());
        if (isHadoopV1()) {
            treasuryJob.outputCommitter(MongoOutputCommitter.class);
        }
        treasuryJob.execute(isRunTestInVm());
        compareDoubled(getClient(getInputUri()).getDB(getOutputUri().getDatabase()).getCollection(getOutputUri().getCollection()));
    }
}

