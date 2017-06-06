package fr.socket.flo.todo.database;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import fr.socket.flo.todo.model.Project;

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

	@Test
	public void onUpgradeTest() throws Exception {
		DatabaseUtils.insertFruits();
		DatabaseHelper helper = new DatabaseHelper(InstrumentationRegistry.getTargetContext());
		helper.onUpgrade(helper.getWritableDatabase(), 0, 1);
		final CountDownLatch signal = new CountDownLatch(1);
		DataManager.getInstance().initialize(InstrumentationRegistry.getTargetContext());
		DataManager.getInstance().getAllProjects(new OnMultipleObjectsLoadedListener<Project>() {
			@Override
			public void onObjectsLoaded(List<Project> projects) {
				Assert.assertEquals(0, projects.size());
				signal.countDown();
			}
		});
		signal.await();
	}
}
