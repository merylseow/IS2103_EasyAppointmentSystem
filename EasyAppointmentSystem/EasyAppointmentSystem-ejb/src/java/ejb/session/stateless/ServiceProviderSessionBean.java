package ejb.session.stateless;

import entity.AppointmentEntity;
import entity.CategoryEntity;
import entity.CustomerEntity;
import entity.ServiceProviderEntity;
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
import static util.enumeration.StatusEnum.APPROVED;
import static util.enumeration.StatusEnum.BLOCKED;
import static util.enumeration.StatusEnum.PENDING;
import util.exception.CategoryNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginException;
import util.exception.ServiceProviderAlreadyExistsException;
import util.exception.ServiceProviderNotFoundException;
import util.exception.UnknownPersistenceException;
import util.security.CryptographicHelper;

@Stateless
@Local(ServiceProviderSessionBeanLocal.class)
@Remote(ServiceProviderSessionBeanRemote.class)

public class ServiceProviderSessionBean implements ServiceProviderSessionBeanRemote, ServiceProviderSessionBeanLocal {

    @PersistenceContext(unitName = "EasyAppointmentSystem-ejbPU")
    private EntityManager em;

    @EJB
    private CategorySessionBeanLocal categorySessionBeanLocal;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public ServiceProviderSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public List<ServiceProviderEntity> retrieveAllServiceProviders() {
        Query query = em.createQuery("SELECT s FROM ServiceProviderEntity s");
        return query.getResultList();
    }

    @Override
    public ServiceProviderEntity createNewServiceProvider(ServiceProviderEntity newServiceProviderEntity, Integer categoryNum) throws CategoryNotFoundException,ServiceProviderAlreadyExistsException,UnknownPersistenceException, ServiceProviderNotFoundException, InputDataValidationException {
        Set<ConstraintViolation<ServiceProviderEntity>> constraintViolations = validator.validate(newServiceProviderEntity);

        if (constraintViolations.isEmpty()) {
            try {

                if (newServiceProviderEntity != null) {
                    CategoryEntity c = categorySessionBeanLocal.retrieveCategoryByCategoryNum(categoryNum);

                    newServiceProviderEntity.setBusinessCategory(categoryNum);
                    newServiceProviderEntity.setCategory(c);
                    c.getServiceProviders().add(newServiceProviderEntity);
                    //em.merge(c);
                    //em.flush();
                    em.persist(newServiceProviderEntity);
                    em.flush();
                } else {
                    throw new ServiceProviderNotFoundException("Service Provider does not exist!");
                }
                   return newServiceProviderEntity;
            }catch (PersistenceException ex) {
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
            }else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
        }

       


    @Override
    public ServiceProviderEntity retrieveServiceProviderById(Long serviceProviderId) throws ServiceProviderNotFoundException {
        ServiceProviderEntity serviceProviderEntity = em.find(ServiceProviderEntity.class,
                 serviceProviderId);

        if (serviceProviderEntity != null) {
            return serviceProviderEntity;
        } else {
            throw new ServiceProviderNotFoundException("Service Provider ID " + serviceProviderId + " does not exist!");
        }
    }

    @Override
    public ServiceProviderEntity retrieveServiceProviderByEmail(String email) throws ServiceProviderNotFoundException {
        Query query = em.createQuery("SELECT s FROM ServiceProviderEntity s WHERE s.email = :inEmail");
        query.setParameter("inEmail", email);

        try {
            return (ServiceProviderEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new ServiceProviderNotFoundException("Service provider cannot be found!\n");
        }
    }

    @Override
    public List<ServiceProviderEntity> retrieveServiceProvidersInCriteria(Integer category, String city) throws CategoryNotFoundException {
        CategoryEntity cat = retrieveCategoryByCategoryNum(category);
        Query query = em.createQuery("SELECT s FROM ServiceProviderEntity s WHERE s.businessCategory = :inCategory AND s.city = :inCity");
        query.setParameter("inCategory", category);
        query.setParameter("inCity", city);
        return query.getResultList();
    }

    @Override
    public void updateServiceProvider(ServiceProviderEntity s) throws ServiceProviderNotFoundException,InputDataValidationException,ServiceProviderAlreadyExistsException,UnknownPersistenceException {
        Set<ConstraintViolation<ServiceProviderEntity>> constraintViolations = validator.validate(s);

        if (constraintViolations.isEmpty()) {
            try {
                if (s != null && s.getServiceProviderId() != null) {
            try {
                ServiceProviderEntity spEntityToUpdate = retrieveServiceProviderById(s.getServiceProviderId());

                if (spEntityToUpdate.getServiceProviderId().equals(s.getServiceProviderId())) {
                    spEntityToUpdate.setName(s.getName());
                    spEntityToUpdate.setRegistrationNumber(s.getRegistrationNumber());
                    spEntityToUpdate.setCity(s.getCity());
                    spEntityToUpdate.setAddress(s.getAddress());
                    spEntityToUpdate.setPhone(s.getPhone());
                    spEntityToUpdate.setStatus(s.getStatus());
                    spEntityToUpdate.setRating(s.getRating());
                    spEntityToUpdate.setCategory(s.getCategory());
                    spEntityToUpdate.setAppointments(s.getAppointments());
                    spEntityToUpdate.setEmail(s.getEmail());
                    spEntityToUpdate.setPassword(s.getPassword());
                }
            } catch (ServiceProviderNotFoundException ex) {
                throw new ServiceProviderNotFoundException("An error has occurred while retrieving service provider: " + ex.getMessage() + "\n");
            }
        } else {
            throw new ServiceProviderNotFoundException("Service Provider ID not provided for service provider to be updated");
        }

            }catch (PersistenceException ex) {
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
            }else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
       
    }

    @Override
    public ServiceProviderEntity serviceProviderEntityLogin(String email, String password) throws InvalidLoginException {
        try {
            ServiceProviderEntity serviceProviderEntity = retrieveServiceProviderByEmail(email);
            String decrypt = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + serviceProviderEntity.getSalt()));

            if (serviceProviderEntity.getPassword().equals(decrypt)) {
                if (serviceProviderEntity.getStatus().equals(APPROVED)) {
                    return serviceProviderEntity;
                } else if (serviceProviderEntity.getStatus().equals(PENDING)) {
                    throw new InvalidLoginException("Service provider status is still pending!");
                } else if (serviceProviderEntity.getStatus().equals(BLOCKED)) {
                    throw new InvalidLoginException("Service provider status is blocked by administrator!");
                }
            } else {
                throw new InvalidLoginException("Invalid password, please try again!");
            }
        } catch (ServiceProviderNotFoundException ex) {
            throw new InvalidLoginException("Email address does not exist, please try again!");
        }
        return null;
    }

    @Override
    public CategoryEntity retrieveCategoryByCategoryNum(Integer categoryNum) throws CategoryNotFoundException {
        Query query = em.createQuery("SELECT c FROM CategoryEntity c WHERE c.categoryNum = :inCategoryNum");
        query.setParameter("inCategoryNum", categoryNum);
        try {
            return (CategoryEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new CategoryNotFoundException("Category does not exist!");
        }
    }

    @Override
    public CategoryEntity retrieveCategoryByCategoryName(String categoryName) throws CategoryNotFoundException {
        Query query = em.createQuery("SELECT c FROM CategoryEntity c WHERE c.categoryName = :inCategoryName");
        query.setParameter("inCategoryName", categoryName);
        //System.out.println("Cat: " + query.getSingleResult());
        try {
            if (query.getResultList().isEmpty()) {
                throw new CategoryNotFoundException("Category does not exist!");
            }

            return (CategoryEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new CategoryNotFoundException("Category does not exist!");
        }
    }

    @Override
    public List<Calendar> retrieveAllTimeSlots(Date date) {
        ArrayList<Calendar> timeSlots = new ArrayList<>();

        Calendar first = Calendar.getInstance();
        first.setTime(date);
        first.set(Calendar.HOUR_OF_DAY, 8);
        first.set(Calendar.MINUTE, 30);
        timeSlots.add(first);

        Calendar second = Calendar.getInstance();
        second.setTime(date);
        second.set(Calendar.HOUR_OF_DAY, 9);
        second.set(Calendar.MINUTE, 30);
        timeSlots.add(second);

        Calendar third = Calendar.getInstance();
        third.setTime(date);
        third.set(Calendar.HOUR_OF_DAY, 10);
        third.set(Calendar.MINUTE, 30);
        timeSlots.add(third);

        Calendar fourth = Calendar.getInstance();
        fourth.setTime(date);
        fourth.set(Calendar.HOUR_OF_DAY, 11);
        fourth.set(Calendar.MINUTE, 30);
        timeSlots.add(fourth);

        Calendar fifth = Calendar.getInstance();
        fifth.setTime(date);
        fifth.set(Calendar.HOUR_OF_DAY, 12);
        fifth.set(Calendar.MINUTE, 30);
        timeSlots.add(fifth);

        Calendar sixth = Calendar.getInstance();
        sixth.setTime(date);
        sixth.set(Calendar.HOUR_OF_DAY, 13);
        sixth.set(Calendar.MINUTE, 30);
        timeSlots.add(sixth);

        Calendar seventh = Calendar.getInstance();
        seventh.setTime(date);
        seventh.set(Calendar.HOUR_OF_DAY, 14);
        seventh.set(Calendar.MINUTE, 30);
        timeSlots.add(seventh);

        Calendar eighth = Calendar.getInstance();
        eighth.setTime(date);
        eighth.set(Calendar.HOUR_OF_DAY, 15);
        eighth.set(Calendar.MINUTE, 30);
        timeSlots.add(eighth);

        Calendar ninth = Calendar.getInstance();
        ninth.setTime(date);
        ninth.set(Calendar.HOUR_OF_DAY, 16);
        ninth.set(Calendar.MINUTE, 30);
        timeSlots.add(ninth);

        Calendar tenth = Calendar.getInstance();
        tenth.setTime(date);
        tenth.set(Calendar.HOUR_OF_DAY, 17);
        tenth.set(Calendar.MINUTE, 30);
        timeSlots.add(tenth);
        return timeSlots;
    }

    @Override
    public List<Calendar> retrieveOccupiedSlots(Date date, Long serviceProvId) throws ServiceProviderNotFoundException {
        ServiceProviderEntity s = retrieveServiceProviderById(serviceProvId);
        Query query = em.createQuery("SELECT a FROM AppointmentEntity a");
        List<AppointmentEntity> allAppts = query.getResultList();
        List<Calendar> occupiedTimeSlots = new ArrayList<>();

        //Date that is passed into this method is currently (for eg.) 2021-04-15 00:00
        //a.getAppointmentDate() returns exactly the correct date and time, hence it will always not be equal.
        for (AppointmentEntity a : allAppts) {
            if (a.getAppointmentDate().equals(date) && a.getServiceProviderEntity().getServiceProviderId().equals(serviceProvId));
            //fixed over here
            Calendar c = Calendar.getInstance();
            c.setTime(a.getAppointmentDate());
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);

            if (c.getTime().equals(date) && a.getServiceProviderEntity().getServiceProviderId().equals(serviceProvId)) {
                /*
                System.out.println("start");
                Calendar convert1 = Calendar.getInstance();
                Calendar convert2 = Calendar.getInstance();
                convert1.setTime(a.getAppointmentDate());
                convert2.setTime(a.getAppointmentTime());
                System.out.println(convert1.toString());
                System.out.println(convert2.toString());
                System.out.println("end");
                 */
                Calendar cal = Calendar.getInstance();
                cal.setTime(a.getAppointmentTime());
                occupiedTimeSlots.add(cal);
            }
        }
        /*
        List<Calendar> calList = new ArrayList<>();
        for (Date date : occupiedTimeSlots)
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            calList.add(cal);
        }
         */
        return occupiedTimeSlots;
    }

    @Override
    public List<String> retrieveOccupiedSlotsString(String dateStr, Long serviceProvId) throws ServiceProviderNotFoundException, ParseException {
        SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date date = datef.parse(dateStr);
        List<Calendar> occupiedSlots = retrieveOccupiedSlots(date, serviceProvId);
        List<String> newList = new ArrayList<>();
        for (Calendar occupied : occupiedSlots) {
            SimpleDateFormat timef = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            //occupied is type Calendar, hence illegal conversion
            //String timeStr = timef.format(occupied); 
            Date occupiedDate = occupied.getTime();
            String timeStr = timef.format(occupiedDate);
            newList.add(timeStr);
        }
        return newList;
    }

    @Override
    public List<Calendar> retrieveAvailableTimeSlots(Date date, Long serviceProvId) throws ServiceProviderNotFoundException {
        List<Calendar> allTimeSlots = retrieveAllTimeSlots(date);
        List<Calendar> occupiedSlots = retrieveOccupiedSlots(date, serviceProvId);
        List<Calendar> availSlotsFirst = new ArrayList<>();

        // Only keep unoccupied slots
        for (Calendar timeSlot : allTimeSlots) {
            if (!occupiedSlots.contains(timeSlot)) {
                availSlotsFirst.add(timeSlot);
            }
        }

        Calendar current = Calendar.getInstance();
        current.add(Calendar.HOUR, 2);
        Date earliestTimeAvail = current.getTime();

        List<Calendar> availSlotsFinal = new ArrayList<>();

        for (Calendar timeSlot : availSlotsFirst) {
            // if time slot is later than the earliest possible time, it is an available slot
            //if (timeSlot.getTime().compareTo(earliestTimeAvail) >= 0)
            if (timeSlot.getTime().after(earliestTimeAvail)) {
                availSlotsFinal.add(timeSlot);
            }
        }
        return availSlotsFinal;
    }

    @Override
    public List<String> retrieveAvailableTimeSlotsString(String dateStr, Long serviceProviderId) throws ServiceProviderNotFoundException, ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse(dateStr);
        List<Calendar> availTimes = retrieveAvailableTimeSlots(date, serviceProviderId);
        List<String> newList = new ArrayList<>();
        for (Calendar time : availTimes) {
            Date calTime = time.getTime();
            SimpleDateFormat timef = new SimpleDateFormat("HH:mm");
            String timeStr = timef.format(calTime);
            newList.add(timeStr);
        }
        return newList;
    }

    @Override
    public Calendar retrieveFirstAvailableTime(Date date, Long serviceProvId) throws ServiceProviderNotFoundException {
        List<Calendar> availableTimeSlots = retrieveAvailableTimeSlots(date, serviceProvId);
        return availableTimeSlots.get(0);
    }

    @Override
    public String retrieveFirstAvailableTimeString(String dateStr, Long serviceProvId) throws ServiceProviderNotFoundException, ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse(dateStr);
        Calendar firstAvailTime = retrieveFirstAvailableTime(date, serviceProvId);
        Date firstAvail = firstAvailTime.getTime();
        SimpleDateFormat timef = new SimpleDateFormat("HH:mm");
        String timeStr = timef.format(firstAvail);
        return timeStr;
    }

    @Override
    public void rateServiceProvider(Long serviceProvId, Double rating) throws ServiceProviderNotFoundException {
        ServiceProviderEntity s = retrieveServiceProviderById(serviceProvId);
        s.addToRatingsList(rating);
    }

    @Override
    public Double computeAverageRating(Long serviceProvId) throws ServiceProviderNotFoundException {
        ServiceProviderEntity s = retrieveServiceProviderById(serviceProvId);
        List<Double> ratings = s.getRatingsList();
        Double ratingSum = 0.0;
        for (Double rating : ratings) {
            ratingSum += rating;
        }
        Double aveRating = ratingSum / ratings.size();
        return aveRating;
    }

    @Override
    public List<ServiceProviderEntity> retrievePendingServiceProviders() {
        Query query = em.createQuery("SELECT s FROM ServiceProviderEntity s WHERE s.status = :inStatus");
        query.setParameter("inStatus", PENDING);
        return query.getResultList();
    }

    @Override
    public List<ServiceProviderEntity> retrieveUnblockedServiceProviders() {
        Query query = em.createQuery("SELECT s FROM ServiceProviderEntity s WHERE s.status <> :inStatus");
        query.setParameter("inStatus", BLOCKED);
        return query.getResultList();
    }

    @Override
    public List<ServiceProviderEntity> retrieveServiceProvidersByCategory(String categoryName) throws CategoryNotFoundException {
        CategoryEntity c = retrieveCategoryByCategoryName(categoryName);
        Query query = em.createQuery("SELECT s FROM ServiceProviderEntity s WHERE s.category.categoryName = :inCatName");
        query.setParameter("inCatName", categoryName);
        return query.getResultList();
    }

    @Override
    public ServiceProviderEntity retrieveAppointmentsList(Long serviceProviderId) throws ServiceProviderNotFoundException {
        ServiceProviderEntity sp = em.find(ServiceProviderEntity.class,
                 serviceProviderId);
        if (sp == null) {
            throw new ServiceProviderNotFoundException("Service Provider does not exist!");
        } else {
            sp.getAppointments().size();
            return sp;
        }
    }

    @Override
    public boolean canBeRated(Long customerId, Long serviceProviderId) throws CustomerNotFoundException, ServiceProviderNotFoundException
    {
        CustomerEntity c = em.find(CustomerEntity.class, customerId);
        ServiceProviderEntity s = retrieveServiceProviderById(serviceProviderId);
        if (c == null) {
            throw new CustomerNotFoundException("Customer does not exist!");
        } else if (s == null) {
            throw new ServiceProviderNotFoundException("Customer does not exist!");
        }

        Query query = em.createQuery("SELECT a FROM AppointmentEntity a WHERE a.customerEntity.customerId = :inCustomerId "
                + "AND a.serviceProviderEntity.serviceProviderId = :inServiceProvId");
        query.setParameter("inCustomerId", customerId);
        query.setParameter("inServiceProvId", serviceProviderId);
        List<AppointmentEntity> appts = query.getResultList();

//        Date currDate = new Date();
//
//        if (appts.isEmpty()) {
//            return false; // cannot rate
//        } else {
//            for (AppointmentEntity a : appts) {
//                Long diffInTime = currDate.getTime() - a.getAppointmentTime().getTime();
//                Long diffInHours = (diffInTime / (1000 * 60 * 60)) % 365;
//                if (diffInHours <= 1) {
//                    return false;
//                }
//            }
//        }
        if (appts.isEmpty())
        {
            return false;
        }
        return true;
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<ServiceProviderEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
