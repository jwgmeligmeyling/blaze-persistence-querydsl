package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.spi.CriteriaBuilderConfiguration;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQuery;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.pallasathenagroup.querydsl.QAuthor.author;
import static com.pallasathenagroup.querydsl.QBook.book;
import static com.pallasathenagroup.querydsl.QTestEntity.testEntity;
import static com.querydsl.jpa.JPAExpressions.select;
import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;

public class BasicQueryTest extends BaseCoreFunctionalTestCase {

    private CriteriaBuilderConfiguration criteriaBuilderConfiguration;
    private CriteriaBuilderFactory criteriaBuilderFactory;

    @Override
    protected Class<?>[] getAnnotatedClasses() {
        return new Class<?>[] { TestEntity.class, Author.class, Book.class, Publication.class, Publisher.class };
    }

    @Before
    public void setUp() {
        criteriaBuilderConfiguration = Criteria.getDefault();
        criteriaBuilderFactory = criteriaBuilderConfiguration.createCriteriaBuilderFactory(this.sessionFactory());

        doInJPA(this::sessionFactory, entityManager -> {
            TestEntity testEntity = new TestEntity();
            testEntity.field = "bogus";
            entityManager.persist(testEntity);
        });
    }

    @Test
    public void testThroughBPVisitor() {
        doInJPA(this::sessionFactory, entityManager -> {
            JPAQuery<Tuple> query = new JPAQuery<TestEntity>(entityManager).from(testEntity)
                    .select(testEntity.field.as("blep"), testEntity.field.substring(2))
                    .where(testEntity.field.length().gt(1));

            BlazeCriteriaVisitor<Tuple> blazeCriteriaVisitor = new BlazeCriteriaVisitor<>(criteriaBuilderFactory, entityManager, JPQLTemplates.DEFAULT);
            blazeCriteriaVisitor.serialize(query.getMetadata(), false, null);
            CriteriaBuilder<Tuple> criteriaBuilder = blazeCriteriaVisitor.getCriteriaBuilder();
            List<Tuple> fetch = criteriaBuilder.getResultList();
            Assert.assertFalse(fetch.isEmpty());
        });
    }


    @Test
    public void testThroughBlazeJPAQuery() {
        doInJPA(this::sessionFactory, entityManager -> {
            BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<TestEntity>(entityManager, criteriaBuilderFactory).from(testEntity)
                    .select(testEntity.field.as("blep"), testEntity.field.substring(2))
                    .where(testEntity.field.length().gt(1));

            List<Tuple> fetch = query.fetch();
            Assert.assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testSubQuery() {
        doInJPA(this::sessionFactory, entityManager -> {
            QTestEntity sub = new QTestEntity("sub");
            BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<TestEntity>(entityManager, criteriaBuilderFactory).from(testEntity)
                    .select(testEntity.field.as("blep"), testEntity.field.substring(2))
                    .where(testEntity.id.in(select(sub.id).from(sub)));

            List<Tuple> fetch = query.fetch();
            Assert.assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testTransformBlazeJPAQuery() {
        doInJPA(this::sessionFactory, entityManager -> {
            Map<Long, String> blep = new BlazeJPAQuery<TestEntity>(entityManager, criteriaBuilderFactory).from(testEntity)
                    .where(testEntity.field.length().gt(1))
                    .groupBy(testEntity.id, testEntity.field)
                    .transform(GroupBy.groupBy(testEntity.id).as(testEntity.field));

            testEntity.getRoot();
            Assert.assertFalse(blep.isEmpty());
        });
    }

    @Test
    public void testAssociationJoin() {
        doInJPA(this::sessionFactory, entityManager -> {
            Map<Author, List<Book>> booksByAuthor = new BlazeJPAQuery<TestEntity>(entityManager, criteriaBuilderFactory)
                    .from(author)
                    .innerJoin(author.books, book)
                    .transform(GroupBy.groupBy(author).as(GroupBy.list(book)));
        });
    }

    @Test
    public void testEntityJoin() {
        doInJPA(this::sessionFactory, entityManager -> {
            QAuthor otherAuthor = new QAuthor("otherAuthor");
            QBook otherBook = new QBook("otherBook");
            Map<Author, List<Book>> booksByAuthor = new BlazeJPAQuery<TestEntity>(entityManager, criteriaBuilderFactory)
                    .from(otherAuthor)
                    .innerJoin(otherBook).on(otherBook.author.eq(otherAuthor))
                    .transform(GroupBy.groupBy(otherAuthor).as(GroupBy.list(otherBook)));
        });
    }


    @Test
    public void testFromValues() {
        doInJPA(this::sessionFactory, entityManager -> {
            Book theBook = new Book();
            theBook.id = 1337l;
            theBook.name = "test";

            List<Book> fetch = new BlazeJPAQuery<TestEntity>(entityManager, criteriaBuilderFactory)
                    .fromValues(book, Collections.singleton(theBook))
                    .select(book)
                    .fetch();

            System.out.println(fetch);
        });
    }
}
