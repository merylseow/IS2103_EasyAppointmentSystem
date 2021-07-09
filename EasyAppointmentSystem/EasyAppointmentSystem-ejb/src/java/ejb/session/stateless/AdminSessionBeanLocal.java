package ejb.session.stateless;

import entity.AdminEntity;
import entity.ServiceProviderEntity;
import java.util.List;
import util.exception.AdminNotFoundException;
import util.exception.InvalidLoginException;
import util.exception.ServiceProviderNotFoundException;


public interface AdminSessionBeanLocal
{    
    AdminEntity createNewAdmin(AdminEntity newAdminEntity) throws AdminNotFoundException;
    
    List<AdminEntity> retrieveAllAdmins();
    
    AdminEntity retrieveAdminByAdminId(Long adminId) throws AdminNotFoundException;
    
    AdminEntity retrieveAdminByUsername(String username) throws AdminNotFoundException;
    
    AdminEntity adminLogin(String username, String password) throws InvalidLoginException;
    
    void updateAdmin(AdminEntity adminEntity) throws AdminNotFoundException;
    
    void deleteAdmin(Long adminId) throws AdminNotFoundException;

    public ServiceProviderEntity retrieveServiceProviderById(Long serviceProviderId) throws ServiceProviderNotFoundException;

    public void approveServiceProvider(Long serviceProvId) throws ServiceProviderNotFoundException;

    public void blockServiceProvider(Long serviceProvId) throws ServiceProviderNotFoundException;
}