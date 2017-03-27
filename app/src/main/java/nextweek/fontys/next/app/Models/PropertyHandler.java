package nextweek.fontys.next.app.Models;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Callable;

/**
 * Created by David on 27-3-2017.
 */

public class PropertyHandler implements Callable<String[]> {

    private final static String LOG_PROPERTIES_FILE = "/log.properties";
    private String path;

    /**
     * Default constructor
     */
    public PropertyHandler(Context context) {
        path = context.getFilesDir().getPath();
    }

    /**
     * Constructor for setting new login values
     * @param username
     * @param password
     */
    public PropertyHandler(Context context, String username, String password) {
        path = context.getFilesDir().getPath();
        setFileData(username, password);
    }

    @Override
    public String[] call() throws Exception {
        checkForFile();
        return getFileData();
    }

    /**
     * Fetches property values from the properties file. And checks them with the database.
     * @return The two values stored in the properties file.
     */
    private String[] getFileData() {
        String[] data = new String[2];
        Properties props = new Properties();
        try {
            FileInputStream input = new FileInputStream(path + LOG_PROPERTIES_FILE);
            props.load(input);
            data[0] = props.getProperty("Username");
            data[0] = props.getProperty("Password");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * Sets login values of the user in a properties file.
     * @param username
     * @param password
     */
    private void setFileData(String username, String password) {
        Properties props = new Properties();
        try {
            FileInputStream input = new FileInputStream(path + LOG_PROPERTIES_FILE);
            props.load(input);
            props.setProperty("Username", username);
            props.setProperty("Password", password);

            FileOutputStream output = new FileOutputStream(path + LOG_PROPERTIES_FILE);
            props.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the properties file exists.
     * If not a new properties file will be created.
     * Default keys: Username, Password.
     * Default values: "".
     */
    private void checkForFile() {
        File file = new File(path + LOG_PROPERTIES_FILE);
        if (!file.exists()) {
            Properties props = new Properties();
            try {
                FileOutputStream output = new FileOutputStream(path + LOG_PROPERTIES_FILE);
                props.setProperty("Username", "");
                props.setProperty("Password", "");
                props.store(output, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
