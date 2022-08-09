package hello.jdbc.exception.translator;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class SpringExceptionTranslatorTest {

    DataSource dataSource;

    @BeforeEach
    void init() {
        dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    }

    @Test
    void sqlExceptionErrorCode() {
        String sql = "select bad grammar";

        try{
            Connection con = dataSource.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.executeQuery();
        } catch (SQLException e) {
            assertThat(e.getErrorCode()).isEqualTo(42122);   //h2 db의 bad grammar 에러 코드
            int errorCode = e.getErrorCode();
            log.info("errorCode={}", errorCode);
            log.info("error", e);
        }
    }

    /**
     * 스프링 예외 추상화 기능 적용
     */
    @Test
    void exceptionTranslator() {
        String sql = "select bad grammar";

        try{
            Connection con = dataSource.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.executeQuery();
        } catch (SQLException e) {
            assertThat(e.getErrorCode()).isEqualTo(42122);   //h2 db의 bad grammar 에러 코드

            //스프링이 제공하는 SQL 예외 변환기(sql-error-codes.xml)
            SQLErrorCodeSQLExceptionTranslator exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);

            //BadSqlGrammarException
            DataAccessException resultEx = exTranslator.translate("select", sql, e);
            log.info("resutEx", resultEx);
            assertThat(resultEx.getClass()).isEqualTo(BadSqlGrammarException.class);
        }
    }
}
