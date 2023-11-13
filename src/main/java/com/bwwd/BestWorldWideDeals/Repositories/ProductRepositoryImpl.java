package com.bwwd.BestWorldWideDeals.Repositories;

import com.bwwd.BestWorldWideDeals.Models.Filter;
import com.bwwd.BestWorldWideDeals.Models.Operators;
import com.bwwd.BestWorldWideDeals.Models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.*;

@Component
public class ProductRepositoryImpl{

    @Autowired
    @Lazy // This is important it will prevent circular dependency
    private ProductRepository productRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public List<Product> findAllProducts(List<Filter> filters) throws Exception {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> product = query.from(Product.class);
        List<Predicate> predicates = new ArrayList<>();
        List<Map.Entry<Boolean,In>> inClauses = new ArrayList<>();
        for (Filter filter : filters) {
            if (filter.operator.equals(Operators.IN)) {
                try {
                    String className = Product.class.getField(filter.fieldName).getType().getName();
                    In inClause = cb.in(product.get(filter.fieldName));
                    if (className.equals("java.lang.Double") || className.equals("java.lang.Long")) {
                        List<Number> nums = (List<Number>) filter.fieldValue;
                        nums.stream().forEach(n -> inClause.value(n));
                    } else if (className.equals("java.lang.String")) {
                        List<String> strs = (List<String>) filter.fieldValue;
                        strs.stream().forEach(s -> inClause.value(s));
                    } else {
                        throw new Exception("Failed to prepare Query for Operatos IN.");
                    }
                    inClauses.add(new AbstractMap.SimpleEntry<>(filter.isNegate,inClause));
                } catch (Exception e) {
                    throw new Exception("Failed to prepare Query for Operatos IN.");
                }
            }
            Predicate predicate = null;
            switch (filter.operator) {
                case EQUAL:
                    predicate = cb.equal(product.get(filter.fieldName), filter.fieldValue);
                    break;
                case GT:
                    predicate = cb.gt(product.get(filter.fieldName), (Number) filter.fieldValue);
                    break;
                case LT:
                    predicate = cb.lt(product.get(filter.fieldName), (Number) filter.fieldValue);
                    break;
                case GE:
                    predicate = cb.ge(product.get(filter.fieldName), (Number) filter.fieldValue);
                    break;
                case LE:
                    predicate = cb.le(product.get(filter.fieldName), (Number) filter.fieldValue);
                case LIKE:
                    predicate = cb.like(product.get(filter.fieldName), (String) filter.fieldValue);
                    break;
            }
            if (Objects.isNull(predicate)) {
                continue;
            }
            if (filter.isNegate) {
                predicates.add(cb.not(predicate));
            } else {
                predicates.add(predicate);
            }
        }

        Predicate inCalusePredicate = null;
        if(inClauses.size() >=2 ){
            inCalusePredicate = cb.and(
                    inClauses.get(0).getKey() ? cb.not(inClauses.get(0).getValue()) : inClauses.get(0).getValue(),
                    inClauses.get(1).getKey() ? cb.not(inClauses.get(1).getValue()) : inClauses.get(1).getValue());
            for(int i=2;i<inClauses.size();i++){
                inCalusePredicate = cb.and(inCalusePredicate,
                        inClauses.get(i).getKey() ? cb.not(inClauses.get(i).getValue()) : inClauses.get(i).getValue());
            }
        }

        Predicate regularPredicate = null;
        if(predicates.size() >=2 ){
            regularPredicate = cb.and(predicates.toArray(new Predicate[predicates.size()]));
        }else if(predicates.size() == 1){
            regularPredicate = predicates.get(0);
        }

        Predicate finalPredicate = null;
        if(Objects.nonNull(inCalusePredicate) && Objects.nonNull(regularPredicate)){
            finalPredicate = cb.and(inCalusePredicate, regularPredicate);
        }else if(Objects.nonNull(regularPredicate)){
            finalPredicate = regularPredicate;
        }else if(Objects.nonNull(inCalusePredicate)){
            finalPredicate = inCalusePredicate;
        }

        if(inClauses.size() > 0 && Objects.nonNull(finalPredicate)) {
            query.select(product).where(cb.and(
                                  inClauses.get(0).getKey() ?
                                  cb.not(inClauses.get(0).getValue()) :
                                  inClauses.get(0).getValue(),
                                  finalPredicate));
        }else if(Objects.nonNull(finalPredicate)){
            query.select(product).where(finalPredicate);
        }else if(inClauses.size() > 0) {
            query.select(product).where(inClauses.get(0).getKey() ?
                              cb.not(inClauses.get(0).getValue()) :
                              inClauses.get(0).getValue());
        }else{
            throw new Exception("Failed to create a Query with provided Filters.");
        }

        return entityManager.createQuery(query).getResultList();

    }
}