package repository.inmemory;

import model.*;
import repository.BookingRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BookingInMemoryRepository implements BookingRepository {
    private final AtomicInteger count = new AtomicInteger();
    private final List<Booking> bookings = Collections.synchronizedList(new ArrayList<>());

    @Override
    public Booking create(Cab cab, Location source, Location destination, long charge, BookingStatus status) {
        int id = count.incrementAndGet();
        Booking booking = new Booking(id, System.currentTimeMillis(), cab, source, destination, charge, status);
        bookings.add(booking);
        return booking;
    }

    @Override
    public Booking get(int bookingId) {
        if (bookingId < 1 || bookingId > count.get()) throw new IllegalArgumentException("booking id invalid");
        return bookings.get(bookingId - 1);
    }

    @Override
    public Optional<NoOfBookingRequestsInLocation> getLocationAndTimeWhichHasHigherDemand(long startTs, long endTs) {
        Calendar c = Calendar.getInstance();
        Map<NoOfBookingRequestsInLocation, Long> grp = bookingsInRange(startTs, endTs).collect(Collectors.groupingBy((b) -> {
            c.setTimeInMillis(b.timestamp());
            return new NoOfBookingRequestsInLocation(b.source(), c.get(Calendar.HOUR));
        }, Collectors.counting()));
        return grp.entrySet().stream().map((es) -> es.getKey().withNoOfBookingRequest(es.getValue())).max(Comparator.comparingLong(NoOfBookingRequestsInLocation::noOfBookingRequests));
    }

    @Override
    public long totalEarningFromBookings(Cab cab, long startTs, long endTs) {
        return bookingsInRange(startTs, endTs).filter(b -> b.cab().id().equals(cab.id())).mapToLong(Booking::charge).sum();
    }

    private Stream<Booking> bookingsInRange(long startTs, long endTs) {
        return bookings.stream().filter(b -> b.timestamp() >= startTs && b.timestamp() <= endTs);
    }

}
