package com.neb.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ProjectStatusConverter
        implements AttributeConverter<ProjectStatus, String> {

    @Override
    public String convertToDatabaseColumn(ProjectStatus status) {
        if (status == null) return null;
        return status.name().toLowerCase(); // PLANNED → planned
    }

    @Override
    public ProjectStatus convertToEntityAttribute(String dbValue) {
        if (dbValue == null) return null;
        return ProjectStatus.valueOf(dbValue.toUpperCase()); // planned → PLANNED
    }
}
