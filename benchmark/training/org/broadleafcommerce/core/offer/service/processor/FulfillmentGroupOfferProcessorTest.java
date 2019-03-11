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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.CandidateFulfillmentGroupOffer;
import org.broadleafcommerce.core.offer.domain.CandidateFulfillmentGroupOfferImpl;
import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.CandidateItemOfferImpl;
import org.broadleafcommerce.core.offer.domain.FulfillmentGroupAdjustment;
import org.broadleafcommerce.core.offer.domain.FulfillmentGroupAdjustmentImpl;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferImpl;
import org.broadleafcommerce.core.offer.domain.OfferQualifyingCriteriaXref;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustmentImpl;
import org.broadleafcommerce.core.offer.service.OfferDataItemProvider;
import org.broadleafcommerce.core.offer.service.OfferServiceImpl;
import org.broadleafcommerce.core.offer.service.OfferServiceUtilities;
import org.broadleafcommerce.core.offer.service.discount.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateFulfillmentGroupOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableFulfillmentGroup;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.core.order.dao.OrderItemDao;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetail;
import org.broadleafcommerce.core.order.domain.OrderMultishipOption;
import org.broadleafcommerce.core.order.domain.OrderMultishipOptionImpl;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderMultishipOptionService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupItemRequest;
import org.broadleafcommerce.profile.core.domain.Address;
import org.easymock.EasyMock;


/**
 *
 *
 * @author jfischer
 */
public class FulfillmentGroupOfferProcessorTest extends TestCase {
    protected OfferDao offerDaoMock;

    protected OrderItemDao orderItemDaoMock;

    protected OfferServiceImpl offerService;

    protected final OfferDataItemProvider dataProvider = new OfferDataItemProvider();

    protected OrderService orderServiceMock;

    protected OrderItemService orderItemServiceMock;

    protected FulfillmentGroupItemDao fgItemDaoMock;

    protected FulfillmentGroupService fgServiceMock;

    protected OrderMultishipOptionService multishipOptionServiceMock;

    protected OfferTimeZoneProcessor offerTimeZoneProcessorMock;

    protected OfferServiceUtilities offerServiceUtilitiesMock;

    protected FulfillmentGroupOfferProcessorImpl fgProcessor;

    /**
     * Created to work around a dependency in FulfillmentGroupOfferProcessorImpl to a live application context and
     * system properties service since it uses BLCSystemProperty
     *
     * @author Phillip Verheyden (phillipuniverse)
     */
    protected static class TestableFulfillmentGroupOfferProcessor extends FulfillmentGroupOfferProcessorImpl {
        @Override
        protected boolean getQualifyGroupAcrossAllOrderItems(PromotableFulfillmentGroup fg) {
            return false;
        }
    }

    public void testApplyAllFulfillmentGroupOffersWithOrderItemOffers() throws Exception {
        final ThreadLocal<Order> myOrder = new ThreadLocal<Order>();
        EasyMock.expect(orderItemDaoMock.createOrderItemPriceDetail()).andAnswer(OfferDataItemProvider.getCreateOrderItemPriceDetailAnswer()).anyTimes();
        EasyMock.expect(orderItemDaoMock.createOrderItemQualifier()).andAnswer(OfferDataItemProvider.getCreateOrderItemQualifierAnswer()).atLeastOnce();
        EasyMock.expect(fgServiceMock.addItemToFulfillmentGroup(EasyMock.isA(FulfillmentGroupItemRequest.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getAddItemToFulfillmentGroupAnswer()).anyTimes();
        EasyMock.expect(orderServiceMock.removeItem(EasyMock.isA(Long.class), EasyMock.isA(Long.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getRemoveItemFromOrderAnswer()).anyTimes();
        EasyMock.expect(orderServiceMock.save(EasyMock.isA(Order.class), EasyMock.isA(Boolean.class))).andAnswer(OfferDataItemProvider.getSaveOrderAnswer()).anyTimes();
        EasyMock.expect(offerServiceUtilitiesMock.orderMeetsQualifyingSubtotalRequirements(EasyMock.isA(PromotableOrder.class), EasyMock.isA(Offer.class), EasyMock.isA(HashMap.class))).andReturn(true).anyTimes();
        EasyMock.expect(offerServiceUtilitiesMock.orderMeetsSubtotalRequirements(EasyMock.isA(PromotableOrder.class), EasyMock.isA(Offer.class))).andReturn(true).anyTimes();
        EasyMock.expect(orderServiceMock.getAutomaticallyMergeLikeItems()).andReturn(true).anyTimes();
        EasyMock.expect(orderItemServiceMock.saveOrderItem(EasyMock.isA(OrderItem.class))).andAnswer(OfferDataItemProvider.getSaveOrderItemAnswer()).anyTimes();
        EasyMock.expect(fgItemDaoMock.save(EasyMock.isA(FulfillmentGroupItem.class))).andAnswer(OfferDataItemProvider.getSaveFulfillmentGroupItemAnswer()).anyTimes();
        EasyMock.expect(offerDaoMock.createOrderItemPriceDetailAdjustment()).andAnswer(OfferDataItemProvider.getCreateOrderItemPriceDetailAdjustmentAnswer()).anyTimes();
        EasyMock.expect(offerDaoMock.createFulfillmentGroupAdjustment()).andAnswer(OfferDataItemProvider.getCreateFulfillmentGroupAdjustmentAnswer()).anyTimes();
        EasyMock.expect(orderServiceMock.findOrderById(EasyMock.isA(Long.class))).andAnswer(new org.easymock.IAnswer<Order>() {
            @Override
            public Order answer() throws Throwable {
                return myOrder.get();
            }
        }).anyTimes();
        EasyMock.expect(multishipOptionServiceMock.findOrderMultishipOptions(EasyMock.isA(Long.class))).andAnswer(new org.easymock.IAnswer<java.util.List<OrderMultishipOption>>() {
            @Override
            public java.util.List<OrderMultishipOption> answer() throws Throwable {
                java.util.List<OrderMultishipOption> options = new ArrayList<OrderMultishipOption>();
                PromotableOrder order = dataProvider.createBasicPromotableOrder();
                for (FulfillmentGroup fg : order.getOrder().getFulfillmentGroups()) {
                    Address address = fg.getAddress();
                    for (FulfillmentGroupItem fgItem : fg.getFulfillmentGroupItems()) {
                        for (int j = 0; j < (fgItem.getQuantity()); j++) {
                            OrderMultishipOption option = new OrderMultishipOptionImpl();
                            option.setOrder(order.getOrder());
                            option.setAddress(address);
                            option.setOrderItem(fgItem.getOrderItem());
                            options.add(option);
                        }
                    }
                }
                return options;
            }
        }).anyTimes();
        multishipOptionServiceMock.deleteAllOrderMultishipOptions(EasyMock.isA(Order.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(fgServiceMock.collapseToOneShippableFulfillmentGroup(EasyMock.isA(Order.class), EasyMock.eq(false))).andAnswer(new org.easymock.IAnswer<Order>() {
            @Override
            public Order answer() throws Throwable {
                Order order = ((Order) (EasyMock.getCurrentArguments()[0]));
                order.getFulfillmentGroups().get(0).getFulfillmentGroupItems().addAll(order.getFulfillmentGroups().get(1).getFulfillmentGroupItems());
                order.getFulfillmentGroups().remove(order.getFulfillmentGroups().get(1));
                return order;
            }
        }).anyTimes();
        EasyMock.expect(fgItemDaoMock.create()).andAnswer(OfferDataItemProvider.getCreateFulfillmentGroupItemAnswer()).anyTimes();
        fgItemDaoMock.delete(EasyMock.isA(FulfillmentGroupItem.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(offerTimeZoneProcessorMock.getTimeZone(EasyMock.isA(OfferImpl.class))).andReturn(TimeZone.getTimeZone("CST")).anyTimes();
        replay();
        PromotableOrder promotableOrder = dataProvider.createBasicPromotableOrder();
        Order order = promotableOrder.getOrder();
        myOrder.set(promotableOrder.getOrder());
        java.util.List<PromotableCandidateFulfillmentGroupOffer> qualifiedOffers = new ArrayList<PromotableCandidateFulfillmentGroupOffer>();
        java.util.List<Offer> offers = dataProvider.createFGBasedOffer("order.subTotal.getAmount()>20", "fulfillmentGroup.address.postalCode==75244", PERCENT_OFF);
        offers.addAll(dataProvider.createFGBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", "fulfillmentGroup.address.postalCode==75244", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"));
        offers.get(1).setName("secondOffer");
        offers.addAll(dataProvider.createItemBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"));
        offerService.applyAndSaveOffersToOrder(offers, promotableOrder.getOrder());
        offers.get(0).setTotalitarianOffer(true);
        offerService.applyAndSaveFulfillmentGroupOffersToOrder(offers, promotableOrder.getOrder());
        int fgAdjustmentCount = 0;
        for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
            fgAdjustmentCount += fg.getFulfillmentGroupAdjustments().size();
        }
        // The totalitarian offer that applies to both fg's is not combinable and is a worse offer than the order item offers
        // - it is therefore ignored
        // However, the second combinable fg offer is allowed to be applied.
        TestCase.assertTrue((fgAdjustmentCount == 1));
        promotableOrder = dataProvider.createBasicPromotableOrder();
        myOrder.set(promotableOrder.getOrder());
        offers.get(2).setValue(new BigDecimal("1"));
        offerService.applyAndSaveOffersToOrder(offers, promotableOrder.getOrder());
        offerService.applyAndSaveFulfillmentGroupOffersToOrder(offers, promotableOrder.getOrder());
        fgAdjustmentCount = 0;
        order = promotableOrder.getOrder();
        for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
            fgAdjustmentCount += fg.getFulfillmentGroupAdjustments().size();
        }
        // The totalitarian fg offer is now a better deal than the order item offers, therefore the totalitarian fg offer is applied
        // and the order item offers are removed
        TestCase.assertTrue((fgAdjustmentCount == 2));
        int itemAdjustmentCount = 0;
        for (OrderItem item : order.getOrderItems()) {
            for (OrderItemPriceDetail detail : item.getOrderItemPriceDetails()) {
                itemAdjustmentCount += detail.getOrderItemPriceDetailAdjustments().size();
            }
        }
        // Confirm that the order item offers are removed
        TestCase.assertTrue((itemAdjustmentCount == 0));
        verify();
    }

    public void testApplyAllFulfillmentGroupOffers() {
        EasyMock.expect(offerServiceUtilitiesMock.orderMeetsQualifyingSubtotalRequirements(EasyMock.isA(PromotableOrder.class), EasyMock.isA(Offer.class), EasyMock.isA(HashMap.class))).andReturn(true).anyTimes();
        EasyMock.expect(offerServiceUtilitiesMock.orderMeetsSubtotalRequirements(EasyMock.isA(PromotableOrder.class), EasyMock.isA(Offer.class))).andReturn(true).anyTimes();
        replay();
        PromotableOrder order = dataProvider.createBasicPromotableOrder();
        java.util.List<PromotableCandidateFulfillmentGroupOffer> qualifiedOffers = new ArrayList<PromotableCandidateFulfillmentGroupOffer>();
        java.util.List<Offer> offers = dataProvider.createFGBasedOffer("order.subTotal.getAmount()>20", "fulfillmentGroup.address.postalCode==75244", PERCENT_OFF);
        fgProcessor.filterFulfillmentGroupLevelOffer(order, qualifiedOffers, offers.get(0));
        boolean offerApplied = fgProcessor.applyAllFulfillmentGroupOffers(qualifiedOffers, order);
        TestCase.assertTrue(offerApplied);
        order = dataProvider.createBasicPromotableOrder();
        qualifiedOffers = new ArrayList<PromotableCandidateFulfillmentGroupOffer>();
        offers = dataProvider.createFGBasedOffer("order.subTotal.getAmount()>20", "fulfillmentGroup.address.postalCode==75244", PERCENT_OFF);
        offers.addAll(dataProvider.createFGBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", "fulfillmentGroup.address.postalCode==75244", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"));
        offers.get(1).setName("secondOffer");
        fgProcessor.filterFulfillmentGroupLevelOffer(order, qualifiedOffers, offers.get(0));
        fgProcessor.filterFulfillmentGroupLevelOffer(order, qualifiedOffers, offers.get(1));
        offerApplied = fgProcessor.applyAllFulfillmentGroupOffers(qualifiedOffers, order);
        // the first offer applies to both fulfillment groups, but the second offer only applies to one of the fulfillment groups
        TestCase.assertTrue(offerApplied);
        int fgAdjustmentCount = 0;
        for (PromotableFulfillmentGroup fg : order.getFulfillmentGroups()) {
            fgAdjustmentCount += fg.getCandidateFulfillmentGroupAdjustments().size();
        }
        TestCase.assertTrue((fgAdjustmentCount == 3));
        verify();
    }

    public void testFilterFulfillmentGroupLevelOffer() {
        replay();
        PromotableOrder order = dataProvider.createBasicPromotableOrder();
        java.util.List<PromotableCandidateFulfillmentGroupOffer> qualifiedOffers = new ArrayList<PromotableCandidateFulfillmentGroupOffer>();
        java.util.List<Offer> offers = dataProvider.createFGBasedOffer("order.subTotal.getAmount()>20", "fulfillmentGroup.address.postalCode==75244", PERCENT_OFF);
        fgProcessor.filterFulfillmentGroupLevelOffer(order, qualifiedOffers, offers.get(0));
        // test that the valid fg offer is included
        // No item criteria, so each fulfillment group applies
        TestCase.assertTrue((((qualifiedOffers.size()) == 2) && (qualifiedOffers.get(0).getOffer().equals(offers.get(0)))));
        qualifiedOffers = new ArrayList<PromotableCandidateFulfillmentGroupOffer>();
        offers = dataProvider.createFGBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", "fulfillmentGroup.address.postalCode==75244", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        fgProcessor.filterFulfillmentGroupLevelOffer(order, qualifiedOffers, offers.get(0));
        // test that the valid fg offer is included
        // only 1 fulfillment group has qualifying items
        TestCase.assertTrue((((qualifiedOffers.size()) == 1) && (qualifiedOffers.get(0).getOffer().equals(offers.get(0)))));
        qualifiedOffers = new ArrayList<PromotableCandidateFulfillmentGroupOffer>();
        offers = dataProvider.createFGBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", "fulfillmentGroup.address.postalCode==75240", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"),MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        fgProcessor.filterFulfillmentGroupLevelOffer(order, qualifiedOffers, offers.get(0));
        // test that the invalid fg offer is excluded - zipcode is wrong
        TestCase.assertTrue(((qualifiedOffers.size()) == 0));
        qualifiedOffers = new ArrayList<PromotableCandidateFulfillmentGroupOffer>();
        offers = dataProvider.createFGBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", "fulfillmentGroup.address.postalCode==75244", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test5\"),MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        fgProcessor.filterFulfillmentGroupLevelOffer(order, qualifiedOffers, offers.get(0));
        // test that the invalid fg offer is excluded - no qualifying items
        TestCase.assertTrue(((qualifiedOffers.size()) == 0));
        verify();
    }

    public void testCouldOfferApplyToFulfillmentGroup() {
        replay();
        PromotableOrder order = dataProvider.createBasicPromotableOrder();
        java.util.List<Offer> offers = dataProvider.createFGBasedOffer("order.subTotal.getAmount()>20", "fulfillmentGroup.address.postalCode==75244", PERCENT_OFF);
        boolean couldApply = fgProcessor.couldOfferApplyToFulfillmentGroup(offers.get(0), order.getFulfillmentGroups().get(0));
        // test that the valid fg offer is included
        TestCase.assertTrue(couldApply);
        offers = dataProvider.createFGBasedOffer("order.subTotal.getAmount()>20", "fulfillmentGroup.address.postalCode==75240", PERCENT_OFF);
        couldApply = fgProcessor.couldOfferApplyToFulfillmentGroup(offers.get(0), order.getFulfillmentGroups().get(0));
        // test that the invalid fg offer is excluded
        TestCase.assertFalse(couldApply);
        verify();
    }

    public void testCouldOrderItemMeetOfferRequirement() {
        replay();
        PromotableOrder order = dataProvider.createBasicPromotableOrder();
        java.util.List<Offer> offers = dataProvider.createFGBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", "fulfillmentGroup.address.postalCode==75244", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        OfferQualifyingCriteriaXref xref = offers.get(0).getQualifyingItemCriteriaXref().iterator().next();
        boolean couldApply = fgProcessor.couldOrderItemMeetOfferRequirement(xref.getOfferItemCriteria(), order.getDiscountableOrderItems().get(0));
        // test that the valid fg offer is included
        TestCase.assertTrue(couldApply);
        offers = dataProvider.createFGBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", "fulfillmentGroup.address.postalCode==75244", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        xref = offers.get(0).getQualifyingItemCriteriaXref().iterator().next();
        couldApply = fgProcessor.couldOrderItemMeetOfferRequirement(xref.getOfferItemCriteria(), order.getDiscountableOrderItems().get(0));
        // test that the invalid fg offer is excluded
        TestCase.assertFalse(couldApply);
        verify();
    }

    public void testCouldOfferApplyToOrderItems() {
        replay();
        PromotableOrder order = dataProvider.createBasicPromotableOrder();
        java.util.List<PromotableOrderItem> orderItems = new ArrayList<PromotableOrderItem>();
        for (PromotableOrderItem orderItem : order.getDiscountableOrderItems()) {
            orderItems.add(orderItem);
        }
        java.util.List<Offer> offers = dataProvider.createFGBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", "fulfillmentGroup.address.postalCode==75244", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        CandidatePromotionItems candidates = fgProcessor.couldOfferApplyToOrderItems(offers.get(0), orderItems);
        // test that the valid fg offer is included
        TestCase.assertTrue(((candidates.isMatchedQualifier()) && ((candidates.getCandidateQualifiersMap().size()) == 1)));
        offers = dataProvider.createFGBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", "fulfillmentGroup.address.postalCode==75244", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        candidates = fgProcessor.couldOfferApplyToOrderItems(offers.get(0), orderItems);
        // test that the invalid fg offer is excluded because there are no qualifying items
        TestCase.assertFalse(((candidates.isMatchedQualifier()) && ((candidates.getCandidateQualifiersMap().size()) == 1)));
        verify();
    }

    public class CandidateFulfillmentGroupOfferAnswer implements org.easymock.IAnswer<CandidateFulfillmentGroupOffer> {
        @Override
        public CandidateFulfillmentGroupOffer answer() throws Throwable {
            return new CandidateFulfillmentGroupOfferImpl();
        }
    }

    public class FulfillmentGroupAdjustmentAnswer implements org.easymock.IAnswer<FulfillmentGroupAdjustment> {
        @Override
        public FulfillmentGroupAdjustment answer() throws Throwable {
            return new FulfillmentGroupAdjustmentImpl();
        }
    }

    public class CandidateItemOfferAnswer implements org.easymock.IAnswer<CandidateItemOffer> {
        @Override
        public CandidateItemOffer answer() throws Throwable {
            return new CandidateItemOfferImpl();
        }
    }

    public class OrderItemAdjustmentAnswer implements org.easymock.IAnswer<OrderItemAdjustment> {
        @Override
        public OrderItemAdjustment answer() throws Throwable {
            return new OrderItemAdjustmentImpl();
        }
    }
}

