import processing.core.PImage;

import java.util.List;

public abstract class FunctionallyRandom extends MoveTo {
    protected Point lastPos;
    private final PathingStrategy pathing = new RandomPathingStrategy();

    public FunctionallyRandom(String id, Point position, List<PImage> images, int actionPeriod) {
        super(id, position, images, actionPeriod);
    }

    protected abstract boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler);

    protected abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    protected Point nextPosition(WorldModel world, Point destPos)
    {
        lastPos = getPosition();
        List<Point> path = pathing.computePath(getPosition(), destPos,
                p -> world.withinBounds(p) && (!world.isOccupied(p) || world.getOccupancyCell(p).getID().equals("mainChar")),
                PathingStrategy::neighbors, pathing.CARDINAL_NEIGHBORS);
        if (path.size() == 0)
            return null;
        return path.get(0);
    }
}

