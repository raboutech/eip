package eip;
/*
 * created by Denoun 06/05/2017
 */

import java.util.Scanner;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.log4j.BasicConfigurator;

public class ProducerConsumer {
	public static void main(String[] args) throws Exception {
		
		 // Configure le logger par d�faut
		 BasicConfigurator.configure();
		
		 // Contexte Camel par d�faut
		 CamelContext context = new DefaultCamelContext();
		
		 // Cr�e une route contenant le consommateur
		 RouteBuilder routeBuilder = new RouteBuilder() {
		
				 	@Override
				 	public void configure() throws Exception {
						 // On d�finit un consommateur 'consumer-1'
						 // qui va �crire le message
					 from("direct:consumer-1").to("log:affiche-1-log");
					 /*
					  * question 2.a ajout d'un autre consommateur 
					  */
					 from("direct:consumer-2").to("file:affiche-1-file");
					 
					 /*
					  * question 2.b 
					  *  Ajout d'une route consumer-all qui envoi un message � consumer-2 
					  *  si l'ent�te destinataire contient � �crire �
					  *  consumer-1 sinon.
					  */
					 from("direct:consumer-all").choice()
						.when(header("destinataire").isEqualTo("�crire"))
							.to("direct:consumer-2")
						.otherwise()
							.to("direct:consumer-1");
				 	

				 	/*
				 	 * question 3
				 	 * 
				 	 */
				 	from("direct:zoo-manager")
				 	.setHeader(Exchange.HTTP_METHOD,constant("GET"))
				 	.to("http://127.0.0.1:8080/find/byName/${header.name}")
				 	.log("reponse received : ${body}");
				 	/*
				 	 * question 3.b
				 	 */
				 	
				 	from("direct:Citymanager")
				 	.setHeader(Exchange.HTTP_METHOD,constant("GET"))
				 	.to("api.geonames.org/search?q=${header.name}&username=denoun_rabou")
				 	.log("reponse received : ${body}");
				 	
				 	
				 	
			
				 	/*
					 * Question 4
					 * Integration du service (Jgroupe) 
					 */
	                from("jgroups:m1gil").choice()
	                .when(body().contains("ADFTW"))
	                	.log("Le mot cle est:${body}")
	                .otherwise()
	                	.log("reponse received  : ${body}");
					
				 	}
		 	};
		 	
		 	

		 	// On ajoute la route au contexte
		 routeBuilder.addRoutesToCamelContext(context);
		 	// On d�marre le contexte pour activer les routes
		 context.start();
		
		 	// On cr�e un producteur
		 ProducerTemplate pt = context.createProducerTemplate();
		 	// qui envoie un message au consommateur 'consumer-1'
		    // pt.sendBody("direct:consumer-1", "Hello world !");
		 
		 
		 
		 String message="";
		 
		 	/*
		 	 * question 1.a 
		 	 */
		 do{
			 System.out.println("veuillez entrer votre message");
			 Scanner scanner = new Scanner(System.in);
			    message = scanner.nextLine();
			    
			    /*
			     * question 2.c 
			     * ajout d'une nouvelle route consumer-all
			     */
			    if(!message.startsWith("w"))
			    	pt.sendBody("direct:consumer-all",message);
			    else
			    	pt.sendBodyAndHeader("direct:consumer-all",message,"destinataire","�crire");
			   
			    /* 
			     * question 2.c
			     */
//			    if(!message.startsWith("w"))
//			    	pt.sendBody("direct:consumer-all",message);
//			    else
//			    	pt.sendBodyAndHeader("direct:consumer-all",message,"destinataire","�crire");
			    
			    
			    /*
			     * question 3.a
			     * 
			     */
			   //pt.sendBody("direct:CityManager",message);
			    
			    /*
			     * question 3.b
			     * 
			     */
				
			   //pt.sendBody("direct:geonames",message);
			    
			    /*
			     * Envoyoi du message dans le r�seau local
			     * 
			     */
			    pt.sendBody("jgroups:m1gil", message);

		
			    // question 1.b
		 }while (!message.equals("exit"));
		
	}
}