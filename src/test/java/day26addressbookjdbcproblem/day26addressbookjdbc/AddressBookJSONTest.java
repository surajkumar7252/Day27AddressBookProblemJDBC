package day26addressbookjdbcproblem.day26addressbookjdbc;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class AddressBookJSONTest {
	public static final Logger log = LogManager.getLogger(AddressBookJSONTest.class);
	@Test
	public void contactDataWhenGivenInJsonServer_whenRetrieved_shouldMatchTheCount() {
		ContactDetails[] arrayOfContacts=getAddressBookList();
		AddressBookJDBCService addressBookService;
		addressBookService=new AddressBookJDBCService(Arrays.asList(arrayOfContacts));
		long entries=addressBookService.listOfContactDetails.size();
		Assert.assertEquals(6, entries);
	}

	public ContactDetails[] getAddressBookList() {

        Response response =RestAssured.get("/addressbook");
        log.info("Employee Payroll Entries in JsonServer: \n"+response.asString());
        ContactDetails[] arrayOfEmps=new Gson().fromJson(response.asString(),ContactDetails[].class);
        return arrayOfEmps;
	}
	
    public Response addContactToJsonServer(ContactDetails contactDetails) {
		String empJson=new Gson().toJson(contactDetails);
		RequestSpecification request=RestAssured.given();
		request.header("Content-Type","application/json");
		request.body(empJson);
		return request.post("/addressbook");
	}
  

    @Test
    public void contactDetailsWhenGivenInJsonServer_whenAdded_shouldMatchResponseAndCount() throws AddressBookException, SQLException
    {
    	AddressBookJDBCService addressBookService;
    	ContactDetails[] arrayOfContacts = getAddressBookList();
    	addressBookService=new AddressBookJDBCService(Arrays.asList(arrayOfContacts));
    	LocalDate startDate=LocalDate.of(2017, 1, 13);
    	ContactDetails contactDetails=new ContactDetails("Manikant", "Kumar", "CS-3,Hirapur","Friend", "Dhanbad", "Jharkhand",164812);
    	Response response=addContactToJsonServer(contactDetails);
    	int statusCode=response.getStatusCode();
    	Assert.assertEquals(201, statusCode);
    	contactDetails=new Gson().fromJson(response.asString(),ContactDetails.class);
    	addressBookService.addNewContactToJsonServerUsingRestAPI(contactDetails);
    	long entries=addressBookService.listOfContactDetails.size();
    	Assert.assertEquals(5, entries);
    }

}
