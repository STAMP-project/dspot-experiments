/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.lob;


import DialectChecks.SupportsExpectedLobUsagePattern;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialectFeature;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
@RequiresDialectFeature(SupportsExpectedLobUsagePattern.class)
public class BlobTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testBlobSerialization() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Map<String, String> image = new HashMap<String, String>();
        image.put("meta", "metadata");
        image.put("data", "imagedata");
        ImageReader reader = new ImageReader();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(image);
        reader.setImage(em.unwrap(Session.class).getLobHelper().createBlob(baos.toByteArray()));
        em.persist(reader);
        em.getTransaction().commit();
        em.close();// useless but y'a know

        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        reader = em.find(ImageReader.class, reader.getId());
        ObjectInputStream ois = new ObjectInputStream(reader.getImage().getBinaryStream());
        image = ((HashMap<String, String>) (ois.readObject()));
        Assert.assertTrue(image.containsKey("meta"));
        em.getTransaction().commit();
        em.close();
    }
}

