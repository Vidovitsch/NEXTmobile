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
    private ArrayList<Integer> groupLocations;

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
        return this.groupLocations;
    }

    public void validateScan(final String code, final Integer location) {
        //Check if the scanned code exists
        DatabaseReference ref = database.child("QRCode");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exists = false;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (String.valueOf(ds.getKey()).equals(code)) {
                        //Scanned code exists
                        exists = true;

                        //Check if the scanned code is already connected to a location
                        DatabaseReference ref2 = database.child("GroupLocation");
                        ref2.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                boolean connected = false;
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    if (String.valueOf(ds.getValue()).equals(code)) {
                                        //Scanned code is already connected to a location
                                        connected = true;
                                        activity.handleScan(false, "This QR-code is already connected to location " +
                                            ds.getKey());
                                    }
                                }
                                if (!connected) {
                                    //Scanned code is no yet connected to a location

                                    //Connecting the code with the corresponding location
                                    DatabaseReference ref3 = database.child("GroupLocation")
                                            .child(String.valueOf(location));
                                    ref3.setValue(code);
                                    activity.handleScan(true, "QR-code has been connected to location " +
                                        String.valueOf(location));

                                    //Removing a used location from the list
                                    groupLocations.remove(location);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
                if (!exists) {
                    //Scanned code doesn't exist
                    activity.handleScan(false, "This code doesn't exist or is invalid");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadGroupLocations() {
        groupLocations = new ArrayList<>();
        DatabaseReference ref = database.child("GroupLocation");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //Only location with no qr-code
                    if (String.valueOf(ds.getValue()).equals("null")) {
                        groupLocations.add(Integer.valueOf(ds.getKey()));
                    }
                }
                activity.setupViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
