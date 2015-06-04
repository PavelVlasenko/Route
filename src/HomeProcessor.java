import java.util.*;

public class HomeProcessor
{
    public Home findNearestHome(Point basicPoint, List<Home> homeList)
    {
        Home nearestHome = null;
        double xb = basicPoint.getX();
        double yb = basicPoint.getY();

        double rmin = Double.POSITIVE_INFINITY;
        for (Home home : homeList)
        {
            double x = home.getPoint().getX();
            double y = home.getPoint().getY();

            double r = Math.sqrt((x-xb)*(x-xb) + (y-yb)*(y-yb));
            if (r < rmin)
            {
                rmin = r;
                nearestHome = home;
            }
        }
        return nearestHome;
    }

    public Home findNearestHome(Home basicHome, List<Home> homeList)
    {
       return findNearestHome(basicHome.getPoint(), homeList);
    }

    public List<Home> fillDay(Home basicHome, List<Home> homeListOrign)
    {
        List<Home> homeList = new ArrayList<Home>(homeListOrign);
        List<Home> dayList = new ArrayList<Home>();
        dayList.add(basicHome);
        double time = basicHome.getDuration();
        while (time < 480d & homeList.size() > 0)
        {
            Home nearestHome = findNearestHome(basicHome, homeList);
            //System.out.println("Add home to day: " + nearestHome);
            dayList.add(nearestHome);
            time = calculateTime(dayList);
            if (time > 480d)
            {
               dayList.remove(nearestHome);
               return dayList;
            }
            homeList.remove(nearestHome);
        }
        return dayList;
    }

    public double calculateTime(List<Home> dayListOrign)
    {
        if(dayListOrign.size() < 2)
        {
            return  dayListOrign.get(0).getDuration();
        }
        List<Home> dayList = new ArrayList<Home>(dayListOrign);
        Home basicPoint = dayList.remove(0);
        Home endPoint = findNearestHome(basicPoint, dayList);
        dayList.remove(endPoint);
        double time = basicPoint.getDuration() + endPoint.getDuration() + getDriveTime(basicPoint, endPoint);
        while (dayList.size() > 0)
        {
            Home currentBasicHome = findNearestHome(basicPoint, dayList);
            double driveBasic = getDriveTime(basicPoint, currentBasicHome);
            Home currentEndHome = findNearestHome(endPoint, dayList);
            double driveEnd = getDriveTime(endPoint, currentEndHome);

            if(driveBasic < driveEnd)
            {
                time = time + getDriveTime(basicPoint, currentBasicHome) + currentBasicHome.getDuration();
                basicPoint = currentBasicHome;
                dayList.remove(currentBasicHome);
            }

            else
            {
                time = time + getDriveTime(endPoint, currentEndHome) + currentEndHome.getDuration();
                endPoint = currentEndHome;
                dayList.remove(currentEndHome);
            }

        }
        return time;
    }

    public double calculateTotalDriveTime(List<List<Home>> days)
    {
        double totalTime = 0d;
        for(List<Home> day : days)
        {
            totalTime +=calculateDayDriveTime(day);
        }
        return totalTime;
    }

    private double calculateDayDriveTime(List<Home> day)
    {
        double dayTime = 0d;
        if(day.size() > 2)
        {
            for (int i = 1; i < day.size(); i++) {
                double curTime = getDriveTime(day.get(i), day.get(i-1));
                dayTime += curTime;
            }
        }
        return dayTime;
    }

    private double getDriveTime(Home home1, Home home2)
    {
        double x1 = home1.getPoint().getX();
        double y1 = home1.getPoint().getY();

       return getDriveTime(x1, y1, home2);
    }

    private double getDriveTime(double x1, double y1, Home home2)
    {
        double x2 = home2.getPoint().getX();
        double y2 = home2.getPoint().getY();

        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }

    private double getDriveTime(Point point, Home home)
    {
        double x1 = point.getX();
        double y1 = point.getY();

        double x2 = home.getPoint().getX();
        double y2 = home.getPoint().getY();

        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }

    /**
     *   Find point, nearest to corner
     */
    public Home getNearestToCorner(List<Home> homeListOrign)
    {
        List<Home> homeList = new ArrayList<Home>(homeListOrign);
        double maxX = 0d;
        double maxY = 0d;

        for (Home home : homeList)
        {
            double x = home.getPoint().getX();
            double y = home.getPoint().getY();
            if(x > maxX)
            {
                maxX = x;
            }
            if(y > maxY)
            {
                maxY = y;
            }
        }

        Home h1 = findNearestHome(new Point(0, 0), homeList);
        double r1 = getDriveTime(0, 0, h1);
        homeList.remove(h1);

        Home h2 = findNearestHome(new Point(maxX, 0), homeList);
        double r2 = getDriveTime(maxX, 0, h2);
        homeList.remove(h2);

        Home h3 = findNearestHome(new Point(maxX, maxY), homeList);
        double r3 = getDriveTime(maxX, maxY, h3);
        homeList.remove(h3);

        Home h4 = findNearestHome(new Point(0, maxY), homeList);
        double r4 = getDriveTime(0, maxY, h4);

        double rmin = r1;
        Home nearest = h1;
        if (r2 < rmin) nearest = h2;
        if (r3 < rmin) nearest = h3;
        if (r4 < rmin) nearest = h4;

        return nearest;
    }

    public double calculateTotalTime(List<List<Home>> days)
    {
        double sum = 0d;
        for(List<Home> day : days)
        {
            sum += this.calculateTime(day);
        }
        return sum;
    }

    public List<List<Home>> optimizeRoute(List<List<Home>> days, List<Home> homeListOrign)
    {
        List<Home> homes = new ArrayList<>(homeListOrign);

        Map<Point, List<Home>> points = new LinkedHashMap<>();
        for (List<Home> day : days)
        {
            double xSum = 0;
            double ySum = 0;
            for (Home home : day)
            {
                xSum += home.getPoint().getX();
                ySum += home.getPoint().getY();
            }

            points.put(new Point(xSum / day.size(), ySum / day.size()), new ArrayList<Home>());
        }

        //System.out.println("New centers : " + points.keySet());

        List<List<Home>> newDays = new ArrayList<>(days.size());
        Set<Point> pointSet = points.keySet();

        for (Home home : homes)
        {
            double rMin = Double.POSITIVE_INFINITY;
            List<Home> day = null;
            Point point1 = null;
            boolean add = true;

            while (add) {
                for (Point point : pointSet)
                {
                    double r = getDriveTime(point, home);
                    if (r < rMin) {
                        day = points.get(point);
                        point1 = point;
                        rMin = r;
                    }
                }
                day.add(home);
                double time = calculateTime(day);
                if(time > 480)
                {
                    day.remove(home);
                    pointSet.remove(point1);
                }
                else
                {
                    add = false;
                }
            }
        }
        List<List<Home>> returnDays = new ArrayList<>();
        for (List<Home> day : points.values())
        {
            returnDays.add(day);
        }


        return returnDays;
    }

    public List<List<Home>> minimizeDriveTime(List<List<Home>> daysOrign)
    {
        List<List<Home>> newList =  new ArrayList<>();
        List<List<Home>> days = new ArrayList<>();

        for(List<Home> oldList : daysOrign)
        {
            days.add(new ArrayList<>(oldList));
        }

        for(List<Home> day : days)
        {
            List<Home> newDay = new ArrayList<>();
            Home basicPoint = day.remove(0);
            Home endPoint = findNearestHome(basicPoint, day);
            day.remove(endPoint);

            newDay.add(basicPoint);
            newDay.add(endPoint);

            while (day.size() > 0)
            {
                Home currentBasicHome = findNearestHome(basicPoint, day);
                double driveBasic = getDriveTime(basicPoint, currentBasicHome);
                Home currentEndHome = findNearestHome(endPoint, day);
                double driveEnd = getDriveTime(endPoint, currentEndHome);

                if(driveBasic < driveEnd)
                {
                    newDay.add(0, currentBasicHome);
                    basicPoint = currentBasicHome;
                    day.remove(currentBasicHome);
                }

                else
                {
                    newDay.add( currentEndHome);
                    endPoint = currentEndHome;
                    day.remove(currentEndHome);
                }
            }
            newList.add(newDay);
        }

        return newList;
    }

    public void showAllInfo(List<List<Home>> days)
    {
        String header = "\n====== COMMON INFO ========================================================";
        String totalTime = "TOTAL TIME: %.2f min";
        String totalDayTime = "Total time: %.2f min";
        String totalDriveTime = "TOTAL DRIVE TIME: %.2f min";
        String dayDriveTime = "Day drive time: %.2f min";

        System.out.println(header);
        System.out.println(String.format(totalTime, calculateTotalTime(days)));
        System.out.println(String.format(totalDriveTime, calculateTotalDriveTime(days)));

        for(List<Home> day : days)
        {
            System.out.println("\n===== DAY " + (days.indexOf(day)+1)+ " ===============================================================");
            System.out.println("Homes: " + day);
            System.out.println("Number of houses: " + day.size());
            System.out.println(String.format(totalDayTime, calculateTime(day)));
            System.out.println(String.format(dayDriveTime, calculateDayDriveTime(day)));
        }
    }

}
