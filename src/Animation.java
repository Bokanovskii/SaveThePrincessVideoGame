public class Animation extends Action {
    private final int repeatCount;

    public Animation(ActionableEntity entity, int repeatCount)
    {
        super(entity);
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler)
    {
        ActionableEntity entity = getEntity();
        if (!(entity instanceof Animated))
            throw new IllegalArgumentException();
        entity.nextImage();
        if (repeatCount != 1) {
            scheduler.scheduleEvent(entity, Create.createAnimationAction(Math.max(repeatCount - 1, 0), entity),
                    ((Animated)entity).getAnimationPeriod());
        }
    }

}
