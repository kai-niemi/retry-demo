package io.roach.retrydemo.domain;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import io.roach.retrydemo.util.Assert;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/order")
@TransactionAttribute(TransactionAttributeType.NEVER)
public class OrderResource {
    @PersistenceContext(unitName = "orderSystemPU")
    private EntityManager entityManager;

    @Inject
    private OrderService orderService;

    @Inject
    private Logger logger;

    @GET
    @Path("/")
    @Produces({APPLICATION_JSON})
    public List<Order> findOrders() {
        return new ArrayList<>(orderService.findAllOrders());
    }

    @GET
    @Path("/{id}")
    @Produces({APPLICATION_JSON})
    public Order getOrderById(@PathParam("id") Long id) {
        Assert.isFalse(entityManager.isJoinedToTransaction(), "Expected no transaction!");
        return orderService.getOrderById(id);
    }

    @DELETE
    @Path("/")
    @Produces({APPLICATION_JSON})
    public Response deleteOrders() {
        orderService.deleteAll();
        return Response.ok().build();
    }

    @GET
    @Path("/template")
    @Produces({APPLICATION_JSON})
    public OrderRequest getOrderTemplate() {
        Address address1 = new Address();
        address1.setAddress1("Street 1.1");
        address1.setAddress2("Street 1.2");
        address1.setCity("City 1");
        address1.setPostcode("Code 1");
        address1.setCountry("Country 1");

        Address address2 = new Address();
        address2.setAddress1("Street 2.1");
        address2.setAddress2("Street 2.2");
        address2.setCity("City 2");
        address2.setPostcode("Code 2");
        address2.setCountry("Country 2");

        OrderRequest template = new OrderRequest();
        template.setBillAddress(address1);
        template.setDeliveryAddress(address2);
        template.setRequestId(UUID.randomUUID().toString());
        template.setCustomerId(-1L);

        return template;
    }

    @POST
    @Path("/")
    @Consumes({APPLICATION_JSON})
    @Produces({APPLICATION_JSON})
    public Response submitOrder(OrderRequest request) {
        Order order = new Order();
        order.setBillAddress(request.getBillAddress());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setStatus(ShipmentStatus.PLACED);
        order.setDatePlaced(new Date());
        order.setTotalPrice(BigDecimal.TEN);
        return Response.ok(orderService.placeOrder(order)).status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    @Produces({APPLICATION_JSON})
    public Order updateOrderStatus(@PathParam("id") Long id,
                                   @QueryParam("status") ShipmentStatus shipmentStatus,
                                   @DefaultValue("0") @QueryParam("amount") BigDecimal amount,
                                   @DefaultValue("0") @QueryParam("delay") long commitDelay
    ) {
        logger.info("update order id={} status={} amount={} delay={}",
                id, shipmentStatus, amount, commitDelay);

        Assert.isFalse(entityManager.isJoinedToTransaction(), "Expected no transaction!");
        return orderService.updateOrderStatus(id, shipmentStatus, amount, commitDelay);
    }
}