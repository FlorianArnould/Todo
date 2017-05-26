package fr.socket.flo.todo.view.dialog;

/**
 * @author Florian Arnould
 * @version 1.0
 */
@FunctionalInterface
public interface OnDialogFinishedListener<E> {
	void onDialogFinished(E result);
}
