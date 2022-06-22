package model;

public record Booking(int id, long timestamp, Cab cab, Location source, Location destination, BookingStatus status) { }
