package serviceproviderterminalclient;

import ejb.session.stateless.AppointmentSessionBeanRemote;
import ejb.session.stateless.CategorySessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.EmailSessionBeanRemote;
import ejb.session.stateless.ServiceProviderSessionBeanRemote;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;



public class Main {

    @Resource(mappedName = "jms/queueCheckoutNotification")
    private static Queue queueCheckoutNotification;

    @Resource(mappedName = "jms/queueCheckoutNotificationFactory")
    private static ConnectionFactory queueCheckoutNotificationFactory;

    @EJB(name = "EmailSessionBeanRemote")
    private static EmailSessionBeanRemote emailSessionBeanRemote;
    
    @EJB(name = "CustomerSessionBeanRemote")
    private static CustomerSessionBeanRemote customerSessionBeanRemote;
    
    @EJB(name = "AppointmentSessionBeanRemote")
    private static AppointmentSessionBeanRemote appointmentSessionBeanRemote;
    
    @EJB(name = "ServiceProviderSessionBeanRemote")
    private static ServiceProviderSessionBeanRemote serviceProviderSessionBeanRemote;
    
    @EJB(name = "CategorySessionBeanRemote")
    private static CategorySessionBeanRemote categorySessionBeanRemote;
    

    
    public static void main(String[] args)
    {
        MainApp terminal = new MainApp(customerSessionBeanRemote, appointmentSessionBeanRemote, serviceProviderSessionBeanRemote, categorySessionBeanRemote, emailSessionBeanRemote,queueCheckoutNotification, queueCheckoutNotificationFactory);
        terminal.run();   
    }  
}