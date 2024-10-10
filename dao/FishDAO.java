package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dto.FishDTO;

// fishdata table CRUD
public class FishDAO {

	private String username = "system";
	private String password = "11111111";
	private String url = "jdbc:oracle:thin:@localhost:1521:orcl";
	private String driverName = "oracle.jdbc.driver.OracleDriver";
	private Connection conn = null;

	public static FishDAO fishdao = null; // * 싱글톤 기법 >> 자기 자신의 객체 주소 변수, 어디서든 접근(public), 공용변수(static)

	// * 싱글톤 기법 >> 나만 객체를 만들도록 생성자를 private로 설정. 외부에서 객체 만들지 못함
	private FishDAO() {
		init();
	}

	public static FishDAO getInstance() { // * 싱클톤 기법
		if (fishdao == null) {
			fishdao = new FishDAO(); // 현재 객체가 null이면 객체 만들고
		}
		return fishdao; // null 아니면 주소 넘긴다.
	}

	private void init() { // 드라이버 로드, 최초 1회만
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println("오라클 드라이버 로드 성공");
			// 드라이버 로드가 제대로 됐다면(빌드가 제대로 됐다면), 오라클 드라이버 로드 성공 메세지 출력
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private boolean conn() { // 커넥션 가져오는 공통 코드를 메서드로 정의
		try {
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "system", "11111111");
//			System.out.println("커넥션 자원 획득 성공");
			return true; // 커넥션 자원을 정상적으로 획득할 시
		} catch (Exception e) {
		}
		return false; // 획득 실패시
	}

	// 맵핑하려고 매개변수로 FishDTO fdto 가져옴, 리턴 x >> void
	public void add(FishDTO fdto) {
		if (conn()) { // if 가 true : 커넥션 자원을 정상적으로 획득한 것 >> if문 실행
			try {
				// 쿼리문 작성
				String sql = "insert into fishdata values (?,?,default)"; // 물음표가 있으므로 PreparedStatement사용
				PreparedStatement psmt = conn.prepareStatement(sql); // Statement : 물음표 필요 없지만 상대적으로 속도 느림
				// Mapping
				psmt.setString(1, fdto.getId());
				psmt.setString(2, fdto.getPwd());

				/*
				 * 쿼리 실행 >> DB로부터 리턴값은 받지 않는다. 등록할 때, 이런 커리문을 집어넣겠다.라는 의미 psmt.executeUpdate();
				 */
				// 쿼리 실행 >> 리턴을 받아서 다음 처리 작업 정의. 제대로 들어갔는지 확인. commit과 rollback은 트렌젝션처리
				int resultInt = psmt.executeUpdate(); // 결과가 성공한 수로 나오므로
				if (resultInt > 0) {
					conn.commit();
				} else {
					conn.rollback();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally { // conn 자원 반납
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e2) {
				}
			}
		} else {
			System.out.println("데이터베이스 커넥션 실패");
		}
	}

	// 맵핑 필요없어서 매개변수x
	// call한 위치로 리턴하므로 리턴타입은 튜플 여러개 >> ArrayList
	public ArrayList<FishDTO> selectAll() {
		ArrayList<FishDTO> flist = new ArrayList<FishDTO>();
		if (conn()) {
			try {
				String sql = "select * from fishdata";
				PreparedStatement psmt = conn.prepareStatement(sql);
				ResultSet rs = psmt.executeQuery();
				// ResultSet은 테이블 형식으로 가져온다고 이해
				while (rs.next()) { // next() 메서드는 rs에서 참조하는 테이블에서
									// 튜플을 순차적으로 하나씩 접근하는 메서드
					FishDTO fishTemp = new FishDTO();
					// rs.getString("id") >> rs가 접근한 튜플에서 id 컬럼의 값을 가져옴
					fishTemp.setId(rs.getString("id"));
					fishTemp.setPwd(rs.getString("pwd"));
					fishTemp.setIndate(rs.getString("indate"));
					flist.add(fishTemp);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally { // conn 자원 반납
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e2) {

				}
			}
		}
		return flist;
	}

	public FishDTO selectOne(String findId) {
		if (conn()) {
			try {
				String sql = "select * from fishdata where id = ?";
				PreparedStatement psmt = conn.prepareStatement(sql);
				psmt.setString(1, findId);
				ResultSet rs = psmt.executeQuery();
				if (rs.next()) { // 쿼리 결과가 튜플 하나일 경우는 이렇게 해도 됨
									// while도 가능함
					FishDTO fishTemp = new FishDTO();
					// rs.getString("id") >> rs가 접근한 튜플에서 id 컬럼의 값을 가져옴
					fishTemp.setId(rs.getString("id"));
					fishTemp.setPwd(rs.getString("pwd"));
					fishTemp.setIndate(rs.getString("indate"));
					return fishTemp;
				}
			} catch (Exception e) {
			} finally { // conn 자원 반납
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e2) {

				}
			}
		}
		return null;
	}

	public void delete(String delId) {
		if (conn()) {
			try {
				String sql = "delete from fishdata where id=?";
				PreparedStatement psmt = conn.prepareStatement(sql);
				psmt.setString(1, delId);
				psmt.executeUpdate();
			} catch (Exception e) {
			} finally { // conn 자원 반납
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e2) {
				}
			}
		}
	}

	public void update(FishDTO fdto) {
		if (conn()) {
			try {
				String sql = "update fishdata set pwd=? where id=? ";
				PreparedStatement psmt = conn.prepareStatement(sql);
				psmt.setString(1, fdto.getPwd());
				psmt.setString(2, fdto.getId());
				psmt.executeUpdate();
			} catch (Exception e) {
			} finally { // conn 자원 반납
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e2) {
				}
			}
		}
	}

}
