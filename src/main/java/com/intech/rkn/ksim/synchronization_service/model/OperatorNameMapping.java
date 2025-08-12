package com.intech.rkn.ksim.synchronization_service.model;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Table(schema = "catalogs", name = "operator_name_mappings")
public record OperatorNameMapping(
        @Id
        Integer id,
        String operatorName,
        int operatorId
) {
}