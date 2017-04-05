package nextweek.fontys.next.app.Data;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nextweek.fontys.next.app.Activities.LogActivity;
import nextweek.fontys.next.app.Activities.ScanActivity;
import nextweek.fontys.next.app.Activities.SplashActivity;

/**
 * Created by David on 27-3-2017.
 */

public class DBManipulator {

    private static DBManipulator instance = null;
    private DatabaseReference database;
    private FirebaseUser fbUser = null;

    private boolean locationTaken = false;
    private String locationTakenGroupID;

    private DBManipulator() {
        database = FirebaseDatabase.getInstance().getReference();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public static DBManipulator getInstance() {
        if (instance == null) {
            instance = new DBManipulator();
        }
        return instance;
    }

    public void validateScan(final ScanActivity activity, final String groupLocation) {
        DatabaseReference ref = database.child("Group").getRef();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String location = String.valueOf(snapshot.child("Location").getValue());
                    if (location.equals(groupLocation)) {
                        locationTaken = true;
                        locationTakenGroupID = String.valueOf(snapshot.getKey());
                    }
                }

                if (locationTaken) {
                    activity.validateScan(false, groupLocation, locationTakenGroupID);
                    locationTaken = false;
                } else {
                    setNewGroupLocation(groupLocation);
                    activity.validateScan(true, groupLocation, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }

    public void checkScannedSigned(final SplashActivity activity) {
        DatabaseReference ref = database.child("User").child(fbUser.getUid()).child("GroupID").getRef();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String groupID = String.valueOf(dataSnapshot.getValue());
                DatabaseReference ref = database.child("Group").child(groupID).child("Location").getRef();
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int location = Integer.valueOf(String.valueOf(dataSnapshot.getValue()));
                        if (location != 0) {
                            activity.checkScannedSigned(true, location);
                        } else {
                            activity.checkScannedSigned(false, 0);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public void checkScannedUnsigned(final LogActivity activity) {
        DatabaseReference ref = database.child("User").child(fbUser.getUid()).child("GroupID").getRef();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String groupID = String.valueOf(dataSnapshot.getValue());
                DatabaseReference ref = database.child("Group").child(groupID).child("Location").getRef();
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int location = Integer.valueOf(String.valueOf(dataSnapshot.getValue()));
                        if (location != 0) {
                            activity.checkScannedUnsigned(true, location);
                        } else {
                            activity.checkScannedUnsigned(false, 0);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void setNewGroupLocation(final String groupLocation) {
        DatabaseReference ref = database.child("User").child(fbUser.getUid()).child("GroupID").getRef();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String groupID = String.valueOf(dataSnapshot.getValue());
                database.child("Group").child(groupID).child("Location").setValue(groupLocation);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
}
