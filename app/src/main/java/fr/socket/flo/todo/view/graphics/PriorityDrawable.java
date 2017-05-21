package fr.socket.flo.todo.view.graphics;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class PriorityDrawable extends TextDrawable {
	public PriorityDrawable(int priority) {
		super(String.valueOf(priority), ColorGenerator.priorityColor(priority), ColorGenerator.textColorOfLatestPriorityColor());
	}
}
