package hello.jdbc.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

public class CheckedAppTest {

    @Test
    void checked() {
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(controller::request)
                //하위 Exception까지 다 확인됨
                .isInstanceOf(Exception.class);
    }


    static class Controller {
        Service service = new Service();

        //체크 예외의 문제는 이처럼 컨트롤러에서 SQLException을 의존하는 것이다. 컨트롤러가 구체적인 JDBC 기술에 의존하는 상태가 된다.
        //이처럼 불필요한 의존관계 문제가 발생하게 된다.
        public void request() throws SQLException, ConnectException {
            service.logic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() throws SQLException, ConnectException {
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient {
        public void call() throws ConnectException {
            throw new ConnectException("연결 실패");
        }
    }

    static class Repository {
        public void call() throws SQLException {
            throw new SQLException("ex");
        }
    }

}
