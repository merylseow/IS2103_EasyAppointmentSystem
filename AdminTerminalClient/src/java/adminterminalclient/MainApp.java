package adminterminalclient;

import java.util.Scanner;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.ServiceProviderSessionBeanRemote;
import ejb.session.stateless.AppointmentSessionBeanRemote;
import util.exception.EntityManagerException;
import util.exception.InvalidLoginException;
import ejb.session.stateless.AdminSessionBeanRemote;
import ejb.session.stateless.CategorySessionBeanRemote;
import entity.AdminEntity;
import ejb.session.stateless.EmailSessionBeanRemote;
import java.util.InputMismatchException;
import javax.jms.Queue;
import javax.jms.ConnectionFactory;

public class MainApp {

    private CustomerSessionBeanRemote customerSessionBeanRemote;
    private ServiceProviderSessionBeanRemote serviceProviderSessionBeanRemote;
    private AdminSessionBeanRemote adminSessionBeanRemote;
    private AppointmentSessionBeanRemote appointmentSessionBeanRemote;
    private CategorySessionBeanRemote categorySessionBeanRemote;
    private EmailSessionBeanRemote emailSessionBeanRemote;

    private Queue queueCheckoutNotification;
    private ConnectionFactory queueCheckoutNotificationFactory;

    private AdminOperationModule adminOperationModule;
    private AdminEntity currentAdminEntity;

    public MainApp() {
    }

    public MainApp(CustomerSessionBeanRemote customerSessionBeanRemote, ServiceProviderSessionBeanRemote serviceProviderSessionBeanRemote, AdminSessionBeanRemote adminSessionBeanRemote, AppointmentSessionBeanRemote appointmentSessionBeanRemote, CategorySessionBeanRemote categorySessionBeanRemote, EmailSessionBeanRemote emailSessionBeanRemote,
            Queue queueCheckoutNotification, ConnectionFactory queueCheckoutNotificationFactory) {
        this();
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.serviceProviderSessionBeanRemote = serviceProviderSessionBeanRemote;
        this.adminSessionBeanRemote = adminSessionBeanRemote;
        this.appointmentSessionBeanRemote = appointmentSessionBeanRemote;
        this.categorySessionBeanRemote = categorySessionBeanRemote;
        this.emailSessionBeanRemote = emailSessionBeanRemote;
        this.queueCheckoutNotification = queueCheckoutNotification;
        this.queueCheckoutNotificationFactory = queueCheckoutNotificationFactory;
    }

    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            try {
                System.out.println("*** Welcome to Admin terminal ***\n");
                System.out.println("1: Login");
                System.out.println("2: Exit\n");
                response = 0;

                while (response < 1 || response > 2) {
                    System.out.print("> ");
                    response = scanner.nextInt();

                    if (response == 1) {
                        try {
                            doLogin();
                            System.out.println("Login successful!\n");

                            adminOperationModule = new AdminOperationModule(customerSessionBeanRemote, serviceProviderSessionBeanRemote, adminSessionBeanRemote, appointmentSessionBeanRemote, categorySessionBeanRemote, emailSessionBeanRemote, queueCheckoutNotification, queueCheckoutNotificationFactory);
                            menuMain();
                        } catch (InvalidLoginException | EntityManagerException ex) {
                            System.out.println("Invalid login credentials: " + ex.getMessage() + "\n");
                        }

                    } else if (response == 2) {
                        break;
                    } else {
                        System.out.println("Invalid option, please try again!\n");
                    }
                }

                if (response == 2) {
                    break;
                }
            } catch (InputMismatchException ex) {
                System.out.println("Invalid input! Exiting...\n");
            }

        }

    }

    private void doLogin() throws InvalidLoginException, EntityManagerException {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";

        System.out.println("*** Admin terminal :: Login ***\n");
        System.out.print("Enter Email Address> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        System.out.println();

        if (username.length() > 0 && password.length() > 0) {
            currentAdminEntity = adminSessionBeanRemote.adminLogin(username, password);
        } else {
            throw new InvalidLoginException("Missing login credential!");
        }
    }

    private void menuMain() {
        System.out.println("*** Admin terminal :: Main ***\n");
        System.out.println("You are logged in as " + currentAdminEntity.getName() + "\n");
        adminOperationModule.menuAdminOperation();
    }
}
