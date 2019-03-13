package org.robobinding.widget.adapterview;


import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.robobinding.itempresentationmodel.ViewTypeSelectable;
import org.robobinding.util.RandomValues;


/**
 *
 *
 * @since 1.0
 * @author Cheng Wei
 */
public class MultiItemLayoutSelectorTest {
    @Rule
    public ExpectedException thrownException = ExpectedException.none();

    private List<Integer> itemLayoutIds;

    @Test
    public void shouldViewTypeCountEqualsNumItemLayouts() {
        MultiItemLayoutSelector layoutSelector = new MultiItemLayoutSelector(itemLayoutIds, null);
        Assert.assertThat(layoutSelector.getViewTypeCount(), Matchers.equalTo(itemLayoutIds.size()));
    }

    @Test
    public void shouldReturnUserSelectedViewType() {
        int userSelectedViewType = RandomValues.nextInt(itemLayoutIds.size());
        ViewTypeSelectable viewTypeSelector = withUserSelectedViewType(userSelectedViewType);
        MultiItemLayoutSelector layoutSelector = new MultiItemLayoutSelector(itemLayoutIds, viewTypeSelector);
        int actualViewType = layoutSelector.getItemViewType(anyItem(), anyPosition());
        Assert.assertThat(actualViewType, Matchers.equalTo(userSelectedViewType));
    }

    @Test
    public void shouldThrowExceptionGivenInvalidUserSelectedViewType() {
        thrownException.expect(RuntimeException.class);
        thrownException.expectMessage("invalid selected view type");
        int invalidUserSelectedViewType = (itemLayoutIds.size()) + (RandomValues.anyInteger());
        ViewTypeSelectable viewTypeSelector = withUserSelectedViewType(invalidUserSelectedViewType);
        MultiItemLayoutSelector layoutSelector = new MultiItemLayoutSelector(itemLayoutIds, viewTypeSelector);
        layoutSelector.getItemViewType(anyItem(), anyPosition());
    }

    @Test
    public void shouldReturnUserSelectedItemlayout() {
        int userSelectedViewType = RandomValues.nextInt(itemLayoutIds.size());
        ViewTypeSelectable viewTypeSelector = withUserSelectedViewType(userSelectedViewType);
        MultiItemLayoutSelector layoutSelector = new MultiItemLayoutSelector(itemLayoutIds, viewTypeSelector);
        int actualItemLayout = layoutSelector.selectLayout(userSelectedViewType);
        Assert.assertThat(actualItemLayout, Matchers.equalTo(itemLayoutIds.get(userSelectedViewType)));
    }
}

