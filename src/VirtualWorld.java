import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import processing.core.*;

/*
VirtualWorld is our main wrapper
It keeps track of data necessary to use Processing for drawing but also keeps track of the necessary
components to make our world run (eventScheduler), the data in our world (WorldModel) and our
current view (think virtual camera) into that world (WorldView)
 */

public final class VirtualWorld
   extends PApplet
{
   public static final int TIMER_ACTION_PERIOD = 100;

   public static final int VIEW_WIDTH = 640;
   public static final int VIEW_HEIGHT = 480;
   public static final int TILE_WIDTH = 32;
   public static final int TILE_HEIGHT = 32;
   public static final int WORLD_WIDTH_SCALE = 1;
   public static final int WORLD_HEIGHT_SCALE = 1;

   public static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
   public static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
   public static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
   public static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

   public static final String IMAGE_LIST_FILE_NAME = "imagelist";
   public static final String DEFAULT_IMAGE_NAME = "background_default";
   public static final int DEFAULT_IMAGE_COLOR = 0x808080;

   public static final String LOAD_FILE_NAME = "world.sav";

   public static final String FAST_FLAG = "-fast";
   public static final String FASTER_FLAG = "-faster";
   public static final String FASTEST_FLAG = "-fastest";
   public static final double FAST_SCALE = 0.5;
   public static final double FASTER_SCALE = 0.25;
   public static final double FASTEST_SCALE = 0.10;

   private static final Point CHEST_LOCATION_1 = new Point(6, 7);
   private static final Point CHEST_LOCATION_2 = new Point(13, 7);

   private static final String LOST_MSG = "You Lost!";
   private static final String WON_MSG = "You Won!";

   public static double timeScale = 1.0;

   private ImageStore imageStore;
   private WorldModel world;
   private WorldView view;
   private EventScheduler scheduler;
   private MainCharacter mainCharacter;

   public long next_time;

   public void settings()
   {
      size(VIEW_WIDTH, VIEW_HEIGHT);
   }

   /*
      Processing entry point for "sketch" setup.
   */
   public void setup()
   {
      this.imageStore = new ImageStore(
         createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
      this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
         createDefaultBackground(imageStore));
      this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world,
         TILE_WIDTH, TILE_HEIGHT);
      this.scheduler = new EventScheduler(timeScale);

      loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
      loadWorld(world, LOAD_FILE_NAME, imageStore);
      mainCharacter = world.getMainCharacter();
      scheduleActions(world, scheduler, imageStore);

      next_time = System.currentTimeMillis() + TIMER_ACTION_PERIOD;

      // need to load the images
   }

   public void draw()
   {
      long time = System.currentTimeMillis();
      if (time >= next_time)
      {
         this.scheduler.updateOnTime(time);
         next_time = time + TIMER_ACTION_PERIOD;
      }

      view.drawViewport();

      if (world.getLost() && scheduler.getEventQueue().isEmpty()){
         fill (217, 179, 130);
         stroke(217, 179, 130);
         int rectWidth = 10 * TILE_WIDTH;
         int rectHeight = 8 * TILE_HEIGHT;
         rect((VIEW_WIDTH / 2) - (rectWidth / 2), (VIEW_HEIGHT/2) - (rectHeight / 2), rectWidth, rectHeight);
         textSize(40);
         fill(0, 0, 0);
         if (world.getWon())
            text(WON_MSG, VIEW_WIDTH / 2 - WON_MSG.length() * 10 - 10, VIEW_HEIGHT / 2 + 15);
         else {
            text(LOST_MSG, VIEW_WIDTH / 2 - LOST_MSG.length() * 10, VIEW_HEIGHT / 2 + 15);
         }
      }
   }

   public void keyPressed()
   {
      if (key == CODED)
      {
         int dx = 0;
         int dy = 0;

         switch (keyCode)
         {
            case UP:
               dy = -1;
               world.getMainCharacter().setImageIndex(1);
               break;
            case DOWN:
               dy = 1;
               world.getMainCharacter().setImageIndex(0);
               break;
            case LEFT:
               dx = -1;
               world.getMainCharacter().setImageIndex(3);
               break;
            case RIGHT:
               dx = 1;
               world.getMainCharacter().setImageIndex(2);
               break;
         }
         Point nextPos = new Point(mainCharacter.getPosition().x + dx, mainCharacter.getPosition().y + dy);
         if (!world.isOccupied(nextPos))
            world.moveEntity(mainCharacter, nextPos);
      }
   }

   public void mousePressed()
   {
      if (!world.getChestOpen() && clickedChest(mouseX / TILE_WIDTH, mouseY / TILE_HEIGHT))
      {
         world.setChestOpen(true);
         if(world.getChestAffectedSize() < 3) {
            world.incrementChestAffectedSize();
            world.setChestAffectedBackground(world.getChestAffectedSize());
            world.spawnDevils(imageStore, scheduler);
            world.spawnReapers(imageStore, scheduler);
         }
      }
   }

   private boolean clickedChest(int mx, int my)
   {
      return (mx == CHEST_LOCATION_1.x && my == CHEST_LOCATION_1.y) ||
              (mx == CHEST_LOCATION_2.x && my == CHEST_LOCATION_2.y);
   }


   public static Background createDefaultBackground(ImageStore imageStore)
   {
      return new Background(DEFAULT_IMAGE_NAME,
         imageStore.getImageList(DEFAULT_IMAGE_NAME));
   }

   public static PImage createImageColored(int width, int height, int color)
   {
      PImage img = new PImage(width, height, RGB);
      img.loadPixels();
      for (int i = 0; i < img.pixels.length; i++)
      {
         img.pixels[i] = color;
      }
      img.updatePixels();
      return img;
   }

   private static void loadImages(String filename, ImageStore imageStore,
      PApplet screen)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         imageStore.loadImages(in, screen);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   public static void loadWorld(WorldModel world, String filename,
      ImageStore imageStore)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         world.load(in, imageStore);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   public static void scheduleActions(WorldModel world,
      EventScheduler scheduler, ImageStore imageStore)
   {
      for (Entity entity : world.getEntities())
      {
         //Only start actions for entities that include action (not those with just animations)
         if (entity instanceof ActionableEntity && entity.getActionPeriod() > 0)
            ((ActionableEntity)entity).scheduleActions(scheduler, world, imageStore);
      }
   }

   public static void parseCommandLine(String [] args)
   {
      for (String arg : args)
      {
         switch (arg)
         {
            case FAST_FLAG:
               timeScale = Math.min(FAST_SCALE, timeScale);
               break;
            case FASTER_FLAG:
               timeScale = Math.min(FASTER_SCALE, timeScale);
               break;
            case FASTEST_FLAG:
               timeScale = Math.min(FASTEST_SCALE, timeScale);
               break;
         }
      }
   }

   public static void main(String [] args)
   {
      parseCommandLine(args);
      PApplet.main(VirtualWorld.class);
   }
}
