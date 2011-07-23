import java.util.Vector;

public class EventListImpl implements EventList {
	private final Vector<Event> data;

	public EventListImpl() {
		data = new Vector<Event>();
	}

	public boolean add(Event e) {
		data.addElement(e);
		return true;
	}

	public Event removeNext() {
		if (data.isEmpty())
			return null;

		int firstIndex = 0;
		double first = (data.elementAt(firstIndex)).getTime();
		for (int i = 0; i < data.size(); i++)
			if ((data.elementAt(i)).getTime() < first) {
				first = (data.elementAt(i)).getTime();
				firstIndex = i;
			}

		final Event next = data.elementAt(firstIndex);
		data.removeElement(next);

		return next;
	}

	public String toString() {
		return data.toString();
	}

	public Event removeTimer(int entity) {
		int timerIndex = -1;
		Event timer = null;

		for (int i = 0; i < data.size(); i++)
			if ((((data.elementAt(i))).getType() == NetworkSimulator.TIMERINTERRUPT)
					&& (((data.elementAt(i))).getEntity() == entity)) {
				timerIndex = i;
				break;
			}

		if (timerIndex != -1) {
			timer = (data.elementAt(timerIndex));
			data.removeElement(timer);
		}

		return timer;

	}

	public double getLastPacketTime(int entityTo) {
		double time = 0;
		for (int i = 0; i < data.size(); i++)
			if ((((data.elementAt(i))).getType() == NetworkSimulator.FROMLAYER3)
					&& (((data.elementAt(i))).getEntity() == entityTo))
				time = ((data.elementAt(i))).getTime();

		return time;
	}
}
