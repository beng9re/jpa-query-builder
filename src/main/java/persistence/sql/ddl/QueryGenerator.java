package persistence.sql.ddl;

import persistence.dialect.Dialect;
import persistence.exception.NoEntityException;
import persistence.meta.EntityMeta;
import persistence.vender.dialect.H2Dialect;

public class QueryGenerator<T> {
    private final static Dialect DEFAULT_DIALECT = new H2Dialect();
    private final EntityMeta entityTable;
    private final Dialect dialect;

    private QueryGenerator(Class<T> entityClass, Dialect dialect) {
        if (entityClass == null) {
            throw new NoEntityException();
        }
        this.entityTable = new EntityMeta(entityClass);
        this.dialect = dialect;
    }
    private QueryGenerator(Class<T> entityClass) {
        this(entityClass, DEFAULT_DIALECT);
    }

    public static <T> QueryGenerator<T> from(Class<T> entityClass) {
        return new QueryGenerator<>(entityClass);
    }
    public static <T> QueryGenerator<T> of(Class<T> entityClass, Dialect dialect) {
        return new QueryGenerator<>(entityClass, dialect);
    }

    public String create() {
        return new CreateQueryBuilder<>(entityTable, dialect).create();
    }

    public String drop() {
        return new DropQueryBuilder<>(entityTable, dialect).drop();
    }
}