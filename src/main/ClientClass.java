package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ClientClass {
	public static void main(String[] args) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, SAXException {
		
		int choice = 1; 
		Scanner s = new Scanner(System.in);
		while(choice != 3){
			System.out.println("Enter your Choice : ");
			System.out.println("1. Add FoodItem");
			System.out.println("2. Get FoodItem");
			System.out.println("3. Quit");
			try{
			choice = Integer.parseInt(s.nextLine());}
			catch(Exception e){
				System.out.println("Invalid Option");
				System.out.println("");
				continue;
			}
			
			if (choice == 1){
				System.out.println("Enter FoodItem Country : ");
				String country = s.nextLine();
				System.out.println("Enter FoodItem Name : ");
				String name = s.nextLine();
				System.out.println("Enter FoodItem Description : ");
				String desc = s.nextLine();
				System.out.println("Enter FoodItem Category : ");
				String cate = s.nextLine();
				System.out.println("Enter FoodItem Price : ");
				String price = s.nextLine();
				addFoodItem(country,name,desc,cate,price);
			}else if (choice == 2){
				ArrayList<String> foodItemId = new ArrayList<String>();
				while(true){
					System.out.println("Enter FoodItem ID (Enter 0 to exit): ");
					String itemId = s.nextLine();
					if(itemId.equalsIgnoreCase("0")){
						break;
					}
					foodItemId.add(itemId); 
				}
				getFoodItem(foodItemId);
			}else
				System.out.println("Invalid Option");
				System.out.println("");

		}
		s.close();
	}
	
	public static void addFoodItem(String country,String name,String desc,String cate,String price) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, SAXException{
		Document xmlpayload = generateAddXMLMsg("http://cse564.asu.edu/PoxAssignment", country, name, desc, cate, price);
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
        StringWriter writer = new StringWriter();
        DOMSource source = new DOMSource(xmlpayload);
        transformer.transform(source, new StreamResult(writer));
        String output = writer.getBuffer().toString();
        
	  try {

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost("http://localhost:8080/PoxAssignServer/rest/serverclass/postrequesthandler");
		postRequest.setHeader("content-type", "application/xml");
		postRequest.setEntity(new StringEntity(output));
		
		HttpResponse response = httpClient.execute(postRequest);
		
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
				+ response.getStatusLine().getStatusCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
		System.out.println("");
		System.out.println("***************************************************************************");
		System.out.println("XML Response is :");
		System.out.println("");

		String o;
		while ((o = br.readLine()) != null) {
			System.out.println(o);
		}
		
		System.out.println("***************************************************************************");
		System.out.println("");
	  }catch (IOException e) {

			e.printStackTrace();

		  }
	}
	
	public static void getFoodItem(ArrayList<String> ItemID) throws ParserConfigurationException, TransformerException, SAXException{
		Document xmlpayload = generateGetXMLMsg("http://cse564.asu.edu/PoxAssignment", ItemID);
		
		DOMSource source = new DOMSource(xmlpayload);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
        StringWriter writer = new StringWriter();
        transformer.transform(source, new StreamResult(writer));
        String output = writer.getBuffer().toString();
        
  	  try {

  		DefaultHttpClient httpClient = new DefaultHttpClient();
  		HttpPost postRequest = new HttpPost(
  			"http://localhost:8080/POX-FoodMenu-1208664752-Eclipse/rest/serverclass/postrequesthandler");
  		postRequest.setHeader("content-type", "application/xml");
  		postRequest.setEntity(new StringEntity(output));
  		
  		HttpResponse response = httpClient.execute(postRequest);
  		
  		if (response.getStatusLine().getStatusCode() != 200) {
  			throw new RuntimeException("Failed : HTTP error code : "
  				+ response.getStatusLine().getStatusCode());
  		}

		BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

		System.out.println("");
		System.out.println("***************************************************************************");
		System.out.println("XML Response is :");
		System.out.println("");

		String o;
		while ((o = br.readLine()) != null) {
			System.out.println(o);
		}
		
		System.out.println("***************************************************************************");
		System.out.println("");
		
		}catch (IOException e) {

  			e.printStackTrace();

  		  }
        

	}
	
	public static Document generateGetXMLMsg(String NS, ArrayList<String> ItemID) throws ParserConfigurationException{
		DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder icBuilder;
		icBuilder = icFactory.newDocumentBuilder();
		Document doc = icBuilder.newDocument();
		Element mainRootElement = doc.createElementNS(NS, "SelectedFoodItems");
		doc.appendChild(mainRootElement);
		for (int i = 0; i < ItemID.size();i++){
			
			Element fooditemid = doc.createElement("FoodItemId");
			fooditemid.appendChild(doc.createTextNode(ItemID.get(i)));
			
			mainRootElement.appendChild(fooditemid);	
		}
		return doc;
	}
	
	public static Document generateAddXMLMsg(String NS, String Country, String name, String Description, String Category, String Price) throws ParserConfigurationException{
		DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder icBuilder;
		icBuilder = icFactory.newDocumentBuilder();
		Document doc = icBuilder.newDocument();
		Element mainRootElement = doc.createElementNS(NS, "NewFoodItems");
		doc.appendChild(mainRootElement);
		
		Element foodItem = doc.createElementNS(NS,"FoodItem");
		foodItem.setAttribute("country", Country);
		
		Element namenode = doc.createElement("name");
		namenode.appendChild(doc.createTextNode(name));
		
		foodItem.appendChild(namenode);
		
		
		Element descnode = doc.createElement("description");
		descnode.appendChild(doc.createTextNode(Description));
		
		foodItem.appendChild(descnode);
		
		Element catenode = doc.createElement("category");
		catenode.appendChild(doc.createTextNode(Category));
		
		foodItem.appendChild(catenode);
		
		Element pricenode = doc.createElement("price");
		pricenode.appendChild(doc.createTextNode(Price));
		
		foodItem.appendChild(pricenode);
		
		mainRootElement.appendChild(foodItem);
		return doc;
		
	}
}
