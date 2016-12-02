package com.socket.florian.todo.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.socket.florian.todo.R;
import com.socket.florian.todo.storage.Task;

import java.util.ArrayList;
import java.util.List;


class TaskAdapter extends BaseAdapter implements Filterable{

    private Context _context;
    private List<Task> _tasks;
    private List<Task> _filteredTasks;
    private Filter _filter;

    TaskAdapter(Context context){
        super();
        _context = context;
        _tasks = new ArrayList<>();
        _filteredTasks = _tasks;
        getFilter();
    }

    public void add(Task task){
        _tasks.add(task);
    }

    void updateAll(List<Task> tasks){
        _tasks.clear();
        for(Task t : tasks){
            _tasks.add(t);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return _filteredTasks.size();
    }

    @Override
    public Object getItem(int i) {
        return _filteredTasks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_task, parent, false);
        }
        TextView name = (TextView) view.findViewById(R.id.rowTaskName);
        Task task = _filteredTasks.get(position);
        name.setText(task.getName());
        return view;
    }

    @Override
    public Filter getFilter() {
        if(_filter == null){
            _filter = new TaskFilter();
        }
        return _filter;
    }

    private class TaskFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                List<Task> tempList = new ArrayList<>();
                for(Task t : _tasks){
                    if(t.getName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        tempList.add(t);
                    }
                }
                results.count = tempList.size();
                results.values = tempList;
            } else {
                results.count = _tasks.size();
                results.values = _tasks;
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            _filteredTasks = (List<Task>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
