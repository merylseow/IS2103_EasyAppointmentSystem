package customerterminalclient;

import ejb.session.stateless.AppointmentSessionBeanRemote;
import ejb.session.stateless.CategorySessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.ServiceProviderSessionBeanRemote;
import entity.CustomerEntity;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginException;


public class MainApp {
    
    private CustomerSessionBeanRemote customerSessionBeanRemote;
    private AppointmentSessionBeanRemote appointmentSessionBeanRemote;
    private ServiceProviderSessionBeanRemote serviceProviderSessionBeanRemote;
    private CategorySessionBeanRemote categorySessionBeanRemote;
    
    private CustomerOperationModule customerOperationModule;
    private CustomerEntity currentCustomerEntity;
   
 
    public MainApp() 
    {
    }

    public MainApp(CustomerSessionBeanRemote customerSessionBeanRemote, AppointmentSessionBeanRemote appointmentSessionBeanRemote, ServiceProviderSessionBeanRemote serviceProviderSessionBeanRemote, CategorySessionBeanRemote categorySessionBeanRemote)
    {
        this();
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.appointmentSessionBeanRemote = appointmentSessionBeanRemote;
        this.serviceProviderSessionBeanRemote = serviceProviderSessionBeanRemote;
        this.categorySessionBeanRemote = categorySessionBeanRemote;
    }
    
    
    
    public void run() 
    {
        Scanner scanner = new Scanner(System.in);
        Integer response;
        
        try 
        {
            while (true)
            {
                System.out.println("*** Welcome to Customer terminal ***\n");
                System.out.println("1: Registration");
                System.out.println("2: Login");
                System.out.println("3: Exit\n");
                response = 0;

                while(response < 1 || response > 3)
                {
                    System.out.print("> ");
                    response = scanner.nextInt();

                    if (response == 1)
                    {
                        doRegistration();
                    }
                    else if (response == 2)
                    {
                        doLogin();
                        System.out.println("Login successful!\n");

                        customerOperationModule = new CustomerOperationModule(customerSessionBeanRemote, appointmentSessionBeanRemote, serviceProviderSessionBeanRemote, categorySessionBeanRemote, currentCustomerEntity);
                        menuMain();
                    }
                    else if (response == 3)
                    {
                        break; 
                    }
                    else
                    {
                        System.out.println("Invalid option. Please try again.\n");
                    }
                }

                if (response == 3) 
                {
                    break;
                }
            }
        }
        catch (InputMismatchException ex) 
        {
            System.out.println("Invalid input!\n");
        }
        catch (InvalidLoginException ex)
        {
            System.out.println("Invalid login credentials: " + ex.getMessage() + "\n");
        }
    }
    
    private void doRegistration() 
    {
        try 
        {
            Scanner scanner = new Scanner(System.in);
        
            CustomerEntity customerEntity = new CustomerEntity();

            System.out.println("*** Customer terminal :: Registration Operation ***\n");
            
            System.out.print("Enter Identity Number (NRIC or Passport Number)> ");
            String identityNumber = scanner.nextLine().trim().toUpperCase();
            while(identityNumber.length() != 9 || !identityNumber.substring(0,1).matches("[A-Z]") || !identityNumber.substring(1,8).matches("[0-9]+") || !identityNumber.substring(8,9).matches("[A-Z]"))
            {
                if (identityNumber.length() != 9) 
                {
                    System.out.println("Identity Number must be 9 charcters long!\n");
                    System.out.print("Enter Identity Number (NRIC or Passport Number)> ");
                    identityNumber = scanner.nextLine().trim().toUpperCase();
                }
                else if (!identityNumber.substring(0,1).matches("[A-Z]"))
                {
                    System.out.println("Identity Number's first charcter must be an alphabet!\n");
                    System.out.print("Enter Identity Number (NRIC or Passport Number)> ");
                    identityNumber = scanner.nextLine().trim().toUpperCase();
                }
                else if (!identityNumber.substring(1,8).matches("[0-9]+"))
                {
                    System.out.println("Identity Number's second to eigth character must be a digit!\n");
                    System.out.print("Enter Identity Number (NRIC or Passport Number)> ");
                    identityNumber = scanner.nextLine().trim().toUpperCase();
                }
                else if (!identityNumber.substring(8,9).matches("[A-Z]"))
                {
                    System.out.println("Identity Number's last charcter must be an alphabet!\n");
                    System.out.print("Enter Identity Number (NRIC or Passport Number)> ");
                    identityNumber = scanner.nextLine().trim().toUpperCase();
                }
            }
            customerEntity.setIdentityNumber(identityNumber);

            System.out.print("Enter First Name> ");
            String firstName = scanner.nextLine().trim();
            while(firstName.isEmpty() || firstName.length() > 32 || !firstName.matches("^[a-zA-Z\\s']*$"))
            {
                if (firstName.isEmpty()) 
                {
                    System.out.println("First name must not be empty!\n");
                    System.out.print("Enter First Name> ");
                    firstName = scanner.nextLine().trim();
                }
                else if (firstName.length() > 32)
                {
                    System.out.println("First name must be less than 33 characters!\n");
                    System.out.print("Enter First Name> ");
                    firstName = scanner.nextLine().trim();
                }
                else if (firstName.matches("^[a-zA-Z\\s']*$"))
                {
                    System.out.println("First name must only contain alphabets!\n");
                    System.out.print("Enter First Name> ");
                    firstName = scanner.nextLine().trim();
                }
            }
            customerEntity.setFirstName(firstName);

            System.out.print("Enter Last Name> ");
            String lastName = scanner.nextLine().trim();
            while(lastName.isEmpty() || lastName.length() > 32 || !lastName.matches("^[a-zA-Z\\s']*$")) 
            {
                if (lastName.isEmpty()) 
                {
                    System.out.println("Last name must not be empty!\n");
                    System.out.print("Enter Last Name> ");
                    lastName = scanner.nextLine().trim();
                }
                else if (lastName.length() > 32)
                {
                    System.out.println("Last name must be less than 33 characters!\n");
                    System.out.print("Enter Last Name> ");
                    lastName = scanner.nextLine().trim();
                }
                else if (lastName.matches("^[a-zA-Z\\s']*$"))
                {
                    System.out.println("Last name must only contain alphabets!\n");
                    System.out.print("Enter Last Name> ");
                    lastName = scanner.nextLine().trim();
                }
            }
            customerEntity.setLastName(lastName);

            System.out.print("Enter Gender> ");
            Character gender = scanner.nextLine().trim().toUpperCase().charAt(0);
            while (!gender.equals('M') && !gender.equals('F')) 
            {
                System.out.println("Gender must either be male or female!\n");
                System.out.print("Enter Gender> ");
                gender = scanner.nextLine().trim().toUpperCase().charAt(0);
            }
            customerEntity.setGender(gender);

            System.out.print("Enter Age> ");
            Integer age = scanner.nextInt();
            while (age < 18 || age > 80) 
            {
                if (age < 18) 
                {
                    System.out.println("You must be 18 years or older\n");
                    System.out.print("Enter Age> ");
                    age = scanner.nextInt();
                }
                else if (age > 80) 
                {
                    System.out.println("Invalid age\n");
                    System.out.print("Enter Age> ");
                    age = scanner.nextInt();
                }
            }
            customerEntity.setAge(age);
            scanner.nextLine();

            System.out.print("Enter Phone Number> ");
            String phoneNumber = scanner.nextLine().trim();
            while (phoneNumber.length() < 8 || !phoneNumber.matches("[0-9]+")) 
            {
                if (phoneNumber.length() < 8)
                {
                    System.out.println("Phone number must be at least 8 digits long!\n");
                    System.out.print("Enter Phone Number> ");
                    phoneNumber = scanner.nextLine().trim();
                }
                else if (!phoneNumber.matches("[0-9]+"))
                {
                    System.out.println("Phone number must not contain any alphabets!\n");
                    System.out.print("Enter Phone Number> ");
                    phoneNumber = scanner.nextLine().trim();
                }
            }
            customerEntity.setPhoneNumber(phoneNumber);

            System.out.print("Enter Address> ");
            String address = scanner.nextLine().trim();
            while(address.isEmpty() || address.length() > 32) 
            {
                if (address.isEmpty()) 
                {
                    System.out.println("Address must not be empty!\n");
                    System.out.print("Enter Address> ");
                    address = scanner.nextLine().trim();
                }
                else if (address.length() > 32)
                {
                    System.out.println("Address must be less than 33 characters!\n");
                    System.out.print("Enter Address> ");
                    address = scanner.nextLine().trim();
                }
            }
            customerEntity.setAddress(address);

            System.out.print("Enter City> ");
            String city = scanner.nextLine().trim();
            while(city.isEmpty() || city.length() > 32) 
            {
                if (city.isEmpty()) 
                {
                    System.out.println("City must not be empty!\n");
                    System.out.print("Enter City> ");
                    city = scanner.nextLine().trim();
                }
                else if (city.length() > 32)
                {
                    System.out.println("City must be less than 33 characters!\n");
                    System.out.print("Enter City> ");
                    city = scanner.nextLine().trim();
                }
            }
            customerEntity.setCity(city);

            System.out.print("Enter Email Address (This will be your usename)> ");
            String emailAddress = scanner.nextLine().trim();

            String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
            Pattern pattern = Pattern.compile(regex);

            while(emailAddress.isEmpty() || emailAddress.length() > 32 || !pattern.matcher(emailAddress).matches()) 
            {
                if (emailAddress.isEmpty()) 
                {
                    System.out.println("Email address must not be empty!\n");
                    System.out.print("Enter Email address> ");
                    emailAddress = scanner.nextLine().trim();
                }
                else if (emailAddress.length() > 32)
                {
                    System.out.println("Email address must be less than 33 characters!\n");
                    System.out.print("Enter Email address> ");
                    emailAddress = scanner.nextLine().trim();
                }
                else if (!pattern.matcher(emailAddress).matches())
                    {
                        System.out.println("Invalid email address format!\n");
                        System.out.print("Enter Email> ");
                        emailAddress = scanner.nextLine().trim();
                    }
            }
            customerEntity.setEmailAddress(emailAddress);

            System.out.print("Enter Password> ");
            String password = scanner.nextLine().trim();
            while(password.length() != 6 || !password.matches("[0-9]+")) 
            {
                System.out.println("Password must be exactly 6 digits!\n");
                System.out.print("Enter Password> ");
                password = scanner.nextLine().trim();
            }
            customerEntity.setPassword(password);

            customerEntity = customerSessionBeanRemote.createNewCustomer(customerEntity);
            
            System.out.println("You have been registered successfully!\n"); 
            
            System.out.print("Enter 0 to go back to the previous menu.\n> ");
            Integer option = scanner.nextInt();
            while (option != 0) 
            {
                System.out.print("Enter 0 to go back to the previous menu.\n> ");
                option = scanner.nextInt();
            }
            System.out.println();
        }
        catch (InputMismatchException ex) 
        {
            System.out.println("Invalid input!\n");
        } catch (CustomerNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving customer: " + ex.getMessage() + "\n");
        }
    }
    
    private void doLogin() throws InvalidLoginException
    {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";
        
        System.out.println("*** Customer terminal :: Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
        if (username.length() > 0 && password.length() > 0)
        {
            currentCustomerEntity = customerSessionBeanRemote.customerLogin(username, password);
        }
        else
        {
            throw new InvalidLoginException("Either username or password is wrong!\n");
        }
    }
    
    private void menuMain()
    {
        System.out.println("*** Customer terminal :: Main ***\n");
        System.out.println("You are logged in as " + currentCustomerEntity.getName() + "\n");
        customerOperationModule.menuCustomerOperation();
    }
}
