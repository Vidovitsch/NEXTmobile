package nextweek.fontys.next.app.Data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nextweek.fontys.next.app.Activities.InfoActivity;
import nextweek.fontys.next.app.Activities.LogActivity;
import nextweek.fontys.next.app.Activities.ScanActivity;

/**
 * Created by David on 27-3-2017.
 */

public class DBManipulator {

    private static DBManipulator instance = null;
    private FirebaseUser fbUser = null;
    private DatabaseReference database;

    private String currentGroupID = null;
    private int currentGroupLocation = -1;

    private InfoActivity infoActivity = null;
    private ScanActivity scanActivity = null;
    private LogActivity logActivity = null;

    private DBManipulator() {
        database = FirebaseDatabase.getInstance().getReference();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        setCurrentGroupID();
    }

    /**
     * Gets the instance of this class
     * @return instance of this class
     */
    public static DBManipulator getInstance() {
        if (instance == null) {
            instance = new DBManipulator();
        }
        return instance;
    }

    /**
     * Gets the current group location of signed in user.
     * @return group location
     */
    public int getCurrentGroupLocation() {
        return currentGroupLocation;
    }

    public void setSignedInUser() {
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Sets the current active activity within this class
     * @param infoActivity currently active
     */
    public void setInfoActivity(InfoActivity infoActivity) {
        this.infoActivity = infoActivity;
        scanActivity = null;
        logActivity = null;
    }

    /**
     * Sets the current active activity within this class
     * @param scanActivity currently active
     */
    public void setScanActivity(ScanActivity scanActivity) {
        this.scanActivity = scanActivity;
        infoActivity = null;
        logActivity = null;
    }

    /**
     * Sets the current active activity within this class
     * @param logActivity currently active
     */
    public void setLogActivity(LogActivity logActivity) {
        this.logActivity = logActivity;
        infoActivity = null;
        scanActivity = null;
    }

    /**
     * Validates the made scan.
     * @param activity: scan activity
     * @param groupLocation: scanned number
     */
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

    /**
     * Sets the new scanned group location
     * @param newGroupLocation of the current group
     */
    public void setNewGroupLocation(int newGroupLocation) {
        currentGroupLocation = newGroupLocation;
        database.child("Group").child(currentGroupID).child("Location").setValue(newGroupLocation);
    }

    /**
     * Sets the groupID of the current group of the signed in user
     */
    public void setCurrentGroupID() {
        if (fbUser != null) {
            DatabaseReference ref = database.child("User").child(fbUser.getUid()).child("GroupID").getRef();
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentGroupID = String.valueOf(dataSnapshot.getValue());

                    //Chain-method to ensure it gets called only when this method is done
                    setCurrentGroupLocation(currentGroupID);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    /**
     * Sets the group location of the group of the signed in user.
     * @param currentGroupID of the group of the signed in user.
     */
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

    /**
     * Updates the currently active activity on location change (within Firebase)
     */
    private void updateActivities() {
        if (infoActivity != null) {
            infoActivity.updateGroupLocation();
        }
        if (scanActivity != null) {
            scanActivity.openInfoActivity();
        }
        if (logActivity != null) {
            logActivity.onLoginSuccess(currentGroupLocation);
        }
    }
}
