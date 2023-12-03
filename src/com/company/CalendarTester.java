package com.company;

/**
 * CalendarTester contains a main method that tests the CalendarModel and CalendarView classes.
 */
public class CalendarTester {
    /**
     * Produces a main menu that allows the user to interact with a calendar.
     * @param args any arguments passed during run time
     */
    public static void main(String [] args) {
        String filename = "events.txt";
        CalendarModel calendarModel = new CalendarModel();
        CalendarView view = new CalendarView(calendarModel);
        calendarModel.saveToFile(filename);
        calendarModel.loadFromFile(filename);
        view.display();
    }
}
