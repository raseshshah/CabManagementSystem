package service;

import model.*;
import repository.BookingRepository;
import repository.CabRepository;
import repository.LocationRepository;
import stratergy.CabSelectionStrategy;

import java.util.Set;

public record BookingService(CabRepository cabRepository, LocationRepository locationRepository,
                             CabSelectionStrategy cabSelectionStrategy,
                             BookingRepository bookingRepository) {
    public Booking bookACab(Location source, Location destination, long charge) {
        if (source.equals(Location.UNKNOWN) || destination.equals(Location.UNKNOWN))
            throw new IllegalArgumentException("please provide valid location");
        if (source.equals(destination)) throw new IllegalArgumentException("same city travel is not available");
        if (!locationRepository.exists(source))
            throw new IllegalArgumentException("cab service is not available at location " + source);
        if (!locationRepository.exists(destination))
            throw new IllegalArgumentException("cab service is not available at location " + destination);
        Set<Cab> availableCabs = cabRepository.availableCabs(source);
        Cab selectedCab = null;
        boolean lockedCab = false;
        while (!availableCabs.isEmpty() && !lockedCab) {
            selectedCab = cabSelectionStrategy.select(availableCabs);
            Cab updateCab = selectedCab.withStateAndCity(CabState.ON_TRIP, Location.UNKNOWN);
            lockedCab = cabRepository.replace(selectedCab, updateCab);
            if (lockedCab) selectedCab = updateCab;
            else selectedCab = null;
            availableCabs.remove(selectedCab);
        }
        BookingStatus bookingStatus = lockedCab ? BookingStatus.SUCCESS : BookingStatus.FAIL;
        return bookingRepository.create(selectedCab, source, destination, charge, bookingStatus);
    }

    public void releaseACab(Booking booking) {
        if (booking.status() == BookingStatus.FAIL) throw new IllegalArgumentException("booking wasn't successful");
        if(!cabRepository.get(booking.cab().id()).state().equals(CabState.ON_TRIP)) {
            throw new IllegalArgumentException("cannot already released cab");
        }
        cabRepository.replace(booking.cab(), booking.cab().withStateAndCity(CabState.IDLE, booking.destination()));
    }
}
