package ejb.mdb;

import ejb.session.stateless.AppointmentSessionBeanLocal;
import entity.AppointmentEntity;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import util.exception.AppointmentNotFoundException;
import ejb.session.stateless.EmailSessionBeanLocal;



@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/queueCheckoutNotification"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})

public class CheckoutNotificationMdb implements MessageListener 
{
    @EJB
    private AppointmentSessionBeanLocal appointmentSessionBeanLocal;
    @EJB
    private EmailSessionBeanLocal emailSessionBeanLocal;
    
    
    
    public CheckoutNotificationMdb() 
    {
    }
    
    
    
    @PostConstruct
    public void postConstruct()
    {
    }
    
    
    
    @PreDestroy
    public void preDestroy()
    {
    }


    
    @Override
    public void onMessage(Message message)
    {
        try
        {
            if (message instanceof MapMessage)
            {
                MapMessage mapMessage = (MapMessage)message;                
                String toEmailAddress = mapMessage.getString("toEmailAddress");
                String fromEmailAddress = mapMessage.getString("fromEmailAddress");
                Long appointmentEntityId = mapMessage.getLong("appointmentId");
                AppointmentEntity appointmentEntity = appointmentSessionBeanLocal.retrieveAppointmentByAppointmentId(appointmentEntityId);
                
                emailSessionBeanLocal.emailCheckoutNotificationSync(appointmentEntity, fromEmailAddress, toEmailAddress);
                
                System.err.println("********** EmailNotificationMdb.onMessage: " + appointmentEntity.getAppointmentId() + "; " + toEmailAddress + "; " + fromEmailAddress);
            }
        }
        catch(AppointmentNotFoundException | JMSException ex)
        {
            System.err.println("EmailNotificationMdb.onMessage(): " + ex.getMessage());
        }
    }    
}
