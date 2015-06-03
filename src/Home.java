
public class Home
{
    private int duration;
    private Point point;

    public Home(int duration, Point point)
    {
        this.duration = duration;
        this.point = point;
    }

    public int getDuration()
    {
        return duration;
    }

    public void setDuration(int duration)
    {
        this.duration = duration;
    }

    public Point getPoint()
    {
        return point;
    }

    public void setPoint(Point point)
    {
        this.point = point;
    }

    @Override
    public String toString()
    {
        return duration + "(" + point.getX() + ", " + point.getY() + ")";
    }
}
