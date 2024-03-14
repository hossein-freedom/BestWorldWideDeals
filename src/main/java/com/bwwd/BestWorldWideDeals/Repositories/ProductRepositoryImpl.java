package com.bwwd.BestWorldWideDeals.Repositories;

import com.bwwd.BestWorldWideDeals.Models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import javax.persistence.TypedQuery;
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


    private Predicate processFilterNode(FilterNode filterNode, CriteriaBuilder cb,
                                                        Root<Product> product) throws Exception{
        Operand operand = filterNode.operand;
        List<Filter> filters = filterNode.filters;
        if(filters.isEmpty()){
            return null;
        }
        List<Predicate> predicates = new ArrayList<>();
        List<Map.Entry<Boolean,In>> inClauses = new ArrayList<>();
        for (Filter filter : filters) {
            if (filter.operator.equals(Operator.IN)) {
                try {
                    String className = Product.class.getField(filter.fieldName).getType().getName();
                    In inClause = cb.in(product.get(filter.fieldName));
                    if (className.equals("java.lang.Double")) {
                        List<Number> nums = (List<Number>) filter.fieldValue;
                        nums.stream().forEach(n -> inClause.value(n));
                    } else if (className.equals("java.lang.String")) {
                        List<String> strs = (List<String>) filter.fieldValue;
                        strs.stream().forEach(s -> inClause.value(s));
                    } else if(className.equals("com.bwwd.BestWorldWideDeals.Models.Source")){
                        List<String> strs = (List<String>) filter.fieldValue;
                        strs.stream().forEach(s -> inClause.value(Source.valueOf(s)));
                    }else {
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
                    break;
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
        if(inClauses.size() >=2 ) {
            inCalusePredicate = operand.equals(Operand.AND) ?
                        cb.and(
                            inClauses.get(0).getKey() ? cb.not(inClauses.get(0).getValue()) : inClauses.get(0).getValue(),
                            inClauses.get(1).getKey() ? cb.not(inClauses.get(1).getValue()) : inClauses.get(1).getValue())
                        :
                        cb.or(
                            inClauses.get(0).getKey() ? cb.not(inClauses.get(0).getValue()) : inClauses.get(0).getValue(),
                            inClauses.get(1).getKey() ? cb.not(inClauses.get(1).getValue()) : inClauses.get(1).getValue());
            for (int i = 2; i < inClauses.size(); i++) {
                inCalusePredicate = operand.equals(Operand.AND) ?
                        cb.and(inCalusePredicate,
                             inClauses.get(i).getKey() ? cb.not(inClauses.get(i).getValue()) : inClauses.get(i).getValue())
                        :
                        cb.or(inCalusePredicate,
                                inClauses.get(i).getKey() ? cb.not(inClauses.get(i).getValue()) : inClauses.get(i).getValue());
            }
        }
        Predicate regularPredicate = null;
        if(predicates.size() >=2 ){
            regularPredicate = operand.equals(Operand.AND) ?
                                    cb.and(predicates.toArray(new Predicate[predicates.size()]))
                                    :
                                    cb.or(predicates.toArray(new Predicate[predicates.size()]));
        }else if(predicates.size() == 1){
            regularPredicate = predicates.get(0);
        }

        Predicate finalPredicate = null;
        if(Objects.nonNull(inCalusePredicate) && Objects.nonNull(regularPredicate)){
            finalPredicate = operand.equals(Operand.AND) ?
                    cb.and(inCalusePredicate, regularPredicate)
                    :
                    cb.or(inCalusePredicate, regularPredicate);
        }else if(Objects.nonNull(regularPredicate)){
            finalPredicate = regularPredicate;
        }else if(Objects.nonNull(inCalusePredicate)){
            finalPredicate = inCalusePredicate;
        }

        if(inClauses.size() > 0 && Objects.nonNull(finalPredicate)) {
            return cb.and(
                    inClauses.get(0).getKey() ? cb.not(inClauses.get(0).getValue()) : inClauses.get(0).getValue(), finalPredicate);
        }else if(Objects.nonNull(finalPredicate)){
            return finalPredicate;
        }else if(inClauses.size() > 0) {
            return inClauses.get(0).getKey() ? cb.not(inClauses.get(0).getValue()) : inClauses.get(0).getValue();
        }else {
            throw new Exception("Failed to create a Query with provided Filters.");
        }
    }

    private Predicate processPredicates(PredicateNode predicateNode, CriteriaBuilder cb,
                                                                    Root<Product> product){
        Operand operand = predicateNode.operand;
        Predicate leftPredicate = null;
        Predicate rightPredicate = null;
        try{
            if(Objects.nonNull(predicateNode.leftFilterNode) &&
                    Objects.nonNull(predicateNode.leftFilterNode.filters) &&
                        Objects.nonNull(predicateNode.leftFilterNode.operand)) {
                leftPredicate = processFilterNode(predicateNode.leftFilterNode, cb, product);
            }
            if(Objects.nonNull(predicateNode.rightFilterNode) &&
                    Objects.nonNull(predicateNode.rightFilterNode.filters) &&
                        Objects.nonNull(predicateNode.rightFilterNode.operand)) {
                rightPredicate = processFilterNode(predicateNode.rightFilterNode, cb, product);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        if(Objects.nonNull(leftPredicate) && Objects.nonNull(rightPredicate)){
            return operand.equals(Operand.AND) ?
                    cb.and(leftPredicate, rightPredicate)
                    :
                    cb.or(leftPredicate, rightPredicate);
        }else if(Objects.nonNull(leftPredicate)){
            return leftPredicate;
        }
        return rightPredicate;
    }

    public Map<String, List<String>> getCategorySubcategoryByFilter(SearchCriteria searchCriteria) throws Exception{
        searchCriteria.page.setPageSize(10000);
        List<Product> products = findAllProducts(searchCriteria).getProducts();
        Map<String, List<String>> output = new HashMap<>();
        products.stream().forEach( product -> {
            if (!output.containsKey(product.category)){
                output.put(product.category, new ArrayList<>());
            }
            output.get(product.category).add(product.subCategory);
        });
        return output;
    }

    public List<Source> getSourcesByFilter (SearchCriteria searchCriteria) throws Exception {
        searchCriteria.page.setPageSize(10000);
        List<Product> products = findAllProducts(searchCriteria).getProducts();
        List<Source> output = new ArrayList<>();
        products.stream().forEach(product -> {
            if (!output.contains(product.source)){
                output.add(product.source);
            }
        });
        return output;
    }

    public ProductSearchResult findAllProducts(SearchCriteria searchCriteria) throws Exception {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> product = query.from(Product.class);
        if (searchCriteria.searchType.equals("FILTERED")) {
            Predicate predicate = processPredicates(searchCriteria.predicateNode, cb, product);
            query.select(product).distinct(true).where(predicate);
        }else if (searchCriteria.searchType.equals("ALL")){
            query.select(product).distinct(true);
        }
        if(Objects.nonNull(searchCriteria.sort)){
            if(searchCriteria.sort.isAsc) {
                query.orderBy(cb.asc(product.get(searchCriteria.sort.fieldName)));
            }else{
                query.orderBy(cb.desc(product.get(searchCriteria.sort.fieldName)));
            }
        }
        TypedQuery<Product> typedQuery = entityManager.createQuery(query);
        typedQuery.setMaxResults(searchCriteria.page.pageSize);
        typedQuery.setFirstResult(searchCriteria.page.pageSize * searchCriteria.page.pageNumber);
        List<Product> products = typedQuery.getResultList();
        Long resultCount = findAllProductsCount( searchCriteria );
        ProductSearchResult result = new ProductSearchResult();
        result.setProducts(products);
        result.setTotalResultNumber(resultCount);
        result.setCurPageNumber(searchCriteria.page.getPageNumber());
        result.setCurPageSize(searchCriteria.page.getPageSize());
        return result;
    }

    public Long findAllProductsCount(SearchCriteria searchCriteria) throws Exception {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Product> product = query.from(Product.class);
        if (searchCriteria.searchType.equals("FILTERED")) {
            Predicate predicate = processPredicates(searchCriteria.predicateNode, cb, product);
            query.select(cb.count(product)).where(predicate);
        }else if (searchCriteria.searchType.equals("ALL")){
            query.select(cb.count(product));
        }
        return entityManager.createQuery(query).getSingleResult();
    }
}