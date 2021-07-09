package customerapplication;


import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import ws.client.CustomerAlreadyExistsException_Exception;
import ws.client.CustomerEntity;
import ws.client.CustomerNotFoundException_Exception;
import ws.client.InputDataValidationException_Exception;
import ws.client.InvalidLoginException;
import ws.client.InvalidLoginException_Exception;
import ws.client.UnknownPersistenceException_Exception;


public class MainApp
{    
    private CustomerOperationModule customerOperationModule;
    private CustomerEntity currentCustomerEntity;
    
    public MainApp()
    {
        customerOperationModule = new CustomerOperationModule();
    }
    
    public void runApp()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while (true)
        {
            System.out.println("*** Welcome to Customer terminal ***\n");
            System.out.println("1: Registration");
            System.out.println("2: Login");
            System.out.println("3: Exit\n");
            
            try
            {
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
                        try
                        {
                            doLogin();
                            System.out.println("Login successful!\n");
                            menuMain();
                        }
                        catch (InvalidLoginException_Exception ex)
                        {
                            System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                        }
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
            catch (NumberFormatException ex)
            {
                System.out.println("Invalid option, please try again!\n");    
            }
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
            while(firstName.isEmpty() || firstName.length() > 32 || !firstName.matches("^[a-zA-Z]*$")) 
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
                else if (!firstName.matches("^[a-zA-Z]*$"))
                {
                    System.out.println("First name must only contain alphabets!\n");
                    System.out.print("Enter First Name> ");
                    firstName = scanner.nextLine().trim();
                }
            }
            customerEntity.setFirstName(firstName);

            System.out.print("Enter Last Name> ");
            String lastName = scanner.nextLine().trim();
            while(lastName.isEmpty() || lastName.length() > 32 || !lastName.matches("^[a-zA-Z]*$")) 
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
                else if (!lastName.matches("^[a-zA-Z]*$"))
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
                System.out.println("Please enter either M or F.\n");
                System.out.print("Enter Gender> ");
                gender = scanner.nextLine().trim().toUpperCase().charAt(0);
            }
            customerEntity.setGender((int)gender);

            System.out.print("Enter Age> ");
            Integer age = scanner.nextInt();
            while (age < 18 || age > 80) 
            {
                if (age < 18) 
                {
                    System.out.println("You must be 18 years or older.\n");
                    System.out.print("Enter Age> ");
                    age = scanner.nextInt();
                }
                else if (age > 80) 
                {
                    System.out.println("Please enter a valid age.\n");
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
                    System.out.println("Phone number must be at least 8 digits long.\n");
                    System.out.print("Enter Phone Number> ");
                    phoneNumber = scanner.nextLine().trim();
                }
                else if (!phoneNumber.matches("[0-9]+"))
                {
                    System.out.println("Phone number must not contain any alphabets.\n");
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
                    System.out.println("Address must not be empty.\n");
                    System.out.print("Enter Address> ");
                    address = scanner.nextLine().trim();
                }
                else if (address.length() > 32)
                {
                    System.out.println("Address must be less than 33 characters.\n");
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
                    System.out.println("City must not be empty.\n");
                    System.out.print("Enter City> ");
                    city = scanner.nextLine().trim();
                }
                else if (city.length() > 32)
                {
                    System.out.println("City must be less than 33 characters.\n");
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
                    System.out.println("Email address must not be empty.\n");
                    System.out.print("Enter Email address> ");
                    emailAddress = scanner.nextLine().trim();
                }
                else if (emailAddress.length() > 32)
                {
                    System.out.println("Email address must be less than 33 characters.\n");
                    System.out.print("Enter Email address> ");
                    emailAddress = scanner.nextLine().trim();
                }
                else if (!pattern.matcher(emailAddress).matches())
                    {
                        System.out.println("Please enter a valid email address format.\n");
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

            createNewCustomer(customerEntity);
            
            System.out.println("You have been registered successfully.\n"); 
            
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
            System.out.println("Invalid input.\n");
        }
        catch (CustomerNotFoundException_Exception ex)
        {
            System.out.println("An error has occurred while retrieving customer: " + ex.getMessage() + "\n");
        } catch (CustomerAlreadyExistsException_Exception ex) {
            System.out.println("Customer already exists!");
        } catch (InputDataValidationException_Exception ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownPersistenceException_Exception ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void doLogin() throws InvalidLoginException_Exception
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
            currentCustomerEntity = customerLogin(username, password);
            customerOperationModule.setCurrentCustomerEntity(currentCustomerEntity);
        }
        else
        {
            InvalidLoginException ex = new InvalidLoginException();
            throw new InvalidLoginException_Exception("Either username or password is wrong!\n", ex);
        }
    }
    
    private void menuMain()
    {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        while (true)
        {
            System.out.println("*** Customer terminal :: Main ***\n");
            System.out.println("You are login as "  + currentCustomerEntity.getFirstName() + " " + currentCustomerEntity.getLastName());
            System.out.println("1: Search Operation");
            System.out.println("2: Add Appointment");
            System.out.println("3: View Appointments");
            System.out.println("4: Cancel Appointment");
            System.out.println("5: Rate Service Provider");
            System.out.println("6: Logout\n");
            
            try 
            {
                response = 0;
                while (response < 1 || response > 6)
                {
                    System.out.print("> ");
                    String string = sc.nextLine();
                    response = Integer.parseInt(string);
                    System.out.println();

                    if (response == 1)
                    {
                        customerOperationModule.doSearch();
                    }
                    else if (response == 2)
                    {
                        customerOperationModule.doAddAppointment();
                    }
                    else if (response == 3)
                    {
                        customerOperationModule.doViewAppointments();
                    }
                    else if (response == 4)
                    {
                        customerOperationModule.doCancelAppointment();
                    }
                    else if (response == 5)
                    {
                        customerOperationModule.doRateServiceProvider();
                    }
                    else if (response == 6)
                    {
                        break;
                    }
                    else
                    {
                        System.out.println("Invalid option, please try again!\n");                
                    }
                }
                if(response == 6)
                {
                    currentCustomerEntity = null;
                    break;
                }
            }
            catch (NumberFormatException ex)
            {
                System.out.println("Invalid option, please try again!\n");    
            }
        }
    }
    
    private static CustomerEntity createNewCustomer(ws.client.CustomerEntity arg0) throws CustomerNotFoundException_Exception, CustomerAlreadyExistsException_Exception, InputDataValidationException_Exception, UnknownPersistenceException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.createNewCustomer(arg0);
    }
    
    
    private static CustomerEntity customerLogin(java.lang.String arg0, java.lang.String arg1) throws InvalidLoginException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.customerLogin(arg0, arg1);
    }

    private static CustomerEntity retrieveCustomerByCustomerId(java.lang.Long arg0) throws CustomerNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.retrieveCustomerByCustomerId(arg0);
    }

    private static CustomerEntity retrieveCustomerByUsername(java.lang.String arg0) throws CustomerNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.retrieveCustomerByUsername(arg0);
    }

    private static void updateCustomer(ws.client.CustomerEntity arg0) throws CustomerNotFoundException_Exception, CustomerAlreadyExistsException_Exception, InputDataValidationException_Exception, UnknownPersistenceException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        port.updateCustomer(arg0);
    }
    
    
}
