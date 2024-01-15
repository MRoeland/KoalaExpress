Readme voor eindwerk project Android

Het is belangrijk om de applicatie te draaien met telefoon en niet via de emulator.
er is internet access nodig op de emulator en dat gaat soms mis. de emulator internet toegang is soms te traag.

Clientside: 
	- android java project KoalaExpress
serverside API: 
	- webservlets via tomcat server op synology NAS thuis 
			base address in browser : "http://91.181.60.26:7070/KoalaExpressServer" of "http://jurnas.synology.me:7070/KoalaExpressServer").
			vb : http://91.181.60.26:7070/KoalaExpressServer/products?action=listCategories
			Tomcat gui website administratie voor deploy: http://91.181.60.26:7070/manager/html
			username : tomcat   password: secret
	- MariaDB v 10.3.32 op synology NAS ("http://91.181.60.26/phpmyadmin" "http://jurnas.synology.me/phpmyadmin") 
			username : koala   password : Chickenmadras1@
			dependency voor mysql connector op basis van JDBC : https://mvnrepository.com/artifact/mysql/mysql-connector-java
	- Image Store online
			op website jursairplanefactory.com/koalaimg folder
			vb : www.jursairplanefactory.com/koalaimg/hawaii-9605.png
			
Accounts:
	- KoalaExpress App 
			vb user : mroeland03 	password : chickenmadras
			   user : aruftabeti03   password : chickenmadras
				 
	- Paypal Sandbox accounts voor business account en personal account (zie class PayPalConfig)
			paypal sandbox api key : PAYPAL_CLIENT_ID : AbF7alF2K8rKrxodK4PZaYjqwCv6dFjjq9lhploKLDmMAb6vrr12YcTLTu0taiUkIvSepB2aJFyFdhce
			paypal sandbox api secret key : PAYPAL_SECRET_KEY1 : EDv_Vpd_IbiAg0tg_LpKHoWowxZGWkT8VwLgjetqls-3uIJN2yia3uq8TXnzxS5AUhHQvxOmaNwtt9ae
			paypal dashboard developer : https://developer.paypal.com  -> login to dashboard

	- Google Maps API  ( zie manifest.xml )
			API_KEY : AIzaSyAOOsra2hLUCeNQ-0N9jZKmbKIajXQ0k70
			Beheer van Google Maps Platform: https://console.cloud.google.com/google/maps-apis/api-list?project=koalaexpressapp
			

Info sources:
 
- paypal rest api:
	- eerst geprobeerd met braintree sdk. dit bleek niet meer gesupporteerd in latere versies van android
	      https://stackoverflow.com/questions/67200260/paypal-sdk-integration-with-android-login-failed
	- verder gezocht naar huidige versie en rest api gevonden
		 gevonden via en stappen gevolgd van : https://lo-victoria.com/the-complete-guide-to-integrate-paypal-in-mobile-apps
		 verdere info over order en inhoud: https://developer.paypal.com/docs/api/orders/v2/#orders_create
		 
		 hoe gebruik van die api volgens stappen in lo-victory.com site
			er wordt gebruikt gemaakt van custom tab browser (CCT) voor de redirect naar paypal
			er zijn een aantal web api calls na mekaar (access token halen, create order, checkout order)
			eerste een token ophalen, algemeen in globals,bijgehouden in datarepository. wordt opgehaald als blank anders zelfde behouden
				een token is een soort van sessie id die heel de tijd herhaalt wordt
			dan een order maken en naar paypal sturen
			response van create order zijn een aantal href links die door paypal teruggestuurd worden, hieruit nemen we de "approve" blok link en roepen die url in href aan
			dit opent het paypal inlog deel via CCT (custom tab browser)
			een deeplink brengt je terug van confirmation naar de payment confirmation in de android app 
			een deep link is een link die een url omleidt naar een intent en je app opent -> hangt vast aan activity declaratie in manifest.xml bij activity .CheckoutActivity
			zie android manifest.xml en de link als return URL in de app (dit werkte alleen met eigen scheme voor de url, niet met http)
			de finale confirmation and checkoutorder call naar paypal uit de checkoutactivity
			op einde gaat terug naar mainactivity en springt naar het winkelmandje bij start via een Intent string parameter
		verificatie accounts in paypal : kijken naar accounts en balans https://developer.paypal.com/dashboard/accounts
				mail controleren van ontvangen of verstuurd in sandbox accounts via: https://developer.paypal.com/dashboard/notifications


- hoe starten van activity met start parameters
	https://stuff.mit.edu/afs/sipb/project/android/docs/training/basics/firstapp/starting-activity.html  -> zie onderaan pagina
	
- hoe image laden via een URL online
		https://stackoverflow.com/questions/18953632/how-to-set-image-from-url-for-imageview
		dit uitgebreid met local opslaan op device na inladen online
		als op device gevonden laden van device en niet opnieuw ophalen (class : Task_ImageLoad en LocalFileOperations)
		
- how to make a reusable dialog class : 
	https://www.linkedin.com/learning/android-development-tips/create-a-reusable-dialog-class
	layout maken, afleiden van DialogFragment

- het maken van een custom switchbutton voor afhalen/bezorgen knop bij vestigingen dialoog
	https://www.youtube.com/watch?v=q1-ur7p2Bks
	https://www.youtube.com/watch?v=NIU-jZfJNnY
	https://stackoverflow.com/questions/23358822/how-to-custom-switch-button
	https://www.youtube.com/watch?v=5xMPLe1gnOA

- een bestaande layout gebruiken als deel van een ander layout file: include (gebruikt in WinkelmandjeFragment)
	https://stackoverflow.com/questions/5632114/how-to-include-layout-inside-layout

- hoe een jpg file toevoegen aan lokale resource file en gebruiken in imageview
	https://www.youtube.com/watch?v=Y7JTkXoN8OE

- how to use searchview om te filteren op product categorie in winkel scherm
	https://abhiandroid.com/ui/searchview#gsc.tab=0
	(https://www.youtube.com/watch?v=Qb2DN4h61yg)
	
- search icon on right side in searchview: 
	https://stackoverflow.com/questions/36905036/how-to-move-searchview-s-search-icon-to-the-right-side
	
- how to get color object from resource colors
	https://stackoverflow.com/questions/30547049/android-get-color-object-from-resource
	
- probleem dat bij switch van checkoutactivity naar mainactivity en springen naar winkelmandjefragment heeft probleem dat je niet meer terug naar eerste winkel scherm kan springen :
	(laatste deel van deze pagina heeft het over setStartDestination van een navgraph om elders te starten
	https://developer.android.com/guide/navigation/use-graph/programmatic

- gebruik van asynctask en duidelijke uitleg van de parameters <input, progress, resultaat> achter de asynctask template, zie tekening
	https://itecnote.com/tecnote/android-okhttp-async-calls-asynctask-or-okhttp-async-api/
	
- gebruik van asynctask om api okhttpclient calls te doen :
	https://blog.fossasia.org/receiving-data-from-the-network-asynchronously/

- asynctask: onpostexecute in main gui thread en onbackground in achtergrond thread ( zie answer 9 en 5 in link )
	https://stackoverflow.com/questions/50252205/why-is-asynctask-working-on-the-main-thread
	
- gebruiken van enum in de loadingscreen om status bij te houden van de voortgang
	https://www.javatpoint.com/enum-in-java

- hoe een latlng coordinaat voor google maps vinden op basis van een adres dat opgeslagen is bij location in db (vestigingen) -> Geocoder google api class en getFromLocationName(adres).
	https://www.youtube.com/watch?v=bBF2AkwPYtQ

- dialog maken met dialog fragment:
	https://www.geeksforgeeks.org/dialogfragment-in-android/
	https://www.youtube.com/watch?v=oNl1pndO-I4

- loading screen tijdens laden van repository en maken offline room db (automatisch naar mainactivity als klaar en check status met een automatische timer)
	how to start an activity automatically from an activity after a task completes. youtube : "Automatically Change Activity after Few Seconds | Tutorial"
	gebruik van timer object en switch activity : https://www.youtube.com/watch?v=2JGm67leD7g

	
- serverside servlets maken met tomcat en httpservlet versies:
	servlets en tomcat: https://www.youtube.com/watch?v=7TOmdDJc14s   (introduction to servlets)
 	https://tomcat.apache.org/tomcat-7.0-doc/servletapi/javax/servlet/http/HttpServlet.html   (referentie httpservlet)
	https://www3.ntu.edu.sg/home/ehchua/programming/howto/tomcat_howto.html  (install tomcat http server en voorbeeld van httpservlet class "HelloServlet"
	https://jakarta.ee/specifications/servlet/6.0/apidocs/jakarta.servlet/jakarta/servlet/http/httpservlet

- use json in servlet om resultaat door te geven in response en algemeen over json en servlets
	https://www.youtube.com/watch?v=KFkN8akuEoQ

- jackson JSON bibliotheek gebruiken , hoe een json string terug omzetten tot een class met jackson
	https://www.youtube.com/watch?v=LsiYA3MIAcs

- schrijven JSON van object naar string 
	https://www.youtube.com/watch?v=pJbvqguVb8c

- hoe een string doorgeven in url met speciale characters -> URLEncoder en URLDecoder
	https://www.urlencoder.io/java/
	https://www.urldecoder.io/java/
	
- hoe connecteren aan een mariadb database in java: mysqldatasource	connection
	https://www.javaguides.net/2018/10/jdbc-datasource-connection-mysql-example.html
	voorbeelden van queries:
	https://www.javaguides.net/2018/10/jdbc-statement-interface.html
	voorbeeld van query met doorgeven van een parameter om te zoeken
	https://www.javaguides.net/2018/10/jdbc-preparedstatement-select-records-example.html
	

vragen aan chatgpt:
chatgpt:
- google api
	"i am writing an android java app using google map api. i want to use markers with a custom image, can you write an example"
	"how to add extended info to a marker"
	"can I add a custom info window to the marker"

- dialog fragment pass arguments
	"i am writing an android java app. if I am using a dialogfragment which i"m starting from the main activity. can I pass arguments to this dialog ?"
	-> result use bundle

- dialog transparent frame maken om de witte hoeken achter rounded corners van de dialog weg te doen (logindialog, infodialog) :
	"i have a cardview in a layout for a dialogfragment with rounded corners on the cardview. how can i make the background of the dialog transparent ?"
	oplossing style maken in styles.xml en setStyle in oncreate van dialogfragment

- order items toevoegen aan een paypal order bij request order:
	veel knoeien met de juiste structuur door te sturen naar paypal voor aanmaak van het order. wou orderlijnen ook toevoegen aan paypal orderlijnen
	"i am writing an android java app and making a paypal transation using the paypal api. to request my order I am making the content using the following code to identify the order. How can I add detailed items to this 
	request which will contain each individual ordered item and it's quantity and price. (+reeds bestande json code)"
	
	later probleem dat totaal amount niet gelijk aan order item totaal, na lang zoeken gevonden dat reduction amount moet meegeven als je breakdown doet van total price in total items en reduction
	"android java app, making a paypal transation using the paypal api. to request my order I need to pass reduction value on the order price in the order object. (+reeds bestande json code)"

- android java app. i'm making a layout as defined below. I would like to programmatically attach the rv_WinkelItems recyclerview below the searchview when clicking on the hourglass icon of the searchview and when closing 
  the searchview, programmatically attach it to the bottom of the rv_Winkelcategories. how can i do this. + layout winkelfragment pasted
	(om winkel item recycler top te verplaatsen naar bottom of searchview, of bottom of categories recycler)
	antwoord -> gebruik van ConstraintSet, clone van fragmentwinkel constraint layout, nieuwe connect, en apply aanroepen bij onclose en onsearchclick




Extra uitleg:

Over de opbouw van de app:

Er is een data bibliotheek en er zijn 3 fragmenten met elk hun viewmodel

- een winkel fragment waar je de artikelen kan kiezen en toevoegen aan je mandje
- een winkelmandje fragment dat het mandje laat zien en toelaat vestiging te kiezen waar je wil afhalen of laten leveren, wie je bent en een korting ingeven.
	de kortingen zijn hard gecodeerd maar kunnen later ook uit de databank komen. de waarden die je kan kiezen nu zijn "k10", "k5" voor percent korting en v5 voor vaste 5euro
- een vestiging fragment dat de locaties van de winkels laat zien maar deze heeft verder geen link relatie met het mandje.

elke fragment heeft een model.

omdat ik alle data op 1 plaats wou inlezen en bijhouden heb ik een singleton object gemaakt waar alle data opgeslagen is en van waaruit alle oproepen om data op te halen
van verschillende plaatsen staat (online via api of lokaal uit roomdb). het repository object noemt KoalaDataRepository. hierin zit ook het winkelmandje, de ingelogde klant en de
geselecteerde vestiging en alle gelezen data uit de databank.
al deze objecten zijn mutablelivedata zodat je vanuit de fragmenten een observer kan maken. zodra de inhoud verandert van zo een object wordt een observer functie aangeroepen
zodat de fragement opnieuw kan tekenen. ook product categorieen en producten zijn live objecten. 
de repository wordt in bijna elk scherm gebruikt om aan de data te komen die nodig is om af te beelden.
ik heb zoveel mogelijk geprobeerd om de functies die eigen zijn aan de pure data in de data repository te houden.
ophalen van data bvb, inlezen online of van de room db. dat gaat met async task objecten en omdat je niet weet wanneer die klaar zijn is het handig om met die observers een melding
te krijgen dat een object gewijzigd is.

Voor het online ophalen van de gegevens gebruik ik een api dit ik gemaakt heb met intelliJ en die draait op een tomcat server op een NAS.
De klassen aan de server kant zijn afgeleid van httpwebservlet en tomcat zorgt ervoor dat als je de url oproept met een bepaald servletnaam, de juiste klasse gestart wordt.
ik geeft dan ook nog een parameter "action" mee om binnen een servlet meerdere functie te kunnen maken.
er zijn servlets voor "products", "customers", "orders" en "versions" (om online check te doen).
op de server kant gaat elke functie een connectie maken naar de databank en met een select de gegevens ophalen uit een mysql mariadb.
ik maak daar dezelfde objecten aan als in de app en geeft ze dan met omzetten naar json string door aan de response url.
alle servlets geven data terug. alleen orders laat toe om het winkelmandje op te sturen en op te slaan.
elke servlet geeft een JSON string terug die gemaakt is op basis van een ReturnMessage object. 
elke boodschap die terugkomt is een "ReturnMessage" en heeft een "resultHeader" die een boolean succes/failure heeft en een errormessage/boodschap en een string veld Content.
bij het uitschrijven uit de servlet schrijft hij eerst de json van de header, dan is er een | teken en daarachter staat de inhoud van wat je terug wil sturen.
ik heb een klasse gemaakt in de app voor alles wat met json te maken heeft waar ik veel gebruikte functie in gestopt heb (JSonhelper)
als ik een resultaat terugkrijg van een servlet in de app maak ik een objectmapper (jackson JSON dependency) 
 bij elke return neem ik de s = (response.body().string()); en laat die omzetten in een returnMessage msg = JSONhelper.extractmsgFromJSONAnswer(s);
 dit deelt eerst het resultaat op het | teken. het eerste deel zet ik om met json naar een resultHeader en zet de rest vcan de boodschap in "content".
 als er succes was in de header neem ik daarna de content en zet die met objectmapper om naar de class die ik terug krijg. (meestal is dat een list object zoals ProductCategoryList, ProductList, CustomerList, LocationList...)
 pas op het einde van het lezen en omzetten in OnPostExcecute van de asynctask taak zet ik de data uiteindelijk in de repository data.




LoadingScreenActivity: 

De loading screen activity is toegevoegd om de tijd te overbruggen van het inladen van de data en wegschrijven naar lokale room DB vooraleer naar het winkelscherm te gaan.
De manier waarop het werkt is dat er meerdere statussen zijn die een flow volgen.
de flow is afhankelijk van de online check status.
Indien Online is de volgorde : stepOnlineCheck -> stepInitialseerRepo -> stepCopieerLokaalInRoomDB -> stepLeesMandjeUitRoomDB -> stepSplashKlaar
Indien Offline is de volgorde : stepOnlineCheck -> stepInitialseerRepo -> stepLeesMandjeUitRoomDB -> stepSplashKlaar

Om van de ene stap naar de andere te gaan is er een timer gemaakt die elke seconde controleert wat de toestand van elke status is en of je naar de volgende stap mag
binnen in elke stap wordt gekeken of je online of offline bent en de data gelezen lokaal of via web.
de reden waarom ik dit met een timer doe is omdat je niet vooraf weet hoe lang elke operatie in de asyntasks zal duren.

stepOnlineCheck : doet een asynctask okhttp taak die een call naar de server doet. indien online krijg je antwoord terug. dit gebeurd al bij oncreateview van de activity

binnen de time check ik op volgende voorwaarden:

huidige status is stepOnlineCheck en repository variabele mServerStatusGechecked is op true gezet (dat gebeurd nadat server check gebeurd is)
	indien true, gaan we de volgende stap zetten. in beide gevallen is volgende stap stepInitialseerRepo
			als we online zijn gaan we de repo initialiseren (alle objecten via een asynctask okhttp inladen)
			als we niet online zijn gaan we de repo initialiseren vanuit een lokale roomdb

huidige status is stepInitialseerRepo en de repo toestand isReady(). isready geeft true als alle dataobjecten ingelezen zijn in de repo (arraylists.size > 0)
	indien true zetten we de volgende step
		als online zijn, gaan we eerste de data die we net gelezen hebben wegschrijven in de roomdb en is de nieuwe stap stepCopieerLokaalInRoomDB
		als we offline zijn gaan we het laatst opgeslagen lokaal order lezen (LoadOrderFromRoomDB) uit roomdb en is de volgende stap stepLeesMandjeUitRoomDB
		
huidige status is stepCopieerLokaalInRoomDB en ReadyInitDB == true. ReadyInitDB wordt op true gezet wanneer de ganse roomdb aangemaakt is.
	de volgende stap is stepLeesMandjeUitRoomDB en we lezen het order in uit de database met LoadOrderFromRoomDB

huidige status is stepLeesMandjeUitRoomDB en mFinishedReadingBasketFromRoomDB = true (inladen van order is klaar)
	zet volgende stap op stepSplashKlaar

huidige status is stepSplashKlaar
	ga naar de MainActivity die het winkelscherm zal starten.
	

in de roomdb herschrijven we telkens alle data inhoud behalve de order en orderlijnen. die blijven altijd bewaard als een soort van lokale cookie zodat de laatste 
winkelmandje inhoud elke keer opnieuw ingeladen kan worden.



PayPal:
hoe het paypal systeem werkt.

je moet aan paypal eerst een token vragen. dat is een soort van identificatie van je transactie. omdat je verschillende calls over dezelfde transactie doet moet je 
een soort van identificatie hebben om steeds naar je transactie te kunnen verwijzen. dit doe je met een "access token"
het acces token hou ik bij in de data repository. bij het starten van het winkelmandje fragment controleer ik of mijn acces token een waarde bevat.
indien ja, dan blijven diezelfde waarde gebruiken, indien lege string dan haal ik een nieuwe access token op bij paypal. (GetPaypalAccessToken() in winkelmandjefragment).
dit is om te vermijden dat je elke keer als je van scherm wisselt een nieuw accesstoken zou aanvragen bij paypal.
de access token wordt op het einde van de hele afhandeling en bevestiging terug op leeg gezet zodat er daarna een nieuwe aangemaakt wordt.
het access token wordt bij elke call naar paypal meegegeven in de header als client.addHeader("Authorization", "Bearer " + accessToken);

als alle checks gelukt zijn (mandje heeft minstens 1 product, klant is ingelogd, vestiging is gekozen, je bent online), dan start de paypal workflow met de functie createPayPalOrder()
voor de verschillende stappen heb ik een zeer goeie tutorial gevolgd die in mijn readme's ook staat (https://lo-victoria.com/the-complete-guide-to-integrate-paypal-in-mobile-apps)
de eerste call naar paypal is het bekend maken van het order en de inhoud ervan. (basis url https://api-m.sandbox.paypal.com/v2/checkout/orders)
hier maken we eerst een jsonobject waarin de items gezet worden, de totale prijs, de korting, de shipping informatie.
en belangrijk: we geven een url mee die aan het einde moet opgeroepen worden om verder te gaan in onze app. dat gaat met een deeplink. 
die deeplink hangt vast aan de CheckoutActivity en wordt gedefinieerd in de parameteres van de activity in AndroidManifest.xml

het antwoord van paypal is een resultaat dat 4 "rel" waardes krijgt met voor elk van die waarden een link in href om aan te roepen. (zie onderaan om vb te zien)
om het betalen verder te starten moet je de rel waarde "approve" zoeken en dan de href die daar bijhoort oproepen.
er wordt een custom browser (CCT) gestart en je geeft de href waarde mee om die te openen. (die code heb ik overgenomen uit het voorbeeld)
hiermee wordt nu het paypal inlog scherm getoond en de keuze van welke betalingswijze je wil gebruiken. 
wanneer je in die browser dan "review transaction" kiest wordt de deeplink opgeroepen uit de browser en neemt de app weer over en start door de deeplink de checkoutactivity.

in de checkoutactivity zijn 2 knoppen, 1 stopt de transactie en gaat terug naar de mainactivity en springt naar winkelmandje fragment. (returnToOrderZonderConfirm())
hierin springen we naar mainactivity en geven de intent extra string info mee JumpToFragment = WinkelMandjeAnnuleren. dat gaan we in create van mainactivity gebruiken.
als je "bevestigen" klik roepen we captureOrder() op.
bij het terug komen uit de deeplink krijgen we van paypal een redirect url terug. hierin zit het orderID dat we moeten gebruiken 
in oncreate van checkoutactivity doen we : orderID = redirectUri.getQueryParameter("token"); en payerID = redirectUri.getQueryParameter("PayerID");
de orderID is nodig als parameter om de laatste call naar paypal te doen
hierin roepen we de laatste stap op van de paypal transactie : https://api-m.sandbox.paypal.com/v2/checkout/orders/"+orderID+"/capture

bij onsucces van de laatste call is de betaling gebeurd en de payment id zit verborden in het antwoord. ik heb chatgpt gebruikt om de paymentID te gaan zoeken in dat antwoord.
in onsuccess zet ik de paymentid en de current time als betalings datum in het winkelmandje.
dan slaan we het order online op in de mariadb met de Task_SendOrderToDB asynctask. als resultaat hiervan krijgen we de koala order ID terug die toegekend is bij het invoegen in de tabellen.
om te wachten tot dit klaar is doe ik een truucje met een taak die eerst 3 seconden wacht en dan pas naar de mainactivity terugspring.
bij het springen naar mainactivity geef ik JumpToFragment = WinkelMandjeSucces mee en ook het afgesloten order ID en paypal payment id.

bij het starten van main activity wordt er in oncreate gekeken of er een parameter is meegegeven met de intent
	String intentExtra = getIntent().getStringExtra("JumpToFragment");
    if (intentExtra != null && (intentExtra.equals("WinkelMandjeSucces") ||  intentExtra.equals("WinkelMandjeAnnuleren"))) {
	
	in beide gevallen springen we naar winkelmandje fragment. via de navcontroller springen ging niet goed omdat hij dan niet meer naar eerste fragment kon klikken omdat hij de war raakte.
	heb dan gevonden dat je de startdestination kan zetten (zie readme voor bron) en dan ging het wel goed
	
	als het bovendien succes was dan laten we de info dialog zien met de melding dat bestelling klaar is en tonen ordernr
	daarna 
		- maken we het winkelmandje leeg.
		- zetten we de paypal acces token op leeg zodat er volgende keer een nieuwe gemaakt wordt
		- wissen we de laatste gekende order in de roomDB omdat het order klaar is.
		


voorbeeld van resultaat van een create order wat je terugkrijgt van paypal en waarin we approve moeten zoeken en dan de href oproepen
{
  "id": "7SK00420K5251910F",
  "status": "CREATED",
  "links": [
    {
      "href": "https://api.sandbox.paypal.com/v2/checkout/orders/7SK00420K5251910F",
      "rel": "self",
      "method": "GET"
    },
    {
      "href": "https://www.sandbox.paypal.com/checkoutnow?token=7SK00420K5251910F",
      "rel": "approve",
      "method": "GET"
    },
    {
      "href": "https://api.sandbox.paypal.com/v2/checkout/orders/7SK00420K5251910F",
      "rel": "update",
      "method": "PATCH"
    },
    {
      "href": "https://api.sandbox.paypal.com/v2/checkout/orders/7SK00420K5251910F/capture",
      "rel": "capture",
      "method": "POST"
    }
  ]
}
