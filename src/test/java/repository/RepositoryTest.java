package repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import database.DatabaseServer;
import database.H2;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;
import jdbc.BaseJdbcTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import persistence.entity.JdbcTemplate;
import persistence.testFixtures.Person;


public class RepositoryTest {

    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;
    private Person person = new Person("이름", 30, "email@odna");
    private Person person2 = new Person("이름2", 32, "email2@odna");
    @BeforeAll
    static void setUp() throws SQLException {
        final DatabaseServer server = new H2();
        server.start();
        jdbcTemplate = new BaseJdbcTemplate(server.getConnection());
    }


    @TestFactory
    @DisplayName("레포지토리를 관리 한다.")
    Stream<DynamicNode> testFactory() {
        final DDLRepository<Person> ddlRepository = new BaseDDLRepository<>(jdbcTemplate, Person.class);
        final CrudRepository<Person> crudRepository = new BaseCrudRepository<>(jdbcTemplate, Person.class);


        return Stream.of(
                dynamicContainer("테이블이", Stream.of(
                        dynamicTest("존재하지 않으면 예외가 발생한다.", () -> {
                            assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
                                crudRepository.findAll();
                            });
                        })
                )),
                dynamicContainer("테이블을 생성하면", Stream.of(
                        dynamicTest("테이블을 생성된다.", () -> {
                            assertDoesNotThrow(ddlRepository::createTable);
                        })
                )),
                dynamicContainer("데이터가 존재하지 않으면", Stream.of(
                        dynamicTest("다건 조회시 0건을 반환 한다",() -> {
                            assertThat(crudRepository.findAll()).hasSize(0);
                        }),
                        dynamicTest("단건 조회시 null을 반환 한다",() -> {
                            assertThat(crudRepository.findById(Person.class, -999)).isNull();
                        })
                )),
                dynamicContainer("2건을 저장을 하고.", Stream.of(
                    dynamicTest("저장을 하고", () -> {
                        assertDoesNotThrow(() -> {
                            crudRepository.save(person);
                            crudRepository.save(person2);
                        });
                    }),
                    dynamicContainer("전체를 조회하면", Stream.of(
                        dynamicTest("2건이 조회된다.", () -> {
                            //when
                            final List<Person> persons = crudRepository.findAll();

                            //then
                            assertSoftly((it) -> {
                                it.assertThat(persons).hasSize(2);
                                it.assertThat(persons.get(0).getName()).isEqualTo(person.getName());
                                it.assertThat(persons.get(0).getAge()).isEqualTo(person.getAge());
                                it.assertThat(persons.get(0).getEmail()).isEqualTo(person.getEmail());
                                it.assertThat(persons.get(1).getName()).isEqualTo(person2.getName());
                                it.assertThat(persons.get(1).getAge()).isEqualTo(person2.getAge());
                                it.assertThat(persons.get(1).getEmail()).isEqualTo(person2.getEmail());
                            });
                        }))
                    )
                )),
                dynamicContainer("전체를 조회 하고 조회한 결과의 아이디로 조회하면", Stream.of(
                        dynamicTest("해당건을 조회한다.", () -> {
                            //given
                            final List<Person> persons = crudRepository.findAll();
                            final Long id = persons.get(0).getId();

                            //when
                            final Person result = crudRepository.findById(Person.class, id);

                            //then
                            assertSoftly((it) -> {
                                it.assertThat(persons.get(0).getName()).isEqualTo(result.getName());
                                it.assertThat(persons.get(0).getAge()).isEqualTo(result.getAge());
                                it.assertThat(persons.get(0).getEmail()).isEqualTo(result.getEmail());
                            });
                        }))
                ),
                dynamicContainer("전체를 조회 하고 조회한 결과의 아이디로 삭제하면", Stream.of(
                        dynamicTest("해당건을 삭제한다.", () -> {
                            //given
                            final List<Person> persons = crudRepository.findAll();

                            //when
                            crudRepository.delete(persons.get(0));

                            //then
                            assertThat(crudRepository.findAll()).hasSize(1);
                    }))
                ),
                dynamicTest("테이블이 삭제된다", () ->
                    assertDoesNotThrow(ddlRepository::dropTable)
                )

        );
    }

}
