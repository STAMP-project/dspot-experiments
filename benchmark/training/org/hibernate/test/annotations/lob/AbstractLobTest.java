/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.lob;


import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public abstract class AbstractLobTest<B extends AbstractBook, C extends AbstractCompiledCode> extends BaseCoreFunctionalTestCase {
    @Test
    public void testSerializableToBlob() throws Exception {
        B book = createBook();
        Editor editor = new Editor();
        editor.setName("O'Reilly");
        book.setEditor(editor);
        book.setCode2(new char[]{ 'r' });
        AbstractLobTest.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(book);
        });
        AbstractLobTest.doInHibernate(this::sessionFactory, ( session) -> {
            B loadedBook = getBookClass().cast(session.get(getBookClass(), getId(book)));
            assertNotNull(loadedBook.getEditor());
            assertEquals(book.getEditor().getName(), loadedBook.getEditor().getName());
            loadedBook.setEditor(null);
        });
        AbstractLobTest.doInHibernate(this::sessionFactory, ( session) -> {
            B loadedBook = getBookClass().cast(session.get(getBookClass(), getId(book)));
            assertNull(loadedBook.getEditor());
        });
    }

    @Test
    public void testClob() throws Exception {
        B book = createBook();
        book.setShortDescription("Hibernate Bible");
        book.setFullText("Hibernate in Action aims to...");
        book.setCode(new Character[]{ 'a', 'b', 'c' });
        book.setCode2(new char[]{ 'a', 'b', 'c' });
        AbstractLobTest.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(book);
        });
        AbstractLobTest.doInHibernate(this::sessionFactory, ( session) -> {
            B b2 = getBookClass().cast(session.get(getBookClass(), getId(book)));
            assertNotNull(b2);
            assertEquals(b2.getFullText(), book.getFullText());
            assertEquals(b2.getCode()[1].charValue(), book.getCode()[1].charValue());
            assertEquals(b2.getCode2()[2], book.getCode2()[2]);
        });
    }

    @Test
    public void testBlob() throws Exception {
        C cc = createCompiledCode();
        Byte[] header = new Byte[2];
        header[0] = new Byte(((byte) (3)));
        header[1] = new Byte(((byte) (0)));
        cc.setHeader(header);
        int codeSize = 5;
        byte[] full = new byte[codeSize];
        for (int i = 0; i < codeSize; i++) {
            full[i] = ((byte) (1 + i));
        }
        cc.setFullCode(full);
        AbstractLobTest.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(cc);
        });
        AbstractLobTest.doInHibernate(this::sessionFactory, ( session) -> {
            C recompiled = getCompiledCodeClass().cast(session.get(getCompiledCodeClass(), getId(cc)));
            assertEquals(recompiled.getHeader()[1], cc.getHeader()[1]);
            assertEquals(recompiled.getFullCode()[(codeSize - 1)], cc.getFullCode()[(codeSize - 1)]);
        });
    }

    @Test
    @SkipForDialect(SybaseDialect.class)
    public void testBinary() throws Exception {
        C cc = createCompiledCode();
        byte[] metadata = new byte[2];
        metadata[0] = ((byte) (3));
        metadata[1] = ((byte) (0));
        cc.setMetadata(metadata);
        AbstractLobTest.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(cc);
        });
        AbstractLobTest.doInHibernate(this::sessionFactory, ( session) -> {
            C recompiled = getCompiledCodeClass().cast(session.get(getCompiledCodeClass(), getId(cc)));
            assertEquals(recompiled.getMetadata()[1], cc.getMetadata()[1]);
        });
    }
}

