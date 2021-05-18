import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Devil extends MoveTo{
    private final PathingStrategy pathing = new AStarPathingStrategy();
    private Entity nearestReaper;


    public Devil(String id, Point position, List<PImage> images, int actionPeriod) {
        super(id, position, images, actionPeriod);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> maybeNearestReaper = world.findNearest(getPosition(),
                new Reaper(null, null, null, 0));
        if (maybeNearestReaper.isEmpty()){
            scheduler.unscheduleAllEvents(this);
            world.removeEntity(this);
            return;
        } else if (nearestReaper == null) {
            maybeNearestReaper.ifPresent(entity -> nearestReaper = entity);
        }
        if (world.getLost()) {
            scheduler.unscheduleAllEvents(this);
        } else if (moveTo(world, nearestReaper, scheduler)) {
            scheduler.unscheduleAllEvents(this);
            world.removeEntity(this);
        } else{
            scheduler.scheduleEvent(this, Create.createActivityAction(world, imageStore, this), getActionPeriod());
        }
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler)
    {
        if (target == null)
            return true;
        if (adjacent(getPosition(), target.getPosition()) || getPosition() == target.getPosition())
        {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            return true;
        }
        else {
            Point nextPos = nextPositionDevil(world, target.getPosition());
            if (nextPos == null)
                return true;
            basicNextPosLogic(world, nextPos);
            return false;
        }
    }

    private Point nextPositionDevil(WorldModel world, Point destPos)
    {
        List<Point> path = pathing.computePath(getPosition(), destPos,
                    p -> world.withinBounds(p) && (!world.isOccupied(p) ||
                            world.getOccupancyCell(p).getClass().equals(Reaper.class)),
                    PathingStrategy::neighbors, pathing.DIAGONAL_CARDINAL_NEIGHBORS);
        if (path.size() == 0) {
            return null;
        }
        return path.get(0);
    }
}
