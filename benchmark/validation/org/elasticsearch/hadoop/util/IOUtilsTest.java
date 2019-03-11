package org.elasticsearch.hadoop.util;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.elasticsearch.hadoop.EsHadoopIllegalArgumentException;
import org.junit.Assert;
import org.junit.Test;


public class IOUtilsTest {
    @Test
    public void openResource() throws Exception {
        InputStream inputStream = IOUtils.open("org/elasticsearch/hadoop/util/textdata.txt");
        Assert.assertNotNull(inputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Assert.assertEquals("Hello World. This is used by IOUtilsTest.", reader.readLine());
    }

    @Test
    public void openFile() throws Exception {
        File tempFile = File.createTempFile("textdata", "txt");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile)));
        writer.write("Hello World. This is used by IOUtilsTest.");
        writer.close();
        InputStream inputStream = IOUtils.open(tempFile.toURI().toURL().toString());
        Assert.assertNotNull(inputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Assert.assertEquals("Hello World. This is used by IOUtilsTest.", reader.readLine());
    }

    @Test(expected = EsHadoopIllegalArgumentException.class)
    public void openNonExistingFile() throws Exception {
        InputStream inputStream = IOUtils.open("file:///This/Doesnt/Exist");
        Assert.fail("Shouldn't pass");
    }
}

