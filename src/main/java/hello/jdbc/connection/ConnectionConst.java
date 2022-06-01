package hello.jdbc.connection;

/**
 * 상수 모음이므로 객체 생성되지 않도록 abstract 키워드 적용
 */
public abstract class ConnectionConst {
    public static final String URL = "jdbc:h2:tcp://localhost/~/test";
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "";
}
