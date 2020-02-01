@QueryEntities({
        BlobEntity.class,
        BookEntity.class,
        BookISBNReferenceEntity.class,
        DeletePersonCTE.class,
        Document.class,
        DocumentForEntityKeyMaps.class,
        DocumentForOneToOne.class,
        DocumentForOneToOneJoinTable.class,
        DocumentForSimpleOneToOne.class,
        DocumentInfo.class,
        DocumentInfoSimple.class,
        DocumentNodeCTE.class,
        DocumentTupleEntity.class,
        DocumentType.class,
        DocumentWithNullableName.class,
        EmbeddableTestEntity.class,
        EmbeddableTestEntityContainer.class,
        EmbeddableTestEntitySub.class,
        EmbeddedDocumentTupleEntity.class,
        EmbeddedDocumentTupleEntityId.class,
        IdClassEntity.class,
        IdHolderCte.class,
        IndexedNode.class,
        IndexedNode2.class,
        IntIdEntity.class,
        JuniorProjectLeader.class,
        KeyedNode.class,
        KeyedNode2.class,
        KeysetEntity.class,
        KeysetEntity2.class,
        LargeProject.class,
        LocalizedEntity.class,
        LongSequenceEntity.class,
        NameObject.class,
        NameObjectContainer.class,
        NameObjectContainer2.class,
        NaturalIdJoinTableEntity.class,
        Order.class,
        OrderPosition.class,
        OrderPositionId.class,
        OrderPositionElement.class,
        OrderPositionHead.class,
        IndexedEmbeddable.class,
        KeyedEmbeddable.class,
        EmbeddableTestEntityEmbeddable.class,
        EmbeddedDocumentTupleEntity.class,
        EmbeddableTestEntityNestedEmbeddable.class,
        EmbeddableTestEntityId.class,
        OrderPositionHeadId.class,
        Ownable.class,
        ParameterOrderCte.class,
        ParameterOrderCteB.class,
        ParameterOrderEntity.class,
        Parent.class,
        Person.class,
        PersonCTE.class,
        PersonForEntityKeyMaps.class,
        PolymorphicBase.class,
        PolymorphicPropertyBase.class,
        PolymorphicPropertyMapBase.class,
        PolymorphicPropertySub1.class,
        PolymorphicPropertySub2.class,
        PolymorphicSub1.class,
        PolymorphicSub2.class,
        PrimitiveDocument.class,
        PrimitivePerson.class,
        Project.class,
        ProjectLeader.class,
        // TODO doesn't compile in QueryDSL, file a bug there...
//        RawTypeEntity.class,
        RecursiveEntity.class,
        Root.class,
        Root2.class,
        SchemaEntity.class,
        SecondaryTableEntityBase.class,
        SecondaryTableEntitySub.class,
        SeniorProjectLeader.class,
        SequenceBaseEntity.class,
        SmallProject.class,
        StringIdCTE.class,
        Sub1.class,
        Sub1Sub1.class,
        Sub1Sub2.class,
        Sub2.class,
        Sub2Sub1.class,
        Sub2Sub2.class,
        TestAdvancedCTE1.class,
        TestAdvancedCTE2.class,
        TestCTE.class,
        TestCTEEmbeddable.class,
        TPCBase.class,
        TPCSub1.class,
        TPCSub2.class,
        Version.class,
        Workflow.class
})
package com.pallasathenagroup.querydsl;

import com.blazebit.persistence.testsuite.entity.BlobEntity;
import com.blazebit.persistence.testsuite.entity.BookEntity;
import com.blazebit.persistence.testsuite.entity.BookISBNReferenceEntity;
import com.blazebit.persistence.testsuite.entity.DeletePersonCTE;
import com.blazebit.persistence.testsuite.entity.Document;
import com.blazebit.persistence.testsuite.entity.DocumentForEntityKeyMaps;
import com.blazebit.persistence.testsuite.entity.DocumentForOneToOne;
import com.blazebit.persistence.testsuite.entity.DocumentForOneToOneJoinTable;
import com.blazebit.persistence.testsuite.entity.DocumentForSimpleOneToOne;
import com.blazebit.persistence.testsuite.entity.DocumentInfo;
import com.blazebit.persistence.testsuite.entity.DocumentInfoSimple;
import com.blazebit.persistence.testsuite.entity.DocumentNodeCTE;
import com.blazebit.persistence.testsuite.entity.DocumentTupleEntity;
import com.blazebit.persistence.testsuite.entity.DocumentType;
import com.blazebit.persistence.testsuite.entity.DocumentWithNullableName;
import com.blazebit.persistence.testsuite.entity.EmbeddableTestEntity;
import com.blazebit.persistence.testsuite.entity.EmbeddableTestEntityContainer;
import com.blazebit.persistence.testsuite.entity.EmbeddableTestEntityEmbeddable;
import com.blazebit.persistence.testsuite.entity.EmbeddableTestEntityId;
import com.blazebit.persistence.testsuite.entity.EmbeddableTestEntityNestedEmbeddable;
import com.blazebit.persistence.testsuite.entity.EmbeddableTestEntitySub;
import com.blazebit.persistence.testsuite.entity.EmbeddedDocumentTupleEntity;
import com.blazebit.persistence.testsuite.entity.EmbeddedDocumentTupleEntityId;
import com.blazebit.persistence.testsuite.entity.IdClassEntity;
import com.blazebit.persistence.testsuite.entity.IndexedEmbeddable;
import com.blazebit.persistence.testsuite.entity.IndexedNode;
import com.blazebit.persistence.testsuite.entity.IndexedNode2;
import com.blazebit.persistence.testsuite.entity.IntIdEntity;
import com.blazebit.persistence.testsuite.entity.JuniorProjectLeader;
import com.blazebit.persistence.testsuite.entity.KeyedEmbeddable;
import com.blazebit.persistence.testsuite.entity.KeyedNode;
import com.blazebit.persistence.testsuite.entity.KeyedNode2;
import com.blazebit.persistence.testsuite.entity.KeysetEntity;
import com.blazebit.persistence.testsuite.entity.KeysetEntity2;
import com.blazebit.persistence.testsuite.entity.LargeProject;
import com.blazebit.persistence.testsuite.entity.LocalizedEntity;
import com.blazebit.persistence.testsuite.entity.LongSequenceEntity;
import com.blazebit.persistence.testsuite.entity.NameObject;
import com.blazebit.persistence.testsuite.entity.NameObjectContainer;
import com.blazebit.persistence.testsuite.entity.NameObjectContainer2;
import com.blazebit.persistence.testsuite.entity.NaturalIdJoinTableEntity;
import com.blazebit.persistence.testsuite.entity.Order;
import com.blazebit.persistence.testsuite.entity.OrderPosition;
import com.blazebit.persistence.testsuite.entity.OrderPositionElement;
import com.blazebit.persistence.testsuite.entity.OrderPositionHead;
import com.blazebit.persistence.testsuite.entity.OrderPositionHeadId;
import com.blazebit.persistence.testsuite.entity.OrderPositionId;
import com.blazebit.persistence.testsuite.entity.Ownable;
import com.blazebit.persistence.testsuite.entity.ParameterOrderCte;
import com.blazebit.persistence.testsuite.entity.ParameterOrderCteB;
import com.blazebit.persistence.testsuite.entity.ParameterOrderEntity;
import com.blazebit.persistence.testsuite.entity.Parent;
import com.blazebit.persistence.testsuite.entity.Person;
import com.blazebit.persistence.testsuite.entity.PersonCTE;
import com.blazebit.persistence.testsuite.entity.PersonForEntityKeyMaps;
import com.blazebit.persistence.testsuite.entity.PolymorphicBase;
import com.blazebit.persistence.testsuite.entity.PolymorphicPropertyBase;
import com.blazebit.persistence.testsuite.entity.PolymorphicPropertyMapBase;
import com.blazebit.persistence.testsuite.entity.PolymorphicPropertySub1;
import com.blazebit.persistence.testsuite.entity.PolymorphicPropertySub2;
import com.blazebit.persistence.testsuite.entity.PolymorphicSub1;
import com.blazebit.persistence.testsuite.entity.PolymorphicSub2;
import com.blazebit.persistence.testsuite.entity.PrimitiveDocument;
import com.blazebit.persistence.testsuite.entity.PrimitivePerson;
import com.blazebit.persistence.testsuite.entity.Project;
import com.blazebit.persistence.testsuite.entity.ProjectLeader;
import com.blazebit.persistence.testsuite.entity.RecursiveEntity;
import com.blazebit.persistence.testsuite.entity.Root;
import com.blazebit.persistence.testsuite.entity.Root2;
import com.blazebit.persistence.testsuite.entity.SchemaEntity;
import com.blazebit.persistence.testsuite.entity.SecondaryTableEntityBase;
import com.blazebit.persistence.testsuite.entity.SecondaryTableEntitySub;
import com.blazebit.persistence.testsuite.entity.SeniorProjectLeader;
import com.blazebit.persistence.testsuite.entity.SequenceBaseEntity;
import com.blazebit.persistence.testsuite.entity.SmallProject;
import com.blazebit.persistence.testsuite.entity.StringIdCTE;
import com.blazebit.persistence.testsuite.entity.Sub1;
import com.blazebit.persistence.testsuite.entity.Sub1Sub1;
import com.blazebit.persistence.testsuite.entity.Sub1Sub2;
import com.blazebit.persistence.testsuite.entity.Sub2;
import com.blazebit.persistence.testsuite.entity.Sub2Sub1;
import com.blazebit.persistence.testsuite.entity.Sub2Sub2;
import com.blazebit.persistence.testsuite.entity.TPCBase;
import com.blazebit.persistence.testsuite.entity.TPCSub1;
import com.blazebit.persistence.testsuite.entity.TPCSub2;
import com.blazebit.persistence.testsuite.entity.TestAdvancedCTE1;
import com.blazebit.persistence.testsuite.entity.TestAdvancedCTE2;
import com.blazebit.persistence.testsuite.entity.TestCTE;
import com.blazebit.persistence.testsuite.entity.TestCTEEmbeddable;
import com.blazebit.persistence.testsuite.entity.Version;
import com.blazebit.persistence.testsuite.entity.Workflow;
import com.querydsl.core.annotations.QueryEntities;