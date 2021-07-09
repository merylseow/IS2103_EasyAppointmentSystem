package customerterminalclient;

import ejb.session.stateless.AppointmentSessionBeanRemote;
import ejb.session.stateless.CategorySessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.ServiceProviderSessionBeanRemote;
import entity.AppointmentEntity;
import entity.CategoryEntity;
import entity.CustomerEntity;
import entity.ServiceProviderEntity;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import static util.enumeration.StatusEnum.APPROVED;
import util.exception.AppointmentNotFoundException;
import util.exception.CategoryNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.ServiceProviderNotFoundException;


public class CustomerOperationModule
{
    private CustomerSessionBeanRemote customerSessionBeanRemote;
    private AppointmentSessionBeanRemote appointmentSessionBeanRemote;
    private ServiceProviderSessionBeanRemote serviceProviderSessionBeanRemote;
    private CategorySessionBeanRemote categorySessionBeanRemote;
    
    private CustomerEntity currentCustomerEntity;

    
    public CustomerOperationModule()
    {
    }

    public CustomerOperationModule(CustomerSessionBeanRemote customerSessionBeanRemote, AppointmentSessionBeanRemote appointmentSessionBeanRemote, ServiceProviderSessionBeanRemote serviceProviderSessionBeanRemote, CategorySessionBeanRemote categorySessionBeanRemote, CustomerEntity currentCustomerEntity)
    {
        this();
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.appointmentSessionBeanRemote = appointmentSessionBeanRemote;
        this.serviceProviderSessionBeanRemote = serviceProviderSessionBeanRemote;
        this.categorySessionBeanRemote = categorySessionBeanRemote;
        this.currentCustomerEntity = currentCustomerEntity;
    }
    
    public void menuCustomerOperation()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        try 
        {
            while(true)
            {
                System.out.println("1: Search Operation");
                System.out.println("2: Add Appointment");
                System.out.println("3: View Appointments");
                System.out.println("4: Cancel Appointment");
                System.out.println("5: Rate service provider");
                System.out.println("6: Logout\n");

                response = 0;

                while(response < 1 || response > 6)
                {
                    System.out.print("> ");

                    response = scanner.nextInt();
                    System.out.println();

                    if(response == 1)
                    {
                        search();
                    }
                    else if(response == 2)
                    {
                        addAppointment();
                    }
                    else if(response == 3)
                    {
                        viewAppointments();
                    }
                    else if(response == 4)
                    {
                        cancelAppointment();
                    }
                    else if(response == 5)
                    {
                        rateServiceProvider();
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
                    break;
                }
            }
        }
        catch (InputMismatchException ex)
        {
            System.out.println("Invalid input!\n");
        }
    }
    
    public void search()
    {
        try
        {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat timeFormat = new SimpleDateFormat("HH:mm");
            
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("*** Customer terminal :: Search ***\n");
            
            List<CategoryEntity> categories = categorySessionBeanRemote.retrieveAllCategories();
        
            for (int i = 1; i <= categories.size() - 1; i++)
            {
                if (i != categories.size() - 1)
                {
                    System.out.print(i + " " + categories.get(i).getCategoryName() + " | ");
                }
                else
                {
                    System.out.println(i + " " + categories.get(i).getCategoryName());
                }
            }
            
            System.out.print("Enter Business Category> ");
            Integer maxSize = categories.size() - 1;
            Integer catNum = scanner.nextInt();
            scanner.nextLine();
            
            while (catNum == 0 || catNum > maxSize)
            {
                System.out.print("Please enter a valid input between 1 and " + maxSize + ".\n");
                System.out.print("Enter business Category> ");
                catNum = scanner.nextInt();
            }
            
            CategoryEntity category = categories.get(catNum - 1);
            
            System.out.print("Enter City> ");
            String city = scanner.nextLine().trim();
            
            System.out.print("Enter Date (yyyy-MM-dd)> ");
            String dateString = scanner.nextLine().trim();
            
            Date date = dateFormat.parse(dateString);
            
            Date curr = new Date();
            
            List<ServiceProviderEntity> spInCriteria = serviceProviderSessionBeanRemote.retrieveServiceProvidersInCriteria(category.getCategoryNum(), city);
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            
            if (date.before(curr) && date.getDate() != curr.getDate())
            {
                System.out.println("Enter an appointment date that is later than current date.");
            }
            else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) // Remove from list if date is a Sunday
            {
                System.out.println("There are no service providers available on Sunday.");
            }
            else
            {
                if (spInCriteria.isEmpty())
                {
                    System.out.println("There are no service providers that match your search.");
                }
                else
                {
                    System.out.println(String.format("%10s | %20s | %10s | %20s | %10s", "Service Provider Id", "Name", "First Available Time", "Address", "Overall rating"));
                    //System.out.println("Service Provider Id | Name           | First Available Time | Address          | Overall rating");
                    for (ServiceProviderEntity s : spInCriteria)
                    {
                        if (s.getStatus() == APPROVED)
                        {
                            List<Calendar> availSlotsForTheDay = serviceProviderSessionBeanRemote.retrieveAvailableTimeSlots(date, s.getServiceProviderId());

                            // there are available slots
                            if (!availSlotsForTheDay.isEmpty())
                            {
                                Calendar firstAvailTime = serviceProviderSessionBeanRemote.retrieveFirstAvailableTime(date, s.getServiceProviderId());
                                Date timef = firstAvailTime.getTime();
                                String strTime = timeFormat.format(timef);
                                
                                if (s.getRating() == 0.0) 
                                {
                                    System.out.println(String.format("%10s | %20s | %10s | %20s | %10s", s.getServiceProviderId(), s.getName(), strTime, s.getAddress(), "No ratings yet."));
                                    //System.out.println(s.getServiceProviderId() + "                   | " + s.getName() + " | " + strTime + "             | " + s.getAddress() + "         | " + "No rating yet.");
                                } 
                                else 
                                {
                                    System.out.println(String.format("%10s | %20s | %10s | %20s | %10s", s.getServiceProviderId(), s.getName(), strTime, s.getAddress(), new DecimalFormat("#.#").format(s.getRating())));
                                    //System.out.println(s.getServiceProviderId() + "                   | " + s.getName() + " | " + strTime + "             | " + s.getAddress() + "         | " + new DecimalFormat("#.#").format(s.getRating()));
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (CategoryNotFoundException ex)
        {
            System.out.println("Category does not exist!");
        }
        catch (ParseException | InputMismatchException ex)
        {
            System.out.println("Invalid input!\n");
        }
        catch (ServiceProviderNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving service provider: " + ex.getMessage() + "\n");
        }
    }
    
    public void addAppointment()
    {
        try
        {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat timeFormat = new SimpleDateFormat("HH:mm");
            
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("*** Customer terminal :: Add Appointment ***\n");
            
            List<CategoryEntity> categories = categorySessionBeanRemote.retrieveAllCategories();
        
            for (int i = 1; i <= categories.size() - 1; i++)
            {
                if (i != categories.size() - 1)
                {
                    System.out.print(i + " " + categories.get(i).getCategoryName() + " | ");
                }
                else
                {
                    System.out.println(i + " " + categories.get(i).getCategoryName());
                }
            }
            
            System.out.print("Enter Business Category> ");
            Integer catNum = scanner.nextInt();
            scanner.nextLine();
            CategoryEntity category = categories.get(catNum - 1);
            
            System.out.print("Enter city> ");
            String city = scanner.nextLine().trim();
            
            System.out.print("Enter date (yyyy-MM-dd)> ");
            String dateString = scanner.nextLine().trim();
            System.out.println();
            
            List<ServiceProviderEntity> spInCriteria = serviceProviderSessionBeanRemote.retrieveServiceProvidersInCriteria(category.getCategoryNum(), city);
            
            List<Long> availSPId = new ArrayList<>();

            Date date = dateFormat.parse(dateString);
            Date curr = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            
            while ((date.before(curr) && date.getDate() != curr.getDate()) || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) 
            {
                if (date.before(curr) && date.getDate() != curr.getDate()) 
                {
                    System.out.println("Enter an appointment date that is later than current date.\n");
                }
                else 
                {
                    System.out.println("There are no service providers available on Sunday.\n");
                }
                System.out.print("Enter date (yyyy-MM-dd)> ");
                dateString = scanner.nextLine().trim();
                date = dateFormat.parse(dateString);
                cal.setTime(date);
                System.out.println();
            }
                
            if (spInCriteria.isEmpty())
            {
                System.out.println("There are no service providers that match your search.\n");
            }
            else
            {
                System.out.println(String.format("%10s | %20s | %10s | %20s | %10s", "Service Provider Id", "Name", "First Available Time", "Address", "Overall rating"));   
                //System.out.println("Service Provider Id | Name      | First Available Time | Address | Overall rating");
                for (ServiceProviderEntity s : spInCriteria)
                {
                    if (s.getStatus() == APPROVED)
                    {
                        List<Calendar> availSlotsForTheDay = serviceProviderSessionBeanRemote.retrieveAvailableTimeSlots(date, s.getServiceProviderId());
                        // there are available slots
                        if (!availSlotsForTheDay.isEmpty())
                        {
                            Calendar firstAvailTime = serviceProviderSessionBeanRemote.retrieveFirstAvailableTime(date, s.getServiceProviderId());
                            Date timef = firstAvailTime.getTime();
                            String strTime = timeFormat.format(timef);
                            if (s.getRating() == 0.0) 
                            {
                                System.out.println(String.format("%10s | %20s | %10s | %20s | %10s", s.getServiceProviderId(), s.getName(), strTime, s.getAddress(), "No ratings yet."));
                                //System.out.println(s.getServiceProviderId() + "                   | " + s.getName() + " | " + strTime + "             | " + s.getAddress() + "         | " + "No rating yet.");
                            } 
                            else 
                            {
                                System.out.println(String.format("%10s | %20s | %10s | %20s | %10s", s.getServiceProviderId(), s.getName(), strTime, s.getAddress(), new DecimalFormat("#.#").format(s.getRating())));
                                //System.out.println(s.getServiceProviderId() + "                   | " + s.getName() + " | " + strTime + "             | " + s.getAddress() + "         | " + new DecimalFormat("#.#").format(s.getRating()));
                            }
                            availSPId.add(s.getServiceProviderId());
                        }
                    }
                }
            }
            
            if (!spInCriteria.isEmpty()) 
            {
                int response = -1;

                while (response != 0)
                {
                    System.out.println("Enter 0 to go back to the previous menu.");
                    System.out.print("Service Provider Id> ");
                    Long serviceProviderID = scanner.nextLong();
                    System.out.println();

                    if (serviceProviderID == 0L)
                    {
                        break;
                    } 
                    else if (!availSPId.contains(serviceProviderID)) 
                    {
                        System.out.println("Service Provider selected has no available slots!\n");
                        break;
                    }

                    scanner.nextLine();

                    ServiceProviderEntity s = serviceProviderSessionBeanRemote.retrieveServiceProviderById(serviceProviderID);

                    List<Calendar> availSlots = serviceProviderSessionBeanRemote.retrieveAvailableTimeSlots(date, s.getServiceProviderId());

                    System.out.println("Available Appointment slots:");

                    if (availSlots.isEmpty())
                    {
                        System.out.println("There are no more available slots for today.\n");
                        break;
                    }
                    else
                    {
                        String timings = "";

                        for (int i = 0; i < availSlots.size(); i++)
                        {
                            Date timing = availSlots.get(i).getTime();
                            String strTime = timeFormat.format(timing);
                            if (i != availSlots.size() - 1)
                            {
                                timings += strTime + " | ";
                            }
                            else
                            {
                                timings += strTime;
                            }
                        }

                        System.out.println(timings);
                        System.out.println();
                    }

                    System.out.println("Enter 0 to go back to the previous menu.");
                    System.out.print("Enter time (HH:mm)> ");
                    String timeString = scanner.nextLine().trim();
                    System.out.println();

                    if (timeString.equals("0"))
                    {
                        break;
                    }

                    Date time = timeFormat.parse(timeString);
                    Calendar dTime = Calendar.getInstance();
                    dTime.setTime(time);

                    Calendar cTime = Calendar.getInstance();
                    cTime.setTime(date);
                    cTime.set(Calendar.HOUR_OF_DAY, dTime.get(Calendar.HOUR_OF_DAY));
                    cTime.set(Calendar.MINUTE, dTime.get(Calendar.MINUTE));

                    if (availSlots.contains(cTime)) 
                    {
                        appointmentSessionBeanRemote.bookAppointment(s.getServiceProviderId(), currentCustomerEntity.getCustomerId(), date, time);
                        System.out.println("The appointment with " + s.getName() + " at " + timeFormat.format(time) + " on " + dateFormat.format(date) + " is confirmed.\n");
                    }
                    else 
                    {
                        System.out.println("Invalid timing!\n");
                    }

                    System.out.println("Enter 0 to go back to the previous menu.");
                    System.out.print("Exit> ");
                    response = scanner.nextInt();
                    System.out.println();
                }   
            }
        }
        catch (ParseException | InputMismatchException ex)
        {
            System.out.println("Invalid input!\n");
        }
        catch (CategoryNotFoundException ex)
        {
            System.out.println("Category does not exist!\n");
        }
        catch (ServiceProviderNotFoundException ex)
        {
            System.out.println("Service Provider does not exist!\n");
        } 
        catch (CustomerNotFoundException ex) 
        {
            System.out.println("Customer does not exist!\n");
        }
        catch (AppointmentNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving appointment: " + ex.getMessage() + "\n");
        }
    }
        
    public void viewAppointments()
    {
        try
        {
            System.out.println("*** Customer terminal :: View Appointments ***\n");
            
            System.out.println("Appointments:\n");
            System.out.println(String.format("%20s | %20s | %10s | %20s", "Appointment No.", "Date", "Time", "Appointment code"));
            //System.out.println("Appointment No. | Date    | Time");
            
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat time = new SimpleDateFormat("HH:mm");
            
            List<AppointmentEntity> appointments = appointmentSessionBeanRemote.retrieveCustomerAppointments(currentCustomerEntity.getCustomerId());
            
            for (AppointmentEntity appt : appointments)
            {
                System.out.println(String.format("%20s | %20s | %10s | %20s", appt.getAppointmentId(), date.format(appt.getAppointmentDate()), time.format(appt.getAppointmentTime()), appt.getAppointmentCode()));
                //System.out.println(appt.getAppointmentId() + "      | " + date.format(appt.getAppointmentDate()) + " | " + time.format(appt.getAppointmentTime()));
            }
            System.out.println();
        }
        catch (CustomerNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving customer: " + ex.getMessage() + "\n");
        }
    }
    
    public void cancelAppointment()
    {
        try
        {
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("*** Customer terminal :: Cancel Appointment ***\n");
            
            System.out.println("Appointments:\n");
            System.out.println(String.format("%20s | %20s | %10s | %20s", "Appointment No.", "Date", "Time", "Appointment code"));
            //System.out.println("Appointment No. | Date    | Time");
            
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat time = new SimpleDateFormat("HH:mm");
            
            List<AppointmentEntity> custAppts = appointmentSessionBeanRemote.retrieveCustomerAppointments(currentCustomerEntity.getCustomerId());
            
            for (AppointmentEntity a : custAppts)
            {
                System.out.println(String.format("%20s | %20s | %10s | %20s", a.getAppointmentId(), date.format(a.getAppointmentDate()), time.format(a.getAppointmentTime()), a.getAppointmentCode()));
                //System.out.println(a.getAppointmentId() + "      | " + date.format(a.getAppointmentDate()) + " | " + time.format(a.getAppointmentTime()));
            }
            System.out.println();
            
            try
            {
                System.out.print("Enter Appointment Id to cancel appointment> ");
                Long appointmentId = scanner.nextLong();
                scanner.nextLine();
                System.out.println();
                
                AppointmentEntity a = appointmentSessionBeanRemote.retrieveAppointmentByAppointmentId(appointmentId);
                
                //update appointment list in customers
                custAppts.remove(a);
                currentCustomerEntity.setAppointments(custAppts);
                customerSessionBeanRemote.updateCustomer(currentCustomerEntity);
                
                //update appointment list in service provider
                ServiceProviderEntity s = a.getServiceProviderEntity();
                List<AppointmentEntity> spAppts = appointmentSessionBeanRemote.retrieveServiceProvAppointmentsForServiceProv(s.getServiceProviderId());
                spAppts.remove(a);
                s.setAppointments(spAppts);
                serviceProviderSessionBeanRemote.updateServiceProvider(s);
                
                //delete appointment entity using sessionbean
                appointmentSessionBeanRemote.deleteAppointment(appointmentId);
            }
            catch (AppointmentNotFoundException ex)
            {
                System.out.println("Appointment does not exist!\n");
            }
            catch (ServiceProviderNotFoundException ex)
            {
                System.out.println("Service Provider does not exist!\n");
            }
            catch (CustomerNotFoundException ex)
            {
                System.out.println("Customer does not exist!\n");
            }
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input!\n");
            }
        }
        catch (CustomerNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving customer: " + ex.getMessage() + "\n");
        }
    }
    
    public void rateServiceProvider() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** Customer terminal :: Rate Service Provider ***\n");
        
        try
        {
            System.out.print("Enter service provider Id> ");
            Long serviceProviderId = scanner.nextLong();
            scanner.nextLine();
            
            ServiceProviderEntity s = serviceProviderSessionBeanRemote.retrieveServiceProviderById(serviceProviderId);
            
            System.out.print("Enter rating (Out of 5)> ");
            Double rating = scanner.nextDouble();
            scanner.nextLine();
            while (rating < 0 || rating > 5)
            {
                System.out.println("Rating must be between 0 and 5.");
                System.out.print("Enter rating (Out of 5)> ");
                rating = scanner.nextDouble();
                scanner.nextLine();
            }
            System.out.println();
            
            // STORE A LIST OF RATINGS IN SERVICE PROVIDER ENTITY -> RETRIEVE LIST OF RATINGS, ADD CURR RATING, PERSIST
            serviceProviderSessionBeanRemote.rateServiceProvider(serviceProviderId, rating);
            Double newRating = serviceProviderSessionBeanRemote.computeAverageRating(serviceProviderId);
            
            s.setRating(newRating);
            serviceProviderSessionBeanRemote.updateServiceProvider(s);
        }
        catch (ServiceProviderNotFoundException ex)
        {
            System.out.println("Service Provider does not exist!\n");
        }
        catch (InputMismatchException ex)
        {
            System.out.println("Invalid input!\n");
        }
    }
}