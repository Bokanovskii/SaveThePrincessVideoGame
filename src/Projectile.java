import processing.core.PImage;

import java.util.List;

public class Projectile extends MoveTo{
    private enum direction {
        up, rightUp, right, downRight, down, downLeft, left, leftUp
    }
    private direction currentDirection;
    private ActionableEntity owner;
    private int distanceShot = 0;
    private List<Point> projPath;
    private int index;

    public Projectile(String id, Point position, List<PImage> images, int actionPeriod,
                      ActionableEntity owner, List<Point> projPath) {
        super(id, position, images, actionPeriod);
        this.projPath = projPath;
        this.owner = owner;
        index = 1;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if (moveTo(world, world.getMainCharacter(), scheduler) || distanceShot >= 5) {
            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);
            if (owner instanceof DarkElf)
                ((DarkElf) owner).setShootingProjectile(false);
            if (!world.getLost())
                scheduler.scheduleEvent(owner, Create.createActivityAction(world, imageStore, owner), 1);
            else {
                scheduler.unscheduleAllEvents((owner));
            }
        } else {
            scheduler.scheduleEvent(this, Create.createActivityAction(world, imageStore, this), getActionPeriod());
            distanceShot++;
        }
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler)
    {
        // have an index for where I am at in the path and increment
        Point nextPos;
        if (index < projPath.size()) {
            nextPos = projPath.get(index);
            index++;
        } else {
            switch (currentDirection) {
                case up:
                    nextPos = new Point(getPosition().x, getPosition().y + 1);
                    break;
                case right:
                    nextPos = new Point(getPosition().x + 1, getPosition().y);
                    break;
                case left:
                    nextPos = new Point(getPosition().x - 1, getPosition().y);
                    break;
                case down:
                    nextPos = new Point(getPosition().x, getPosition().y - 1);
                    break;
                case leftUp:
                    nextPos = new Point(getPosition().x - 1, getPosition().y + 1);
                    break;
                case rightUp:
                    nextPos = new Point(getPosition().x + 1, getPosition().y + 1);
                    break;
                case downLeft:
                    nextPos = new Point(getPosition().x - 1, getPosition().y - 1);
                    break;
                case downRight:
                    nextPos = new Point(getPosition().x + 1, getPosition().y - 1);
                    break;
                default:
                    nextPos = getPosition();
            }
        }
        if (index >= projPath.size() - 1)
            currentDirection = comparePos(nextPos);
        if(!world.withinBounds(nextPos) || world.isOccupied(nextPos)) {
            if (world.withinBounds(nextPos) &&
                    world.getOccupancyCell(nextPos).getClass().equals(MainCharacter.class))
            {
                world.removeEntity(target);
                scheduler.unscheduleAllEvents(target);
                world.setLost(true);
                world.moveEntity(this, nextPos);
            }
            return true;
        }
        world.moveEntity(this, nextPos);
        return false;
    }

    private direction comparePos(Point nextPos)
    {
        int dx = nextPos.x - getPosition().x;
        int dy = nextPos.y - getPosition().y;
        if (dy == -1 && dx == -1)
            return direction.downLeft;
        if (dy == -1 && dx == 1)
            return direction.downRight;
        if (dy == -1 && dx == 0)
            return direction.down;
        if (dy == 1 && dx == 1)
            return direction.rightUp;
        if (dy == 1 && dx == -1)
            return direction.leftUp;
        if (dy == 1 && dx ==0)
            return direction.up;
        if (dy == 0 && dx == 1)
            return direction.right;
        if (dy == 0 && dx == -1)
            return direction.left;
        return currentDirection;
    }
}
