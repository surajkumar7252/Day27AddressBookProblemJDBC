package day26addressbookjdbcproblem.day26addressbookjdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.cj.jdbc.Driver;




public class AddressBookJDBCService 
{    public static List<ContactDetails> listOfContactDetails = new ArrayList<ContactDetails>();
	public static final Logger log = LogManager.getLogger(AddressBookJDBCService.class);
	public Connection connection;
	public ResultSet resultSetOpted;
	public Statement statementOpted;
	
	public static void main(String[] args) throws AddressBookException, SQLException {
		AddressBookJDBCService newAddressBookJDBCDatabase=new AddressBookJDBCService();
		listOfContactDetails=newAddressBookJDBCDatabase.readContactList();
	}
	
	public Connection connectingToDatabase() throws AddressBookException {

		String jdbcurl = "jdbc:mysql://127.0.0.1:3306/addressbook_service?useSSL=false";
		String userName = "root";
		String password = "HeyBro@1234";
		Connection connection;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			log.info("Drivers Loaded");

		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Can't find the driver in the class path.");
		}
		listDrivers();
		try {
			log.info("Connecting to database: " + jdbcurl);
			connection = DriverManager.getConnection(jdbcurl, userName, password);
			log.info("Connection is successful ");
			return connection;

		} catch (SQLException e) {
			throw new AddressBookException("Connection failed");

		}
	}
	private static void listDrivers() {
		Enumeration<java.sql.Driver> driverList=DriverManager.getDrivers();
		while(driverList.hasMoreElements()) {
			Driver driverClass=(Driver) driverList.nextElement();
			log.info("  "+ driverClass.getClass().getName());
			
		}
		
	}

private List<ContactDetails> readContactList() throws AddressBookException, SQLException {
	List<ContactDetails> contactDetailsList = new ArrayList<ContactDetails>();
	String query="select * from address inner join contact on address.ADDRESS_ID=contact.ID where CITY='Dhanbad' ";
	try {
		connection=this.connectingToDatabase();
		statementOpted=connection.createStatement();
		resultSetOpted=statementOpted.executeQuery(query);
		while (resultSetOpted.next()) {
			String firstName = resultSetOpted.getString("FIRST_NAME ");
			String lastName = resultSetOpted.getString("LAST_NAME ");
			String address = resultSetOpted.getString("ADDRESS_ID ");
			String city=resultSetOpted.getString("CITY ");
			String state=resultSetOpted.getString("STATE ");
			int zip=resultSetOpted.getInt("ZIP ");
			contactDetailsList.add(new ContactDetails(firstName, lastName, address, city, state, zip));
		}
		return contactDetailsList;
	} catch (SQLException e) {
		throw new AddressBookException("Reading From resultset error.");
	}finally {
		if (connection != null)
			connection.close();
	}
}

}