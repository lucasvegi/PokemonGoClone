package teste.lucasvegi.pokemongo.Model;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teste.lucasvegi.pokemongo.Util.BancoDadosSingleton;
import teste.lucasvegi.pokemongo.Util.ClientServerUtil;
import teste.lucasvegi.pokemongo.Util.RandomUtil;
import teste.lucasvegi.pokemongo.Util.TimeUtil;

/**
 * Created by Lucas on 08/12/2016.
 */
public final class ControladoraFachadaSingleton {
    private Usuario user;
    private Map<String,List<Pokemon>> pokemons;
    private Aparecimento[] aparecimentos = new Aparecimento[10];
    private List<Tipo> tiposPokemon;
    private static ControladoraFachadaSingleton INSTANCE = new ControladoraFachadaSingleton();
    private boolean sorteouLendario = false;

    private ControladoraFachadaSingleton() {
        daoTipo();
        daoPokemons(this);
    }

    private void daoTipo(){
        this.tiposPokemon = new ArrayList<Tipo>();

        Cursor c = BancoDadosSingleton.getInstance().buscar("tipo",new String[]{"idTipo","nome"},"","");

        while(c.moveToNext()){
            int idT = c.getColumnIndex("idTipo");
            int name = c.getColumnIndex("nome");

            Tipo t = new Tipo();
            t.setIdTipo(c.getInt(idT));
            t.setNome(c.getString(name));

            this.tiposPokemon.add(t);
        }

        c.close();

        //TODO: apagar testes de impressão tipo

        //IMPRIME DE TESTE
        for (Tipo tp : tiposPokemon){
            Log.d("TIPOS",tp.getNome());
        }
    }

    private void daoPokemons(ControladoraFachadaSingleton controladorGeral){
        pokemons = new HashMap<String,List<Pokemon>>();

        Cursor c = BancoDadosSingleton.getInstance().buscar("pokemon",new String[]{"idPokemon","nome","categoria","foto","icone"},"","");

        while(c.moveToNext()){
            int idP = c.getColumnIndex("idPokemon");
            int name = c.getColumnIndex("nome");
            int cat = c.getColumnIndex("categoria");
            int foto = c.getColumnIndex("foto");
            int icone = c.getColumnIndex("icone");

            Pokemon p = new Pokemon(c.getInt(idP),c.getString(name),c.getString(cat),c.getInt(foto),c.getInt(icone),controladorGeral);

            //verifica se lista de alguma categoria ainda não existe
            if(pokemons.get(p.getCategoria()) == null)
                pokemons.put(p.getCategoria(),new ArrayList<Pokemon>());

            //adiciona o pokemon na lista da sua categoria
            pokemons.get(p.getCategoria()).add(p);
        }

        c.close();

        //TODO: apagar testes de impressão pokemon

        //IMPRIME POKEMONS COMUNS
        for(Pokemon pokemon : pokemons.get("C")){
            String tipos = "";
            for (Tipo tp :  pokemon.getTipos()){
                tipos += tp.getNome();
                tipos += "/";
            }
            Log.d("POKEMONS", pokemon.getNumero() + " - " + pokemon.getNome() + " - " + pokemon.getCategoria() + " - " + pokemon.getFoto() + " - " + pokemon.getIcone() + " - " + tipos);
        }

        //IMPRIME POKEMONS INCOMUNS
        for(Pokemon pokemon : pokemons.get("I")){
            String tipos = "";
            for (Tipo tp :  pokemon.getTipos()){
                tipos += tp.getNome();
                tipos += "/";
            }
            Log.d("POKEMONS", pokemon.getNumero() + " - " + pokemon.getNome() + " - " + pokemon.getCategoria() + " - " + pokemon.getFoto() + " - " + pokemon.getIcone() + " - " + tipos);
        }

        //IMPRIME POKEMONS RAROS
        for(Pokemon pokemon : pokemons.get("R")){
            String tipos = "";
            for (Tipo tp :  pokemon.getTipos()){
                tipos += tp.getNome();
                tipos += "/";
            }
            Log.d("POKEMONS", pokemon.getNumero() + " - " + pokemon.getNome() + " - " + pokemon.getCategoria() + " - " + pokemon.getFoto() + " - " + pokemon.getIcone() + " - " + tipos);
        }

        //IMPRIME POKEMONS LENDARIOS
        for(Pokemon pokemon : pokemons.get("L")){
            String tipos = "";
            for (Tipo tp :  pokemon.getTipos()){
                tipos += tp.getNome();
                tipos += "/";
            }
            Log.d("POKEMONS", pokemon.getNumero() + " - " + pokemon.getNome() + " - " + pokemon.getCategoria() + " - " + pokemon.getFoto() + " - " + pokemon.getIcone() + " - " + tipos);
        }
    }

    public void daoUsuario(){
        //chamado em temSessão(), login() e cadastro()

        Cursor c = BancoDadosSingleton.getInstance().buscar("usuario",new String[]{"login","senha","nome","sexo","foto","dtCadastro"},"","");

        while(c.moveToNext()){
            int login = c.getColumnIndex("login");
            int pass = c.getColumnIndex("senha");
            int name = c.getColumnIndex("nome");
            int sexo = c.getColumnIndex("sexo");
            int foto = c.getColumnIndex("foto");
            int dtCad = c.getColumnIndex("dtCadastro");

            user = new Usuario(c.getString(login));

            user.setSenha(c.getString(pass));
            user.setNome(c.getString(name));
            user.setSexo(c.getString(sexo));
            user.setFoto(c.getString(foto)); //IMPLEMENTAR RETIRAR FOTO DE USUÁRIO NO CADASTRO
            user.setDtCadastro(c.getString(dtCad));
        }

        c.close();

    }

    public static ControladoraFachadaSingleton getInstance(){
        return INSTANCE;
    }

    public Usuario getUsuario(){
        return this.user;
    }

    public List<Pokemon> getPokemons(){
        //extrai do MAP todos os valores pokemon e junta em uma lista ordenada para retornar.
        List<Pokemon> pkmn = new ArrayList<Pokemon>();

        for (Map.Entry<String, List<Pokemon>> entry : pokemons.entrySet()){
            //add listas de pokemon no final da lista a ser retornada
            pkmn.addAll(entry.getValue());
        }

        //ordena a lista a ser retornada baseado no número do pokemon
        Collections.sort(pkmn, new Comparator<Pokemon>() {
            @Override
            public int compare(Pokemon pk2, Pokemon pk1) {
                if(pk1.getNumero() > pk2.getNumero())
                    return -1;
                else if(pk1.getNumero() < pk2.getNumero())
                    return +1;
                return 0;
            }
        });

        //TODO: apagar testes de impressão lista pokemon
        for(Pokemon p : pkmn){
            Log.d("LISTA_PKMN", "Pokemon: " + p.getNome());
        }

        return pkmn;
    }

    public Aparecimento[] getAparecimentos(){
        return this.aparecimentos;
    }

    protected List<Tipo> getTipos(){
        return tiposPokemon;
    }

    public void sorteiaAparecimentos(double LatMin, double LatMax, double LongMin, double LongMax){

        int tamComum = pokemons.get("C").size();
        int tamIncomum = pokemons.get("I").size();
        int tamRaro = pokemons.get("R").size();
        int tamLendario = pokemons.get("L").size();

        Log.d("SORTEIO","C: " + tamComum + " I: "+ tamIncomum + " R: "+ tamRaro + " L: " + tamLendario);

        int contAparecimentos = 0;
        int totalComuns = 0; //valor a ser definido no sorteio de lendário

        int min = 0;
        int max;

        //obtem hora atual
        Map<String,String> tempo = TimeUtil.getHoraMinutoSegundoDiaMesAno();
        Log.d("TEMPO",tempo.get("hora") +":"+tempo.get("minuto")+":"+tempo.get("segundo")+" - "+tempo.get("dia")+"/"+tempo.get("mes")+"/"+tempo.get("ano")+" "+tempo.get("timezone"));

        //obtem valores a serem usado no critério de lendários
        int numIntSorteado = RandomUtil.randomIntInRange(1,101);
        int numIntSorteado2 = RandomUtil.randomIntInRange(1,101);
        int somaMinSegAtual = (Integer.parseInt(tempo.get("minuto")) + Integer.parseInt(tempo.get("segundo")));

        Log.d("SORTEIO","NumInt: " + numIntSorteado + " NumInt2: " + numIntSorteado2 + " SomaMinSeg: " + somaMinSegAtual);

        //define se sorteia lendário
        if(!sorteouLendario && numIntSorteado % 2 == 0 && numIntSorteado2 % 2 == 0 && somaMinSegAtual % 2 != 0){
            sorteouLendario = true;
            totalComuns = 5;

            //sorteia pokemons lendário
            for(int i = 0; i < 1; i++){
                max = tamLendario;
                int sorteio = RandomUtil.randomIntInRange(min,max);

                Aparecimento ap = new Aparecimento();
                ap.setLatitude(RandomUtil.randomDoubleInRange(LatMin, LatMax));
                ap.setLongitude(RandomUtil.randomDoubleInRange(LongMin, LongMax));
                ap.setPokemon(pokemons.get("L").get(sorteio));

                this.aparecimentos[contAparecimentos] = ap;
                Log.d("SORTEIO","LENDÁRIO: " + aparecimentos[contAparecimentos].getPokemon().getNome());

                contAparecimentos++;
            }
        }
        else{
            sorteouLendario = false;
            totalComuns = 6;
        }

        //sorteia pokemons comuns
        for(int i = 0; i < totalComuns; i++){
            max = tamComum;
            int sorteio = RandomUtil.randomIntInRange(min,max);

            Aparecimento ap = new Aparecimento();
            ap.setLatitude(RandomUtil.randomDoubleInRange(LatMin, LatMax));
            ap.setLongitude(RandomUtil.randomDoubleInRange(LongMin, LongMax));
            ap.setPokemon(pokemons.get("C").get(sorteio));

            this.aparecimentos[contAparecimentos] = ap;
            Log.d("SORTEIO","COMUM: " + aparecimentos[contAparecimentos].getPokemon().getNome());

            contAparecimentos++;
        }

        //sorteia pokemons incomuns
        for(int i = 0; i < 3; i++){
            max = tamIncomum;
            int sorteio = RandomUtil.randomIntInRange(min,max);

            Aparecimento ap = new Aparecimento();
            ap.setLatitude(RandomUtil.randomDoubleInRange(LatMin, LatMax));
            ap.setLongitude(RandomUtil.randomDoubleInRange(LongMin, LongMax));
            ap.setPokemon(pokemons.get("I").get(sorteio));

            this.aparecimentos[contAparecimentos] = ap;
            Log.d("SORTEIO","INCOMUM: " + aparecimentos[contAparecimentos].getPokemon().getNome());

            contAparecimentos++;
        }

        //sorteia pokemons raros
        for(int i = 0; i < 1; i++){
            max = tamRaro;
            int sorteio = RandomUtil.randomIntInRange(min,max);

            Aparecimento ap = new Aparecimento();
            ap.setLatitude(RandomUtil.randomDoubleInRange(LatMin, LatMax));
            ap.setLongitude(RandomUtil.randomDoubleInRange(LongMin, LongMax));
            ap.setPokemon(pokemons.get("R").get(sorteio));

            this.aparecimentos[contAparecimentos] = ap;
            Log.d("SORTEIO","RARO: " + aparecimentos[contAparecimentos].getPokemon().getNome());

            contAparecimentos++;
        }
    }

    public void loginUser(String login, String senha, Context contextTela){

        Cursor c = BancoDadosSingleton.getInstance().buscar("usuario",
                                                                new String[]{"login","senha","temSessao"},
                                                                "login = '"+login+"' AND senha = '"+senha+"'",
                                                                "");

        //significa que o usuário que está logando é o mesmo que fez login anteriormente
        if(c.getCount() == 1){
            // O método sincronizarPokemonsCapturadosClientServer() chama internamente o método daoUsuario()

            //abri a sessão do usuário
            ContentValues valores = new ContentValues();
            valores.put("temSessao","SIM");
            BancoDadosSingleton.getInstance().atualizar("usuario",valores,"login = '"+login+"'");

            //usuário auxiliar no processo
            Usuario user = new Usuario();

            //obtem informação do banco para preencher o usuário
            if(c.moveToNext()){
                int indexLogin = c.getColumnIndex("login");
                user.setLogin(c.getString(indexLogin));
            }
            c.close();

            //operação Login_sem_sessão_ativa direciona a execução do async task
            sincronizarPokemonsCapturadosClientServer(ClientServerUtil.LOGIN_SEM_SESSAO_ATIVA, "Aguarde a sincronização com o servidor...", user, contextTela);

        }else{
            //EVENTO: chega aqui apenas se ao fazer login o usuário não existir no banco local
            // O método sincronizarPokemonsCapturadosClientServer() chama internamente o método daoUsuario()

            c.close();

            //usuário auxiliar no processo
            Usuario user = new Usuario();
            user.setLogin(login);
            user.setSenha(senha);

            //operação Login_sem_sessão direciona a execução do async task
            sincronizarPokemonsCapturadosClientServer(ClientServerUtil.LOGIN_SEM_SESSAO, "Aguarde a autenticação com o servidor...", user, contextTela);

        }
    }

    protected void sincronizarPokemonsCapturadosClientServer(String operacao, String msgProgressDialog, Usuario usuario, Context contextoTela){
        //método chamado pelos métodos loginUser() e temSessão()

        //Sobe informações do usuário para o server durante o login
        ClientServerUtil clientServerUtil = new ClientServerUtil(operacao,
                msgProgressDialog,
                usuario,
                contextoTela);

        //Executa async task para verificação da necessidade de sincronizar e fazer a sincronização se necessário
        clientServerUtil.execute();
    }

    public boolean logoutUser(){

        //fecha a sessão do usuário
        ContentValues valores = new ContentValues();
        valores.put("temSessao","NAO");

        BancoDadosSingleton.getInstance().atualizar("usuario",valores,"login = '"+this.user.getLogin()+"'");
        return true;
    }

    public void cadastrarUser(String login, String senha, String nome, String sexo, String foto, Context contextoTela) {

        Map<String, String> timeStamp = TimeUtil.getHoraMinutoSegundoDiaMesAno();
        String dtCadastro = timeStamp.get("dia") + "/" + timeStamp.get("mes") + "/" + timeStamp.get("ano") + " " + timeStamp.get("hora") + ":" + timeStamp.get("minuto") + ":" + timeStamp.get("segundo");

        ContentValues valores = new ContentValues();
        valores.put("login", login);
        valores.put("senha", senha);
        valores.put("nome", nome);
        valores.put("sexo", sexo);
        valores.put("foto", foto);
        valores.put("dtCadastro", dtCadastro);
        valores.put("temSessao", "SIM");

        //usado para enviar dados para o servidor
        Usuario usuarioAux = new Usuario();
        usuarioAux.setLogin(login);
        usuarioAux.setSenha(senha);
        usuarioAux.setNome(nome);
        usuarioAux.setSexo(sexo);
        usuarioAux.setDtCadastro(dtCadastro);

        //Sobe informações de cadastro para o servidor na Web
        ClientServerUtil clientServerUtil = new ClientServerUtil(ClientServerUtil.CADASTRAR,
                "Enviando dados para o servidor...",
                usuarioAux,
                valores,
                contextoTela);

        //Cadastra na Nuvem e no banco local caso obtenha sucesso no envio para o servidor
        clientServerUtil.execute();
    }


    public boolean temSessao(Context contextTela){

        Cursor sessao = BancoDadosSingleton.getInstance().buscar("usuario",new String[]{"login","temSessao"},"temSessao = 'SIM'","");

        if(sessao.getCount() == 1){
            // O método sincronizarPokemonsCapturadosClientServer() chama internamente o método daoUsuario()

            //usuário auxiliar no processo
            Usuario user = new Usuario();

            //obtem informação do banco para preencher o usuário
            if(sessao.moveToNext()){
                int indexLogin = sessao.getColumnIndex("login");
                user.setLogin(sessao.getString(indexLogin));
            }
            sessao.close();

            //operação Login_com_sessão direciona a execução do async task
            sincronizarPokemonsCapturadosClientServer(ClientServerUtil.LOGIN_COM_SESSAO,"Aguarde a sincronização com o servidor...",user, contextTela);

            return true;
        }else{
            sessao.close();
            return false;
        }
    }


    /*public Pokemon convertPokemonSerializableToObject(Pokemon pkmn) {
        //obtem lista de pokemons da controladora geral
        List<Pokemon> listPkmn = this.getPokemons();
        Pokemon pkmnAux = null;

        //procura na lista de pokemons da controladora o pokemon parâmetro.
        for (Pokemon pokemon : listPkmn) {
            if (pokemon.getNumero() == pkmn.getNumero()) {
                pkmnAux = pokemon;
                break;
            }
        }

        return pkmnAux;
    }*/
}
