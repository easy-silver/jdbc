package hello.jdbc.repository;

import hello.jdbc.domain.Member;

import java.sql.SQLException;

/**
 * 체크 예외를 다루는 경우 인터페이스에도 선언해주어야 한다.
 * 결국 이 인터페이스는 JDBC 기술에 종속적인 인터페이스가 된다.
 */
public interface MemberRepositoryEx {
    Member save(Member member) throws SQLException;
    Member findById(String memberId) throws SQLException;
    void update(String memberId, int money) throws SQLException;
    void delete(String memberId) throws SQLException;
}
