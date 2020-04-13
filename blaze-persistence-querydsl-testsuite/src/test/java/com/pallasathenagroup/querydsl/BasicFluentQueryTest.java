package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.Queryable;
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
import com.pallasathenagroup.querydsl.api.CriteriaBuilder;
import com.pallasathenagroup.querydsl.api.FinalSetOperationCriteriaBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Param;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLTemplates;
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
import static com.pallasathenagroup.querydsl.JPQLNextExpressions.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class BasicFluentQueryTest extends AbstractCoreTest {

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
            CriteriaBuilder<Object> query = CriteriaBuilder.getCriteriaBuilder(entityManager, cbf)
                    .from(testEntity)
                    .select(testEntity.field.as("blep"))
                    .select(testEntity.field.substring(2))
                    .where(testEntity.field.length().gt(1));

            BlazeCriteriaBuilderRenderer<Tuple> blazeCriteriaBuilderRenderer = new BlazeCriteriaBuilderRenderer<>(cbf, entityManager, JPQLTemplates.DEFAULT);
            Queryable<Tuple, ?> queryable = blazeCriteriaBuilderRenderer.render(query);
            List<Tuple> result = queryable.getResultList();
            assertFalse(result.isEmpty());
        });
    }

    @Test
    public void testThroughBlazeJPAQuery() {
        doInJPA(entityManager -> {
            QTestEntity testEntity = QTestEntity.testEntity;

            CriteriaBuilder<Tuple> query = CriteriaBuilder.<Tuple> getCriteriaBuilder(entityManager, cbf)
                    .from(testEntity)
                    .select(testEntity.field.as("blep"))
                    .select(testEntity.field.substring(2))
                    .where(testEntity.field.length().gt(1));

            List<Tuple> fetch = query.getResultList();
            assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testParameterExpression() {
        doInJPA(entityManager -> {
            Param<Integer> param = new Param<>(Integer.class, "theSuperName");

            CriteriaBuilder<Tuple> query = CriteriaBuilder.<Tuple> getCriteriaBuilder(entityManager, cbf)
                    .from(testEntity)
                    .select(testEntity.field.as("blep"))
                    .select(testEntity.field.substring(2))
                    .where(testEntity.field.length().gt(param))
                    .set(param, 1);

            List<Tuple> fetch = query.getResultList();
            assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testParameterExpressionInSelect() {
        doInJPA(entityManager -> {
            Param<Integer> param = new Param<>(Integer.class, "theSuperName");

            CriteriaBuilder<Tuple> query = CriteriaBuilder.<Tuple> getCriteriaBuilder(entityManager, cbf)
                    .from(testEntity)
                    .select(testEntity.field.as("blep"))
                    .select(param)
                    .where(testEntity.field.length().gt(param))
                    .set(param, 1);

            List<Tuple> fetch = query.getResultList();
            assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testSubQuery() {
        doInJPA(entityManager -> {
            QTestEntity sub = new QTestEntity("sub");
            CriteriaBuilder<Tuple> query = CriteriaBuilder.<Tuple> getCriteriaBuilder(entityManager, cbf)
                    .from(testEntity)
                    .select(testEntity.field.as("blep"))
                    .select(testEntity.field.substring(2))
                    .where(testEntity.id.in(select(sub.id).from(sub)));

            List<Tuple> fetch = query.getResultList();
            assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testWindowFunction() {
        doInJPA(entityManager -> {
            QTestEntity sub = new QTestEntity("sub");
            CriteriaBuilder<Tuple> query = CriteriaBuilder.<Tuple> getCriteriaBuilder(entityManager, cbf)
                    .from(testEntity)
                    .select(testEntity.field.as("blep"))
                    .select(rowNumber())
                    .select(lastValue(testEntity.field).over().partitionBy(testEntity.id))
                    .where(testEntity.id.in(select(sub.id).from(sub)));

            List<Tuple> fetch = query.getResultList();
            assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testNamedWindowFunction() {
        doInJPA(entityManager -> {
            QTestEntity sub = new QTestEntity("sub");

            NamedWindow blep = new NamedWindow("whihi").partitionBy(testEntity.id);

            CriteriaBuilder<Tuple> query = CriteriaBuilder.<Tuple> getCriteriaBuilder(entityManager, cbf)
                    .from(testEntity)
                    .window(blep)
                    .select(testEntity.field.as("blep"))
                    .select(rowNumber().over(blep))
                    .select(lastValue(testEntity.field).over(blep))
                    .where(testEntity.id.in(select(sub.id).from(sub)));

            List<Tuple> fetch = query.getResultList();
            assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testNestedSubQuery() {
        doInJPA(entityManager -> {
            QTestEntity sub = new QTestEntity("sub");
            QTestEntity sub2 = new QTestEntity("sub2");
            CriteriaBuilder<Tuple> query = CriteriaBuilder.<Tuple> getCriteriaBuilder(entityManager, cbf)
                    .from(testEntity)
                    .select(testEntity.field.as("blep"))
                    .select(testEntity.field.substring(2))
                    .where(testEntity.id.in(select(sub.id).from(sub).where(sub.id.in(select(sub2.id).from(sub2).where(sub2.id.eq(sub.id)))).limit(5)));

            List<Tuple> fetch = query.getResultList();
            assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testTransformBlazeJPAQuery() {
        doInJPA(entityManager -> {
            Map<Long, String> blep = CriteriaBuilder.getCriteriaBuilder(entityManager, cbf)
                    .from(testEntity)
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
            Map<Author, List<Book>> booksByAuthor = CriteriaBuilder.getCriteriaBuilder(entityManager, cbf)
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
            Map<Author, List<Book>> booksByAuthor = CriteriaBuilder.getCriteriaBuilder(entityManager, cbf)
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

            List<Book> fetch = CriteriaBuilder.<Book> getCriteriaBuilder(entityManager, cbf)
                    .fromValues(book, Collections.singleton(theBook))
                    .select(book)
                    .getResultList();

            assertNotNull(fetch);
        });
    }

    @Test
    public void testFromValuesAttributes() {
        doInJPA(entityManager -> {
            StringPath bookName = Expressions.stringPath("bookName");

            List<String> fetch = CriteriaBuilder.<String> getCriteriaBuilder(entityManager, cbf)
                    .fromValues(book.name, bookName, Collections.singleton("book"))
                    .select(bookName)
                    .getResultList();

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
            List<Book> fetch = CriteriaBuilder.<Book> getCriteriaBuilder(entityManager, cbf)
                .select(book).from(book).where(book.id.eq(1337L))
                .union()
                .select(book).from(book).where(book.id.eq(41L))
                .intersect()
                .select(book).from(book).where(book.id.eq(42L))
                .except()
                .select(book).from(book).where(book.id.eq(43L))
                .intersect()
                .select(book).from(book).where(book.id.eq(46L))
                .endSet()
                .getResultList();

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

            FinalSetOperationCriteriaBuilder<Long> setOp = CriteriaBuilder.<Long> getCriteriaBuilder(entityManager, cbf)
                    .select(book.id).from(book).where(book.id.eq(1337L))
                    .union()
                    .select(book.id).from(book).where(book.id.eq(41L))
                    .intersect()
                    .select(book.id).from(book).where(book.id.eq(42L))
                    .except()
                    .select(book.id).from(book).where(book.id.eq(43L))
                    .intersect()
                    .select(book.id).from(book).where(book.id.eq(46L))
                    .endSet();

            QBook book2 = new QBook("secondBook");

            List<Book> fetch = CriteriaBuilder.<Book>getCriteriaBuilder(entityManager, cbf)
                    .select(book2)
                    .from(book2).where(book2.id.in(setOp))
                    .getResultList();

            assertNotNull(fetch);
        });
    }

    @Test
    public void testCTE() {
        doInJPA(entityManager -> {
            List<Long> fetch = CriteriaBuilder.<Long> getCriteriaBuilder(entityManager, cbf)
                    .with(idHolderCte)
                        .bind(idHolderCte.id).select(book.id)
                        .bind(idHolderCte.name).select(book.name)
                        .from(book)
                    .end()
                    .select(idHolderCte.id).from(idHolderCte)
                    .getResultList();

            assertNotNull(fetch);
        });
    }

    @Test
    public void testCTEUnion() {
        doInJPA(entityManager -> {
            List<Long> fetch = CriteriaBuilder.<Long> getCriteriaBuilder(entityManager, cbf)
                    .with(idHolderCte)
                        .bind(idHolderCte.id).select(book.id)
                        .bind(idHolderCte.name).select(book.name)
                        .from(book)
                    .union()
                        .bind(idHolderCte.id).select(book.id)
                        .bind(idHolderCte.name).select(book.name)
                        .from(book)
                    .intersect()
                        .bind(idHolderCte.id).select(book.id)
                        .bind(idHolderCte.name).select(book.name)
                        .from(book)
                    .endSet()
                    .end()
                    .select(idHolderCte.id).from(idHolderCte)
                    .getResultList();

            assertNotNull(fetch);
        });
    }

    @Test
    public void testCTEFromValues() {
        doInJPA(entityManager -> {
            Book theBook = new Book();
            theBook.id = 1337L;
            theBook.name = "test";

            List<Long> fetch = CriteriaBuilder.<Long> getCriteriaBuilder(entityManager, cbf)
                    .with(idHolderCte)
                        .fromValues(book, Collections.singleton(theBook))
                        .bind(idHolderCte.id).select(book.id)
                        .bind(idHolderCte.name).select(book.name)
                    .end()
                    .select(idHolderCte.id).from(idHolderCte)
                    .getResultList();

            assertNotNull(fetch);
        });
    }

    @Test
    public void testRecursiveCTEUnion() {
        doInJPA(entityManager -> {
            List<Long> fetch = CriteriaBuilder.<Long> getCriteriaBuilder(entityManager, cbf)
                    .withRecursive(idHolderCte)
                        .bind(idHolderCte.id).select(book.id)
                        .bind(idHolderCte.name).select(book.name)
                        .where(book.id.eq(1L))
                        .from(book)
                    .union()
                        .bind(idHolderCte.id).select(book.id)
                        .bind(idHolderCte.name).select(book.name)
                        .from(book)
                        .join(idHolderCte).on(idHolderCte.id.add(1L).eq(book.id))
                    .endSet()
                    .end()
                    .select(idHolderCte.id).from(idHolderCte)
                    .getResultList();

            assertNotNull(fetch);
        });
    }


    @Test
    public void testInlineEntityWithLimit() {
        doInJPA(entityManager -> {
            QRecursiveEntity recursiveEntity = new QRecursiveEntity("t");

            List<RecursiveEntity> fetch = CriteriaBuilder.<RecursiveEntity> getCriteriaBuilder(entityManager, cbf)
                .select(recursiveEntity)
                .from(select(recursiveEntity)
                        .from(recursiveEntity)
                        .where(recursiveEntity.parent.name.eq("root1"))
                        .orderBy(recursiveEntity.name.asc())
                        .limit(1L), recursiveEntity)
                .getResultList();

                assertNotNull(fetch);
        });
    }

    @Test
    public void testMultipleInlineEntityWithLimitJoin() {
        doInJPA(entityManager -> {
            QRecursiveEntity t = new QRecursiveEntity("t");
            QRecursiveEntity subT = new QRecursiveEntity("subT");
            QRecursiveEntity subT2 = new QRecursiveEntity("subT2");

            List<RecursiveEntity> fetch = CriteriaBuilder.<RecursiveEntity> getCriteriaBuilder(entityManager, cbf)
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
                    .getResultList();

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

            CriteriaBuilder.<RecursiveEntity> getCriteriaBuilder(entityManager, cbf)
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
                    .getResultList();

        });
    }

    @Test
    public void testJoinInlineEntityWithLimit() {
        doInJPA(entityManager -> {
            QRecursiveEntity t = new QRecursiveEntity("t");
            QRecursiveEntity subT = new QRecursiveEntity("subT");
            QRecursiveEntity subT2 = new QRecursiveEntity("subT2");

            List<Tuple> fetch = CriteriaBuilder.<Tuple> getCriteriaBuilder(entityManager, cbf)
                .select(t).select(subT2)
                .from(t)
                .leftJoin(select(subT).from(t.children, subT).orderBy(subT.id.asc()).limit(1), subT2)
                .lateral()
                .getResultList();

            assertNotNull(fetch);
        });
    }


    @Test
    public void testJoinInlineEntityWithLimitWithCTE() {
        doInJPA(entityManager -> {
            QRecursiveEntity t = new QRecursiveEntity("t");
            QRecursiveEntity subT = new QRecursiveEntity("subT");
            QRecursiveEntity subT2 = new QRecursiveEntity("subT2");

            List<Tuple> fetch = CriteriaBuilder.<Tuple> getCriteriaBuilder(entityManager, cbf)
                    .with(idHolderCte)
                        .bind(idHolderCte.id).select(book.id)
                        .bind(idHolderCte.name).select(book.name)
                        .from(book).where(book.id.eq(1L))
                    .end()
                    .select(t).select(subT2)
                    .from(t)
                    .leftJoin(select(subT).from(t.children, subT).orderBy(subT.id.asc()).limit(1), subT2)
                    .where(t.id.in(select(idHolderCte.id).from(idHolderCte)))
                    .lateral()
                    .getResultList();

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

            CriteriaBuilder.<Tuple> getCriteriaBuilder(entityManager, cbf)
                    .select(t).select(subT2)
                    .from(t)
                    .leftJoin(union, subT2).on(subT2.eq(subT2))
                    .getResultList();
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

            CriteriaBuilder.<Tuple> getCriteriaBuilder(entityManager, cbf)
                    .select(t).select(subT2)
                    .from(t)
                    .leftJoin(union, subT2).on(subT2.eq(subT2))
                    .getResultList();
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

            CriteriaBuilder.<Tuple> getCriteriaBuilder(entityManager, cbf)
                    .select(t).select(subT2)
                    .from(t)
                    .leftJoin(union, subT2).on(subT2.eq(subT2))
                    .lateral()
                    .getResultList();
        });
    }

}
