package ejb.session.singleton;

import ejb.session.stateless.AdminSessionBeanLocal;
import ejb.session.stateless.AppointmentSessionBeanLocal;
import ejb.session.stateless.CategorySessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.ServiceProviderSessionBeanLocal;
import entity.AdminEntity;
import entity.AppointmentEntity;
import entity.CategoryEntity;
import entity.CustomerEntity;
import entity.ServiceProviderEntity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import static util.enumeration.StatusEnum.APPROVED;
import util.exception.AdminNotFoundException;
import util.exception.AppointmentNotFoundException;
import util.exception.CategoryAlreadyExistsException;
import util.exception.CategoryNotFoundException;
import util.exception.CustomerAlreadyExistsException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.ServiceProviderAlreadyExistsException;
import util.exception.ServiceProviderNotFoundException;
import util.exception.UnknownPersistenceException;


@Singleton
@LocalBean
@Startup

public class DataInitializationSessionBean
{    

    @EJB(name = "AppointmentSessionBeanLocal")
    private AppointmentSessionBeanLocal appointmentSessionBeanLocal;
    
    @PersistenceContext(unitName = "EasyAppointmentSystem-ejbPU")
    private EntityManager em;
    
    @EJB
    private AdminSessionBeanLocal adminSessionBeanLocal;
    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;
    @EJB
    private CategorySessionBeanLocal categorySessionBeanLocal;
    @EJB
    private ServiceProviderSessionBeanLocal serviceProviderSessionBeanLocal;
    
    
    
    
    public DataInitializationSessionBean()
    {
    }
    
    
    @PostConstruct
    public void postConstruct() {
        try {
            categorySessionBeanLocal.retrieveCategoryByCategoryNum(1);
        }
        catch(CategoryNotFoundException ex) {
            initializeData();
        }
    }
    
    
    
    private void initializeData()
    {
        try
        {
            adminSessionBeanLocal.createNewAdmin(new AdminEntity("Admin01", "admin01@easyappointment.com", "001001"));
            
            customerSessionBeanLocal.createNewCustomer(new CustomerEntity("T0012345Q", "Jia", "Jun", 'M', 30, "91234567", "Upper Kent Ridge", "Singapore", "jiajunsayshello@gmail.com", "001001"));
            customerSessionBeanLocal.createNewCustomer(new CustomerEntity("S9871125Q", "Meryl", "Seow", 'F', 25, "90123484", "Orchard Road", "Singapore", "merylseoww@gmail.com", "001001"));
            
            categorySessionBeanLocal.createNewCategory(new CategoryEntity(0, "No category")); // For service providers who are under no category
            categorySessionBeanLocal.createNewCategory(new CategoryEntity(1, "Health"));
            categorySessionBeanLocal.createNewCategory(new CategoryEntity(2, "Fashion"));
            categorySessionBeanLocal.createNewCategory(new CategoryEntity(3, "Education"));
            
            serviceProviderSessionBeanLocal.createNewServiceProvider(new ServiceProviderEntity("Kevin Tan", 1, (long)4123, "Singapore", "Flora Drive", "kevin@yahoo.com", "92345678", "010101", APPROVED, 0.0), 1);
            serviceProviderSessionBeanLocal.createNewServiceProvider(new ServiceProviderEntity("Ruzzel Ong", 2, (long)2127, "Clementi", "Block E", "ruzzel@yahoo.com", "97178777", "010101", APPROVED, 0.0), 2);
            serviceProviderSessionBeanLocal.createNewServiceProvider(new ServiceProviderEntity("Mary Lim", 2, new Long(0010011), "Clementi", "Block D", "mary@yahoo.com", "93714877", "010101", APPROVED, 0.0), 2);
            
            //appointments for Liza
            appointmentSessionBeanLocal.createNewAppointment((long)1, (long)1, new AppointmentEntity("104121030", new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2021-04-12 10:30"), new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2021-04-12 10:30")));
            appointmentSessionBeanLocal.createNewAppointment((long)1, (long)2, new AppointmentEntity("204171230", new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2021-04-17 12:30"), new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2021-04-17 12:30")));
            appointmentSessionBeanLocal.createNewAppointment((long)1, (long)2, new AppointmentEntity("204121530", new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2021-04-12 16:30"), new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2021-04-12 16:30")));
            
            //appointments for Max
            appointmentSessionBeanLocal.createNewAppointment((long)2, (long)1, new AppointmentEntity("104111030", new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2021-04-11 10:30"), new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2021-04-11 10:30")));
            appointmentSessionBeanLocal.createNewAppointment((long)2, (long)2, new AppointmentEntity("204181330", new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2021-04-18 13:30"), new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2021-04-18 13:30")));
        }
        catch (AdminNotFoundException ex)
        {
            System.out.println("Admin does not exist!");
        }
        catch (CustomerNotFoundException ex)
        {
            System.out.println("Customer does not exist!");
        }
        catch (CategoryNotFoundException ex)
        {
            System.out.println("Category does not exist!");
        }
        catch (ServiceProviderNotFoundException ex)
        {
            System.out.println("Service provider does not exist!");
        } 
        catch (ParseException ex) 
        {
            Logger.getLogger(DataInitializationSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (AppointmentNotFoundException ex) 
        {
            System.out.println("Appointment does not exist!");
        } 
        catch (InputDataValidationException ex) 
        {
            System.out.println("Input date not valid!");
        } catch (CustomerAlreadyExistsException ex) {
            Logger.getLogger(DataInitializationSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownPersistenceException ex) {
            Logger.getLogger(DataInitializationSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceProviderAlreadyExistsException ex) {
            Logger.getLogger(DataInitializationSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CategoryAlreadyExistsException ex) {
            Logger.getLogger(DataInitializationSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
}