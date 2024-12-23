package ru.practicum.explorewithme.utils;

import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SortUtils {

    private static final Integer SPRING_CONTROLLER_LIST_SIZE = 2;
    private static final Pattern QUERY_VALUE_PATTERN = Pattern.compile("^\\w+(\\.\\w+){0,2},(?i)(asc|desc)$");
    private static final String DELIMITER_COMMA = ",";
    private static final Pattern DELIMITER_PATTERN = Pattern.compile(",");

    public static Sort by(List<String> sort) {
        return CollectionUtils.isEmpty(sort)
                ? Sort.unsorted()
                : Sort.by(tryToConcat(sort).stream()
                .map(SortUtils::by)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    public static Sort.Order by(String query) {
        if (!StringUtils.hasText(query)) {
            return null;
        } else if (!QUERY_VALUE_PATTERN.matcher(query).matches()) {
            throw new RuntimeException("Invalid query: " + query);
        } else {
            String[] parts = DELIMITER_PATTERN.split(query, 2);
            return new Sort.Order(Sort.Direction.fromString(parts[1]), parts[0]);
        }
    }

    private static String by(Sort.Order order) {
        return String.join("", order.getProperty(), ",", order.getDirection().name());
    }

    private static List<String> tryToConcat(List<String> sort) {
        if (sort.size() == SPRING_CONTROLLER_LIST_SIZE) {
            String concat = String.join(",", sort);
            if (QUERY_VALUE_PATTERN.matcher(concat).matches()) {
                return List.of(concat);
            }
        }

        return sort;
    }
}
