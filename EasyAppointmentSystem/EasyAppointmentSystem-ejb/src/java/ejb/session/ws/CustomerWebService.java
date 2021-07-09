package ejb.session.ws;

import ejb.session.stateless.AppointmentSessionBeanLocal;
import ejb.session.stateless.CategorySessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.ServiceProviderSessionBeanLocal;
import entity.AppointmentEntity;
import entity.CategoryEntity;
import entity.CustomerEntity;
import entity.ServiceProviderEntity;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.AppointmentNotFoundException;
import util.exception.CategoryNotFoundException;
import util.exception.CustomerAlreadyExistsException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginException;
import util.exception.ServiceProviderAlreadyExistsException;
import util.exception.ServiceProviderNotFoundException;
import util.exception.UnknownPersistenceException;


@WebService(serviceName = "CustomerWebService")
@Stateless()
public class CustomerWebService {

    @EJB(name = "CategorySessionBeanLocal")
    private CategorySessionBeanLocal categorySessionBeanLocal;

    @EJB(name = "ServiceProviderSessionBeanLocal")
    private ServiceProviderSessionBeanLocal serviceProviderSessionBeanLocal;

    @EJB(name = "AppointmentSessionBeanLocal")
    private AppointmentSessionBeanLocal appointmentSessionBeanLocal;

    @EJB(name = "CustomerSessionBeanLocal")
    private CustomerSessionBeanLocal customerSessionBeanLocal;
    
    
    
    
    @WebMethod(operationName = "retrieveCustomerByUsername")
    public CustomerEntity retrieveCustomerByUsername(@WebParam(name = "email") String email) throws CustomerNotFoundException 
    {
        return customerSessionBeanLocal.retrieveCustomerByUsername(email);
    }
    
    @WebMethod(operationName = "createNewCustomer")
    public CustomerEntity createNewCustomer(@WebParam(name = "newCustomerEntity") CustomerEntity newCustomerEntity) throws CustomerNotFoundException, CustomerAlreadyExistsException, UnknownPersistenceException, InputDataValidationException 
    {
        return customerSessionBeanLocal.createNewCustomer(newCustomerEntity);
    }
    
    @WebMethod(operationName = "retrieveServiceProviderAppointments")
    public List<AppointmentEntity> retrieveServiceProviderAppointments(@WebParam(name = "serviceProviderId") Long serviceProviderId) throws ServiceProviderNotFoundException 
    {
        return serviceProviderSessionBeanLocal.retrieveAppointmentsList(serviceProviderId).getAppointments();
    }
    
    @WebMethod(operationName = "retrieveCustomerByCustomerId")
    public CustomerEntity retrieveCustomerByCustomerId(@WebParam(name = "customerId") Long customerId) throws CustomerNotFoundException 
    {
        return customerSessionBeanLocal.retrieveCustomerByCustomerId(customerId);
    }
    
    @WebMethod(operationName = "updateCustomer")
    public void updateCustomer(@WebParam(name = "c") CustomerEntity c) throws CustomerNotFoundException, CustomerAlreadyExistsException, InputDataValidationException, UnknownPersistenceException 
    {
        customerSessionBeanLocal.updateCustomer(c);
    }
    
    @WebMethod(operationName = "retrieveAppointmentByAppointmentId")
    public AppointmentEntity retrieveAppointmentByAppointmentId(@WebParam(name = "appointmentId") Long appointmentId) throws AppointmentNotFoundException
    {
        return appointmentSessionBeanLocal.retrieveAppointmentByAppointmentId(appointmentId);
    }
    
//    @WebMethod(operationName = "retrieveServiceProvAppointmentsForServiceProv")
//     public List<AppointmentEntity> retrieveServiceProvAppointmentsForServiceProv(@WebParam(name = "serviceProvId")Long serviceProvId) throws ServiceProviderNotFoundException
//     {
//         return appointmentSessionBeanLocal.retrieveServiceProvAppointmentsForServiceProv(serviceProvId);
//     }
     
    @WebMethod(operationName = "updateServiceProvider")
    public void updateServiceProvider(ServiceProviderEntity s) throws ServiceProviderNotFoundException, InputDataValidationException, ServiceProviderAlreadyExistsException, UnknownPersistenceException
    {
        serviceProviderSessionBeanLocal.updateServiceProvider(s);
    }
    
    @WebMethod(operationName = "computeAverageRating")
    public Double computeAverageRating(@WebParam(name = "serviceProvId")Long serviceProvId) throws ServiceProviderNotFoundException
    {
        return serviceProviderSessionBeanLocal.computeAverageRating(serviceProvId);
    }
     
    @WebMethod(operationName = "retrieveServiceProviderById")
    public ServiceProviderEntity retrieveServiceProviderById(@WebParam(name = "serviceProviderId") Long serviceProviderId) throws ServiceProviderNotFoundException
    {
        return serviceProviderSessionBeanLocal.retrieveServiceProviderById(serviceProviderId);
    }
    
    @WebMethod(operationName = "deleteCustomer")
    public void deleteCustomer(@WebParam(name = "customerId") Long customerId) 
    {
        customerSessionBeanLocal.deleteCustomer(customerId);
    }
    
    @WebMethod(operationName = "retrieveCategoryByCategoryNum")
    public CategoryEntity retrieveCategoryByCategoryNum(@WebParam(name = "catNum") Integer catNum) throws CategoryNotFoundException
    {
        return categorySessionBeanLocal.retrieveCategoryByCategoryNum(catNum);
    }
    
    @WebMethod(operationName = "retrieveServiceProvidersInCriteria")
    public List<ServiceProviderEntity> retrieveServiceProvidersInCriteria(@WebParam(name = "category") Integer category, @WebParam(name = "city") String city) throws CategoryNotFoundException
    {
        return serviceProviderSessionBeanLocal.retrieveServiceProvidersInCriteria(category, city);
    }
    
    @WebMethod(operationName = "customerLogin")
    public CustomerEntity customerLogin(@WebParam(name = "email") String email, @WebParam(name = "password") String password) throws InvalidLoginException 
    {
        return customerSessionBeanLocal.customerLogin(email, password);
    }
    
    @WebMethod(operationName = "registerCustomer")
    public void registerCustomer(@WebParam(name = "customerEntity") CustomerEntity customerEntity) throws CustomerNotFoundException, CustomerAlreadyExistsException, UnknownPersistenceException, InputDataValidationException 
    {
        customerSessionBeanLocal.createNewCustomer(customerEntity);
    }
    
    @WebMethod(operationName = "deleteAppointment")
    public void deleteAppointment(@WebParam(name = "appointmentId") Long appointmentId) throws AppointmentNotFoundException
    {
        appointmentSessionBeanLocal.deleteAppointment(appointmentId);
    }
    
    @WebMethod(operationName = "retrieveBusinessCategories")
    public List<CategoryEntity> retrieveBusinessCategories()
    {
        return categorySessionBeanLocal.retrieveAllCategories();
    }
    
    @WebMethod(operationName = "rateServiceProvider")
    public void rateServiceProvider(@WebParam(name = "serviceProvId") Long serviceProvId, @WebParam(name = "rating") Double rating) throws ServiceProviderNotFoundException 
    {
        serviceProviderSessionBeanLocal.rateServiceProvider(serviceProvId, rating);
    }
    
    @WebMethod(operationName = "retrieveAllCategories")
    public List<CategoryEntity> retrieveAllCategories()
    {
        return categorySessionBeanLocal.retrieveAllCategories();
    }
    
    @WebMethod(operationName = "retrieveAvailableTimeSlotsString")
    public List<String> retrieveAvailableTimeSlotsString(@WebParam(name = "date")String dateStr, @WebParam(name = "serviceProviderId")Long serviceProviderId) throws ServiceProviderNotFoundException, ParseException
    {
        return serviceProviderSessionBeanLocal.retrieveAvailableTimeSlotsString(dateStr, serviceProviderId);
    }
    
    @WebMethod(operationName = "retrieveFirstAvailableTimeString")
    public String retrieveFirstAvailableTimeString(@WebParam(name = "date")String dateStr, @WebParam(name = "serviceProviderId")Long serviceProviderId) throws ServiceProviderNotFoundException, ParseException
    {
        return serviceProviderSessionBeanLocal.retrieveFirstAvailableTimeString(dateStr, serviceProviderId);
    }
    
    @WebMethod(operationName = "bookAppointment")
    public void bookAppointment(@WebParam(name = "serviceProvId")Long serviceProvId, @WebParam(name = "custId")Long custId, @WebParam(name = "dateStr")String dateStr, @WebParam(name = "timeStr")String timeStr) throws ServiceProviderNotFoundException, CustomerNotFoundException, AppointmentNotFoundException, ParseException, InputDataValidationException, ServiceProviderAlreadyExistsException, UnknownPersistenceException
    {
        appointmentSessionBeanLocal.bookAppointment(serviceProvId, custId, dateStr, timeStr);
    }
    
    @WebMethod(operationName = "retrieveCustomerAppointments")
    public List<AppointmentEntity> retrieveCustomerAppointments(@WebParam(name = "customerId")Long customerId) throws CustomerNotFoundException
    {
        return appointmentSessionBeanLocal.retrieveCustomerAppointments(customerId);
    }
    
    @WebMethod(operationName = "cancelAppointment")
    public void cancelAppointment(@WebParam(name = "appointmentId")Long appointmentId) throws AppointmentNotFoundException, CustomerNotFoundException, ServiceProviderNotFoundException
    {
        appointmentSessionBeanLocal.cancelAppointment(appointmentId);
    }
    
    @WebMethod(operationName = "getAppointmentTimeString")
    public String getAppointmentTimeString(Long appointmentId) throws AppointmentNotFoundException
    {
        return appointmentSessionBeanLocal.getAppointmentTimeString(appointmentId);
    }
    
    @WebMethod(operationName = "getAppointmentDateString")
    public String getAppointmentDateString(Long appointmentId) throws AppointmentNotFoundException
    {
        return appointmentSessionBeanLocal.getAppointmentDateString(appointmentId);
    }
    
    @WebMethod(operationName = "canBeCancelled")
    public boolean canBeCancelled(Long appointmentId) throws AppointmentNotFoundException
    {
        return appointmentSessionBeanLocal.canBeCancelled(appointmentId);
    }
    
    @WebMethod(operationName = "retrieveOccupiedSlotsString")
    public List<String> retrieveOccupiedSlotsString(String dateStr, Long serviceProvId) throws ServiceProviderNotFoundException, ParseException
    {
        return serviceProviderSessionBeanLocal.retrieveOccupiedSlotsString(dateStr, serviceProvId);
    }
    
    @WebMethod(operationName = "canBeRated")
    public boolean canBeRated(Long customerId, Long serviceProviderId) throws CustomerNotFoundException, ServiceProviderNotFoundException
    {
        return serviceProviderSessionBeanLocal.canBeRated(customerId, serviceProviderId);
    }
}

