package persistence.sql.ddl;

import jakarta.persistence.Transient;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import persistence.exception.FiledEmptyException;

public class EntityColumns {
    private final List<EntityColumn> entityColumns;
    private final Direct direct;

    public EntityColumns(Field[] fields) {
        if (fields == null) {
            throw new FiledEmptyException();
        }
        this.direct = new H2Direct();
        this.entityColumns = extractColumns(fields);

    }

    public EntityColumns(Field[] fields, Direct direct) {
        this.direct = direct;
        this.entityColumns = extractColumns(fields);

    }

    private List<EntityColumn> extractColumns(Field[] fields) {
        return Arrays.stream(fields)
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .map((field) -> new EntityColumn(field, direct))
                .collect(Collectors.toList());
    }

    public String createColumnsQuery() {
        return createColumnsQuery(entityColumns) + " " + direct.primaryKey(
                createPrimaryKeyConstantsQuery(entityColumns));
    }

    private String createColumnsQuery(List<EntityColumn> columns) {
        return columns
                .stream()
                .map(EntityColumn::createColumQuery)
                .collect(Collectors.joining(", ", "", ","));

    }

    private String createPrimaryKeyConstantsQuery(List<EntityColumn> columns) {
        return columns.stream()
                        .filter(EntityColumn::isPk)
                        .map(EntityColumn::getName)
                        .collect(Collectors.joining(", "));
    }
}
