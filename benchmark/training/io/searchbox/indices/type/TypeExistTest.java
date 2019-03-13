package io.searchbox.indices.type;


import ElasticsearchVersion.V55;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author happyprg(hongsgo@gmail.com)
 */
public class TypeExistTest {
    @Test
    public void testBasicUriGeneration_compatibleForES55() {
        TypeExist typeExist = new TypeExist.Builder("happyprg").addType("seohoo").build();
        Assert.assertEquals("HEAD", typeExist.getRestMethodName());
        Assert.assertEquals("happyprg/_mapping/seohoo", typeExist.getURI(V55));
    }

    @Test
    public void equalsReturnsTrueForSameDestination() {
        TypeExist typeExist1 = new TypeExist.Builder("twitter").addType("tweet").build();
        TypeExist typeExist1Duplicate = new TypeExist.Builder("twitter").addType("tweet").build();
        Assert.assertEquals(typeExist1, typeExist1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentDestination() {
        TypeExist typeExist1 = new TypeExist.Builder("twitter").addType("tweet").build();
        TypeExist typeExist2 = new TypeExist.Builder("myspace").addType("page").build();
        Assert.assertNotEquals(typeExist1, typeExist2);
    }
}

