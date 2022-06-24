package repository;

import model.*;

import java.util.Optional;

public interface BookingRepository {
    Booking create(Cab cab, Location source, Location destination, long charge, BookingStatus status);
    Booking get(int bookingId);
    Optional<NoOfBookingRequestsInLocation> getLocationAndTimeWhichHasHigherDemand(long startTs, long endTs);

    long totalEarningFromBookings(Cab cab, long startTs, long endTs);
}
