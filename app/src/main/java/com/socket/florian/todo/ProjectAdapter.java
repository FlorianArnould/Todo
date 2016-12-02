package com.socket.florian.todo;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
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

final class ProjectAdapter extends BaseAdapter implements Filterable {

    private Context _context;
    private List<Project> _projects;
    private List<Project> _filteredProjects;
    private Filter _filter;

    ProjectAdapter(Context context){
        super();
        _context = context;
        _projects = new ArrayList<>();
        _filteredProjects = _projects;
        getFilter();
    }

    public void add(Project project){
        _projects.add(project);
    }

    void updateAll(List<Project> projects){
        _projects.clear();
        for(Project p : projects){
            _projects.add(p);
        }
        notifyDataSetChanged();
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
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_main, parent, false);
        }
        TextView text = (TextView) view.findViewById(R.id.rowProjectRemainingTime);
        text.setText(R.string.unlimited_time);
        TextView name = (TextView) view.findViewById(R.id.rowProjectName);
        Project project = _filteredProjects.get(position);
        name.setText(project.getName());
        if (project.isStarting()) {
            Drawable drawable = _context.getDrawable(R.drawable.rocket_launch);
            drawable.setTint(_context.getColor(android.R.color.tertiary_text_light));
            name.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        } else if (project.isInProgress()) {
            name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_flying, 0, 0, 0);
        } else {
            name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket, 0, 0, 0);
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
