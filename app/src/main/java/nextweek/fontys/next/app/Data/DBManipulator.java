package nextweek.fontys.next.app.Data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nextweek.fontys.next.app.Activities.InfoActivity;
import nextweek.fontys.next.app.Activities.ScanActivity;

/**
 * Created by David on 27-3-2017.
 */

public class DBManipulator {

    private static DBManipulator instance = null;
    private DatabaseReference database;

    private String currentGroupID = null;
    private int currentGroupLocation = -1;

    private InfoActivity infoActivity = null;
    private ScanActivity scanActivity = null;

    private DBManipulator() {
        database = FirebaseDatabase.getInstance().getReference();
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        setCurrentGroupID(fbUser);
    }

    public static DBManipulator getInstance() {
        if (instance == null) {
            instance = new DBManipulator();
        }
        return instance;
    }

    public int getCurrentGroupLocation() {
        return currentGroupLocation;
    }

    public void setInfoActivity(InfoActivity infoActivity) {
        this.infoActivity = infoActivity;
        scanActivity = null;
    }

    public void setScanActivity(ScanActivity scanActivity) {
        this.scanActivity = scanActivity;
        infoActivity = null;
    }

    public void validateScan(final ScanActivity activity, final int groupLocation) {
        DatabaseReference ref = database.child("Group").getRef();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    int location = Integer.valueOf(String.valueOf(snapshot.child("Location").getValue()));
                    if (location == groupLocation) {
                        activity.validateScan(false, Integer.valueOf(String.valueOf(snapshot.getKey())));
                        return;
                    }
                }
                setNewGroupLocation(groupLocation);
                activity.validateScan(true, 0);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }

    public void setNewGroupLocation(int newGroupLocation) {
        currentGroupLocation = newGroupLocation;
        database.child("Group").child(currentGroupID).child("Location").setValue(newGroupLocation);
    }

    private void setCurrentGroupID(FirebaseUser fbUser) {
        DatabaseReference ref = database.child("User").child(fbUser.getUid()).child("GroupID").getRef();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentGroupID = String.valueOf(dataSnapshot.getValue());

                //Chain-method to ensure it gets called only when this method is done
                setCurrentGroupLocation(currentGroupID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void setCurrentGroupLocation(final String currentGroupID) {
        DatabaseReference ref = database.child("Group").child(currentGroupID).child("Location").getRef();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentGroupLocation = Integer.valueOf(String.valueOf(dataSnapshot.getValue()));
                updateActivities();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void updateActivities() {
        if (infoActivity != null) {
            infoActivity.updateGroupLocation();
        }
        if (scanActivity != null) {
            scanActivity.openInfoActivity();
        }
    }
}
