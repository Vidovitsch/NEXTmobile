package nextweek.fontys.next.app.Data;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import nextweek.fontys.next.app.Activities.Organisation.AllocActivity;

/**
 * Created by David on 10-5-2017.
 */

public class DBManipulatorOrg {

    private DatabaseReference database = null;
    private AllocActivity activity = null;
    private Map<Integer, String> groupLocations = null;

    /**
     * This class is linked with the AllocActivity (allocation activity)
     * @param activity
     */
    public DBManipulatorOrg(AllocActivity activity) {
        database = FirebaseDatabase.getInstance().getReference();

        this.activity = activity;
        loadGroupLocations();
    }

    public ArrayList<Integer> getGroupLocations() {
        ArrayList<Integer> list = new ArrayList<>();
        list.addAll(groupLocations.keySet());
        return list;
    }

    private void loadGroupLocations() {
        groupLocations = new TreeMap<>();
        DatabaseReference ref = database.child("GroupLocation");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    groupLocations.put(Integer.valueOf(ds.getKey()), String.valueOf(ds.getValue()));
                }
                activity.setupViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
