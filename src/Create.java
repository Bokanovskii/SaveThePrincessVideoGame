import java.util.List;
import java.util.Random;
import processing.core.PImage;


abstract class Create
{
   public static Animation createAnimationAction(int repeatCount, ActionableEntity e)
   {
      return new Animation(e, repeatCount);
   }

   public static Activity createActivityAction(WorldModel world, ImageStore imageStore, ActionableEntity e)
   {
      return new Activity(e, world, imageStore);
   }

   public static DarkElf createDarkElf(String id, Point position, int actionPeriod, List<PImage> images)
   {
      return new DarkElf(id, position, images, actionPeriod);
   }

   public static Princess createPrincess(String id, Point position, int actionPeriod, List<PImage> images)
   {
      return new Princess(id, position, images, actionPeriod);
   }

   public static Halo createHalo(String id, Point position, int actionPeriod, int animationPeriod, List<PImage> images)
   {
      return new Halo(id, position, images, actionPeriod, animationPeriod);
   }

   public static Reaper createReaper(String id, Point position, int actionPeriod, List<PImage> images)
   {
      return new Reaper(id, position, images, actionPeriod);
   }

   public static Devil createDevil(String id, Point position, int actionPeriod, List<PImage> images)
   {
      return new Devil(id, position, images, actionPeriod);
   }

   public static Chest createChest(String id, Point position, int actionPeriod, List<PImage> images)
   {
      return new Chest(id, position, images, actionPeriod);
   }

   public static Projectile createProjectile(String id, Point position, int actionPeriod,
                                             List<PImage> images, ActionableEntity owner, List<Point> projPath)
   {
      return new Projectile(id, position, images, actionPeriod, owner, projPath);
   }

   public static Obstacle createObstacle(String id, Point position, List<PImage> images)
   {
      return new Obstacle(id, position, images, 0);
   }

}
