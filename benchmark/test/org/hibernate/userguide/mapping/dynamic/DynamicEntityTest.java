/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.dynamic;


import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class DynamicEntityTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::mapping-model-dynamic-example[]
            Map<String, String> book = new HashMap<>();
            book.put("isbn", "978-9730228236");
            book.put("title", "High-Performance Java Persistence");
            book.put("author", "Vlad Mihalcea");
            entityManager.unwrap(.class).save("Book", book);
            // end::mapping-model-dynamic-example[]
        });
    }
}

