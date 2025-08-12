package com.intech.rkn.ksim.synchronization_service.repository;

import lombok.RequiredArgsConstructor;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public abstract class AbstractRepository {

    private final ITemplateEngine templateEngine;

    protected String getSql(String templateName) {
        return templateEngine.process(templateName, new Context());
    }
}
