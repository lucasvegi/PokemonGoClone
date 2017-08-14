package teste.lucasvegi.pokemongo.Controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import teste.lucasvegi.pokemongo.Model.ControladoraFachadaSingleton;
import teste.lucasvegi.pokemongo.R;
import teste.lucasvegi.pokemongo.Util.InternetUtil;
import teste.lucasvegi.pokemongo.Util.SecurityUtil;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
    }

    public void clickLogin(View view){
        try {
            Log.i("LOGIN", "Autenticando a entrada no sistema...");

            EditText edtUsuario = (EditText) findViewById(R.id.edtUsuarioLogin);
            EditText edtSenha = (EditText) findViewById(R.id.edtSenhaLogin);

            //Obtem dados do usuário
            String usuario = edtUsuario.getText().toString();
            String senha = edtSenha.getText().toString();

            //usado para validar dados antes de enviar ao servidor
            boolean loginOk = true;

            //Verifica preenchimento de campos obrigatórios e valida dados
            if (!SecurityUtil.isAlphanumeric(usuario)) { //verifica se user não é alfanumérico por questão de segurança
                Toast.makeText(this, "Informe um usuário válido!", Toast.LENGTH_SHORT).show();
                loginOk = false;
            }else if (!SecurityUtil.isAlphanumeric(senha)) { //verifica se senha não é alfanumérico por questão de segurança
                Toast.makeText(this, "Informe uma senha válida!", Toast.LENGTH_SHORT).show();
                loginOk = false;
            }

            //tenta fazer o login se os dados de usuário forem válidos
            if(loginOk) {
                //verifica a existência de internet
                if (InternetUtil.isNetworkAvailable(LoginActivity.this)) {

                    //Delega para um async task a tarefa de validar o login e sincronizar dados com o servidor
                    ControladoraFachadaSingleton.getInstance().loginUser(usuario, senha, LoginActivity.this);

                } else {
                    InternetUtil.exibeToastFaltaInternet("Verifique as configurações de Internet e tente novamente.", LoginActivity.this);
                }
            }

        }catch (Exception e){
            Log.e("LOGIN", "ERRO: " + e.getMessage());
        }

    }

    public void clickCadastrar(View v){
        Intent it = new Intent(this, CadastrarActivity.class);
        startActivity(it);
        finish();
    }
}
