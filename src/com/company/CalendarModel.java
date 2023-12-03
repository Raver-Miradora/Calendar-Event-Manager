package com.company;
/**
 * Contains a class thar represents a calendar.
 */
import javax.swing.*;
import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * CalendarModel represents a calendar and defines an underlying data structure to hold events.
 */

public class CalendarModel {
    private final ArrayList<Event> events; // can be one-time or recurring
    private String viewType; // can be either "Day", "Week", "Month", or "Agenda"
    private LocalDate dateToView; // default will be whatever date today is
    private CalendarView calendarView; // for mvc architecture
    private String eventsToView; // will be used to set the text area in calendarView
    private LocalDate agendaStartDate; // will be used for agenda view
    private LocalDate agendaEndDate; // will be used for agenda view
    private EventFormatter formatter; //for the strategy patter requirement. Formats the events in the text area

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    /**
     * Constructs a MyCalendar object from the contents of events.txt.
     */

    public CalendarModel() {
        viewType = "Day";
        dateToView = LocalDate.now();
        events = new ArrayList<>(); // initializing events ArrayList
        loadFromFile("events.txt"); //populating events ArrayList
        formatter = new NameFirstFormatter(); //initializing formatter
        eventsToView = dayViewAsString(dateToView); //initializing contents of text area
    }

    /**
     * Gets the ArrayList of events.
     */

    public ArrayList<Event> getEvents() {
        return events;
    }

    /**
     * Checks if an event conflicts with any existing events in the calendar and adds it to the calendar if not.
     * @param newEvent the event to be added
     */
    public void addEvent(Event newEvent) {
        for (Event e : events) {
            if (e.conflicts(newEvent)) {
                JPanel panel = new JPanel();
                JOptionPane.showMessageDialog(panel, "Error: cannot add " + newEvent.getName() + " due to a " +
                        "time conflict with " + e.getName() + ".", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        events.add(newEvent);
        Collections.sort(events);
    }

    /**
     * Sets the view type.
     * @param viewType the time span type to view
     * precondition: Should only take "Day", "Week", and "Month" for the parameter.
     */
    public void setViewType(String viewType) {
        this.viewType = viewType;
        setEventsToView();
    }

    /**
     * Sets the view type to Agenda view.
     * @param startDate the start date of the time span to view
     * @param endDate the end date of the time span to view
     */
    public void setViewType(String startDate, String endDate) {
        String previousViewType = viewType;
        try {
        viewType = "Agenda";
        agendaStartDate = LocalDate.parse(startDate, DATE_FORMATTER);
        agendaEndDate = LocalDate.parse(endDate, DATE_FORMATTER);
        setEventsToView();
        }
        catch (DateTimeParseException dateTimeParseException) {
            viewType = previousViewType;
            JPanel panel = new JPanel();
            JOptionPane.showMessageDialog(panel, "Invalid inputs or formats for one or both dates.\n Required" +
                    " format: MM/DD/YYYY.\n Example: type 01/02/2021 for January 2nd, 2021", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Gets the date to view.
     * @return the date to view
     */
    public LocalDate getDateToView() {
        return dateToView;
    }

    /**
     * Sets date to view to a specified date. Used when clicking Today button or dates in current calendar.
     * @param dateParam the date to view
     */
    public void setDateToView(LocalDate dateParam) {
        dateToView = dateParam;
        setEventsToView();
    }

    /**
     * Sets dateToView to a date either one day, week, or month after the current dateToView depending on the
     * current viewType.
     */
    public void setNextDateToView() {
        switch (viewType) {
            case "Day" -> dateToView = dateToView.plusDays(1);
            case "Week" -> dateToView = dateToView.plusWeeks(1);
            case "Month" -> dateToView = dateToView.plusMonths(1);
            default ->  {
                JPanel panel = new JPanel();
                JOptionPane.showMessageDialog(panel, "Invalid view type for moving date forward.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        setEventsToView();
    }

    /**
     * Sets dateToView to a date either one day, week, or month before the current dateToView depending on the
     * current viewType.
     */
    public void setPreviousDateToView() {
        switch (viewType) {
            case "Day" -> dateToView = dateToView.minusDays(1);
            case "Week" -> dateToView = dateToView.minusWeeks(1);
            case "Month" -> dateToView = dateToView.minusMonths(1);
            default ->  {
                JPanel panel = new JPanel();
                JOptionPane.showMessageDialog(panel, "Invalid view type for moving date back.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        setEventsToView();
    }

    /**
     * Gets the string (eventsToView) that will be used to set the text area in CalendarView.
     * @return a string with a list of all events to view
     */
    public String getEventsToView() {
        return eventsToView;
    }

    /**
     * Updates the string (eventsToView) that will be used to set the text area in CalendarView.
     * precondition: agendaStartDate occurs before agendaEndDate
     */
    public void setEventsToView() {
        switch (viewType) {
            case "Day" -> eventsToView = dayViewAsString(dateToView);
            case "Week" -> {
                eventsToView = "";
                LocalDate temp = dateToView;
                while (!temp.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                    temp = temp.minusDays(1);
                }
                for (int i = 0; i < 7; i++) {
                    eventsToView += dayViewAsString(temp);
                    temp = temp.plusDays(1);
                }
            }
            case "Month" -> {
                eventsToView = "";
                LocalDate temp = LocalDate.of(dateToView.getYear(), dateToView.getMonthValue(), 1);
                while (temp.getMonth().equals(dateToView.getMonth())) {
                    eventsToView += dayViewAsString(temp);
                    temp = temp.plusDays(1);
                }
            }
            case "Agenda" -> {
                eventsToView = "";
                LocalDate temp = agendaStartDate;
                while (!temp.isAfter(agendaEndDate)) {
                    eventsToView += dayViewAsString(temp);
                    temp = temp.plusDays(1);
                }
            }
            default -> {
                JPanel panel = new JPanel();
                JOptionPane.showMessageDialog(panel, "Invalid view type for showing events.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        notifyCalendarView();
    }

      public void saveToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Event event : events) {
                writer.println(event.toFileString()); // Convert event to a string representation
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        public void loadFromFile(String filename) {
            // Clear existing events before loading
            events.clear();
            try (Scanner scanner = new Scanner(new File(filename))) {
                while (scanner.hasNextLine()) {
                    String eventData = scanner.nextLine();
                    Event event = Event.parseFromFileString(eventData); // Parse string back to Event object
                    events.add(event);
                }
            } catch (FileNotFoundException e) {
                createEmptyFile(filename);
            }
        }

        private void createEmptyFile(String filename) {
            try {
                File file = new File(filename);
                if (file.createNewFile()) {
                    System.out.println("File created: " + file.getName());
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException e) {
                System.out.println("An error occurred while creating the file.");
                e.printStackTrace();
            }
        }


    /**
     * Registers a CalendarView.
     * @param view the CalendarView to register
     */
    public void registerCalendarView(CalendarView view) {
        calendarView = view;
    }

    /**
     * Notifies the CalendarView of any changes made to CalendarModel.
     */
    public void notifyCalendarView() {
        calendarView.update(eventsToView);
    }

    /**
     * Sets the formatter to a specific EventFormatter
     * @param formatter the formatter to set this formatter to
     */
    public void setFormatter(EventFormatter formatter) {
        this.formatter = formatter;
        setEventsToView();
    }

    /**
     * Returns a string displaying a date along with scheduled events in order of event start time.
     * @param date the date to be displayed
     * @return a string with a date and events
     */
    private String dayViewAsString(LocalDate date) {
        ArrayList<Event> dayEvents = new ArrayList<>();
        String dateAndEvents = date.getDayOfWeek() + ", " + date.getMonth() + " " + date.getDayOfMonth() + ", " +
                date.getYear() + "\n";
        for (Event e : events) {
            if (e.getDates().contains(date)) {
                dayEvents.add(new Event(e.getName(), date.format(DATE_FORMATTER),
                        e.getTimeInterval().getStartTime().toString(), e.getTimeInterval().getEndTime().toString()));
            }
        }
        Collections.sort(dayEvents);
        for (Event e : dayEvents) {
            dateAndEvents += formatter.formatEvent(e);
        }
        dateAndEvents += "\n";
        return dateAndEvents;
    }
}
