package com.lazure.partola.model.criteria;

import java.util.List;
import java.util.Optional;

/**
 * @author Ivan Partola
 */
public record ProductCriteria (
        Optional<String> title,
        Optional<List<Integer>> categoryId) {
}
