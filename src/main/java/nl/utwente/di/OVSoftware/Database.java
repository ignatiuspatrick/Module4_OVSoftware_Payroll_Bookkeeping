package nl.utwente.di.OVSoftware;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Database {

	private static String retr = "SELECT h.res_id, h.fullname, r.purchaseprice, r.vandatum, r.totdatum "
			+ "FROM di08.humres h, di08.employeerates r "
			+ "WHERE h.res_id = r.crdnr AND h.\"freefield 16\" = 'N' "
			+ "ORDER BY h.res_id";
	
	private static String all = "SELECT h.res_id, h.fullname, h.emp_stat "
			+ "FROM di08.humres h "
			+ "WHERE h.\"freefield 16\" = 'N' "
			+ "ORDER BY h.res_id";
	
	private static String specpr(int crdnr) { 
		return "SELECT r.purchaseprice, r.vandatum, r.totdatum "
		+ "FROM di08.employeerates r, di08.humres h "
		+ "WHERE r.crdnr = h.res_id "
		+ "AND r.crdnr = " + crdnr;
	}
	
	private static String search(int crdnr, String fullname) {
		return "ALTER TABLE di08.humres ADD COLUMN employees tsvector;"
			+ "UPDATE di08.humres"
			+ "SET employees = to_tsvector(english, coalesce(res_id, \")||''|| coalesce(fullname, \")));"
			+ "CREATE INDEX employees ON di08.humres"
			+ "USING GIN(employees);"
			+ "SELECT h.res_id, h.fullname, h.emp_stat, ts_rank(employee, query) AS rank"
			+ "FROM di08.humres h, to_tsquery('" + crdnr + "&" + fullname + "') query"
			+ "WHERE employees @@ query"
			+ "ORDER BY rank DESC;";
	}
	
	public static List<Employee> getEmployees(String query) {
		ResultSet res = getData("", query);
		List<Employee> l = new ArrayList<>();
		try {
			while(!res.isLast()) {
				res.next();
				l.add(new Employee(res.getInt(1), res.getString(2),res.getString(3)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return l;
	}
	
	public static List<Employee> allEmployees(){
		return getEmployees(all);
	}
	
	public static List<Payrates> getPayratesSpecificEmployee(int crdnr){
		ResultSet res = getData("", Database.specpr(crdnr));
		List<Payrates> l = new ArrayList<>();
		try {
			while(!res.isLast()) {
				res.next();
				l.add(new Payrates(res.getDouble(1), res.getString(2), res.getString(3)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return l;
	}
	
	
	public static List<Employee> searchEmployees(int crdnr, String fullname) {
		ResultSet res = getData("", Database.search(crdnr, fullname));
		List<Employee> l = new ArrayList<>();
		try {
			while(!res.isLast()) {
				res.next();
				l.add(new Employee(res.getInt(1), res.getString(2),res.getString(3)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return l;
		
	}

	public static ResultSet getData(String db, String query){
		try {
			Class.forName("org.postgresql.Driver");
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		String url = "jdbc:postgresql://farm03.ewi.utwente.nl:7016/docker";
		try {
			Connection conn = DriverManager.getConnection(url, "docker", "YkOkimczn");
			Statement statement = conn.createStatement();
			ResultSet res = statement.executeQuery(query);
			return res;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
