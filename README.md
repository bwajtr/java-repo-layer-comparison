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
* **EBean** (see [implementation](src/main/java/com/clevergang/dbtests/repository/impl/ebean/EBeanDataRepositoryImpl.java)) 
* **JDBI (version 2.77)** (see [implementation](src/main/java/com/clevergang/dbtests/repository/impl/jdbi/JDBIDataRepositoryImpl.java))

I tried to find optimal (== most readable) implementation in every framework, but comments are welcomed! There are a lot of comments in the code explaining why I chose such implementation and some FIXMEs on places which I do not like, but which cannot be implemented differently or which I have troubles to improve...

Furthermore, I considered (and tried to implement) even following frameworks, but it turned out they do not meet the conditions:
* [Speedment](https://github.com/speedment/speedment) - hides SQL language too much and tries to replace with stream operations; not all scenarios can be implemented in it; as of 11/30/2016 and version 3.0.1 the documentation on GitHub is very weak
* [sql2o](http://www.sql2o.org/) - it does not support Spring transaction management at all (tested version 1.6.0-RC3), that's a show stopper - tracking this in [Issue #7](https://github.com/bwajtr/java-persistence-frameworks-comparison/issues/7) 

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
5. JUnit tests will pass when executed from a Gradle build. If you want tests to be passing even from your IDE, then [setup EBean enhancer for your IDE](http://ebean-orm.github.io/docs/setup/enhancement) 
6. Give the scenarios a test run by running one of the test classes and enjoy :)

## Why only non-JPA?

Well, I and my colleagues were always trying to "stick with the standard" in our projects so we used JPA in the past, but after many years of JPA usage (Hibernate mostly), we realized it's counterproductive. In most of our projects, it caused more problems than it helped to solve - especially in big projects (with lots of tables and relations). There are many reasons for those failures - but the biggest issue is that JPA implementations simply turned into bloatware. A lot of strange magic is happening inside and the complexity is so high, that you need a high-class Hibernate "mega expert" in every team so the app actually shows some performance and the code is manageable...

So we dropped JPA completely, started using JDBCTemplate and discovered that we can deliver apps sooner (which was kind of surprising), they are a lot faster (thanks to effective use of DB) and much more robust... This was really relaxing and we do not plan to return to JPA at all... (yes, even for CRUD applications!) 

This project aims to explore other options in the SQL mapping area than just JDBCTemplate. 

## Conclusions/Notes

Please note that following remarks are very subjective, opinionated and do not have to necessarily apply to you.

#### What would I choose
  
1. If a project manager is ok with an additional cost of a license or the project uses one of open source databases (like PostgreSQL) then definitely go with **jOOQ**.
2. If your project uses Oracle, DB2, MSSQL or any other commercial database and additional cost for the jOOQ license is not acceptable, then go with **JDBCTemplate** (for me, personally, it wins over other choices for its maturity and documentation).
  
#### Subjective pros/cons of each framework 

**JDBC Template**
* Pros
    * Feels like you are very close to JDBC itself
    * Implemented all of the scenarios without bigger issues - there were no hidden surprises
    * Very easy batch operations
    * Easy setup
* Cons
    * Methods in JDBCDataRepositoryImpl are not much readable - that's because you have to inline SQL in Java code. It would have been better if Java supported multiline strings.
    * Debug logging could be better  

**jOOQ**
* Pros
  * Very fluent, very easy to write new queries, code is very readable
  * Once setup it's very easy to use, excellent for simple queries
  * Awesome logger debug output
* Cons
  * Paid license for certain databases - it'll be difficult to persuade managers that it's worth it :)
  * Not so much usable for big queries - it's better to use native SQL (see scenario 9.)
  * Weird syntax of batch operations (in case that you do not use UpdatableRecord). But it's not a big deal... 
  
**MyBatis**
* Pros
  * Writing SQL statements in XML mapper file feels good - it's easy to work with parameters.
* Cons
  * quite a lot of files for single DAO implementation (MyBatisDataRepositoryImpl, DataRepositoryMapper and DataRepositoryMapper.xml), though navigation is not such a big deal
  * at version 3.4.0 unable to work with Java8 DateTime types (LocalDate etc.), support possible through 3rd party library (mybatis-types), see build.gradle and <typeHandlers> configuration in mybatis-config.xml
  * can't run batch and non-batch operations in single SqlSession, but have to create completely new SqlSession instead (see configuration in DbTestsApplication class). Surprisingly, this does not necessarily mean that the batch and non-batch operations will be executed in different transactions (as we would expect), so at the end this is not a total drawback, but just inconvenience
  * expected that localCacheScope=STATEMENT is default MyBatis behavior, which is not... I know this is questionable drawback, but it was kind of surprise for me, see mybatis-config.xml
  
**EBean**
* Pros
  * Everything looks very nice - all the scenarios are implemented by very readable code
  * Super simple batch operations (actually it's only about using right method :) ) 
  * Although there are methods which make CRUD operations and Querying super simple, there are still means how to execute plain SQL and even a way how to get the basic JDBC Transaction object, which you can use for core JDBC stuff. That is really good.  
* Cons
  * Necessity to write the entities (I mean @Entity classes) - it would be cool to have some generator for it
  * Necessity of "enhancement" of the entities - this was quite surprising to me - but actually it's basically only about right environment setup (IDE plugin and Gradle plugin) and then you don't have to think about it 
  * Online documentation is quite weak (as of December 1, 2016). A lot of things are hidden in videos and you have to google for details or get into JavaDocs... However, JavaDoc is very good and I generally didn't have a problem to find what I needed in JavaDoc. Also, the API is quite understandable... to sum it up, that weak online documentation is not such a big deal.
  * Logging could be better
  * Allows JPA OneToMany and ManyToOne relations modeling and possibility to "lazy fetch" these relations - actually, I do not like this concept at all as it can lead to potentially very ineffective code. Per documentation and experiences of several people on internet EBean behaves better than full blown JPA implementation in this manner, but you can still be hit by the N+1 problem and all the performance traps, which lazy fetching brings... 
  
**JDBI (version 2.77)**
  * Pros
    * I like the fluent style of creating statements and binding parameters - I'd like to see something like that in JDBC Template
    * Code is generally more readable than jdbc template
    * Quite easy and understandable batch operations    
  * Cons
    * Extremely weak logging :(
    * Very weak documentation (as of 5.12.2016, version 2.77)
    * I don't quite like the necessity to open&close handle for each DAO method -> it's little bit unclear for me if the handle should be opened for each method or if it's ok to open one handle per HTTP request... documentation is not much clear about this...
