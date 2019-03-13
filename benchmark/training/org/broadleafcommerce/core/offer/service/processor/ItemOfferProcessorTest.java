/**
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 *
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.offer.service.processor;


import OfferDiscountType.PERCENT_OFF;
import OfferItemRestrictionRuleType.QUALIFIER_TARGET;
import OfferItemRestrictionRuleType.TARGET;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.CandidateItemOfferImpl;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferQualifyingCriteriaXref;
import org.broadleafcommerce.core.offer.domain.OfferTargetCriteriaXref;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustmentImpl;
import org.broadleafcommerce.core.offer.service.OfferDataItemProvider;
import org.broadleafcommerce.core.offer.service.OfferServiceImpl;
import org.broadleafcommerce.core.offer.service.discount.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateItemOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.core.order.dao.OrderItemDao;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderMultishipOptionService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.easymock.IAnswer;


/**
 *
 *
 * @author jfischer
 */
public class ItemOfferProcessorTest extends TestCase {
    protected OfferDao offerDaoMock;

    protected OrderItemDao orderItemDaoMock;

    protected OrderService orderServiceMock;

    protected OfferServiceImpl offerService;

    protected OrderItemService orderItemServiceMock;

    protected FulfillmentGroupItemDao fgItemDaoMock;

    protected OfferDataItemProvider dataProvider = new OfferDataItemProvider();

    protected FulfillmentGroupService fgServiceMock;

    protected OrderMultishipOptionService multishipOptionServiceMock;

    protected OfferTimeZoneProcessor offerTimeZoneProcessorMock;

    protected ItemOfferProcessorImpl itemProcessor;

    public void testFilterItemLevelOffer() throws Exception {
        replay();
        List<PromotableCandidateItemOffer> qualifiedOffers = new ArrayList<PromotableCandidateItemOffer>();
        List<Offer> offers = dataProvider.createItemBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, null, null);
        PromotableOrder order = dataProvider.createBasicPromotableOrder();
        itemProcessor.filterItemLevelOffer(order, qualifiedOffers, offers.get(0));
        // test that the valid order item offer is included - legacy format - no qualifier
        // since there's no qualifier, both items can apply
        // This line is commented out because we are no longer creating legacy offers.
        // assertTrue(qualifiedOffers.size() == 2 && qualifiedOffers.get(0).getOffer().equals(offers.get(0)) && qualifiedOffers.get(1).getOffer().equals(offers.get(0)));
        qualifiedOffers = new ArrayList<PromotableCandidateItemOffer>();
        offers = dataProvider.createItemBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        itemProcessor.filterItemLevelOffer(order, qualifiedOffers, offers.get(0));
        // test that the valid order item offer is included
        // there is a qualifier and the item qualifying criteria requires only 1, therefore there will be only one qualifier in the qualifiers map
        // we don't know the targets yet, so there's only one CandidateItemOffer for now
        TestCase.assertTrue(((((qualifiedOffers.size()) == 1) && (qualifiedOffers.get(0).getOffer().equals(offers.get(0)))) && ((qualifiedOffers.get(0).getCandidateQualifiersMap().size()) == 1)));
        // Add a subtotal requirement that will be met by the item offer.
        qualifiedOffers = new ArrayList<PromotableCandidateItemOffer>();
        offers = dataProvider.createItemBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        offers.get(0).setQualifyingItemSubTotal(new Money(1));
        itemProcessor.filterItemLevelOffer(order, qualifiedOffers, offers.get(0));
        // test that the valid order item offer is included
        // there is a qualifier and the item qualifying criteria requires only 1, therefore there will be only one qualifier in the qualifiers map
        // we don't know the targets yet, so there's only one CandidateItemOffer for now
        TestCase.assertTrue(((((qualifiedOffers.size()) == 1) && (qualifiedOffers.get(0).getOffer().equals(offers.get(0)))) && ((qualifiedOffers.get(0).getCandidateQualifiersMap().size()) == 1)));
        // Add a subtotal requirement that will not be met by the item offer.
        qualifiedOffers = new ArrayList<PromotableCandidateItemOffer>();
        offers = dataProvider.createItemBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        offers.get(0).setQualifyingItemSubTotal(new Money(99999));
        itemProcessor.filterItemLevelOffer(order, qualifiedOffers, offers.get(0));
        // Since the item qualification subTotal is not met, the qualified offer size should
        // be zero.
        TestCase.assertTrue(((qualifiedOffers.size()) == 0));
        qualifiedOffers = new ArrayList<PromotableCandidateItemOffer>();
        offers = dataProvider.createItemBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", "([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        itemProcessor.filterItemLevelOffer(order, qualifiedOffers, offers.get(0));
        // test that the invalid order item offer is excluded
        TestCase.assertTrue(((qualifiedOffers.size()) == 0));
        verify();
    }

    public void testCouldOfferApplyToOrder() throws Exception {
        replay();
        PromotableOrder order = dataProvider.createBasicPromotableOrder();
        List<Offer> offers = dataProvider.createItemBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, null, null);
        boolean couldApply = itemProcessor.couldOfferApplyToOrder(offers.get(0), order, order.getDiscountableOrderItems().get(0), order.getFulfillmentGroups().get(0));
        // test that the valid order item offer is included
        TestCase.assertTrue(couldApply);
        offers = dataProvider.createItemBasedOfferWithItemCriteria("order.subTotal.getAmount()==0", PERCENT_OFF, null, null);
        couldApply = itemProcessor.couldOfferApplyToOrder(offers.get(0), order, order.getDiscountableOrderItems().get(0), order.getFulfillmentGroups().get(0));
        // test that the invalid order item offer is excluded
        TestCase.assertFalse(couldApply);
        verify();
    }

    public void testCouldOrderItemMeetOfferRequirement() throws Exception {
        replay();
        PromotableOrder order = dataProvider.createBasicPromotableOrder();
        List<Offer> offers = dataProvider.createItemBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        OfferQualifyingCriteriaXref xref = offers.get(0).getQualifyingItemCriteriaXref().iterator().next();
        boolean couldApply = itemProcessor.couldOrderItemMeetOfferRequirement(xref.getOfferItemCriteria(), order.getDiscountableOrderItems().get(0));
        // test that the valid order item offer is included
        TestCase.assertTrue(couldApply);
        offers = dataProvider.createItemBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", "([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        xref = offers.get(0).getQualifyingItemCriteriaXref().iterator().next();
        couldApply = itemProcessor.couldOrderItemMeetOfferRequirement(xref.getOfferItemCriteria(), order.getDiscountableOrderItems().get(0));
        // test that the invalid order item offer is excluded
        TestCase.assertFalse(couldApply);
        verify();
    }

    public void testCouldOfferApplyToOrderItems() throws Exception {
        replay();
        PromotableOrder order = dataProvider.createBasicPromotableOrder();
        List<Offer> offers = dataProvider.createItemBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        List<PromotableOrderItem> orderItems = new ArrayList<PromotableOrderItem>();
        for (PromotableOrderItem orderItem : order.getDiscountableOrderItems()) {
            orderItems.add(orderItem);
        }
        CandidatePromotionItems candidates = itemProcessor.couldOfferApplyToOrderItems(offers.get(0), orderItems);
        // test that the valid order item offer is included
        // both cart items are valid for qualification and target
        TestCase.assertTrue(((((((candidates.isMatchedQualifier()) && ((candidates.getCandidateQualifiersMap().size()) == 1)) && ((candidates.getCandidateQualifiersMap().values().iterator().next().size()) == 2)) && (candidates.isMatchedTarget())) && ((candidates.getCandidateTargetsMap().size()) == 1)) && ((candidates.getCandidateTargetsMap().values().iterator().next().size()) == 2)));
        offers = dataProvider.createItemBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", "([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        candidates = itemProcessor.couldOfferApplyToOrderItems(offers.get(0), orderItems);
        // test that the invalid order item offer is excluded because there are no qualifying items
        TestCase.assertFalse(((candidates.isMatchedQualifier()) && ((candidates.getCandidateQualifiersMap().size()) == 1)));
        verify();
    }

    public void testApplyAllItemOffers() throws Exception {
        replay();
        Order order = dataProvider.createBasicOrder();
        Offer offer1 = dataProvider.createItemBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))").get(0);
        offer1.setId(1L);
        List<Offer> offers = new ArrayList<Offer>();
        offers.add(offer1);
        List<PromotableCandidateItemOffer> qualifiedOffers = new ArrayList<PromotableCandidateItemOffer>();
        order.updatePrices();
        offerService.applyAndSaveOffersToOrder(offers, order);
        TestCase.assertTrue(((order.getTotalAdjustmentsValue().getAmount().doubleValue()) > 0));
        order = dataProvider.createBasicOrder();
        qualifiedOffers = new ArrayList<PromotableCandidateItemOffer>();
        offer1.setApplyDiscountToSalePrice(false);
        getSku().setSalePrice(new Money(1.0));
        getSku().setSalePrice(new Money(1.0));
        order.updatePrices();
        offerService.applyAndSaveOffersToOrder(offers, order);
        TestCase.assertTrue(((order.getTotalAdjustmentsValue().getAmount().doubleValue()) == 0));
        verify();
    }

    public void testApplyAdjustments() throws Exception {
        replay();
        Order order = dataProvider.createBasicOrder();
        Offer offer1 = dataProvider.createItemBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))").get(0);
        offer1.setId(1L);
        OfferQualifyingCriteriaXref xref = offer1.getQualifyingItemCriteriaXref().iterator().next();
        xref.getOfferItemCriteria().setQuantity(2);
        offer1.setCombinableWithOtherOffers(false);
        Offer offer2 = dataProvider.createItemBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))").get(0);
        offer2.setId(2L);
        List<Offer> offerListWithOneOffer = new ArrayList<Offer>();
        offerListWithOneOffer.add(offer1);
        List<Offer> offerListWithTwoOffers = new ArrayList<Offer>();
        offerListWithTwoOffers.add(offer1);
        offerListWithTwoOffers.add(offer2);
        order.updatePrices();
        offerService.applyAndSaveOffersToOrder(offerListWithOneOffer, order);
        TestCase.assertTrue(((checkOrderItemOfferAppliedCount(order)) == 1));
        TestCase.assertTrue(((checkOrderItemOfferAppliedQuantity(order, offer1)) == 1));
        // Add the second offer.   The first was nonCombinable so it should still be 1
        order = dataProvider.createBasicOrder();
        order.updatePrices();
        offerService.applyAndSaveOffersToOrder(offerListWithTwoOffers, order);
        TestCase.assertTrue(((checkOrderItemOfferAppliedCount(order)) == 2));
        TestCase.assertTrue(((checkOrderItemOfferAppliedQuantity(order, offer2)) == 2));
        TestCase.assertTrue(((checkOrderItemOfferAppliedQuantity(order, offer1)) == 0));
        // Reset offer1 to combinable.   Now both should be applied.
        offer1.setCombinableWithOtherOffers(true);
        order = dataProvider.createBasicOrder();
        order.updatePrices();
        offerService.applyAndSaveOffersToOrder(offerListWithTwoOffers, order);
        TestCase.assertTrue(((checkOrderItemOfferAppliedCount(order)) == 2));
        TestCase.assertTrue(((checkOrderItemOfferAppliedQuantity(order, offer2)) == 2));
        TestCase.assertTrue(((checkOrderItemOfferAppliedQuantity(order, offer1)) == 0));
        // Offer 1 back to nonCombinable but don't allow discount to the sale price
        // and make the sale price a better overall offer
        offer1.setCombinableWithOtherOffers(false);
        offer1.setApplyDiscountToSalePrice(false);
        order = dataProvider.createBasicOrder();
        getSku().setSalePrice(new Money(10.0));
        order.updatePrices();
        offerService.applyAndSaveOffersToOrder(offerListWithOneOffer, order);
        TestCase.assertTrue(((checkOrderItemOfferAppliedCount(order)) == 0));
        // Try again with two offers.   The second should be applied.
        order.updatePrices();
        offerService.applyAndSaveOffersToOrder(offerListWithTwoOffers, order);
        TestCase.assertTrue(((checkOrderItemOfferAppliedCount(order)) == 2));
        // Trying with 2nd offer as nonCombinable.
        offer1.setCombinableWithOtherOffers(true);
        getSku().setSalePrice(null);
        offer2.setCombinableWithOtherOffers(false);
        order.updatePrices();
        offerService.applyAndSaveOffersToOrder(offerListWithOneOffer, order);
        TestCase.assertTrue(((checkOrderItemOfferAppliedCount(order)) == 1));
        order.updatePrices();
        offerService.applyAndSaveOffersToOrder(offerListWithTwoOffers, order);
        TestCase.assertTrue(((checkOrderItemOfferAppliedQuantity(order, offer2)) == 2));
        TestCase.assertTrue(((checkOrderItemOfferAppliedQuantity(order, offer1)) == 0));
        // Set qualifying criteria quantity to 1
        // Set qualifying target criteria to 2
        order = dataProvider.createBasicOrder();
        xref = offer1.getQualifyingItemCriteriaXref().iterator().next();
        xref.getOfferItemCriteria().setQuantity(1);
        OfferTargetCriteriaXref targetXref = offer1.getTargetItemCriteriaXref().iterator().next();
        targetXref.getOfferItemCriteria().setQuantity(2);
        order.updatePrices();
        offerService.applyAndSaveOffersToOrder(offerListWithOneOffer, order);
        TestCase.assertTrue(((checkOrderItemOfferAppliedQuantity(order, offer1)) == 2));
        // Reset both offers to combinable and the qualifiers as allowing duplicate QUALIFIERs
        // and Targets
        offer1.setCombinableWithOtherOffers(true);
        offer2.setCombinableWithOtherOffers(true);
        offer1.setOfferItemQualifierRuleType(QUALIFIER_TARGET);
        offer1.setOfferItemTargetRuleType(QUALIFIER_TARGET);
        offer2.setOfferItemQualifierRuleType(QUALIFIER_TARGET);
        offer2.setOfferItemTargetRuleType(QUALIFIER_TARGET);
        order = dataProvider.createBasicOrder();
        order.updatePrices();
        offerService.applyAndSaveOffersToOrder(offerListWithTwoOffers, order);
        TestCase.assertTrue(((checkOrderItemOfferAppliedCount(order)) == 4));
        TestCase.assertTrue(((checkOrderItemOfferAppliedQuantity(order, offer2)) == 2));
        TestCase.assertTrue(((checkOrderItemOfferAppliedQuantity(order, offer1)) == 2));
        verify();
    }

    public void testApplyItemQualifiersAndTargets() throws Exception {
        replay();
        List<PromotableCandidateItemOffer> qualifiedOffers = new ArrayList<PromotableCandidateItemOffer>();
        Offer offer1 = dataProvider.createItemBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))").get(0);
        offer1.setId(1L);
        Offer offer2 = dataProvider.createItemBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))").get(0);
        offer2.setId(2L);
        OfferTargetCriteriaXref targetXref = offer2.getTargetItemCriteriaXref().iterator().next();
        targetXref.getOfferItemCriteria().setQuantity(4);
        offer2.getQualifyingItemCriteriaXref().clear();
        offer2.setOfferItemTargetRuleType(TARGET);
        Offer offer3 = dataProvider.createItemBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))").get(0);
        PromotableOrder promotableOrder = dataProvider.createBasicPromotableOrder();
        itemProcessor.filterItemLevelOffer(promotableOrder, qualifiedOffers, offer1);
        TestCase.assertTrue(((((qualifiedOffers.size()) == 1) && (qualifiedOffers.get(0).getOffer().equals(offer1))) && ((qualifiedOffers.get(0).getCandidateQualifiersMap().size()) == 1)));
        itemProcessor.filterItemLevelOffer(promotableOrder, qualifiedOffers, offer2);
        TestCase.assertTrue(((((qualifiedOffers.size()) == 2) && (qualifiedOffers.get(1).getOffer().equals(offer2))) && ((qualifiedOffers.get(1).getCandidateQualifiersMap().size()) == 0)));
        itemProcessor.filterItemLevelOffer(promotableOrder, qualifiedOffers, offer3);
        TestCase.assertTrue(((((qualifiedOffers.size()) == 3) && (qualifiedOffers.get(2).getOffer().equals(offer3))) && ((qualifiedOffers.get(2).getCandidateQualifiersMap().size()) == 1)));
        // Try with just the second offer.   Expect to get 4 targets based on the offer having no qualifiers required
        // and targeting category test1 or test2 and that the offer requires 4 target criteria.
        Order order = dataProvider.createBasicOrder();
        List<Offer> offerList = new ArrayList<Offer>();
        offerList.add(offer2);
        offerService.applyAndSaveOffersToOrder(offerList, order);
        TestCase.assertTrue(((checkOrderItemOfferAppliedQuantity(order, offer2)) == 4));
        TestCase.assertTrue(((countPriceDetails(order)) == 3));
        // Now try with both offers.   Since the targets can be reused, we expect to have 4 targets on offer2
        // and 1 target on offer1
        order = dataProvider.createBasicOrder();
        offerList.add(offer1);// add in second offer (which happens to be offer1)

        offerService.applyAndSaveOffersToOrder(offerList, order);
        TestCase.assertTrue(((checkOrderItemOfferAppliedQuantity(order, offer2)) == 4));
        TestCase.assertTrue(((countPriceDetails(order)) == 3));
        // All three offers - offer 3 is now higher priority so the best offer (offer 2) won't be applied
        order = dataProvider.createBasicOrder();
        offerList.add(offer3);// add in second offer (which happens to be offer1)

        offer3.setPriority((-1));
        offerService.applyAndSaveOffersToOrder(offerList, order);
        TestCase.assertTrue(((checkOrderItemOfferAppliedQuantity(order, offer3)) == 2));
        TestCase.assertTrue(((countPriceDetails(order)) == 4));
        verify();
    }

    public class Answer implements IAnswer<CandidateItemOffer> {
        @Override
        public CandidateItemOffer answer() throws Throwable {
            return new CandidateItemOfferImpl();
        }
    }

    public class Answer2 implements IAnswer<OrderItemAdjustment> {
        @Override
        public OrderItemAdjustment answer() throws Throwable {
            return new OrderItemAdjustmentImpl();
        }
    }
}

