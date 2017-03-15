package com.concordia.mcga.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.concordia.mcga.activities.R;
import com.concordia.mcga.models.Building;
import com.concordia.mcga.models.Campus;
import com.concordia.mcga.models.POI;
import com.concordia.mcga.models.Room;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This adapter searches through both campuses to find POIs (for now, buildings) that match a query.
 */
public class POISearchAdapter extends BaseExpandableListAdapter {
    private Context context;

    public static final int SGW_INDEX = 0;
    public static final int LOY_INDEX = 1;

    private List<Building> sgwFilteredList;
    private List<Building> loyolaFilteredList;
    private List<Room> roomFilteredList;

    private List<List> masterList;

    /**
     * The constructor initializes the empty lists that store the currently queried POIs
     * @param context View context to search
     */
    public POISearchAdapter(Context context) {
        this.context = context;

        this.sgwFilteredList = new ArrayList<>();
        this.loyolaFilteredList = new ArrayList<>();
        this.roomFilteredList = new ArrayList<>();

        this.masterList = new ArrayList<>();
    }

    /**
     * Evaluates the number of campuses active based on the current queried POIs
     * @return The number of active campuses
     */
    @Override
    public int getGroupCount() {
        return masterList.size();
    }

    /**
     * This overrides the BaseExpandableList function to get the number of children (POI) entries
     * @param childPosition Campus index
     * @return Number of POIs specific to the passed campus
     */
    @Override
    public int getChildrenCount(int childPosition) {
        return masterList.get(childPosition).size();
    }

    /**
     * This overrides the BaseExpandableList function to get the POI group specified by an index.
     * @param groupPosition Active campus index
     * @return The campus specified by the index
     */
    @Override
    public Object getGroup(int groupPosition) {
        return masterList.get(groupPosition);
    }

    /**
     * This overrides the BaseExpandableList function to get an actual menu item - in this case, POI
     * @param groupPosition The active campus index (see getGroup)
     * @param childPosition The POI index within the specified campus
     * @return The POI that exists according to the filtered query
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return masterList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * Helper function to determine whether a given filtered POI list (from the query) is empty
     * @param index The campus to query
     * @return True if the query-filtered campus list is empty, false if it has items
     */
    public boolean getGroupIsEmpty(int index) {
        return masterList.get(index).isEmpty();
    }

    /**
     * Inflates and returns the view associated with the index-specified campus or POI group
     * @param groupPosition POIgroup index
     * @param b Whether the group is collapsed or expanded
     * @param view The old view to reuse, if possible
     * @param viewGroup The parent that this view will be attached to
     * @return The inflated POI group view
     */
    @Override
    public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {
        String title;
        int resId;

        List group = (List) getGroup(groupPosition);
        if (group == sgwFilteredList) {
            title = "Sir George Williams";
            resId = R.mipmap.ic_sgw_campus;
        } else if (group == loyolaFilteredList) {
            title = "Loyola";
            resId = R.mipmap.ic_loy_campus;
        } else {
            title = "Classrooms";
            resId = R.mipmap.ic_h_building; // Placeholder
        }

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.poi_search_group_row, null);
        }

        CircleImageView campusImage = (CircleImageView) view.findViewById(R.id.groupImage);
        campusImage.setImageResource(resId);

        TextView campusRow = (TextView) view.findViewById(R.id.groupText);
        campusRow.setText(title);

        return view;
    }

    /**
     * Inflates and returns the view associated with the POI list item. For now, a building name
     * @param groupPosition The group/campus index
     * @param childPosition The child/POI index
     * @param b Whether the child is the last one in the group
     * @param view Existing view to reuse if possible
     * @param viewGroup The parent group this view will be attached to
     * @return The POI menu item (building name)
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View view,
                             ViewGroup viewGroup) {

        POI poi = (POI)getChild(groupPosition, childPosition);

        if (poi == null) {
            return null;
        }

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.poi_search_child_row, null);
        }

        TextView buildingRow = (TextView) view.findViewById(R.id.childText);
        int backgroundColor = (childPosition % 2) == 0 ? Color.WHITE : Color.LTGRAY;
        view.setBackgroundColor(backgroundColor);
        buildingRow.setText(poi.getName());

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    /**
     * This is the actual filter to select the POIs specified by the passed string.
     * The POIs that are determined to meet the query criteria are included in the filtered lists
     * (see constructor.)
     * @param query The string query to match POIs against
     */
    public void filterData(String query) {
        query = query.toLowerCase();
        sgwFilteredList.clear();
        loyolaFilteredList.clear();
        roomFilteredList.clear();
        masterList.clear();

        if (query.isEmpty()) {
            // loyolaFilteredList.addAll(Campus.LOY.getBuildings());
        } else {
            // Get matching SGW buildings
            if (Campus.SGW.getName().toLowerCase().contains(query) ||
                    Campus.SGW.getShortName().toLowerCase().contains(query)) {
                sgwFilteredList.addAll(Campus.SGW.getBuildings());
            } else {
                List<Building> sgwList = new ArrayList<>();
                for (Building building : Campus.SGW.getBuildings()) {
                    if (building.getName().toLowerCase().contains(query) ||
                            building.getShortName().toLowerCase().contains(query)) {
                        sgwList.add(building);
                    }
                }
                sgwFilteredList.addAll(sgwList);
            }

            // Get matching Loyola buildings
            if (Campus.LOY.getName().toLowerCase().contains(query) ||
                    Campus.LOY.getShortName().toLowerCase().contains(query)) {
                loyolaFilteredList.addAll(Campus.LOY.getBuildings());
            } else {
                List<Building> loyList = new ArrayList<>();
                for (Building building : Campus.LOY.getBuildings()) {
                    if (building.getName().toLowerCase().contains(query) ||
                            building.getShortName().toLowerCase().contains(query)) {
                        loyList.add(building);
                    }
                }
                loyolaFilteredList.addAll(loyList);
            }

            // Get matching rooms
            List<Room> roomList = new ArrayList<>();
            for (Building building : Campus.LOY.getBuildings()) {
                for (Room room : building.getRooms()) {
                    if (room.getName().toLowerCase().contains(query)) {
                        roomList.add(room);
                    }
                }
            }
            for (Building building : Campus.SGW.getBuildings()) {
                for (Room room : building.getRooms()) {
                    if (room.getName().toLowerCase().contains(query)) {
                        roomList.add(room);
                    }
                }
            }
            roomFilteredList.addAll(roomList);
        }

        if (!sgwFilteredList.isEmpty()) {
            masterList.add(sgwFilteredList);
        }
        if (!loyolaFilteredList.isEmpty()) {
            masterList.add(loyolaFilteredList);
        }
        if (!roomFilteredList.isEmpty()) {
            masterList.add(roomFilteredList);
        }
        notifyDataSetChanged();
    }
}

