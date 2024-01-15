package be.ehb.koalaexpress.Tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import be.ehb.koalaexpress.KoalaDataRepository;
import be.ehb.koalaexpress.LocalFileOperations;
import be.ehb.koalaexpress.R;

public class Task_ImageLoad extends AsyncTask<String, Object, Bitmap> {

    private ImageView mImageView;
    private Context mContext;
    public Boolean mFileIsGevonden;

    public Task_ImageLoad(ImageView imageView) {

        mImageView = imageView;
        mContext = imageView.getContext();
        mFileIsGevonden=false;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        // this executes in a background thread
        Bitmap returnBitmap = null;
        try {
            // Imagenaam komt binnen via parameter van execute
            String filename = params[0];
            String localfoldername = "/images";
            String localfilename = localfoldername+ "/" + filename;
            //controleer eerst of filename lokaal gekend is
            if(!LocalFileOperations.FileExists(localfilename, mContext) && KoalaDataRepository.getInstance().mServerIsOnline) {
                // img niet on local file, try to get from web als online bent
                String url = "http://www.Jursairplanefactory.com/koalaimg/" + filename;
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                if (input != null) {
                    //bitmap is correct ontvangen van web, nu opslaan lokaal voor toekomstig rechtstreeks lezen
                    LocalFileOperations.createFolderIfNotExists(localfoldername, mContext);
                    LocalFileOperations.saveInputStreamToLocalFile(input, localfilename, mContext);
                }
            }
            if(LocalFileOperations.FileExists(localfilename, mContext)) {
                // als file nu bestaat al lokaal, openen via lokale file  /data/user/0/be.ehb.koalaexpress/files/test.bmp
                InputStream inputStreamFromFile = LocalFileOperations.loadInputStreamFromLocalFile(localfilename, mContext);
                if (inputStreamFromFile != null) {
                    returnBitmap = BitmapFactory.decodeStream(inputStreamFromFile);
                    try {
                        inputStreamFromFile.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    mFileIsGevonden = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        // this exectutes in the main UI thread
        if (mFileIsGevonden == true) {
            mImageView.setImageBitmap(result);
        }
        else {
            mImageView.setImageResource(R.mipmap.ic_no_image);
        }
    }

}