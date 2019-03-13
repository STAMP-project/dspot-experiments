/**
 * =================================================
 * Copyright 2006 tagtraum industries incorporated
 * All rights reserved.
 * =================================================
 */
package com.tagtraum.perf.gcviewer.imp;


import com.tagtraum.perf.gcviewer.UnittestHelper;
import com.tagtraum.perf.gcviewer.model.GCModel;
import com.tagtraum.perf.gcviewer.model.GcResourceFile;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;

import static com.tagtraum.perf.gcviewer.UnittestHelper.FOLDER.HP;


/**
 * Date: Jan 30, 2002
 * Time: 5:53:55 PM
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */
public class TestDataReaderHPUX1_4_1 {
    @Test
    public void testParse1() throws Exception {
        String fileName = "SampleHP-UX1_4_1.txt";
        final InputStream in = UnittestHelper.getResourceAsStream(HP, fileName);
        final DataReader reader = new DataReaderHPUX1_4_1(new GcResourceFile(fileName), in);
        GCModel model = reader.read();
        Assert.assertEquals("number of events", 4, model.size());
    }
}

