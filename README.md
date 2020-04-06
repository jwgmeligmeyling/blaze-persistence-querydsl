Blaze-Persistence extensions for QueryDSL
======
![Java CI](https://github.com/jwgmeligmeyling/blaze-persistence-querydsl/workflows/Java%20CI/badge.svg)

Extensions for creating [Blaze-Persistence](https://github.com/Blazebit/blaze-persistence) queries using QueryDSL 4.

## What is it?

This is a proof of concept for a QueryDSL wrapper for Blaze-Persistence. It currently supports the following:

* CTE's and recursive CTE's
* Subquery joins
* Lateral joins
* Values clauses
* Attribute values clauses
* Window functions
* `group_agg` support
* Utility methods for date/time
* Set operations (`UNION`, `INTERSECT` and `EXCEPT`)
* Named window support

Features for future consideration:

* Fluent builders for CTE's and set operations

This wrapper executes QueryDSL queries through Blaze-Persistence's CriteriaBuilder API and benefits from the strong Blaze-Persistence query benefits.

An alternative approach for QueryDSL integration was already demonstrated in PR https://github.com/Blazebit/blaze-persistence/pull/744.
The approach in this repository intends to use the standard QueryDSL API's instead. 

## Maven configuration

```xml
 <dependencies>
    <dependency>
        <groupId>${project.groupId}</groupId>
        <artifactId>blaze-persistence-querydsl-expressions</artifactId>
        <version>${project.version}</version>
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

## Examples
The following chapters demonstrate some of the possibilities of the blaze-persistence-querydsl-expressions integration.

### Plain query
```java
QTestEntity testEntity = QTestEntity.testEntity;

BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<Tuple>(entityManager, cbf).from(testEntity)
         .select(testEntity.field.as("blep"), testEntity.field.substring(2))
         .where(testEntity.field.length().gt(1));

List<Tuple> fetch = query.fetch();
 ```

### Window functions
```java
 QTestEntity sub = new QTestEntity("sub");
 BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<Tuple>(entityManager, cbf).from(testEntity)
         .select(testEntity.field.as("blep"), WindowExpressions.rowNumber(), WindowExpressions.lastValue(testEntity.field).over().partitionBy(testEntity.id))
         .where(testEntity.id.in(select(sub.id).from(sub)));

 List<Tuple> fetch = query.fetch();
 ```

### Named window functions
```java
 QTestEntity sub = new QTestEntity("sub");
 NamedWindow blep = new NamedWindow("whihi").partitionBy(testEntity.id);

 BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<Tuple>(entityManager, cbf).from(testEntity)
         .window(blep)
         .select(testEntity.field.as("blep"), WindowExpressions.rowNumber().over(blep), WindowExpressions.lastValue(testEntity.field).over(blep))
         .where(testEntity.id.in(select(sub.id).from(sub)));

 List<Tuple> fetch = query.fetch();
 ```

### Regular association joins
```java
 Map<Author, List<Book>> booksByAuthor = new BlazeJPAQuery<>(entityManager, cbf)
         .from(author)
         .innerJoin(author.books, book)
         .transform(GroupBy.groupBy(author).as(GroupBy.list(book)));
```

### Regular entity joins
```java
 QAuthor otherAuthor = new QAuthor("otherAuthor");
 QBook otherBook = new QBook("otherBook");
 Map<Author, List<Book>> booksByAuthor = new BlazeJPAQuery<Tuple>(entityManager, cbf)
         .from(otherAuthor)
         .innerJoin(otherBook).on(otherBook.author.eq(otherAuthor))
         .transform(GroupBy.groupBy(otherAuthor).as(GroupBy.list(otherBook)));
```

### Managed type values clause
```java
 Book theBook = new Book();
 theBook.id = 1337L;
 theBook.name = "test";

 List<Book> fetch = new BlazeJPAQuery<Book>(entityManager, cbf)
         .fromValues(book, Collections.singleton(theBook))
         .select(book)
         .fetch();
 ```

### Managed attribute values clause
```java
 StringPath bookName = Expressions.stringPath("bookName");

 List<String> fetch = new BlazeJPAQuery<>(entityManager, cbf)
         .fromValues(book.name, bookName, Collections.singleton("book"))
         .select(bookName)
         .fetch();   
 ```

### Common Table Expressions
First declare your CTE entity:

```java
 @CTE
 @Entity
 public class IdHolderCte {

     @Id
     Long id;

     String name;

 }
 ```

Next, it can be queried as such:

```java
 List<Long> fetch = new BlazeJPAQuery<TestEntity>(entityManager, cbf)
     .with(idHolderCte, select(
             CTEUtils.bind(idHolderCte.id, book.id),
             CTEUtils.bind(idHolderCte.name, book.name)).from(book))
     .select(idHolderCte.id).from(idHolderCte)
     .fetch();
 ```

Note: Set operations are also allowed in CTE's, and through set operations it is also possible to write recursive CTE's.

### Subquery joins
A limitation of JPQL frequently stumbled opon, is that subqueries cannot be joined. With Blaze-Persistence however, this is perfectly possible:

```java
 QRecursiveEntity recursiveEntity = new QRecursiveEntity("t");

 List<RecursiveEntity> fetch = new BlazeJPAQuery<>(entityManager, cbf)
         .select(recursiveEntity)
         .from(select(recursiveEntity)
                 .from(recursiveEntity)
                 .where(recursiveEntity.parent.name.eq("root1"))
                 .orderBy(recursiveEntity.name.asc())
                 .limit(1L), recursiveEntity)
         .fetch();
 ```

The subquery may project any managed entity, including CTE's.

### Lateral joins
Subquery joins are allowed to access outer query variables, if a lateral join is used.

```java
 QRecursiveEntity t = new QRecursiveEntity("t");
 QRecursiveEntity subT = new QRecursiveEntity("subT");
 QRecursiveEntity subT2 = new QRecursiveEntity("subT2");

 List<Tuple> fetch = new BlazeJPAQuery<>(entityManager, cbf)
         .select(t, subT2)
         .from(t)
         .leftJoin(select(subT).from(t.children, subT).orderBy(subT.id.asc()).limit(1), subT2)
         .lateral()
         .fetch();
```
