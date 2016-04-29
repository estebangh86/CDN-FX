package groovycia2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.logging.Level;

public class XMLHandler {

    private ObservableList<Ticket> ticketlist;

    public XMLHandler(ObservableList<Ticket> ticketlist){
        this.ticketlist = ticketlist;
    }

    public void setTicketList(ObservableList<Ticket> ticketlist){
        this.ticketlist = ticketlist;
    }

    public ObservableList<Ticket> readCommunityXMLFile(boolean isDBV){
        DebugLogger.log("Reading community database...", Level.INFO);
        try{
            if(CustomXMLHandler.getCommunityPath() == null){
                DebugLogger.log("No community database found!", Level.WARNING);
                return null;
            }

            File xmlFile = new File(CustomXMLHandler.getCommunityPath());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("Ticket");

            Ticket tickets[] = new Ticket[0];

            if(!isDBV)
                tickets = new Ticket[ticketlist.size()];

            ObservableList<Ticket> tickets1 = FXCollections.observableArrayList();

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    String name = eElement.getElementsByTagName("name").item(0).getTextContent();
                    String region = eElement.getElementsByTagName("region").item(0).getTextContent();
                    String serial = eElement.getElementsByTagName("serial").item(0).getTextContent();
                    String titleid = eElement.getElementsByTagName("titleid").item(0).getTextContent().toLowerCase();

                    int i = 0;

                    if(!isDBV){
                        for(Ticket tiktik:ticketlist){

                            if(tiktik.getTitleID().toLowerCase().contains(titleid) && titleid.length() > 1){
                                if(region.equals("WLD"))
                                    region = "ALL";
                                tiktik.setName(name);
                                tiktik.setRegion(region);
                                tiktik.setSerial(serial);
                                tickets[i] = tiktik;
                                i++;
                            }else{
                                tickets[i] = tiktik;
                                i++;
                            }
                        }
                    }else{
                        Ticket tiktik = new Ticket();
                        if(region.equals("WLD"))
                            region = "ALL";
                        tiktik.setName(name);
                        tiktik.setRegion(region);
                        tiktik.setSerial(serial);
                        tiktik.setTitleID(titleid.toUpperCase());
                        tickets1.add(tiktik);
                        //i++;
                    }

                }
            }

            DebugLogger.log("Database processed!", Level.INFO);

            if(!isDBV){
                ObservableList<Ticket> ticketlist = FXCollections.observableArrayList(tickets);
                ticketlist.removeAll(Collections.singleton(null));
                return ticketlist;
            }else {
                return tickets1;
            }


        }catch (Exception e){
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            DebugLogger.log(errors.toString(), Level.SEVERE);
        }
        return null;
    }

}
