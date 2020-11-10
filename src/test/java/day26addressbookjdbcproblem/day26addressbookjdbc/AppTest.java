package day26addressbookjdbcproblem.day26addressbookjdbc;

import java.sql.SQLException;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
public class AppTest 
{    List<ContactDetails> listOfContacts; 
      AddressBookJDBCService addressBookService=new AddressBookJDBCService();
   
    @Test
    public void contactToAddressBookWhenAdded_shouldReturnTheNumberOfMatchedResults() throws SQLException {
		ContactDetails munna = new ContactDetails("Munna", "Kumar", "Mithapur", "Gaya", "Bihar", 120012);
		try {
			listOfContacts = this.addressBookService.readContactList();
			Assert.assertEquals(1, listOfContacts.size());
			Assert.assertEquals(munna, listOfContacts.get(1));
		} catch (AddressBookException e) {
			e.printStackTrace();
		}
	}
    
    @Test
	public void updatingContactDetails_shouldMatchWithTheResuts() throws SQLException {
		
		try {
			
			this.addressBookService.updateAddressBookDetails("Shravan","Kumar","Paraiya", "Gaya", "Bihar", 585252);
			this.addressBookService.readContactsDetails("Shravan","Kumar");
			Assert.assertEquals(this.addressBookService.resultSetOpted.getString("FIRST_NAME "), "Shravan");
		}catch(AddressBookException e) {
			e.printStackTrace();
		}
	}
}
