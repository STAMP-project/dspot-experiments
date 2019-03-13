/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.boot.binding.cacheable;


import java.io.File;
import java.io.FileNotFoundException;
import org.hibernate.boot.MappingException;
import org.hibernate.boot.jaxb.internal.MappingBinder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Originally developed to help diagnose HHH-10131 - the original tests
 * check 4 conditions:<ol>
 *     <li>strict usage where the cached file does exist</li>
 *     <li>strict usage where the cached file does not exist</li>
 *     <li>non-strict usage where the cached file does exist</li>
 *     <li>non-strict usage where the cached file does not exist</li>
 * </ol>
 *
 * @author Steve Ebersole
 */
public class CacheableHbmXmlTest extends BaseUnitTestCase {
    private static final String HBM_RESOURCE_NAME = "org/hibernate/test/boot/binding/cacheable/SimpleEntity.hbm.xml";

    private StandardServiceRegistry ssr;

    private MappingBinder binder;

    private File hbmXmlFile;

    private File hbmXmlBinFile;

    @Test
    public void testStrictCaseWhereFileDoesPreviouslyExist() throws FileNotFoundException {
        deleteBinFile();
        createBinFile();
        try {
            addCacheableFileStrictly(hbmXmlFile).buildMetadata();
        } catch (MappingException e) {
            Assert.fail("addCacheableFileStrictly led to MappingException when bin file existed");
        }
    }

    @Test
    public void testStrictCaseWhereFileDoesNotPreviouslyExist() throws FileNotFoundException {
        deleteBinFile();
        try {
            addCacheableFileStrictly(hbmXmlFile).buildMetadata();
            Assert.fail("addCacheableFileStrictly should be led to MappingException when bin file does not exist");
        } catch (MappingException ignore) {
            // this is the expected result
        }
    }

    @Test
    public void testNonStrictCaseWhereFileDoesPreviouslyExist() {
        deleteBinFile();
        createBinFile();
        addCacheableFile(hbmXmlFile).buildMetadata();
    }

    @Test
    public void testNonStrictCaseWhereFileDoesNotPreviouslyExist() {
        deleteBinFile();
        addCacheableFile(hbmXmlFile).buildMetadata();
    }
}

