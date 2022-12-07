package io.roach.retrydemo;

import java.util.concurrent.Callable;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import io.roach.retrydemo.util.Assert;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

@Stateless
public class TransactionService {
    @PersistenceContext(unitName = "orderSystemPU")
    private EntityManager entityManager;

    @TransactionAttribute(REQUIRES_NEW)
    public <T> T executeWithinTransaction(final Callable<T> task) throws Exception {
        Assert.isTrue(entityManager.isJoinedToTransaction(), "Expected transaction!");
        return task.call();
    }
}
