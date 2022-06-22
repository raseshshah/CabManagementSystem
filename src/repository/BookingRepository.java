package repository;

import model.*;

import java.util.Optional;

public interface BookingRepository {
    Booking create(Cab cab, Location source, Location destination, BookingStatus status);
    Booking get(int bookingId);
    Optional<NoOfBookingRequestsInLocation> getLocationAndTimeWhichHasHigherDemand();
}
