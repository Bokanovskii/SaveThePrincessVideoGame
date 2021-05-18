import processing.core.PImage;
import java.util.List;

public class MainCharacter extends Entity{
    private static MainCharacter onlyInstance;
    private MainCharacter(String id, Point position, List<PImage> images, int actionPeriod) {
        super(id, position, images, actionPeriod);
    }

    public static MainCharacter getMainCharacter()
    {
        if(onlyInstance == null)
        {
            throw new IllegalArgumentException();
        }
        return onlyInstance;
    }

    public static MainCharacter getMainCharacter(String id, Point position, List<PImage> images,
                                                 int actionPeriod)
    {
        if(onlyInstance == null)
        {
            onlyInstance = new MainCharacter(id, position, images, actionPeriod);
        }
        return onlyInstance;
    }
}
