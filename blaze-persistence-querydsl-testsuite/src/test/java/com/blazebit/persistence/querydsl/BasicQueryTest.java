/*
 * Copyright 2014 - 2020 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blazebit.persistence.querydsl;

import com.blazebit.persistence.Queryable;
import com.blazebit.persistence.testsuite.AbstractCoreTest;
import com.blazebit.persistence.testsuite.entity.*;
import com.blazebit.persistence.testsuite.tx.TxVoidWork;
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

import static com.blazebit.persistence.testsuite.entity.QDocument.document;
import static com.blazebit.persistence.testsuite.entity.QIdHolderCTE.idHolderCTE;
import static com.blazebit.persistence.testsuite.entity.QPerson.person;
import static com.blazebit.persistence.testsuite.entity.QRecursiveEntity.recursiveEntity;
import static com.blazebit.persistence.querydsl.SetUtils.intersect;
import static com.blazebit.persistence.querydsl.SetUtils.union;
import static com.blazebit.persistence.querydsl.JPQLNextExpressions.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class BasicQueryTest extends AbstractCoreTest {

    @Rule public ExpectedException expectedException = ExpectedException.none();

    @Override
    protected Class<?>[] getEntityClasses() {
        return new Class<?>[] {
                Document.class,
                Person.class,
                IntIdEntity.class,
                Version.class,
                BookEntity.class,
                RecursiveEntity.class,
                TestCTE.class,
                IdHolderCTE.class,
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
            Person person = new Person();
            person.setName("Person");
            Document testEntity = new Document();
            testEntity.setName("bogus");
            testEntity.setOwner(person);
            entityManager.persist(testEntity);
        });
    }

    @Test
    public void testThroughBPVisitor() {
        doInJPA(entityManager -> {
            JPAQuery<Tuple> query = new JPAQuery<Document>(entityManager).from(document)
                    .select(document.name.as("blep"), document.name.substring(2))
                    .where(document.name.length().gt(1));

            BlazeCriteriaBuilderRenderer<Tuple> blazeCriteriaBuilderRenderer = new BlazeCriteriaBuilderRenderer<>(cbf, entityManager, JPQLTemplates.DEFAULT);
            Queryable<Tuple, ?> queryable = blazeCriteriaBuilderRenderer.render(query);
            List<Tuple> fetch = queryable.getResultList();
            assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testThroughBlazeJPAQuery() {
        doInJPA(entityManager -> {
            BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<Tuple>(entityManager, cbf).from(document)
                    .select(document.name.as("blep"), document.name.substring(2))
                    .where(document.name.length().gt(1));

            List<Tuple> fetch = query.fetch();
            assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testParameterExpression() {
        doInJPA(entityManager -> {
            Param<Integer> param = new Param<>(Integer.class, "theSuperName");

            BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<Document>(entityManager, cbf).from(document)
                    .select(document.name.as("blep"), document.name.substring(2))
                    .where(document.name.length().gt(param))
                    .set(param, 1);

            List<Tuple> fetch = query.fetch();
            assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testParameterExpressionInSelect() {
        doInJPA(entityManager -> {
            Param<Integer> param = new Param<>(Integer.class, "theSuperName");

            BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<Document>(entityManager, cbf).from(document)
                    .select(document.name.as("blep"), param)
                    .where(document.name.length().gt(param))
                    .set(param, 1);

            List<Tuple> fetch = query.fetch();
            assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testSubQuery() {
        doInJPA(entityManager -> {
            QDocument sub = new QDocument("sub");
            BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<Document>(entityManager, cbf).from(document)
                    .select(document.name.as("blep"), document.name.substring(2))
                    .where(document.id.in(select(sub.id).from(sub)));

            List<Tuple> fetch = query.fetch();
            assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testWindowFunction() {
        doInJPA(entityManager -> {
            QDocument sub = new QDocument("sub");
            BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<Document>(entityManager, cbf).from(document)
                    .select(document.name.as("blep"), rowNumber(), lastValue(document.name).over().partitionBy(document.id))
                    .where(document.id.in(select(sub.id).from(sub)));

            List<Tuple> fetch = query.fetch();
            assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testNamedWindowFunction() {
        doInJPA(entityManager -> {
            QDocument sub = new QDocument("sub");

            NamedWindow blep = new NamedWindow("whihi").partitionBy(document.id);

            BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<Document>(entityManager, cbf).from(document)
                    .window(blep)
                    .select(document.name.as("blep"), rowNumber().over(blep), lastValue(document.name).over(blep))
                    .where(document.id.in(select(sub.id).from(sub)));

            List<Tuple> fetch = query.fetch();
            assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testNestedSubQuery() {
        doInJPA(entityManager -> {
            QDocument sub = new QDocument("sub");
            QDocument sub2 = new QDocument("sub2");
            BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<Document>(entityManager, cbf).from(document)
                    .select(document.name.as("blep"), document.name.substring(2))
                    .where(document.id.in(select(sub.id).from(sub).where(sub.id.in(select(sub2.id).from(sub2).where(sub2.id.eq(sub.id)))).limit(5)));

            List<Tuple> fetch = query.fetch();
            assertFalse(fetch.isEmpty());
        });
    }

    @Test
    public void testTransformBlazeJPAQuery() {
        doInJPA(entityManager -> {
            Map<Long, String> blep = new BlazeJPAQuery<Document>(entityManager, cbf).from(document)
                    .where(document.name.length().gt(1))
                    .groupBy(document.id, document.name)
                    .transform(GroupBy.groupBy(document.id).as(document.name));

            document.getRoot();
            assertFalse(blep.isEmpty());
        });
    }

    @Test
    public void testAssociationJoin() {
        doInJPA(entityManager -> {
            Map<Person, List<Document>> booksByAuthor = new BlazeJPAQuery<Document>(entityManager, cbf)
                    .from(person)
                    .innerJoin(person.ownedDocuments, document)
                    .transform(GroupBy.groupBy(person).as(GroupBy.list(document)));

            assertNotNull(booksByAuthor);
        });
    }

    @Test
    public void testEntityJoin() {
        doInJPA(entityManager -> {
            QPerson otherAuthor = new QPerson("otherAuthor");
            QDocument otherBook = new QDocument("otherBook");
            Map<Person, List<Document>> booksByAuthor = new BlazeJPAQuery<Document>(entityManager, cbf)
                    .from(otherAuthor)
                    .innerJoin(otherBook).on(otherBook.owner.eq(otherAuthor))
                    .transform(GroupBy.groupBy(otherAuthor).as(GroupBy.list(otherBook)));

            assertNotNull(booksByAuthor);
        });
    }


    @Test
    public void testFromValues() {
        doInJPA(entityManager -> {
            Document theBook = new Document();
            theBook.setId(1337L);
            theBook.setName("test");

            List<Document> fetch = new BlazeJPAQuery<Document>(entityManager, cbf)
                    .fromValues(document, Collections.singleton(theBook))
                    .select(document)
                    .fetch();

            assertNotNull(fetch);
        });
    }

    @Test
    public void testFromValuesAttributes() {
        doInJPA(entityManager -> {
            StringPath bookName = Expressions.stringPath("bookName");

            List<String> fetch = new BlazeJPAQuery<Document>(entityManager, cbf)
                    .fromValues(document.name, bookName, Collections.singleton("book"))
                    .select(bookName)
                    .fetch();

            assertNotNull(fetch);
        });
    }

    @Test
    public void  testComplexUnion() {
        doInJPA(entityManager -> {
            Person person = new Person();
            person.setName("Person");
            entityManager.persist(person);

            Document theBook = new Document();
            theBook.setId(1337L);
            theBook.setName("test");
            theBook.setOwner(person);
            entityManager.merge(theBook);

            Document theSequel = new Document();
            theSequel.setId(42L);
            theSequel.setName("test2");
            theSequel.setOwner(person);
            entityManager.merge(theSequel);
        });

        doInJPA(entityManager -> {
            List<Document> fetch = new BlazeJPAQuery<Document>(entityManager, cbf)
                    .union(
                        new BlazeJPAQuery<Document>().intersect(
                                select(document).from(document).where(document.id.eq(41L)),
                                new BlazeJPAQuery<Document>().except(
                                        select(document).from(document).where(document.id.eq(42L)),
                                        select(document).from(document).where(document.id.eq(43L)))),
                        select(document).from(document).where(document.id.eq(46L)))
                    .fetch();

            assertNotNull(fetch);
        });
    }

    @Test
    public void testComplexSubqueryUnion() {
        doInJPA(entityManager -> {
            Person person = new Person();
            person.setName("Person");
            entityManager.persist(person);

            Document theBook = new Document();
            theBook.setId(1337L);
            theBook.setName("test");
            theBook.setOwner(person);
            entityManager.merge(theBook);

            Document theSequel = new Document();
            theSequel.setId(42L);
            theSequel.setName("test2");
            theSequel.setOwner(person);
            entityManager.merge(theSequel);
        });

        doInJPA(entityManager -> {
            SetExpression<Long> union = new BlazeJPAQuery<Long>(entityManager, cbf)
                    .union(select(document.id).from(document).where(document.id.eq(1337L)),
                            new BlazeJPAQuery<Long>().intersect(
                                    select(document.id).from(document).where(document.id.eq(41L)),
                                    new BlazeJPAQuery<Long>().except(
                                            select(document.id).from(document).where(document.id.eq(42L)),
                                            select(document.id).from(document).where(document.id.eq(43L)))),
                            select(document.id).from(document).where(document.id.eq(46L))
                    );

            QDocument book2 = new QDocument("secondBook");

            List<Document> fetch = new BlazeJPAQuery<Document>(entityManager, cbf)
                    .select(book2)
                    .from(book2).where(book2.id.in(union))
                    .fetch();

            assertNotNull(fetch);
        });
    }

    @Test
    public void testCTE() {
        doInJPA(entityManager -> {
            List<Long> fetch = new BlazeJPAQuery<Document>(entityManager, cbf)
                    .with(idHolderCTE, idHolderCTE.id).as(select(document.id).from(document))
                    .select(idHolderCTE.id).from(idHolderCTE)
                    .fetch();

            assertNotNull(fetch);
        });
    }

    @Test
    public void testCTEWithBinds() {
        doInJPA(entityManager -> {
            List<Long> fetch = new BlazeJPAQuery<Document>(entityManager, cbf)
                    .with(idHolderCTE, select(new Binds<IdHolderCTE>().bind(idHolderCTE.id, document.id)).from(document))
                    .select(idHolderCTE.id).from(idHolderCTE)
                    .fetch();

            assertNotNull(fetch);
        });
    }

    @Test
    public void testCTEWithBinds2() {
        doInJPA(entityManager -> {
            List<Long> fetch = new BlazeJPAQuery<Document>(entityManager, cbf)
                .with(idHolderCTE, select(
                        bind(idHolderCTE.id, document.id)).from(document))
                .select(idHolderCTE.id).from(idHolderCTE)
                .fetch();

                assertNotNull(fetch);
        });
    }

    @Test
    public void testCTEUnion() {
        doInJPA(entityManager -> {
            List<Long> fetch = new BlazeJPAQuery<Document>(entityManager, cbf)
                    .with(idHolderCTE, idHolderCTE.id).as(union(select(document.id).from(document), intersect(select(document.id).from(document), select(document.id).from(document))))
                    .select(idHolderCTE.id).from(idHolderCTE)
                    .fetch();

            assertNotNull(fetch);
        });
    }



    @Test
    public void testCTEFromValues() {
        doInJPA(entityManager -> {
            Document theBook = new Document();
            theBook.setId(1337L);
            theBook.setName("test");

            List<Long> fetch = new BlazeJPAQuery<Document>(entityManager, cbf)
                    .with(idHolderCTE, idHolderCTE.id).as(new BlazeJPAQuery<Document>(entityManager, cbf)
                            .fromValues(document, Collections.singleton(theBook))
                            .select(document.id))
                    .select(idHolderCTE.id).from(idHolderCTE)
                    .fetch();

            assertNotNull(fetch);
        });
    }

    @Test
    public void testRecursiveCTEUnion() {
        doInJPA(entityManager -> {
            List<Long> fetch = new BlazeJPAQuery<Document>(entityManager, cbf)
                    .withRecursive(idHolderCTE, idHolderCTE.id).as(union(select(document.id).from(document).where(document.id.eq(1L)), select(document.id).from(document)
                            .join(idHolderCTE).on(idHolderCTE.id.add(1L).eq(document.id))))
                    .select(idHolderCTE.id).from(idHolderCTE)
                    .fetch();

            assertNotNull(fetch);
        });
    }

    @Test
    public void testRecursiveBindBuilder() {
        doInJPA(entityManager -> {
            List<Long> fetch = new BlazeJPAQuery<Document>(entityManager, cbf)
                    .withRecursive(idHolderCTE, idHolderCTE.id)
                        .as(new BlazeJPAQuery<>().unionAll(
                                select(document.id).from(document).where(document.id.eq(1L)),
                                select(document.id).from(document).join(idHolderCTE).on(idHolderCTE.id.add(1L).eq(document.id))))
                    .select(idHolderCTE.id).from(idHolderCTE)
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
    public void testCteInSubquery() {
        doInJPA(entityManager -> {
            QIdHolderCTE idHolderCTE = QIdHolderCTE.idHolderCTE;

            List<Document> fetch = new BlazeJPAQuery<>(entityManager, cbf)
                    .select(document)
                    .from(document)
                    .innerJoin(selectFrom(idHolderCTE)
                                    .with(idHolderCTE, idHolderCTE.id).as(select(document.id).from(document)),
                            idHolderCTE).on(idHolderCTE.id.eq(document.id))
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
                    .with(idHolderCTE, idHolderCTE.id)
                    .as(select(document.id).from(document).where(document.id.eq(1L)))
                    .select(t, subT2)
                    .from(t)
                    .leftJoin(select(subT).from(t.children, subT).orderBy(subT.id.asc()).limit(1), subT2)
                    .where(t.id.in(select(idHolderCTE.id).from(idHolderCTE)))
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
