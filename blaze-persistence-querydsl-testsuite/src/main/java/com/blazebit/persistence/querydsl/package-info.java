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
        IdHolderCTE.class,
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
package com.blazebit.persistence.querydsl;

import com.blazebit.persistence.testsuite.entity.*;
import com.querydsl.core.annotations.QueryEntities;