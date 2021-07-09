package ejb.session.stateless;

import entity.CustomerEntity;
import util.exception.CustomerAlreadyExistsException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginException;
import util.exception.UnknownPersistenceException;


public interface CustomerSessionBeanLocal
{
    public CustomerEntity createNewCustomer(CustomerEntity newCustomerEntity) throws CustomerNotFoundException,CustomerAlreadyExistsException,UnknownPersistenceException,InputDataValidationException;
    
    public CustomerEntity retrieveCustomerByCustomerId(Long customerId) throws CustomerNotFoundException;

    public void updateCustomer(CustomerEntity customerEntity) throws CustomerNotFoundException,CustomerAlreadyExistsException,InputDataValidationException,UnknownPersistenceException ;

    public void deleteCustomer(Long customerId);

    public CustomerEntity customerLogin(String username, String password) throws InvalidLoginException;

    public CustomerEntity retrieveCustomerByUsername(String email) throws CustomerNotFoundException;  

    public CustomerEntity retrieveCustomerAppointments(Long custId) throws CustomerNotFoundException;
}
