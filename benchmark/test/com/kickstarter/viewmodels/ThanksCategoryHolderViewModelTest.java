package com.kickstarter.viewmodels;


import ThanksCategoryHolderViewModel.ViewModel;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.mock.factories.CategoryFactory;
import com.kickstarter.models.Category;
import org.junit.Test;
import rx.observers.TestSubscriber;


public final class ThanksCategoryHolderViewModelTest extends KSRobolectricTestCase {
    private ViewModel vm;

    private final TestSubscriber<String> categoryName = new TestSubscriber();

    private final TestSubscriber<Category> notifyDelegateOfCategoryClick = new TestSubscriber();

    @Test
    public void testCategoryName() {
        final Category category = CategoryFactory.musicCategory();
        setUpEnvironment(environment());
        this.vm.getInputs().configureWith(category);
        this.categoryName.assertValues(category.name());
    }

    @Test
    public void testCategoryViewClicked() {
        final Category category = CategoryFactory.bluesCategory();
        setUpEnvironment(environment());
        this.vm.getInputs().configureWith(category);
        this.vm.getInputs().categoryViewClicked();
        this.notifyDelegateOfCategoryClick.assertValues(category);
    }
}

