package teste.lucasvegi.pokemongo.Util.JSON;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import teste.lucasvegi.pokemongo.Util.ClientServerUtil;

public class ServerFunctions {
	
	private JSONParser jsonParser;

	private static String MAPA = "mapa";
	
	private static String URL = "http://pokemongo.fornut.com.br/android_index.php";
	
	public ServerFunctions(){
		jsonParser = new JSONParser();
	}


	public JSONObject capturar(String login, int idPokemon, String dtCaptura, double latitude, double longitude, int timeConnection, int timeSocket){

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("tag", ClientServerUtil.CAPTURAR));
		params.add(new BasicNameValuePair("login", login));
		params.add(new BasicNameValuePair("idPokemon", idPokemon+""));
		params.add(new BasicNameValuePair("dtCaptura", dtCaptura));
		params.add(new BasicNameValuePair("latitude", latitude+""));
		params.add(new BasicNameValuePair("longitude", longitude+""));

		JSONObject json = jsonParser.getJSONFromUrl(URL, params, timeConnection, timeSocket);
		return json;
	}

	public JSONObject verificarLoginRepetido(String login, int timeConnection, int timeSocket){

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("tag", ClientServerUtil.VERIFICAR_LOGIN_REPETIDO));
		params.add(new BasicNameValuePair("login", login));

		JSONObject json = jsonParser.getJSONFromUrl(URL, params, timeConnection, timeSocket);
		return json;
	}

	public JSONObject cadastrarUsuario(String login, String senha, String nome, String sexo, String dtCadastro, int timeConnection, int timeSocket){

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("tag", ClientServerUtil.CADASTRAR));
		params.add(new BasicNameValuePair("login", login));
		params.add(new BasicNameValuePair("senha", senha));
		params.add(new BasicNameValuePair("nome", nome));
		params.add(new BasicNameValuePair("sexo", sexo));
		params.add(new BasicNameValuePair("dtCadastro", dtCadastro));

		JSONObject json = jsonParser.getJSONFromUrl(URL, params, timeConnection, timeSocket);
		return json;
	}

	public JSONObject contarNumeroDeCapturas(String login, int timeConnection, int timeSocket){

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("tag", ClientServerUtil.CONTAR_NUMERO_DE_CAPTURAS));
		params.add(new BasicNameValuePair("login", login));

		JSONObject json = jsonParser.getJSONFromUrl(URL, params, timeConnection, timeSocket);
		return json;
	}

	public JSONObject getPokemonsDoServer(String login, int timeConnection, int timeSocket){

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("tag", ClientServerUtil.OBTER_POKEMONS_DO_SERVER));
		params.add(new BasicNameValuePair("login", login));

		JSONObject json = jsonParser.getJSONFromUrl(URL, params, timeConnection, timeSocket);
		return json;
	}

	public JSONObject validaUsuario(String login, String senha, int timeConnection, int timeSocket){

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("tag", ClientServerUtil.VALIDA_USUARIO_DURANTE_LOGIN));
		params.add(new BasicNameValuePair("login", login));
		params.add(new BasicNameValuePair("senha", senha));

		JSONObject json = jsonParser.getJSONFromUrl(URL, params, timeConnection, timeSocket);
		return json;
	}

	
	/*
	public JSONObject Pontos(int timeConnection, int timeSocket){

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		params.add(new BasicNameValuePair("tag", MAPA));
	    
		JSONObject json = jsonParser.getJSONFromUrl(URL, params, timeConnection, timeSocket);
		return json;
	}*/

}
