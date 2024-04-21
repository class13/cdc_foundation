# The CDC Foundation: Simplifying Change Data Capture
This article explains how to use Postgres, Kafka and Debezium to build a foundation for capturing and reacting to 
data changes in real-time.  
Built once, it can be used as a basis for reliable data pipelines, providing downstream systems with guaranteed 
and in-order messages.

## Kafka & Debezium
**Kafka** is an open-source platform for streaming data. 
It acts as a central hub for messages published by various applications.

**Debezium** is an open-source connector that reads the write-ahead log (WAL) of a database 
to capture all changes (creation, updates, and deletions) made in perfect order.

## Introduction to CDC
**CDC (change data capture)** describes identifying and capturing changes made to a database in real-time 
and delivering them to a downstream system such as another microservice or a data warehouse.

## Use Cases of CDC
### The transactional outbox pattern
- [ ] Make this to a refernce

https://microservices.io/patterns/data/transactional-outbox.html  
A service might need to commit changes to the database and send a message to Kafka within one single call. 
But directly writing into the Kafka topic might lead to inconsistencies, 
as the transaction might get rolled back, while the message would remain in the Kafka topic.  

To solve this, instead of writing to Kafka directly, we write in an **Outbox table* instead. 
The write operation into this table is captured and is sent to Kafka asynchronously.  
This pattern might also be convenient for delivering data to other downstream systems besides Kafka.

### Syncing data between systems
#### Syncing data to a data warehouse
In order to gain data insights over an entire ecosystem, the data from many microservices is required. 
It is not a good idea for a central data warehouse to directly access the databases of those services directly.

Instead, CDC can be utilized to replicate data and send it to the data warehouse.

#### Syncing data between applications
When an organization maintains multiple services, data needs to be sent between them.
Kafka is a convenient platform to achieve this as it does not couple the two services directly.

The data changes written into the database of one service are captured, and written to Kafka. 
The messages are read by the second application and persisted in its own database.

### Hooks
It is a common requirement to run a piece of code after a database change has been made, 
and there are multiple options to choose from to make that possible.

**Database triggers** are able to run code reliably, but the code needs to be written in the database’s SQL flavor 
and is deployed to the database directly. 
These triggers are therefore very different from your service’s other code, and this raises the complexity.

**ORMs** (like JPA) often ship with built-in hooks, but those only trigger reliably if everything is going through that ORM
and won’t trigger if your service uses native queries (for example in legacy code).

**Directly implementing** running an action from your application code, at each code location 
where a specific database change is made is an option as well, but might be tedious to do,
as you'll need to find all occurrences and always think about it, when implementing a new occurrence.

Using **CDC** to capture changes on the database directly and reacting to those changes is a convenient option,
as most logic can be implemented in the same language and codebase of the service.  
Additionally, this method supports native queries as well.

## Kafka Connect
For CDC, we need to capture data from the database, and this is often done with Kafka Connect and its connectors.  
We currently use two different connector types, each with a different method to capture changes

### Query-Based CDC - JDBC
A JDBC connector periodically executes a defined query and writes the returned rows into a Kafka topic.  
As the query is customizable, you can easily join tables to create rich data models.

A complex challenge with this method is to define what rows are new. This challenge can be tackled in various ways.
By keeping track of modification timestamps or sequential IDs, new data can be filtered for, 
but this method has considerable limitations and complications.

Both current timestamps and sequential IDs are generated when the insert or update statement is executed, 
not when the transaction is committed. This detail has significant implications.

When rows are updated multiple times during polling intervals, those intermediate changes are missed.  

Data might be committed out of order, leading to out of order messages or entirely missed messages.  

Also, hard-deletes (deleting a row) cannot be captured at all, 
requiring the application to do soft-deletes (representing deletes as a status column), if they need to be captured.

### Log-based CDC - Debezium
A Debezium connector reads the **WAL (write-ahead log)** of Postgres. 
This log contains all changes, including creates, updates and deletes made in the database in chronological order,
and therefore is not limited the same way query-based CDC is.

A Debezium connector can capture changes of multiple tables 
and does not have to be configured with a specific query to execute.
In this way, a Debezium connector can be way more universal, than any query-based process could be.

On the other hand, Debezium is a bit tricky to set up correctly. 
From publications to replication slots, a lot of technical knowledge of Postgres is necessary.

Furthermore, if there are problems with Debezium, they are extremely difficult to understand or fix,
as the WAL is not human-readable and therefore a complete black box.

Also, with Debezium data is always captured change on table/row-level, 
and therefore does not support joins to make desired models, like a query does.

### Choosing Debezium
A universal CDC solution must rely on a log-based method, simply because a query-based one is inherently limited
to one specific query and therefore one specific use-case.

A log-based method (like Debezium) can capture all changes of an entire database with a single universal setup.

## Troubles with multiple connectors
Often Kafka Connect configurations are set up to serve one single use-case, 
which means a team ends up creating and maintaining multiple different connectors.  

Configuring a new connector for each new CDC-based feature takes quite some time to implement, 
as writing the configuration might be troublesome and is not well-supported by most IDEs.

It involves cycles of trial and error, including lengthy deployments as local testing is unfeasible. 
Finished versions often stop and cause incidents, due to edge-cases on production, which are difficult to fix.

Connector configuration options are often limited, and their documentation complicated or lacking.

The best course of action is to work with Kafka Connect as little as possible, 
setting up a universal solution up once and then rarely touch it ever again.

## The CDC Foundation

- [ ] Images goes here

A simple universal CDC solution, involves a single Debezium connector, capturing all changes of the entire database.
This connector writes those changes into a single Kafka topic (the **CDC Monolog**).   
This is the constant part, that is rarely every changed.

All specific logic or complexity is excluded from Kafka Connect,
Specific CDC use-cases are implemented, by consuming and processing this **CDC Monolog** 
in the codebase of the application.  
This is the plastic part, that is adapted with every new feature.

## Technical Implementation
This section is dedicated to describe how previously mentioned CDC use-cases, 
can be implemented on top of a **Debezium CDC Foundation**.

### General

- [ ] Images goes here

A Kafka consumer of the **CDC Monolog** has to process all changes of the database.  
This is relatively expensive, therefore performance is a concern to prevent a bottleneck.

There should be as few direct Kafka consumers of the **CDC Monolog** as possible, preferably just one
This consumer should be implemented in the application that makes the changes in the database, 
as to keep the pipeline easy to understand. 

This consumer should only have two jobs. 
Firstly, ignore everything irrelevant as fast as possible. 
Secondly, re-route to specific **use-case topics**.  

After re-routing, the **use-case topics** can be consumed once more. 
From this point forward, performance is no longer that critical. 

You can communicate with external services, even if they are slow or unreliable.  
You can fetch additional data from the database, 
severely transform the message 
or even perform expensive processing. 

Due to the re-routing, these processes are decoupled 
and therefore do not slow down processing of the underlying foundation.  
These **use-case consumers** do not even necessarily need to be implemented in the same service.

### Transactional Outbox Pattern

- [ ] Images goes here

Implementing a **Transactional Kafka Outbox** on top of the **CDC Foundation** is straight forward. 

The service writes a message event into an **Outbox Table**.  
This message contains the value and key of the message of the topic in which it should be written into Kafka.

The **CDC Foundation** ensures, that this new row is received in the **CDC Monolog Consumer*. 
The **Kafka Outbox Event Handler** subscribes to the **Kafka Outbox Table**, 
and therefore receives all messages related to it.

Upon delivery, the **Kafka Outbox Event Handler** writes a new message, 
containing the persisted value and key into the declared target topic.

Because the service is already responsible for writing the finished message into the table,
no additional logic, like transformation, is needed after that point 
and therefore no **use-case consumer** is needed.

### Syncing data between systems
The following section will show how to duplicate transactional data to a data warehouse.  
While a data warehouse is used as a specific exemple,the principles are still applicable 
for duplicating data to any other system.

#### Providing row-level events

- [ ] Images goes here

Capturing row-level changes and sending them to a **DWH (data warehouse)** is simple, 
as the CDC Foundation already captures such events. 
The events simply have to be re-routed to specific topics.

A **DWH Sync Event Handler** subscribes to all relevant tables and re-routes the events into dedicated **DWH Topics**.

#### Providing aggregates

- [ ] Images goes here

Sending enriched models, made up from data of multiple tables, is also possible.
 
Events are not sent directly to the data warehouse, instead they are consumed with the **DWH Enrichment Consumer**, 
implemented in the application.  
The **DWH Enrichment Consumer**, fetches all relevant enrichment data from the database and creates a new model.  
This model is then sent to a new Kafka topic.

### Hooks

- [ ] Images goes here

Implementing hooks on top of the CDC Foundation involves implementing a **Hook Event Handler**, 
that filters for relevant events and re-routes them to a dedicated topic.

This topic is then consumed by a **Hook Event Consumer** that executes the hook’s logic.

It might seem more straight-forward to implement the hook directly in the **Hook Event Handler**, 
but remember, the **CDC Monolog Consumer** has to process all database changes. 
Executing any kind of complex action within the **CDC Monolog Consumer** can lead to a bottleneck.

## Proof of Concept
- [ ] write this section / reference other doc
- [ ] include images