package com.ctrip.framework.apollo.portal.component.txtresolver;


import com.ctrip.framework.apollo.common.dto.ItemChangeSets;
import com.ctrip.framework.apollo.common.dto.ItemDTO;
import com.ctrip.framework.apollo.common.exception.BadRequestException;
import com.ctrip.framework.apollo.portal.AbstractUnitTest;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;


public class PropertyResolverTest extends AbstractUnitTest {
    @InjectMocks
    private PropertyResolver resolver;

    @Test
    public void testEmptyText() {
        try {
            resolver.resolve(0, "", null);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof BadRequestException));
        }
    }

    @Test
    public void testAddItemBeforeNoItem() {
        ItemChangeSets changeSets = resolver.resolve(1, "a=b\nb=c", Collections.emptyList());
        Assert.assertEquals(2, changeSets.getCreateItems().size());
    }

    @Test
    public void testAddItemBeforeHasItem() {
        ItemChangeSets changeSets = resolver.resolve(1, "x=y\na=b\nb=c\nc=d", mockBaseItemHas3Key());
        Assert.assertEquals("x", changeSets.getCreateItems().get(0).getKey());
        Assert.assertEquals(1, changeSets.getCreateItems().size());
        Assert.assertEquals(3, changeSets.getUpdateItems().size());
    }

    @Test
    public void testAddCommentAndBlankItem() {
        ItemChangeSets changeSets = resolver.resolve(1, "#ddd\na=b\n\nb=c\nc=d", mockBaseItemHas3Key());
        Assert.assertEquals(2, changeSets.getCreateItems().size());
        Assert.assertEquals(3, changeSets.getUpdateItems().size());
    }

    @Test
    public void testChangeItemNumLine() {
        ItemChangeSets changeSets = resolver.resolve(1, "b=c\nc=d\na=b", mockBaseItemHas3Key());
        Assert.assertEquals(3, changeSets.getUpdateItems().size());
    }

    @Test
    public void testDeleteItem() {
        ItemChangeSets changeSets = resolver.resolve(1, "a=b", mockBaseItemHas3Key());
        Assert.assertEquals(2, changeSets.getDeleteItems().size());
    }

    @Test
    public void testDeleteCommentItem() {
        ItemChangeSets changeSets = resolver.resolve(1, "a=b\n\nb=c", mockBaseItemWith2Key1Comment1Blank());
        Assert.assertEquals(2, changeSets.getDeleteItems().size());
        Assert.assertEquals(2, changeSets.getUpdateItems().size());
        Assert.assertEquals(1, changeSets.getCreateItems().size());
    }

    @Test
    public void testDeleteBlankItem() {
        ItemChangeSets changeSets = resolver.resolve(1, "#qqqq\na=b\nb=c", mockBaseItemWith2Key1Comment1Blank());
        Assert.assertEquals(1, changeSets.getDeleteItems().size());
        Assert.assertEquals(1, changeSets.getUpdateItems().size());
        Assert.assertEquals(0, changeSets.getCreateItems().size());
    }

    @Test
    public void testUpdateItem() {
        ItemChangeSets changeSets = resolver.resolve(1, "a=d", mockBaseItemHas3Key());
        List<ItemDTO> updateItems = changeSets.getUpdateItems();
        Assert.assertEquals(1, updateItems.size());
        Assert.assertEquals("d", updateItems.get(0).getValue());
    }

    @Test
    public void testUpdateCommentItem() {
        ItemChangeSets changeSets = resolver.resolve(1, ("#ww\n" + (("a=b\n" + "\n") + "b=c")), mockBaseItemWith2Key1Comment1Blank());
        Assert.assertEquals(1, changeSets.getDeleteItems().size());
        Assert.assertEquals(0, changeSets.getUpdateItems().size());
        Assert.assertEquals(1, changeSets.getCreateItems().size());
    }

    @Test
    public void testAllSituation() {
        ItemChangeSets changeSets = resolver.resolve(1, "#ww\nd=e\nb=c\na=b\n\nq=w\n#eee", mockBaseItemWith2Key1Comment1Blank());
        Assert.assertEquals(2, changeSets.getDeleteItems().size());
        Assert.assertEquals(2, changeSets.getUpdateItems().size());
        Assert.assertEquals(5, changeSets.getCreateItems().size());
    }
}

