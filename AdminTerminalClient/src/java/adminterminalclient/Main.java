package adminterminalclient;

import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.ServiceProviderSessionBeanRemote;
import ejb.session.stateless.AppointmentSessionBeanRemote;
import javax.ejb.EJB;
import ejb.session.stateless.AdminSessionBeanRemote;
import ejb.session.stateless.CategorySessionBeanRemote;
import ejb.session.stateless.EmailSessionBeanRemote;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;



public class Main
{

    @Resource(mappedName = "jms/queueCheckoutNotification")
    private static javax.jms.Queue queueCheckoutNotification;

    @Resource(mappedName = "jms/queueCheckoutNotificationFactory")
    private static ConnectionFactory queueCheckoutNotificationFactory;
    @EJB
    private static CustomerSessionBeanRemote customerSessionBeanRemote;
    @EJB
    private static ServiceProviderSessionBeanRemote serviceProviderSessionBeanRemote;
    @EJB
    private static AdminSessionBeanRemote adminSessionBeanRemote;
    @EJB
    private static AppointmentSessionBeanRemote appointmentSessionBeanRemote;
    @EJB

    private static CategorySessionBeanRemote categorySessionBeanRemote;
    private static EmailSessionBeanRemote emailSessionBeanRemote;
    

    
    
 
    public static void main(String[] args)
    {

        MainApp mainApp = new MainApp(customerSessionBeanRemote, serviceProviderSessionBeanRemote, adminSessionBeanRemote, appointmentSessionBeanRemote, categorySessionBeanRemote, emailSessionBeanRemote, queueCheckoutNotification, queueCheckoutNotificationFactory);
        mainApp.runApp();
    }

    
}