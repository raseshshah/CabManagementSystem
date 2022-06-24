package repository.inmemory;

import model.*;
import repository.BookingRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class BookingInMemoryRepository implements BookingRepository {
    private final AtomicInteger count = new AtomicInteger();
    private final List<Booking> bookings = Collections.synchronizedList(new ArrayList<>());

    @Override
    public Booking create(Cab cab, Location source, Location destination, BookingStatus status) {
        int id = count.incrementAndGet();
        Booking booking = new Booking(id, System.currentTimeMillis(), cab, source, destination, status);
        bookings.add(booking);
        return booking;
    }

    @Override
    public Booking get(int bookingId) {
        if (bookingId < 1 || bookingId > count.get()) throw new IllegalArgumentException("booking id invalid");
        return bookings.get(bookingId - 1);
    }

    @Override
    public Optional<NoOfBookingRequestsInLocation> getLocationAndTimeWhichHasHigherDemand() {
        Calendar c = Calendar.getInstance();
        Map<NoOfBookingRequestsInLocation, Long> grp = bookings.stream().collect(Collectors.groupingBy((b) -> {
            c.setTimeInMillis(b.timestamp());
            return new NoOfBookingRequestsInLocation(b.source(), c.get(Calendar.HOUR));
        }, Collectors.counting()));
        return grp.entrySet().stream().map((es) -> es.getKey().withNoOfBookingRequest(es.getValue())).max(Comparator.comparingLong(NoOfBookingRequestsInLocation::noOfBookingRequests));
    }
}
