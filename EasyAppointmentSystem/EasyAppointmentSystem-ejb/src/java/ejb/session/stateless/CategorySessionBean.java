package ejb.session.stateless;

import entity.CategoryEntity;
import entity.ServiceProviderEntity;
import java.util.List;
import java.util.Set;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CategoryAlreadyExistsException;
import util.exception.CategoryNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.ServiceProviderAlreadyExistsException;
import util.exception.ServiceProviderNotFoundException;
import util.exception.UnknownPersistenceException;

@Stateless
@Local(CategorySessionBeanLocal.class)
@Remote(CategorySessionBeanRemote.class)

public class CategorySessionBean implements CategorySessionBeanLocal, CategorySessionBeanRemote {

    @PersistenceContext(unitName = "EasyAppointmentSystem-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public CategorySessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public CategoryEntity createNewCategory(CategoryEntity newCategoryEntity) throws CategoryNotFoundException,InputDataValidationException,CategoryAlreadyExistsException,UnknownPersistenceException {
        Set<ConstraintViolation<CategoryEntity>> constraintViolations = validator.validate(newCategoryEntity);

        if (constraintViolations.isEmpty()) {
            try {
                if (newCategoryEntity != null) {
                    em.persist(newCategoryEntity);
                    em.flush();
                } else {
                    throw new CategoryNotFoundException("Category does not exist!");
                }

                return newCategoryEntity;

            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new CategoryAlreadyExistsException("Service Provider already exists");
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }



@Override
        public List<CategoryEntity> retrieveAllCategories()
    {
        Query query = em.createQuery("SELECT c FROM CategoryEntity c");
        
        return query.getResultList();
    }
    
    @Override
        public CategoryEntity retrieveCategoryByCategoryName(String categoryName) throws CategoryNotFoundException
    {
        Query query = em.createQuery("SELECT c FROM CategoryEntity c WHERE c.categoryName = :inCategoryName");
        query.setParameter("inCategoryName", categoryName);
        System.out.println("Cat: " + query.getSingleResult());
        try
        {
            return (CategoryEntity)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new CategoryNotFoundException("Category does not exist!");
        }
    }
    
    @Override
        public CategoryEntity retrieveCategoryByCategoryNum(Integer categoryNum) throws CategoryNotFoundException
    {
        Query query = em.createQuery("SELECT c FROM CategoryEntity c WHERE c.categoryNum = :inCategoryNum");
        query.setParameter("inCategoryNum", categoryNum);
        try
        {
            return (CategoryEntity)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new CategoryNotFoundException("Category does not exist!");
        }
    }
    
    @Override
        public void deleteCategory(String categoryName) throws CategoryNotFoundException
    {
        CategoryEntity categoryEntityToRemove = retrieveCategoryByCategoryName(categoryName);
        em.remove(categoryEntityToRemove);
    }
    
    @Override
        public List<CategoryEntity> retrieveBusinessCategories() 
    {
        Query query = em.createQuery("SELECT s FROM CategoryEntity s ORDER BY s.categoryId");
        return query.getResultList();
    }

private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<CategoryEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}

