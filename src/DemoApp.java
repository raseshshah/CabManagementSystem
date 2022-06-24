import model.Cab;
import model.CabState;
import model.Location;
import repository.BookingRepository;
import repository.CabRepository;
import repository.CabStateChangeRepository;
import repository.LocationRepository;
import repository.inmemory.BookingInMemoryRepository;
import repository.inmemory.CabInMemoryRepository;
import repository.inmemory.CabStateChangeInMemoryRepository;
import repository.inmemory.LocationInMemoryRepository;
import service.BookingService;
import stratergy.CabSelectionStrategy;

import java.time.Instant;
import java.util.Arrays;

public class DemoApp {
    //data
    static Location[] cities = new Location[]{new Location("Gujarat","Gandhinagar"), new Location("Gujarat","Ahmedabad"), new Location("Gujarat", "Vadodadra"), Location.UNKNOWN};
    static Cab[] cabs = new Cab[]{
            new Cab("GJ001MX", CabState.IDLE, cities[0], Instant.now().minusSeconds(100).toEpochMilli()),
            new Cab("GJ001MP", CabState.IDLE, cities[0]),
            new Cab("GJ002MX", CabState.IDLE, cities[1]),
            new Cab("GJ003MX", CabState.IDLE, cities[2]),
    };

    static long statTs = System.currentTimeMillis();

    //repo
    static LocationRepository locationRepository = new LocationInMemoryRepository();
    static CabRepository cabRepository = new CabInMemoryRepository(locationRepository);
    static CabStateChangeRepository cabStateChangeRepository = new CabStateChangeInMemoryRepository();
    static BookingRepository bookingRepository = new BookingInMemoryRepository();

    //services
    static BookingService bookingService = new BookingService(cabRepository, locationRepository, CabSelectionStrategy.MAX_IDLE, bookingRepository);

    public static void main(String[] args) throws InterruptedException {
        System.out.println("---------------Welcome to cab management system --------");
        System.out.println("Initializing the database with cabs and operating locations");
        initDatabase();
        statusOfAllCabs("before booking");
        Thread.sleep(1000);
        doBookings();
        statusOfAllCabs("after booking");
        Thread.sleep(1000);
        releaseCabs();
        statusOfAllCabs("after releasing cabs");
        idleTimeForCabs();
        stateChanges();
        //System.out.println("demand: " + bookingRepository.getLocationAndTimeWhichHasHigherDemand());
    }

    public static void initDatabase() {
        cabRepository.registerChangeListener(cabStateChangeRepository);
        Arrays.stream(cities).forEach(locationRepository::create);
        Arrays.stream(cabs).forEach(cabRepository::create);
    }

    public static void doBookings() {
        bookingService.bookACab(cities[0], cities[1], 0);
        bookingService.bookACab(cities[0], cities[2], 0);
        bookingService.bookACab(cities[1], cities[2], 0);
    }

    public static void releaseCabs() {
        bookingService.releaseACab(bookingRepository.get(0));
        bookingService.releaseACab(bookingRepository.get(1));
        bookingService.releaseACab(bookingRepository.get(2));
    }

    public static void stateChanges() {
        System.out.println("state change logs:");
        for (Cab cab : cabs) {
            System.out.println(cabStateChangeRepository.logs(cab.id(), statTs, System.currentTimeMillis()));
        }
    }

    public static void statusOfAllCabs(String msg) {
        System.out.println("status of cabs:" + msg);
        for (Cab cab : cabs) {
            System.out.println(cabRepository.get(cab.id()));
        }
    }

    public static void idleTimeForCabs() {
        System.out.println("idle time of cabs:");
        for (Cab cab : cabs) {
            System.out.println(cabRepository.get(cab.id()) + " " + cabStateChangeRepository.totalIdleTimeInGivenDuration(cab.id(), statTs, System.currentTimeMillis()) + " ms");
        }
    }
}
