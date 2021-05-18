import processing.core.PImage;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class Halo extends Animated{
        private final PathingStrategy pathing = new SingleStepPathingStrategy();

        public Halo(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
            super(id, position, images, actionPeriod, animationPeriod);

        }

        public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
            Optional<Entity> princess = world.findNearest(getPosition(), new Princess(null, null, null, 0));
            if (princess.isEmpty())
                throw new IllegalCallerException();
            if (world.getLost()) {
                scheduler.unscheduleAllEvents(this);
            } else if (moveTo(world, princess.get(), scheduler)) {
                world.setLost(true);
                world.setWon(true);
            } else{
                scheduler.scheduleEvent(this, Create.createAnimationAction(2, this), getAnimationPeriod());
                scheduler.scheduleEvent(this, Create.createActivityAction(world, imageStore, this), getActionPeriod());
            }
        }

        public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler)
        {
            if (adjacent(getPosition(), target.getPosition()))
                return true;
            Point nextPos = nextPosition(world, target.getPosition());
            if (nextPos == null)
                return false;
            if (!getPosition().equals(nextPos))
            {
                world.moveEntity(this, nextPos);
            }
            return false;
        }

    protected Point nextPosition(WorldModel world, Point destPos)
    {
        List<Point> path = pathing.computePath(getPosition(),
                destPos,
                p -> world.withinBounds(p) && (!world.isOccupied(p) || world.getOccupancyCell(p).getID().equals("mainChar")),
                PathingStrategy::neighbors,
                pathing.CARDINAL_NEIGHBORS);
        if (path.size() == 0)
            return null;
        return path.get(0);
    }
}