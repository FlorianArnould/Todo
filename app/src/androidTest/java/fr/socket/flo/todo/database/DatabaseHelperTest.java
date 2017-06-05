package fr.socket.flo.todo.database;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Florian Arnould
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {
	@Test
	public void createDatabaseTest() {
		DataManager.getInstance().initialize(InstrumentationRegistry.getTargetContext());
		DataManager.getInstance().closeDatabase();
		InstrumentationRegistry.getTargetContext().deleteDatabase("ProjectsDatabase.db");
		DataManager.getInstance().initialize(InstrumentationRegistry.getTargetContext());
	}
}
