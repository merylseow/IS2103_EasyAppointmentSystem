package customerterminalclient;

import ejb.session.stateless.AppointmentSessionBeanRemote;
import ejb.session.stateless.CategorySessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.ServiceProviderSessionBeanRemote;
import javax.ejb.EJB;


public class Main
{
    @EJB(name = "ServiceProviderSessionBeanRemote")
    private static ServiceProviderSessionBeanRemote serviceProviderSessionBeanRemote;

    @EJB(name = "AppointmentSessionBeanRemote")
    private static AppointmentSessionBeanRemote appointmentSessionBeanRemote;

    @EJB(name = "CustomerSessionBeanRemote")
    private static CustomerSessionBeanRemote customerSessionBeanRemote;
    
    @EJB(name = "CategorySessionBeanRemote")
    private static CategorySessionBeanRemote categorySessionBeanRemote;
    
    public static void main(String[] args)
    {
        MainApp terminal = new MainApp(customerSessionBeanRemote, appointmentSessionBeanRemote, serviceProviderSessionBeanRemote, categorySessionBeanRemote);
        terminal.run();   
    }
}
