Blaze-Persistence extensions for QueryDSL
======

Extensions for creating Blaze-Persistence queries using QueryDSL.

# What is it?

# Example use

```java
Map<Author, List<Book>> booksByAuthor = new BlazeJPAQuery<TestEntity>(entityManager, criteriaBuilderFactory)
        .from(author)
        .innerJoin(author.books, book)
        .transform(GroupBy.groupBy(author).as(GroupBy.list(book)));
```

# Maven configuration

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