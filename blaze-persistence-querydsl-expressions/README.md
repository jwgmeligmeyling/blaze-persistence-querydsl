# Blaze-Persistence QueryDSL expressions

The `blaze-persistence-querydsl-expressions` module implements an extended expression model for Blaze-Persistence JPQL Next.
The module provides a `BlazeJPAQuery` as a default implementation of `ExtendedJPAQuery`, which extends the all familiar `JPQLQuery`. `BlazeJPAQuery` is analog to `JPAQuery`.
Users can implement extensions on top of `ExtendedJPAQuery` by extending `AbstractBlazeJPAQuery`.

`BlazeJPAQuery` can be serialized using the `JPQLNextSerializer`, and may be rendered to a `CriteriaBuilder` using the `BlazeCriteriaBuilderRenderer`.
This allows for the queries to be executed through Blaze-Persistence JPQL Next query engine.
Be sure to use the `JPQLNextTemplates` or any `Templates` implementation that includes the extensions from `JPQLNextTemplates` when using JPQL Next specific features (e.g. window functions, values clauses, set operations, common table expressions).

This module aims an API that is as close to the original QueryDSL API as possible.
Where features did not exist in `querydsl-jpa`, but did exist in `querydsl-sql`, we stayed as close to the existing SQL implementation as close as possible.
This includes the implementation for window functions, common table expressions (CTE's) and union queries which was the basis for all types of set expressions.

Staying close to QueryDSL's API however, also means that the API is not as fluent as Blaze-Persistence users are accustomed to.
This means that creating common table expressions or complex set operations may lead to superfluous code.
For an API closer to the existing `CriteriaBuilder` API from Blaze-Persistence, look at our `blaze-persistence-querydsl-api` module.

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
