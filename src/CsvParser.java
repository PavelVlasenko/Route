import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvParser
{
    public List<Home> parseFile(String csvFile)
    {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        List<Home> homeList = new ArrayList<Home>();

        try {

            br = new BufferedReader(new FileReader(csvFile));
            int lineNum = 0;
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] home = line.split(cvsSplitBy);

                for(int index = 0; index < home.length; index ++)
                {
                    String homeItem = home[index];
                    if (!homeItem.isEmpty())
                    {
                        homeList.add(new Home(Integer.parseInt(homeItem), new Point(index, lineNum)));
                    }
                }
                lineNum++;
            }

        }
        catch (FileNotFoundException e)
        {
            System.out.println("File input.csv not found");
        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return homeList;
    }

    public void writeFile(List<List<Home>> days, String csvFile)
    {
        List<Home> homes = new ArrayList<>();
        for(List<Home> day : days)
        {
            homes.addAll(day);
        }

        double xMax = 0d;
        double yMax = 0d;

        for(Home home : homes)
        {
            double x = home.getPoint().getX();
            double y = home.getPoint().getY();
            if(x > xMax)
            {
                xMax = x;
            }
            if(y > yMax)
            {
                yMax = y;
            }
        }
        String[][] array = new String[(int)yMax + 1][(int)xMax + 1];

        for (List<Home> all : days)
        {
            String dl;
            switch (days.indexOf(all))
            {
                case 0: dl = "A"; break;
                case 1: dl = "B"; break;
                case 2: dl = "C"; break;
                case 3: dl = "D"; break;
                case 4: dl = "E"; break;
                default: dl = "Z"; break;
            }

            for(Home home : all)
            {
                int x = (int)home.getPoint().getX();
                int y = (int)home.getPoint().getY();
                int index = all.indexOf(home);
                array[y][x] = dl + index;
            }
        }

        String r = "";

        for (String [] innerArray : array)
        {
            for(String s: innerArray)
            {
                if (s == null)
                {
                    r+=",";
                }
                else r += s + ",";
            }
            r+="\n";
        }


        try
        {
            FileWriter writer = new FileWriter(csvFile);

            writer.append(r);

            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

    }
}
