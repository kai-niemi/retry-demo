package io.roach.retrydemo;

import java.util.concurrent.Callable;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

@Stateless
public class TransactionService {
    @TransactionAttribute(REQUIRES_NEW)
    public <T> T executeWithinTransaction(final Callable<T> task) throws Exception {
        return task.call();
    }
}
