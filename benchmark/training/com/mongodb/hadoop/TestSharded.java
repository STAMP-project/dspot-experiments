package com.mongodb.hadoop;


import com.mongodb.DBCollection;
import com.mongodb.MongoClientURI;
import com.mongodb.hadoop.examples.treasury.TreasuryYieldXMLConfig;
import com.mongodb.hadoop.mapred.output.MongoOutputCommitter;
import com.mongodb.hadoop.testutils.MapReduceJob;
import org.junit.Assert;
import org.junit.Test;


public class TestSharded extends BaseShardedTest {
    @Test
    public void testBasicInputSource() {
        MapReduceJob job = new MapReduceJob(TreasuryYieldXMLConfig.class.getName()).jar(TreasuryTest.JOBJAR_PATH).inputUris(getInputUri()).outputUri(getOutputUri());
        if (isHadoopV1()) {
            job.outputCommitter(MongoOutputCommitter.class);
        }
        job.execute(isRunTestInVm());
        compareResults(getMongos().getDB("mongo_hadoop").getCollection("yield_historical.out"), getReference());
    }

    @Test
    public void testMultiMongos() {
        MongoClientURI outputUri = getOutputUri();
        MapReduceJob job = new MapReduceJob(TreasuryYieldXMLConfig.class.getName()).jar(TreasuryTest.JOBJAR_PATH).param(INPUT_MONGOS_HOSTS, "localhost:27017 localhost:27018").inputUris(getInputUri()).outputUri(outputUri);
        if (isHadoopV1()) {
            job.outputCommitter(MongoOutputCommitter.class);
        }
        job.execute(isRunTestInVm());
        compareResults(getMongos().getDB(outputUri.getDatabase()).getCollection(outputUri.getCollection()), getReference());
    }

    @Test
    public void testRangeQueries() {
        DBCollection collection = getMongos().getDB(getOutputUri().getDatabase()).getCollection(getOutputUri().getCollection());
        collection.drop();
        MapReduceJob job = new MapReduceJob(TreasuryYieldXMLConfig.class.getName()).jar(TreasuryTest.JOBJAR_PATH).inputUris(getInputUri()).outputUri(getOutputUri()).param(SPLITS_USE_RANGEQUERY, "true");
        if (isHadoopV1()) {
            job.outputCommitter(MongoOutputCommitter.class);
        }
        job.execute(isRunTestInVm());
        compareResults(collection, getReference());
        collection.drop();
        job.param(INPUT_QUERY, "{\"_id\":{\"$gt\":{\"$date\":1182470400000}}}").execute(isRunTestInVm());
        // Make sure that this fails when rangequery is used with a query that conflicts
        Assert.assertFalse("This collection shouldn't exist because of the failure", getMongos().getDB("mongo_hadoop").getCollectionNames().contains("yield_historical.out"));
    }

    @Test
    public void testShardedClusterWithGtLtQueryFormats() {
        DBCollection collection = getMongos().getDB("mongo_hadoop").getCollection("yield_historical.out");
        collection.drop();
        MapReduceJob job = new MapReduceJob(TreasuryYieldXMLConfig.class.getName()).jar(TreasuryTest.JOBJAR_PATH).inputUris(getInputUri()).outputUri(getOutputUri()).param(SPLITS_USE_RANGEQUERY, "true");
        if (isHadoopV1()) {
            job.outputCommitter(MongoOutputCommitter.class);
        }
        job.execute(isRunTestInVm());
        compareResults(collection, getReference());
        collection.drop();
        job.param(INPUT_QUERY, "{\"_id\":{\"$gt\":{\"$date\":1182470400000}}}").inputUris(getInputUri()).execute(isRunTestInVm());
        // Make sure that this fails when rangequery is used with a query that conflicts
        Assert.assertEquals(0, collection.count());
    }
}

