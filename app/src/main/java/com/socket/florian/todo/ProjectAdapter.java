package com.socket.florian.todo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.socket.florian.todo.storage.Project;

import java.util.ArrayList;
import java.util.List;

public final class ProjectAdapter extends BaseAdapter implements Filterable {

    private Context _context;
    private List<Project> _projects;
    private List<Project> _filteredProjects;
    private Filter _filter;

    public ProjectAdapter(Context context){
        super();
        _context = context;
        _projects = new ArrayList<>();
        _filteredProjects = _projects;
        getFilter();
    }

    public void add(Project project){
        _projects.add(project);
    }

    public void updateAll(List<Project> projects){
        _projects.clear();
        for(Project p : projects){
            _projects.add(p);
        }
    }

    @Override
    public int getCount() {
        return _filteredProjects.size();
    }

    @Override
    public Object getItem(int i) {
        return _filteredProjects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.row, null);
        TextView text = (TextView) view.findViewById(R.id.rowProjectRemainingTime);
        TextView name = (TextView) view.findViewById(R.id.rowProjectName);
        ImageView image = (ImageView) view.findViewById(R.id.rowProgressIcon);
        Project project = _filteredProjects.get(position);
        name.setText(project.getName());
        if (project.isStarting()) {
            image.setImageResource(R.drawable.rocket_launch);
        } else if (project.isInProgress()) {
            image.setImageResource(R.drawable.rocket_flying);
        } else {
            image.setImageResource(R.drawable.rocket);
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        if(_filter == null){
            _filter = new ProjectFilter();
        }
        return _filter;
    }

    private class ProjectFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                List<Project> tempList = new ArrayList<>();
                for(Project p : _projects){
                    if(p.getName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        tempList.add(p);
                    }
                }
                results.count = tempList.size();
                results.values = tempList;
            } else {
                results.count = _projects.size();
                results.values = _projects;
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            _filteredProjects = (List<Project>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
