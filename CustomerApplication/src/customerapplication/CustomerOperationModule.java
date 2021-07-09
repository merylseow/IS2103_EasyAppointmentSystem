package customerapplication;


import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ws.client.AppointmentEntity;
import ws.client.AppointmentNotFoundException_Exception;
import ws.client.CategoryEntity;
import ws.client.CategoryNotFoundException_Exception;
import ws.client.CustomerAlreadyExistsException_Exception;
import ws.client.CustomerEntity;
import ws.client.CustomerNotFoundException;
import ws.client.CustomerNotFoundException_Exception;
import ws.client.InputDataValidationException_Exception;
import ws.client.ParseException_Exception;
import ws.client.ServiceProviderAlreadyExistsException_Exception;
import ws.client.ServiceProviderEntity;
import ws.client.ServiceProviderNotFoundException_Exception;
import static ws.client.StatusEnum.APPROVED;
import ws.client.UnknownPersistenceException_Exception;



public class CustomerOperationModule
{
    Scanner sc;
    private CustomerEntity currentCustomerEntity;

    
    public CustomerOperationModule()
    {
        sc = new Scanner(System.in);
    }
    
    public void setCurrentCustomerEntity(CustomerEntity customerEntity)
    {
        this.currentCustomerEntity = customerEntity;
    }
    
    public void doSearch()
    {
        try
        {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            //DateFormat timeFormat = new SimpleDateFormat("HH:mm");
            
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("*** Customer terminal :: Search ***\n");
            
            List<CategoryEntity> categories = retrieveAllCategories();
        
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
            
            CategoryEntity category = categories.get(catNum);
            
            System.out.print("Enter City> ");
            String city = scanner.nextLine().trim();
            
            System.out.print("Enter Date (yyyy-MM-dd)> ");
            String dateString = scanner.nextLine().trim();
            
            Date date = dateFormat.parse(dateString);
            
            List<ServiceProviderEntity> spInCriteria = retrieveServiceProvidersInCriteria(category.getCategoryNum(), city);
            
            Date curr = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            
            if (date.before(curr) && date.getDate() != curr.getDate())
            {
                System.out.println("Enter an appointment date that is later than current date.\n");
            }
            else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) // Remove from list if date is a Sunday
            {
                System.out.println("There are no service providers available on Sunday.\n");
            }
            else
            {
                if (spInCriteria.isEmpty())
                {
                    System.out.println("There are no service providers that match your search.\n");
                }
                else
                {
                    System.out.println(String.format("%10s | %20s | %10s | %20s | %10s", "Service Provider Id", "Name", "First Available Time", "Address", "Overall rating"));
                    
                    //XMLGregorianCalendar dateGC = null;
                    //GregorianCalendar gc = new GregorianCalendar();
                    //gc.setTime(date);
                    //dateGC = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
                    
                    for (ServiceProviderEntity s : spInCriteria)
                    {
                        if (s.getStatus() == APPROVED)
                        {
                            List<String> availSlotsForTheDay = retrieveAvailableTimeSlotsString(dateString, s.getServiceProviderId());

                            // there are available slots
                            if (!availSlotsForTheDay.isEmpty())
                            {
                                String firstAvailTime = retrieveFirstAvailableTimeString(dateString, s.getServiceProviderId());
                                
                                if (s.getRating() == 0.0) 
                                {
                                    System.out.println(String.format("%19s | %20s | %20s | %20s | %10s", s.getServiceProviderId(), s.getName(), firstAvailTime, s.getAddress(), "No ratings yet."));
                                } 
                                else 
                                {
                                    System.out.println(String.format("%19s | %20s | %20s | %20s | %10s", s.getServiceProviderId(), s.getName(), firstAvailTime, s.getAddress(), new DecimalFormat("#.#").format(s.getRating())));
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (CategoryNotFoundException_Exception ex)
        {
            System.out.println("Category does not exist!\n");
        }
        catch (ParseException | InputMismatchException ex)
        {
            System.out.println("Invalid input!\n");
        }
        catch (ServiceProviderNotFoundException_Exception ex)
        {
            System.out.println("An error has occurred while retrieving service provider: " + ex.getMessage() + "\n");
        }
//        catch (DatatypeConfigurationException ex)
//        {
//            System.out.println("Data configuration exception!");
//        }
        catch (ParseException_Exception ex)
        {
            System.out.println("Wrong input format.\n");
        }
    }
    
    public void doAddAppointment()
    {
        try
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat timeFormat = new SimpleDateFormat("HH:mm");
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("*** Customer terminal :: Add Appointment ***\n");
            
            List<CategoryEntity> categories = retrieveAllCategories();
        
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
            CategoryEntity category = categories.get(catNum);
            
            System.out.print("Enter city> ");
            String city = scanner.nextLine().trim();
            
            System.out.print("Enter date (yyyy-MM-dd)> ");
            String dateString = scanner.nextLine().trim();
            System.out.println();
            
            List<ServiceProviderEntity> spInCriteria = retrieveServiceProvidersInCriteria(category.getCategoryNum(), city);
            
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
                
                for (ServiceProviderEntity s : spInCriteria)
                {
                    if (s.getStatus() == APPROVED)
                    {
                        List<String> availSlotsForTheDay = retrieveAvailableTimeSlotsString(dateString, s.getServiceProviderId());
                        //List<String> occupiedSlots = retrieveOccupiedSlotsString(dateString, s.getServiceProviderId());
                        
                        // Service provider has available slots
                        if (!availSlotsForTheDay.isEmpty())
                        {
                            String firstAvailTime = retrieveFirstAvailableTimeString(dateString, s.getServiceProviderId());
                            if (s.getRating() == 0.0) 
                            {
                                System.out.println(String.format("%19s | %20s | %20s | %20s | %10s", s.getServiceProviderId(), s.getName(), firstAvailTime, s.getAddress(), "No ratings yet."));
                            } 
                            else 
                            {
                                System.out.println(String.format("%19s | %20s | %20s | %20s | %10s", s.getServiceProviderId(), s.getName(), firstAvailTime, s.getAddress(), new DecimalFormat("#.#").format(s.getRating())));
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
                    Long serviceProviderId = scanner.nextLong();
                    System.out.println();

                    if (serviceProviderId == 0L)
                    {
                        break;
                    }
                    else if (!availSPId.contains(serviceProviderId)) 
                    {
                        System.out.println("Service Provider selected has no available slots.\n");
                        break;
                    }

                    scanner.nextLine();

                    ServiceProviderEntity s = retrieveServiceProviderById(serviceProviderId);
                    List<String> availSlots = retrieveAvailableTimeSlotsString(dateString, s.getServiceProviderId());

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
                            String timing = availSlots.get(i);
                            if (i != availSlots.size() - 1)
                            {
                                timings += timing + " | ";
                            }
                            else
                            {
                                timings += timing;
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

                    //Wrong date but correct timing: eg. Thu Jan 01 08:30:00 SGT 1970
                    Date time = timeFormat.parse(timeString);
                    Calendar dTime = Calendar.getInstance();
                    dTime.setTime(time);
                    System.out.println(dTime.getTime());

                    //Set the correct date with correct timing: eg. Thu Apr 15 08:30:00 SGT 2021
                    Calendar cTime = Calendar.getInstance();
                    cTime.setTime(date);
                    cTime.set(Calendar.HOUR_OF_DAY, dTime.get(Calendar.HOUR_OF_DAY));
                    cTime.set(Calendar.MINUTE, dTime.get(Calendar.MINUTE));
                    System.out.println(cTime.getTime());
                    
                    Date calDate = cTime.getTime();
                    String cDateStr = dateFormat.format(calDate);
                    String cTimeStr = timeFormat.format(calDate);

                    if (availSlots.contains(cTimeStr)) 
                    {
                        bookAppointment(s.getServiceProviderId(), currentCustomerEntity.getCustomerId(), cDateStr, cTimeStr);
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
        catch (CategoryNotFoundException_Exception ex)
        {
            System.out.println("Category does not exist!\n");
        }
        catch (ServiceProviderNotFoundException_Exception ex)
        {
            System.out.println("Service Provider does not exist!\n");
        } 
        catch (CustomerNotFoundException_Exception ex) 
        {
            System.out.println("Customer does not exist!\n");
        }
        catch (AppointmentNotFoundException_Exception ex)
        {
            System.out.println("An error has occurred while retrieving appointment: " + ex.getMessage() + "\n");
        }
        catch (ParseException_Exception ex)
        {
            System.out.println("Invalid input!\n");
        } catch (InputDataValidationException_Exception ex) {
            Logger.getLogger(CustomerOperationModule.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceProviderAlreadyExistsException_Exception ex) {
            System.out.println("Service Provider already exists!");
        } catch (UnknownPersistenceException_Exception ex) {
            Logger.getLogger(CustomerOperationModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void doViewAppointments()
    {
        try
        {
            System.out.println("*** Customer terminal :: View Appointments ***\n");
            
            System.out.println("Appointments:\n");
            System.out.println(String.format("%20s | %20s | %10s | %20s", "Appointment No.", "Date", "Time", "Appointment code"));
            
            SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");
            List<AppointmentEntity> appointments = retrieveCustomerAppointments(currentCustomerEntity.getCustomerId());
            
            for (AppointmentEntity appt : appointments)
            {
                String dateStr = getAppointmentDateString(appt.getAppointmentId());
                String timeStr = getAppointmentTimeString(appt.getAppointmentId());
                System.out.println(String.format("%20s | %20s | %10s | %20s", appt.getAppointmentId(), dateStr, timeStr, appt.getAppointmentCode()));
            }
            System.out.println();
            int response = -1;

            while (response != 0)
            {
                System.out.println("Enter 0 to go back to the previous menu.");
                System.out.print("Exit> ");
                response = sc.nextInt();
            }
        }
        catch (CustomerNotFoundException_Exception ex)
        {
            System.out.println("An error has occurred while retrieving customer: " + ex.getMessage() + "\n");
        }
        catch (AppointmentNotFoundException_Exception ex)
        {
            System.out.println("An error has occurred while retrieving appointment: " + ex.getMessage() + "\n");
        }
    }
    

    public void doCancelAppointment()
    {
        try
        {
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("*** Customer terminal :: Cancel Appointment ***\n");
            
            System.out.println("Appointments:\n");
            System.out.println(String.format("%20s | %20s | %10s | %20s", "Appointment No.", "Date", "Time", "Appointment code"));
            
            SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd");
            
            List<AppointmentEntity> custAppts = retrieveCustomerAppointments(currentCustomerEntity.getCustomerId());
            
            for (AppointmentEntity a : custAppts)
            {
                String dateStr = getAppointmentDateString(a.getAppointmentId());
                String timeStr = getAppointmentTimeString(a.getAppointmentId());
                System.out.println(String.format("%20s | %20s | %10s | %20s", a.getAppointmentId(), dateStr, timeStr, a.getAppointmentCode()));
            }
            System.out.println();
            
            try
            {
                int response = -1;
                    
                    while (response != 0)
                    {
                        System.out.println("Enter 0 to go back to the previous menu.");
                        System.out.print("Enter Appointment Id> ");
                        Long appointmentId = sc.nextLong();
                        System.out.println();

                        if (appointmentId == 0L)
                        {
                            break;
                        }

                        sc.nextLine();

                        boolean canCancel = canBeCancelled(appointmentId);
                        
                        if (!canCancel)
                        {
                            System.out.println("Appointments can only be cancelled 24 hours before your appointment date.\n");
                        }
                        else
                        {
                            cancelAppointment(appointmentId);
                            System.out.println("Your appointment has been cancelled.");
                            System.out.println();
                        }
                        
                        System.out.println("Enter 0 to go back to the previous menu.");
                        System.out.print("Exit> ");
                        response = scanner.nextInt();
                }
            }
            catch (ServiceProviderNotFoundException_Exception ex)
            {
                System.out.println("An error has occurred while retrieving service provider: " + ex.getMessage() + "\n");
            }
            catch (CustomerNotFoundException_Exception ex)
            {
                System.out.println("An error has occurred while retrieving customer: " + ex.getMessage() + "\n");
            }
        }
        catch (CustomerNotFoundException_Exception ex)
        {
            System.out.println("An error has occurred while retrieving customer: " + ex.getMessage() + "\n");
        }
        catch (AppointmentNotFoundException_Exception ex)
        {
            System.out.println("An error has occurred while retrieving appointment: " + ex.getMessage() + "\n");
        }
    }
    
    
    public void doRateServiceProvider()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** Customer terminal :: Rate Service Provider ***\n");
        
        try
        {
            System.out.print("Enter service provider Id> ");
            Long serviceProviderId = scanner.nextLong();
            scanner.nextLine();
            
            ServiceProviderEntity s = retrieveServiceProviderById(serviceProviderId);
            
            boolean canRate = canBeRated(currentCustomerEntity.getCustomerId(), serviceProviderId);
            
            if (!canRate)
            {
                System.out.println("You cannot rate as you do not have any past appointments with service provider " + s.getName() + ".");
            }
            else
            {
                System.out.print("Enter rating (1 to 5)> ");
                Double rating = scanner.nextDouble();
                scanner.nextLine();
                while (rating < 1 || rating > 5)
                {
                    System.out.println("Rating must be between 1 and 5.");
                    System.out.print("Enter rating (1 to 5)> ");
                    rating = scanner.nextDouble();
                    scanner.nextLine();
                }
                System.out.println();
            
                // STORE A LIST OF RATINGS IN SERVICE PROVIDER ENTITY -> RETRIEVE LIST OF RATINGS, ADD CURR RATING
                rateServiceProvider(serviceProviderId, rating);
                Double newRating = computeAverageRating(serviceProviderId);
                
                s.setRating(newRating);
                updateServiceProvider(s);

                System.out.println("Thank you for rating!");
                System.out.println();
                int response = -1;

                while (response != 0)
                {
                    System.out.println("Enter 0 to go back to the previous menu.");
                    System.out.print("Exit> ");
                    response = scanner.nextInt();
                }
            }  
        }
        catch (ServiceProviderNotFoundException_Exception ex)
        {
            System.out.println("An error has occurred while retrieving service provider: " + ex.getMessage() + "\n");
        }
        catch (InputMismatchException ex)
        {
            System.out.println("Invalid input!\n");
        }
        catch (CustomerNotFoundException_Exception ex)
        {
            System.out.println("An error has occurred while retrieving customer: " + ex.getMessage() + "\n");
        } catch (InputDataValidationException_Exception ex) {
            Logger.getLogger(CustomerOperationModule.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceProviderAlreadyExistsException_Exception ex) {
            System.out.println("Service Provider already exists");
        } catch (UnknownPersistenceException_Exception ex) {
            Logger.getLogger(CustomerOperationModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static CategoryEntity retrieveCategoryByCategoryNum(java.lang.Integer catNum) throws CategoryNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.retrieveCategoryByCategoryNum(catNum);
    }

    private static java.util.List<ws.client.ServiceProviderEntity> retrieveServiceProvidersInCriteria(java.lang.Integer category, java.lang.String city) throws CategoryNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.retrieveServiceProvidersInCriteria(category, city);
    }

    private static ServiceProviderEntity retrieveServiceProviderById(java.lang.Long serviceProviderId) throws ServiceProviderNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.retrieveServiceProviderById(serviceProviderId);
    }

    private static void deleteAppointment(java.lang.Long appointmentId) throws AppointmentNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        port.deleteAppointment(appointmentId);
    }

    private static void rateServiceProvider(java.lang.Long serviceProvId, java.lang.Double rating) throws ServiceProviderNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        port.rateServiceProvider(serviceProvId, rating);
    }

    private static void updateCustomer(ws.client.CustomerEntity c) throws CustomerNotFoundException_Exception, CustomerAlreadyExistsException_Exception, InputDataValidationException_Exception, UnknownPersistenceException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        port.updateCustomer(c);
    }
    
    private static java.util.List<ws.client.CategoryEntity> retrieveAllCategories() {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.retrieveAllCategories();
    }
    
    private static AppointmentEntity retrieveAppointmentByAppointmentId(java.lang.Long appointmentId) throws AppointmentNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.retrieveAppointmentByAppointmentId(appointmentId);
    }

    private static java.util.List<ws.client.AppointmentEntity> retrieveServiceProvAppointmentsForServiceProv(java.lang.Long serviceProvId) throws ServiceProviderNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.retrieveServiceProvAppointmentsForServiceProv(serviceProvId);
    }

    private static void updateServiceProvider(ws.client.ServiceProviderEntity arg0) throws ServiceProviderNotFoundException_Exception, InputDataValidationException_Exception, ServiceProviderAlreadyExistsException_Exception, UnknownPersistenceException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        port.updateServiceProvider(arg0);
    }

    private static Double computeAverageRating(java.lang.Long serviceProvId) throws ServiceProviderNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.computeAverageRating(serviceProvId);
    }

//    private static java.util.List<java.lang.String> retrieveAvailableTimeSlotsString(javax.xml.datatype.XMLGregorianCalendar date, java.lang.Long serviceProviderId) throws ServiceProviderNotFoundException_Exception {
//        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
//        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
//        return port.retrieveAvailableTimeSlotsString(date, serviceProviderId);
//    }

//    private static String retrieveFirstAvailableTimeString(javax.xml.datatype.XMLGregorianCalendar date, java.lang.Long serviceProviderId) throws ServiceProviderNotFoundException_Exception {
//        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
//        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
//        return port.retrieveFirstAvailableTimeString(date, serviceProviderId);
//    }

    private static void bookAppointment(java.lang.Long serviceProvId, java.lang.Long custId, java.lang.String dateStr, java.lang.String timeStr) throws ServiceProviderNotFoundException_Exception, ParseException_Exception, CustomerNotFoundException_Exception, AppointmentNotFoundException_Exception, InputDataValidationException_Exception, ServiceProviderAlreadyExistsException_Exception, UnknownPersistenceException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        port.bookAppointment(serviceProvId, custId, dateStr, timeStr);
    }

    private static java.util.List<ws.client.AppointmentEntity> retrieveCustomerAppointments(java.lang.Long customerId) throws CustomerNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.retrieveCustomerAppointments(customerId);
    }

    private static void cancelAppointment(java.lang.Long appointmentId) throws AppointmentNotFoundException_Exception, ServiceProviderNotFoundException_Exception, CustomerNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        port.cancelAppointment(appointmentId);
    }

    private static String getAppointmentTimeString(java.lang.Long arg0) throws AppointmentNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.getAppointmentTimeString(arg0);
    }

    private static String getAppointmentDateString(java.lang.Long arg0) throws AppointmentNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.getAppointmentDateString(arg0);
    }

    private static boolean canBeCancelled(java.lang.Long arg0) throws AppointmentNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.canBeCancelled(arg0);
    }

    private static java.util.List<java.lang.String> retrieveAvailableTimeSlotsString(java.lang.String date, java.lang.Long serviceProviderId) throws ParseException_Exception, ServiceProviderNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.retrieveAvailableTimeSlotsString(date, serviceProviderId);
    }

    private static String retrieveFirstAvailableTimeString(java.lang.String date, java.lang.Long serviceProviderId) throws ServiceProviderNotFoundException_Exception, ParseException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.retrieveFirstAvailableTimeString(date, serviceProviderId);
    }

    private static java.util.List<java.lang.String> retrieveOccupiedSlotsString(java.lang.String arg0, java.lang.Long arg1) throws ParseException_Exception, ServiceProviderNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.retrieveOccupiedSlotsString(arg0, arg1);
    }

    private static boolean canBeRated(java.lang.Long arg0, java.lang.Long arg1) throws ServiceProviderNotFoundException_Exception, CustomerNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.canBeRated(arg0, arg1);
    }
}
