package org.jabref.model.entry;


import BibtexString.Type.AUTHOR;
import BibtexString.Type.OTHER;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class BibtexStringTest {
    @Test
    public void test() {
        // Instantiate
        BibtexString bs = new BibtexString("AAA", "An alternative action");
        bs.setId("ID");
        Assertions.assertEquals("ID", bs.getId());
        Assertions.assertEquals("AAA", bs.getName());
        Assertions.assertEquals("An alternative action", bs.getContent());
        Assertions.assertEquals(OTHER, bs.getType());
        // Clone
        BibtexString bs2 = ((BibtexString) (bs.clone()));
        Assertions.assertEquals(bs.getId(), bs2.getId());
        Assertions.assertEquals(bs.getName(), bs2.getName());
        Assertions.assertEquals(bs.getContent(), bs2.getContent());
        Assertions.assertEquals(bs.getType(), bs2.getType());
        // Change cloned BibtexString
        bs2.setId(IdGenerator.next());
        Assertions.assertNotEquals(bs.getId(), bs2.getId());
        bs2.setName("aOG");
        Assertions.assertEquals(AUTHOR, bs2.getType());
        bs2.setContent("Oscar Gustafsson");
        Assertions.assertEquals("aOG", bs2.getName());
        Assertions.assertEquals("Oscar Gustafsson", bs2.getContent());
    }

    @Test
    public void getContentNeverReturnsNull() {
        BibtexString bs = new BibtexString("SomeName", null);
        Assertions.assertNotNull(bs.getContent());
    }
}

