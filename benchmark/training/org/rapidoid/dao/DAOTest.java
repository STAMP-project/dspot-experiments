package org.rapidoid.dao;


import org.junit.jupiter.api.Test;
import org.rapidoid.test.TestCommons;


/**
 *
 *
 * @author Nikolche Mihajlovski
 * @since 3.0.0
 */
public class DAOTest extends TestCommons {
    @Test
    public void testEntityTypeInference() {
        PersonService service = new PersonService();
        // exercise the entity type inference
        eq(getEntityType(), Person.class);
    }
}

