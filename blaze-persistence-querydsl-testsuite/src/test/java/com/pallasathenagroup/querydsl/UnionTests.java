package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.testsuite.AbstractCoreTest;
import com.blazebit.persistence.testsuite.entity.ParameterOrderCte;
import com.blazebit.persistence.testsuite.entity.ParameterOrderCteB;
import com.blazebit.persistence.testsuite.entity.ParameterOrderEntity;
import com.blazebit.persistence.testsuite.entity.RecursiveEntity;
import com.blazebit.persistence.testsuite.entity.TestAdvancedCTE1;
import com.blazebit.persistence.testsuite.entity.TestAdvancedCTE2;
import com.blazebit.persistence.testsuite.entity.TestCTE;
import com.blazebit.persistence.testsuite.tx.TxVoidWork;
import com.pallasathenagroup.querydsl.impl.CriteriaBuilderImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.function.Consumer;

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
        public void testFluent() {
            doInJPA(entityManager -> {
                CriteriaBuilderImpl<Book> criteriaBuilder = new CriteriaBuilderImpl<>(new BlazeJPAQuery<>(entityManager, cbf));

                String queryString = criteriaBuilder.select(book).from(book).where(book.id.gt(1337L))
                        .union()
                        .select(book).from(book).where(book.id.gt(1337L))
                        .endSet()
                        .getQueryString();

                System.out.println(queryString);
            });
        }

    }

}
