package teste.lucasvegi.pokemongo.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import teste.lucasvegi.pokemongo.Controller.MapActivity;
import teste.lucasvegi.pokemongo.Model.Aparecimento;
import teste.lucasvegi.pokemongo.Model.ControladoraFachadaSingleton;
import teste.lucasvegi.pokemongo.Model.Usuario;
import teste.lucasvegi.pokemongo.Util.JSON.ServerFunctions;

/**
 * Created by Lucas on 02/01/2017.
 */
public class ClientServerUtil extends AsyncTask<String, String, String> {
    private ProgressDialog pDialog;
    private Context contextoTela;
    private String operacao;
    private String msgPD;
    private ContentValues valores;

    //itens para as operações
    private Aparecimento aparecimento;
    private String dtCaptura;
    private Usuario usuario;

    //operações básicas
    public static final String LOGIN_COM_SESSAO = "login_com_sessao";
    public static final String LOGIN_SEM_SESSAO = "login_sem_sessao";
    public static final String LOGIN_SEM_SESSAO_ATIVA = "login_sem_sessao_ativa";
    public static final String CONTAR_NUMERO_DE_CAPTURAS = "contar_numero_de_capturas";
    public static final String OBTER_POKEMONS_DO_SERVER = "obter_pokemons_do_server";
    public static final String VALIDA_USUARIO_DURANTE_LOGIN = "valida_usuario_durante_login";
    public static final String CADASTRAR = "cadastrar";
    public static final String VERIFICAR_LOGIN_REPETIDO = "verifica_login_repetido";
    public static final String CAPTURAR = "capturar";

    //Respostas padrão doInBackground
    private static final String RespostaCapturouComSucesso = "capturou";
    private static final String RespostaNaoCapturou = "naoCapturou";
    private static final String RespostaLoginDisponivel = "loginDisponivel";
    private static final String RespostaLoginNaoDisponivel = "loginIndisponivel";
    private static final String RespostaCadastrouComSucesso = "cadastrou";
    private static final String RespostaNaoCadastrou = "naoCadastrou";
    private String RespostaAposExecucao = "";

    private Handler handler = new Handler();
    private ServerFunctions serverFunctions;
    public JSONArray pokemons = null;

    private int TIMEOUT_CONNECTION = 20000;
    private int TIMEOUT_SOCKET = 20000;

    //construtor utiizado para sincronizar dados do usuário com o servidor durante o login
    public ClientServerUtil(String operacao, String msgProgressDialog, Usuario usuario, Context contextTela){
        this.operacao = operacao;
        this.contextoTela = contextTela;
        this.usuario = usuario;
        this.msgPD = msgProgressDialog;
    }

    //construtor utilizado para cadastrar usuário
    public ClientServerUtil(String operacao, String msgProgressDialog, Usuario usuario, ContentValues values, Context contextTela){
        this.operacao = operacao;
        this.contextoTela = contextTela;
        this.msgPD = msgProgressDialog;
        this.usuario = usuario;
        this.valores = values;
    }

    //construtor usado apenas quando estiver capturando pokemon
    public ClientServerUtil(String operacao, String msgProgressDialog, Usuario usuario, Aparecimento aparecimento, String dtCaptura, Context contextTela){
        this.operacao = operacao;
        this.contextoTela = contextTela;
        this.msgPD = msgProgressDialog;
        this.usuario = usuario;
        this.aparecimento = aparecimento;
        this.dtCaptura = dtCaptura;
    }

    @Override
    protected void onPreExecute() {
        try {
            super.onPreExecute();
            pDialog = new ProgressDialog(contextoTela);
            pDialog.setMessage(msgPD);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

            Log.i("ASYNC_TASK", "PRE-EXECUÇÃO ASYNC TASK");
        }catch (Exception e){
            Log.e("ASYNC_TASK", "ERRO NA PRE-EXECUÇÃO ASYNC TASK: " + e.getMessage());
        }

    }

    protected String doInBackground(String... args) {
        try {
            serverFunctions = new ServerFunctions();

            if(operacao.equals(CAPTURAR)){

                RespostaAposExecucao = capturarAsync();
                return RespostaAposExecucao;

            } else if(operacao.equals(CADASTRAR)){

                if(verificaDisponibilidadeLoginAsync().equals(RespostaLoginDisponivel)){
                    RespostaAposExecucao = cadastrarUsuarioAsync();
                    return RespostaAposExecucao;
                }else{
                    RespostaAposExecucao = RespostaLoginNaoDisponivel;
                    return RespostaAposExecucao;
                }

            }else if(operacao.equals(LOGIN_COM_SESSAO) ){

                loginComSessao();

            }else if(operacao.equals(LOGIN_SEM_SESSAO_ATIVA) ){

                loginSemSessaoAtiva();

            }
            else if(operacao.equals(LOGIN_SEM_SESSAO) ){

                loginSemSessao();
            }
            return "";
        }catch (Exception e) {
            Log.e("ASYNC_TASK", "Problemas com a comunicação com o servidor: " + e.getMessage());

            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(contextoTela, "Servidor indisponível no momento...", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return "ERRO";
    }

    @Override
    protected void onCancelled() {
        try {
            super.onCancelled();
            pDialog.dismiss();
            Log.i("ASYNC_TASK", "CANCELOU ASYNC TASK");
        }catch (Exception e){
            Log.e("ASYNC_TASK", "ERRO AO CANCELAR ASYNC TASK: " + e.getMessage());
        }

    }

    protected void onPostExecute(String file_url) {
        try{
            if(operacao.equals(CAPTURAR)){
                super.onPostExecute(file_url);
                Log.i("ASYNC_TASK", "Após a execução: " + CAPTURAR);

                capturarOnPostExecute();

            }else if(operacao.equals(CADASTRAR)){
                super.onPostExecute(file_url);
                Log.i("ASYNC_TASK", "Após a execução: " + CADASTRAR);

                cadastrarUsuarioOnPostExecute();
            }else {
                pDialog.dismiss();
            }
        }catch (Exception e){
            Log.e("ASYNC_TASK", "ERRO Após a execução: " + e.getMessage());
        }
    }

    private String capturarAsync(){
        JSONObject json = serverFunctions.capturar(usuario.getLogin(), aparecimento.getPokemon().getNumero(), dtCaptura,
                aparecimento.getLatitude(), aparecimento.getLongitude(), TIMEOUT_CONNECTION, TIMEOUT_SOCKET);

        try {
            String res = json.getString("success");
            if(Integer.parseInt(res) == 1){
                //enviou captura com sucesso para o servidor
                Log.i("ASYNC_TASK", "CAPTUROU");
                return RespostaCapturouComSucesso;

            }else{
                Log.e("ASYNC_TASK", "Problema na captura");
                return RespostaNaoCapturou;
            }
        }catch (Exception e){
            Log.e("ASYNC_TASK", "ERRO NA CAPTURA: " + e.getMessage());
            return RespostaNaoCapturou;
        }
    }

    private void capturarOnPostExecute(){
        pDialog.dismiss();

        //fecha a tela do módulo de captura quando o pokémon chega ao servidor
        ((Activity) contextoTela).finish();
    }

    private String verificaDisponibilidadeLoginAsync(){
        JSONObject jsonVerifica = serverFunctions.verificarLoginRepetido(usuario.getLogin(), TIMEOUT_CONNECTION, TIMEOUT_SOCKET);

        try {
            String resVerifica = jsonVerifica.getString("success");
            if (Integer.parseInt(resVerifica) == 1) {
                Log.i("ASYNC_TASK", "Login disponivel");
                return RespostaLoginDisponivel;
            } else {
                //avisa que login não está disponível
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(contextoTela, "Usuário já existente. Escolha outro...", Toast.LENGTH_SHORT).show();
                        Log.i("ASYNC_TASK", "Login repetido...");
                    }
                });

                return RespostaLoginNaoDisponivel;
            }
        }catch (Exception e){
            Log.e("ASYNC_TASK", "ERRO NA VERIFICAÇÃO DE DISPONIBILIDADE DO LOGIN: " + e.getMessage());
            return RespostaLoginNaoDisponivel;
        }
    }

    private String cadastrarUsuarioAsync(){
        JSONObject jsonCadastro = serverFunctions.cadastrarUsuario(usuario.getLogin(), usuario.getSenha(), usuario.getNome(), usuario.getSexo(),
                usuario.getDtCadastro(), TIMEOUT_CONNECTION, TIMEOUT_SOCKET);

        try {
            String resCadastro = jsonCadastro.getString("success");

            if(Integer.parseInt(resCadastro) == 1) {
                //cadastrado com sucesso

                Log.i("ASYNC_TASK", "CADASTRADO COM SUCESSO");
                return RespostaCadastrouComSucesso;

            }else{
                //avisa que não foi cadastrado
                Log.e("ASYNC_TASK", "Problema no cadastro do usuário");
                return RespostaNaoCadastrou;
            }
        }catch (Exception e){
            Log.e("ASYNC_TASK", "ERRO NA CADASTRO DE USUÁRIO: " + e.getMessage());
            return RespostaNaoCadastrou;
        }
    }

    private void cadastrarUsuarioOnPostExecute(){
        if(RespostaAposExecucao.equals(ClientServerUtil.RespostaCadastrouComSucesso)) {
            //limpa tabelas locais de pokemons capturados e de usuário
            BancoDadosSingleton.getInstance().deletar("pokemonusuario", "");
            BancoDadosSingleton.getInstance().deletar("usuario", "");

            //insere o usuário no banco local
            BancoDadosSingleton.getInstance().inserir("usuario", valores);

            //chama apenas após cadastrar usuário e ENVIAR CADASTRO PARA O SERVIDOR
            ControladoraFachadaSingleton.getInstance().daoUsuario();

            Log.i("ASYNC_TASK", "Comparação Controladora....Sucesso");
            pDialog.dismiss();

            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(contextoTela, "Usuário cadastrado!", Toast.LENGTH_SHORT).show();
                }
            });

            Intent it = new Intent(contextoTela, MapActivity.class);
            ((Activity) contextoTela).startActivity(it);
            ((Activity) contextoTela).finish();
        }

        //verifica se houve problema ao tentar cadastrar
        if(RespostaAposExecucao.equals(ClientServerUtil.RespostaNaoCadastrou)) {
            handler.post(new Runnable() {
                public void run() {
                    Log.e("CADASTRO", "ERRO: Problemas ao tentar cadastrar o Usuário. Tente novamente!");
                    Toast.makeText(contextoTela, "Problemas ao tentar cadastrar o Usuário.\nTente novamente!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        //feito para evitar o dismiss repetido quando o resultado do cadastro for de sucesso
        if(!RespostaAposExecucao.equals(ClientServerUtil.RespostaCadastrouComSucesso))
            pDialog.dismiss();
    }

    private boolean ehNecessarioSincronizarPokemonsComServer(){
        JSONObject jsonNumCapturas  = serverFunctions.contarNumeroDeCapturas(usuario.getLogin(), TIMEOUT_CONNECTION, TIMEOUT_SOCKET);
        try {
            String res = jsonNumCapturas.getString("success");
            if(Integer.parseInt(res) == 1){
                Log.i("ASYNC_TASK", "OBTEVE NUMERO DE CAPTURAS NO SERVIDOR");

                int totalCapturasNoServer = Integer.parseInt(jsonNumCapturas.getString("quantidade"));

                Cursor cursorCapturasLocais = BancoDadosSingleton.getInstance().buscar("pokemonusuario",
                                                                                        new String[]{"idPokemon"},
                                                                                        "login = '"+usuario.getLogin()+"'",
                                                                                        "");

                //verifica se o numero de capturas locais e do server são diferentes
                if(totalCapturasNoServer != cursorCapturasLocais.getCount()){
                    Log.i("ASYNC_TASK", "SERÁ NECESSÁRIO SINCRONIZAR POKEMONS COM O SERVIDOR");
                    cursorCapturasLocais.close();
                    return true;
                }else{
                    Log.i("ASYNC_TASK", "NÃO SERÁ NECESSÁRIO SINCRONIZAR POKEMONS COM O SERVIDOR");
                    cursorCapturasLocais.close();
                    return false;
                }

            }else{
                handler.post(new Runnable() {
                    public void run() {
                        Log.e("ASYNC_TASK", "Problema ao tentar obter o número de capturas no servidor");
                        Toast.makeText(contextoTela, "Problemas de comunicação com o servidor.\nTente novamente mais tarde!", Toast.LENGTH_SHORT).show();
                    }
                });

                ((Activity)contextoTela).finish(); //fecha a aplicação
                return false;
            }
        }catch (Exception e){
            Log.e("ASYNC_TASK", "ERRO AO OBTER O NÚMERO DE CAPTURAS NO SERVIDOR: " + e.getMessage());

            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(contextoTela, "Problemas de comunicação com o servidor.\nTente novamente mais tarde!", Toast.LENGTH_SHORT).show();
                }
            });

            ((Activity)contextoTela).finish(); //fecha a aplicação
            return false;
        }
    }

    private boolean getPokemonsDoServer(){
        JSONObject jsonPokemons  = serverFunctions.getPokemonsDoServer(usuario.getLogin(), TIMEOUT_CONNECTION, TIMEOUT_SOCKET);
        try {
            String res = jsonPokemons.getString("success");
            if(Integer.parseInt(res) == 1){
                Log.i("ASYNC_TASK", "OBTEVE POKEMONS CAPTURADOS NO SERVIDOR");

                //apagar a base de pokemons do banco local
                int totalApagado = BancoDadosSingleton.getInstance().deletar("pokemonusuario","");
                Log.i("ASYNC_TASK", "Total de pokemons apagados do banco local: " + totalApagado);

                //pega o array de pokemons vindos do servidor
                pokemons = jsonPokemons.getJSONArray("pokemons");

                //usado para inserir valores no banco
                ContentValues valores = new ContentValues();

                //varre esse array de pokemons
                for(int i = 0; i < pokemons.length(); i++){
                    JSONObject pkmn = pokemons.getJSONObject(i);
                    String login        = pkmn.getString("login");
                    String idPokemon    = pkmn.getString("idPokemon");
                    String dtCaptura    = pkmn.getString("dtCaptura");
                    String latitude     = pkmn.getString("latitude");
                    String longitude    = pkmn.getString("longitude");

                    //prepara valores para serem persistidos no banco
                    valores.put("login",login);
                    valores.put("idPokemon",idPokemon);
                    valores.put("dtCaptura",dtCaptura);
                    valores.put("latitude",latitude);
                    valores.put("longitude",longitude);
                    Log.i("ASYNC_TASK", "Inserindo pokemon vindo do servidor no banco local - POKEMON: " + idPokemon + " Data: " + dtCaptura);

                    //insere pokemon vindo do servidor no banco de dados local
                    BancoDadosSingleton.getInstance().inserir("pokemonusuario",valores);

                    //limpa o ContentValues para receber dados de um novo pokemon
                    valores.clear();
                }
                return true;

            }else{
                Log.e("ASYNC_TASK", "Problema ao tentar obter POKEMONS CAPTURADOS NO SERVIDOR");
                return false;
            }
        }catch (Exception e){
            Log.e("ASYNC_TASK", "Problema ao tentar obter POKEMONS CAPTURADOS NO SERVIDOR: " + e.getMessage());
            return false;
        }
    }

    private boolean usuarioExiste(){
        //ALGORITMO: chega aqui apenas se ao fazer login o usuário não existir no banco local

        //1 - verificar se ele existe na nuvem
        //2 - se existir na nuvem, buscar os dados do usuario no server
        //2.1 - apagar os pokemon capturados localmente
        //2.2 - apagar o usuário existente localmente
        //2.3 - Salvar no banco local os dados do usuário vindos do server -

        JSONObject jsonUsuario  = serverFunctions.validaUsuario(usuario.getLogin(), usuario.getSenha(), TIMEOUT_CONNECTION, TIMEOUT_SOCKET);
        try {
            String res = jsonUsuario.getString("success");
            if(Integer.parseInt(res) == 1){
                Log.i("ASYNC_TASK", "O USUÁRIO EXISTE NO SERVIDOR");

                //apagar a base de pokemons do banco local
                int totalApagado = BancoDadosSingleton.getInstance().deletar("pokemonusuario","");
                Log.i("ASYNC_TASK", "Total de pokemons apagados do banco local: " + totalApagado);

                //apagar a base de usuários do banco local
                totalApagado = BancoDadosSingleton.getInstance().deletar("usuario","");
                Log.i("ASYNC_TASK", "Total de usuarios apagados do banco local: " + totalApagado);

                //usado para inserir valores no banco
                ContentValues valores = new ContentValues();

                //prepara valores para serem persistidos no banco
                valores.put("login",jsonUsuario.getString("login"));
                valores.put("senha",jsonUsuario.getString("senha"));
                valores.put("nome",jsonUsuario.getString("nome"));
                valores.put("sexo",jsonUsuario.getString("sexo"));
                valores.put("foto","");
                valores.put("dtCadastro",jsonUsuario.getString("dtCadastro"));
                valores.put("temSessao","SIM");
                Log.i("ASYNC_TASK", "Inserindo usuário vindo do servidor no banco local - User: " + jsonUsuario.getString("login") + " Nome: " + jsonUsuario.getString("nome"));

                //insere usuário vindo do servidor no banco de dados local
                BancoDadosSingleton.getInstance().inserir("usuario",valores);

                //limpa o ContentValues para receber dados de um novo pokemon
                valores.clear();

                return true;

            }else{
                Log.i("ASYNC_TASK", "Usuário e/ou senha inválido(s)!");
                return false;
            }
        }catch (Exception e){
            Log.e("ASYNC_TASK", "Problema ao tentar obter dados do usuário NO SERVIDOR: " + e.getMessage());
            return false;
        }
    }

    private void serverSync(String nomeOperacao){
        //ALGORITMO: chega aqui pelas chamadas dos metodos temSessao() e loginUser() da ControladoraFachadaSingleton

        //1 - verificar se o numero de pokemon capturados localmente é diferente do servidor
        //2 - se for diferente,
        //2.1 - buscar todos os pokemon capturados na nuvem.
        //2.2 - apagar o banco local de pokemons capturados e salvar nele os pokemon vindos do server
        //3 - chamar método daoUsuario()

        try {
            if(ehNecessarioSincronizarPokemonsComServer()){
                if(getPokemonsDoServer()){
                    //preeche o objeto usuário e os objetos pokemons capturados com dados atualizados do servidor
                    ControladoraFachadaSingleton.getInstance().daoUsuario();

                    //navega até a tela principal com o login realizado com sucesso
                    Intent i = new Intent(contextoTela, MapActivity.class);
                    ((Activity)contextoTela).startActivity(i);
                    ((Activity)contextoTela).finish();

                }else{
                    handler.post(new Runnable() {
                        public void run() {
                            Log.e("ASYNC_TASK", "ERRO: Problemas ao tentar sincronizar pokemons com o servidor");
                            Toast.makeText(contextoTela, "Problemas de comunicação com o servidor.\nTente novamente mais tarde!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    ((Activity)contextoTela).finish();
                }
            }else {
                //preeche o objeto usuário e os objetos pokemons capturados com dados atualizados do servidor
                ControladoraFachadaSingleton.getInstance().daoUsuario();

                //navega até a tela principal com o login realizado com sucesso
                Intent i = new Intent(contextoTela, MapActivity.class);
                ((Activity)contextoTela).startActivity(i);
                ((Activity)contextoTela).finish();
            }
        }catch (Exception e){
            Log.e("ASYNC_TASK", "ERRO: Problemas ao tentar fazer " + nomeOperacao + ": " + e.getMessage());
        }
    }

    private void loginComSessao(){
        try {
            serverSync("loginComSessao()");
        }catch (Exception e){
            Log.e("ASYNC_TASK", "ERRO: Problemas ao tentar fazer loginComSessao: " + e.getMessage());
        }
    }

    private void loginSemSessaoAtiva(){
        try {
            serverSync("loginSemSessaoAtiva()");
        }catch (Exception e){
            Log.e("ASYNC_TASK", "ERRO: Problemas ao tentar fazer loginSemSessaoAtiva: " + e.getMessage());
        }
    }

    private void loginSemSessao(){
        //ALGORITMO: chega aqui apenas se ao fazer login o usuário não existir no banco local

        //1 - chamar o método serverSync() - Esse método chama internamente o método daoUsuario()
        //2 - se ele NÃO existir na nuvem, avisar o usuário com um Toast

        try {
            if(usuarioExiste()){
                serverSync("loginSemSessao");
            }else{
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(contextoTela, "Usuário e/ou senha inválido(s)!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }catch (Exception e){
            Log.e("ASYNC_TASK", "ERRO: Problemas ao tentar fazer loginSemSessao: " + e.getMessage());
        }
    }



}
