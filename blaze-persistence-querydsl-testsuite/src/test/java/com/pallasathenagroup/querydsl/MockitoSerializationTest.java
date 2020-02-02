package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.FinalSetOperationCriteriaBuilder;
import com.blazebit.persistence.LeafOngoingSetOperationCriteriaBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;

import static com.pallasathenagroup.querydsl.QBook.book;
import static com.querydsl.jpa.JPAExpressions.select;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Strict.class)
public class MockitoSerializationTest {

    @Mock
    CriteriaBuilderFactory criteriaBuilderFactory;

    @Mock
    EntityManager entityManager;

    @Mock(answer = Answers.RETURNS_SELF)
    CriteriaBuilder<Book> criteriaBuilder;

    @Mock(answer = Answers.RETURNS_SELF)
    LeafOngoingSetOperationCriteriaBuilder<Book> leafOngoingSetOperationCriteriaBuilder;

    @Mock(answer = Answers.RETURNS_SELF)
    FinalSetOperationCriteriaBuilder<Book> finalSetOperationCTECriteriaBuilder;

    @Test
    public void criteriaBuilderInvocationsTest() {
        String expectedQueryString = "<bogus>";

        when(criteriaBuilderFactory.create(any(), any())).thenReturn((CriteriaBuilder) criteriaBuilder);
        when(criteriaBuilder.getFrom(anyString())).thenReturn(null);
        doReturn(leafOngoingSetOperationCriteriaBuilder).when(criteriaBuilder).union();
        when(leafOngoingSetOperationCriteriaBuilder.getFrom(anyString())).thenReturn(null);
        doReturn(finalSetOperationCTECriteriaBuilder).when(leafOngoingSetOperationCriteriaBuilder).endSet();
        doReturn(expectedQueryString).when(finalSetOperationCTECriteriaBuilder).getQueryString();

        String queryString = new BlazeJPAQuery<TestEntity>(entityManager, criteriaBuilderFactory)
                .union(
                        select(book).from(book).where(book.id.lt(1337L)),
                        select(book).from(book).where(book.id.gt(42L))
                )
                .getQueryString();

        assertEquals(expectedQueryString, queryString);
    }
}
