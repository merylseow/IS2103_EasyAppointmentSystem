package ejb.session.stateless;

import entity.CustomerEntity;
import java.util.Set;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CustomerAlreadyExistsException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginException;
import util.exception.UnknownPersistenceException;
import util.security.CryptographicHelper;

@Stateless
@Local(CustomerSessionBeanLocal.class)
@Remote(CustomerSessionBeanRemote.class)
public class CustomerSessionBean implements CustomerSessionBeanRemote, CustomerSessionBeanLocal {

    @PersistenceContext(unitName = "EasyAppointmentSystem-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public CustomerSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public CustomerEntity createNewCustomer(CustomerEntity newCustomerEntity) throws CustomerNotFoundException, CustomerAlreadyExistsException, UnknownPersistenceException, InputDataValidationException {

        Set<ConstraintViolation<CustomerEntity>> constraintViolations = validator.validate(newCustomerEntity);

        if (constraintViolations.isEmpty()) {
            try {
                if (newCustomerEntity != null) {
                    em.persist(newCustomerEntity);
                    em.flush();
                } else {
                    throw new CustomerNotFoundException("Customer does not exist!");
                }
                return newCustomerEntity;

            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new CustomerAlreadyExistsException("Customer already exists");
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }

    }

    @Override
    public CustomerEntity retrieveCustomerByCustomerId(Long customerId) throws CustomerNotFoundException {
        CustomerEntity customerEntity = em.find(CustomerEntity.class, customerId);

        if (customerEntity != null) {
            return customerEntity;
        } else {
            throw new CustomerNotFoundException("Customer cannot be found!\n");
        }
    }

    @Override
    public CustomerEntity retrieveCustomerByUsername(String email) throws CustomerNotFoundException {
        Query query = em.createQuery("SELECT c FROM CustomerEntity c WHERE c.emailAddress = :inEmail");
        query.setParameter("inEmail", email);

        try {
            return (CustomerEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new CustomerNotFoundException("Customer's email address " + email + " not found");
        }
    }

    @Override
    public void updateCustomer(CustomerEntity c) throws CustomerNotFoundException,CustomerAlreadyExistsException,InputDataValidationException,UnknownPersistenceException {
        Set<ConstraintViolation<CustomerEntity>> constraintViolations = validator.validate(c);

        if (constraintViolations.isEmpty()) {
            try {
                if (c != null && c.getCustomerId() != null) {
                    try {
                        CustomerEntity customerEntityToUpdate = retrieveCustomerByCustomerId(c.getCustomerId());

                        if (customerEntityToUpdate.getCustomerId().equals(c.getCustomerId())) {
                            customerEntityToUpdate.setIdentityNumber(c.getIdentityNumber());
                            customerEntityToUpdate.setFirstName(c.getFirstName());
                            customerEntityToUpdate.setLastName(c.getLastName());
                            customerEntityToUpdate.setGender(c.getGender());
                            customerEntityToUpdate.setAge(c.getAge());
                            customerEntityToUpdate.setPhoneNumber(c.getPhoneNumber());
                            customerEntityToUpdate.setAddress(c.getAddress());
                            customerEntityToUpdate.setCity(c.getCity());
                            customerEntityToUpdate.setAppointments(c.getAppointments());
                        }

                        em.merge(customerEntityToUpdate);
                    } catch (CustomerNotFoundException ex) {
                        throw new CustomerNotFoundException("An error has occurred while retrieving customer: " + ex.getMessage() + "\n");
                    }
                } else {
                    throw new CustomerNotFoundException("Customer ID not provided for customer to be updated");
                }

            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new CustomerAlreadyExistsException("Customer already exists");
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }

    }

    @Override
    public void deleteCustomer(Long customerId) {
        try {
            CustomerEntity customerEntity = retrieveCustomerByCustomerId(customerId);
            em.remove(customerEntity);
        } catch (CustomerNotFoundException ex) {
            System.out.println("Customer cannot be deleted.\n");
        }
    }

    @Override
    public CustomerEntity customerLogin(String email, String password) throws InvalidLoginException {
        try {
            CustomerEntity customerEntity = retrieveCustomerByUsername(email);
            String decrypt = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + customerEntity.getSalt()));

            if (customerEntity.getPassword().equals(decrypt)) {
                return customerEntity;
            } else {
                throw new InvalidLoginException("Password is wrong!");
            }
        } catch (CustomerNotFoundException ex) {
            throw new InvalidLoginException("Invalid username!");
        }
    }

    @Override
    public CustomerEntity retrieveCustomerAppointments(Long custId) throws CustomerNotFoundException {
        try {
            CustomerEntity customerEntity = em.find(CustomerEntity.class, custId);
            customerEntity.getAppointments().size();
            return customerEntity;
        } catch (NoResultException | NullPointerException ex) {
            throw new CustomerNotFoundException("Customer does not exist!");
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<CustomerEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
