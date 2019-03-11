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
package org.broadleafcommerce.core.offer.service;


import OfferDiscountType.AMOUNT_OFF;
import OfferDiscountType.PERCENT_OFF;
import OfferRuleType.ORDER;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.dao.CustomerOfferDao;
import org.broadleafcommerce.core.offer.dao.OfferCodeDao;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.CandidateItemOfferImpl;
import org.broadleafcommerce.core.offer.domain.CandidateOrderOffer;
import org.broadleafcommerce.core.offer.domain.CandidateOrderOfferImpl;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferImpl;
import org.broadleafcommerce.core.offer.domain.OfferOfferRuleXref;
import org.broadleafcommerce.core.offer.domain.OfferRule;
import org.broadleafcommerce.core.offer.domain.OfferRuleImpl;
import org.broadleafcommerce.core.offer.domain.OrderAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderAdjustmentImpl;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustmentImpl;
import org.broadleafcommerce.core.offer.service.processor.OfferTimeZoneProcessor;
import org.broadleafcommerce.core.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.core.order.dao.OrderItemDao;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetail;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetailImpl;
import org.broadleafcommerce.core.order.domain.OrderItemQualifier;
import org.broadleafcommerce.core.order.domain.OrderItemQualifierImpl;
import org.broadleafcommerce.core.order.domain.OrderMultishipOption;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderMultishipOptionService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupItemRequest;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.easymock.EasyMock;


/**
 *
 *
 * @author jfischer
 */
public class OfferServiceTest extends TestCase {
    protected OfferServiceImpl offerService;

    protected CustomerOfferDao customerOfferDaoMock;

    protected OfferCodeDao offerCodeDaoMock;

    protected OfferDao offerDaoMock;

    protected OrderItemDao orderItemDaoMock;

    protected OrderService orderServiceMock;

    protected OrderItemService orderItemServiceMock;

    protected FulfillmentGroupItemDao fgItemDaoMock;

    protected OfferDataItemProvider dataProvider = new OfferDataItemProvider();

    protected OfferTimeZoneProcessor offerTimeZoneProcessorMock;

    private FulfillmentGroupService fgServiceMock;

    private OrderMultishipOptionService multishipOptionServiceMock;

    public void testApplyOffersToOrder_Order() throws Exception {
        final ThreadLocal<Order> myOrder = new ThreadLocal<>();
        EasyMock.expect(offerDaoMock.createOrderItemPriceDetailAdjustment()).andAnswer(OfferDataItemProvider.getCreateOrderItemPriceDetailAdjustmentAnswer()).anyTimes();
        OfferServiceTest.CandidateOrderOfferAnswer candidateOrderOfferAnswer = new OfferServiceTest.CandidateOrderOfferAnswer();
        OfferServiceTest.OrderAdjustmentAnswer orderAdjustmentAnswer = new OfferServiceTest.OrderAdjustmentAnswer();
        EasyMock.expect(offerDaoMock.createOrderAdjustment()).andAnswer(orderAdjustmentAnswer).atLeastOnce();
        OfferServiceTest.OrderItemPriceDetailAnswer orderItemPriceDetailAnswer = new OfferServiceTest.OrderItemPriceDetailAnswer();
        EasyMock.expect(orderItemDaoMock.createOrderItemPriceDetail()).andAnswer(orderItemPriceDetailAnswer).atLeastOnce();
        OfferServiceTest.OrderItemQualifierAnswer orderItemQualifierAnswer = new OfferServiceTest.OrderItemQualifierAnswer();
        EasyMock.expect(orderItemDaoMock.createOrderItemQualifier()).andAnswer(orderItemQualifierAnswer).atLeastOnce();
        OfferServiceTest.CandidateItemOfferAnswer candidateItemOfferAnswer = new OfferServiceTest.CandidateItemOfferAnswer();
        OfferServiceTest.OrderItemAdjustmentAnswer orderItemAdjustmentAnswer = new OfferServiceTest.OrderItemAdjustmentAnswer();
        EasyMock.expect(fgServiceMock.addItemToFulfillmentGroup(EasyMock.isA(FulfillmentGroupItemRequest.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getAddItemToFulfillmentGroupAnswer()).anyTimes();
        EasyMock.expect(orderServiceMock.removeItem(EasyMock.isA(Long.class), EasyMock.isA(Long.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getRemoveItemFromOrderAnswer()).anyTimes();
        EasyMock.expect(orderServiceMock.save(EasyMock.isA(Order.class), EasyMock.isA(Boolean.class))).andAnswer(OfferDataItemProvider.getSaveOrderAnswer()).anyTimes();
        EasyMock.expect(orderServiceMock.findOrderById(EasyMock.isA(Long.class))).andAnswer(new org.easymock.IAnswer<Order>() {
            @Override
            public Order answer() throws Throwable {
                return myOrder.get();
            }
        }).anyTimes();
        EasyMock.expect(orderServiceMock.getAutomaticallyMergeLikeItems()).andReturn(true).anyTimes();
        EasyMock.expect(orderItemServiceMock.saveOrderItem(EasyMock.isA(OrderItem.class))).andAnswer(OfferDataItemProvider.getSaveOrderItemAnswer()).anyTimes();
        EasyMock.expect(fgItemDaoMock.save(EasyMock.isA(FulfillmentGroupItem.class))).andAnswer(OfferDataItemProvider.getSaveFulfillmentGroupItemAnswer()).anyTimes();
        EasyMock.expect(multishipOptionServiceMock.findOrderMultishipOptions(EasyMock.isA(Long.class))).andAnswer(new org.easymock.IAnswer<java.util.List<OrderMultishipOption>>() {
            @Override
            public java.util.List<OrderMultishipOption> answer() throws Throwable {
                return new ArrayList<>();
            }
        }).anyTimes();
        multishipOptionServiceMock.deleteAllOrderMultishipOptions(EasyMock.isA(Order.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(fgServiceMock.collapseToOneShippableFulfillmentGroup(EasyMock.isA(Order.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getSameOrderAnswer()).anyTimes();
        EasyMock.expect(fgItemDaoMock.create()).andAnswer(OfferDataItemProvider.getCreateFulfillmentGroupItemAnswer()).anyTimes();
        fgItemDaoMock.delete(EasyMock.isA(FulfillmentGroupItem.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(offerTimeZoneProcessorMock.getTimeZone(EasyMock.isA(OfferImpl.class))).andReturn(TimeZone.getTimeZone("CST")).anyTimes();
        replay();
        Order order = dataProvider.createBasicOrder();
        myOrder.set(order);
        java.util.List<Offer> offers = dataProvider.createOrderBasedOffer("order.subTotal.getAmount()>126", PERCENT_OFF);
        offerService.applyAndSaveOffersToOrder(offers, order);
        int adjustmentCount = order.getOrderAdjustments().size();
        TestCase.assertTrue((adjustmentCount == 1));
        TestCase.assertTrue(order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(116.95)));
        order = dataProvider.createBasicOrder();
        myOrder.set(order);
        offers = dataProvider.createOrderBasedOffer("order.subTotal.getAmount()>126", PERCENT_OFF);
        java.util.List<Offer> offers2 = dataProvider.createItemBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        offers.addAll(offers2);
        offerService.applyAndSaveOffersToOrder(offers, order);
        // with the item offers in play, the subtotal restriction for the order offer is no longer valid
        adjustmentCount = countItemAdjustments(order);
        int qualifierCount = countItemQualifiers(order);
        TestCase.assertTrue((adjustmentCount == 2));
        TestCase.assertTrue((qualifierCount == 2));
        adjustmentCount = order.getOrderAdjustments().size();
        TestCase.assertTrue((adjustmentCount == 0));
        // assertTrue(order.getSubTotal().equals(new Money(124.95D)));
        order = dataProvider.createBasicOrder();
        myOrder.set(order);
        OfferRule orderRule = new OfferRuleImpl();
        // orderRule.setMatchRule("order.subTotal.getAmount()>124");
        orderRule.setMatchRule("order.subTotal.getAmount()>100");
        Offer offer = offers.get(0);
        OfferOfferRuleXref ruleXref = new org.broadleafcommerce.core.offer.domain.OfferOfferRuleXrefImpl(offer, orderRule, ORDER.getType());
        offer.getOfferMatchRulesXref().put(ORDER.getType(), ruleXref);
        offerService.applyAndSaveOffersToOrder(offers, order);
        // now that the order restriction has been lessened, even with the item level discounts applied,
        // the order offer still qualifies
        adjustmentCount = countItemAdjustments(order);
        qualifierCount = countItemQualifiers(order);
        TestCase.assertTrue((adjustmentCount == 2));
        TestCase.assertTrue((qualifierCount == 2));
        adjustmentCount = order.getOrderAdjustments().size();
        TestCase.assertTrue((adjustmentCount == 1));
        TestCase.assertTrue(order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(112.45)));
        TestCase.assertTrue(order.getSubTotal().equals(new Money(124.95)));
        order = dataProvider.createBasicPromotableOrder().getOrder();
        myOrder.set(order);
        // offers.get(0).setCombinableWithOtherOffers(false);
        java.util.List<Offer> offers3 = dataProvider.createOrderBasedOffer("order.subTotal.getAmount()>20", AMOUNT_OFF);
        offers.addAll(offers3);
        offerService.applyAndSaveOffersToOrder(offers, order);
        adjustmentCount = order.getOrderAdjustments().size();
        TestCase.assertTrue((adjustmentCount == 2));
        order = dataProvider.createBasicPromotableOrder().getOrder();
        myOrder.set(order);
        offers.get(0).setCombinableWithOtherOffers(false);
        offerService.applyAndSaveOffersToOrder(offers, order);
        // there is a non combinable order offer now
        adjustmentCount = countItemAdjustments(order);
        qualifierCount = countItemQualifiers(order);
        TestCase.assertTrue((adjustmentCount == 2));
        TestCase.assertTrue((qualifierCount == 2));
        adjustmentCount = order.getOrderAdjustments().size();
        TestCase.assertTrue((adjustmentCount == 1));
        TestCase.assertTrue(order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(112.45)));
        TestCase.assertTrue(order.getSubTotal().equals(new Money(124.95)));
        order = dataProvider.createBasicPromotableOrder().getOrder();
        myOrder.set(order);
        offers.get(0).setTotalitarianOffer(true);
        offerService.applyAndSaveOffersToOrder(offers, order);
        // there is a totalitarian order offer now - it is better than the item offers - the item offers are removed
        adjustmentCount = countItemAdjustments(order);
        qualifierCount = countItemQualifiers(order);
        TestCase.assertTrue((adjustmentCount == 0));
        TestCase.assertTrue((qualifierCount == 0));
        adjustmentCount = order.getOrderAdjustments().size();
        TestCase.assertTrue((adjustmentCount == 1));
        TestCase.assertTrue(order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(116.95)));
        TestCase.assertTrue(order.getSubTotal().equals(new Money(129.95)));
        order = dataProvider.createBasicPromotableOrder().getOrder();
        myOrder.set(order);
        offers.get(0).setValue(new BigDecimal(".05"));
        offers.get(2).setValue(new BigDecimal(".01"));
        offers.get(2).setDiscountType(PERCENT_OFF);
        offerService.applyAndSaveOffersToOrder(offers, order);
        // even though the first order offer is totalitarian, it is worth less than the order item offer, so it is removed.
        // the other order offer is still valid, however, and is included.
        adjustmentCount = countItemAdjustments(order);
        TestCase.assertTrue((adjustmentCount == 2));
        adjustmentCount = order.getOrderAdjustments().size();
        TestCase.assertTrue((adjustmentCount == 1));
        TestCase.assertTrue(order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(124.94)));
        TestCase.assertTrue(order.getSubTotal().equals(new Money(124.95)));
        verify();
    }

    public void testApplyOffersToOrder_Items() throws Exception {
        final ThreadLocal<Order> myOrder = new ThreadLocal<>();
        EasyMock.expect(offerDaoMock.createOrderItemPriceDetailAdjustment()).andAnswer(OfferDataItemProvider.getCreateOrderItemPriceDetailAdjustmentAnswer()).anyTimes();
        OfferServiceTest.CandidateItemOfferAnswer answer = new OfferServiceTest.CandidateItemOfferAnswer();
        OfferServiceTest.OrderItemAdjustmentAnswer answer2 = new OfferServiceTest.OrderItemAdjustmentAnswer();
        OfferServiceTest.OrderItemPriceDetailAnswer orderItemPriceDetailAnswer = new OfferServiceTest.OrderItemPriceDetailAnswer();
        EasyMock.expect(orderItemDaoMock.createOrderItemPriceDetail()).andAnswer(orderItemPriceDetailAnswer).atLeastOnce();
        OfferServiceTest.OrderItemQualifierAnswer orderItemQualifierAnswer = new OfferServiceTest.OrderItemQualifierAnswer();
        EasyMock.expect(orderItemDaoMock.createOrderItemQualifier()).andAnswer(orderItemQualifierAnswer).atLeastOnce();
        EasyMock.expect(orderServiceMock.getAutomaticallyMergeLikeItems()).andReturn(true).anyTimes();
        EasyMock.expect(orderServiceMock.save(EasyMock.isA(Order.class), EasyMock.isA(Boolean.class))).andAnswer(OfferDataItemProvider.getSaveOrderAnswer()).anyTimes();
        EasyMock.expect(orderItemServiceMock.saveOrderItem(EasyMock.isA(OrderItem.class))).andAnswer(OfferDataItemProvider.getSaveOrderItemAnswer()).anyTimes();
        EasyMock.expect(fgItemDaoMock.save(EasyMock.isA(FulfillmentGroupItem.class))).andAnswer(OfferDataItemProvider.getSaveFulfillmentGroupItemAnswer()).anyTimes();
        EasyMock.expect(fgServiceMock.addItemToFulfillmentGroup(EasyMock.isA(FulfillmentGroupItemRequest.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getAddItemToFulfillmentGroupAnswer()).anyTimes();
        EasyMock.expect(orderServiceMock.removeItem(EasyMock.isA(Long.class), EasyMock.isA(Long.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getRemoveItemFromOrderAnswer()).anyTimes();
        EasyMock.expect(multishipOptionServiceMock.findOrderMultishipOptions(EasyMock.isA(Long.class))).andAnswer(new org.easymock.IAnswer<java.util.List<OrderMultishipOption>>() {
            @Override
            public java.util.List<OrderMultishipOption> answer() throws Throwable {
                return new ArrayList<>();
            }
        }).anyTimes();
        EasyMock.expect(orderServiceMock.findOrderById(EasyMock.isA(Long.class))).andAnswer(new org.easymock.IAnswer<Order>() {
            @Override
            public Order answer() throws Throwable {
                return myOrder.get();
            }
        }).anyTimes();
        multishipOptionServiceMock.deleteAllOrderMultishipOptions(EasyMock.isA(Order.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(fgServiceMock.collapseToOneShippableFulfillmentGroup(EasyMock.isA(Order.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getSameOrderAnswer()).anyTimes();
        EasyMock.expect(fgItemDaoMock.create()).andAnswer(OfferDataItemProvider.getCreateFulfillmentGroupItemAnswer()).anyTimes();
        fgItemDaoMock.delete(EasyMock.isA(FulfillmentGroupItem.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(offerTimeZoneProcessorMock.getTimeZone(EasyMock.isA(OfferImpl.class))).andReturn(TimeZone.getTimeZone("CST")).anyTimes();
        replay();
        Order order = dataProvider.createBasicPromotableOrder().getOrder();
        myOrder.set(order);
        java.util.List<Offer> offers = dataProvider.createItemBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        offerService.applyAndSaveOffersToOrder(offers, order);
        int adjustmentCount = countItemAdjustments(order);
        TestCase.assertTrue((adjustmentCount == 2));
        order = dataProvider.createBasicPromotableOrder().getOrder();
        myOrder.set(order);
        offers = dataProvider.createItemBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", "([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        offerService.applyAndSaveOffersToOrder(offers, order);
        adjustmentCount = countItemAdjustments(order);
        // Qualifiers are there, but the targets are not, so no adjustments
        TestCase.assertTrue((adjustmentCount == 0));
        verify();
    }

    public void testBuildOfferListForOrder() throws Exception {
        EasyMock.expect(customerOfferDaoMock.readCustomerOffersByCustomer(EasyMock.isA(Customer.class))).andReturn(new ArrayList<org.broadleafcommerce.core.offer.domain.CustomerOffer>());
        EasyMock.expect(offerDaoMock.readOffersByAutomaticDeliveryType()).andReturn(dataProvider.createCustomerBasedOffer(null, dataProvider.yesterday(), dataProvider.tomorrow(), PERCENT_OFF));
        replay();
        Order order = dataProvider.createBasicPromotableOrder().getOrder();
        java.util.List<Offer> offers = offerService.buildOfferListForOrder(order);
        TestCase.assertTrue(((offers.size()) == 1));
        verify();
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

    public class CandidateOrderOfferAnswer implements org.easymock.IAnswer<CandidateOrderOffer> {
        @Override
        public CandidateOrderOffer answer() throws Throwable {
            return new CandidateOrderOfferImpl();
        }
    }

    public class OrderAdjustmentAnswer implements org.easymock.IAnswer<OrderAdjustment> {
        @Override
        public OrderAdjustment answer() throws Throwable {
            return new OrderAdjustmentImpl();
        }
    }

    public class OrderItemPriceDetailAnswer implements org.easymock.IAnswer<OrderItemPriceDetail> {
        @Override
        public OrderItemPriceDetail answer() throws Throwable {
            return new OrderItemPriceDetailImpl();
        }
    }

    public class OrderItemQualifierAnswer implements org.easymock.IAnswer<OrderItemQualifier> {
        @Override
        public OrderItemQualifier answer() throws Throwable {
            return new OrderItemQualifierImpl();
        }
    }
}

