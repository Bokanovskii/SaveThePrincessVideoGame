import processing.core.PImage;

import java.util.List;

public abstract class ActionableEntity extends Entity {
    public ActionableEntity(String id, Point position, List<PImage> images, int actionPeriod) {
        super(id, position, images, actionPeriod);
    }

    protected abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    protected abstract void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);
}
