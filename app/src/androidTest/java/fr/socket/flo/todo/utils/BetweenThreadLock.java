package fr.socket.flo.todo.utils;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Florian Arnould
 */
public class BetweenThreadLock{
	private Lock _lock;

	public BetweenThreadLock() {
		_lock = new ReentrantLock();
	}

	public void lock() {
		_lock.lock();
	}

	public boolean tryLock(long time, @NonNull TimeUnit unit) throws InterruptedException {
		return _lock.tryLock(time, unit);
	}

	public void unlock() {
		_lock = new ReentrantLock();
	}
}
