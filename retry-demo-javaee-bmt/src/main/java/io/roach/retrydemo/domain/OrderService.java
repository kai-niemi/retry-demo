package io.roach.retrydemo.domain;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.NoSuchEntityException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

import org.slf4j.Logger;

import io.roach.retrydemo.TransactionBoundary;
import io.roach.retrydemo.util.Assert;

import static javax.ejb.TransactionAttributeType.NOT_SUPPORTED;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class OrderService {
    @PersistenceContext(unitName = "orderSystemPU")
    private EntityManager entityManager;

    @Inject
    private Logger logger;

    public List<Order> findAllOrders() {
        Assert.isFalse(entityManager.isJoinedToTransaction(), "Expected no transaction!");

        CriteriaQuery<Order> cq = entityManager.getCriteriaBuilder().createQuery(Order.class);
        cq.select(cq.from(Order.class));
        return entityManager.createQuery(cq).getResultList();
    }

    public Order getOrderById(Long orderId) {
        Order order = entityManager.find(Order.class, orderId);
        if (order == null) {
            throw new NoSuchEntityException("ID: " + orderId);
        }
        return order;
    }

    @TransactionBoundary
    public Order placeOrder(Order order) {
        Assert.isTrue(entityManager.isJoinedToTransaction(), "Expected transaction!");

        entityManager.persist(order);
        return order;
    }

    @TransactionBoundary
    public Order updateOrder(Order order) {
        Assert.isTrue(entityManager.isJoinedToTransaction(), "Expected transaction!");

        entityManager.merge(order);
        return order;
    }

    @TransactionBoundary
    public Order updateOrderStatus(Long orderId, ShipmentStatus status, BigDecimal amount,
                                   long commitDelay) {
        Assert.isTrue(entityManager.isJoinedToTransaction(), "Expected transaction!");

        Order order = entityManager.find(Order.class, orderId);
        if (order == null) {
            throw new NoSuchEntityException("ID: " + orderId);
        }

        if (commitDelay > 0) {
            logger.info("Waiting {}ms before write and commit", commitDelay);
            try {
                Thread.sleep(commitDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            logger.info("Proceeding with write");
        }

        order.setStatus(status);

        if (BigDecimal.ZERO.compareTo(amount) != 0) {
            order.setTotalPrice(order.getTotalPrice().add(amount));
        }

        entityManager.merge(order);
        entityManager.flush();

        return order;
    }

    @TransactionBoundary
    public void deleteAll() {
        Assert.isTrue(entityManager.isJoinedToTransaction(), "Expected transaction!");

        Query removeAll = entityManager.createQuery("delete from Order");
        removeAll.executeUpdate();
    }
}
