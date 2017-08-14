package teste.lucasvegi.pokemongo.Util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Lucas on 05/01/2017.
 */
public class InternetUtil {

    //verifica a existência de internet
    public static boolean isNetworkAvailable(Context ctx) {
        try {
            ConnectivityManager manager = (ConnectivityManager) ((Activity) ctx).getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();

            boolean isAvailable = false;
            if (networkInfo != null && networkInfo.isConnected()) {
                isAvailable = true;
            }
            return isAvailable;
        }catch (Exception e){
            Log.e("INTERNET", "Erro ao verificar a conexão com a Internet: " + e.getMessage());
            return false;
        }
    }

    public static void exibeToastFaltaInternet(String msg, Context ctx){
        Toast toast = Toast.makeText(ctx, "SEM CONEXÃO COM A INTERNET!\n" + msg, Toast.LENGTH_LONG);

        //centralizando a mensagem do Toast
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if( v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }
}
