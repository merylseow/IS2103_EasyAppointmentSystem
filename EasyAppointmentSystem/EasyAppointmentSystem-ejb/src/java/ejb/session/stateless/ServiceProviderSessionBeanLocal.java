package ejb.session.stateless;

import entity.AppointmentEntity;
import entity.CategoryEntity;
import entity.ServiceProviderEntity;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import util.exception.CategoryNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginException;
import util.exception.ServiceProviderAlreadyExistsException;
import util.exception.ServiceProviderNotFoundException;
import util.exception.UnknownPersistenceException;


public interface ServiceProviderSessionBeanLocal
{
    public List<ServiceProviderEntity> retrieveAllServiceProviders();

    public ServiceProviderEntity createNewServiceProvider(ServiceProviderEntity newServiceProviderEntity, Integer categoryNum) throws CategoryNotFoundException,ServiceProviderAlreadyExistsException,UnknownPersistenceException, ServiceProviderNotFoundException, InputDataValidationException;
    
    public ServiceProviderEntity retrieveServiceProviderById(Long serviceProviderId) throws ServiceProviderNotFoundException;

    public void updateServiceProvider(ServiceProviderEntity serviceProviderEntity) throws ServiceProviderNotFoundException,InputDataValidationException,ServiceProviderAlreadyExistsException,UnknownPersistenceException ;

    public ServiceProviderEntity retrieveServiceProviderByEmail(String email) throws ServiceProviderNotFoundException;

    public ServiceProviderEntity serviceProviderEntityLogin(String email, String password) throws InvalidLoginException;

//    public List<Date> retrieveAllTimeSlots(Date date);
//
//    public List<Date> retrieveOccupiedSlots(Date date, Long serviceProvId) throws ServiceProviderNotFoundException;
//
//    public List<Date> retrieveAvailableTimeSlots(Date date, Long serviceProvId) throws ServiceProviderNotFoundException;

    public List<ServiceProviderEntity> retrieveServiceProvidersInCriteria(Integer category, String city) throws CategoryNotFoundException;

    public Double computeAverageRating(Long serviceProvId) throws ServiceProviderNotFoundException;

    public void rateServiceProvider(Long serviceProvId, Double rating) throws ServiceProviderNotFoundException;

    public List<ServiceProviderEntity> retrievePendingServiceProviders();

    public List<ServiceProviderEntity> retrieveUnblockedServiceProviders();

    public List<ServiceProviderEntity> retrieveServiceProvidersByCategory(String categoryName) throws CategoryNotFoundException;

    public CategoryEntity retrieveCategoryByCategoryNum(Integer categoryNum) throws CategoryNotFoundException;

    public CategoryEntity retrieveCategoryByCategoryName(String categoryName) throws CategoryNotFoundException;

    public Calendar retrieveFirstAvailableTime(Date date, Long serviceProvId) throws ServiceProviderNotFoundException;

    //public String encryptPassword(String password) throws NoSuchAlgorithmException;

    public ServiceProviderEntity retrieveAppointmentsList(Long serviceProviderId) throws ServiceProviderNotFoundException;

    public List<String> retrieveAvailableTimeSlotsString(String dateStr, Long serviceProviderId) throws ServiceProviderNotFoundException, ParseException;

    public String retrieveFirstAvailableTimeString(String dateStr, Long serviceProvId) throws ServiceProviderNotFoundException, ParseException;
    
    public List<Calendar> retrieveAllTimeSlots(Date date);

    public List<Calendar> retrieveOccupiedSlots(Date date, Long serviceProvId) throws ServiceProviderNotFoundException;

    public List<Calendar> retrieveAvailableTimeSlots(Date date, Long serviceProvId) throws ServiceProviderNotFoundException;

    public List<String> retrieveOccupiedSlotsString(String dateStr, Long serviceProvId) throws ServiceProviderNotFoundException, ParseException;

    public boolean canBeRated(Long customerId, Long serviceProviderId) throws CustomerNotFoundException, ServiceProviderNotFoundException;

    //public List<AppointmentEntity> retrieveServiceProvPendingAppointments(Long serviceProviderId) throws ServiceProviderNotFoundException;
}
