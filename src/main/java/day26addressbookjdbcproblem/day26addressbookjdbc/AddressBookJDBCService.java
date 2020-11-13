package day26addressbookjdbcproblem.day26addressbookjdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.cj.jdbc.Driver;

import jdk.internal.org.jline.utils.Log;





public class AddressBookJDBCService 
{   public static List<ContactDetails> listOfContactDetails = new ArrayList<ContactDetails>();
    public static List<ContactDetails> listForAddingDetailsUsingThread = new ArrayList<ContactDetails>();
	public static final Logger log = LogManager.getLogger(AddressBookJDBCService.class);
	public Connection connection;
	public ResultSet resultSetOpted;
	public Statement statementOpted;
	public static AddressBookJDBCService newAddressBookJDBCDatabase=new AddressBookJDBCService();
	public AddressBookJDBCService(List<ContactDetails> listOfContactDetails) {
		
		this.listOfContactDetails=new ArrayList<>(listOfContactDetails);
	}

	public AddressBookJDBCService() {
		listOfContactDetails = new ArrayList<ContactDetails>();
	}

	public static void main(String[] args) throws AddressBookException, SQLException {
		
		listOfContactDetails=newAddressBookJDBCDatabase.readContactList();
		newAddressBookJDBCDatabase.addContactToDB("Shubham", "Kumar", "CS-1,Hirapur","Friend", "Dhanbad", "Jharkhand",164512);
	
		newAddressBookJDBCDatabase.updateAddressBookDetails("Munna", "Kumar",  "Mithapur", "Gaya", "Bihar",120012);
		newAddressBookJDBCDatabase.readContactsDetails("Shravan","Kumar");
		LocalDate startDate=LocalDate.of(2017, 1, 13);
		newAddressBookJDBCDatabase.readContactsDetailsInParticularDuration(startDate, LocalDate.now());
		log.info(" Enter the input in the format (c/s,Name_Of_City_or_State) where c=city and s=state.");
		newAddressBookJDBCDatabase.readContactsDetailsCountByCityOrState('c',"Gaya");
		newAddressBookJDBCDatabase.addingMultipleDataUsingThread(listForAddingDetailsUsingThread);
	}
	
	public synchronized  Connection connectingToDatabase() throws AddressBookException {

		String jdbcurl = "jdbc:mysql://127.0.0.1:3306/addressbook_service?useSSL=false";
		String userName = "root";
		String password = "Star@9835";
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

public List<ContactDetails> readContactList() throws AddressBookException, SQLException {
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
public void updateAddressBookDetails(String firstName, String lastName, String address, String city, String state,int zip) throws AddressBookException, SQLException {
	resultSetOpted=null;
	String query=String.format("select ID from  contact FIRST_NAME='%s' and LAST_NAME='%s'", firstName,lastName) ;
	try {
		connection=this.connectingToDatabase();
		connection.setAutoCommit(false);
		statementOpted=connection.createStatement();
		resultSetOpted=statementOpted.executeQuery(query);
		Integer contactId = resultSetOpted.getInt("ID");
		String updateQuery = String.format("update address set ADDRESS_ID ='%s',CITY ='%s', STATE='%s', ZIP=%s where ID=%s;",
				address, city, state, zip, contactId);
		
		statementOpted=connection.createStatement();
		statementOpted.executeQuery(updateQuery);
		
		connection.commit();
	}catch (SQLException e) {
		connection.rollback();
		throw new AddressBookException("Updation Failed.");
	}finally {
		if (connection != null)
			connection.close();
	}
}

public void readContactsDetails(String firstName, String lastName) throws AddressBookException, SQLException {
	resultSetOpted=null;
	String query = String.format("select * from contact join address on contact.ID =address.ADDRESS_ID  where FIRST_NAME='%s' and LAST_NAME='%s'",firstName, lastName);
	try {
		connection=this.connectingToDatabase();
		
		statementOpted=connection.createStatement();
		resultSetOpted=statementOpted.executeQuery(query);
		readContactList();
	} catch (SQLException e) {
		throw new AddressBookException("Retrieve Error");
	}finally {
		if (connection != null)
			connection.close();
	}
}

public void readContactsDetailsInParticularDuration(LocalDate startDate, LocalDate endDate) throws AddressBookException, SQLException {
	resultSetOpted=null;
	String queryToAddField="alter table contact add DATE_ADDED date  after TYPE";
	String query = String.format("select * from contact join address on contact.ID =address.ADDRESS_ID  where START between cast('%s' as date) and cast('%s' as date));",startDate, endDate);
	try {
		connection=this.connectingToDatabase();
		Statement addField=connection.createStatement();
		addField.executeQuery(queryToAddField);
		statementOpted=connection.createStatement();
		resultSetOpted=statementOpted.executeQuery(query);
		while (resultSetOpted.next()) {
			String firstName = resultSetOpted.getString("FIRST_NAME ");
			String lastName = resultSetOpted.getString("LAST_NAME ");
			String address = resultSetOpted.getString("ADDRESS_ID ");
			String city=resultSetOpted.getString("CITY ");
			String state=resultSetOpted.getString("STATE ");
			int zip=resultSetOpted.getInt("ZIP ");
			log.info("First Name: "+ firstName+" Last Name: "+ lastName +" Address : "+ address+" City : "+city+"State :"+state+" Zip : "+zip);
			
		}
		
	} catch (SQLException e) {
		throw new AddressBookException("Retrieve Error");
	}finally {
		if (connection != null)
			connection.close();
	}
}

public void readContactsDetailsCountByCityOrState(char option, String parameter_Name_Feed) throws AddressBookException, SQLException {
	String query=null;
	resultSetOpted=null;
	switch(option) {
	case 'c':query=String.format("select count(CITY) from address join contact on address.ADDRESS_ID=contact.ID where CITY='%s' ",parameter_Name_Feed);
	         break;
	case 's':query=String.format("select count(STATE) from address join contact on address.ADDRESS_ID=contact.ID where STATE='%s' ",parameter_Name_Feed);
             break;
	
	}
	try {
		connection=this.connectingToDatabase();
		statementOpted=connection.createStatement();
		resultSetOpted=statementOpted.executeQuery(query);
		Log.info("Count as per your search is "+ resultSetOpted.getInt(1));
		
	} catch (SQLException e) {
		throw new AddressBookException("Retrieve Error");
	}finally {
		if (connection != null)
			connection.close();
	}
}

public void addContactToDB(String firstName,String lastName,String address,String type, String city, String state,Integer zipCode) throws AddressBookException, SQLException {
    	     ContactDetails contactDetails;
    	     resultSetOpted=null;
    	 String query1 = String.format("insert into contact (FIRST_NAME,LAST_NAME,TYPE) values ('%s','%s','%s')",firstName, lastName,type);
		try {
			connection.setAutoCommit(false);
			connection=this.connectingToDatabase();
			Statement statement1=connection.createStatement();
			resultSetOpted=statement1.executeQuery(query1);
			Integer id=resultSetOpted.getInt("ID");
			
			
			String query2=String.format("insert into address (ADDRESS_ID,ADDRESS,CITY,STATE,ZIP) values ('%s','%s','%s',%s')",id, address,city,state,zipCode);
			Statement statement2=connection.createStatement();
			statement2.executeQuery(query2);
			contactDetails = new ContactDetails( firstName,lastName, address,type,  city, state, zipCode);
			connection.commit();
			
				} catch (SQLException e) {
					connection.rollback();
					throw new AddressBookException("Adding Details Failed");
		}
		finally {
			if (connection != null)
				connection.close();
		}
	}

public void addingMultipleDataUsingThread(List<ContactDetails> contactListToBeAdded) throws AddressBookException, SQLException {
    Map<Integer,Boolean> addressDetailsAddition=new HashMap<Integer,Boolean>();
    contactListToBeAdded.forEach(contactDetails ->{
  		 Runnable task=()->{
  			addressDetailsAddition.put(contactDetails.hashCode(),false); 
              log.info("Details Being Added: "+Thread.currentThread().getName()); 
               try {
					this.addContactToDB(contactDetails.firstName,contactDetails.lastName,contactDetails.address,contactDetails.type,contactDetails.city,contactDetails.state,contactDetails.zip);
				} catch (AddressBookException | SQLException e) {
					
					e.printStackTrace();
				}
               addressDetailsAddition.put(contactDetails.hashCode(),true); 
  		     log.info("Address Book Updated : "+Thread.currentThread().getName());
  		 };
  		 Thread thread=new  Thread(task,contactDetails.firstName);
  	     thread.start();
  	 });
  	 while(addressDetailsAddition.containsValue(false)) {
  		 try {
  			 Thread.sleep(1000);
  		 }catch(InterruptedException e) {
  			 e.printStackTrace();
  		 }
  	 }
     }

public void addNewContactToJsonServerUsingRestAPI(ContactDetails contactDetails) throws AddressBookException, SQLException {
	this.addContactToDB(contactDetails.firstName,contactDetails.lastName,contactDetails.address,contactDetails.type,contactDetails.city, contactDetails.state,contactDetails.zip);
	
}

}
