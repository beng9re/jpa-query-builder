package persistence.sql.ddl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryBuilderDDLTest {

    @Test
    @DisplayName("엔티티 어노테이션이 붙지 않은 엔티티 클래스는 예외가 발생한다.")
    void noEntity() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new QueryBuilderDDL<>(NoHasEntity.class));
    }


    @Test
    @DisplayName("요구사항 1 - @id를 가진 create 쿼리 만들기 ")
    void pkHasCreateQuery() {
        //given
        QueryBuilderDDL<PkHasPerson> ddl = new QueryBuilderDDL<>(PkHasPerson.class);

        //when
        String sql = ddl.create();

        //then
        assertThat(sql).isEqualTo("CREATE TABLE PkHasPerson "
                + "(id bigint, "
                + "name varchar(255), "
                + "age integer "
                + "primary key (id)"
                + ")"
        );
    }

    @Test
    @DisplayName("요구사항 2 - 칼럼이름이 변경되는 create 쿼리 만들어보기")
    void changColumNameQuery() {
        //given
        QueryBuilderDDL<ChangColumNamePerson> ddl = new QueryBuilderDDL<>(ChangColumNamePerson.class);

        //when
        String sql = ddl.create();

        //then
        assertThat(sql).isEqualTo("CREATE TABLE ChangColumNamePerson "
                + "(id bigint generated by default as identity, "
                + "nick_name varchar(255), "
                + "old integer, "
                + "email varchar(255) not null "
                + "primary key (id))"
        );
    }

    @Test
    @DisplayName("요구사항 3 - @Transient와 @Table 이름이 변경되는 create 쿼리 만들어보기")
    void transientAndTableQuery() {
        //given
        QueryBuilderDDL<Person> ddl = new QueryBuilderDDL<>(Person.class);

        //when
        String sql = ddl.create();

        //then
        assertThat(sql).isEqualTo("CREATE TABLE users "
                + "(id bigint generated by default as identity,"
                + " nick_name varchar(255), old integer,"
                + " email varchar(255) not null"
                + " primary key (id))");

    }

}
