package com.twitter.elephantbird.pig.util;


import com.twitter.elephantbird.mapreduce.input.RawSequenceFileRecordReader;
import com.twitter.elephantbird.pig.load.SequenceFileLoader;
import com.twitter.elephantbird.util.HadoopCompat;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.cli.ParseException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.pig.PigServer;
import org.apache.pig.backend.hadoop.executionengine.mapReduceLayer.PigSplit;
import org.apache.pig.impl.plan.OperatorKey;
import org.junit.Test;


/**
 * Base class which facilitates creation of unit tests for {@link WritableConverter}
 * implementations.
 *
 * @author Andy Schlaikjer
 */
public abstract class AbstractTestWritableConverter<W extends Writable, C extends WritableConverter<W>> {
    private final Class<? extends W> writableClass;

    private final Class<? extends C> writableConverterClass;

    private final String writableConverterArguments;

    private final W[] data;

    private final String[] expected;

    private final String valueSchema;

    protected PigServer pigServer;

    protected String tempFilename;

    public AbstractTestWritableConverter(final Class<? extends W> writableClass, final Class<? extends C> writableConverterClass, final String writableConverterArguments, final W[] data, final String[] expected, final String valueSchema) {
        this.writableClass = writableClass;
        this.writableConverterClass = writableConverterClass;
        this.writableConverterArguments = (writableConverterArguments == null) ? "" : writableConverterArguments;
        this.data = data;
        this.expected = expected;
        this.valueSchema = valueSchema;
    }

    @Test
    public void readOutsidePig() throws IOException, ClassCastException, ClassNotFoundException, IllegalAccessException, InstantiationException, InterruptedException, ParseException {
        // simulate Pig front-end runtime
        final SequenceFileLoader<IntWritable, Text> loader = new SequenceFileLoader<IntWritable, Text>(String.format("-c %s", IntWritableConverter.class.getName()), String.format("-c %s %s", writableConverterClass.getName(), writableConverterArguments));
        Job job = new Job();
        loader.setUDFContextSignature("12345");
        loader.setLocation(tempFilename, job);
        // simulate Pig back-end runtime
        final RecordReader<DataInputBuffer, DataInputBuffer> reader = new RawSequenceFileRecordReader();
        final FileSplit fileSplit = new FileSplit(new Path(tempFilename), 0, new File(tempFilename).length(), new String[]{ "localhost" });
        final TaskAttemptContext context = HadoopCompat.newTaskAttemptContext(HadoopCompat.getConfiguration(job), new TaskAttemptID());
        reader.initialize(fileSplit, context);
        final InputSplit[] wrappedSplits = new InputSplit[]{ fileSplit };
        final int inputIndex = 0;
        final List<OperatorKey> targetOps = Arrays.asList(new OperatorKey("54321", 0));
        final int splitIndex = 0;
        final PigSplit split = new PigSplit(wrappedSplits, inputIndex, targetOps, splitIndex);
        split.setConf(HadoopCompat.getConfiguration(job));
        loader.prepareToRead(reader, split);
        // read tuples and validate
        validate(new LoadFuncTupleIterator(loader));
    }

    @Test
    public void read() throws IOException {
        registerReadQuery();
        validate(pigServer.openIterator("A"));
    }

    @Test
    public void readWriteRead() throws IOException {
        registerReadQuery();
        registerWriteQuery(((tempFilename) + "-2"));
        registerReadQuery(((tempFilename) + "-2"));
        validate(pigServer.openIterator("A"));
    }
}

