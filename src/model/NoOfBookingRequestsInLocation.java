package model;

public record NoOfBookingRequestsInLocation(Location location, int hour, long noOfBookingRequests) {
    public NoOfBookingRequestsInLocation(Location location, int hour) {
        this(location, hour,0);
    }
    public NoOfBookingRequestsInLocation withNoOfBookingRequest(long count) {
       return new NoOfBookingRequestsInLocation(location, hour, count);
    }
}
