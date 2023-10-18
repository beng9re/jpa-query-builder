package persistence.meta;

import jakarta.persistence.Transient;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import persistence.exception.FiledEmptyException;

public class EntityColumns {
    private final List<EntityColumn> entityColumns;

    public EntityColumns(Field[] fields) {
        if (fields == null) {
            throw new FiledEmptyException();
        }
        this.entityColumns = extractColumns(fields);
    }

    private List<EntityColumn> extractColumns(Field[] fields) {
        return Arrays.stream(fields)
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .map(EntityColumn::new)
                .collect(Collectors.toList());
    }


    public List<EntityColumn> getEntityColumns() {
        return Collections.unmodifiableList(entityColumns);
    }


}
