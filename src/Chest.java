import processing.core.PImage;

import java.util.List;

public class Chest extends ActionableEntity{
    private int openTime = 0;
    // every "time" is actionPeriod 100

    public Chest(String id, Point position, List<PImage> images, int actionPeriod) {
        super(id, position, images, actionPeriod);
    }
    // call this in the key pressed if chestOpen and escape pressed

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if (world.getChestOpen() && !world.getLost() && openTime < 4)
        {
            openTime++;
            setImageIndex(1);
            scheduleActions(scheduler, world, imageStore);
        } else {
            world.setChestOpen(false);
            openTime = 0;
            setImageIndex(0);
            if (!world.getLost())
                scheduleActions(scheduler, world, imageStore);
            else {
                scheduler.unscheduleAllEvents(this);
            }
        }
    }

    protected void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent(this, Create.createActivityAction(world, imageStore, this), this.getActionPeriod());
    }

//    public void recordSchedule(EventScheduler scheduler)
//    {
//        eventQueueToReset.clear();
//        pendingEventsToReset.clear();
//        eventQueueToReset.addAll(scheduler.getEventQueue());
//        pendingEventsToReset.putAll(scheduler.getPendingEvents());
//    }

//    public void rescheduleChest(EventScheduler scheduler) {
//        scheduler.setEventQueue(eventQueueToReset);
//        scheduler.setPendingEvents(pendingEventsToReset);
//    }
}
