package example.ui.application;

import org.vaadin.navigator7.uri.EntityUriAnalyzer;

import example.model.BaseEntity;
import example.model.Company;
import example.model.Product;

/** Example of EntityUriAnalyzer.
 * Your Navigator7 can live with the default provided ParamUriAnalyzer (only working with Strings and simple types).
 * If you have a DB you probably want an EntityUriAnalyzer.
 * 
 * @author John Rizzo - BlackBeltFactory.com
 */
public class MyUriAnalyzer extends EntityUriAnalyzer<BaseEntity> {

    @Override
    public BaseEntity findEntity(Class<? extends BaseEntity> entityClass, String pk) {
        // This fake demo implementation always returns the same entities.
        if (entityClass == Product.class) {
            Product product = new Product();
            product.setId(34L);
            product.setPartNumber(45355224345L);
            product.setLabel("Black Belt");
            return product;
        } else if (entityClass == Company.class) {
            Company co = new Company();
            co.setId(9998L);
            co.setComanyName("Vaadin & co.");
            return co;
        } else {
            throw new RuntimeException("Unsupported entity type");  // This one could be thrown by JPA if bad params passed :-)
        }
        
        ///// Real JPA impelmentation would be something simpler, like:
        // return entityManager.find(entityClass, pk);
    }

    @Override
    public String getEntityFragmentValue(BaseEntity entity) {
        // It's especially easy here because the primary key is in a common ancestor.
        return entity.getId().toString();
    }

}
