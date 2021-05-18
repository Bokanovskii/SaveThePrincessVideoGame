/*
Action: ideally what our various entities might do in our virutal world
 */

abstract class Action {
   private final ActionableEntity entity;

   public Action(ActionableEntity entity)
   {
      this.entity = entity;
   }

   protected ActionableEntity getEntity() { return entity; }
   protected abstract void executeAction(EventScheduler scheduler);
}
