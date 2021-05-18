import processing.core.PApplet;
import processing.core.PImage;

import java.util.Optional;

/*
WorldView ideally mostly controls drawing the current part of the whole world
that we can see based on the viewport
*/

final class WorldView
{
   private final PApplet screen;
   private final WorldModel world;
   private final int tileWidth;
   private final int tileHeight;
   private final Viewport viewport;

   public WorldView(int numRows, int numCols, PApplet screen, WorldModel world,
      int tileWidth, int tileHeight)
   {
      this.screen = screen;
      this.world = world;
      this.tileWidth = tileWidth;
      this.tileHeight = tileHeight;
      this.viewport = new Viewport(numRows, numCols);
   }

   public void shiftView(int colDelta, int rowDelta)
   {
      int newCol = clamp(viewport.getCol() + colDelta, 0, world.getNumCols() - viewport.getNumCols());
      int newRow = clamp(viewport.getRow() + rowDelta, 0, world.getNumRows() - viewport.getNumRows());

      viewport.shift(newCol, newRow);
   }

   private int clamp(int value, int low, int high)
   {
      return Math.min(high, Math.max(value, low));
   }

   private void drawBackground()
   {
      for (int row = 0; row < viewport.getNumRows(); row++)
      {
         for (int col = 0; col < viewport.getNumCols(); col++)
         {
            Point worldPoint = viewport.viewportToWorld(col, row);
            Optional<PImage> image = world.getBackgroundImage(worldPoint);
            if (image.isPresent())
            {
               screen.image(image.get(), col * tileWidth,
                       row * tileHeight);
            }
         }
      }
   }

   private void drawEntities()
   {
      for (Entity entity : world.getEntities())
      {
         Point pos = entity.getPosition();

         if (viewport.contains(pos))
         {
            Point viewPoint = viewport.worldToViewport(pos.x, pos.y);
            screen.image(entity.getCurrentImage(),viewPoint.x * tileWidth, viewPoint.y * tileHeight);
         }
      }
   }

   public void drawViewport()
   {
      drawBackground();
      drawEntities();
   }
}