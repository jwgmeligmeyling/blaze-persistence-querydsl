Blaze-Persistence extensions for QueryDSL
======

Extensions for creating [Blaze-Persistence](https://github.com/Blazebit/blaze-persistence) queries using QueryDSL 4.

## What is it?

This is a proof of concept for a QueryDSL wrapper for Blaze-Persistence.

Features for future consideration:

* CTE support
* Set operation support
* Utility methods for date/time
* `group_agg` support
* Window function support

In its current state this wrapper only supports the standard JPQL / QueryDSL operations, but executes these through Blaze-Persistence's CriteriaBuilder API.

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