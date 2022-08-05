package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String formId, String toId, int money) throws SQLException {
        //DataSource를 통해 커넥션을 받아온다.
        Connection con = dataSource.getConnection();
        try {
            //자동 커밋 모드를 해제하면 트랜잭션 시작
            con.setAutoCommit(false);
            //비즈니스 로직
            bizLogic(con, formId, toId, money);
            con.commit();   //성공 시 커밋

        } catch (Exception e) {
            con.rollback(); //실패 시 롤백
            throw new IllegalStateException(e);

        }finally {
            release(con);
        }
    }

    private void bizLogic(Connection con, String formId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con, formId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromMember.getMemberId(), fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con, toMember.getMemberId(), toMember.getMoney() + money);
    }

    private void release(Connection con) {
        if (con != null) {
            try {
                //커넥션을 풀에 반환하기 전에 커밋모드를 오토로 변경하여 반환한다.
                con.setAutoCommit(true);
                con.close();
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생!!!");
        }
    }
}
