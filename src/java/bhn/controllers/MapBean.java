package bhn.controllers;  
  
import java.io.Serializable;  
  
import javax.faces.application.FacesMessage;  
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;  
  
import org.primefaces.event.map.OverlaySelectEvent;  
import org.primefaces.model.map.DefaultMapModel;  
import org.primefaces.model.map.LatLng;  
import org.primefaces.model.map.MapModel;  
import org.primefaces.model.map.Marker;  
  
@ManagedBean(name="mapBean")
@ViewScoped
public class MapBean implements Serializable {  
  
    private static Double longitud;
    private static Double latitud;
    
    private static MapModel simpleModel;  
  
    private Marker marker;
    
    public static void setLatitudAndLongitud(Double latitud, Double longitud){
        MapBean.latitud = latitud;
        MapBean.longitud = longitud;
    }
    

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }
    
    

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public void setSimpleModel(MapModel simpleModel) {
        this.simpleModel = simpleModel;
    }
    
  
    public MapBean() {
        simpleModel = new DefaultMapModel();        
        LatLng coord1 = new LatLng(18.533826, -69.842723);
        LatLng coord2 = new LatLng(18.54586, -69.900692);
        LatLng coord3 = new LatLng(39.926892, -74.172051);
        MapBean.simpleModel.addOverlay(new Marker(coord1, "Cancino Shelter"));
        MapBean.simpleModel.addOverlay(new Marker(coord2, "villa shelter"));
        MapBean.simpleModel.addOverlay(new Marker(coord3, "villa shelter"));
        
    }  
      
    public MapModel getSimpleModel() {  
        return simpleModel;  
    }  
      
    public void onMarkerSelect(OverlaySelectEvent event) {  
        marker = (Marker) event.getOverlay();  
          
        addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "Marker Selected", marker.getTitle()));  
    }  
      
    public Marker getMarker() {  
        return marker;  
    }  
      
    public void addMessage(FacesMessage message) {  
        FacesContext.getCurrentInstance().addMessage(null, message);  
    }  
}  