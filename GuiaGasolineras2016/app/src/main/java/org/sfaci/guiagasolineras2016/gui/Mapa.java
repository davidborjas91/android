package org.sfaci.guiagasolineras2016.gui;

import java.util.ArrayList;

import android.app.Activity;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.sfaci.guiagasolineras2016.R;
import org.sfaci.guiagasolineras2016.base.Gasolinera;
import org.sfaci.guiagasolineras2016.base.Ubicacion;
import org.sfaci.guiagasolineras2016.database.Database;

import org.sfaci.guiagasolineras2016.util.Constantes;

/**
 * Activity que representa el mapa de la aplicación
 * y pinta ubicaciones de Gasolineras o Ubicaciones propias
 * @author Santiago Faci
 * @version curso 2014-2015
 */

public class Mapa extends Activity implements OnClickListener, OnMarkerClickListener {

	private GoogleMap mapa;
	private Database database;
	private Marker marker;

    // Gestor de localizaciones
    private LocationManager locationManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa);
		
		Button btGuardarPosicion = (Button) findViewById(R.id.btGuardarPosicion);
		btGuardarPosicion.setOnClickListener(this);
		Button btVerMiPosicion = (Button) findViewById(R.id.btVerMiPosicion);
		btVerMiPosicion.setOnClickListener(this);
		Button btDistancia = (Button) findViewById(R.id.btDistancia);
		btDistancia.setOnClickListener(this);
		
		// Inicializa el sistema de mapas de Google
        MapsInitializer.initialize(this);
        
        // Obtiene una referencia al objeto que permite "manejar" el mapa
        mapa = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mapa.setOnMarkerClickListener(this);
        
        // Comprueba si hay que mostrar una gasolinera o toda la lista
        String accion = getIntent().getStringExtra("accion");
        if (accion.equals("gasolinera")) {
	        Gasolinera gasolinera = getIntent().getParcelableExtra("gasolinera");
	        if (gasolinera != null)
	        	marcarGasolinera(gasolinera);
        }
        else if (accion.equals("gasolineras")){
        	ArrayList<Gasolinera> gasolineras = getIntent().getParcelableArrayListExtra("gasolineras");
        	if (gasolineras != null) {
        		marcarGasolineras(gasolineras);
        	}
        }
        else if (accion.equals("ubicacion")) {
            Ubicacion ubicacion = getIntent().getParcelableExtra("ubicacion");
            if (ubicacion != null)
                marcarUbicacion(ubicacion);
        }
        else if (accion.equals("ubicaciones")) {
            ArrayList<Ubicacion> ubicaciones = getIntent().getParcelableArrayListExtra("ubicaciones");
            if (ubicaciones != null) {
                marcarUbicaciones(ubicaciones);
            }
        }
        else {
        	// Sólo mostrar mapa, no hacer nada
        }

        // Inicializa el gestor de localizaciones
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Listener para gestionar los cambios de ubicacion o
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        // Activa el botón para localizar mi posición
        mapa.setMyLocationEnabled(true);
        
        database = new Database(this);
	}
	
	/**
	 * Se muestra la Activity
	 */
	@Override
	protected void onStart() {
		
		super.onStart();
	}


	/**
	 * Se oculta la Activity
	 */
	@Override
    protected void onStop() {

        super.onStop();
    }

	/**
	 * Añade la marca de una gasolinera
     * @param gasolinera
	 */
	private void marcarGasolinera(Gasolinera gasolinera) {
		
		// Prepara y añade una nueva marca al mapa
		mapa.addMarker(new MarkerOptions()
			.position(gasolinera.getPosicion())
			.title(gasolinera.getNombre()));
		
		// Posiciona la vista del usuario en el punto que se acaba de agregar
    	CameraUpdate camara =
    			CameraUpdateFactory.newLatLng(gasolinera.getPosicion());
        	 
    	// Coloca la vista del mapa sobre la posición de la gasolinera 
    	// y activa el zoom para verlo de cerca
    	mapa.moveCamera(camara);
    	mapa.animateCamera(CameraUpdateFactory.zoomTo(12.0f), 2000, null);
	}

    /**
     * Añade la marca de una Ubicación
     * @param ubicacion
     */
    private void marcarUbicacion(Ubicacion ubicacion) {

        // Prepara y añade una nueva marca al mapa
        mapa.addMarker(new MarkerOptions()
                .position(ubicacion.getPosicion())
                .title(ubicacion.getNombre()));

        // Posiciona la vista del usuario en el punto que se acaba de agregar
        CameraUpdate camara =
                CameraUpdateFactory.newLatLng(ubicacion.getPosicion());

        // Coloca la vista del mapa sobre la posición de la gasolinera
        // y activa el zoom para verlo de cerca
        mapa.moveCamera(camara);
        mapa.animateCamera(CameraUpdateFactory.zoomTo(12.0f), 2000, null);
    }
	
	/**
	 * Añade las marcas de todas las gasolineras
	 * @param gasolineras
	 */
	private void marcarGasolineras(ArrayList<Gasolinera> gasolineras) {
		
		if (gasolineras.size() > 0) {
			for (Gasolinera gasolinera : gasolineras) {
				
				marcarGasolinera(gasolinera);
			}
		}
		
		// Posiciona la vista del usuario en Zaragoza
    	CameraUpdate camara =
    			CameraUpdateFactory.newLatLng(Constantes.ZARAGOZA);
        	 
    	// Coloca la vista del mapa sobre la posición de la ciudad 
    	// y activa el zoom para verlo de cerca
    	mapa.moveCamera(camara);
    	mapa.animateCamera(CameraUpdateFactory.zoomTo(9.0f), 2000, null);
	}

    /**
     * Añade las marcas de todas las Ubicaciones
     * @param ubicaciones
     */
    private void marcarUbicaciones(ArrayList<Ubicacion> ubicaciones) {

        if (ubicaciones.size() > 0) {
            for (Ubicacion ubicacion : ubicaciones) {

                marcarUbicacion(ubicacion);
            }
        }

        // Posiciona la vista del usuario en Zaragoza
        CameraUpdate camara =
                CameraUpdateFactory.newLatLng(Constantes.ZARAGOZA);

        // Coloca la vista del mapa sobre la posición de la ciudad
        // y activa el zoom para verlo de cerca
        mapa.moveCamera(camara);
        mapa.animateCamera(CameraUpdateFactory.zoomTo(9.0f), 2000, null);
    }

	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.btGuardarPosicion:
			// Guarda la posición actual en la Base de Datos
			Location location = mapa.getMyLocation();
            if (location != null) {
                Ubicacion ubicacion = new Ubicacion();
                ubicacion.setNombre("Desde Mapa");
                ubicacion.setPosicion(new LatLng(location.getLatitude(), location.getLongitude()));
                database.nuevaUbicacion(ubicacion);
            }
            else {
                Toast.makeText(this, R.string.sinubicacion_message, Toast.LENGTH_SHORT).show();
            }
			break;
		case R.id.btVerMiPosicion:
			// TODO
			break;
		case R.id.btDistancia:
			// Calcula la distancia en metros entre la posición actual y la última marca pulsada
			Location yo = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if ((marker != null) && (yo != null)) {
				Location loc = new Location(marker.getTitle());
				loc.setLatitude(marker.getPosition().latitude);
				loc.setLongitude(marker.getPosition().longitude);
				
				Toast.makeText(this, String.valueOf(yo.distanceTo(loc)), Toast.LENGTH_LONG).show();
			}
            else {
                Toast.makeText(this, R.string.distancia_error_message, Toast.LENGTH_LONG).show();
            }
			
			break;
		default:
			break;
		}
	}

	/**
	 * Se pulsa una marca en el mapa
	 */
	@Override
	public boolean onMarkerClick(Marker marker) {
		
		this.marker = marker;
		
		return false;
	}
}
