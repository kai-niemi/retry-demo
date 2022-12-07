package io.roach.retrydemo;

import java.sql.SQLException;
import java.sql.SQLTransactionRollbackException;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.postgresql.util.PSQLState;
import org.slf4j.Logger;

import io.roach.retrydemo.util.Assert;
import io.roach.retrydemo.util.ExceptionUtils;

/**
 * A transaction boundary retry interceptor for bean managed transactions.
 */
@TransactionBoundary
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class TransactionRetryInterceptor {
    public static final int MAX_RETRY_ATTEMPTS = 10;

    public static final int MAX_BACKOFF_TIME_MILLIS = 15000;

    private static final ThreadLocalRandom RAND = ThreadLocalRandom.current();

    @PersistenceContext(unitName = "orderSystemPU")
    private EntityManager entityManager;

    @Inject
    private TransactionService transactionService;

    @Inject
    private Logger logger;

    @AroundInvoke
    public Object aroundTransactionBoundary(InvocationContext ctx) throws Exception {
        Assert.isFalse(entityManager.isJoinedToTransaction(), "Expected no transaction!");

        logger.info("Intercepting transactional method in retry loop: {}", ctx.getMethod().toGenericString());

        for (int attempt = 1; attempt < MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                // Uses REQUIRES_NEW that will suspend current txn
                Object rv = transactionService.executeWithinTransaction(ctx::proceed);
                if (attempt > 1) {
                    logger.info("Recovered from transient error (attempt {}): {}",
                            attempt, ctx.getMethod().toGenericString());
                } else {
                    logger.info("Transactional method completed (attempt {}): {}",
                            attempt, ctx.getMethod().toGenericString());
                }
                return rv;
            } catch (Exception ex) {
                Throwable t = ExceptionUtils.getMostSpecificCause(ex);
                if (t instanceof SQLException) {
                    SQLException sqlException = (SQLException) t;

                    if (PSQLState.SERIALIZATION_FAILURE.getState().equals(sqlException.getSQLState())) {
                        long backoffMillis = Math.min((long) (Math.pow(2, attempt) + RAND.nextInt(0, 1000)),
                                MAX_BACKOFF_TIME_MILLIS);
                        logger.warn("Detected transient error (attempt {}) backoff for {}ms: {}",
                                attempt, backoffMillis, sqlException);
                        try {
                            Thread.sleep(backoffMillis);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    } else {
                        logger.info("Detected non-transient error (propagating): {}", t.getMessage());
                        throw ex;
                    }
                } else {
                    logger.info("Detected non-transient error (propagating): {}", t.getMessage());
                    throw ex;
                }
            }
        }
        throw new SQLTransactionRollbackException("Too many serialization conflicts - giving up retries!");
    }
}
