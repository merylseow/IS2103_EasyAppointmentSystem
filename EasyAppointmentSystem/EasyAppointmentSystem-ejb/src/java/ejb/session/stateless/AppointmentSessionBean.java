package ejb.session.stateless;

import entity.AppointmentEntity;
import entity.CategoryEntity;
import entity.CustomerEntity;
import entity.ServiceProviderEntity;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
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
import util.exception.AppointmentNotFoundException;
import util.exception.CategoryNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.ServiceProviderAlreadyExistsException;
import util.exception.ServiceProviderNotFoundException;
import util.exception.UnknownPersistenceException;

@Stateless
@Local(AppointmentSessionBeanLocal.class)
@Remote(AppointmentSessionBeanRemote.class)

public class AppointmentSessionBean implements AppointmentSessionBeanLocal, AppointmentSessionBeanRemote {

    @PersistenceContext(unitName = "EasyAppointmentSystem-ejbPU")
    private EntityManager em;

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;
    @EJB
    private ServiceProviderSessionBeanLocal serviceProviderSessionBeanLocal;
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public AppointmentSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public AppointmentEntity createNewAppointment(Long customerId, Long serviceProvId, AppointmentEntity newAppointmentEntity) throws CustomerNotFoundException, ServiceProviderNotFoundException, AppointmentNotFoundException,InputDataValidationException,ServiceProviderAlreadyExistsException,UnknownPersistenceException {
        Set<ConstraintViolation<AppointmentEntity>> constraintViolations = validator.validate(newAppointmentEntity);

        if (constraintViolations.isEmpty()) {
            try {
                if (newAppointmentEntity != null) {
                    CustomerEntity c = customerSessionBeanLocal.retrieveCustomerByCustomerId(customerId);
                    ServiceProviderEntity s = serviceProviderSessionBeanLocal.retrieveServiceProviderById(serviceProvId);
                    newAppointmentEntity.setCustomerEntity(c);
                    newAppointmentEntity.setServiceProviderEntity(s);
                    c.getAppointments().add(newAppointmentEntity);
                    s.getAppointments().add(newAppointmentEntity);
                    em.persist(newAppointmentEntity);
                    em.flush();
                    return newAppointmentEntity;
                } else {
                    throw new AppointmentNotFoundException("Appointment does not exist!");
                }

            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new ServiceProviderAlreadyExistsException("Service Provider already exists");
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
    public List<AppointmentEntity> retrieveAllAppointments() {
        Query query = em.createQuery("SELECT a FROM AppointmentEntity a");

        return query.getResultList();
    }

    @Override
    public AppointmentEntity retrieveAppointmentByAppointmentId(Long appointmentId) throws AppointmentNotFoundException {
        AppointmentEntity appointmentEntity = em.find(AppointmentEntity.class, appointmentId);

        if (appointmentEntity != null) {
            return appointmentEntity;
        } else {
            throw new AppointmentNotFoundException("Appointment does not exist!");
        }
    }

    @Override
    public AppointmentEntity retrieveAppointmentByCustomerId(Long custId) throws AppointmentNotFoundException, CustomerNotFoundException {
        CustomerEntity c = retrieveCustomerByCustomerId(custId);
        Query query = em.createQuery("SELECT a FROM AppointmentEntity a WHERE a.customerEntity.customerId = :inCustId");
        query.setParameter("inCustId", custId);

        try {
            return (AppointmentEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new AppointmentNotFoundException("Appointment does not exist!");
        }
    }

    @Override
    public AppointmentEntity retrieveAppointmentByServiceProviderId(Long serviceProvId) throws AppointmentNotFoundException, ServiceProviderNotFoundException {
        ServiceProviderEntity s = retrieveServiceProviderById(serviceProvId);
        Query query = em.createQuery("SELECT a FROM AppointmentEntity a WHERE a.serviceProviderEntity.serviceProviderId :inServiceProvId");
        query.setParameter("inServiceProvId", serviceProvId);

        try {
            return (AppointmentEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new AppointmentNotFoundException("Appointment does not exist!");
        }
    }

    @Override
    public AppointmentEntity retrieveAppointmentByAppointmentCode(String appointmentCode) throws AppointmentNotFoundException {
        Query query = em.createQuery("SELECT a FROM AppointmentEntity a WHERE a.appointmentCode = :inAppointmentCode");
        query.setParameter("inAppointmentCode", appointmentCode);
        try
        {
            return (AppointmentEntity) query.getSingleResult();
        }
        catch (NoResultException | NonUniqueResultException ex)
        {
            throw new AppointmentNotFoundException("Appointment does not exist!");
        }
    }

    @Override
    public void updateAppointment(AppointmentEntity appointmentEntity) throws AppointmentNotFoundException {
        if (appointmentEntity != null && appointmentEntity.getAppointmentId() != null) {
            AppointmentEntity appointmentEntityToUpdate = retrieveAppointmentByAppointmentId(appointmentEntity.getAppointmentId());

            if (appointmentEntityToUpdate.getAppointmentId().equals(appointmentEntityToUpdate.getAppointmentId())) {
                appointmentEntityToUpdate.setAppointmentDate(appointmentEntityToUpdate.getAppointmentDate());
            }
        } else {
            throw new AppointmentNotFoundException("Appointment ID not provided for appointment to be updated");
        }
    }

    @Override
    public void deleteAppointment(Long appointmentId) throws AppointmentNotFoundException {
        AppointmentEntity appointmentEntityToRemove = retrieveAppointmentByAppointmentId(appointmentId);
        em.remove(appointmentEntityToRemove);
    }

    @Override
    public List<AppointmentEntity> retrieveCustomerAppointments(Long customerId) throws CustomerNotFoundException {
        CustomerEntity c = retrieveCustomerByCustomerId(customerId);
        Query query = em.createQuery("SELECT a FROM AppointmentEntity a WHERE a.customerEntity.customerId = :inCustomerId");
        query.setParameter("inCustomerId", customerId);
        return query.getResultList();
    }

    @Override
    public List<AppointmentEntity> retrieveServiceProvPendingAppointments (Long serviceProviderId) throws ServiceProviderNotFoundException
    {
        ServiceProviderEntity s = retrieveServiceProviderById(serviceProviderId);
        List<AppointmentEntity> spAppts = s.getAppointments();
        List<AppointmentEntity> newList = new ArrayList<>();
        for (AppointmentEntity a : spAppts)
        {
            Date currentDate = new Date();
            long diffInTime = a.getAppointmentDate().getTime() - currentDate.getTime();
            long diffInHours = (diffInTime / (1000 * 60 * 60 )) % 365;
            if( diffInHours >= -1) {
                newList.add(a);
            }
        }
        return newList;
    }
    
    @Override
    public List<AppointmentEntity> retrieveServiceProvAppointmentsForAdmin(Long serviceProvId) throws ServiceProviderNotFoundException {
        ServiceProviderEntity s = retrieveServiceProviderById(serviceProvId);
        Query query = em.createQuery("SELECT a FROM AppointmentEntity a WHERE "
                + "a.serviceProviderEntity.serviceProviderId = :inServiceProvId");
        query.setParameter("inServiceProvId", serviceProvId);
        return query.getResultList();
    }

    @Override
    public ServiceProviderEntity retrieveServiceProviderById(Long serviceProviderId) throws ServiceProviderNotFoundException {
        Query query = em.createQuery("SELECT s FROM ServiceProviderEntity s WHERE s.serviceProviderId = :inServiceProviderId");
        query.setParameter("inServiceProviderId", serviceProviderId);

        try {
            return (ServiceProviderEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new ServiceProviderNotFoundException("Service provider cannot be found!\n");
        }
    }

    @Override
    public CustomerEntity retrieveCustomerByCustomerId(Long customerId) throws CustomerNotFoundException {
        Query query = em.createQuery("SELECT c FROM CustomerEntity c WHERE c.customerId = :inCustomerId");
        query.setParameter("inCustomerId", customerId);

        try {
            return (CustomerEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new CustomerNotFoundException("Customer cannot be found!\n");
        }
    }

    @Override
    public void mergeAppointment(AppointmentEntity appointmentEntity) {
        em.merge(appointmentEntity);
    }

    @Override
    public void bookAppointment(Long serviceProvId, Long custId, String dateStr, String timeStr) throws ServiceProviderNotFoundException, CustomerNotFoundException, AppointmentNotFoundException, ParseException,InputDataValidationException,ServiceProviderAlreadyExistsException,UnknownPersistenceException {
        ServiceProviderEntity s = retrieveServiceProviderById(serviceProvId);
        CustomerEntity c = retrieveCustomerByCustomerId(custId);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Date date = dateFormat.parse(dateStr);
        Date time = timeFormat.parse(timeStr);

        //Date and Time is NOT THE SAME so I set the date's HH:mm with time's HH:mm
        Calendar calDate = Calendar.getInstance();
        calDate.setTime(date);
        Calendar calTime = Calendar.getInstance();
        calTime.setTime(time);
        calDate.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY));
        calDate.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE));

        //0 is January, 11 is December
        int month = calDate.get(Calendar.MONTH) + 1;
        String strMonth = "";
        if (month < 10) {
            strMonth += "0" + Integer.toString(month);
        } else {
            strMonth = Integer.toString(month);
        }

        int day = calDate.get(Calendar.DAY_OF_MONTH);
        String strDay = "";
        if (day < 10) {
            strDay += "0" + Integer.toString(day);
        } else {
            strDay = Integer.toString(day);
        }

        DateFormat timeF = new SimpleDateFormat("HHmm");
        String strTime = timeF.format(time);

        String code = Long.toString(s.getServiceProviderId()) + strMonth + strDay + strTime;
        AppointmentEntity a = new AppointmentEntity(code, calDate.getTime(), calDate.getTime());

        AppointmentEntity appt = createNewAppointment(custId, serviceProvId, a);
//        a.setServiceProviderEntity(s);
//        a.setCustomerEntity(c);
//        c.getAppointments().add(a);
//        s.getAppointments().add(a);
//        em.persist(s);
//        s.addToServiceProvAppointments(a);
//        c.addToCustomerAppointments(a);
//        em.persist(a);
//        em.flush();
    }

    @Override
    public void cancelAppointment(Long appointmentId) throws AppointmentNotFoundException, CustomerNotFoundException, ServiceProviderNotFoundException {
        AppointmentEntity a = retrieveAppointmentByAppointmentId(appointmentId);

        CustomerEntity c = a.getCustomerEntity();
        ServiceProviderEntity s = a.getServiceProviderEntity();

        List<AppointmentEntity> custAppts = retrieveCustomerAppointments(c.getCustomerId());
        //update appointment list in customers
        custAppts.remove(a);
        c.setAppointments(custAppts);
        em.merge(c);

        //update appointment list in service provider
        List<AppointmentEntity> spAppts = retrieveServiceProvPendingAppointments(s.getServiceProviderId());
        spAppts.remove(a);
        s.setAppointments(spAppts);
        em.merge(s);

        //delete appointment entity using sessionbean
        deleteAppointment(appointmentId);
    }

    @Override
    public String getAppointmentTimeString(Long appointmentId) throws AppointmentNotFoundException {
        AppointmentEntity a = retrieveAppointmentByAppointmentId(appointmentId);
        Date time = a.getAppointmentTime();
        SimpleDateFormat timeF = new SimpleDateFormat("HH:mm");
        return timeF.format(time);
    }

    @Override
    public String getAppointmentDateString(Long appointmentId) throws AppointmentNotFoundException {
        AppointmentEntity a = retrieveAppointmentByAppointmentId(appointmentId);
        Date date = a.getAppointmentDate();
        SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd");
        return dateF.format(date);
    }

    @Override
    public boolean canBeCancelled(Long appointmentId) throws AppointmentNotFoundException {
        AppointmentEntity a = retrieveAppointmentByAppointmentId(appointmentId);
        Date currDate = new Date();
        Date appointmentDate = a.getAppointmentDate();
        long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;
        return Math.abs(currDate.getTime() - appointmentDate.getTime()) > MILLIS_PER_DAY;
                       
//        AppointmentEntity a = retrieveAppointmentByAppointmentId(appointmentId);
//        Date curr = new Date();
//        Calendar apptDateLatest = Calendar.getInstance();
//        apptDateLatest.setTime(a.getAppointmentTime());
//        apptDateLatest.add(Calendar.HOUR, -24);
//        Date latestTimeToCancel = apptDateLatest.getTime();
//        if (curr.after(latestTimeToCancel)) // too late to cancel
//        {
//            return false;
//        }
//        return true;
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<AppointmentEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
