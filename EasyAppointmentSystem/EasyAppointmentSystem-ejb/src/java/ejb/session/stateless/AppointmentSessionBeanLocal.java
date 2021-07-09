package ejb.session.stateless;

import entity.AppointmentEntity;
import entity.CustomerEntity;
import entity.ServiceProviderEntity;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import util.exception.AppointmentNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.ServiceProviderAlreadyExistsException;
import util.exception.ServiceProviderNotFoundException;
import util.exception.UnknownPersistenceException;


public interface AppointmentSessionBeanLocal
{
    public AppointmentEntity createNewAppointment(Long customerId, Long serviceProvId, AppointmentEntity newAppointmentEntity) throws CustomerNotFoundException, ServiceProviderNotFoundException, AppointmentNotFoundException,InputDataValidationException,ServiceProviderAlreadyExistsException,UnknownPersistenceException ;

    public List<AppointmentEntity> retrieveAllAppointments();

    public AppointmentEntity retrieveAppointmentByAppointmentId(Long appointmentId) throws AppointmentNotFoundException;

    public AppointmentEntity retrieveAppointmentByCustomerId(Long custId) throws AppointmentNotFoundException, CustomerNotFoundException;

    public AppointmentEntity retrieveAppointmentByAppointmentCode(String appointmentCode) throws AppointmentNotFoundException;

    public void updateAppointment(AppointmentEntity appointmentEntity) throws AppointmentNotFoundException;

    public void deleteAppointment(Long appointmentId) throws AppointmentNotFoundException;

    public List<AppointmentEntity> retrieveCustomerAppointments(Long customerId) throws CustomerNotFoundException;

    public List<AppointmentEntity> retrieveServiceProvPendingAppointments(Long serviceProvId) throws ServiceProviderNotFoundException;

    public ServiceProviderEntity retrieveServiceProviderById(Long serviceProviderId) throws ServiceProviderNotFoundException;

    public void bookAppointment(Long serviceProvId, Long customerId, String dateStr, String timeStr) throws ServiceProviderNotFoundException, CustomerNotFoundException, AppointmentNotFoundException, ParseException,InputDataValidationException,ServiceProviderAlreadyExistsException,UnknownPersistenceException;

    public AppointmentEntity retrieveAppointmentByServiceProviderId(Long serviceProvId) throws AppointmentNotFoundException, ServiceProviderNotFoundException;

    public CustomerEntity retrieveCustomerByCustomerId(Long customerId) throws CustomerNotFoundException;

    public List<AppointmentEntity> retrieveServiceProvAppointmentsForAdmin(Long serviceProvId) throws ServiceProviderNotFoundException;

    public void mergeAppointment(AppointmentEntity appointmentEntity);

    public void cancelAppointment(Long appointmentId) throws AppointmentNotFoundException, CustomerNotFoundException, ServiceProviderNotFoundException;

    public String getAppointmentTimeString(Long appointmentId) throws AppointmentNotFoundException;
    
    public String getAppointmentDateString(Long appointmentId) throws AppointmentNotFoundException;

    public boolean canBeCancelled(Long appointmentId) throws AppointmentNotFoundException;
}