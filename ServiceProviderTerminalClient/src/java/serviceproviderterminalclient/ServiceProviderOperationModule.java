package serviceproviderterminalclient;

import ejb.session.stateless.AppointmentSessionBeanRemote;
import ejb.session.stateless.CategorySessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.ServiceProviderSessionBeanRemote;
import entity.AppointmentEntity;
import entity.CategoryEntity;
import entity.CustomerEntity;
import entity.ServiceProviderEntity;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.enumeration.StatusEnum;
import util.exception.AppointmentNotFoundException;
import util.exception.CustomerAlreadyExistsException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.ServiceProviderAlreadyExistsException;
import util.exception.ServiceProviderNotFoundException;
import util.exception.UnknownPersistenceException;


public class ServiceProviderOperationModule
{
    private ServiceProviderSessionBeanRemote serviceProviderSessionBeanRemote;
    private CategorySessionBeanRemote categorySessionBeanRemote;
    private AppointmentSessionBeanRemote appointmentSessionBeanRemote;
    private CustomerSessionBeanRemote customerSessionBeanRemote;
    private ServiceProviderEntity currentServiceProviderEntity;

    public ServiceProviderOperationModule()
    {
    }
    
    
    public ServiceProviderOperationModule(ServiceProviderSessionBeanRemote serviceProviderSessionBeanRemote, CategorySessionBeanRemote categorySessionBeanRemote, AppointmentSessionBeanRemote appointmentSessionBeanRemote, CustomerSessionBeanRemote customerSessionBeanRemote, ServiceProviderEntity currentServiceProviderEntity)
    {
        this();
        this.serviceProviderSessionBeanRemote = serviceProviderSessionBeanRemote;
        this.categorySessionBeanRemote = categorySessionBeanRemote;
        this.appointmentSessionBeanRemote = appointmentSessionBeanRemote;
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.currentServiceProviderEntity = currentServiceProviderEntity;
    }
    
    public void menuServiceProviderOperation()
    {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        try
        {
            while (true)
            {
                System.out.println("1: View profile");
                System.out.println("2: Edit profile");
                System.out.println("3: View Appointments");
                System.out.println("4: Cancel Appointment");
                System.out.println("5: Logout\n");

                response = 0;

                while (response < 1 || response > 5)
                {
                    System.out.print("> ");

                    response = sc.nextInt();
                    System.out.println();

                    if (response == 1)
                    {
                        viewProfile();
                    }
                    else if (response == 2)
                    {
                        editProfile();
                    }
                    else if (response == 3)
                    {
                        viewAppointments();
                    }
                    else if (response == 4)
                    {
                        cancelAppointment();
                    }
                    else if (response == 5)
                    {
                        break;
                    }
                    else
                    {
                        System.out.println("Invalid option, please try again!\n");
                    }
                }

                if (response == 5)
                {
                    break;
                }
            }
        }
        catch (InputMismatchException ex)
        {
            System.out.println("Invalid input!\n");
        }
    }
    
    public void viewProfile()
    {
        Long serviceProvId = currentServiceProviderEntity.getServiceProviderId();
        String name = currentServiceProviderEntity.getName();
        CategoryEntity category = currentServiceProviderEntity.getCategory();
        Long businessRegistrationNo = currentServiceProviderEntity.getRegistrationNumber();
        String city = currentServiceProviderEntity.getCity();
        String address = currentServiceProviderEntity.getAddress();
        String email = currentServiceProviderEntity.getEmail();
        String phone = currentServiceProviderEntity.getPhone();
        String password = currentServiceProviderEntity.getPassword();
        StatusEnum status = currentServiceProviderEntity.getStatus();
        double rating = currentServiceProviderEntity.getRating();
        
        System.out.println("Your service provider ID is: " + serviceProvId);
        System.out.println("Your name is: " + name);
        System.out.println("Your business category is: " + category.getCategoryName());
        System.out.println("Your business registration number is: " + businessRegistrationNo);
        System.out.println("Your city is: " + city);
        System.out.println("Your business address is: " + address);
        System.out.println("Your email address is: " + email);
        System.out.println("Your phone number is: " + phone);
        System.out.println("Your password is: " + password);
        System.out.println("Your current status is: " + status);
        
        if (rating == 0)
        {
            System.out.println("You do not have any ratings currently.\n");
        }
        else
        {
            System.out.println("Your rating is: " + new DecimalFormat("#.#").format(rating) + "\n");
        }
    }
    
    public void editProfile() 
    {
        try
        {
            Scanner sc = new Scanner(System.in);

            boolean flag = true;
            while (flag == true)
            {
                System.out.println("Would you like to change your city? (y/n)");
                System.out.print("> ");
                Character yn = Character.toLowerCase(sc.next().charAt(0));


                if (yn == 'y') 
                {
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("Enter new city: ");
                    String newCity = scanner.nextLine().trim();
                    currentServiceProviderEntity.setCity(newCity);
                    serviceProviderSessionBeanRemote.updateServiceProvider(currentServiceProviderEntity);
                    flag = false;
                }
                else if (yn == 'n') 
                {
                    flag = false;
                }
                else 
                {
                    System.out.println("Invalid input, please try again!\n");
                }
            }

            boolean secondFlag = true;
            while (secondFlag == true)
            {
                System.out.println("Would you like to change your business address? (y/n)");
                System.out.print("> ");
                Character yn = Character.toLowerCase(sc.next().charAt(0));


                if (yn == 'y') 
                {
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("Enter new business address: ");
                    String newAddress = scanner.nextLine().trim();
                    currentServiceProviderEntity.setAddress(newAddress);
                    serviceProviderSessionBeanRemote.updateServiceProvider(currentServiceProviderEntity);
                    secondFlag = false;
                }
                else if (yn == 'n') 
                {
                    secondFlag = false;
                } 
                else 
                {
                    System.out.println("Invalid input, please try again!\n");
                }
            }

            boolean thirdFlag = true;
            while (thirdFlag == true)
            {
                System.out.println("Would you like to change your email address? (y/n)");
                System.out.print("> ");
                Character yn = Character.toLowerCase(sc.next().charAt(0));


                if (yn == 'y') 
                {
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("Enter new email address: ");
                    String newEmail = scanner.nextLine().trim();
                    currentServiceProviderEntity.setEmail(newEmail);
                    serviceProviderSessionBeanRemote.updateServiceProvider(currentServiceProviderEntity);
                    thirdFlag = false;
                }
                else if (yn == 'n') 
                {
                    thirdFlag = false;
                } 
                else 
                {
                    System.out.println("Invalid input, please try again!\n");
                }
            }

            boolean fourthFlag = true;
            while (fourthFlag == true)
            {
                System.out.println("Would you like to change your phone number? (y/n)");
                System.out.print("> ");
                Character yn = Character.toLowerCase(sc.next().charAt(0));


                if (yn == 'y') 
                {
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("Enter new phone number: ");
                    String newPhone = scanner.nextLine().trim();
                    scanner.nextLine();
                    currentServiceProviderEntity.setPhone(newPhone);
                    serviceProviderSessionBeanRemote.updateServiceProvider(currentServiceProviderEntity);
                    fourthFlag = false;
                }
                else if (yn == 'n') 
                {
                    fourthFlag = false;
                } 
                else 
                {
                    System.out.println("Invalid input, please try again!\n");
                }
            }

            boolean fifthFlag = true;
            while (fifthFlag == true)
            {
                System.out.println("Would you like to change your password? (y/n)");
                System.out.print("> ");
                Character yn = Character.toLowerCase(sc.next().charAt(0));


                if (yn == 'y') 
                {
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("Enter new password: ");
                    String newPassword = scanner.nextLine().trim();
                    currentServiceProviderEntity.setPassword(newPassword);
                    serviceProviderSessionBeanRemote.updateServiceProvider(currentServiceProviderEntity);
                    fifthFlag = false;
                }
                else if (yn == 'n') 
                {
                    fifthFlag = false;
                } 
                else 
                {
                    System.out.println("Invalid input, please try again!\n");
                }
            }
            System.out.println("Your profile has been updated succesfully.\n");
        }
        catch (ServiceProviderNotFoundException ex)
        {
            System.out.println("Service provider does not exist!");
        }
        catch (InputDataValidationException ex)
        {
            System.out.println("Invalid input.");
        }
        catch (ServiceProviderAlreadyExistsException ex)
        {
            System.out.println("Service Provider already exists.");
        }
        catch (UnknownPersistenceException ex)
        {
            System.out.println("Unknown persistence exception has occurred.");
        } 
    }
    
    public void viewAppointments() 
    {
        try
        {
            System.out.println("*** Service provider terminal :: View Appointments ***\n");
            System.out.println("Appointments:\n");
            
            System.out.println(String.format("%20s | %10s | %10s | %20s", "Name", "Date", "Time", "Appointment No."));
            
            List<AppointmentEntity> spAppts = appointmentSessionBeanRemote.retrieveServiceProvPendingAppointments(currentServiceProviderEntity.getServiceProviderId());
            
            if (spAppts.isEmpty())
            {
                System.out.println("You have no ongoing or upcoming appointments.\n");
            }
            else   
            {
                for (AppointmentEntity a : spAppts)
                {
                    SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat timeF = new SimpleDateFormat("HH:mm");
                    String fullName = a.getCustomerEntity().getFirstName() + "  " + a.getCustomerEntity().getLastName();
                    System.out.println(String.format("%20s | %10s | %10s | %20s", fullName, dateF.format(a.getAppointmentDate()), timeF.format(a.getAppointmentTime()), a.getAppointmentCode()));
                }
            }
        }
        catch (ServiceProviderNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving service provider: " + ex.getMessage() + "\n");
        }
    }
    
    public void cancelAppointment() 
    {
        try
        {
            int response = -1;
            while (response != 0)
            {
                Scanner sc = new Scanner(System.in);
                System.out.println("*** Service provider terminal :: Cancel Appointments ***\n");
                System.out.println("Appointments:\n");
            
                System.out.println(String.format("%20s | %10s | %10s | %20s", "Name", "Date", "Time", "Appointment No."));
            
                List<AppointmentEntity> spAppts = appointmentSessionBeanRemote.retrieveServiceProvPendingAppointments(currentServiceProviderEntity.getServiceProviderId());
            
                if (spAppts.isEmpty())
                {
                    System.out.println("Service provider has no appointments to be cancelled.\n");
                    System.out.println("Enter 0 to go back to the previous menu.");
                    System.out.print("Exit> ");
                    response = sc.nextInt();
                    System.out.println();
                }
                else
                {
                    for (AppointmentEntity a : spAppts)
                    {
                        SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat timeF = new SimpleDateFormat("HH:mm");
                        String fullName = a.getCustomerEntity().getFirstName() + " " + a.getCustomerEntity().getLastName();
                        System.out.println(String.format("%20s | %10s | %10s | %20s", fullName, dateF.format(a.getAppointmentDate()), timeF.format(a.getAppointmentTime()), a.getAppointmentCode()));
                    }
                    
                    System.out.println("Enter 0 to go back to the previous menu.");
                    System.out.print("Enter Appointment Id> ");
                    String appointmentCode = sc.next();
                    System.out.println();

                    if (appointmentCode.equals("0"))
                    {
                        break;
                    }

                    sc.nextLine();
                
                    AppointmentEntity a = appointmentSessionBeanRemote.retrieveAppointmentByAppointmentCode(appointmentCode);
                    boolean canCancel = appointmentSessionBeanRemote.canBeCancelled(a.getAppointmentId());
                        
                    if (!canCancel)
                    {
                        System.out.println("Appointments can only be cancelled 24 hours before your appointment date.");
                    }
                    else
                    {
                        spAppts.remove(a);
                        currentServiceProviderEntity.setAppointments(spAppts);
                        serviceProviderSessionBeanRemote.updateServiceProvider(currentServiceProviderEntity);

                        CustomerEntity c = a.getCustomerEntity();

                        List<AppointmentEntity> custAppts = appointmentSessionBeanRemote.retrieveCustomerAppointments(c.getCustomerId());
                        custAppts.remove(a);
                        c.setAppointments(custAppts);
                        customerSessionBeanRemote.updateCustomer(c);

                        //delete appointment entity using sessionbean
                        appointmentSessionBeanRemote.deleteAppointment(a.getAppointmentId());
                        System.out.println("Appointment " + a.getAppointmentCode() + " has been canceled successfully.");
                        System.out.println("Enter 0 to go back to the previous menu.");
                        System.out.print("Enter Appointment Id> ");
                        response = sc.nextInt();
                        System.out.println();
                    }
                }
            }
        }
        catch (ServiceProviderNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving service provider: " + ex.getMessage() + "\n");
        }
        catch (AppointmentNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving appointment: " + ex.getMessage() + "\n");
        }
        catch (CustomerNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving customer: " + ex.getMessage() + "\n");
        }
        catch (InputDataValidationException ex)
        {
            System.out.println("Invalid input type.");
        }
        catch (ServiceProviderAlreadyExistsException ex)
        {
            System.out.println("Service Provider already exists.");
        }
        catch (UnknownPersistenceException ex)
        {
            System.out.println("Unknown persistence exception has occurred.");
        }
        catch (CustomerAlreadyExistsException ex)
        {
            System.out.println("Customer already exists.");
        }
    }
//    public void exit() 
//    {
//        System.exit(0);
//    }
}
