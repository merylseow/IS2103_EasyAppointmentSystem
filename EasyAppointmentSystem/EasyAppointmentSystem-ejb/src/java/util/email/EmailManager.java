package util.email;

import entity.AppointmentEntity;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;



public class EmailManager 
{
    private final String emailServerName = "smtp.gmail.com";
    private final String mailer = "JavaMailer";
    private String smtpAuthUser;
    private String smtpAuthPassword;
    
    
    
    public EmailManager()
    {
    }

    
    
    public EmailManager(String smtpAuthUser, String smtpAuthPassword)
    {
        this.smtpAuthUser = smtpAuthUser;
        this.smtpAuthPassword = smtpAuthPassword;
    }
    
    
    
    public Boolean emailCheckoutNotification(AppointmentEntity appointmentEntity, String fromEmailAddress, String toEmailAddress)
    {
        String emailBody = "";
        
        emailBody += "You have booked an appointment successfully for appointment code: " + appointmentEntity.getAppointmentCode() + "\n\n";
        emailBody += "S/N     Service Provider     Appointment Date        Appointment Time  \n\n";
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        
        Date datef = appointmentEntity.getAppointmentDate();
        Date timef = appointmentEntity.getAppointmentTime();
        
        String strDate = dateFormat.format(datef);
        String strTime = timeFormat.format(timef);
        emailBody += appointmentEntity.getAppointmentId()
                + "     " + appointmentEntity.getServiceProviderEntity().getName()
                + "     " + strDate
                + "     " + strTime
                + "\n";
          
        try 
        {
            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", emailServerName);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");            
            props.put("mail.smtp.debug", "true");            
            javax.mail.Authenticator auth = new SMTPAuthenticator(smtpAuthUser, smtpAuthPassword);
            Session session = Session.getInstance(props, auth);
            session.setDebug(true);            
            Message msg = new MimeMessage(session);
                                    
            if (msg != null)
            {
                msg.setFrom(InternetAddress.parse(fromEmailAddress, false)[0]);
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailAddress, false));
                msg.setSubject("Reminder Sent Successfully!");
                msg.setText(emailBody);
                msg.setHeader("X-Mailer", mailer);
                
                Date timeStamp = new Date();
                msg.setSentDate(timeStamp);
                
                Transport.send(msg);
                
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Exception e) 
        {
            e.printStackTrace();
            
            return false;
        }
    }
    
    
}

