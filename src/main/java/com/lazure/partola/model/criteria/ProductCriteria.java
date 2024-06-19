package com.lazure.partola.model.criteria;

import java.util.List;
import java.util.Optional;

/**
 * @author Ivan Partola
 */
public record ProductCriteria (
        Optional<String> title,
        Double maxPrice,
        Double minPrice,
        Optional<List<Integer>> categoryId) {
}
