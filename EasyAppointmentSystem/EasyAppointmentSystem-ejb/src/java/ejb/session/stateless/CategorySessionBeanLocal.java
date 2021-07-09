package ejb.session.stateless;

import entity.CategoryEntity;
import java.util.List;
import util.exception.CategoryAlreadyExistsException;
import util.exception.CategoryNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.ServiceProviderAlreadyExistsException;
import util.exception.UnknownPersistenceException;


public interface CategorySessionBeanLocal
{
    public CategoryEntity createNewCategory(CategoryEntity newCategoryEntity) throws CategoryNotFoundException,InputDataValidationException,CategoryAlreadyExistsException,UnknownPersistenceException ;   

    public CategoryEntity retrieveCategoryByCategoryName(String categoryName) throws CategoryNotFoundException;

    public void deleteCategory(String categoryName) throws CategoryNotFoundException;
    
    public List<CategoryEntity> retrieveAllCategories();

    public CategoryEntity retrieveCategoryByCategoryNum(Integer categoryNum) throws CategoryNotFoundException;

    public List<CategoryEntity> retrieveBusinessCategories();
}
