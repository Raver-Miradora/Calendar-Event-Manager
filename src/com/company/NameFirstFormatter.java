package com.company;
/**
 * This file contains a class that formats an event's information in a name-first format.
 */

/**
 * Represents an event formatter that formats events as "Name of Event: startTime - endTime".
 */
public class NameFirstFormatter implements EventFormatter {
    /**
     * Returns a string with an event's name and time interval.
     * @param event the event whose information will be formatted
     * @return a string with the event's information
     */
    @Override
    public String formatEvent(Event event) {
        return event.getName() + ": " + event.getTimeInterval().toString() + "\n";
    }
}
