import processing.core.PImage;

import java.util.List;

public class Princess extends FunctionallyRandom{

    public Princess(String id, Point position, List<PImage> images, int actionPeriod) {
        super(id, position, images, actionPeriod);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if (world.getLost()) {
            scheduler.unscheduleAllEvents(this);
        } else if (moveTo(world, world.getMainCharacter(), scheduler)) {
            world.setLost(true);
        } else{
            scheduler.scheduleEvent(this, Create.createActivityAction(world, imageStore, this), getActionPeriod());
        }
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler)
    {
            Point nextPos = nextPosition(world, lastPos);
            if (nextPos == null)
                return false;
            basicNextPosLogic(world, nextPos);
            return false;
    }
}
