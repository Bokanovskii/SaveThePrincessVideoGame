import java.util.List;

import processing.core.PImage;

/*
Entity ideally would includes functions for how all the entities in our virtual world might act...
 */


abstract class Entity
{
   private final String id;
   private Point position;
   private final List<PImage> images;
   private int imageIndex;
   private final int actionPeriod;

   public Entity(String id, Point position, List<PImage> images, int actionPeriod)
   {
      this.id = id;
      this.position = position;
      this.images = images;
      this.imageIndex = 0;
      this.actionPeriod = actionPeriod;
   }
   protected List<PImage> getImages() { return images; }
   protected String getID() { return id; }
   protected Point getPosition()
   {
      return position;
   }
   protected void setPosition(Point pos)
   {
      position = pos;
   }
   protected int getActionPeriod()
   {
      return actionPeriod;
   }
   protected PImage getCurrentImage()
   {
      return images.get(imageIndex);
   }
   protected void setImageIndex(int n) {imageIndex = n; }

   protected void nextImage()
   {
      imageIndex = (imageIndex + 1) % images.size();
   }


}
