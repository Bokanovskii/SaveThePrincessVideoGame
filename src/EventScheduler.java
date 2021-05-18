import java.util.*;

/*
EventScheduler: ideally our way of controlling what happens in our virtual world
 */

final class EventScheduler
{
   private  PriorityQueue<Event> eventQueue;
   private  Map<Entity, List<Event>> pendingEvents;
   private final double timeScale;

   public EventScheduler(double timeScale)
   {
      this.eventQueue = new PriorityQueue<>(new EventComparator());
      this.pendingEvents = new HashMap<>();
      this.timeScale = timeScale;
   }

   public PriorityQueue<Event> getEventQueue() { return eventQueue; }
   public Map<Entity, List<Event>> getPendingEvents() { return pendingEvents; }
   public void setEventQueue(PriorityQueue<Event> l) {
      eventQueue.addAll(l);
   }
   public void setPendingEvents(Map<Entity, List<Event>> l) {
      pendingEvents.putAll(l);
   }

   public void scheduleEvent(Entity entity, Action action, long afterPeriod)
   {
      long time = System.currentTimeMillis() +
              (long)(afterPeriod * timeScale);
      Event event = new Event(action, time, entity);

      eventQueue.add(event);

      // update list of pending events for the given entity
      List<Event> pending = pendingEvents.getOrDefault(entity,
              new LinkedList<>());
      pending.add(event);
      pendingEvents.put(entity, pending);
   }

   public void unscheduleAllEvents(Entity entity)
   {
      List<Event> pending = pendingEvents.remove(entity);

      if (pending != null)
      {
         for (Event event : pending)
         {
            eventQueue.remove(event);
         }
      }
   }

   public void updateOnTime(long time)
   {
      while (!eventQueue.isEmpty() && eventQueue.peek().getTime() < time)
      {
         Event next = eventQueue.poll();

         removePendingEvent(next);

         next.getAction().executeAction(this);
      }
   }

   private void removePendingEvent(Event event)
   {
      List<Event> pending = pendingEvents.get(event.getEntity());

      if (pending != null)
      {
         pending.remove(event);
      }
   }

}
