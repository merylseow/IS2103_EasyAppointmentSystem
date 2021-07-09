package ejb.session.stateless;

import entity.AdminEntity;
import entity.ServiceProviderEntity;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import static util.enumeration.StatusEnum.APPROVED;
import static util.enumeration.StatusEnum.BLOCKED;
import util.exception.AdminNotFoundException;
import util.exception.InvalidLoginException;
import util.exception.ServiceProviderNotFoundException;
import util.security.CryptographicHelper;


@Stateless
@Local(AdminSessionBeanLocal.class)
@Remote(AdminSessionBeanRemote.class)

public class AdminSessionBean implements AdminSessionBeanLocal, AdminSessionBeanRemote
{
    @PersistenceContext(unitName = "EasyAppointmentSystem-ejbPU")
    private EntityManager em;
    
    
    public AdminSessionBean()
    {
    }
    
    
    @Override
    public AdminEntity createNewAdmin(AdminEntity newAdminEntity) throws AdminNotFoundException
    {
        if (newAdminEntity != null)
        {
            em.persist(newAdminEntity);
            em.flush();
        }
        else
        {
            throw new AdminNotFoundException("Admin does not exist!");
        }
        
        return newAdminEntity;
    }
    
    
    @Override
    public List<AdminEntity> retrieveAllAdmins()
    {
        Query query = em.createQuery("SELECT a FROM AdminEntity a");
        
        return query.getResultList();
    }
    
    
    @Override
    public AdminEntity retrieveAdminByAdminId(Long adminId) throws AdminNotFoundException
    {
        AdminEntity adminEntity = em.find(AdminEntity.class, adminId);
        
        if (adminEntity != null)
        {
            return adminEntity;
        }
        else
        {
            throw new AdminNotFoundException("Admin ID" + adminId + " does not exist!");
        }
    }
    
    
    @Override
    public AdminEntity retrieveAdminByUsername(String username) throws AdminNotFoundException
    {
        Query query = em.createQuery("SELECT a FROM AdminEntity a WHERE a.userName = :inUserName");
        query.setParameter("inUserName", username);
        
        try
        {
            return (AdminEntity)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new AdminNotFoundException("Admin Username " + username + " does not exist!");
        }
    }
    
    
    @Override
    public AdminEntity adminLogin(String username, String password) throws InvalidLoginException
    {
        try
        {
            AdminEntity adminEntity = retrieveAdminByUsername(username);
            String decrypt = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + adminEntity.getSalt()));
            
            if(adminEntity.getPassword().equals(decrypt))
            {               
                return adminEntity;
            }
            else
            {
                throw new InvalidLoginException("Username does not exist or invalid password!");
            }
        }
        catch(AdminNotFoundException ex)
        {
            throw new InvalidLoginException("Username does not exist or invalid password!");
        }
    }
    
    
    @Override
    public void updateAdmin(AdminEntity adminEntity) throws AdminNotFoundException
    {
        if(adminEntity != null && adminEntity.getAdminId() != null)
        {
            AdminEntity adminEntityToUpdate = retrieveAdminByAdminId(adminEntity.getAdminId());
            
            if(adminEntityToUpdate.getUserName().equals(adminEntity.getUserName()))
            {
                adminEntityToUpdate.setName(adminEntity.getName());
                adminEntityToUpdate.setName(adminEntity.getName());
            }
        }
        else 
        {
            throw new AdminNotFoundException("Admin ID not provided for admin to be updated");
        }
    }
    
    @Override
    public void deleteAdmin(Long adminId) throws AdminNotFoundException
    {
        AdminEntity adminEntityToRemove = retrieveAdminByAdminId(adminId);
        em.remove(adminEntityToRemove);
    }
    
    @Override
    public ServiceProviderEntity retrieveServiceProviderById(Long serviceProviderId) throws ServiceProviderNotFoundException
    {
        ServiceProviderEntity serviceProviderEntity = em.find(ServiceProviderEntity.class, serviceProviderId);
        
        if (serviceProviderEntity != null)
        {
            return serviceProviderEntity;
        }
        else
        {
            throw new ServiceProviderNotFoundException("Service provider cannot be found!\n");
        }
    }
    
    @Override
    public void approveServiceProvider(Long serviceProvId) throws ServiceProviderNotFoundException
    {
        ServiceProviderEntity s = retrieveServiceProviderById(serviceProvId);
        try
        {
            ServiceProviderEntity serviceProvEntity = retrieveServiceProviderById(serviceProvId);
            serviceProvEntity.setStatus(APPROVED);
            em.persist(serviceProvEntity);
            em.flush();
        }
        catch (ServiceProviderNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving service provider: " + ex.getMessage() + "\n");
        }
    }
    
    @Override
    public void blockServiceProvider(Long serviceProvId) throws ServiceProviderNotFoundException
    {
        ServiceProviderEntity s = retrieveServiceProviderById(serviceProvId);
        try
        {
            ServiceProviderEntity serviceProvEntity = retrieveServiceProviderById(serviceProvId);
            serviceProvEntity.setStatus(BLOCKED);
            em.persist(serviceProvEntity);
            em.flush();
        }
        catch (ServiceProviderNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving service provider: " + ex.getMessage() + "\n");
        }
    }
}

