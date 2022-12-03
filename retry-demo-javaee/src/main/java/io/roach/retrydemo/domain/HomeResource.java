package io.roach.retrydemo.domain;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;

@Path("/")
@TransactionAttribute(TransactionAttributeType.NEVER)
public class HomeResource {
    @PersistenceContext(unitName = "orderSystemPU")
    private EntityManager entityManager;

    @Inject
    private Logger logger;

    @GET
    @Produces({"text/plain", "application/json"})
    public String ping() {
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
        return "Hello from " +
                entityManager.createNativeQuery("select version()").getSingleResult();
    }
}
