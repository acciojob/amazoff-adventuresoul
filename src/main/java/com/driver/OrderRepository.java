package com.driver;

import java.util.*;

import org.apache.catalina.startup.HostRuleSet;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

    private HashMap<String, Order> orderMap;
    private HashMap<String, DeliveryPartner> partnerMap;
    private HashMap<String, HashSet<String>> partnerToOrderMap;
    private HashMap<String, String> orderToPartnerMap;

    public OrderRepository(){
        this.orderMap = new HashMap<String, Order>();
        this.partnerMap = new HashMap<String, DeliveryPartner>();
        this.partnerToOrderMap = new HashMap<String, HashSet<String>>();
        this.orderToPartnerMap = new HashMap<String, String>();
    }

    public void saveOrder(Order order){
        // your code here
        this.orderMap.put(order.getId(), order);
    }
   
    public void savePartner(String partnerId){
        // your code here
        // create a new partner with given partnerId and save it
        this.partnerMap.put(partnerId, new DeliveryPartner(partnerId));
    }

    public void saveOrderPartnerMap(String orderId, String partnerId){
        if(orderMap.containsKey(orderId) && partnerMap.containsKey(partnerId)){
            // your code here
            //add order to given partner's order list
            //increase order count of partner
            //assign partner to this order
            this.orderToPartnerMap.put(orderId, partnerId);
            int presentOrders = this.partnerMap.get(partnerId).getNumberOfOrders();
            this.partnerMap.get(partnerId).setNumberOfOrders(presentOrders+1);
            
            // ensure partner has orderId HashSet
            this.partnerToOrderMap.putIfAbsent(partnerId, new HashSet<>());
            this.partnerToOrderMap.get(partnerId).add(orderId);
        }
    }

    public Order findOrderById(String orderId){
        // your code here
        return this.orderMap.get(orderId);
    }

    public DeliveryPartner findPartnerById(String partnerId){
        // your code here
        return this.partnerMap.get(partnerId);
    }

    public Integer findOrderCountByPartnerId(String partnerId){
        // your code here
        HashSet<String> orderSet =  this.partnerToOrderMap.get(partnerId);
        return (orderSet == null) ? 0: orderSet.size();
    }

    public List<String> findOrdersByPartnerId(String partnerId){
        // your code here
        Set<String> orders = this.partnerToOrderMap.get(partnerId);
        // List<String> ans = new ArrayList<>();

        // for (String s: orders) {
        //     ans.add(s);
        // }

        return (orders == null) ? new ArrayList<>(): new ArrayList<>(orders);
    }

    public List<String> findAllOrders(){
        // your code here
        // return list of all orders
        List<String> ans = new ArrayList<>();
        this.orderMap.forEach((Oid, delTime) -> ans.add(Oid));
        return ans;
    }

    public void deletePartner(String partnerId){
        // your code here
        // delete partner by ID
        if (!this.partnerMap.containsKey(partnerId)) {
            return;
        }

        // update this.orderToPartnerMap by deleting rows with partnerId
        // update this.partnerToOrderMap by deleting rows containing partnerId
        
        Set<String> ordersToRemove = this.partnerToOrderMap.get(partnerId);
        if (ordersToRemove != null) {
            for (String orderId: ordersToRemove) {
                this.orderToPartnerMap.remove(orderId);
            }
            this.partnerToOrderMap.remove(partnerId);
        }

        this.partnerMap.remove(partnerId);
    }

    public void deleteOrder(String orderId){
        // your code here
        // delete order by ID
        if (!this.orderMap.containsKey(orderId)) {
            return;
        }

        this.orderMap.remove(orderId);

        String partnerId = this.orderToPartnerMap.remove(orderId);

        if (partnerId != null && partnerMap.containsKey(partnerId)) {
            int presentDeliveries = this.partnerMap.get(partnerId).getNumberOfOrders();
            this.partnerMap.get(partnerId).setNumberOfOrders(presentDeliveries - 1);
            
            Set<String> orderIds = this.partnerToOrderMap.get(partnerId);
            if (orderIds != null) {
                orderIds.remove(orderId);

                if (orderIds.isEmpty()) {
                    this.partnerToOrderMap.remove(partnerId);
                }
            }
        }
        // update this.orderToPartnerMap by deleting rows with orderId
        // update this.partnerToOrderMap by deleting rows containing orderId
    }

    public Integer findCountOfUnassignedOrders(){
        // your code here
        // Integer totalOrders = this.orderMap.size();
        // Integer assignedOrders = this.orderToPartnerMap.size();
        // return totalOrders - assignedOrders;
        return this.orderMap.size() - this.orderToPartnerMap.size();
    }

    public Integer findOrdersLeftAfterGivenTimeByPartnerId(String timeString, String partnerId){
        // your code here
        Set<String> orderIds = this.partnerToOrderMap.get(partnerId);
        
        if (orderIds == null) {
            return 0;
        }

        Integer definedTime = Integer.parseInt(timeString.substring(0, 2)) * 60 + Integer.parseInt(timeString.substring(3, 5));
        
        Integer count = 0;

        for (String oid: orderIds) {
            Order order = this.orderMap.get(oid);
            if(order != null && order.getDeliveryTime() > definedTime) {
                count++;
            }
        }

        return count;
    }

    public String findLastDeliveryTimeByPartnerId(String partnerId){
        // your code here
        // code should return string in format HH:MM
        Set<String> orderIds = this.partnerToOrderMap.get(partnerId);

        if (orderIds == null || orderIds.isEmpty()) {
            return "00:00";
        }

        int lastTime = 0;
        for (String oid: orderIds) {
            Order order = this.orderMap.get(oid);
            lastTime = Math.max(lastTime, order.getDeliveryTime());
        }

        int hours = lastTime / 60;
        int minutes = lastTime % 60;

        return String.format("%02d:%02d", hours, minutes);
    }
        
}