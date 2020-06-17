package com.gi.rhapp.utilities;

import org.joda.time.DateTime;
import org.joda.time.Days;

public class DateUtils {
    public static int getDaysBetweenIgnoreWeekends(DateTime startDate, DateTime endDate) {

        // If the start date is equal to the closing date, spent 0 days
        if (startDate.equals(endDate))
            return 0;

        // A number that represents the day for the start date, Monday = 1 , Tuesday = 2 , Wednesday = 3 ...
        int dayOfWeekStartDateNumber = startDate.getDayOfWeek();
        int dayOfWeekEndDateNumber = endDate.getDayOfWeek();

        // If the starting date is Saturday or Sunday , pretend to be Monday
        if (dayOfWeekStartDateNumber == 6 || dayOfWeekStartDateNumber == 7) {
            int DaysToAdd = 8 - dayOfWeekStartDateNumber;
            startDate = startDate.plusDays(DaysToAdd);
            dayOfWeekStartDateNumber = Integer.valueOf(startDate.dayOfWeek().getAsString());
        }

        DateTime effectiveEndDate = endDate;

        if (dayOfWeekEndDateNumber == 6 || dayOfWeekEndDateNumber == 7) {
            effectiveEndDate = endDate.minusDays(Math.abs(5 - dayOfWeekEndDateNumber));
        }

        // How many days have passed counting weekends
        int days = Days.daysBetween(startDate.toLocalDate(), effectiveEndDate.toLocalDate()).getDays();


        // How many weeks have passed
        int weeks = days / 7;
        // Excess days left. E.g. one week and three days the excess will be 3

        int excess = days % 7;

        // Excess of days spent for the weekend , then it must be removed two days
        // the final number of days
        if (excess + dayOfWeekStartDateNumber >= 6) {
            // Week count * 5 working days + excess days - the weekend that excess crossed
            return weeks * 5 + excess - 2;
        }
        // Weeks count * 5 working days + excess days
        return weeks * 5 + excess;
    }
}
