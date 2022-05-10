
import java.util.Random;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 * @author ebruno
 * 
 * This CoAP server simulates a pulse oximeter used to measure a patient's
 * heart rate and blood oxygen saturation level. Each of these readings is
 * set as CoAP sub-resources, "heartrate" and "oxygen-saturation", under 
 * the parent resource "pulseoximeter"
 * 
 */
public class CoapPulseOximeter extends CoapServer {
    public static void main(String[] args) {
        CoapPulseOximeter pulseOxi = new CoapPulseOximeter();
        pulseOxi.start();

        // Register with Resource Directory
        pulseOxi.registerRD();

    }
    
    HeartRateResource heartRateResource = null;
    OxiResource oxiResouce = null;

    public CoapPulseOximeter() {
        try {
            // Add the CoAP Resources
            CoapResource pulseOxiResource = 
                    new CoapResource("pulseoximeter");
            
            heartRateResource = new HeartRateResource();
            oxiResouce = new OxiResource();
            pulseOxiResource.add( heartRateResource );
            pulseOxiResource.add( oxiResouce );
            
            this.add(pulseOxiResource);
            
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    
    private void registerRD() {
        try {
        CoapClient q = new CoapClient("coap://10.0.1.111/.well-known/core"); 
        CoapResponse resp = q.get();
        System.out.println("--Registered resources: " + resp.getResponseText());
        
        CoapClient rd = new CoapClient("coap://10.0.1.111/rd?ep=pulseoximeter/heartrate/");
        
        System.out.println("Registering resource: heartrate ");
        resp = rd.post("</pulseoximeter/heartrate>;"
                        + "ct=41;rt=\"Heartrate Resource\";"
                        + "if=\"sensor\"", 
                        MediaTypeRegistry.APPLICATION_LINK_FORMAT);
        System.out.println("--Response: " + 
                            resp.getCode() + ", " + 
                            resp.getOptions().getLocationString());

        rd = new CoapClient("coap://10.0.1.111/rd?ep=pulseoximeter/oxygen-saturation/"); 
        System.out.println("Registering resource: oxygen-saturation ");
        resp = rd.post("</pulseoximeter/oxygen-saturation>;"
                        + "ct=41;rt=\"Oxygen Saturation Resource\";"
                        + "if=\"sensor\"", 
                        MediaTypeRegistry.APPLICATION_LINK_FORMAT);
        System.out.println("--Response: " + 
                            resp.getCode() + ", " + 
                            resp.getOptions().getLocationString());
        
        resp = q.get();
        System.out.println("--Registered resources: " + resp.getResponseText());
            //"/.well-known/core"
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }   

    /////////////////////////////////////////////////////////////////////////
    // CoAP resource for patient's heartrate
    /////////////////////////////////////////////////////////////////////////
    class HeartRateResource extends CoapResource {
        public HeartRateResource() {
            // set resource identifier
            super("heartrate");

            // set display name
            getAttributes().setTitle("Heartrate Resource");
        }

        @Override
        public void handleGET(CoapExchange exchange) {
            // respond with the patient's current heart rate (random)
            int hr = 70 + new Random(System.currentTimeMillis()).nextInt(8);
            exchange.respond("Heartrate: " + hr);
        }
    }
    
    /////////////////////////////////////////////////////////////////////////
    // CoAP resource for patient's Oxygen saturation
    /////////////////////////////////////////////////////////////////////////
    class OxiResource extends CoapResource {
        public OxiResource() {
            // set resource identifier
            super("oxygen-saturation");

            // set display name
            getAttributes().setTitle("Oxygen Saturation Resource");
        }

        @Override
        public void handleGET(CoapExchange exchange) {
            // respond with the patient's current blood oxygen saturation
            int oxi = 90 + new Random(System.currentTimeMillis()).nextInt(10);
            exchange.respond("Oxygen saturation: " + oxi);
        }
    }
    
    
}
