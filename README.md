Blaze-Persistence extensions for QueryDSL
======

Extensions for creating [Blaze-Persistence](https://github.com/Blazebit/blaze-persistence) queries using QueryDSL 4.

## What is it?

This is a proof of concept for a QueryDSL wrapper for Blaze-Persistence. It currently supports the following:

* CTE's and Recursive CTE's
* Values clauses
* Window functions
* `group_agg` support
* Utility methods for date/time

Features for future consideration:

* Set operation support

This wrapper executes QueryDSL queries through Blaze-Persistence's CriteriaBuilder API and benefits from the strong Blaze-Persistence query benefits.

An alternative approach for QueryDSL integration was already demonstrated in PR https://github.com/Blazebit/blaze-persistence/pull/744.
The approach in this repository intends to use the standard QueryDSL API's instead. 

## Example use

```java
Map<Author, List<Book>> booksByAuthor = new BlazeJPAQuery<TestEntity>(entityManager, criteriaBuilderFactory)
        .from(author)
        .innerJoin(author.books, book)
        .transform(GroupBy.groupBy(author).as(GroupBy.list(book)));
```

## Maven configuration

```xml
 <dependencies>
    <dependency>
        <groupId>${project.groupId}</groupId>
        <artifactId>blaze-persistence-querydsl-api</artifactId>
        <version>${project.version}</version>
    </dependency>
    <dependency>
        <groupId>${project.groupId}</groupId>
        <artifactId>blaze-persistence-querydsl-impl</artifactId>
        <version>${project.version}</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>com.blazebit</groupId>
        <artifactId>blaze-persistence-core-impl</artifactId>
        <version>${blaze-persistence.version}</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>com.blazebit</groupId>
        <artifactId>blaze-persistence-integration-hibernate-5.4</artifactId>
        <version>${blaze-persistence.version}</version>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```