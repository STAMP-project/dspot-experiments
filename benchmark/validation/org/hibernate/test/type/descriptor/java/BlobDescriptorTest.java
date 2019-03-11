/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.type.descriptor.java;


import BlobTypeDescriptor.INSTANCE;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import org.hibernate.engine.jdbc.BlobImplementer;
import org.hibernate.engine.jdbc.BlobProxy;
import org.hibernate.testing.TestForIssue;
import org.hibernate.type.descriptor.java.DataHelper;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class BlobDescriptorTest extends AbstractDescriptorTest<Blob> {
    final Blob original = BlobProxy.generateProxy(new byte[]{ 1, 2, 3 });

    final Blob copy = BlobProxy.generateProxy(new byte[]{ 1, 2, 3 });

    final Blob different = BlobProxy.generateProxy(new byte[]{ 3, 2, 1 });

    public BlobDescriptorTest() {
        super(INSTANCE);
    }

    @Test
    @Override
    public void testEquality() {
        // blobs of the same internal value are not really comparable
        Assert.assertFalse(((original) == (copy)));
        Assert.assertTrue(INSTANCE.areEqual(original, original));
        Assert.assertFalse(INSTANCE.areEqual(original, copy));
        Assert.assertFalse(INSTANCE.areEqual(original, different));
    }

    @Test
    @Override
    public void testExternalization() {
        // blobs of the same internal value are not really comparable
        String externalized = INSTANCE.toString(original);
        Blob consumed = INSTANCE.fromString(externalized);
        try {
            PrimitiveByteArrayTypeDescriptor.INSTANCE.areEqual(DataHelper.extractBytes(original.getBinaryStream()), DataHelper.extractBytes(consumed.getBinaryStream()));
        } catch (SQLException e) {
            Assert.fail(("SQLException accessing blob : " + (e.getMessage())));
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-8193")
    public void testStreamResetOnAccess() throws IOException, SQLException {
        byte[] bytes = new byte[]{ 1, 2, 3, 4 };
        BlobImplementer blob = ((BlobImplementer) (BlobProxy.generateProxy(bytes)));
        int value = blob.getUnderlyingStream().getInputStream().read();
        // Call to BlobImplementer#getUnderlyingStream() should mark input stream for reset.
        Assert.assertEquals(bytes.length, blob.getUnderlyingStream().getInputStream().available());
    }
}

