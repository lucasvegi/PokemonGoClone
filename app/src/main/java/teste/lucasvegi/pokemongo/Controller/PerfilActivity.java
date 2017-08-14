package teste.lucasvegi.pokemongo.Controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import teste.lucasvegi.pokemongo.Model.ControladoraFachadaSingleton;
import teste.lucasvegi.pokemongo.Model.Pokemon;
import teste.lucasvegi.pokemongo.Model.PokemonCapturado;
import teste.lucasvegi.pokemongo.R;

public class PerfilActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        //obtem referências das views
        ImageView imageView = (ImageView) findViewById(R.id.imgTreinadorPerfil);
        TextView txtInicioAventura = (TextView) findViewById(R.id.txtInicioAventuraPerfil);
        TextView txtNumCapturas = (TextView) findViewById(R.id.txtNumCapturasPerfil);
        TextView txtNomeTreinador = (TextView) findViewById(R.id.txtNomeTreinadorPerfil);

        try {
            //Define o nome do treinador
            txtNomeTreinador.setText(ControladoraFachadaSingleton.getInstance().getUsuario().getLogin());

            //Define a imagem do perfil baseando-se no sexo do usuário
            if(ControladoraFachadaSingleton.getInstance().getUsuario().getSexo().equals("M"))
                imageView.setImageResource(R.drawable.male_grande);
            else
                imageView.setImageResource(R.drawable.female_grande);

            //Define o início da aventura
            txtInicioAventura.setText(ControladoraFachadaSingleton.getInstance().getUsuario().getDtCadastro());

            //Define o número de pokemons capturados pelo usuário
            int contCaptura = 0;
            for (Map.Entry<Pokemon,List<PokemonCapturado>> entry : ControladoraFachadaSingleton.getInstance().getUsuario().getPokemons().entrySet()){
                contCaptura += entry.getValue().size();
            }
            txtNumCapturas.setText(contCaptura+"");

        }catch (Exception e){
            Log.e("PERFIL", "ERRO: " + e.getMessage());
        }
    }

    public void clickLogout(View v){
        try {
            Log.i("LOGOUT", "Saindo...");

            AlertDialog.Builder alerta = new AlertDialog.Builder(this);
            alerta.setTitle("SAIR");
            alerta.setMessage("Deseja finalizar essa sessão?");

            //Configura ação para confirmação positiva
            alerta.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ControladoraFachadaSingleton.getInstance().logoutUser();
                    setResult(MapActivity.MENU_PERFIL);

                    finish();
                }
            });

            //Configura ação para negação da ação
            alerta.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            //Exibe janela de confirmação
            alerta.show();

        }catch (Exception e){
            Log.e("LOGOUT", "ERRO: " + e.getMessage());
        }

    }

    public void clickVoltar(View v){
        finish();
    }
}
