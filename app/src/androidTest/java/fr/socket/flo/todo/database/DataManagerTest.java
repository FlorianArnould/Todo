package fr.socket.flo.todo.database;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import fr.socket.flo.todo.model.CursorMock;
import fr.socket.flo.todo.model.Project;
import fr.socket.flo.todo.model.Task;

/**
 * @author Florian Arnould
 */
@RunWith(AndroidJUnit4.class)
public class DataManagerTest {
	@Test
	public void getFavoritesTest() throws Exception {
		addProjectsInDatabase();
		final CountDownLatch signal = new CountDownLatch(1);
		DataManager.getInstance().getFavorites(new OnMultipleObjectsLoadedListener<Project>() {
			@Override
			public void onObjectsLoaded(List<Project> projects) {
				Assert.assertEquals(1, projects.size());
				Assert.assertEquals("test0", projects.get(0).getName());
				signal.countDown();
			}
		});
		signal.await();
	}

	@Test
	public void getAllProjectsTest() throws Exception {
		addProjectsInDatabase();
		final CountDownLatch signal = new CountDownLatch(1);
		DataManager.getInstance().getAllProjects(new OnMultipleObjectsLoadedListener<Project>() {
			@Override
			public void onObjectsLoaded(List<Project> projects) {
				Assert.assertEquals(2, projects.size());
				Assert.assertEquals("test0", projects.get(0).getName());
				Assert.assertEquals("test1", projects.get(1).getName());
				signal.countDown();
			}
		});
		signal.await();
	}

	@Test
	public void getProjectByIdTest() throws Exception {
		Map<Integer, String> map = addProjectsInDatabase();
		final CountDownLatch signal = new CountDownLatch(map.size());
		for (final Map.Entry<Integer, String> entry : map.entrySet()) {
			DataManager.getInstance().getProjectById(entry.getKey(), new OnObjectLoadedListener<Project>() {
				@Override
				public void onObjectLoaded(Project project) {
					Assert.assertEquals(entry.getValue(), project.getName());
					signal.countDown();
				}
			});
		}
		signal.await();
	}

	@Test
	public void getTasksByProjectIdTest() throws Exception {
		Map<Integer, Map<Integer, String>> map = addTasksInDatabase(addProjectsInDatabase().keySet());
		final CountDownLatch signal = new CountDownLatch(map.size());
		for (Map.Entry<Integer, Map<Integer, String>> entry : map.entrySet()) {
			final Map<Integer, String> subMap = entry.getValue();
			DataManager.getInstance().getTasksByProjectId(entry.getKey(), new OnMultipleObjectsLoadedListener<Task>() {
				@Override
				public void onObjectsLoaded(List<Task> tasks) {
					for (Task task : tasks) {
						Assert.assertEquals(subMap.get(task.getId()), task.getName());
					}
					signal.countDown();
				}
			});
		}
		signal.await();
	}

	@Test
	public void getTaskByIdTest() throws Exception {
		Map<Integer, Map<Integer, String>> map = addTasksInDatabase(addProjectsInDatabase().keySet());
		for (Map<Integer, String> subMap : map.values()) {
			final CountDownLatch signal = new CountDownLatch(subMap.size());
			for (final Map.Entry<Integer, String> entry : subMap.entrySet()) {
				DataManager.getInstance().getTaskById(entry.getKey(), new OnObjectLoadedListener<Task>() {
					@Override
					public void onObjectLoaded(Task task) {
						Assert.assertEquals(entry.getValue(), task.getName());
						signal.countDown();
					}
				});
			}
			signal.await();
		}
	}

	@Test
	public void getInProgressTasksTest() throws Exception {
		addTasksInDatabase(addProjectsInDatabase().keySet());
		final CountDownLatch signal = new CountDownLatch(1);
		DataManager.getInstance().getInProgressTasks(new OnMultipleObjectsLoadedListener<Task>() {
			@Override
			public void onObjectsLoaded(List<Task> tasks) {
				Assert.assertEquals(2, tasks.size());
				Assert.assertEquals("task00", tasks.get(0).getName());
				Assert.assertEquals("task10", tasks.get(1).getName());
				signal.countDown();
			}
		});
		signal.await();
	}

	@Test
	public void updateTest() throws Exception {
		DataManager.getInstance().initialize(InstrumentationRegistry.getTargetContext());
		DatabaseUtils.cleanDatabase();
		final Project project = new Project(CursorMock.createProjectCursorMock(Project.NONE, "test0", null, null, null, null, 3, Color.BLACK, 1));
		final CountDownLatch signal = new CountDownLatch(1);
		DataManager.getInstance().save(project, new OnNewObjectCreatedListener() {
			@Override
			public void onNewObjectCreated(int projectId) {
				Project newProject = new Project(CursorMock.createProjectCursorMock(projectId, "updated", null, null, null, null, 3, Color.BLACK, 1));
				DataManager.getInstance().update(newProject);
				signal.countDown();
			}
		});
		signal.await();
		final CountDownLatch signal2 = new CountDownLatch(1);
		DataManager.getInstance().getAllProjects(new OnMultipleObjectsLoadedListener<Project>() {
			@Override
			public void onObjectsLoaded(List<Project> projects) {
				Assert.assertEquals(1, projects.size());
				Assert.assertEquals("updated", projects.get(0).getName());
				signal2.countDown();
			}
		});
		signal2.await();
	}

	@Test
	public void removeTest() throws Exception {
		Map<Integer, String> map = addProjectsInDatabase();
		final List<Map.Entry<Integer, String>> list = new ArrayList<>(map.entrySet());
		final CountDownLatch signal = new CountDownLatch(1);
		DataManager.getInstance().getAllProjects(new OnMultipleObjectsLoadedListener<Project>() {
			@Override
			public void onObjectsLoaded(List<Project> projects) {
				for (Project project : projects) {
					if (project.getId() == list.get(0).getKey()) {
						DataManager.getInstance().remove(projects.get(0), new OnDataChangedListener() {
							@Override
							public void onDataChanged() {
								signal.countDown();
							}
						});
					}
				}
			}
		});
		signal.await();
		final CountDownLatch signal2 = new CountDownLatch(1);
		DataManager.getInstance().getAllProjects(new OnMultipleObjectsLoadedListener<Project>() {
			@Override
			public void onObjectsLoaded(List<Project> projects) {
				Assert.assertEquals(1, projects.size());
				Assert.assertEquals(list.get(1).getValue(), projects.get(0).getName());
				signal2.countDown();
			}
		});
		signal2.await();
	}

	private Map<Integer, String> addProjectsInDatabase() throws Exception {
		DataManager.getInstance().initialize(InstrumentationRegistry.getTargetContext());
		DatabaseUtils.cleanDatabase();
		Project project = new Project(CursorMock.createProjectCursorMock(Project.NONE, "test0", null, null, null, null, 3, Color.BLACK, 1));
		Project project2 = new Project(CursorMock.createProjectCursorMock(Project.NONE, "test1", null, null, null, null, 3, Color.BLACK, 0));
		final CountDownLatch signal = new CountDownLatch(2);
		@SuppressLint("UseSparseArrays") final Map<Integer, String> map = new HashMap<>();
		DataManager.getInstance().save(project, new OnNewObjectCreatedListener() {
			@Override
			public void onNewObjectCreated(int projectId) {
				map.put(projectId, "test0");
				signal.countDown();
			}
		});
		DataManager.getInstance().save(project2, new OnNewObjectCreatedListener() {
			@Override
			public void onNewObjectCreated(int projectId) {
				map.put(projectId, "test1");
				signal.countDown();
			}
		});
		signal.await();
		return map;
	}

	private Map<Integer, Map<Integer, String>> addTasksInDatabase(Collection<Integer> projectIds) throws Exception {
		final CountDownLatch signal = new CountDownLatch(projectIds.size() * 2);
		@SuppressLint("UseSparseArrays") Map<Integer, Map<Integer, String>> map = new HashMap<>();
		int i = 0;
		for (int projectId : projectIds) {
			@SuppressLint("UseSparseArrays") final Map<Integer, String> subMap = new HashMap<>();
			map.put(projectId, subMap);
			final Task task = new Task(CursorMock.createTaskCursorMock(Task.NONE, "task" + i + "0", null, null, null, null, 2, projectId, Task.State.IN_PROGRESS.name()));
			DataManager.getInstance().save(task, new OnNewObjectCreatedListener() {
				@Override
				public void onNewObjectCreated(int taskId) {
					subMap.put(taskId, task.getName());
					signal.countDown();
				}
			});
			final Task task2 = new Task(CursorMock.createTaskCursorMock(Task.NONE, "task" + i + "1", null, null, null, null, 2, projectId, Task.State.COMPLETED.name()));
			DataManager.getInstance().save(task2, new OnNewObjectCreatedListener() {
				@Override
				public void onNewObjectCreated(int taskId) {
					subMap.put(taskId, task2.getName());
					signal.countDown();
				}
			});
			i++;
		}
		signal.await();
		return map;
	}
}
