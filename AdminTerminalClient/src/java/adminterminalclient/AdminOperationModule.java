package adminterminalclient;

import ejb.session.stateless.AppointmentSessionBeanRemote;
import ejb.session.stateless.CategorySessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.ServiceProviderSessionBeanRemote;
import entity.AppointmentEntity;
import entity.CategoryEntity;
import java.util.Scanner;
import entity.ServiceProviderEntity;
import java.util.List;
import static util.enumeration.StatusEnum.APPROVED;
import static util.enumeration.StatusEnum.BLOCKED;
import static util.enumeration.StatusEnum.PENDING;
import util.exception.CategoryNotFoundException;
import util.exception.ServiceProviderNotFoundException;
import ejb.session.stateless.AdminSessionBeanRemote;
import ejb.session.stateless.EmailSessionBeanRemote;
import entity.CustomerEntity;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import util.exception.CategoryAlreadyExistsException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.ServiceProviderAlreadyExistsException;
import util.exception.UnknownPersistenceException;

public class AdminOperationModule {

    private CustomerSessionBeanRemote customerSessionBeanRemote;
    private ServiceProviderSessionBeanRemote serviceProviderSessionBeanRemote;
    private AdminSessionBeanRemote adminSessionBeanRemote;
    private AppointmentSessionBeanRemote appointmentSessionBeanRemote;
    private CategorySessionBeanRemote categorySessionBeanRemote;
    private EmailSessionBeanRemote emailSessionBeanRemote;

    private Queue queueCheckoutNotification;
    private ConnectionFactory queueCheckoutNotificationFactory;

    public AdminOperationModule() {
    }

    public AdminOperationModule(CustomerSessionBeanRemote customerSessionBeanRemote, ServiceProviderSessionBeanRemote serviceProviderSessionBeanRemote, AdminSessionBeanRemote adminSessionBeanRemote, AppointmentSessionBeanRemote appointmentSessionBeanRemote, CategorySessionBeanRemote categorySessionBeanRemote, EmailSessionBeanRemote emailSessionBeanRemote,
            Queue queueCheckoutNotification, ConnectionFactory queueCheckoutNotificationFactory) {
        this();
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.serviceProviderSessionBeanRemote = serviceProviderSessionBeanRemote;
        this.adminSessionBeanRemote = adminSessionBeanRemote;
        this.appointmentSessionBeanRemote = appointmentSessionBeanRemote;
        this.categorySessionBeanRemote = categorySessionBeanRemote;
        this.emailSessionBeanRemote = emailSessionBeanRemote;
        this.queueCheckoutNotification = queueCheckoutNotification;
        this.queueCheckoutNotificationFactory = queueCheckoutNotificationFactory;
    }

    public void menuAdminOperation() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        {
            while (true) {
                System.out.println("1: View Appointments for customers");
                System.out.println("2: View Appointments for service providers");
                System.out.println("3: View service providers");
                System.out.println("4: Approve service provider");
                System.out.println("5: Block service provider");
                System.out.println("6: Add Business category");
                System.out.println("7: Remove Business category");
                System.out.println("8: Send reminder email");
                System.out.println("9: Logout\n");

                response = 0;
                try {
                    while (response < 1 || response > 9) {
                        System.out.print("> ");

                        String string = scanner.nextLine();
                        response = Integer.parseInt(string);
                        System.out.println();

                        if (response == 1) {
                            doViewCustAppointments();
                        } else if (response == 2) {
                            doViewServiceProvAppointments();
                        } else if (response == 3) {
                            doViewServiceProviders();
                        } else if (response == 4) {
                            doApproveServiceProvider();
                        } else if (response == 5) {
                            doBlockServiceProvider();
                        } else if (response == 6) {
                            doAddBusinessCategory();
                        } else if (response == 7) {
                            doRemoveBusinessCategory();
                        } else if (response == 8) {
                            doSendReminderEmail();
                        } else if (response == 9) {
                            break;
                        } else {
                            System.out.println("Invalid option, please try again!\n");

                        }
                    }

                    if (response == 9) {
                        break;
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
        }
    }

    private void doViewCustAppointments() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");

        System.out.println("*** Admin terminal :: View Appointments for customers ***\n");
        Scanner sc = new Scanner(System.in);
        
        try
        {
            Long customerId = sc.nextLong();
            sc.nextLine();
            System.out.println();
            
            //if (customerId == 0L)
            //{
            //}

                List<AppointmentEntity> custAppts = appointmentSessionBeanRemote.retrieveCustomerAppointments(customerId);
                System.out.println("Appointments:\n");
                    
                System.out.println(String.format("%20s | %20s | %10s | %10s | %20s", "Name", "Business category", "Date", "Time", "Appointment No."));

                if (custAppts.isEmpty())
                {
                    System.out.println("There are no upcoming appointments.\n");
                }
                else
                {
                    for (AppointmentEntity a : custAppts)
                    {
                        String fullName = a.getCustomerEntity().getFirstName() + " " + a.getCustomerEntity().getLastName();
                        CategoryEntity cat = a.getServiceProviderEntity().getCategory();
                        String strDate = dateFormat.format(a.getAppointmentDate());
                        String strTime = timeFormat.format(a.getAppointmentTime());
                        System.out.println(String.format("%20s | %20s | %10s | %10s | %20s", fullName, cat.getCategoryName(), strDate, strTime, a.getAppointmentCode()));
                    }
                    System.out.println();
                }
                
                int response = -1;
                while (response != 0)
                {
                    System.out.println("Enter 0 to go back to the previous menu.");
                    System.out.print("Enter customer Id> ");
                    response = sc.nextInt();
                }
            }
            catch (CustomerNotFoundException ex)
            {
                System.out.println("An error has occurred while retrieving customer: " + ex.getMessage());
            }
        catch (InputMismatchException ex)
        {
            System.out.println("Invalid input! Returning to menu...\n");
        }
    }

    private void doViewServiceProvAppointments() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");

        long serviceProvId = -1;
        System.out.println("*** Admin terminal :: View Appointments for service providers ***\n");

        try {
            while (serviceProvId != 0) {
                try {
                    Scanner scanner = new Scanner(System.in);
                    if (serviceProvId != -1) {
                        System.out.println("Enter 0 to go back to the previous menu.");
                    }
                    System.out.print("Enter service provider Id> ");
                    serviceProvId = scanner.nextLong();
                    scanner.nextLine();
                    System.out.println();

                    if (serviceProvId == 0) {
                        break;
                    }

                    List<AppointmentEntity> spAppts = appointmentSessionBeanRemote.retrieveServiceProvAppointmentsForAdmin(serviceProvId);
                    System.out.println(String.format("%20s | %20s | %10s | %10s | %20s", "Customer name", "Business category", "Date", "Time", "Appointment No."));

                    if (spAppts.isEmpty()) {
                        System.out.println("There are no upcoming appointments.\n");
                    } else {
                        for (AppointmentEntity a : spAppts) {
                            String fullName = a.getCustomerEntity().getFirstName() + " " + a.getCustomerEntity().getLastName();
                            CategoryEntity cat = a.getServiceProviderEntity().getCategory();
                            String strDate = dateFormat.format(a.getAppointmentDate());
                            String strTime = timeFormat.format(a.getAppointmentTime());
                            System.out.println(String.format("%20s | %20s | %10s | %10s | %20s", fullName, cat.getCategoryName(), strDate, strTime, a.getAppointmentCode()));
                        }
                        System.out.println();
                    }
                } catch (ServiceProviderNotFoundException ex) {
                    System.out.println("An error has occurred while retrieving service provider: " + ex.getMessage());
                }
            }
        } catch (InputMismatchException ex) {
            System.out.println("Invalid input! Please try again!\n");
        }
    }

    private void doViewServiceProviders() {
        System.out.println("*** Admin terminal :: View service providers ***\n");
        List<ServiceProviderEntity> serviceProviders = serviceProviderSessionBeanRemote.retrieveAllServiceProviders();
        System.out.println(String.format("%10s | %20s | %20s | %20s | %20s | %20s | %20s | %20s", "Id", "Name", "Business category", "Business Reg. No.", "City", "Address", "Email", "Phone", "Status"));

        for (ServiceProviderEntity s : serviceProviders) {
            System.out.println(String.format("%10s | %20s | %20s | %20s | %20s | %20s | %20s | %20s", s.getServiceProviderId(), s.getName(), s.getCategory().getCategoryName(),
                    s.getRegistrationNumber(), s.getCity(), s.getAddress(), s.getEmail(), s.getPhone(), s.getStatus()));
        }
        System.out.println();
    }

    private void doApproveServiceProvider() {
        Scanner scanner = new Scanner(System.in);
        long serviceProvId = -1;

        try {
            System.out.println("*** Admin terminal :: Approve service provider ***\n");
            System.out.println("List of service providers with pending approval:\n");
            System.out.println(String.format("%10s | %20s | %20s | %20s | %20s | %20s | %20s | %20s", "Id", "Name", "Business category", "Business Reg. No.", "City", "Address", "Email", "Phone", "Status"));

            List<ServiceProviderEntity> pending = serviceProviderSessionBeanRemote.retrievePendingServiceProviders();

            for (ServiceProviderEntity s : pending) {
                System.out.println(String.format("%10s | %20s | %20s | %20s | %20s | %20s | %20s | %20s", s.getServiceProviderId(), s.getName(), s.getCategory().getCategoryName(),
                        s.getRegistrationNumber(), s.getCity(), s.getAddress(), s.getEmail(), s.getPhone(), s.getStatus()));
            }
            System.out.println();

            while (serviceProvId != 0) {
                try {
                    System.out.println("Enter 0 to go back to the previous menu.");
                    System.out.print("Enter service provider Id> ");
                    String string = scanner.nextLine();
                    serviceProvId = Long.parseLong(string);

                    System.out.println();

                    if (serviceProvId == 0) {
                        break;
                    }

                    ServiceProviderEntity serviceProvEntity = serviceProviderSessionBeanRemote.retrieveServiceProviderById(serviceProvId);

                    if (serviceProvEntity.getStatus() == PENDING) {
                        adminSessionBeanRemote.approveServiceProvider(serviceProvId);
                        System.out.println(serviceProvEntity.getName() + "'s registration is approved.");
                    } else if (serviceProvEntity.getStatus() == APPROVED) {
                        System.out.println(serviceProvEntity.getName() + "'s registration is already approved and cannot be approved again.");
                    } else {
                        System.out.println(serviceProvEntity.getName() + "'s registration is blocked and cannot be approved.");
                    }
                } catch (ServiceProviderNotFoundException ex) {
                    System.out.println("An error has occurred while retrieving service provider: " + ex.getMessage());
                }
            }
        } catch (NumberFormatException ex) {
            System.out.println("Invalid input! Please try again!\n");
        }
    }

    private void doBlockServiceProvider() {
        Scanner scanner = new Scanner(System.in);
        long serviceProvId = -1;

        try {
            System.out.println("*** Admin terminal :: Block service provider ***\n");
            System.out.println("List of service providers not blocked:\n");
            System.out.println("Id | Name | Business category | Business Reg. No. | City | Address | Email | Phone");
            List<ServiceProviderEntity> unblocked = serviceProviderSessionBeanRemote.retrieveUnblockedServiceProviders();
            
            for (ServiceProviderEntity s : unblocked)
            {
                System.out.println(String.format("%10s | %20s | %20s | %20s | %20s | %20s | %20s | %20s", s.getServiceProviderId(), s.getName(), s.getCategory().getCategoryName(), 
                        s.getRegistrationNumber(), s.getCity(), s.getAddress(), s.getEmail(), s.getPhone(), s.getStatus()));
            }
            System.out.println();

            while (serviceProvId != 0) {
                try {
                    System.out.println("Enter 0 to go back to the previous menu.");
                    System.out.print("Enter service provider Id> ");
                    String string = scanner.nextLine();
                    serviceProvId = Long.parseLong(string);

                    System.out.println();

                    if (serviceProvId == 0) {
                        break;
                    }

                    ServiceProviderEntity serviceProvEntity = serviceProviderSessionBeanRemote.retrieveServiceProviderById(serviceProvId);

                    if (serviceProvEntity.getStatus() != BLOCKED) {
                        adminSessionBeanRemote.blockServiceProvider(serviceProvId);
                        System.out.println("You have blocked " + serviceProvEntity.getName() + ".");
                    } else {
                        System.out.println(serviceProvEntity.getName() + "has been blocked and cannot be blocked again.");
                    }
                } catch (ServiceProviderNotFoundException ex) {
                    System.out.println("An error has occurred while retrieving service provider: " + ex.getMessage());
                }
            }
        } catch (NumberFormatException ex) {
            System.out.println("Invalid input! Please try again!\n");
        }
    }

    private void doAddBusinessCategory() {
        Scanner scanner = new Scanner(System.in);
        String newCategoryName = "";
        String regex = "^[a-zA-Z]+$";
        Pattern pattern = Pattern.compile(regex);

        System.out.println("*** Admin terminal :: Add a Business category ***\n");

        try {
            while (!newCategoryName.equals("0")) {
                try {
                    CategoryEntity newCategoryEntity = new CategoryEntity();

                    System.out.println("Enter 0 to go back to the previous menu.");
                    System.out.print("Enter a new business category> ");
                    newCategoryName = scanner.nextLine().trim();
                    System.out.println();

                    while (!pattern.matcher(newCategoryName).matches()) {
                        if (newCategoryName.equals("0")) {
                            break;
                        }
                        System.out.println("Invalid business category name!\n");

                        System.out.println("Enter 0 to go back to the previous menu.");
                        System.out.print("Enter a new business category> ");
                        newCategoryName = scanner.nextLine().trim();
                        System.out.println();
                    }

                    if (newCategoryName.equals("0")) {
                        break;
                    }

                    List<CategoryEntity> allCats = categorySessionBeanRemote.retrieveAllCategories();
                    Integer currentIdx = allCats.size() + 1;

                    newCategoryEntity.setCategoryNum(currentIdx - 1);
                    newCategoryEntity.setCategoryName(newCategoryName);

                    CategoryEntity newCategory = categorySessionBeanRemote.createNewCategory(newCategoryEntity);
                    System.out.printf("The business category \"%s\" is added.\n", newCategory.getCategoryName());
                } catch (CategoryNotFoundException ex) {
                    System.out.println("An error has occurred while retrieving category: " + ex.getMessage());
                } catch (InputDataValidationException ex) {
                    Logger.getLogger(AdminOperationModule.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CategoryAlreadyExistsException ex) {
                    System.out.println("Category already exists");
                } catch (UnknownPersistenceException ex) {
                    Logger.getLogger(AdminOperationModule.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (InputMismatchException ex) {
            System.out.println("Invalid input! Returning to menu...\n");
        }
    }

    private void doRemoveBusinessCategory() {
        Scanner scanner = new Scanner(System.in);
        int response = -1;
        String regex = "^[a-zA-Z]+$";
        Pattern pattern = Pattern.compile(regex);

        System.out.println("*** Admin terminal :: Remove a Business category ***\n");

        try {
            while (response != 0) {
                try {
                    List<CategoryEntity> categories = categorySessionBeanRemote.retrieveAllCategories();

                    for (int i = 1; i <= categories.size() - 1; i++) {
                        if (i != categories.size() - 1) {
                            System.out.print(i + " " + categories.get(i).getCategoryName() + " | ");
                        } else {
                            System.out.println(i + " " + categories.get(i).getCategoryName());
                        }
                    }
                    System.out.println();

                    System.out.println("Enter 0 to go back to the previous menu.");
                    System.out.print("Enter Category Name> ");
                    String categoryName = scanner.nextLine();
                    System.out.println();

                    while (!pattern.matcher(categoryName).matches()) {
                        if (categoryName.equals("0")) {
                            break;
                        }
                        System.out.println("Invalid category name!\n");
                        System.out.print("Enter Category Name> ");
                        categoryName = scanner.nextLine().trim();
                        System.out.println();
                    }

                    if (categoryName.equals("0")) {
                        break;
                    }

                    // Accounting for service providers under the deleted category -> Set category to "No category"
                    List<ServiceProviderEntity> underCategory = serviceProviderSessionBeanRemote.retrieveServiceProvidersByCategory(categoryName);
                    for (ServiceProviderEntity s : underCategory) {
                        CategoryEntity c = categorySessionBeanRemote.retrieveCategoryByCategoryNum(0);
                        s.setCategory(c);
                        serviceProviderSessionBeanRemote.updateServiceProvider(s);
                    }
                    categorySessionBeanRemote.deleteCategory(categoryName);
                    System.out.printf("The business category \"%s\" is deleted.\n", categoryName);
                    System.out.println("Enter 0 to go back to the previous menu.");
                    System.out.println("Enter business category> ");
                    response = scanner.nextInt();
                } catch (CategoryNotFoundException ex) {
                    System.out.println("An error has occurred while retrieving category: " + ex.getMessage() + "\n");
                } catch (ServiceProviderNotFoundException ex) {
                    System.out.println("An error has occurred while retrieving service provider: " + ex.getMessage());
                } catch (InputDataValidationException ex) {
                    Logger.getLogger(AdminOperationModule.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ServiceProviderAlreadyExistsException ex) {
                    System.out.println("Service provider already exists!");
                } catch (UnknownPersistenceException ex) {
                    Logger.getLogger(AdminOperationModule.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (InputMismatchException ex) {
            System.out.println("Invalid input! Returning to menu...\n");
        }
    }

    private void doSendReminderEmail() {
        int response = -1;
        while (response != 0) {
            try {
                Scanner sc = new Scanner(System.in);
                System.out.println("Enter 0 to go back to the previous menu.");
                System.out.print("Enter customer Id> ");
                String string = sc.nextLine();
                Long customerId = Long.parseLong(string);
                System.out.println();

                if (customerId == 0L) {
                    break;
                }

                try {
                    CustomerEntity customer = customerSessionBeanRemote.retrieveCustomerByCustomerId(customerId);
                    List<AppointmentEntity> appointmentList = appointmentSessionBeanRemote.retrieveCustomerAppointments(customerId);

                    if (appointmentList.isEmpty()) {
                        System.out.println("There are no new appointments to " + customer.getFirstName() + " " + customer.getLastName() + ".\n");
                    } else {

                        Date date = new Date();
                        List<AppointmentEntity> newList = new ArrayList<>();
                        for (int i = 0; i < appointmentList.size(); i++) {
                            if (appointmentList.get(i).getAppointmentDate().getTime() - date.getTime() > 0) {
                                newList.add(appointmentList.get(i));
                            }
                        }
                       // System.out.println(newList.size());
                        AppointmentEntity earliest = newList.get(0);
                        for (int i = 0; i < newList.size(); i++) {
                            if (earliest.getAppointmentDate().getTime()>newList.get(i).getAppointmentDate().getTime()) {
                                //System.out.println(earliest.getAppointmentDate());
                                earliest = newList.get(i);

                            }

                            //sendJMSMessageToQueueCheckoutNotification(0L, "JJ <jia111jun@gmail.com>", "jiajunsayshello@gmail.com");        
                            //  System.out.println("Enter 0 to go back to the previous menu.");
                            //  System.out.println("Enter customer Id> ");
                            //  response = sc.nextInt();
                        }
                        sendJMSMessageToQueueCheckoutNotification(earliest.getAppointmentId(), "jia111jun@gmail.com", earliest.getCustomerEntity().getEmailAddress());
                        System.out.println("An email is sent to " + customer.getFirstName() + " " + customer.getLastName() + " for the appointment " + earliest.getAppointmentCode() + ".\n");
                    }
                } catch (CustomerNotFoundException ex) {
                    System.out.println("An error has occurred while retrieving customer: " + ex.getMessage() + "\n");
                }
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input! Please try again!\n");
            }
        }
    }

    private void sendJMSMessageToQueueCheckoutNotification(Long appointmentId, String fromEmailAddress, String toEmailAddress) {
        Connection connection = null;
        Session session = null;
        try {
            connection = queueCheckoutNotificationFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MapMessage mapMessage = session.createMapMessage();
            mapMessage.setString("fromEmailAddress", fromEmailAddress);
            mapMessage.setString("toEmailAddress", toEmailAddress);
            mapMessage.setLong("appointmentId", appointmentId);
            MessageProducer messageProducer = session.createProducer(queueCheckoutNotification);
            messageProducer.send(mapMessage);
        } catch (JMSException ex) {
            Logger.getLogger(AdminOperationModule.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot close session", e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException ex) {
                    Logger.getLogger(AdminOperationModule.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
