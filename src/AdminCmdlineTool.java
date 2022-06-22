import model.Booking;
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
import java.util.Scanner;

public class AdminCmdlineTool {
    //repo
    static LocationRepository locationRepository = new LocationInMemoryRepository();
    static CabRepository cabRepository = new CabInMemoryRepository(locationRepository);
    static CabStateChangeRepository cabStateChangeRepository = new CabStateChangeInMemoryRepository();
    static BookingRepository bookingRepository = new BookingInMemoryRepository();

    //services/
    static BookingService bookingService = new BookingService(cabRepository, locationRepository, CabSelectionStrategy.MAX_IDLE, bookingRepository);

    static Scanner in = new Scanner(System.in);

    static {
        cabRepository.registerChangeListener(cabStateChangeRepository);
        locationRepository.create(Location.UNKNOWN);
    }

    static void printAdminCmd() {
        System.out.println("1. onboard serviceable location (state_name, city_name)");
        System.out.println("2. onboard a new cab (cab_reg_id, cab_state(IDLE,ON_TRIP), location_id)");
        System.out.println("3. update an exiting cab (cab_reg_id, cab_state(IDLE,ON_TRIP), location_id)");
        System.out.println("4. book a cab (source_location_id, dest_location_id)");
        System.out.println("5. release a cab (booking_id)");
        System.out.println("6. check status of cab (all | cab_id)");
        System.out.println("7. check idle time of the cab (cab_id)");
        System.out.println("8. find the city and hour which is having highest booking demand");
        System.out.println("9. check cab state change log (cab_id)");
        System.out.println("10. exit");
    }

    public static void main(String[] args) {
        System.out.println("-----Welcome to Cab management admin panel -------");
        printAdminCmd();
        System.out.println("--------------------------------------------------");
        loop:
        while (true) {
            try {
                switch (waitForCmdId(in)) {
                    case 1:
                        onboardLocation();
                        break;
                    case 2:
                        onboardCab();
                        break;
                    case 3:
                        updateCab();
                        break;
                    case 4:
                        bookACab();
                        break;
                    case 5:
                        releaseACab();
                        break;
                    case 6:
                        checkStatusOfCab();
                        break;
                    case 7:
                        checkIdleTime();
                        break;
                    case 8:
                        checkBookingDemand();
                        break;
                    case 9:
                        checkStateChange();
                        break;
                    case 10:
                        break loop;
                    default:
                        throw new IllegalArgumentException("invalid cmd");
                }
            } catch (Exception ex) {
                System.out.println("error: " + ex.getMessage());
            }
        }
    }

    static Cab parseCab(Scanner in) {
        String[] ins = parseCommaSeparateInput(in, 3);
        return new Cab(ins[0], CabState.valueOf(ins[1]), locationRepository.get(Integer.parseInt(ins[2])));
    }

    static Location parseLocation(Scanner in) {
        String[] ins = parseCommaSeparateInput(in, 2);
        return new Location(ins[0], ins[1]);
    }

    static String[] parseCommaSeparateInput(Scanner in, int expectedLength) {
        System.out.print("comma separated input>");
        String[] ins = in.nextLine().trim().split(",");
        if (ins.length != expectedLength)
            throw new IllegalArgumentException("expected " + expectedLength + " comma separate input");
        return ins;
    }

    static int waitForCmdId(Scanner in) {
        System.out.print("cmd id>");
        try {
            return Integer.parseInt(in.nextLine());
        } catch (Exception ex) {
            throw new IllegalArgumentException("provide valid cmd id");
        }
    }

    static void onboardCab() {
        cabRepository.create(parseCab(in));
        System.out.println("onboarded new cab");
    }

    static void onboardLocation() {
        int id = locationRepository.create(parseLocation(in)).id();
        System.out.println("onboarded location with unique id: " + id);
    }

    static void updateCab() {
        Cab updatedCab = parseCab(in);
        boolean updated = cabRepository.replace(cabRepository.get(updatedCab.id()), updatedCab);
        if (updated) System.out.println("updated successful");
        else System.out.println("update failed");
    }

    static void bookACab() {
        String[] ins = parseCommaSeparateInput(in, 2);
        Booking booking = bookingService.bookACab(locationRepository.get(Integer.parseInt(ins[0])), locationRepository.get(Integer.parseInt(ins[1])));
        System.out.println(booking);
    }

    static void releaseACab() {
        int bookingId = Integer.parseInt(parseCommaSeparateInput(in, 1)[0]);
        bookingService.releaseACab(bookingRepository.get(bookingId));
        System.out.println("released a cab");
    }

    static void checkStatusOfCab() {
        String arg = parseCommaSeparateInput(in, 1)[0];
        if (arg.equals("all")) {
            System.out.println(cabRepository.getAll());
        } else {
            System.out.println(cabRepository.get(arg));
        }
    }

    static void checkIdleTime() {
        String arg = parseCommaSeparateInput(in, 1)[0];
        System.out.println(cabStateChangeRepository.totalIdleTimeInGivenDuration(arg, Instant.now().minusSeconds(2 * 60 * 60).toEpochMilli(), Instant.now().toEpochMilli())+" ms");
    }

    static void checkStateChange() {
        String arg = parseCommaSeparateInput(in, 1)[0];
        System.out.println(cabStateChangeRepository.logs(arg, Instant.now().minusSeconds(2 * 60 * 60).toEpochMilli(), Instant.now().toEpochMilli())+" ms");
    }

    static void checkBookingDemand() {
        System.out.println(bookingRepository.getLocationAndTimeWhichHasHigherDemand());
    }
}
