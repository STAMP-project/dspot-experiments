/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.cfg;


import java.io.File;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests using of cacheable configuration files.
 *
 * @author Steve Ebersole
 */
public class CacheableFileTest extends BaseUnitTestCase {
    public static final String MAPPING = "org/hibernate/test/cfg/Cacheable.hbm.xml";

    private File mappingFile;

    private File mappingBinFile;

    @Test
    public void testCachedFiles() throws Exception {
        Assert.assertFalse(mappingBinFile.exists());
        // This call should create the cached file
        new Configuration().addCacheableFile(mappingFile);
        Assert.assertTrue(mappingBinFile.exists());
        new Configuration().addCacheableFileStrictly(mappingFile);
        // make mappingBinFile obsolete by declaring it a minute older than mappingFile
        mappingBinFile.setLastModified(((mappingFile.lastModified()) - 60000L));
        new Configuration().addCacheableFile(mappingFile);
        Assert.assertTrue(mappingBinFile.exists());
        Assert.assertTrue("mappingFile should have been recreated.", ((mappingBinFile.lastModified()) >= (mappingFile.lastModified())));
    }
}

