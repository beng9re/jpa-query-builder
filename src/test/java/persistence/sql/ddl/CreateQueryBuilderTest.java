package persistence.sql.ddl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.testFixtures.ChangColumNamePerson;
import persistence.testFixtures.Person;
import persistence.testFixtures.PkHasPerson;
import persistence.vender.dialect.H2Dialect;

@DisplayName("CreateQueryBuilder 테스트")
class CreateQueryBuilderTest {

    @Test
    @DisplayName("요구사항 1 - @id를 가진 create 쿼리 만들기 ")
    void pkHasCreateQuery() {
        //given
        final QueryGenerator<PkHasPerson> query = QueryGenerator.from(PkHasPerson.class);

        //when
        String sql = query.create();

        //then
        assertThat(sql).isEqualTo("CREATE TABLE PkHasPerson "
                + "(id bigint, "
                + "name varchar(255), "
                + "age integer, "
                + "primary key (id)"
                + ")"
        );
    }

    @Test
    @DisplayName("요구사항 2 - 칼럼이름이 변경되는 create 쿼리 만들어보기")
    void changColumNameQuery() {
        //given
        final QueryGenerator<ChangColumNamePerson> query = QueryGenerator.from(ChangColumNamePerson.class);

        //when
        String sql = query.create();

        //then
        assertThat(sql).isEqualTo("CREATE TABLE ChangColumNamePerson "
                + "(id bigint generated by default as identity, "
                + "nick_name varchar(255), "
                + "old integer, "
                + "email varchar(255) not null, "
                + "primary key (id))"
        );
    }

    @Test
    @DisplayName("요구사항 3 - @Transient와 @Table 이름이 변경되는 create 쿼리 만들어보기")
    void transientAndTableQuery() {
        //given
        QueryGenerator<Person> ddl = QueryGenerator.from(Person.class);

        //when
        String sql = ddl.create();

        //then
        assertThat(sql).isEqualTo("CREATE TABLE users "
                + "(id bigint generated by default as identity,"
                + " nick_name varchar(255), old integer,"
                + " email varchar(255) not null,"
                + " primary key (id))");

    }

    @Test
    @DisplayName("Create 쿼리가 방언이 바뀌면 이에 맞게 바뀐다.")
    void dialectChange() {
        //given
        QueryGenerator<Person> ddl = QueryGenerator.of(Person.class, new H2Dialect());

        //when
        String sql = ddl.create();

        //then
        assertThat(sql).isEqualTo("CREATE TABLE users (id BIGINT GENERATED BY DEFAULT AS IDENTITY"
                + ", nick_name VARCHAR(255)"
                + ", old INTEGER, email VARCHAR(255) NOT NULL"
                + ", PRIMARY KEY (ID))");

    }

}
