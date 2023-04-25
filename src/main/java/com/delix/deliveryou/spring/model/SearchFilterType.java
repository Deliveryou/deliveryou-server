package com.delix.deliveryou.spring.model;

import java.time.LocalDate;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class SearchFilterType {
    public static final int USER_ID = 0;
    public static final int PHONE = 1;
    public static final int NAME = 2;
    public static final int BIRTH_YEAR = 3;
    public static final int ALL = 4;
    public static final int CITIZEN_ID = 5;

    private static final int MIN_INDEX = 0;
    private static final int MAX_INDEX = 5;

    public static class InvalidSearchFilterException extends RuntimeException {

        public InvalidSearchFilterException() {
        }

        public InvalidSearchFilterException(String message) {
            super(message);
        }

        public InvalidSearchFilterException(String message, Throwable cause) {
            super(message, cause);
        }

        public InvalidSearchFilterException(Throwable cause) {
            super(cause);
        }

        public InvalidSearchFilterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    public static class FilterEngine {
        private final SearchFilter _filter;
        private boolean filtered = false;

        private FilterEngine(SearchFilter searchFilter) {
            _filter = searchFilter;
        }

        public FilterEngine whenFilterByUserId(Consumer<Long> consumer) {
            if (_filter != null && consumer != null && !filtered && _filter.getType() == USER_ID) {
                filtered = true;
                long id = Long.parseLong(_filter.getValue());
                consumer.accept(id);
            }
            return this;
        }

        public FilterEngine whenFilterByPhone(Consumer<String> consumer) {
            if (_filter != null && consumer != null && !filtered && _filter.getType() == PHONE) {
                filtered = true;
                String phone = _filter.getValue().trim();
                consumer.accept(phone);
            }
            return this;
        }

        public FilterEngine whenFilterByName(Consumer<String> consumer) {
            if (_filter != null && consumer != null && !filtered && _filter.getType() == NAME) {
                filtered = true;
                String name = _filter.getValue().trim().replaceAll("\\s{2,}", " ");
                consumer.accept(name);
            }
            return this;
        }

        public FilterEngine whenFilterByBirthYear(Consumer<Integer> consumer) {
            if (_filter != null && consumer != null && !filtered && _filter.getType() == BIRTH_YEAR) {
                filtered = true;
                int year = Integer.parseInt(_filter.getValue());
                consumer.accept(year);
            }
            return this;
        }

        public FilterEngine whenFilterIsInvalid(Runnable runnable) {
            if (_filter == null)
                runnable.run();
            return this;
        }

        public FilterEngine whenGetAll(Runnable runnable) {
            if (_filter != null && runnable != null && _filter.getType() == ALL) {
                filtered = true;
                runnable.run();
            }
            return this;
        }

        public FilterEngine whenFilterByCitizenId(Consumer<String> consumer) {
            if (_filter != null && consumer != null && !filtered && _filter.getType() == CITIZEN_ID)
                consumer.accept(_filter.getValue().trim());
            return this;
        }
    }

    /**
     * @param filter
     * @return a [FilterEngine] instance
     */
    public static FilterEngine getEngine(SearchFilter filter) {
        if (validateFilter(filter))
            return new FilterEngine(filter);

        return new FilterEngine(null);
    }

    /**
     * validate if a [SearchFilter] instance is valid
     * @param filter
     * @return true if valid
     */
    public static boolean validateFilter(SearchFilter filter) {
        if (filter == null)
            return false;

        // validate type index
        int type = filter.getType();
        if (type < MIN_INDEX || type > MAX_INDEX)
            return false;
        else if (type == ALL) {
            return true;
        }

        // validate string value
        String value = filter.getValue();
        if (value == null)
            return false;

        try {
            switch (filter.getType()) {
                case SearchFilterType.USER_ID -> Long.parseLong(value);
                case SearchFilterType.BIRTH_YEAR -> {
                    int year = Integer.parseInt(value);
                    if (year < 1900 || year > LocalDate.now().getYear())
                        return false;
                }
                case SearchFilterType.PHONE -> {
                    Pattern pattern = Pattern.compile("^(\\+|0)\\d*$");
                    if (!pattern.matcher(value.trim()).find())
                        return false;
                }
                case SearchFilterType.NAME -> {
                    if (value.trim().length() == 0)
                        return false;
                }
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Match Vietnamese phone number to a phone filter
     * @param phoneFilterValue
     * @param phoneNumber
     * @return true if matches
     * @throws InvalidSearchFilterException if either of the params is null
     */
    public static boolean phoneFilterMatcher(String phoneFilterValue, String phoneNumber) {
        if (phoneFilterValue == null || phoneNumber == null)
            throw new InvalidSearchFilterException();

        // test if phoneFilterValue and phoneNumber have the correct format
        Pattern pattern = Pattern.compile("^(\\+|0)\\d*$");
        if (!pattern.matcher(phoneFilterValue).find() || !pattern.matcher(phoneNumber).find())
            return false;

        // normalize before comparison
        // 0851234567 or +84851234567 -> 851234567
        phoneFilterValue = phoneFilterValue.replaceAll("^\\+84", "0");

        return phoneNumber.contains(phoneFilterValue);
    }

    /**
     * result:
     * <ul>
     *     <li>[startIndex] will be at least 0</li>
     *     <li>[endIndex] > [startIndex]</li>
     *     <li>if [endIndex] < [startIndex] -> default: [endIndex] = [startIndex] + 10</li>
     * </ul>
     * @param filter
     * @return the param instance
     */
    public static SearchFilter normalizeIndexes(SearchFilter filter) {
        if (filter != null) {
            if (filter.getStartIndex() < 0)
                filter.setStartIndex(0);
            if (filter.getEndIndex() < filter.getStartIndex())
                filter.setEndIndex(filter.getStartIndex() + 10);
        }
        return filter;
    }
}
