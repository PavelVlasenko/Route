import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final int BALANCED_COUNT = 3;

    private static final String INPUT_FILE = "input.csv";
    private static final String BALANCED_FILE = "output.csv";

    public static void main(String[] args)
    {
        CsvParser csvParser = new CsvParser();
        List<Home> homeListOrign = csvParser.parseFile(INPUT_FILE);
        List<Home> homeList = new ArrayList<>(homeListOrign);
        System.out.println("===== File input.csv is loaded =====");
        System.out.println(homeList);

        System.out.println("===== Begin route optimization =====");
        HomeProcessor homeProcessor = new HomeProcessor();

        Home basicHome = homeProcessor.getNearestToCorner(homeList);
        System.out.println("HomeList size:  " + homeList.size());

        homeList.remove(basicHome);

        List<List<Home>> days = new ArrayList<>();

        while (homeList.size() > 0)
        {
            List<Home> day = homeProcessor.fillDay(basicHome, homeList);

            for (Home home : day)
            {
                homeList.remove(home);
            }

            Home newBasicHome = homeProcessor.findNearestHome(basicHome, homeList);
            basicHome = newBasicHome;
            homeList.remove(basicHome);
            days.add(day);
        }

        for (int i =0; i< BALANCED_COUNT; i++)
        {
            days = homeProcessor.optimizeRoute(days, homeListOrign);
        }

        List<List<Home>> balancedDays = homeProcessor.minimizeDriveTime(days);

        System.out.println("\n===== Total time (Balanced list): " + (int)homeProcessor.calculateTotalTime(days) + " min\n");

        for(List<Home> day : balancedDays)
        {
            System.out.println("\n===== DAY " + (balancedDays.indexOf(day)+1)+ " =============================================================================");
            System.out.println("Homes: " + day);
            System.out.println("Number of houses: " + day.size());
            System.out.println("Total time: " + homeProcessor.calculateTime(day) + " min");
        }

        homeProcessor.showDriveTime(balancedDays);
        csvParser.writeFile(balancedDays, BALANCED_FILE);
    }

}
