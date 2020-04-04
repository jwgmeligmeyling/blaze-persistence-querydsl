/**
 * The {@code blaze-persistence-querydsl-expressions} module implements an extended expression model for
 * Blaze-Persistence JPQL Next.
 *
 * <p>
 * The module provides a {@link com.pallasathenagroup.querydsl.BlazeJPAQuery} as a default implementation of {@link
 * com.pallasathenagroup.querydsl.ExtendedJPAQuery}, which extends the all familiar {@link com.querydsl.jpa.JPQLQuery}.
 * {@link com.pallasathenagroup.querydsl.BlazeJPAQuery} is analog to {@link com.querydsl.jpa.impl.JPAQuery}. Users can
 * implement extensions on top of {@link com.pallasathenagroup.querydsl.ExtendedJPAQuery} by extending {@link
 * com.pallasathenagroup.querydsl.AbstractBlazeJPAQuery}.
 * </p>
 *
 * <p>
 * {@link com.pallasathenagroup.querydsl.BlazeJPAQuery} can be serialized using the {@link
 * com.pallasathenagroup.querydsl.JPQLNextSerializer}, and may be rendered to a {@link
 * com.blazebit.persistence.CriteriaBuilder} using the {@link com.pallasathenagroup.querydsl.BlazeCriteriaBuilderRenderer}.
 * This allows for the queries to be executed through Blaze-Persistence JPQL Next query engine. Be sure to use the
 * {@link com.pallasathenagroup.querydsl.JPQLNextTemplates} or any {@code Templates} implementation that includes the
 * extensions from {@code JPQLNextTemplates} when using JPQL Next specific features (e.g. window functions, values
 * clauses, set operations, common table expressions).
 * </p>
 *
 * <p>
 * This module aims an API that is as close to the original QueryDSL API as possible. Where features did not exist in
 * {@code querydsl-jpa}, but did exist in {@code querydsl-sql}, we stayed as close to the existing SQL implementation as
 * close as possible. This includes the implementation for window functions, common table expressions (CTE's) and union
 * queries which was the basis for all types of set expressions.
 * </p>
 *
 * <p>
 * Staying close to QueryDSL's API however, also means that the API is not as fluent as Blaze-Persistence users are
 * accustomed to. This means that creating common table expressions or complex set operations may lead to superfluous
 * code. For an API closer to the existing {@code CriteriaBuilder} API from Blaze-Persistence, look at our {@code
 * blaze-persistence-querydsl-api} module.
 * </p>
 *
 * <h2>Examples</h2>
 * The following chapters demonstrate some of the possibilities of the {@code blaze-persistence-querydsl-expressions}
 * integration.
 *
 * <h3>Plain query</h3>
 * <pre>
 * QTestEntity testEntity = QTestEntity.testEntity;
 *
 * BlazeJPAQuery&lt;Tuple&gt; query = new BlazeJPAQuery&lt;Tuple&gt;(entityManager, cbf).from(testEntity)
 *         .select(testEntity.field.as("blep"), testEntity.field.substring(2))
 *         .where(testEntity.field.length().gt(1));
 *
 * List&lt;Tuple&gt; fetch = query.fetch();
 * </pre>
 *
 * <h3>Implicit joins</h3>
 * <p>Contrary to JPQL, JPQL Next allows for implicit joins.
 * For deep path expressions it is not necessary to specify the joins manually.
 * </p>
 * <pre>
 * List&lt;Book&gt; dilbert = new BlazeJPAQuery&lt;&gt;(entityManager, cbf).from(book)
 *                     .where(book.author.name.eq("Dilbert"))
 *                     .select(book).fetch();
 * </pre>
 *
 * <h3>Window functions</h3>
 * <pre>
 * QTestEntity sub = new QTestEntity("sub");
 * BlazeJPAQuery&lt;Tuple&gt; query = new BlazeJPAQuery&lt;Tuple&gt;(entityManager, cbf).from(testEntity)
 *         .select(testEntity.field.as("blep"), WindowExpressions.rowNumber(), WindowExpressions.lastValue(testEntity.field).over().partitionBy(testEntity.id))
 *         .where(testEntity.id.in(select(sub.id).from(sub)));
 *
 * List&lt;Tuple&gt; fetch = query.fetch();
 * </pre>
 * 
 * <h3>Named window functions</h3>
 * <pre>
 * QTestEntity sub = new QTestEntity("sub");
 * NamedWindow blep = new NamedWindow("whihi").partitionBy(testEntity.id);
 *
 * BlazeJPAQuery&lt;Tuple&gt; query = new BlazeJPAQuery&lt;Tuple&gt;(entityManager, cbf).from(testEntity)
 *         .window(blep)
 *         .select(testEntity.field.as("blep"), WindowExpressions.rowNumber().over(blep), WindowExpressions.lastValue(testEntity.field).over(blep))
 *         .where(testEntity.id.in(select(sub.id).from(sub)));
 *
 * List&lt;Tuple&gt; fetch = query.fetch();
 * </pre>
 * 
 * <h3>Regular association joins</h3>
 * <pre>
 * Map&lt;Author, List&lt;Book&gt;&gt; booksByAuthor = new BlazeJPAQuery&lt;&gt;(entityManager, cbf)
 *         .from(author)
 *         .innerJoin(author.books, book)
 *         .transform(GroupBy.groupBy(author).as(GroupBy.list(book)));
 * </pre>
 * 
 * <h3>Regular entity joins</h3>
 * <pre>
 * QAuthor otherAuthor = new QAuthor("otherAuthor");
 * QBook otherBook = new QBook("otherBook");
 * Map&lt;Author, List&lt;Book&gt;&gt; booksByAuthor = new BlazeJPAQuery&lt;Tuple&gt;(entityManager, cbf)
 *         .from(otherAuthor)
 *         .innerJoin(otherBook).on(otherBook.author.eq(otherAuthor))
 *         .transform(GroupBy.groupBy(otherAuthor).as(GroupBy.list(otherBook)));
 * </pre>
 * 
 * <h3>Managed type values clause</h3>
 * <pre>
 * Book theBook = new Book();
 * theBook.id = 1337L;
 * theBook.name = "test";
 *
 * List&lt;Book&gt; fetch = new BlazeJPAQuery&lt;Book&gt;(entityManager, cbf)
 *         .fromValues(book, Collections.singleton(theBook))
 *         .select(book)
 *         .fetch();
 * </pre>
 * 
 * <h3>Managed attribute values clause</h3>
 * <pre>
 * StringPath bookName = Expressions.stringPath("bookName");
 *
 * List&lt;String&gt; fetch = new BlazeJPAQuery&lt;&gt;(entityManager, cbf)
 *         .fromValues(book.name, bookName, Collections.singleton("book"))
 *         .select(bookName)
 *         .fetch();   
 * </pre>
 * 
 * <h3>Common Table Expressions</h3>
 * <p>First declare your CTE entity:</p>
 * <pre>
 * &#64;CTE
 * &#64;Entity
 * public class IdHolderCte {
 *
 *     &#64;Id
 *     Long id;
 *
 *     String name;
 *
 * }
 * </pre>
 * 
 * <p>Next, it can be queried as such:</p>
 * <pre>
 * List&lt;Long&gt; fetch = new BlazeJPAQuery&lt;TestEntity&gt;(entityManager, cbf)
 *     .with(idHolderCte, select(
 *             CTEUtils.bind(idHolderCte.id, book.id),
 *             CTEUtils.bind(idHolderCte.name, book.name)).from(book))
 *     .select(idHolderCte.id).from(idHolderCte)
 *     .fetch();
 * </pre>
 * 
 * <p>Note: Set operations are also allowed in CTE's, and through set operations
 * it is also possible to write recursive CTE's.</p>
 *
 * <h3>Subquery joins</h3>
 * <p>A limitation of JPQL frequently stumbled opon, is that subqueries cannot be joined.
 * With Blaze-Persistence however, this is perfectly possible:</p>
 * 
 * <pre>
 * QRecursiveEntity recursiveEntity = new QRecursiveEntity("t");
 *
 * List&lt;RecursiveEntity&gt; fetch = new BlazeJPAQuery&lt;&gt;(entityManager, cbf)
 *         .select(recursiveEntity)
 *         .from(JPAExpressions.select(recursiveEntity)
 *                 .from(recursiveEntity)
 *                 .where(recursiveEntity.parent.name.eq("root1"))
 *                 .orderBy(recursiveEntity.name.asc())
 *                 .limit(1L), recursiveEntity)
 *         .fetch();
 * </pre>
 * 
 * <p>The subquery may project any managed entity, including CTE's.</p>
 * 
 * <h3>Lateral joins</h3>
 * <p>Subquery joins are allowed to access outer query variables, if a lateral join
 * is used.</p>
 * 
 * <pre>
 * QRecursiveEntity t = new QRecursiveEntity("t");
 * QRecursiveEntity subT = new QRecursiveEntity("subT");
 * QRecursiveEntity subT2 = new QRecursiveEntity("subT2");
 *
 * List&lt;Tuple&gt; fetch = new BlazeJPAQuery&lt;&gt;(entityManager, cbf)
 *         .select(t, subT2)
 *         .from(t)
 *         .leftJoin(JPAExpressions.select(subT).from(t.children, subT).orderBy(subT.id.asc()).limit(1), subT2)
 *         .lateral()
 *         .fetch();
 * </pre>
 *
 * @author Jan-Willem Gmelig Meyling
 */
package com.pallasathenagroup.querydsl;