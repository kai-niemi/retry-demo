# Transaction Retry Demo for JavaEE and BMTs
                                   
This project demonstrates an AOP-driven retry strategy for JavaEE 
apps using the following stack:

- Stateless session beans with bean managed transactions (BMT)
- `@AroundAdvice` interceptor for retries
- `@TransactionBoundary` meta annotation with interceptor binding
- JAX-RS REST endpoint for testing
- TomEE 8 as embedded container with web profile
- JPA and Hibernate for data access

## Prerequisites

- CockroachDB v22.1+ database (tested version, works with any)
- JDK8+ with 1.8 language level (OpenJDK compatible)
- Maven 3+ (optional, embedded)

### Setup

Create the database:

    cockroach sql --insecure --host=localhost -e "CREATE database orders"

Create the schema:

    cockroach sql --insecure --host=locahlost --database orders  < src/resources/conf/create.sql

Start the app:
             
    ../mvnw clean install tomee:run
    
The default listen port is `8090` (can be changed in pom.xml):
    
## Usage

Open another shell and check that the service is up and connected to the DB:

    curl http://localhost:8090/api

### Step 1: Get Order Request Form
           
This prints out an order form template that we will use to create new orders:

    curl http://localhost:8090/api/order/template| jq

alt pipe it to a file:

    curl http://localhost:8090/api/order/template > form.json

### Submit Order Form
   
Create a new purchase order:

```bash
curl http://localhost:8090/api/order -i -X POST \
-H 'Content-Type: application/json' \
-d '{
    "billAddress": {
        "address1": "Street 1.1",
        "address2": "Street 1.2",
        "city": "City 1",
        "country": "Country 1",
        "postcode": "Code 1"
    },
    "customerId": -1,
    "deliveryAddress": {
        "address1": "Street 2.1",
        "address2": "Street 2.2",
        "city": "City 2",
        "country": "Country 2",
        "postcode": "Code 2"
    },
    "requestId": "bc3cba97-dee9-41b2-9110-2f5dfc2c5dae"
}'
```

or:

```bash
curl http://localhost:8090/api/order -H "Content-Type:application/json" -X POST \
-d "@form.json"
```

### Produce a Read/Write Conflict

Assuming we have an order with ID 1 in status `PLACED`. We will now read that order and 
change the status to something else by using concurrent transactions. This is known as the 
`unrepeatable read` conflict, prevented by 1SR from happening.

To have predictable outcome, we'll use two session with a controllable delay between the 
read and write operations. 

Overview of SQL operations:

```bash
select * from purchase_order where id=1; -- T1 
-- status is `PLACED`
wait 5s -- T1 
select * from purchase_order where id=1; -- T2
wait 5s -- T2
update status='CONFIRMED' where id=1; -- T1
update status='PAID' where id=1; -- T2
commit; -- T1
commit; -- T2 ERROR!
```
                     
Run the first command:

```bash
curl http://localhost:8090/api/order/1?status=CONFIRMED\&delay=5000 -i -X PUT
```

In less than 5 sec, run the second command from another session:

```bash
curl http://localhost:8090/api/order/1?status=PAID\&delay=5000 -i -X PUT
```

This will cause a serialization conflict like: `ERROR: restart transaction: TransactionRetryWithProtoRefreshError: WriteTooOldError: write for key /Table/109/1/12/0 at timestamp 1669990868.355588000,0 too old; wrote at 1669990868.778375000,3: "sql txn" meta={id=92409d02 key=/Table/109/1/12/0 pri=0.03022202 epo=0 ts=1669990868.778375000,3 min=1669990868.355588000,0 seq=0} lock=true stat=PENDING rts=1669990868.355588000,0 wto=false gul=1669990868.855588000,0`

The interceptor will however catch it, retry and eventually succeed providing a `200 OK` to
the client.

