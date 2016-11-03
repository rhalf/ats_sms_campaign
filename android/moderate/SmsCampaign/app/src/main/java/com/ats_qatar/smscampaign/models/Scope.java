package com.ats_qatar.smscampaign.models;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.ats_qatar.smscampaign.services.SmsDetail;
import com.ats_qatar.smscampaign.services.SmsDispatcher;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rhalf on 9/27/2016.
 */

public class Scope extends Application implements Serializable {
    public static SmsDetail smsDetail;
    public static SmsDispatcher smsDispatcher;


//    public static  boolean isServiceRunning(Activity activity, Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }

    public static void deleteMessage(Context context) {
//        try {
//
//            Uri uri = Uri.parse("content://sms/inbox");
//            Cursor cursor = context.getContentResolver().query(uri, new String[] { "_id", "thread_id" }, "read=0", null, null);
//
//            if (cursor != null && cursor.moveToFirst()) {
//                do {
//                    String id = cursor.getString(0);
//                    String threadId = cursor.getString(1);
//
//                    Uri uriMessageThread = Uri.parse("content://sms/conversations/" + threadId);
//                    context.getContentResolver().delete(uriMessageThread, null, null);
//
//                    Uri uriMessageId = Uri.parse("content://sms/conversations/" + id);
//                    context.getContentResolver().delete(uriMessageId, null, null);
//
//                } while (cursor.moveToNext());
//            }
//            cursor.close();
//
//        } catch (Exception exception) {
//            exception.printStackTrace();
//        }
    }

    public static void log(String TAG, String message) {
        Log.d(TAG, "===============" + message + "================");
    }

    public static boolean isExpired(Calendar calendarTarget) {

        Calendar calendarNow = Calendar.getInstance();

        long iTarget = calendarTarget.getTimeInMillis();
        long  iNow = calendarNow.getTimeInMillis();

        if (iTarget > iNow){
            return false;
        } else {
            return true;
        }
    }

    public static int getMinutes(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return (hour * 60) + minute;
    }

    public static boolean isEnabledToday(boolean[] days) {
        Calendar calendar = Calendar.getInstance();

        boolean result = false;

        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.SUNDAY:
                result = days[0];
                break;
            case Calendar.MONDAY:
                result = days[1];
                break;
            case Calendar.TUESDAY:
                result = days[2];
                break;
            case Calendar.WEDNESDAY:
                result = days[3];
                break;
            case Calendar.THURSDAY:
                result = days[4];
                break;
            case Calendar.FRIDAY:
                result = days[5];
                break;
            case Calendar.SATURDAY:
                result = days[6];
                break;
        }

        return result;
    }

    public static long getNewId() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }

    private static String getAppRootPath() {
        File sdCard = Environment.getExternalStorageDirectory();
        File folder = new File(sdCard.getAbsolutePath(), "smsCampaign");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder.getAbsolutePath();
    }

    private static String getExportPath() {
        File folder = new File(getAppRootPath(), "export");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder.getAbsolutePath();
    }

    private static File getDeliveredReport() {
        File folder = new File(getExportPath(), "delivered");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder.getAbsolutePath(), Converter.toString(new Date(), Converter.DATE) + ".csv");
        if (!file.exists()) {
            try {
                file.createNewFile();

                FileOutputStream fileOutputStream = new FileOutputStream(file, true);

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Number, TimeProcess, TimeDelivered\n");
                fileOutputStream.write(stringBuilder.toString().getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static void writeDelivered(Sms sms) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(getDeliveredReport(), true);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(sms.number + "," + sms.timeProcessed + "," + sms.timeDelivered + "\n");
            fileOutputStream.write(stringBuilder.toString().getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getSentReport() {
        File folder = new File(getExportPath(), "sent");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder.getAbsolutePath(), Converter.toString(new Date(), Converter.DATE) + ".csv");
        if (!file.exists()) {
            try {
                file.createNewFile();

                FileOutputStream fileOutputStream = new FileOutputStream(file, true);

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Number,  TimeProcess, Status, TimeSent\n");
                fileOutputStream.write(stringBuilder.toString().getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static void writeSent(Sms sms) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(getSentReport(), true);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(sms.number + "," + sms.timeProcessed + "," + sms.sent + "," + sms.timeSent + "\n");
            fileOutputStream.write(stringBuilder.toString().getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getSummaryReport() {
        File folder = new File(getExportPath(), "summary");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder.getAbsolutePath(), Converter.toString(new Date(), Converter.DATE) + ".csv");
        if (!file.exists()) {
            try {
                file.createNewFile();

                FileOutputStream fileOutputStream = new FileOutputStream(file, true);

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Mode, DateTime, Status, Processed, Sent, Delivered\n");
                fileOutputStream.write(stringBuilder.toString().getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static void writeSummary(int mode, int processed, int sent, int delivered) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(getSentReport(), true);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(mode + "," + Converter.toString(Calendar.getInstance(), Converter.DATE_TIME) + "," + processed + "," + sent + "," + delivered + "\n");
            fileOutputStream.write(stringBuilder.toString().getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] getImportFiles() {
        File folder = new File(getAppRootPath(), "import");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder.list();
    }

    public static ArrayList<String> parseNumberFrom(String filename) throws Exception {
        ArrayList<String> numbers = new ArrayList<>();

        File file = new File(getAppRootPath() + "/import/" + filename);

        FileInputStream fileInputStream = new FileInputStream(file);

        int size = (int) file.length();
        byte[] buffer = new byte[size];

        fileInputStream.read(buffer, 0, size);

        String data = new String(buffer, "UTF-8");
        String[] items = data.split("\n");

        for (int index = 0; index < items.length; index++) {

            String[] object = items[index].split(",");

            for (int index1 = 0; index1 < object.length; index1++) {
                numbers.add(items[index]);
            }
        }

        return numbers;
    }

    public static void forwardFileToServer(Context context, String path) {

        try {

            FTPClient ftpClient = new FTPClient();

            ftpClient.connect("184.107.179.178");
            ftpClient.login("Administrator", "AxzKDYGHRr2Ps7Zs");
            ftpClient.changeWorkingDirectory("/report/");
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            BufferedInputStream buffIn = null;
            buffIn = new BufferedInputStream(new FileInputStream(new File(getAppRootPath() + "/import/" + path)));
            ftpClient.enterLocalPassiveMode();
            ftpClient.storeFile(path, buffIn);
            buffIn.close();
            ftpClient.logout();
            ftpClient.disconnect();


//            new FtpAsyncTask(MyActivity.this).execute(a);
//
//            // Connect to an FTP server on port 21.
//            ftp.connect(, 21, "Administrator", "AxzKDYGHRr2Ps7Zs");
//
//            // Set binary mode.
//            ftp.bin();
//
//            // Change to a new working directory on the FTP server.
//            ftp.cwd("/report/");
//
//            // Upload some files.
//            ftp.stor(new File(getAppRootPath() + "/import/" + path));
//
//            // Quit from the FTP server.
//            ftp.disconnect();
//
//            Toast.makeText(context,"Ftp Done..",Toast.LENGTH_SHORT).show();
        } catch (Exception exception) {
            Toast.makeText(context, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
