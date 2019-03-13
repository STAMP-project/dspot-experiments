/**
 * RED5 Open Source Flash Server - http://code.google.com/p/red5/
 *
 * Copyright 2006-2013 by respective authors (see below). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.red5.server.service;


import java.io.File;
import java.io.IOException;
import junit.framework.TestCase;
import org.red5.cache.impl.NoCacheImpl;
import org.red5.io.ITagReader;
import org.red5.io.ITagWriter;
import org.red5.io.flv.IFLV;
import org.red5.server.service.flv.IFLVService;


/**
 *
 *
 * @author The Red5 Project
 * @author Dominick Accattato (daccattato@gmail.com)
 * @author Luke Hubbard, Codegent Ltd (luke@codegent.com)
 */
public class MetaDataInjectionTest extends TestCase {
    private IFLVService service;

    /**
     * Test MetaData injection
     *
     * @throws IOException
     * 		if io exception
     */
    public void testMetaDataInjection() throws IOException {
        String path = "target/test-classes/fixtures/test_cue1.flv";
        File f = new File(path);
        System.out.println(("Path: " + (f.getAbsolutePath())));
        if (f.exists()) {
            f.delete();
        }
        // Create new file
        f.createNewFile();
        // Use service to grab FLV file
        IFLV flv = ((IFLV) (service.getStreamableFile(f)));
        // Grab a writer for writing a new FLV
        ITagWriter writer = flv.getWriter();
        // Create a reader for testing
        File readfile = new File(path);
        IFLV readflv = ((IFLV) (service.getStreamableFile(readfile)));
        readflv.setCache(NoCacheImpl.getInstance());
        // Grab a reader for reading a FLV in
        ITagReader reader = readflv.getReader();
        // Inject MetaData
        writeTagsWithInjection(reader, writer);
    }
}

