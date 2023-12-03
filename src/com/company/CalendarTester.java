/*
 * Calendar-Event-Manager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Calendar-Event-Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Calendar-Event-Manager. If not, see 
 * https://github.com/Raver-Miradora/Calendar-Event-Manager
 */

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
