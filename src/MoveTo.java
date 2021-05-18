import processing.core.PImage;

import java.util.List;

public abstract class MoveTo extends ActionableEntity{
    public MoveTo(String id, Point position, List<PImage> images, int actionPeriod) {
        super(id, position, images, actionPeriod);
    }

    protected abstract boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler);

    protected void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this,
                Create.createActivityAction(world, imageStore, this), getActionPeriod());
    }

    protected abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    protected boolean adjacent(Point p1, Point p2)
    {
        return (p1.x == p2.x && Math.abs(p1.y - p2.y) == 1) ||
                (p1.y == p2.y && Math.abs(p1.x - p2.x) == 1) ||
                (Math.abs(p1.y - p2.y) == 1 && (Math.abs(p1.x - p2.x) == 1));
    }

    protected void basicNextPosLogic(WorldModel world, Point nextPos)
    {
        if (!getPosition().equals(nextPos))
        {
            int dx = nextPos.x - getPosition().x;
            int dy = nextPos.y - getPosition().y;
            if (dy == -1)
                setImageIndex(1);
            if (dy == 1)
                setImageIndex(0);
            if (dx == 1)
                setImageIndex(2);
            if (dx == -1)
                setImageIndex(3);
            world.moveEntity(this, nextPos);
        }
    }
}
