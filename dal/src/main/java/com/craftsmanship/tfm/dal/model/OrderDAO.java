//package com.craftsmanship.tfm.dal.model;
//
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.craftsmanship.tfm.dal.repository.OrderRepository;
//import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
//import com.craftsmanship.tfm.models.ItemPurchase;
//import com.craftsmanship.tfm.models.Order;
//import com.craftsmanship.tfm.persistence.OrderPersistence;
//import com.craftsmanship.tfm.testing.persistence.OrderPersistenceStub;
//
//@Component
//public class OrderDAO implements OrderPersistence{
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(OrderDAO.class);
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    private ItemDAO itemDAO;
//
//    public OrderDAO(ItemDAO itemDAO) {
//        this.itemDAO = itemDAO;
//    }
//
//    @Override
//    public Order create(Order order) throws ItemDoesNotExist{
//        checkItemsExists(order);
//        
//        return orderRepository.save(order);
//    }
//
//    public Order read(long id) {
//        return orderRepository.getOne(id);
//    }
//
//    public List<Order> list() {
//        return orderRepository.findAll();
//    }
//
//    public Order get(long id) {
//        return orderRepository.findById(id).get();
//    }
//
//    public Order update(long id, Order order) {
//        return orderRepository.save(order);
//    }
//
//    public Order delete(long id) {
//        Order deletedOrder = orderRepository.findById(id).get();
//        orderRepository.delete(deletedOrder);
//        return deletedOrder;
//    }
//
//    public int count() {
//        return (int) orderRepository.count();
//    }
//
//    private void checkItemsExists(Order order) throws ItemDoesNotExist {
//        for (ItemPurchase itemPurchase : order.getItemPurchases()) {
//            LOGGER.info("Checking if Item exists: " + itemPurchase.getItem());
//            itemDAO.get(itemPurchase.getItem().getId());
//        }
//    }
//
//}
