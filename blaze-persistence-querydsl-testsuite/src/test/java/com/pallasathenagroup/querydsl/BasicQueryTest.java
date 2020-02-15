package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.testsuite.AbstractCoreTest;
import com.blazebit.persistence.testsuite.entity.ParameterOrderCte;
import com.blazebit.persistence.testsuite.entity.ParameterOrderCteB;
import com.blazebit.persistence.testsuite.entity.ParameterOrderEntity;
import com.blazebit.persistence.testsuite.entity.QRecursiveEntity;
import com.blazebit.persistence.testsuite.entity.RecursiveEntity;
import com.blazebit.persistence.testsuite.entity.TestAdvancedCTE1;
import com.blazebit.persistence.testsuite.entity.TestAdvancedCTE2;
import com.blazebit.persistence.testsuite.entity.TestCTE;
import com.blazebit.persistence.testsuite.tx.TxVoidWork;
import com.pallasathenagroup.querydsl.CTEUtils.Binds;
import com.pallasathenagroup.querydsl.impl.BlazeCriteriaVisitor;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Param;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.blazebit.persistence.testsuite.entity.QRecursiveEntity.recursiveEntity;
import static com.pallasathenagroup.querydsl.QAuthor.author;
import static com.pallasathenagroup.querydsl.QBook.book;
import static com.pallasathenagroup.querydsl.QIdHolderCte.idHolderCte;
import static com.pallasathenagroup.querydsl.QTestEntity.testEntity;
import static com.pallasathenagroup.querydsl.SetUtils.intersect;
import static com.pallasathenagroup.querydsl.SetUtils.union;
import static com.pallasathenagroup.querydsl.WindowExpressions.lastValue;
import static com.pallasathenagroup.querydsl.WindowExpressions.rowNumber;
import static com.querydsl.jpa.JPAExpressions.select;
import static com.querydsl.jpa.JPAExpressions.selectFrom;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class BasicQueryTest extends AbstractCoreTest {

    @Rule public ExpectedException expectedException = ExpectedException.none();

    @Override
    protected Class<?>[] getEntityClasses() {
        return new Class<?>[] {
                TestEntity.class, Author.class, Book.class, Publication.class, Publisher.class, IdHolderCte.class,
                RecursiveEntity.class,
                TestCTE.class,
                TestAdvancedCTE1.class,
                TestAdvancedCTE2.class,
                ParameterOrderCte.class,
                ParameterOrderCteB.class,
                ParameterOrderEntity.class
        };
    }

    public void doInJPA(Consumer<EntityManager> function) {
        transactional(new TxVoidWork() {
            @Override
            public void work(EntityManager entityManager) {
                function.accept(entityManager);
            }
        });
    }
    
    @Before
    public void setUp() {
        doInJPA(entityManager -> {
            TestEntity testEntity = new TestEntity();
            testEntity.field = "bogus";
            entityManager.persist(testEntity);
        });
    }

    @Test
    public void testThroughBPVisitor() {
        doInJPA(entityManager -> {
            JPAQuery<Tuple> query = new JPAQuery<TestEntity>(entityManager).from(testEntity)
                    .select(testEntity.field.as("blep"), testEntity.field.substring(2))
                    .where(testEntity.field.length().gt(1));

            BlazeCriteriaVisitor<Tuple> blazeCriteriaVisitor = new BlazeCriteriaVisitor<>(cbf, entityManager, JPQLTemplates.DEFAULT);
            blazeCriteriaVisitor.serialize(query.getMetadata(), false, null);
            CriteriaBuilder<Tuple> criteriaBuilder = blazeCriteriaVisitor.getCriteriaBuilder();
            List<Tuple> fetch = criteriaBuilder.getResultList();
            assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testThroughBlazeJPAQuery() {
        doInJPA(entityManager -> {
            BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<TestEntity>(entityManager, cbf).from(testEntity)
                    .select(testEntity.field.as("blep"), testEntity.field.substring(2))
                    .where(testEntity.field.length().gt(1));

            List<Tuple> fetch = query.fetch();
            assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testParameterExpression() {
        doInJPA(entityManager -> {
            Param<Integer> param = new Param<>(Integer.class, "theSuperName");

            BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<TestEntity>(entityManager, cbf).from(testEntity)
                    .select(testEntity.field.as("blep"), testEntity.field.substring(2))
                    .where(testEntity.field.length().gt(param))
                    .set(param, 1);

            List<Tuple> fetch = query.fetch();
            assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testParameterExpressionInSelect() {
        doInJPA(entityManager -> {
            Param<Integer> param = new Param<>(Integer.class, "theSuperName");

            BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<TestEntity>(entityManager, cbf).from(testEntity)
                    .select(testEntity.field.as("blep"), param)
                    .where(testEntity.field.length().gt(param))
                    .set(param, 1);

            List<Tuple> fetch = query.fetch();
            assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testSubQuery() {
        doInJPA(entityManager -> {
            QTestEntity sub = new QTestEntity("sub");
            BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<TestEntity>(entityManager, cbf).from(testEntity)
                    .select(testEntity.field.as("blep"), testEntity.field.substring(2))
                    .where(testEntity.id.in(select(sub.id).from(sub)));

            List<Tuple> fetch = query.fetch();
            assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testWindowFunction() {
        doInJPA(entityManager -> {
            QTestEntity sub = new QTestEntity("sub");
            BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<TestEntity>(entityManager, cbf).from(testEntity)
                    .select(testEntity.field.as("blep"), rowNumber(), lastValue(testEntity.field).over().partitionBy(testEntity.id))
                    .where(testEntity.id.in(select(sub.id).from(sub)));

            List<Tuple> fetch = query.fetch();
            assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testNamedWindowFunction() {
        doInJPA(entityManager -> {
            QTestEntity sub = new QTestEntity("sub");

            NamedWindow blep = new NamedWindow("whihi").partitionBy(testEntity.id);

            BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<TestEntity>(entityManager, cbf).from(testEntity)
                    .window(blep)
                    .select(testEntity.field.as("blep"), rowNumber().over(blep), lastValue(testEntity.field).over(blep))
                    .where(testEntity.id.in(select(sub.id).from(sub)));

            List<Tuple> fetch = query.fetch();
            assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testNestedSubQuery() {
        doInJPA(entityManager -> {
            QTestEntity sub = new QTestEntity("sub");
            QTestEntity sub2 = new QTestEntity("sub2");
            BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<TestEntity>(entityManager, cbf).from(testEntity)
                    .select(testEntity.field.as("blep"), testEntity.field.substring(2))
                    .where(testEntity.id.in(select(sub.id).from(sub).where(sub.id.in(select(sub2.id).from(sub2).where(sub2.id.eq(sub.id)))).limit(5)));

            List<Tuple> fetch = query.fetch();
            assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testTransformBlazeJPAQuery() {
        doInJPA(entityManager -> {
            Map<Long, String> blep = new BlazeJPAQuery<TestEntity>(entityManager, cbf).from(testEntity)
                    .where(testEntity.field.length().gt(1))
                    .groupBy(testEntity.id, testEntity.field)
                    .transform(GroupBy.groupBy(testEntity.id).as(testEntity.field));

            testEntity.getRoot();
            assertFalse(blep.isEmpty());
        });
    }

    @Test
    public void testAssociationJoin() {
        doInJPA(entityManager -> {
            Map<Author, List<Book>> booksByAuthor = new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                    .from(author)
                    .innerJoin(author.books, book)
                    .transform(GroupBy.groupBy(author).as(GroupBy.list(book)));

            assertNotNull(booksByAuthor);
        });
    }

    @Test
    public void testEntityJoin() {
        doInJPA(entityManager -> {
            QAuthor otherAuthor = new QAuthor("otherAuthor");
            QBook otherBook = new QBook("otherBook");
            Map<Author, List<Book>> booksByAuthor = new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                    .from(otherAuthor)
                    .innerJoin(otherBook).on(otherBook.author.eq(otherAuthor))
                    .transform(GroupBy.groupBy(otherAuthor).as(GroupBy.list(otherBook)));

            assertNotNull(booksByAuthor);
        });
    }


    @Test
    public void testFromValues() {
        doInJPA(entityManager -> {
            Book theBook = new Book();
            theBook.id = 1337L;
            theBook.name = "test";

            List<Book> fetch = new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                    .fromValues(book, Collections.singleton(theBook))
                    .select(book)
                    .fetch();

            assertNotNull(fetch);
        });
    }

    @Test
    public void testFromValuesAttributes() {
        doInJPA(entityManager -> {
            StringPath bookName = Expressions.stringPath("bookName");

            List<String> fetch = new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                    .fromValues(book.name, bookName, Collections.singleton("book"))
                    .select(bookName)
                    .fetch();

            assertNotNull(fetch);
        });
    }

    @Test
    public void testComplexUnion() {
        doInJPA(entityManager -> {

            Book theBook = new Book();
            theBook.id = 1337L;
            theBook.name = "test";
            entityManager.merge(theBook);

            Book theSequel = new Book();
            theSequel.id = 42L;
            theSequel.name = "test2";
            entityManager.merge(theSequel);
        });

        doInJPA(entityManager -> {
            List<Book> fetch = new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                    .union(select(book).from(book).where(book.id.eq(1337L)),
                        new BlazeJPAQuery<TestEntity>().intersect(
                                select(book).from(book).where(book.id.eq(41L)),
                                new BlazeJPAQuery<TestEntity>().except(
                                        select(book).from(book).where(book.id.eq(42L)),
                                        select(book).from(book).where(book.id.eq(43L)))),
                                select(book).from(book).where(book.id.eq(46L))
                            )
                    .fetch();

            assertNotNull(fetch);
        });
    }

    @Test
    public void testComplexSubqueryUnion() {
        doInJPA(entityManager -> {

            Book theBook = new Book();
            theBook.id = 1337L;
            theBook.name = "test";
            entityManager.merge(theBook);

            Book theSequel = new Book();
            theSequel.id = 42L;
            theSequel.name = "test2";
            entityManager.merge(theSequel);
        });

        doInJPA(entityManager -> {

            cbf.create(entityManager, Book.class)
                    .from(Book.class)
                    .where("book.name").in().from(Book.class, "b")
                        .select("b.name")
                        .union()
                        .endSet()
                        .end();

            SetExpression<Long> union = new BlazeJPAQuery<Long>(entityManager, cbf)
                    .union(select(book.id).from(book).where(book.id.eq(1337L)),
                            new BlazeJPAQuery<Long>().intersect(
                                    select(book.id).from(book).where(book.id.eq(41L)),
                                    new BlazeJPAQuery<Long>().except(
                                            select(book.id).from(book).where(book.id.eq(42L)),
                                            select(book.id).from(book).where(book.id.eq(43L)))),
                            select(book.id).from(book).where(book.id.eq(46L))
                    );

            QBook book2 = new QBook("secondBook");

            List<Book> fetch = new BlazeJPAQuery<Book>(entityManager, cbf)
                    .select(book2)
                    .from(book2).where(book2.id.in(union))
                    .fetch();

            assertNotNull(fetch);
        });
    }

    @Test
    public void testCTE() {
        doInJPA(entityManager -> {
            List<Long> fetch = new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                    .with(idHolderCte, idHolderCte.id, idHolderCte.name).as(select(book.id, book.name).from(book))
                    .select(idHolderCte.id).from(idHolderCte)
                    .fetch();

            assertNotNull(fetch);
        });
    }

    @Test
    public void testCTEWithBinds() {
        doInJPA(entityManager -> {
            List<Long> fetch = new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                    .with(idHolderCte, select(new CTEUtils.Binds<IdHolderCte>().bind(idHolderCte.id, book.id).bind(idHolderCte.name, book.name)).from(book))
                    .select(idHolderCte.id).from(idHolderCte)
                    .fetch();

            assertNotNull(fetch);
        });
    }

    @Test
    public void testCTEUnion() {
        doInJPA(entityManager -> {
            List<Long> fetch = new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                    .with(idHolderCte, idHolderCte.id, idHolderCte.name).as(union(select(book.id, book.name).from(book), intersect(select(book.id, book.name).from(book), select(book.id, book.name).from(book))))
                    .select(idHolderCte.id).from(idHolderCte)
                    .fetch();

            assertNotNull(fetch);
        });
    }



    @Test
    public void testCTEFromValues() {
        doInJPA(entityManager -> {
            Book theBook = new Book();
            theBook.id = 1337L;
            theBook.name = "test";

            List<Long> fetch = new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                    .with(idHolderCte, idHolderCte.id, idHolderCte.name).as(new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                            .fromValues(book, Collections.singleton(theBook))
                            .select(book.id, book.name))
                    .select(idHolderCte.id).from(idHolderCte)
                    .fetch();

            assertNotNull(fetch);
        });
    }

    @Test
    public void testRecursiveCTEUnion() {
        doInJPA(entityManager -> {
            List<Long> fetch = new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                    .withRecursive(idHolderCte, idHolderCte.id, idHolderCte.name).as(union(select(book.id, book.name).from(book).where(book.id.eq(1L)), select(book.id, book.name).from(book)
                            .join(idHolderCte).on(idHolderCte.id.add(1L).eq(book.id))))
                    .select(idHolderCte.id).from(idHolderCte)
                    .fetch();

            assertNotNull(fetch);
        });
    }

    @Test
    public void testRecursiveBindBuilder() {
        doInJPA(entityManager -> {
            List<Long> fetch = new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                    .withRecursive(idHolderCte, idHolderCte.id, idHolderCte.name)
                        .as(new BlazeJPAQuery<>().unionAll(
                                select(book.id, book.name).from(book).where(book.id.eq(1L)),
                                select(book.id, book.name).from(book).join(idHolderCte).on(idHolderCte.id.add(1L).eq(book.id))))
                    .select(idHolderCte.id).from(idHolderCte)
                    .fetch();

            assertNotNull(fetch);
        });
    }

    @Test
    public void testInlineEntityWithLimit() {
        doInJPA(entityManager -> {
            QRecursiveEntity recursiveEntity = new QRecursiveEntity("t");

            List<RecursiveEntity> fetch = new BlazeJPAQuery<RecursiveEntity>(entityManager, cbf)
                    .select(recursiveEntity)
                    .from(select(recursiveEntity)
                            .from(recursiveEntity)
                            .where(recursiveEntity.parent.name.eq("root1"))
                            .orderBy(recursiveEntity.name.asc())
                            .limit(1L), recursiveEntity)
                    .fetch();

            assertNotNull(fetch);

        });
    }

    @Test
    public void testMultipleInlineEntityWithLimitJoin() {
        doInJPA(entityManager -> {
            QRecursiveEntity t = new QRecursiveEntity("t");
            QRecursiveEntity subT = new QRecursiveEntity("subT");
            QRecursiveEntity subT2 = new QRecursiveEntity("subT2");

            List<RecursiveEntity> fetch = new BlazeJPAQuery<RecursiveEntity>(entityManager, cbf)
                    .select(t)
                    .from(new BlazeJPAQuery<RecursiveEntity>().select(t).from(t)
                            .leftJoin(selectFrom(subT).where(subT.parent.name.eq("root1")).orderBy(subT.name.asc()).limit(1), subT)
                            .on(t.eq(subT))
                            .where(t.parent.name.eq("root1"))
                            .orderBy(t.name.asc())
                            .limit(1L), t)
                    .leftJoin(selectFrom(subT2)
                            .where(subT2.parent.name.eq("root1"))
                            .orderBy(subT2.name.asc())
                            .limit(1), subT2)
                    .on(t.eq(subT2))
                    .fetch();

            assertNotNull(fetch);

        });
    }

    @Test
    public void testMultipleInlineEntityWithLimitJoinLateral() {

        doInJPA(entityManager -> {
            QRecursiveEntity t = new QRecursiveEntity("t");
            QRecursiveEntity subT = new QRecursiveEntity("subT");
            QRecursiveEntity subT2 = new QRecursiveEntity("subT2");
            QRecursiveEntity subT3 = new QRecursiveEntity("subT3");

            new BlazeJPAQuery<RecursiveEntity>(entityManager, cbf)
                    .select(t)
                    .from(new BlazeJPAQuery<RecursiveEntity>().select(t).from(t)
                            .leftJoin(selectFrom(subT).where(subT.parent.name.eq("root1")).orderBy(subT.name.asc()).limit(1), subT)
                            .on(t.eq(subT))
                            .where(t.parent.name.eq("root1"))
                            .orderBy(t.name.asc())
                            .limit(1L), t)
                    .leftJoin(selectFrom(subT2)
                        .where(subT2.parent.name.eq("root1"))
                        .orderBy(subT2.name.asc())
                        .limit(1), subT3)
                        .on(t.eq(subT3))
                        .lateral()
                    .fetch();

        });
    }

    @Test
    public void testJoinInlineEntityWithLimit() {
        doInJPA(entityManager -> {
            QRecursiveEntity t = new QRecursiveEntity("t");
            QRecursiveEntity subT = new QRecursiveEntity("subT");
            QRecursiveEntity subT2 = new QRecursiveEntity("subT2");

            List<Tuple> fetch = new BlazeJPAQuery<RecursiveEntity>(entityManager, cbf)
                    .select(t, subT2)
                    .from(t)
                    .leftJoin(select(subT).from(t.children, subT).orderBy(subT.id.asc()).limit(1), subT2)
                    .lateral()
                    .fetch();

            assertNotNull(fetch);
        });
    }


    @Test
    public void testJoinInlineEntityWithLimitWithCTE() {
        doInJPA(entityManager -> {
            QRecursiveEntity t = new QRecursiveEntity("t");
            QRecursiveEntity subT = new QRecursiveEntity("subT");
            QRecursiveEntity subT2 = new QRecursiveEntity("subT2");

            List<Tuple> fetch = new BlazeJPAQuery<RecursiveEntity>(entityManager, cbf)
                    .with(idHolderCte, idHolderCte.id, idHolderCte.name)
                    .as(select(book.id, book.name).from(book).where(book.id.eq(1L)))
                    .select(t, subT2)
                    .from(t)
                    .leftJoin(select(subT).from(t.children, subT).orderBy(subT.id.asc()).limit(1), subT2)
                    .where(t.id.in(select(idHolderCte.id).from(idHolderCte)))
                    .lateral()
                    .fetch();

            assertNotNull(fetch);
        });
    }

    @Test
    public void testJoinInlineWithLimitUnion() {
        doInJPA(entityManager -> {
            QRecursiveEntity t = new QRecursiveEntity("t");
            QRecursiveEntity subT = new QRecursiveEntity("subT");
            QRecursiveEntity subT2 = new QRecursiveEntity("subT2");
            QRecursiveEntity subT3 = new QRecursiveEntity("subT3");

            JPQLQuery<RecursiveEntity> subA = select(
                    new Binds<RecursiveEntity>()
                        .bind(recursiveEntity.id, subT.id)
                        .bind(recursiveEntity.name, subT.name)
                        .bind(recursiveEntity.parent, subT.parent)
            ).from(subT);

            JPQLQuery<RecursiveEntity> subB = select(
                    new Binds<RecursiveEntity>()
                        .bind(recursiveEntity.id, subT3.id)
                        .bind(recursiveEntity.name, subT3.name)
                        .bind(recursiveEntity.parent, subT3.parent)).from(subT3);

            SubQueryExpression<RecursiveEntity> union = new BlazeJPAQuery<RecursiveEntity>().unionAll(
                    subA,
                    subB
            );

            new BlazeJPAQuery<RecursiveEntity>(entityManager, cbf)
                    .select(t, subT2)
                    .from(t)
                    .leftJoin(union, subT2).on(subT2.eq(subT2))
                    .fetch();
        });
    }

    @Test
    public void testJoinInlineEntityWithLimitUnion() {
        doInJPA(entityManager -> {
            QRecursiveEntity t = new QRecursiveEntity("t");
            QRecursiveEntity subT = new QRecursiveEntity("subT");
            QRecursiveEntity subT2 = new QRecursiveEntity("subT2");
            QRecursiveEntity subT3 = new QRecursiveEntity("subT3");

            JPQLQuery<RecursiveEntity> subA = selectFrom(subT).where(subT.id.lt(5L));
            JPQLQuery<RecursiveEntity> subB = selectFrom(subT3).where(subT3.id.gt(10L));

            SubQueryExpression<RecursiveEntity> union = new BlazeJPAQuery<RecursiveEntity>().unionAll(
                    subA,
                    subB
            );

            new BlazeJPAQuery<RecursiveEntity>(entityManager, cbf)
                    .select(t, subT2)
                    .from(t)
                    .leftJoin(union, subT2).on(subT2.eq(subT2))
                    .fetch();
        });
    }

    @Test
    public void testJoinInlineLateralEntityWithLimitUnion() {
        doInJPA(entityManager -> {
            QRecursiveEntity t = new QRecursiveEntity("t");
            QRecursiveEntity subT = new QRecursiveEntity("subT");
            QRecursiveEntity subT2 = new QRecursiveEntity("subT2");
            QRecursiveEntity subT3 = new QRecursiveEntity("subT3");

            JPQLQuery<RecursiveEntity> subA = selectFrom(subT).where(subT.id.lt(5L));
            JPQLQuery<RecursiveEntity> subB = selectFrom(subT3).where(subT3.id.gt(10L));

            SubQueryExpression<RecursiveEntity> union = new BlazeJPAQuery<RecursiveEntity>().unionAll(
                    subA,
                    subB
            );

            expectedException.expect(IllegalStateException.class);
            expectedException.expectMessage("Lateral join with set operations is not yet supported!");

            new BlazeJPAQuery<RecursiveEntity>(entityManager, cbf)
                    .select(t, subT2)
                    .from(t)
                    .leftJoin(union, subT2).on(subT2.eq(subT2))
                    .lateral()
                    .fetch();
        });
    }

}
