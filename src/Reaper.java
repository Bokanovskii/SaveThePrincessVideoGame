import processing.core.PImage;

import java.util.List;

public class Reaper extends MoveTo{
    private final PathingStrategy pathing = new AStarPathingStrategy();
    private final PathingStrategy confusedPathing = new RandomPathingStrategy();
    private Point lastConfusedPos;

    public Reaper(String id, Point position, List<PImage> images, int actionPeriod) {
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
        if (adjacent(getPosition(), target.getPosition()) || getPosition() == target.getPosition())
        {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            return true;
        }
        else {
            Point nextPos = nextPositionReaper(world, target.getPosition());
            if (nextPos == null)
                return false;
            if (!getPosition().equals(nextPos))
            {
                int additive = 0;
                if (world.getChestAffectedPoints().contains(nextPos))
                    additive += 4;
                int dx = nextPos.x - getPosition().x;
                int dy = nextPos.y - getPosition().y;
                if (dy == -1)
                    setImageIndex(1 + additive);
                if (dy == 1)
                    setImageIndex(0 + additive);
                if (dx == 1)
                    setImageIndex(2 + additive);
                if (dx == -1)
                    setImageIndex(3 + additive);
                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }

    private Point nextPositionReaper(WorldModel world, Point destPos)
    {
        List<Point> path;
        if(world.getChestAffectedPoints().contains(getPosition()))
        {
            path = confusedPathing.computePath(getPosition(), lastConfusedPos,
                    p -> world.withinBounds(p) && (!world.isOccupied(p) || world.getOccupancyCell(p).getID().equals("mainChar")),
                    PathingStrategy::neighbors, pathing.DIAGONAL_CARDINAL_NEIGHBORS);
            if (path.size() != 0)
                lastConfusedPos = path.get(0);
        } else {
            path = pathing.computePath(getPosition(), destPos,
                    p -> world.withinBounds(p) && (!world.isOccupied(p) || world.getOccupancyCell(p).getID().equals("mainChar")),
                    PathingStrategy::neighbors, pathing.CARDINAL_NEIGHBORS);
            lastConfusedPos = null;
        }
        if (path.size() == 0)
            return null;
        return path.get(0);
    }
}
