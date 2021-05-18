import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class DarkElf extends MoveTo{
    private final PathingStrategy pathing = new AStarPathingStrategy();
    private final PathingStrategy projectilePathing = new AStarPathingStrategy();
    private boolean shootingProjectile = false;
    private Point projectileStartPos;
    private List<Point> projectilePath;
    private static final int PROJECTILE_SPEED = 150;

    public DarkElf(String id, Point position, List<PImage> images, int actionPeriod) {
        super(id, position, images, actionPeriod);
    }

    public void setShootingProjectile(boolean shootingProjectile) {
        this.shootingProjectile = shootingProjectile;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if (world.getLost()) {
            scheduler.unscheduleAllEvents(this);
        } else if (moveTo(world, world.getMainCharacter(), scheduler)) {
            world.setLost(true);
        } else if(shootingProjectile) {
            Projectile projectile = Create.createProjectile("projectile" + getID(), projectileStartPos,
                    PROJECTILE_SPEED, imageStore.getImageList("projectile"), this, projectilePath);
            world.addEntity(projectile);
            projectile.scheduleActions(scheduler, world, imageStore);
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
            Point nextPos = nextPositionDarkElf(world, target.getPosition());
            if (nextPos == null || shootingProjectile)
                return false;
            basicNextPosLogic(world, nextPos);
            return false;
        }
    }

    private Point nextPositionDarkElf(WorldModel world, Point destPos)
    {
        List<Point> path = pathing.computePath(getPosition(), destPos,
                p -> world.withinBounds(p) && (!world.isOccupied(p) || world.getOccupancyCell(p).getID().equals("mainChar")),
                PathingStrategy::neighbors, pathing.DIAGONAL_CARDINAL_NEIGHBORS);
        if (path.size() == 0)
            return null;
        Point newPos = path.get(0);

        List<Point> projPath = projectilePathing.computePath(getPosition(), destPos,
                p -> world.withinBounds(p) && (!world.isOccupied(p) || world.getOccupancyCell(p).getID().equals("mainChar")),
                PathingStrategy::neighbors, pathing.CARDINAL_NEIGHBORS);
        projPath.add(destPos);
        if (projPath.size() <= 4 && projPath.size() > 1 && directPath(projPath, 1, 1, 1, 1, 1))
        {
            shootingProjectile = true;
            projectilePath = projPath;
            projectileStartPos = projPath.get(0);
        }
        // have code here to find a path using Diagonal and cardinal to the player
        // if the path size is less than or equal to 4 then pause the DarkElf (unscheduleAllEvents)
        // create a new orb object, schedule an orb object action
        // once the orb object action is complete, schedule the darkElf action again
            // do this by setting and reseting the darkElf action period
        return newPos;
    }

    private boolean directPath(List<Point> projPath, int dx, int dy, int prevdx, int prevdy, int index)
    {
        if (projPath.size() == 2 || index <= 1)
        {
            dx = projPath.get(0).x - getPosition().x;
            dy = projPath.get(0).y - getPosition().y;
            int ddx = projPath.get(1).x - projPath.get(0).x;
            int ddy = projPath.get(1).y - projPath.get(0).y;
            if (ddx != dx || ddy != dy)
                return false;
        }
        if (index == projPath.size())
            return true;
        dx = projPath.get(index).x - projPath.get(index - 1).x;
        dy = projPath.get(index).y - projPath.get(index - 1).y;
        if ((dx != prevdx || dy != prevdy) && index != 1)
            return false;
        prevdx = dx;
        prevdy = dy;
        return directPath(projPath, dx, dy, prevdx, prevdy, ++index);
    }
}
