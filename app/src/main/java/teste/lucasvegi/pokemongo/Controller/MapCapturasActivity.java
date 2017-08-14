package teste.lucasvegi.pokemongo.Controller;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Map;

import teste.lucasvegi.pokemongo.Model.ControladoraFachadaSingleton;
import teste.lucasvegi.pokemongo.Model.Pokemon;
import teste.lucasvegi.pokemongo.Model.PokemonCapturado;
import teste.lucasvegi.pokemongo.R;

public class MapCapturasActivity extends FragmentActivity {

    private GoogleMap map;
    private Pokemon pokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_capturas);

        //configura o mapa
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapaCapturas)).getMap();
        map.setMyLocationEnabled(true);
        map.setBuildingsEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //recupera intent vinda dos detalhes da pokedex
        Intent it = getIntent();
        pokemon = (Pokemon) it.getSerializableExtra("pkmn");

        if(pokemon != null){
            //Exibe todos os pokemons DE UMA ESPÉCIE capturados no mapa
            TextView txtTituloBarra = (TextView) findViewById(R.id.txtTituloMapCapturas);
            txtTituloBarra.setText("Mapa das capturas - " + pokemon.getNome());

            //TODO: RESOLVIDO - procura na lista de pokemons da controladora o pokemon recebido da tela anterior.
            //pokemon = ControladoraFachadaSingleton.getInstance().convertPokemonSerializableToObject(pokemon);
            plotarMarcadoresPokemon(pokemon);
        }else{
            //Exibe todos os pokemons capturados no mapa - acontece quando a navegação vem do mapa principal
            plotarMarcadoresTodosPokemon();
        }
    }

    public void clickVoltar(View v){
        finish();
    }

    public void plotarMarcadoresPokemon(Pokemon pokemon){
        //Plota Marcadores
        try {
            List<PokemonCapturado> listaPc = ControladoraFachadaSingleton.getInstance().getUsuario().getPokemons().get(pokemon);

            for(PokemonCapturado pc : listaPc){
                Log.d("PlotarMarker", "Pokemon: " + pokemon.getNome() + " Lat: " + pc.getLatitude() + " Long: " + pc.getLongitude());

                Marker pokePonto = map.addMarker(new MarkerOptions().
                        icon(BitmapDescriptorFactory.fromResource(pokemon.getIcone())).
                        position(new LatLng(pc.getLatitude(), pc.getLongitude())).
                        title(pokemon.getNome()).snippet(pc.getDtCaptura()));
            }
        }catch (Exception e){
            Log.e("PlotarMarker","ERRO: " + e.getMessage());
        }
    }

    public void plotarMarcadoresTodosPokemon(){
        //Plota Todos os Marcadores
        try {
            //recupera o map de todos os pokemons já capturados pelo treinador
            Map<Pokemon,List<PokemonCapturado>> mapPc = ControladoraFachadaSingleton.getInstance().getUsuario().getPokemons();

            for (Map.Entry<Pokemon,List<PokemonCapturado>> entry : mapPc.entrySet()){
                //varre a lista da todos os pokemons capturados de uma espécie
                for(PokemonCapturado pc : entry.getValue()){
                    Log.d("PlotarMarker", "Pokemon: " + entry.getKey().getNome() + " Lat: " + pc.getLatitude() + " Long: " + pc.getLongitude());

                    Marker pokePonto = map.addMarker(new MarkerOptions().
                            icon(BitmapDescriptorFactory.fromResource(entry.getKey().getIcone())).
                            position(new LatLng(pc.getLatitude(), pc.getLongitude())).
                            title(entry.getKey().getNome()).snippet(pc.getDtCaptura()));
                }
            }
        }catch (Exception e){
            Log.e("PlotarMarker","ERRO: " + e.getMessage());
        }
    }
}
