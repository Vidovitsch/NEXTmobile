package nextweek.fontys.next.app.Data;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import nextweek.fontys.next.app.Activities.SplashActivity;

/**
 * Created by David on 27-3-2017.
 */

public class DBManipulator {

    private static DBManipulator instance = null;
    private DatabaseReference database;
    private FirebaseUser fbUser = null;

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

    public void setScanned() {
        String uid = fbUser.getUid();
        DatabaseReference ref = database.child("User").child(uid).child("GroupID").getRef();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int groupID = Integer.valueOf(String.valueOf(dataSnapshot.getValue()));
                database.child("Group").child(String.valueOf(groupID)).child("Members")
                        .child(fbUser.getUid()).setValue("S");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("getGroupByUser()", databaseError.toString());
            }
        });
    }

    public void checkScanned(final SplashActivity activity) {
        if (fbUser != null) {
            String uid = fbUser.getUid();
            DatabaseReference ref = database.child("User").child(uid).child("GroupID").getRef();
            ref.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int groupID = Integer.valueOf(String.valueOf(dataSnapshot.getValue()));
                    DatabaseReference ref = database.child("Group").child(String.valueOf(groupID))
                            .child("Members").child(fbUser.getUid()).getRef();
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String result = (String) dataSnapshot.getValue();
                            activity.scannedCheck(result.equals("S"));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            activity.scannedCheck(false);
                            Log.e("checkScanned()", databaseError.toString());
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("getGroupByUser()", databaseError.toString());
                }
            });
        }
    }
}
