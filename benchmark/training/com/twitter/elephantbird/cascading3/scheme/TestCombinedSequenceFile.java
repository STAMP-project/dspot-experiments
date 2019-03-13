package com.twitter.elephantbird.cascading3.scheme;


import DelegateCombineFileInputFormat.COMBINED_INPUT_FORMAT_DELEGATE;
import MapReduceInputFormatWrapper.CLASS_CONF_KEY;
import cascading.flow.FlowProcess;
import cascading.flow.hadoop.HadoopFlowProcess;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.RecordReader;
import org.junit.Assert;
import org.junit.Test;


public class TestCombinedSequenceFile {
    @Test
    public void testHadoopConf() {
        CombinedSequenceFile csfScheme = new CombinedSequenceFile(Fields.ALL);
        JobConf conf = new JobConf();
        FlowProcess fp = new HadoopFlowProcess();
        Tap<Configuration, RecordReader, OutputCollector> tap = new cascading.tap.hadoop.util.TempHfs(conf, "test", CombinedSequenceFile.class, false);
        csfScheme.sourceConfInit(fp, tap, conf);
        Assert.assertEquals("MapReduceInputFormatWrapper shold wrap mapred.SequenceFileinputFormat", "org.apache.hadoop.mapred.SequenceFileInputFormat", conf.get(CLASS_CONF_KEY));
        Assert.assertEquals("Delegate combiner should wrap MapReduceInputFormatWrapper", "com.twitter.elephantbird.mapreduce.input.MapReduceInputFormatWrapper", conf.get(COMBINED_INPUT_FORMAT_DELEGATE));
        Assert.assertEquals("DeprecatedInputFormatWrapper should wrap Delegate combiner", "com.twitter.elephantbird.mapreduce.input.combine.DelegateCombineFileInputFormat", conf.get(DeprecatedInputFormatWrapper.CLASS_CONF_KEY));
    }
}

