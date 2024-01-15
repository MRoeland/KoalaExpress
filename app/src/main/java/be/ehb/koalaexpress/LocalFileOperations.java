package be.ehb.koalaexpress;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LocalFileOperations {
    public static void saveInputStreamToLocalFile(InputStream input, String filename, Context context) {
        // Create a FileOutputStream to save the InputStream content to a file
        FileOutputStream outputStream = null;
        try {
            File file = new File(context.getFilesDir(), filename); // Replace with your desired file name and extension
            File f = context.getFilesDir();
            String s = f.toString();
            outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static InputStream loadInputStreamFromLocalFile(String filename, Context context) {
        // Reading from the saved file and creating an InputStream
        InputStream inputStreamFromFile = null;
        try {
            File file = new File(context.getFilesDir(), filename);
            inputStreamFromFile = new FileInputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // opgelet, niet vergeten om in de ontvangende functie te input stream te sluiten !!!
        return inputStreamFromFile;
    }

    public static boolean deleteFileIfExists(String filename, Context context) {
        File file = new File(context.getFilesDir(), filename);
        if (file.exists()) {
            if (file.delete()) {
                return true; // File deleted successfully
            } else {
                return false; // Failed to delete file
            }
        }
        return true; // File doesn't exist, no action needed
    }

    public static boolean FileExists(String filename, Context context) {
        File file = new File(context.getFilesDir(), filename);
        if (file.exists()) {
            return true;
        }
        return false;
    }
    public static boolean createFolderIfNotExists(String folderName, Context context) {
        // Get the app's internal files directory
        File directory = new File(context.getFilesDir(), folderName);
        boolean returnvalue = false;
        // Check if the directory already exists or create it if it doesn't
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                // Directory creation successful
                //Log.d("FolderCreation", "Folder created successfully: " + directory.getAbsolutePath());
                returnvalue = true;
            } else {
                // Directory creation failed
                Log.e("FolderCreation", "Failed to create directory: " + directory.getAbsolutePath());
            }
        } else {
            // Directory already exists
            returnvalue = true;
            //Log.d("FolderCreation", "Folder already exists: " + directory.getAbsolutePath());
        }
        return returnvalue;
    }
}
