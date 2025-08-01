package com.intech.rkn.ksim.synchronization_service.repository.impl;

import lombok.RequiredArgsConstructor;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class AbstractRepository {

    private final ITemplateEngine templateEngine;

    protected String getSql(String templateName) {
        return templateEngine.process(templateName, new Context());
    }

    protected static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
