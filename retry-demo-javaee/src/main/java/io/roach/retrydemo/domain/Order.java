package io.roach.retrydemo.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "purchase_order") // order reserved word
@NamedQueries({
        @NamedQuery(
                name = Order.BY_USER_ID,
                query = "from Order o "
                        + "join fetch o.customer u "
                        + "where u.id=:userId"
        )
})
@DynamicInsert
@DynamicUpdate
public class Order {
    public static final String BY_USER_ID = "Order.BY_USER_ID";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgen")
    @SequenceGenerator(name = "idgen", sequenceName = "seq_order", allocationSize = 1)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false, name = "date_placed")
    private Date datePlaced;

    @Column(name = "bill_to_first_name")
    private String billToFirstName;

    @Column(name = "bill_to_last_name")
    private String billToLastName;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "address1",
                    column = @Column(name = "bill_address1", length = 255)),
            @AttributeOverride(name = "address2",
                    column = @Column(name = "bill_address2", length = 255)),
            @AttributeOverride(name = "city",
                    column = @Column(name = "bill_city", length = 255)),
            @AttributeOverride(name = "postcode",
                    column = @Column(name = "bill_postcode", length = 16)),
            @AttributeOverride(name = "country",
                    column = @Column(name = "bill_country", length = 16))
    })
    private Address billAddress;

    @Column(name = "deliv_to_first_name")
    private String deliverToFirstName;

    @Column(name = "deliv_to_last_name")
    private String deliverToLastName;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "address1",
                    column = @Column(name = "deliv_address1", length = 255)),
            @AttributeOverride(name = "address2",
                    column = @Column(name = "deliv_address2", length = 255)),
            @AttributeOverride(name = "city",
                    column = @Column(name = "deliv_city", length = 255)),
            @AttributeOverride(name = "postcode",
                    column = @Column(name = "deliv_postcode", length = 16)),
            @AttributeOverride(name = "country",
                    column = @Column(name = "deliv_country", length = 16))
    })
    private Address deliveryAddress;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", length = 64)
    private ShipmentStatus status = ShipmentStatus.PLACED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public Order() {
    }

    public Long getId() {
        return id;
    }

    public Date getDatePlaced() {
        return datePlaced;
    }

    public void setDatePlaced(Date datePlaced) {
        this.datePlaced = datePlaced;
    }

    public String getBillToFirstName() {
        return billToFirstName;
    }

    public void setBillToFirstName(String billToFirstName) {
        this.billToFirstName = billToFirstName;
    }

    public String getBillToLastName() {
        return billToLastName;
    }

    public void setBillToLastName(String billToLastName) {
        this.billToLastName = billToLastName;
    }

    public Address getBillAddress() {
        return billAddress;
    }

    public void setBillAddress(Address billAddress) {
        this.billAddress = billAddress;
    }

    public String getDeliverToFirstName() {
        return deliverToFirstName;
    }

    public void setDeliverToFirstName(String deliverToFirstName) {
        this.deliverToFirstName = deliverToFirstName;
    }

    public String getDeliverToLastName() {
        return deliverToLastName;
    }

    public void setDeliverToLastName(String deliverToLastName) {
        this.deliverToLastName = deliverToLastName;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}

