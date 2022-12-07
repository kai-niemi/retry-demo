package io.roach.retrydemo.domain;

import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "customer")
@NamedQueries({
        @NamedQuery(
                name = Customer.QUERY_BY_USERNAME,
                query = "from Customer u "
                        + "where u.userName = :userName"
        )
})
@DynamicInsert
@DynamicUpdate
public class Customer {
    public static final String QUERY_BY_USERNAME = "Customer.findByName";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgen")
    @SequenceGenerator(name = "idgen", sequenceName = "seq_customer", allocationSize = 1)
    private Long id;

    @Column(length = 15, nullable = false, unique = true, name = "user_name")
    private String userName;

    @Column(length = 128, nullable = false)
    private String password;

    @Column(length = 128, name = "first_name")
    private String firstName;

    @Column(length = 128, name = "last_name")
    private String lastName;

    @Column(length = 128)
    private String telephone;

    @Column(length = 128)
    private String email;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "address1",
                    column = @Column(name = "address1", length = 255)),
            @AttributeOverride(name = "address2",
                    column = @Column(name = "address2", length = 255)),
            @AttributeOverride(name = "city",
                    column = @Column(name = "city", length = 255)),
            @AttributeOverride(name = "postcode",
                    column = @Column(name = "postcode", length = 16)),
            @AttributeOverride(name = "country.code",
                    column = @Column(name = "country_code", length = 16)),
            @AttributeOverride(name = "country.name",
                    column = @Column(name = "country_name", length = 36))
    })
    private Address address = new Address();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false, updatable = false)
    private Date created = new Date();

    public Customer() {
    }

    public Customer(String userName) {
        this.userName = userName;
    }
}
