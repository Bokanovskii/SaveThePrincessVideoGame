import processing.core.PImage;

import java.util.*;

/*
WorldModel ideally keeps track of the actual size of our grid world and what is in that world
in terms of entities and background elements
 */
// I CASTED A LOT OF POINTS TO INT HERE CAREFUL!
public class WorldModel
{
   private static final int FISH_REACH = 1;

   private static final String OBSTACLE_KEY = "obstacle";

   private static final String BGND_KEY = "background";

   private static final String MAIN_CHAR_KEY = "mainChar";

   private static final String DARKELF_KEY = "darkElf";

   private static final String REAPER_KEY = "reaper";

   private static final String DEVIL_KEY = "devil";

   private static final String CHEST_KEY = "chest";

   private static final String PRINCESS_KEY = "princess";

   private static final String HALO_KEY = "halo";

   private static final int PROPERTY_KEY = 0;

   private final int numRows;
   private final int numCols;
   private final Background[][] background;
   private int chestAffectedSize = 0;
   private List<Point> chestAffectedPoints = new LinkedList<>();
   private final Entity[][] occupancy;
   private final Set<Entity> entities;
   private MainCharacter mainCharacter;
   private boolean lost = false;
   private boolean won = false;
   private boolean chestOpen = false;

   public WorldModel(int numRows, int numCols, Background defaultBackground)
   {
      this.numRows = numRows;
      this.numCols = numCols;
      this.background = new Background[numRows][numCols];
      this.occupancy = new Entity[numRows][numCols];
      this.entities = new HashSet<>();


      for (int row = 0; row < numRows; row++)
      {
         Arrays.fill(this.background[row], defaultBackground);
      }
   }

   public int getNumRows()
   {
      return numRows;
   }
   public int getNumCols()
   {
      return numCols;
   }
   protected String getObstacleKey() { return OBSTACLE_KEY; }
   protected String getMainCharKey() { return MAIN_CHAR_KEY; }
   protected String getDarkElfKey() { return DARKELF_KEY; }
   protected String getReaperKey() { return REAPER_KEY; }
   protected String getPrincessKey() { return PRINCESS_KEY; }
   protected String getHaloKey() { return HALO_KEY; }
   protected String getChestKey() { return CHEST_KEY; }
   protected MainCharacter getMainCharacter() { return mainCharacter; }
   public Set<Entity> getEntities()
   {
      return entities;
   }
   public void setLost(boolean b) { lost = b; }
   public void setWon(boolean b) { won = b; }
   public boolean getLost() { return lost; }
   public boolean getWon() { return won; }
   public boolean getChestOpen() { return chestOpen; }
   public void setChestOpen(boolean l) { chestOpen = l; }
   public void incrementChestAffectedSize() {
      if (chestAffectedSize == 0)
         chestAffectedSize++;
      chestAffectedSize++;
   }
   public int getChestAffectedSize() { return chestAffectedSize; }
   public List<Point> getChestAffectedPoints() { return chestAffectedPoints; }

   public Optional<Entity> findNearest(Point pos, Object other)
   {
      List<Entity> ofType = new LinkedList<>();
      for (Entity entity : entities)
      {
         if (entity.getClass() == other.getClass())
         {
            ofType.add(entity);
         }
      }

      return nearestEntity(ofType, pos);
   }

   public Optional<Point> findOpenAround(Point pos)
   {
      for (int dy = -FISH_REACH; dy <= FISH_REACH; dy++)
      {
         for (int dx = -FISH_REACH; dx <= FISH_REACH; dx++)
         {
            Point newPt = new Point(pos.x + dx, pos.y + dy);
            if (withinBounds(newPt) &&
                    !isOccupied(newPt))
            {
               return Optional.of(newPt);
            }
         }
      }

      return Optional.empty();
   }

   /*
   Assumes that there is no entity currently occupying the
   intended destination cell.
*/
   public void addEntity(Entity entity)
   {
      if (withinBounds(entity.getPosition()))
      {
         setOccupancyCell(entity.getPosition(), entity);
         entities.add(entity);
      }
   }

   protected void tryAddEntity(Entity entity)
   {
      if (entity.getClass() == MainCharacter.class)
         mainCharacter = (MainCharacter)entity;
//      if (entity.getClass() == Chest.class)
//         chest = (Chest)entity;
      if (isOccupied(entity.getPosition()))
      {
         // arguably the wrong type of exception, but we are not
         // defining our own exceptions yet
         throw new IllegalArgumentException("position occupied");
      }
      addEntity(entity);
   }


   public void moveEntity(Entity entity, Point pos)
   {
      Point oldPos = entity.getPosition();
      if (withinBounds(pos) && !pos.equals(oldPos))
      {
         setOccupancyCell(oldPos, null);
         removeEntityAt(pos);
         setOccupancyCell(pos, entity);
         entity.setPosition(pos);
      }
   }


   public void removeEntity(Entity entity)
   {
      removeEntityAt(entity.getPosition());
   }

   private void removeEntityAt(Point pos)
   {
      if (withinBounds(pos)
              && getOccupancyCell(pos) != null)
      {
         Entity entity = getOccupancyCell(pos);

         /* this moves the entity just outside of the grid for
            debugging purposes */
         entity.setPosition(new Point(-1, -1));
         entities.remove(entity);
         setOccupancyCell(pos, null);
      }
   }

   public boolean withinBounds(Point pos)
   {
      return pos.y >= 0 && pos.y < numRows &&
              pos.x >= 0 && pos.x < numCols;
   }

   public boolean isOccupied(Point pos)
   {
      return withinBounds(pos) &&
              getOccupancyCell(pos) != null;
   }

   public Optional<Entity> getOccupant(Point pos)
   {
      if (isOccupied(pos))
      {
         return Optional.of(getOccupancyCell(pos));
      }
      else
      {
         return Optional.empty();
      }
   }

   public Entity getOccupancyCell(Point pos)
   {
      return occupancy[pos.y][pos.x];
   }

   private void setOccupancyCell(Point pos, Entity entity) { occupancy[pos.y][pos.x] = entity; }

   private Optional<Entity> nearestEntity(List<Entity> entities, Point pos)
   {
      if (entities.isEmpty())
      {
         return Optional.empty();
      }
      else
      {
         Entity nearest = entities.get(0);
         int nearestDistance = distanceSquared(nearest.getPosition(), pos);

         for (Entity other : entities)
         {
            int otherDistance = distanceSquared(other.getPosition(), pos);

            if (otherDistance < nearestDistance)
            {
               nearest = other;
               nearestDistance = otherDistance;
            }
         }

         return Optional.of(nearest);
      }
   }

   private int distanceSquared(Point p1, Point p2)
   {
      int deltaX = (p1.x - p2.x);
      int deltaY = (p1.y - p2.y);

      return deltaX * deltaX + deltaY * deltaY;
   }

   public void setChestAffectedBackground(int size)
   {
      for (int x = (6 - size); x <= (6 + size); x++)
      {
         for (int y = (7 - size); y <= (7 + size); y++)
         {
            background[y][x].setImageIndex(1);
            chestAffectedPoints.add(new Point(x, y));
         }
      }

      for (int x = (13 - size); x <= (13 + size); x++)
      {
         for (int y = (7 - size); y <= (7 + size); y++)
         {
            background[y][x].setImageIndex(1);
            chestAffectedPoints.add(new Point(x, y));
         }
      }
   }

   public void spawnDevils(ImageStore imageStore, EventScheduler scheduler)
   {
      // all points in chestAffected points with an x or y value equal to the difference of chestafeectedsize and the starting point
      Point leftChest = new Point(7, 7);
      Point rightChest = new Point(12, 7);
      ActionableEntity devil1 = Create.createDevil("devil1", leftChest, 425,
              imageStore.getImageList(DEVIL_KEY));
      ActionableEntity devil2 = Create.createDevil("devil2", rightChest, 425,
              imageStore.getImageList(DEVIL_KEY));
      scheduler.scheduleEvent(devil1, Create.createActivityAction(this, imageStore, devil1), 0);
      scheduler.scheduleEvent(devil2, Create.createActivityAction(this, imageStore, devil2), 0);
      if (isOccupied(devil1.getPosition()))
         moveEntity(getOccupancyCell(devil1.getPosition()), new Point(7, 6));
      if (isOccupied(devil2.getPosition()))
         moveEntity(getOccupancyCell(devil2.getPosition()), new Point(12, 6));
      tryAddEntity(devil1);
      tryAddEntity(devil2);
   }

   public void spawnReapers(ImageStore imageStore, EventScheduler scheduler)
   {
      Point topLeft = new Point(3, 11);
      Point topRight = new Point(16, 11);
      Point botLeft = new Point(3, 3);
      Point botRight = new Point(16, 3);
      ActionableEntity reaper1 = Create.createReaper("reaper1", topLeft, 500,
              imageStore.getImageList(REAPER_KEY));
      ActionableEntity reaper2 = Create.createReaper("reaper2", topRight, 500,
              imageStore.getImageList(REAPER_KEY));
      ActionableEntity reaper3 = Create.createReaper("reaper3", botLeft, 500,
              imageStore.getImageList(REAPER_KEY));
      ActionableEntity reaper4 = Create.createReaper("reaper4", botRight, 500,
              imageStore.getImageList(REAPER_KEY));

      scheduler.scheduleEvent(reaper1, Create.createActivityAction(this, imageStore, reaper1), 1);
      scheduler.scheduleEvent(reaper2, Create.createActivityAction(this, imageStore, reaper2), 1);
      scheduler.scheduleEvent(reaper3, Create.createActivityAction(this, imageStore, reaper3), 1);
      scheduler.scheduleEvent(reaper4, Create.createActivityAction(this, imageStore, reaper4), 1);
      if (isOccupied(reaper1.getPosition()))
         moveEntity(getOccupancyCell(reaper1.getPosition()), new Point(3, 10));
      if (isOccupied(reaper2.getPosition()))
         moveEntity(getOccupancyCell(reaper2.getPosition()), new Point(16, 10));
      if (isOccupied(reaper3.getPosition()))
         moveEntity(getOccupancyCell(reaper3.getPosition()), new Point(3, 4));
      if (isOccupied(reaper4.getPosition()))
         moveEntity(getOccupancyCell(reaper4.getPosition()), new Point(16, 4));
      tryAddEntity(reaper1);
      tryAddEntity(reaper2);
      tryAddEntity(reaper3);
      tryAddEntity(reaper4);
   }

   public Optional<PImage> getBackgroundImage(Point pos)
   {
      if (withinBounds(pos))
      {
         return Optional.of(getBackgroundCell(pos).getCurrentImage());
      }
      else
      {
         return Optional.empty();
      }
   }

   private Background getBackgroundCell(Point pos)
   {
      return background[pos.y][pos.x];
   }

   protected void setBackground(Point pos, Background background)
   {
      if (withinBounds(pos))
      {
         setBackgroundCell(pos, background);
      }
   }

   private void setBackgroundCell(Point pos, Background background)
   {
      this.background[pos.y][pos.x] = background;
   }

   // loads the world off a .sav file
   private boolean processLine(String line, ImageStore imageStore)
   {
      String[] properties = line.split("\\s");
      if (properties.length > 0)
      {
         switch (properties[PROPERTY_KEY])
         {
            case BGND_KEY:
               return Parse.parseBackground(this, properties, imageStore);
            case CHEST_KEY:
               return Parse.parseChest(this, properties, imageStore);
            case OBSTACLE_KEY:
               return Parse.parseObstacle(this, properties, imageStore);
            case DARKELF_KEY:
               return Parse.parseDarkElf(this, properties, imageStore);
            case REAPER_KEY:
               return Parse.parseReaper(this, properties, imageStore);
            case MAIN_CHAR_KEY:
               return Parse.parseMainChar(this, properties, imageStore);
            case PRINCESS_KEY:
               return Parse.parsePrincess(this, properties, imageStore);
            case HALO_KEY:
               return Parse.parseHalo(this, properties, imageStore);
         }
      }
      return false;
   }

   public void load(Scanner in, ImageStore imageStore)
   {
      int lineNumber = 0;
      while (in.hasNextLine())
      {
         try
         {
            if (!processLine(in.nextLine(), imageStore))
            {
               System.err.println(String.format("invalid entry on line %d",
                       lineNumber));
            }
         }
         catch (NumberFormatException e)
         {
            System.err.println(String.format("invalid entry on line %d",
                    lineNumber));
         }
         catch (IllegalArgumentException e)
         {
            System.err.println(String.format("issue on line %d: %s",
                    lineNumber, e.getMessage()));
         }
         lineNumber++;
      }
   }
}
