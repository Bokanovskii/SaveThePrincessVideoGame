import processing.core.PImage;

import java.util.List;

public class Obstacle extends Entity {

    public Obstacle(String id, Point position, List<PImage> images, int actionPeriod) {
        super(id, position, images, actionPeriod);
    }

    // if the obstacle class wanted later implementation for actions this would be implemented here
    protected void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
    }

}
