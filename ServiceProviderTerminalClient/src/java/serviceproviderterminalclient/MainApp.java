package serviceproviderterminalclient;

import ejb.session.stateless.AppointmentSessionBeanRemote;
import ejb.session.stateless.CategorySessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.EmailSessionBeanRemote;
import ejb.session.stateless.ServiceProviderSessionBeanRemote;
import entity.CategoryEntity;
import entity.ServiceProviderEntity;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import static util.enumeration.StatusEnum.PENDING;
import util.exception.CategoryNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginException;
import util.exception.ServiceProviderAlreadyExistsException;
import util.exception.ServiceProviderNotFoundException;
import util.exception.UnknownPersistenceException;

public class MainApp {

    private CustomerSessionBeanRemote customerSessionBeanRemote;
    private AppointmentSessionBeanRemote appointmentSessionBeanRemote;
    private ServiceProviderSessionBeanRemote serviceProviderSessionBeanRemote;
    private CategorySessionBeanRemote categorySessionBeanRemote;
    private EmailSessionBeanRemote emailSessionBeanRemote;

    private static Queue queueCheckoutNotification;
    private static ConnectionFactory queueCheckoutNotificationFactory;

    private ServiceProviderOperationModule serviceProviderOperationModule;
    private ServiceProviderEntity currentServiceProviderEntity;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public MainApp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public MainApp(CustomerSessionBeanRemote customerSessionBeanRemote, AppointmentSessionBeanRemote appointmentSessionBeanRemote, ServiceProviderSessionBeanRemote serviceProviderSessionBeanRemote, CategorySessionBeanRemote categorySessionBeanRemote, EmailSessionBeanRemote emailSessionBeanRemote, Queue queueCheckoutNotification, ConnectionFactory queueCheckoutNotificationFactory) {
        this();
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.appointmentSessionBeanRemote = appointmentSessionBeanRemote;
        this.serviceProviderSessionBeanRemote = serviceProviderSessionBeanRemote;
        this.categorySessionBeanRemote = categorySessionBeanRemote;
        this.emailSessionBeanRemote = emailSessionBeanRemote;
        this.queueCheckoutNotification = queueCheckoutNotification;
        this.queueCheckoutNotificationFactory = queueCheckoutNotificationFactory;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        Integer response;

        try {
            while (true) {
                System.out.println("*** Welcome to Service provider terminal ***\n");
                System.out.println("1: Registration");
                System.out.println("2: Login");
                System.out.println("3: Exit\n");
                response = 0;

                while (response < 1 || response > 3) {
                    System.out.print("> ");
                    response = scanner.nextInt();

                    if (response == 1) {
                        doRegistration();
                    } else if (response == 2) {
                        try {
                            doLogin();
                            System.out.println("Login successful!\n");

                            serviceProviderOperationModule = new ServiceProviderOperationModule(serviceProviderSessionBeanRemote, categorySessionBeanRemote, appointmentSessionBeanRemote, customerSessionBeanRemote, currentServiceProviderEntity);
                            menuMain();
                        } catch (InvalidLoginException ex) {
                            System.out.println("Invalid login credentials: " + ex.getMessage() + "\n");
                        }
                    } else if (response == 3) {
                        break;
                    } else {
                        System.out.println("Invalid option. Please try again.\n");
                    }
                }

                if (response == 3) {
                    break;
                }
            }
        } catch (InputMismatchException ex) {
            System.out.println("Invalid input! Exiting...\n");
        }
    }

    private void doRegistration() {
        try {
            Scanner scanner = new Scanner(System.in);

            ServiceProviderEntity serviceProvider = new ServiceProviderEntity();

            Set<ConstraintViolation<ServiceProviderEntity>> constraintViolations = validator.validate(serviceProvider);

            System.out.println("*** Service Provider Terminal :: Registration Operation ***\n");

            System.out.print("Enter Name> ");
            String name = scanner.nextLine().trim();
            while (name.isEmpty() || name.length() > 32 || !name.matches("^[a-zA-Z\\s']*$")) {
                if (name.isEmpty()) {
                    System.out.print("Name must not be empty!\n");
                    System.out.print("Enter Name> ");
                    name = scanner.nextLine().trim();
                } else if (name.length() > 32) {
                    System.out.print("Name must be less than 33 characters!\n");
                    System.out.print("Enter Name> ");
                    name = scanner.nextLine().trim();
                } else if (!name.matches("^[a-zA-Z]*$")) {
                    System.out.println("Name must only contain alphabets!\n");
                    System.out.print("Enter Name> ");
                    name = scanner.nextLine().trim();
                }
            }
            serviceProvider.setName(name);
            serviceProvider.setStatus(PENDING);

            List<CategoryEntity> categories = categorySessionBeanRemote.retrieveAllCategories();

            for (int i = 1; i <= categories.size() - 1; i++) {
                if (i != categories.size() - 1) {
                    System.out.print(i + " " + categories.get(i).getCategoryName() + " | ");
                } else {
                    System.out.println(i + " " + categories.get(i).getCategoryName());
                }
            }
            Integer maxSize = categories.size() - 1;
            System.out.print("Enter Business Category> ");
            Integer inputCat = scanner.nextInt();

            while (inputCat == 0 || inputCat > maxSize) {
                System.out.print("Please enter a valid input between 1 and " + maxSize + ".\n");
                System.out.print("Enter business Category> ");
                inputCat = scanner.nextInt();
            }

            CategoryEntity cat = categories.get(inputCat - 1);
            CategoryEntity c = categorySessionBeanRemote.retrieveCategoryByCategoryNum(cat.getCategoryNum());
            serviceProvider.setCategory(c);

            System.out.print("Enter Business Registration Number> ");
            Long bizRegNum = scanner.nextLong();
            scanner.nextLine();

            serviceProvider.setRegistrationNumber(bizRegNum);

            System.out.print("Enter City> ");
            String city = scanner.nextLine().trim();
            while (city.isEmpty() || city.length() > 32) {
                if (city.isEmpty()) {
                    System.out.print("City must not be empty!\n");
                    System.out.print("Enter City> ");
                    city = scanner.nextLine().trim();
                } else if (city.length() > 32) {
                    System.out.print("City must be less than 33 characters!\n");
                    System.out.print("Enter City> ");
                    city = scanner.nextLine().trim();
                }
            }
            serviceProvider.setCity(city);

            System.out.print("Enter Phone Number> ");
            String phoneNumber = scanner.nextLine().trim();
            while (phoneNumber.length() < 8 || !phoneNumber.matches("[0-9]+")) {
                if (phoneNumber.length() < 8) {
                    System.out.print("Phone number must be at least 8 digits long!\n");
                    System.out.print("Enter Phone Number> ");
                    phoneNumber = scanner.nextLine().trim();
                } else if (!phoneNumber.matches("[0-9]+")) {
                    System.out.print("Phone number must not contain any alphabets or spacings.\n");
                    System.out.print("Enter Phone Number> ");
                    phoneNumber = scanner.nextLine().trim();
                }
            }
            serviceProvider.setPhone(phoneNumber);

            System.out.print("Enter Business Address> ");
            String address = scanner.nextLine().trim();
            while (address.isEmpty() || address.length() > 32) {
                if (address.isEmpty()) {
                    System.out.print("Business Address must not be empty!\n");
                    System.out.print("Enter Business Address> ");
                    address = scanner.nextLine().trim();
                } else if (address.length() > 32) {
                    System.out.println("Business Address must be less than 33 characters!\n");
                    System.out.print("Enter Business Address> ");
                    address = scanner.nextLine().trim();
                }
            }
            serviceProvider.setAddress(address);

            System.out.print("Enter Email> ");
            String email = scanner.nextLine().trim();

            String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
            Pattern pattern = Pattern.compile(regex);

            while (email.isEmpty() || email.length() > 32 || !pattern.matcher(email).matches()) {
                if (email.isEmpty()) {
                    System.out.print("Email must not be empty!\n");
                    System.out.print("Enter Email> ");
                    email = scanner.nextLine().trim();
                } else if (email.length() > 32) {
                    System.out.print("Email must be less than 33 characters!\n");
                    System.out.print("Enter Email> ");
                    email = scanner.nextLine().trim();
                } else if (!pattern.matcher(email).matches()) {
                    System.out.print("Please enter a valid email format.\n");
                    System.out.print("Enter Email> ");
                    email = scanner.nextLine().trim();
                }
            }
            serviceProvider.setEmail(email);

            System.out.print("Enter Password> ");
            String password = scanner.nextLine().trim();
            while (password.length() != 6 || !password.matches("[0-9]+")) {
                System.out.print("Password must be exactly 6 digits and can only contain numbers.\n");
                System.out.print("Enter Password> ");
                password = scanner.nextLine().trim();
            }
            serviceProvider.setPassword(password);
            
            //System.out.print(constraintViolations.size());

            if (constraintViolations.size() == 0) {
                try {
                    //System.out.println("yo");
                    serviceProvider = serviceProviderSessionBeanRemote.createNewServiceProvider(serviceProvider, cat.getCategoryNum());
                    System.out.println("You have been registered successfully!\n");
                } catch (InputDataValidationException ex) {
                    System.out.println(ex.getMessage() + "\n");
                } catch (ServiceProviderAlreadyExistsException ex) {
                    System.out.println("Service Provider already exists\n");
                } catch (UnknownPersistenceException ex) {
                    Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                //System.out.println("hi");
                showInputDataValidationErrorsForServiceProviderEntity(constraintViolations);
            }

            System.out.print("Enter 0 to go back to the previous menu.\n> ");
            Integer option = scanner.nextInt();
            while (option != 0) {
                System.out.print("Enter 0 to go back to the previous menu.\n> ");
                option = scanner.nextInt();
            }
            System.out.println();
        } catch (CategoryNotFoundException ex) {
            System.out.println("Category does not exist!\n");
        } catch (InputMismatchException ex) {
            System.out.println("Invalid input!\n");
        } catch (ServiceProviderNotFoundException ex) {
            System.out.println("An error has occurred while retrieving service provider: " + ex.getMessage() + "\n");
        }
    }

    private void doLogin() throws InvalidLoginException {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";

        System.out.println("*** Service provider terminal :: Login ***\n");
        System.out.print("Enter Email Address> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        System.out.println();

        if (username.length() > 0 && password.length() > 0) {
            currentServiceProviderEntity = serviceProviderSessionBeanRemote.serviceProviderEntityLogin(username, password);
        } else {
            throw new InvalidLoginException("Either email address or password is wrong!\n");
        }
    }

    private void menuMain() {
        System.out.println("*** Service provider terminal :: Main ***\n");
        System.out.println("You are logged in as " + currentServiceProviderEntity.getName() + "\n");
        serviceProviderOperationModule.menuServiceProviderOperation();
    }

    private void showInputDataValidationErrorsForServiceProviderEntity(Set<ConstraintViolation<ServiceProviderEntity>> constraintViolations) {
        System.out.println("\nInput data validation error!: Service Provider with unique fields already exist in system!");

        for (ConstraintViolation constraintViolation : constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
}
