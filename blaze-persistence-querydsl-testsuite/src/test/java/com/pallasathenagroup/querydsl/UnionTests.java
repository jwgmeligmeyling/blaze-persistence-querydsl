package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.testsuite.AbstractCoreTest;
import com.blazebit.persistence.testsuite.base.jpa.category.NoDatanucleus;
import com.blazebit.persistence.testsuite.base.jpa.category.NoEclipselink;
import com.blazebit.persistence.testsuite.base.jpa.category.NoFirebird;
import com.blazebit.persistence.testsuite.base.jpa.category.NoMySQL;
import com.blazebit.persistence.testsuite.base.jpa.category.NoOpenJPA;
import com.blazebit.persistence.testsuite.entity.Document;
import com.blazebit.persistence.testsuite.entity.IntIdEntity;
import com.blazebit.persistence.testsuite.entity.ParameterOrderCte;
import com.blazebit.persistence.testsuite.entity.ParameterOrderCteB;
import com.blazebit.persistence.testsuite.entity.ParameterOrderEntity;
import com.blazebit.persistence.testsuite.entity.Person;
import com.blazebit.persistence.testsuite.entity.QDocument;
import com.blazebit.persistence.testsuite.entity.RecursiveEntity;
import com.blazebit.persistence.testsuite.entity.TestAdvancedCTE1;
import com.blazebit.persistence.testsuite.entity.TestAdvancedCTE2;
import com.blazebit.persistence.testsuite.entity.TestCTE;
import com.blazebit.persistence.testsuite.entity.Version;
import com.blazebit.persistence.testsuite.tx.TxVoidWork;
import com.pallasathenagroup.querydsl.api.FinalSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.LeafOngoingFinalSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.LeafOngoingSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.MiddleOngoingSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.OngoingSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.api.SetOperationBuilder;
import com.pallasathenagroup.querydsl.api.StartOngoingSetOperationCriteriaBuilder;
import com.pallasathenagroup.querydsl.impl.CriteriaBuilderImpl;
import com.pallasathenagroup.querydsl.impl.LeafOngoingSetOperationCriteriaBuilderImpl;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Param;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.pallasathenagroup.querydsl.QBook.book;
import static com.querydsl.jpa.JPAExpressions.select;
import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class UnionTests extends AbstractCoreTest {

    private static abstract class AbstractUnionTest  extends AbstractCoreTest {

        @Override
        protected Class<?>[] getEntityClasses() {
            return new Class<?>[] {
                    TestEntity.class, Author.class, Book.class, Publication.class, Publisher.class, IdHolderCte.class,
                    Document.class,
                    Person.class,
                    IntIdEntity.class,
                    Version.class,
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

    }


    @RunWith(Parameterized.class)
    public static class CriteriaBuilderImplTests extends AbstractUnionTest {

        @Parameterized.Parameters(name = "{0}")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {"UNION", (Function<CriteriaBuilderImpl<Book>, LeafOngoingSetOperationCriteriaBuilder<Book>>) SetOperationBuilder::union, (Function<LeafOngoingSetOperationCriteriaBuilder<Book>, LeafOngoingSetOperationCriteriaBuilder<Book>>) SetOperationBuilder::union, (Function<CriteriaBuilderImpl<Book>, StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::startUnion, (Function<LeafOngoingSetOperationCriteriaBuilderImpl<Book>, StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::startUnion, (Function<StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::union, (Function<StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, StartOngoingSetOperationCriteriaBuilder<Book, MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, Book>>) SetOperationBuilder::startUnion, (Function<OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::union, (Function<OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, StartOngoingSetOperationCriteriaBuilder<Book, MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, Book>>) SetOperationBuilder::startUnion},
                    {"UNION ALL", (Function<CriteriaBuilderImpl<Book>, LeafOngoingSetOperationCriteriaBuilder<Book>>) SetOperationBuilder::unionAll, (Function<LeafOngoingSetOperationCriteriaBuilder<Book>, LeafOngoingSetOperationCriteriaBuilder<Book>>) SetOperationBuilder::unionAll, (Function<CriteriaBuilderImpl<Book>, StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::startUnionAll, (Function<LeafOngoingSetOperationCriteriaBuilderImpl<Book>, StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::startUnionAll, (Function<StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::unionAll, (Function<StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, StartOngoingSetOperationCriteriaBuilder<Book, MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, Book>>) SetOperationBuilder::startUnionAll, (Function<OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::unionAll, (Function<OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, StartOngoingSetOperationCriteriaBuilder<Book, MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, Book>>) SetOperationBuilder::startUnionAll},
                    {"INTERSECT", (Function<CriteriaBuilderImpl<Book>, LeafOngoingSetOperationCriteriaBuilder<Book>>) SetOperationBuilder::intersect, (Function<LeafOngoingSetOperationCriteriaBuilder<Book>, LeafOngoingSetOperationCriteriaBuilder<Book>>) SetOperationBuilder::intersect, (Function<CriteriaBuilderImpl<Book>, StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::startIntersect, (Function<LeafOngoingSetOperationCriteriaBuilderImpl<Book>, StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::startIntersect, (Function<StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::intersect, (Function<StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, StartOngoingSetOperationCriteriaBuilder<Book, MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, Book>>) SetOperationBuilder::startIntersect, (Function<OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::intersect, (Function<OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, StartOngoingSetOperationCriteriaBuilder<Book, MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, Book>>) SetOperationBuilder::startIntersect},
                    {"INTERSECT ALL", (Function<CriteriaBuilderImpl<Book>, LeafOngoingSetOperationCriteriaBuilder<Book>>) SetOperationBuilder::intersectAll, (Function<LeafOngoingSetOperationCriteriaBuilder<Book>, LeafOngoingSetOperationCriteriaBuilder<Book>>) SetOperationBuilder::intersectAll, (Function<CriteriaBuilderImpl<Book>, StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::startIntersectAll, (Function<LeafOngoingSetOperationCriteriaBuilderImpl<Book>, StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::startIntersectAll, (Function<StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::intersectAll, (Function<StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, StartOngoingSetOperationCriteriaBuilder<Book, MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, Book>>) SetOperationBuilder::startIntersectAll, (Function<OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::intersectAll, (Function<OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, StartOngoingSetOperationCriteriaBuilder<Book, MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, Book>>) SetOperationBuilder::startIntersectAll},
                    {"EXCEPT", (Function<CriteriaBuilderImpl<Book>, LeafOngoingSetOperationCriteriaBuilder<Book>>) SetOperationBuilder::except, (Function<LeafOngoingSetOperationCriteriaBuilder<Book>, LeafOngoingSetOperationCriteriaBuilder<Book>>) SetOperationBuilder::except, (Function<CriteriaBuilderImpl<Book>, StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::startExcept, (Function<LeafOngoingSetOperationCriteriaBuilderImpl<Book>, StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::startExcept, (Function<StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::except, (Function<StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, StartOngoingSetOperationCriteriaBuilder<Book, MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, Book>>) SetOperationBuilder::startExcept, (Function<OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::except, (Function<OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, StartOngoingSetOperationCriteriaBuilder<Book, MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, Book>>) SetOperationBuilder::startExcept},
                    {"EXCEPT ALL", (Function<CriteriaBuilderImpl<Book>, LeafOngoingSetOperationCriteriaBuilder<Book>>) SetOperationBuilder::exceptAll, (Function<LeafOngoingSetOperationCriteriaBuilder<Book>, LeafOngoingSetOperationCriteriaBuilder<Book>>) SetOperationBuilder::exceptAll, (Function<CriteriaBuilderImpl<Book>, StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::startExceptAll, (Function<LeafOngoingSetOperationCriteriaBuilderImpl<Book>, StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::startExceptAll, (Function<StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::exceptAll, (Function<StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, StartOngoingSetOperationCriteriaBuilder<Book, MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, Book>>) SetOperationBuilder::startExceptAll, (Function<OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>>) SetOperationBuilder::exceptAll, (Function<OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, StartOngoingSetOperationCriteriaBuilder<Book, MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, Book>>) SetOperationBuilder::startExceptAll},
            });
        }


        @Parameterized.Parameter(1)
        public Function<CriteriaBuilderImpl<Book>, LeafOngoingSetOperationCriteriaBuilder<Book>> criteriaBuilderSetFunction;

        @Parameterized.Parameter(2)
        public Function<LeafOngoingSetOperationCriteriaBuilder<Book>, LeafOngoingSetOperationCriteriaBuilder<Book>> leafOngoingSetFunction;

        @Parameterized.Parameter(3)
        public Function<CriteriaBuilderImpl<Book>, StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>> criteriaBuilderStartSetFunction;

        @Parameterized.Parameter(4)
        public Function<LeafOngoingSetOperationCriteriaBuilder<Book>, StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>> leafOngoingSetStartSetFunction;

        @Parameterized.Parameter(5)
        public Function<StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>> startOngoingSetOperationSetFunction;

        @Parameterized.Parameter(6)
        public Function<StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, StartOngoingSetOperationCriteriaBuilder<Book, MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, Book>> startOngoingSetOperationNestedSetFunction;

        @Parameterized.Parameter(7)
        public Function<OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>> ongoingSetSetFunction;

        @Parameterized.Parameter(8)
        public Function<OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, StartOngoingSetOperationCriteriaBuilder<Book, MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, Book>> ongoingSetNestedSetFunction;

        @Parameterized.Parameter
        public String setOperation;

        private Param<Long> a = new Param<>(Long.class, "a"), b = new Param<>(Long.class, "b"), c = new Param<>(Long.class, "c"), d = new Param<>(Long.class, "d"), e = new Param<>(Long.class, "e"), f = new Param<>(Long.class, "f");

        @Test
        public void testCriteriaBuilderEmptySetOperation() {
            CriteriaBuilderImpl<Book> criteriaBuilder = new CriteriaBuilderImpl<>(new BlazeJPAQuery<>(em, cbf));

            CriteriaBuilderImpl<Book> where = criteriaBuilder.select(book).from(book).where(book.id.gt(a));

            String queryString = criteriaBuilderSetFunction.apply(where).endSet().getQueryString();
            assertEquals("SELECT book FROM Book book WHERE book.id > :a", queryString);

        }

        @Test
        public void testCriteriaBuilderSetOperation() {
            CriteriaBuilderImpl<Book> criteriaBuilder = new CriteriaBuilderImpl<>(new BlazeJPAQuery<>(em, cbf));

            CriteriaBuilderImpl<Book> where = criteriaBuilder.select(book).from(book).where(book.id.gt(a));

            LeafOngoingSetOperationCriteriaBuilder<Book> leafOngoingSetOperationCriteriaBuilder =
                    criteriaBuilderSetFunction.apply(where).select(book).from(book).where(book.id.gt(b));

            String queryString = leafOngoingSetOperationCriteriaBuilder.endSet().getQueryString();
            assertEquals("SELECT book FROM Book book WHERE book.id > :a\n" +
                    setOperation + "\n" +
                    "SELECT book FROM Book book WHERE book.id > :b", queryString);
        }

        @Test
        public void testLeafOngoingSetOperationBuilderSetOperation() {
            CriteriaBuilderImpl<Book> criteriaBuilder = new CriteriaBuilderImpl<>(new BlazeJPAQuery<>(em, cbf));

            CriteriaBuilderImpl<Book> where = criteriaBuilder.select(book).from(book).where(book.id.gt(a));

            LeafOngoingSetOperationCriteriaBuilder<Book> leafOngoingSetOperationCriteriaBuilder =
                    criteriaBuilderSetFunction.apply(where).select(book).from(book).where(book.id.gt(b));

            leafOngoingSetOperationCriteriaBuilder = leafOngoingSetFunction.apply(leafOngoingSetOperationCriteriaBuilder)
                    .select(book).from(book).where(book.id.gt(c));

            String queryString = leafOngoingSetOperationCriteriaBuilder.endSet().getQueryString();
            assertEquals("SELECT book FROM Book book WHERE book.id > :a\n" +
                    setOperation + "\n" +
                    "SELECT book FROM Book book WHERE book.id > :b\n" +
                    setOperation + "\n" +
                    "SELECT book FROM Book book WHERE book.id > :c", queryString);

        }

        @Test
        public void testLeafOngoingSetOperationBuilderEmptySetOperation() {
            CriteriaBuilderImpl<Book> criteriaBuilder = new CriteriaBuilderImpl<>(new BlazeJPAQuery<>(em, cbf));

            CriteriaBuilderImpl<Book> where = criteriaBuilder.select(book).from(book).where(book.id.gt(a));

            LeafOngoingSetOperationCriteriaBuilder<Book> leafOngoingSetOperationCriteriaBuilder =
                    criteriaBuilderSetFunction.apply(where).select(book).from(book).where(book.id.gt(b));

            leafOngoingSetOperationCriteriaBuilder = leafOngoingSetFunction.apply(leafOngoingSetOperationCriteriaBuilder);

            String queryString = leafOngoingSetOperationCriteriaBuilder.endSet().getQueryString();
            assertEquals("SELECT book FROM Book book WHERE book.id > :a\n" +
                    setOperation + "\n" +
                    "SELECT book FROM Book book WHERE book.id > :b", queryString);
        }

        @Test
        public void testCriteriaBuilderStartRhsSet() {
            CriteriaBuilderImpl<Book> criteriaBuilder = new CriteriaBuilderImpl<>(new BlazeJPAQuery<>(em, cbf));

            CriteriaBuilderImpl<Book> where = criteriaBuilder.select(book).from(book).where(book.id.gt(a));

            StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> apply = criteriaBuilderStartSetFunction.apply(where);
            OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> where1 =
                    apply
                            .select(book).from(book).where(book.id.gt(b))
                            .union()
                            .select(book).from(book).where(book.id.gt(c));


            String queryString = where1.endSet().endSet().getQueryString();
            assertEquals("SELECT book FROM Book book WHERE book.id > :a\n" +
                    setOperation + "\n" +
                    "(SELECT book FROM Book book WHERE book.id > :b\nUNION\nSELECT book FROM Book book WHERE book.id > :c)", queryString);
        }

        @Test
        public void testLeafOngoingSetOperationBuilderStartRhsSet() {
            CriteriaBuilderImpl<Book> criteriaBuilder = new CriteriaBuilderImpl<>(new BlazeJPAQuery<>(em, cbf));

            CriteriaBuilderImpl<Book> where = criteriaBuilder.select(book).from(book).where(book.id.gt(a));

            LeafOngoingSetOperationCriteriaBuilder<Book> leafOngoingSetOperationCriteriaBuilder =
                    where.union().select(book).from(book).where(book.id.gt(b));

            OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> where1 = leafOngoingSetStartSetFunction.apply(leafOngoingSetOperationCriteriaBuilder)
                    .select(book).from(book).where(book.id.gt(c))
                    .except()
                    .select(book).from(book).where(book.id.gt(d));


            String queryString = where1.endSet().endSet().getQueryString();
            assertEquals("SELECT book FROM Book book WHERE book.id > :a\n" +
                    "UNION\n" +
                    "SELECT book FROM Book book WHERE book.id > :b\n" +
                    ""+ setOperation + "\n" +
                    "(SELECT book FROM Book book WHERE book.id > :c\n" +
                    "EXCEPT\n" +
                    "SELECT book FROM Book book WHERE book.id > :d)", queryString);

        }

        @Test
        public void testLeafOngoingSetOperationBuilderStartRhsEmptySet() {
            CriteriaBuilderImpl<Book> criteriaBuilder = new CriteriaBuilderImpl<>(new BlazeJPAQuery<>(em, cbf));

            CriteriaBuilderImpl<Book> where = criteriaBuilder.select(book).from(book).where(book.id.gt(a));

            LeafOngoingSetOperationCriteriaBuilder<Book> leafOngoingSetOperationCriteriaBuilder =
                    where.union().select(book).from(book).where(book.id.gt(b));

            StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> where1 = leafOngoingSetStartSetFunction.apply(leafOngoingSetOperationCriteriaBuilder);


            String queryString = where1.endSet().endSet().getQueryString();
            assertEquals("SELECT book FROM Book book WHERE book.id > :a\n" +
                    "UNION\n" +
                    "SELECT book FROM Book book WHERE book.id > :b", queryString);

        }


        @Test
        public void testStartOngoingSetOperationBuilderSetOperation() {
            CriteriaBuilderImpl<Book> criteriaBuilder = new CriteriaBuilderImpl<>(new BlazeJPAQuery<>(em, cbf));

            CriteriaBuilderImpl<Book> where = criteriaBuilder.select(book).from(book).where(book.id.gt(a));

            LeafOngoingSetOperationCriteriaBuilder<Book> leafOngoingSetOperationCriteriaBuilder =
                    where.union().select(book).from(book).where(book.id.gt(b));

            StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> startOngoingSetOperationCriteriaBuilder = leafOngoingSetOperationCriteriaBuilder.startIntersect()
                    .select(book).from(book).where(book.id.gt(c));

            OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> where1 = startOngoingSetOperationSetFunction.apply(startOngoingSetOperationCriteriaBuilder)
                    .select(book).from(book).where(book.id.gt(d))
                    .union().select(book).from(book).where(book.id.gt(e));


            LeafOngoingFinalSetOperationCriteriaBuilder<Book> bookLeafOngoingFinalSetOperationCriteriaBuilder = where1.endSet();
            FinalSetOperationCriteriaBuilder<Book> bookFinalSetOperationCriteriaBuilder = bookLeafOngoingFinalSetOperationCriteriaBuilder.endSet();
            String queryString = bookFinalSetOperationCriteriaBuilder.getQueryString();
            assertEquals("SELECT book FROM Book book WHERE book.id > :a\n" +
                    "UNION\n" +
                    "SELECT book FROM Book book WHERE book.id > :b\n" +
                    "INTERSECT\n" +
                    "(SELECT book FROM Book book WHERE book.id > :c\n"
                    + setOperation + "\n" +
                    "SELECT book FROM Book book WHERE book.id > :d\n" +
                    "UNION\n" +
                    "SELECT book FROM Book book WHERE book.id > :e)", queryString);
        }

        @Test
        public void testStartOngoingSetOperationBuilderEmptySetOperation() {
            CriteriaBuilderImpl<Book> criteriaBuilder = new CriteriaBuilderImpl<>(new BlazeJPAQuery<>(em, cbf));

            CriteriaBuilderImpl<Book> where = criteriaBuilder.select(book).from(book).where(book.id.gt(a));

            LeafOngoingSetOperationCriteriaBuilder<Book> leafOngoingSetOperationCriteriaBuilder =
                    where.union().select(book).from(book).where(book.id.gt(b));

            StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> startOngoingSetOperationCriteriaBuilder = leafOngoingSetOperationCriteriaBuilder.startIntersect()
                    .select(book).from(book).where(book.id.gt(c));

            OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> where1 = startOngoingSetOperationSetFunction.apply(startOngoingSetOperationCriteriaBuilder);


            String queryString = where1.endSet().endSet().getQueryString();
            assertEquals("SELECT book FROM Book book WHERE book.id > :a\n" +
                    "UNION\n" +
                    "SELECT book FROM Book book WHERE book.id > :b\n" +
                    "INTERSECT\n" +
                    "SELECT book FROM Book book WHERE book.id > :c", queryString);

        }

        @Test
        public void testStartOngoingSetOperationBuilderNestedSetOperation() {
            CriteriaBuilderImpl<Book> criteriaBuilder = new CriteriaBuilderImpl<>(new BlazeJPAQuery<>(em, cbf));

            CriteriaBuilderImpl<Book> where = criteriaBuilder.select(book).from(book).where(book.id.gt(a));

            LeafOngoingSetOperationCriteriaBuilder<Book> leafOngoingSetOperationCriteriaBuilder =
                    where.union().select(book).from(book).where(book.id.gt(b));

            StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> startOngoingSetOperationCriteriaBuilder = leafOngoingSetOperationCriteriaBuilder.startIntersect()
                    .select(book).from(book).where(book.id.gt(c));

            OngoingSetOperationCriteriaBuilder<Book, MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, Book> where1 = startOngoingSetOperationNestedSetFunction.apply(startOngoingSetOperationCriteriaBuilder)
                    .select(book).from(book).where(book.id.gt(d)).except().select(book).from(book).where(book.id.gt(e));


            String queryString = where1.endSet().endSet().endSet().getQueryString();
            assertEquals("SELECT book FROM Book book WHERE book.id > :a\n" +
                    "UNION\n" +
                    "SELECT book FROM Book book WHERE book.id > :b\n" +
                    "INTERSECT\n" +
                    "(SELECT book FROM Book book WHERE book.id > :c\n" +
                    setOperation + "\n" +
                    "(SELECT book FROM Book book WHERE book.id > :d\n" +
                    "EXCEPT\n" +
                    "SELECT book FROM Book book WHERE book.id > :e))", queryString);
        }

        @Test
        public void testStartOngoingSetOperationBuilderNestedEmptySetOperation() {
            CriteriaBuilderImpl<Book> criteriaBuilder = new CriteriaBuilderImpl<>(new BlazeJPAQuery<>(em, cbf));

            CriteriaBuilderImpl<Book> where = criteriaBuilder.select(book).from(book).where(book.id.gt(a));

            LeafOngoingSetOperationCriteriaBuilder<Book> leafOngoingSetOperationCriteriaBuilder =
                    where.union().select(book).from(book).where(book.id.gt(b));

            StartOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> startOngoingSetOperationCriteriaBuilder = leafOngoingSetOperationCriteriaBuilder.startIntersect()
                    .select(book).from(book).where(book.id.gt(c));

            StartOngoingSetOperationCriteriaBuilder<Book, MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, Book> where1 = startOngoingSetOperationNestedSetFunction.apply(startOngoingSetOperationCriteriaBuilder);

            String queryString = where1.endSet().endSet().endSet().getQueryString();
            assertEquals("SELECT book FROM Book book WHERE book.id > :a\n" +
                    "UNION\n" +
                    "SELECT book FROM Book book WHERE book.id > :b\n" +
                    "INTERSECT\n" +
                    "SELECT book FROM Book book WHERE book.id > :c", queryString);
        }


        OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> setOperation(OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> builder) {
            return ongoingSetSetFunction.apply(builder);
        }

        @Test
        public void testOngoingSetOperationBuilderSetOperation() {
            CriteriaBuilderImpl<Book> criteriaBuilder = new CriteriaBuilderImpl<>(new BlazeJPAQuery<>(em, cbf));

            CriteriaBuilderImpl<Book> where = criteriaBuilder.select(book).from(book).where(book.id.gt(a));

            OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> union = where
                    .startUnion().select(book).from(book).where(book.id.gt(b))
                    .intersect()
                    .select(book).from(book).where(book.id.gt(c));

            OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> step1 = setOperation(union);
            OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> step2 = step1.select(book).from(book).where(book.id.gt(d));
            OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> step3 = step2.except();
            OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> step4 = step3.select(book).from(book).where(book.id.gt(e));

            LeafOngoingFinalSetOperationCriteriaBuilder<Book> step5 = step4.endSet();
            FinalSetOperationCriteriaBuilder<Book> step6 = step5.endSet();

            String queryString = step6.getQueryString();
            assertEquals("SELECT book FROM Book book WHERE book.id > :a\n" +
                    "UNION\n" +
                    "(SELECT book FROM Book book WHERE book.id > :b\n" +
                    "INTERSECT\n" +
                    "SELECT book FROM Book book WHERE book.id > :c\n" +
                    setOperation + "\n" +
                    "SELECT book FROM Book book WHERE book.id > :d\n" +
                    "EXCEPT\n" +
                    "SELECT book FROM Book book WHERE book.id > :e)", queryString);
        }

        @Test
        public void testOngoingSetOperationBuilderEmptySetOperation() {
            CriteriaBuilderImpl<Book> criteriaBuilder = new CriteriaBuilderImpl<>(new BlazeJPAQuery<>(em, cbf));

            CriteriaBuilderImpl<Book> where = criteriaBuilder.select(book).from(book).where(book.id.gt(a));

            OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> union = where
                    .startUnion().select(book).from(book).where(book.id.gt(b))
                    .intersect()
                    .select(book).from(book).where(book.id.gt(c));

            OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> step1 = setOperation(union);

            LeafOngoingFinalSetOperationCriteriaBuilder<Book> step5 = step1.endSet();
            FinalSetOperationCriteriaBuilder<Book> step6 = step5.endSet();

            String queryString = step6.getQueryString();
            assertEquals("SELECT book FROM Book book WHERE book.id > :a\n" +
                    "UNION\n" +
                    "(SELECT book FROM Book book WHERE book.id > :b\n" +
                    "INTERSECT\n" +
                    "SELECT book FROM Book book WHERE book.id > :c)", queryString);
        }

        private StartOngoingSetOperationCriteriaBuilder<Book, MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, Book> nestedSetOperation(OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> union) {
            return ongoingSetNestedSetFunction.apply(union);
        }

        @Test
        public void testOngoingSetOperationBuilderNestedSetOperation() {
            CriteriaBuilderImpl<Book> criteriaBuilder = new CriteriaBuilderImpl<>(new BlazeJPAQuery<>(em, cbf));

            CriteriaBuilderImpl<Book> where = criteriaBuilder.select(book).from(book).where(book.id.gt(a));

            OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> union = where
                    .startUnion().select(book).from(book).where(book.id.gt(b))
                    .intersect()
                    .select(book).from(book).where(book.id.gt(c));

            StartOngoingSetOperationCriteriaBuilder<Book, MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, Book> step1 = nestedSetOperation(union);

            StartOngoingSetOperationCriteriaBuilder<Book, MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, Book> step2 = step1.select(book).from(book).where(book.id.gt(d));
            OngoingSetOperationCriteriaBuilder<Book, MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, Book> step3 = step2.except();
            OngoingSetOperationCriteriaBuilder<Book, MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, Book> step4 = step3.select(book).from(book).where(book.id.gt(e));

            MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> step5 = step4.endSet();
            LeafOngoingFinalSetOperationCriteriaBuilder<Book> step6 = step5.endSet();
            FinalSetOperationCriteriaBuilder<Book> step7 = step6.endSet();

            String queryString = step7.getQueryString();
            assertEquals("SELECT book FROM Book book WHERE book.id > :a\n" +
                    "UNION\n" +
                    "(SELECT book FROM Book book WHERE book.id > :b\n" +
                    "INTERSECT\n" +
                    "SELECT book FROM Book book WHERE book.id > :c\n" +
                    setOperation + "\n" +
                    "(SELECT book FROM Book book WHERE book.id > :d\n" +
                    "EXCEPT\n" +
                    "SELECT book FROM Book book WHERE book.id > :e))", queryString);
        }

        @Test
        public void testOngoingSetOperationBuilderNestedEmptySetOperation() {
            CriteriaBuilderImpl<Book> criteriaBuilder = new CriteriaBuilderImpl<>(new BlazeJPAQuery<>(em, cbf));

            CriteriaBuilderImpl<Book> where = criteriaBuilder.select(book).from(book).where(book.id.gt(a));

            OngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> union = where
                    .startUnion().select(book).from(book).where(book.id.gt(b))
                    .intersect()
                    .select(book).from(book).where(book.id.gt(c));

            StartOngoingSetOperationCriteriaBuilder<Book, MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book>, Book> step1 = nestedSetOperation(union);


            MiddleOngoingSetOperationCriteriaBuilder<Book, LeafOngoingFinalSetOperationCriteriaBuilder<Book>, Book> step5 = step1.endSet();
            LeafOngoingFinalSetOperationCriteriaBuilder<Book> step6 = step5.endSet();
            FinalSetOperationCriteriaBuilder<Book> step7 = step6.endSet();

            String queryString = step7.getQueryString();
            assertEquals("SELECT book FROM Book book WHERE book.id > :a\n" +
                    "UNION\n" +
                    "(SELECT book FROM Book book WHERE book.id > :b\n" +
                    "INTERSECT\n" +
                    "SELECT book FROM Book book WHERE book.id > :c)", queryString);
        }

    }


    public static class SimpleUnionTest extends AbstractUnionTest {

        @Test
        public void testQueryDSL() {
            doInJPA(entityManager -> {
                String expectedQueryString = cbf.create(entityManager, Book.class)
                    .from(Book.class, "book")
                        .where("book.id").lt(1337l).select("book")
                    .union()
                    .from(Book.class, "book")
                        .where("book.id").gt(1337l).select("book")
                    .endSet()
                    .getQueryString();
                String queryString = new BlazeJPAQuery<TestEntity>(entityManager, cbf)
                        .union(
                            select(book).from(book).where(book.id.lt(1337L)),
                            select(book).from(book).where(book.id.gt(1337L))
                        )
                        .getQueryString();


                assertEquals(expectedQueryString, queryString);
            });
        }

    }

    public static class ComplexUnionTest extends AbstractUnionTest {

        @Test
        public void testQueryDSL() {
            doInJPA(entityManager -> {
                String expectedQueryString = cbf.create(entityManager, Book.class)
                        .from(Book.class).select("book").where("book.id").eq(1337L)
                        .startUnion()
                        .from(Book.class).select("book").where("book.id").eq(41L)
                        .startIntersect()
                        .from(Book.class).select("book").where("book.id").eq(42L)
                        .startExcept()
                        .from(Book.class).select("book").where("book.id").eq(43L)
                        .endSet()
                        .endSet()
                        .endSet()
                        .union()
                        .from(Book.class).select("book").where("book.id").eq(46L)
                        .endSet()
                        .getQueryString();


                String queryString = new BlazeJPAQuery<Book>(entityManager, cbf)
                        .union(select(book).from(book).where(book.id.eq(1337L)),
                                new BlazeJPAQuery<TestEntity>().intersect(
                                        select(book).from(book).where(book.id.eq(41L)),
                                        new BlazeJPAQuery<TestEntity>().except(
                                                select(book).from(book).where(book.id.eq(42L)),
                                                select(book).from(book).where(book.id.eq(43L)))),
                                select(book).from(book).where(book.id.eq(46L))
                        )
                        .getQueryString();

                assertEquals(expectedQueryString, queryString);

            });
        }


        @Test
        public void testPrecedence() {
            QDocument d1 = new QDocument("d1");
            QDocument d2 = new QDocument("d2");
            QDocument d3 = new QDocument("d3");

            FinalSetOperationCriteriaBuilder<String> cb = new CriteriaBuilderImpl<String>(new BlazeJPAQuery<>(em, cbf))
                    .from(d1)
                    .select(d1.name)
                    .where(d1.name.eq("d1"))
                    .intersect()
                    .from(d2)
                    .select(d2.name)
                    .where(d2.name.ne("d2"))
                    .except()
                    .from(d3)
                    .select(d3.name)
                    .where(d3.name.eq("d3"))
                    .endSet();
            String expected = ""
                    + "SELECT d1.name FROM Document d1 WHERE d1.name = :param_0\n"
                    + "INTERSECT\n"
                    + "SELECT d2.name FROM Document d2 WHERE d2.name <> :param_1\n"
                    + "EXCEPT\n"
                    + "SELECT d3.name FROM Document d3 WHERE d3.name = :param_2";

            assertEquals(expected, cb.getQueryString());
        }


        @Test
        public void testUnionAll() {
            QDocument d1 = new QDocument("d1");
            QDocument d2 = new QDocument("d2");
            QDocument d3 = new QDocument("d3");

            FinalSetOperationCriteriaBuilder<Document> cb = new CriteriaBuilderImpl<Document>(new BlazeJPAQuery<>(em, cbf))
                    .from(d1)
                    .select(d1)
                    .where(d1.name.eq("d1"))
                    .unionAll()

                    .from(d2)
                    .select(d2)
                    .where(d2.name.eq("d2"))
                    .endSet();

            String expected = ""
                    + "SELECT d1 FROM Document d1 WHERE d1.name = :param_0\n"
                    + "UNION ALL\n"
                    + "SELECT d2 FROM Document d2 WHERE d2.name = :param_1";

            assertEquals(expected, cb.getQueryString());
        }

        @Test
        public void testUnionAllOrderBy() {
            QDocument d1 = new QDocument("d1");
            QDocument d2 = new QDocument("d2");
            QDocument d3 = new QDocument("d3");

            FinalSetOperationCriteriaBuilder<Document> cb = new CriteriaBuilderImpl<Document>(new BlazeJPAQuery<>(em, cbf))
                    .from(d1)
                    .select(d1)
                    .where(d1.name.eq("d1"))
                    .unionAll()

                    .from(d2)
                    .select(d2)
                    .where(d2.name.eq("d2"))
                    .endSet()
                    .orderBy(Expressions.stringPath("name").asc());

            String expected = ""
                    + "SELECT d1 FROM Document d1 WHERE d1.name = :param_0\n"
                    + "UNION ALL\n"
                    + "SELECT d2 FROM Document d2 WHERE d2.name = :param_1\n"
                    + "ORDER BY name ASC";

            assertEquals(expected, cb.getQueryString());
        }

        @Test
        public void testUnionAllOrderByOperandLimit() {
            QDocument d1 = new QDocument("d1");
            QDocument d2 = new QDocument("d2");
            QDocument d3 = new QDocument("d3");

            FinalSetOperationCriteriaBuilder<Document> cb = new CriteriaBuilderImpl<Document>(new BlazeJPAQuery<>(em, cbf))
                    .from(d1)
                    .select(d1)
                    .where(d1.name.eq("d1"))
                    .unionAll()

                    .from(d2)
                    .select(d2)
                    .where(d2.name.ne("d2"))
                    .orderBy(d2.name.asc()).limit(1L)
                    .endSet()
                    .orderBy(Expressions.stringPath("name").desc()).limit(1L);

            String expected = ""
                    + "SELECT d1 FROM Document d1 WHERE d1.name = :param_0\n"
                    + "UNION ALL\n"
                    + "SELECT d2 FROM Document d2 WHERE d2.name <> :param_1 ORDER BY d2.name ASC LIMIT 1\n"
                    + "ORDER BY name DESC LIMIT 1";

            assertEquals(expected, cb.getQueryString());
        }


        @Test
        @Category({ NoMySQL.class, NoFirebird.class, NoDatanucleus.class, NoEclipselink.class, NoOpenJPA.class })
        public void testIntersectWithNestedUnion() {
            QDocument d1 = new QDocument("d1");
            QDocument d2 = new QDocument("d2");
            QDocument d3 = new QDocument("d3");

            FinalSetOperationCriteriaBuilder<Document> cb = new CriteriaBuilderImpl<Document>(new BlazeJPAQuery<>(em, cbf))
                    .from(d1)
                    .select(d1)
                    .where(d1.name.ne("d1"))
                    .startIntersect()
                    .from(d2)
                    .select(d2)
                    .where(d2.name.ne("d2"))
                    .union()
                    .from(d3)
                    .select(d3)
                    .where(d3.name.eq("d3"))
                    .endSet()
                    .endSet();
            String expected = ""
                    + "SELECT d1 FROM Document d1 WHERE d1.name <> :param_0\n"
                    + "INTERSECT\n"
                    + "(SELECT d2 FROM Document d2 WHERE d2.name <> :param_1\n"
                    + "UNION\n"
                    + "SELECT d3 FROM Document d3 WHERE d3.name = :param_2)";

            assertEquals(expected, cb.getQueryString());
        }


        @Test
        @Category({ NoMySQL.class, NoFirebird.class, NoDatanucleus.class, NoEclipselink.class, NoOpenJPA.class })
        public void testRightNesting() {
            QDocument d1 = new QDocument("d1");
            QDocument d2 = new QDocument("d2");
            QDocument d3 = new QDocument("d3");
            QDocument d4 = new QDocument("d4");
            QDocument d5 = new QDocument("d5");
            QDocument d6 = new QDocument("d6");

            FinalSetOperationCriteriaBuilder<Document> cb = new CriteriaBuilderImpl<Document>(new BlazeJPAQuery<>(em, cbf))
                    .from(d1)
                    .select(d1)
                    .where(d1.name.eq("d1"))
                    .startExcept()
                    .from(d2)
                    .select(d2)
                    .where(d2.name.eq("d2"))
                    .startUnion()
                    .from(d3)
                    .select(d3)
                    .where(d3.name.eq("d3"))
                    .union()
                    .from(d4)
                    .select(d4)
                    .where(d4.name.eq("d4"))
                    .endSet()
                    .union()
                    .from(d5)
                    .select(d5)
                    .where(d5.name.eq("d5"))
                    .endSet()
                    .union()
                    .from(d6)
                    .select(d6)
                    .where(d6.name.eq("d6"))
                    .endSet();
            String expected = ""
                    + "SELECT d1 FROM Document d1 WHERE d1.name = :param_0\n"
                    + "EXCEPT\n"
                    + "(SELECT d2 FROM Document d2 WHERE d2.name = :param_1\n"
                    + "UNION\n"
                    + "(SELECT d3 FROM Document d3 WHERE d3.name = :param_2\n"
                    + "UNION\n"
                    + "SELECT d4 FROM Document d4 WHERE d4.name = :param_3)\n"
                    + "UNION\n"
                    + "SELECT d5 FROM Document d5 WHERE d5.name = :param_4)\n"
                    + "UNION\n"
                    + "SELECT d6 FROM Document d6 WHERE d6.name = :param_5";

            assertEquals(expected, cb.getQueryString());
        }


        @Test
        @Ignore("Left nested sets are currently not supported, nor are they supported in QueryDSL SQL")
        @Category({ NoMySQL.class, NoFirebird.class, NoDatanucleus.class, NoEclipselink.class, NoOpenJPA.class })
        public void testLeftNesting() {
            QDocument d1 = new QDocument("d1");
            QDocument d2 = new QDocument("d2");
            QDocument d3 = new QDocument("d3");
            QDocument d4 = new QDocument("d4");
            QDocument d5 = new QDocument("d5");
            QDocument d6 = new QDocument("d6");

            FinalSetOperationCriteriaBuilder<Document> cb = new CriteriaBuilderImpl<Document>(new BlazeJPAQuery<>(em, cbf))
                    .startSet()
                    .startSet()
                    .startSet()
                    .from(d1)
                    .select(d1)
                    .where(d1.name.eq("D1"))
                    .intersect()
                    .from(d2)
                    .select(d2)
                    .where(d2.name.eq("D2"))
                    .endSet()
                    .union()
                    .from(d3)
                    .select(d3)
                    .where(d3.name.eq("D3"))
                    .endSet()
                    .union()
                    .from(d4)
                    .select(d4)
                    .where(d4.name.eq("D4"))
                    .endSet()
                    .union()
                    .from(d5)
                    .select(d5)
                    .where(d5.name.eq("D5"))
                    .endSet();
            String expected = ""
                    + "(((SELECT d1 FROM Document d1 WHERE d1.name = :param_0\n"
                    + "INTERSECT\n"
                    + "SELECT d2 FROM Document d2 WHERE d2.name = :param_1)\n"
                    + "UNION\n"
                    + "SELECT d3 FROM Document d3 WHERE d3.name = :param_2)\n"
                    + "UNION\n"
                    + "SELECT d4 FROM Document d4 WHERE d4.name = :param_3)\n"
                    + "UNION\n"
                    + "SELECT d5 FROM Document d5 WHERE d5.name = :param_4";

            assertEquals(expected, cb.getQueryString());
        }

        @Test
        public void testDeepNesting() {
            Param<Long> a = new Param<>(Long.class, "a");
            Param<Long> b = new Param<>(Long.class, "b");
            Param<Long> c = new Param<>(Long.class, "c");
            Param<Long> d = new Param<>(Long.class, "d");
            Param<Long> e = new Param<>(Long.class, "e");
            Param<Long> f = new Param<>(Long.class, "f");

            CriteriaBuilderImpl<Book> criteriaBuilder = new CriteriaBuilderImpl<>(new BlazeJPAQuery<>(em, cbf));

            String queryString = criteriaBuilder.select(book).from(book).where(book.id.gt(a))
                    .startUnion()
                    .select(book).from(book).where(book.id.gt(b))
                    .startExcept()
                    .select(book).from(book).where(book.id.gt(c))
                    .startIntersect()
                    .select(book).from(book).where(book.id.gt(d))
                    .startUnionAll()
                    .select(book).from(book).where(book.id.gt(e))
                    .exceptAll()
                    .select(book).from(book).where(book.id.gt(f))
                    .endSet()
                    .endSet()
                    .endSet()
                    .endSet()
                    .endSet()
                    .getQueryString();
            ;

            assertEquals("SELECT book FROM Book book WHERE book.id > :a\n" +
                    "UNION\n" +
                    "(SELECT book FROM Book book WHERE book.id > :b\n" +
                    "EXCEPT\n" +
                    "(SELECT book FROM Book book WHERE book.id > :c\n" +
                    "INTERSECT\n" +
                    "(SELECT book FROM Book book WHERE book.id > :d\n" +
                    "UNION ALL\n" +
                    "(SELECT book FROM Book book WHERE book.id > :e\n" +
                    "EXCEPT ALL\n" +
                    "SELECT book FROM Book book WHERE book.id > :f))))", queryString);

        }

        @Test
        public void testFluent() {
            doInJPA(entityManager -> {

                CriteriaBuilderImpl<Book> criteriaBuilder = new CriteriaBuilderImpl<>(new BlazeJPAQuery<>(entityManager, cbf));
                CriteriaBuilderImpl<Book> criteriaBuilder2 = new CriteriaBuilderImpl<>(new BlazeJPAQuery<>(entityManager, cbf));
                Param<Long> a = new Param<>(Long.class, "a");
                Param<Long> b = new Param<>(Long.class, "b");
                Param<Long> c = new Param<>(Long.class, "c");
                Param<Long> d = new Param<>(Long.class, "d");
                Param<Long> e = new Param<>(Long.class, "e");
                Param<Long> f = new Param<>(Long.class, "f");
                Param<Long> g = new Param<>(Long.class, "g");
                Param<Long> h = new Param<>(Long.class, "h");
                Param<Long> i = new Param<>(Long.class, "i");
                Param<Long> j = new Param<>(Long.class, "j");
                Param<Long> k = new Param<>(Long.class, "k");
                Param<Long> l = new Param<>(Long.class, "l");


                String queryString2 = cbf.create(em, Book.class)
                        .select("book").from(Book.class, "book").where("book.id").gtExpression(":a")
                        .unionAll()
                        .select("book").from(Book.class, "book").where("book.id").gtExpression(":b")
                        .except()
                        .select("book").from(Book.class, "book").where("book.id").gtExpression(":c")
                        .startUnion()
                        .select("book").from(Book.class, "book").where("book.id").gtExpression(":d")
                        .intersect()
                        .select("book").from(Book.class, "book").where("book.id").gtExpression(":e")
                        .startUnion()
                        .select("book").from(Book.class, "book").where("book.id").gtExpression(":f")
                        .intersect()
                        .select("book").from(Book.class, "book").where("book.id").gtExpression(":g")
                        .endSetWith()
                        .orderByAsc("name").setMaxResults(1)
                        .endSet()
//                        .startExcept()
//                        .endSet()
                        .startExceptAll()
                        .startIntersect()
                        .select("book").from(Book.class, "book").where("book.id").gtExpression(":h")
                        .unionAll()
                        .select("book").from(Book.class, "book").where("book.id").gtExpression(":i")
                        .endSet()
                        .except()
                        .select("book").from(Book.class, "book").where("book.id").gtExpression(":j")
                        .endSet()

                        .intersect()
                        .select("book").from(Book.class, "book").where("book.id").gtExpression(":k")
                        .endSet()
                        .intersect()
                        .select("book").from(Book.class, "book").where("book.id").gtExpression(":l")
                        .endSet()
                        .orderByAsc("name")
                        .getQueryString();

                assertEquals("SELECT book FROM Book book WHERE book.id > :a\n" +
                        "UNION ALL\n" +
                        "SELECT book FROM Book book WHERE book.id > :b\n" +
                        "EXCEPT\n" +
                        "SELECT book FROM Book book WHERE book.id > :c\n" +
                        "UNION\n" +
                        "(SELECT book FROM Book book WHERE book.id > :d\n" +
                        "INTERSECT\n" +
                        "SELECT book FROM Book book WHERE book.id > :e\n" +
                        "UNION\n" +
                        "(SELECT book FROM Book book WHERE book.id > :f\n" +
                        "INTERSECT\n" +
                        "SELECT book FROM Book book WHERE book.id > :g\n" +
                        "ORDER BY name ASC NULLS LAST LIMIT 1)\n" +
                        "EXCEPT ALL\n" +
                        "((SELECT book FROM Book book WHERE book.id > :h\n" +
                        "UNION ALL\n" +
                        "SELECT book FROM Book book WHERE book.id > :i)\n" +
                        "EXCEPT\n" +
                        "SELECT book FROM Book book WHERE book.id > :j)\n" +
                        "INTERSECT\n" +
                        "SELECT book FROM Book book WHERE book.id > :k)\n" +
                        "INTERSECT\n" +
                        "SELECT book FROM Book book WHERE book.id > :l\n" +
                        "ORDER BY name ASC NULLS LAST", queryString2);

                FinalSetOperationCriteriaBuilder<Book> bookFinalSetOperationCriteriaBuilder = criteriaBuilder.select(book).from(book).where(book.id.gt(a))
                        .unionAll()
                        .select(book).from(book).where(book.id.gt(b))
                        .except()
                        .select(book).from(book).where(book.id.gt(c))
                        .startUnion()
                        .select(book).from(book).where(book.id.gt(d))
                        .intersect()
                        .select(book).from(book).where(book.id.gt(e))
                        .startUnion()
                        .select(book).from(book).where(book.id.gt(f))
                        .intersect()
                        .select(book).from(book).where(book.id.gt(g))
                        .endSetWith()
                        .orderBy(Expressions.stringPath("name").asc()).limit(1)
                        .endSet()
                        .startExcept()
                        .endSet()
                        .startExceptAll()
                        .startIntersect()
                        .select(book).from(book).where(book.id.gt(h))
                        .unionAll()
                        .select(book).from(book).where(book.id.gt(i))
                        .endSet()
                        .except()
                        .select(book).from(book).where(book.id.gt(j))
                        .endSet()

                        .intersect()
                        .select(book).from(book).where(book.id.gt(k))
                        .endSet()
                        .intersect()
                                        .select(book).from(book).where(book.id.gt(l))
                                .endSet()
                            .orderBy(Expressions.stringPath("name").asc());

                String queryString = bookFinalSetOperationCriteriaBuilder
                        .getQueryString();

                assertEquals("SELECT book FROM Book book WHERE book.id > :a\n" +
                        "UNION ALL\n" +
                        "SELECT book FROM Book book WHERE book.id > :b\n" +
                        "EXCEPT\n" +
                        "SELECT book FROM Book book WHERE book.id > :c\n" +
                        "UNION\n" +
                        "(SELECT book FROM Book book WHERE book.id > :d\n" +
                        "INTERSECT\n" +
                        "SELECT book FROM Book book WHERE book.id > :e\n" +
                        "UNION\n" +
                        "(SELECT book FROM Book book WHERE book.id > :f\n" +
                        "INTERSECT\n" +
                        "SELECT book FROM Book book WHERE book.id > :g\n" +
                        "ORDER BY name ASC NULLS LAST LIMIT 1)\n" +
                        "EXCEPT ALL\n" +
                        "(SELECT book FROM Book book WHERE book.id > :h\n" +
                        "UNION ALL\n" +
                        "SELECT book FROM Book book WHERE book.id > :i\n" +
                        "EXCEPT\n" +
                        "SELECT book FROM Book book WHERE book.id > :j)\n" +
                        "INTERSECT\n" +
                        "SELECT book FROM Book book WHERE book.id > :k)\n" +
                        "INTERSECT\n" +
                        "SELECT book FROM Book book WHERE book.id > :l\n" +
                        "ORDER BY name ASC NULLS LAST", queryString);

                QBook outerBook = new QBook("outerBook");

                String queryString1 = criteriaBuilder2.select(outerBook).from(outerBook).where(outerBook.in(bookFinalSetOperationCriteriaBuilder)).getQueryString();

                System.out.println(queryString);
            });
        }

    }

}
