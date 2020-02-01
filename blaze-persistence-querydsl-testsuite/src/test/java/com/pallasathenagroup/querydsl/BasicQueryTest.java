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
import com.pallasathenagroup.querydsl.impl.BlazeCriteriaVisitor;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.dsl.Param;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

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

public class BasicQueryTest extends AbstractCoreTest {

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
            Assert.assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testThroughBlazeJPAQuery() {
        doInJPA(entityManager -> {
            BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<TestEntity>(entityManager, cbf).from(testEntity)
                    .select(testEntity.field.as("blep"), testEntity.field.substring(2))
                    .where(testEntity.field.length().gt(1));

            List<Tuple> fetch = query.fetch();
            Assert.assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testParameterExpression() {
        doInJPA(entityManager -> {
            Param<Integer> param = new Param<Integer>(Integer.class, "theSuperName");

            BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<TestEntity>(entityManager, cbf).from(testEntity)
                    .select(testEntity.field.as("blep"), testEntity.field.substring(2))
                    .where(testEntity.field.length().gt(param))
                    .set(param, 1);

            List<Tuple> fetch = query.fetch();
            Assert.assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testParameterExpressionInSelect() {
        doInJPA(entityManager -> {
            Param<Integer> param = new Param<Integer>(Integer.class, "theSuperName");

            BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<TestEntity>(entityManager, cbf).from(testEntity)
                    .select(testEntity.field.as("blep"), param)
                    .where(testEntity.field.length().gt(param))
                    .set(param, 1);

            List<Tuple> fetch = query.fetch();
            Assert.assertFalse(fetch.isEmpty());
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
            Assert.assertFalse(fetch.isEmpty());
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
            Assert.assertFalse(fetch.isEmpty());
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
            Assert.assertFalse(fetch.isEmpty());
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
            Assert.assertFalse(blep.isEmpty());
        });
    }

    @Test
    public void testAssociationJoin() {
        doInJPA(entityManager -> {
            Map<Author, List<Book>> booksByAuthor = new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                    .from(author)
                    .innerJoin(author.books, book)
                    .transform(GroupBy.groupBy(author).as(GroupBy.list(book)));
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
        });
    }


    @Test
    public void testFromValues() {
        doInJPA(entityManager -> {
            Book theBook = new Book();
            theBook.id = 1337l;
            theBook.name = "test";

            List<Book> fetch = new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                    .fromValues(book, Collections.singleton(theBook))
                    .select(book)
                    .fetch();

            System.out.println(fetch);
        });
    }

    @Test
    @Ignore
    public void testFluentUnion() {
        doInJPA(entityManager -> {

            Book theBook = new Book();
            theBook.id = 1337l;
            theBook.name = "test";
            entityManager.merge(theBook);

            Book theSequel = new Book();
            theSequel.id = 42l;
            theSequel.name = "test2";
            entityManager.merge(theSequel);
        });


        doInJPA(entityManager -> {


            List<Book> fetch = new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                    .from(book)
                    .select(book)
                    .where(book.id.eq(1337l))
                    .union()
                    .from(book)
                    .select(book)
                    .where(book.id.eq(42l))
                    .fetch();

            System.out.println(fetch);
        });
    }
    @Test
    public void testSimpleUnion() {
        doInJPA(entityManager -> {

            Book theBook = new Book();
            theBook.id = 1337l;
            theBook.name = "test";
            entityManager.merge(theBook);

            Book theSequel = new Book();
            theSequel.id = 42l;
            theSequel.name = "test2";
            entityManager.merge(theSequel);
        });


        doInJPA(entityManager -> {


            List<Book> fetch = new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                    .union(select(book).from(book).where(book.id.eq(1337l)), select(book).from(book).where(book.id.eq(42l)))
                    .fetch();

            System.out.println(fetch);
        });
    }

    @Test
    public void testComplexUnion() {
        doInJPA(entityManager -> {

            Book theBook = new Book();
            theBook.id = 1337l;
            theBook.name = "test";
            entityManager.merge(theBook);

            Book theSequel = new Book();
            theSequel.id = 42l;
            theSequel.name = "test2";
            entityManager.merge(theSequel);
        });

        doInJPA(entityManager -> {


            List<Book> fetch = new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                    .union(select(book).from(book).where(book.id.eq(1337l)),
                        new BlazeJPAQuery<TestEntity>().intersect(
                                select(book).from(book).where(book.id.eq(41l)),
                                new BlazeJPAQuery<TestEntity>().except(
                                        select(book).from(book).where(book.id.eq(42l)),
                                        select(book).from(book).where(book.id.eq(43l)))),
                                select(book).from(book).where(book.id.eq(46l))
                            )
                    .fetch();

            System.out.println(fetch);
        });
    }

    @Test
    public void testComplexSubqueryUnion() {
        doInJPA(entityManager -> {

            Book theBook = new Book();
            theBook.id = 1337l;
            theBook.name = "test";
            entityManager.merge(theBook);

            Book theSequel = new Book();
            theSequel.id = 42l;
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

            SetOperation<Long> union = new BlazeJPAQuery<Long>(entityManager, cbf)
                    .union(select(book.id).from(book).where(book.id.eq(1337l)),
                            new BlazeJPAQuery<Long>().intersect(
                                    select(book.id).from(book).where(book.id.eq(41l)),
                                    new BlazeJPAQuery<Long>().except(
                                            select(book.id).from(book).where(book.id.eq(42l)),
                                            select(book.id).from(book).where(book.id.eq(43l)))),
                            select(book.id).from(book).where(book.id.eq(46l))
                    );

            QBook book2 = new QBook("secondBook");

            List<Book> fetch = new BlazeJPAQuery<Book>(entityManager, cbf)
                    .select(book2)
                    .from(book2).where(book2.id.in(union))
                    .fetch();

            System.out.println(fetch);
        });
    }

    @Test
    public void testCTE() {
        doInJPA(entityManager -> {

            List<Long> fetch = new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                    .with(idHolderCte, idHolderCte.id, idHolderCte.name).as(select(book.id, book.name).from(book))
                    .select(idHolderCte.id).from(idHolderCte)
                    .fetch();

            System.out.println(fetch);
        });
    }

    @Test
    public void testCTEUnion() {
        doInJPA(entityManager -> {

            List<Long> fetch = new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                    .with(idHolderCte, idHolderCte.id, idHolderCte.name).as(union(select(book.id, book.name).from(book), intersect(select(book.id, book.name).from(book), select(book.id, book.name).from(book))))
                    .select(idHolderCte.id).from(idHolderCte)
                    .fetch();

            System.out.println(fetch);
        });
    }



    @Test
    public void testCTEFromValues() {
        doInJPA(entityManager -> {
            Book theBook = new Book();
            theBook.id = 1337l;
            theBook.name = "test";

            List<Long> fetch = new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                    .with(idHolderCte, idHolderCte.id, idHolderCte.name).as(new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                            .fromValues(book, Collections.singleton(theBook))
                            .select(book.id, book.name))
                    .select(idHolderCte.id).from(idHolderCte)
                    .fetch();

            System.out.println(fetch);
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

            System.out.println(fetch);
        });
    }

    @Test
    public void testBindBuilder() {
        doInJPA(entityManager -> {

            List<Long> fetch = new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                    .with(idHolderCte)
                        .bind(idHolderCte.id).select(book.id)
                        .bind(idHolderCte.name).select(book.name)
                        .from(book).where(book.id.eq(1L))
                    .end()
                    .select(idHolderCte.id).from(idHolderCte)
                    .fetch();

            System.out.println(fetch);
        });
    }

    @Test
    @Ignore
    public void testRecursiveBindBuilder() {
        doInJPA(entityManager -> {

            List<Long> fetch = new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                    .withRecursive(idHolderCte)
                        .bind(idHolderCte.id).select(book.id)
                        .bind(idHolderCte.name).select(book.name)
                        .from(book).where(book.id.eq(1L))
                        .union()
                        .bind(idHolderCte.id).select(book.id)
                        .bind(idHolderCte.name).select(book.name)
                        .from(book).join(idHolderCte).on(idHolderCte.id.add(1L).eq(book.id))
                    .end()
                    .select(idHolderCte.id).from(idHolderCte)
                    .fetch();

            System.out.println(fetch);
        });
    }

    @Test
    public void testInlineEntityWithLimit() {
        doInJPA(entityManager -> {
            QRecursiveEntity recursiveEntity = new QRecursiveEntity("t");

            new BlazeJPAQuery<RecursiveEntity>(entityManager, cbf)
                    .select(recursiveEntity)
                    .from(select(recursiveEntity)
                        .from(recursiveEntity)
                        .where(recursiveEntity.parent.name.eq("root1"))
                        .orderBy(recursiveEntity.name.asc())
                        .limit(1L), recursiveEntity)
                        .fetch();

        });
    }

    @Test
    public void testMultipleInlineEntityWithLimitJoin() {
        doInJPA(entityManager -> {
            QRecursiveEntity t = new QRecursiveEntity("t");
            QRecursiveEntity subT = new QRecursiveEntity("subT");
            QRecursiveEntity subT2 = new QRecursiveEntity("subT2");

            new BlazeJPAQuery<RecursiveEntity>(entityManager, cbf)
                    .select(t)
                    .from(new BlazeJPAQuery<RecursiveEntity>().select(t).from(t)
                            .leftJoin(select(subT).from(subT).where(subT.parent.name.eq("root1")).orderBy(subT.name.asc()).limit(1), subT)
                            .on(t.eq(subT))
                            .where(t.parent.name.eq("root1"))
                            .orderBy(t.name.asc())
                            .limit(1L), t)
                    .leftJoin(select(subT2).from(subT2)
                        .where(subT2.parent.name.eq("root1"))
                        .orderBy(subT2.name.asc())
                        .limit(1), subT2)
                        .on(t.eq(subT2))
                    .fetch();

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
                            .leftJoin(select(subT).from(subT).where(subT.parent.name.eq("root1")).orderBy(subT.name.asc()).limit(1), subT)
                            .on(t.eq(subT))
                            .where(t.parent.name.eq("root1"))
                            .orderBy(t.name.asc())
                            .limit(1L), t)
                    .leftJoin(select(subT2).from(subT2)
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
            QRecursiveEntity subT3 = new QRecursiveEntity("subT3");

            new BlazeJPAQuery<RecursiveEntity>(entityManager, cbf)
                    .select(t, subT2)
                    .from(t)
                    .leftJoin(select(subT).from(t.children, subT).orderBy(subT.id.asc()).limit(1), subT2)
                    .lateral()
                    .fetch();
        });
    }

}
