package app.utils;

public final class Constants {
    private Constants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    //Calender bounds;
    public static final int FIRST_DAY = 1;
    public static final int FIRST_MONTH = 1;
    public static final int LAST_DAY = 31;
    public static final int LAST_MONTH = 12;
    public static final int YEAR_LOWER_BOUND = 1900;
    public static final int YEAR_UPPER_BOUND = 2023;
    public static final int APRIL = 4;
    public static final int JUNE = 6;
    public static final int SEPTEMBER = 9;
    public static final int NOVEMBER = 11;
    public static final int FEBRUARY = 2;
    public static final int THIRTY_BOUND = 30;
    public static final int TWENTY_NINE_BOUND = 29;

    //String bounds;
    public static final int DAY_LOWER_BOUND = 0;
    public static final int DAY_UPPER_BOUND = 2;
    public static final int MONTH_LOWER_BOUND = 3;
    public static final int MONTH_UPPER_BOUND = 5;
    public static final int YEAR_LOWER_INDEX = 6;
    public static final int YEAR_UPPER_INDEX = 10;

    //SEARCH LIMIT BOUND.
    public static final int LIMIT = 5;
}
