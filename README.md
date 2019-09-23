# Java persistence frameworks comparison

This project compares usage of **non-JPA** SQL mapping (persistence) frameworks for Java (jOOQ, Spring JDBCTemplate, etc.).
We used it to find out which DB layer would be best during development of https://www.spintrace.com

I'm not comparing performance, but rather how are these frameworks used for everyday tasks.

I prepared some common scenarios, which you typically need to implement a data-centric application, and then I implemented these scenarios using various non-JPA DB layer frameworks. This project should serve
- as a point of reference when deciding for SQL mapping framework 
- as a template of common framework usage scenarios (see scenarios below)
- to document best practices of such common usages (**comments are welcomed!**)

**Use code in the repository as you like (MIT License)**

## Frameworks compared

I have following (subjectively evaluated :)) conditions on frameworks which I choose for consideration:
 
1. The framework should embrace - not hide - SQL language and RDBMS we are using
2. The framework must be mature enough for "enterprise level" use.
3. Can utilize JPA annotations, but must not be full JPA implementation (see "Why only non-JPA?" section below)

With that conditions in respect, following frameworks were compared: 

* **Spring JDBCTemplate** (see [implementation](src/main/java/com/clevergang/dbtests/repository/impl/jdbctemplate/JDBCDataRepositoryImpl.java))
* **jOOQ** (see [implementation](src/main/java/com/clevergang/dbtests/repository/impl/jooq/JooqDataRepositoryImpl.java))
* **MyBatis** (see [implementation](src/main/java/com/clevergang/dbtests/repository/impl/mybatis/MyBatisDataRepositoryImpl.java) and  [mapper](src/main/resources/mybatis/mappers/DataRepositoryMapper.xml))
* **JDBI (version 3.9.1)** (see [implementation](src/main/java/com/clevergang/dbtests/repository/impl/jdbi/JDBIDataRepositoryImpl.java))
* **Vert.x Reactive PosgreSQL Client (version 3.8.0)** (see [implementation](src/main/java/com/clevergang/dbtests/repository/impl/vertxsql/VertxSQLDataRepositoryImpl.java))

I tried to find optimal (== most readable) implementation in every framework, but comments are welcomed! There are a lot of comments in the code explaining why I chose such implementation and some FIXMEs on places which I do not like, but which cannot be implemented differently or which I have troubles to improve.

## Scenarios implemented

These are the scenarios:

1. Fetch single entity based on primary key
2. Fetch list of entities based on condition
3. Save new single entity and return primary key
4. Batch insert multiple entities of the same type and return generated keys
5. Update single existing entity - update all fields of entity at once
6. Fetch many-to-one relation (Company for Department)
7. Fetch one-to-many relation (Departments for Company)
8. Update entities one-to-many relation (Departments in Company) - add two items, update two items and delete one item - all at once
9. Complex select - construct select where conditions based on some boolean conditions + throw in some JOINs
10. Call stored procedure/function and process results
11. Execute query using JDBC simple Statement (not PreparedStatement)
12. Remove single entity based on primary key

Each scenario has it's implementation in the Scenarios class. See Javadoc of [Scenarios](src/main/java/com/clevergang/dbtests/Scenarios.java) methods for a more detailed description of each scenario.

## Model used

![Simple company database model](/SimpleCompanyModel.png?raw=true "Simple company database model")

## How-to

1. Clone the repository
2. Configure PostgreSQL connection details in [application.properties](src/main/resources/application.properties)
3. Create tables and data by running [create-script.sql](sql-updates/create-script.sql)
4. Create one stored procedure by running [register_employee.sql](sql-updates/sql_functions/register_employee.sql)
5. JUnit tests will pass when executed from a Gradle build.
6. Give the scenarios a test run by running one of the test classes and enjoy :)

## Why only non-JPA?

Well, I and my colleagues were always trying to "stick with the standard" in our projects so we used JPA in the past, but after many years of JPA usage (Hibernate mostly), we realized it's counterproductive. In most of our projects, it caused more problems than it helped to solve - especially in big projects (with lots of tables and relations). There are many reasons for those failures - but the biggest issue is that JPA implementations simply turned into bloatware. A lot of strange magic is happening inside and the complexity is so high, that you need a high-class Hibernate "mega expert" in every team so the app actually shows some performance and the code is manageable...

So we dropped JPA completely, started using JDBCTemplate and discovered that we can deliver apps sooner (which was kind of surprising), they are a lot faster (thanks to effective use of DB) and much more robust... This was really relaxing and we do not plan to return to JPA at all... (yes, even for CRUD applications!) 

This project aims to explore other options in the SQL mapping area than just JDBCTemplate. 

## Conclusions/Notes

Please note that following remarks are very subjective, opinionated and do not have to necessarily apply to you.

#### Subjective pros/cons of each framework 

**JDBC Template**
* Pros
    * Feels like you are very close to JDBC itself
    * Easy batch operations
    * Easy setup
* Cons
    * Works only with Spring
    * Cannot return generated IDs after a batch insert
    * Procedural style
    * Methods in JDBCDataRepositoryImpl are not much readable - that's because you have to inline SQL in Java code. It would have been better if Java supported multiline strings.
    * Debug logging could be better  

**jOOQ**
* Pros
  * Fluent style, easy to write new queries, code is very readable
  * Once setup it's very easy to use, excellent for simple queries
  * Awesome logger debug output
* Cons
  * Paid license for certain databases - it'll be difficult to persuade managers that it's worth it :)
  * Not so much usable for big queries - it's better to use native SQL (see scenario 9.)
  * Weird syntax of batch operations (in case that you do not use UpdatableRecord).
  * Requires the metamodel generation (like Criteria API for JPA)
  
**MyBatis**
* Pros
  * Supports multiple template engines.
* Cons
  * External Mapper xml file that needs to be alligned to the java sources.
  * quite a lot of files for single DAO implementation (MyBatisDataRepositoryImpl, DataRepositoryMapper and DataRepositoryMapper.xml)
  * at version 3.4.0 unable to work with Java8 DateTime types (LocalDate etc.), support possible through 3rd party library (mybatis-types), see build.gradle and <typeHandlers> configuration in mybatis-config.xml
  * can't run batch and non-batch operations in single SqlSession, but have to create completely new SqlSession instead (see configuration in DbTestsApplication class).
  * More complex than other solutions.
  * Performace
  
**JDBI (version 3.9.1)**
  * Pros
    * Fluent style of creating statements and binding parameters
    * Code is generally more readable than jdbc template
    * Quite easy and understandable batch operations    
  * Cons
    * Weak logging
    
**Vert.x Reactive PostgreSQL Client (version 3.8.0)**
  * Pros
    * Supports also no-sql DB like MongoDB, Redis and Cassandra.
    * Reactive Programming
    * Fluent and functional API
    * Code is very concise but also grants a low level control
    * API support for any JVM language (Kotlin, Scala, JavaScript, Groovy, Ruby)
  * Cons
    * Manual transaction management on the connection
    * Developers need to be used to Reactive Programming
