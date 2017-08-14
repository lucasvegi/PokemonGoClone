package teste.lucasvegi.pokemongo.Controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import teste.lucasvegi.pokemongo.Model.ControladoraFachadaSingleton;
import teste.lucasvegi.pokemongo.R;
import teste.lucasvegi.pokemongo.Util.InternetUtil;
import teste.lucasvegi.pokemongo.Util.SecurityUtil;

public class CadastrarActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);
    }

    public void clickVoltar(View v){
        Intent it = new Intent(this,LoginActivity.class);
        startActivity(it);
        finish();
    }

    public void clickCadastrarUser(View view){
        try {
            Log.i("CADASTRO", "Cadastrando o usuário no sistema...");

            //usado para validar dados antes de enviar ao servidor
            boolean cadOk = true;

            EditText edtNome = (EditText) findViewById(R.id.edtNomeCadastro);
            EditText edtUsuario = (EditText) findViewById(R.id.edtUsuarioCadastro);
            EditText edtSenha = (EditText) findViewById(R.id.edtSenhaCadastro);
            EditText edtConfirmaSenha = (EditText) findViewById(R.id.edtConfirmacaoSenhaCadastro);
            RadioGroup sexo = (RadioGroup) findViewById(R.id.grupoSexo);

            //obtem dados informados pelo usuário
            String nome = edtNome.getText().toString();
            String user = edtUsuario.getText().toString();
            String senha = edtSenha.getText().toString();
            String confSenha = edtConfirmaSenha.getText().toString();
            String nomeSexo = "";

            //obtem informação do radio group
            if (sexo.getCheckedRadioButtonId() == R.id.sexoMasculino)
                nomeSexo = "M";
            else
                nomeSexo = "F";

            //Verifica preenchimento de campos obrigatórios e valida dados
            if (nome.length() == 0 || nome.length() > 50) {
                Toast.makeText(this, "Informe um nome com até 50 caracteres!", Toast.LENGTH_SHORT).show();
                cadOk = false;
            } else if (user.length() == 0 || user.length() > 45) {
                Toast.makeText(this, "Informe um usuário com até 45 caracteres!", Toast.LENGTH_SHORT).show();
                cadOk = false;
            }else if (!SecurityUtil.isAlphanumeric(user)) { //verifica se user não é alfanumérico por questão de segurança
                Toast.makeText(this, "Informe um usuário que contenha apenas letras e/ou números!", Toast.LENGTH_SHORT).show();
                cadOk = false;
            }else if (senha.length() == 0 || senha.length() > 45) {
                Toast.makeText(this, "Informe uma senha com até 45 caracteres!", Toast.LENGTH_SHORT).show();
                cadOk = false;
            }else if (!SecurityUtil.isAlphanumeric(senha)) { //verifica se senha não é alfanumérico por questão de segurança
                Toast.makeText(this, "Informe uma senha que contenha apenas letras e/ou números!", Toast.LENGTH_SHORT).show();
                cadOk = false;
            } else if (confSenha.length() == 0) {
                Toast.makeText(this, "Informe a confirmação da senha!", Toast.LENGTH_SHORT).show();
                cadOk = false;
            } else if (!senha.equals(confSenha)) {
                Toast.makeText(this, "Confirmação de senha inválida!\nDigite-a novamente.", Toast.LENGTH_SHORT).show();
                cadOk = false;
            }

            //Cadastra usuário se dados forem válidos
            if (cadOk) {

                //verifica a existência de internet
                if(InternetUtil.isNetworkAvailable(CadastrarActivity.this)) {

                    //Delega para um async task a tarefa de cadastrar o usuário e sincronizar dados com o servidor
                    ControladoraFachadaSingleton.getInstance().cadastrarUser(user, senha, nome, nomeSexo, "", CadastrarActivity.this);

                }else{
                    InternetUtil.exibeToastFaltaInternet("Verifique as configurações de Internet e tente novamente.",CadastrarActivity.this);
                }

            }

        }catch (Exception e){
            Log.e("CADASTRO", "ERRO: " + e.getMessage());
        }

    }
}
