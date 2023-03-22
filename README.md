# Projekttitel

## Projektbeschreibung

### Projektziele

[Beschreiben Sie hier in einer kurzen Zusammenfassung Hintergrund, Ziele und Funktionen Ihrer Anwendung. Fügen Sie mehrere sinnvollen Screenshot ein, die den Funktionsumfang der Anwendung demonstrieren.]


Unsere App „FriendCalendar“ ist ein Kalender, mithilfe welches man sowohl private als auch öffentliche Termine, zugänglich für Freunde, erstellen kann. Von fünf Ideenvorschlägen entschieden wir uns letztendlich für diesen, da wir mit den Versionen auf dem Markt nie hundertprozentig zufrieden waren und uns daher daran versuchen wollten, eine gute Alternative zu erschaffen.

So kann man über unsere Applikation Termine erstellen und diese mit Freunden über Google teilen oder als private Termine nur im eigenen Kalender speichern. Natürlich lassen sich gespeicherte Termine auch bearbeiten oder wieder löschen. All dies ermöglicht die im Hintergrund agierende Datenbank. Außerdem kann der Nutzer entscheiden, ob er sich an Termine über eine Benachrichtigung erinnern zu lassen, damit er diese nicht vergisst. Um seine privaten Termine vor ungewollten Eingriffen zu schützen, ist es dem Nutzer möglich die App mit einem Fingerabdruck, bzw. anderer Methode zu sichern.


### Funktionen

[Beschreiben Sie die Funktionen ihrer App. Beziehen Sie sich dabei auf den Anforderungskatalog und stellen Sie eine Verbindung zu den entsprechneden Files oder Directories her.]


**[NotificationsInitializationActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/NotificationInitializationActivity.java), [FingerprintInitializationActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/FingerprintInitializationActivity.java), [GoogleInitialization](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/GoogleInitializationActivity.java):** Beim erstmaligen Öffnen der App nach der Installation wird der Nutzer in einen Initialisierungsfluss weitergeleitet. Bei diesem durchläuft er drei Activities, die ihn nacheinander fragen, ob er Benachrichtigungen aktivieren möchte, dort wird bei Zustimmung zuerst um deren Erlaubnis gebeten, ob er seine Daten mithilfe einer Fingerabdrucksperre sichern und sich mit Google anmelden möchte.

**[CalendarActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/CalendarActivity.java):** Danach gelangt er auf die Hauptseite der App, eine Darstellung des Kalenders, wobei der Nutzer zwischen einer Monats- und Wochenansicht wechseln kann. Zu dieser Activity gelangt der Nutze immer, wenn er die App über ihr Icon öffnet und im Falle einer aktivierten Fingerabdrucksperre eben diese erfolgreich absolviert. Von hier aus kann der Nutzer die Einstellungen öffnen, einen neuen Termin erstellen, einen Freund hinzufügen oder auf die Anzeige eines einzelnen Tages zugreifen. Falls der Nutzer ungültige Daten eingibt, so wird ihm dies prompt vermittelt. Gültige Termine werden hingegen in der Datenbank gespeichert.

**[FingerprintActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/FingerprintActivity.java):** Sofern der Nutzer die Fingerabdrucksperre eingerichtet hat, wird er beim Öffnen der App, egal auf welchem Wege, auf eine Activity geleitet, wo er seinen Fingerabdruck eingeben muss. Nur sofern dies erfolgreich gelingt, hat er Zugriff auf die restliche App. Wenn die Fingerabdrucksperre nicht eingerichtet ist, wird diese Seite übersprungen.

**[SettingsActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/SettingsActivity.java):** In den Einstellungen kann der Nutzer nachträglich B4enachrichtigungen aktivieren und wieder deaktivieren, ebenso wie die Fingerabdrucksperre. Außerdem kann er die Verknüpfung der App mit dem eigenen Google-Konto entsprechend anpassen.

**[AddDateActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/AddDateActivity.java):** Um einen neuen Termin zu erstellen kann der Nutzer einen Titel, ein Datum, eine Uhrzeit für Beginn als auch Ende, eine Beschreibung und einen Ort angeben. Zudem kann er aus einem Dropdown Feld auswählen, mit welchen Freunden der Termin geteilt werden soll, bzw. ob er privat ist. Über zwei Checkboxen hat er auch die Möglichkeit, den neuen Termin mit seinem Google-Kalender zu synchronisieren und sich eine Benachrichtigung etwa 15 Minuten vor dem Termin schicken zu lassen.

**[AddCalendarActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/AddCalendarActivity.java):** Um einen neuen Freund und somit auch dessen Termine hinzuzufügen genügt es, dass der Nutzer dessen Emailadresse eingibt und dies über den entsprechenden Button bestätigt.

**[SingleDayActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/SingleDayActivity.java):** Wenn ein Tag in der CalendarActivity angeklickt wird, so führt dies den Nutzer zur Anzeige aller Termine an diesem Tag. Diese werden mittels Adapter aus der Datenbank ausgelesen und nach Uhrzeit sortiert als vordefinierte Layoutbausteine angezeigt. Sofern der Nutzer einen dieser Termine anklickt, führt ihn dies zur Anzeige von diesem.

**[DateActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/DateActivity.java):** Ein bereits bestehender Termin kann nach Belieben verändert werden. Dabei können die gleichen Attribute gesetzt werden, wie auch beim Erstellen eines Termins in der AddDateActivity. Davon abgesehen kann ein Termin natürlich auch gelöscht werden.

**[CalendarEventManager](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/DataAccess/EventManager.java), [CalendarEvent](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/DatabaseEntities/CalenderEvent.java):** Ein neu angelegter Termin wird als CalendarEvent in der entsprechenden Datenbank gespeichert.

**[CalendarManager](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/DataAccess/CalenderManager.java), [Calendar](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/DatabaseEntities/Calender.java):** Wenn ein Freund hinzugefügt wird, wird dessen Kalender, bzw. Termine als Calendar-Objekt in der Datenbank gespeichert.

**[InputFormatException](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Exception/InputFormatException.java):** Falls eine Termineingabe nicht valide ist, wird dies über eine Exception ausgegeben.

**[CalendarCl](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/RestAPIClient/CalendarCl.java), [CalendarEventList](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/RestAPIClient/CalendarEventList.java), [CalendarEvents](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/RestAPIClient/CalendarEvents.java), [CalendarListCl](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/RestAPIClient/CalendarListCl.java), [SharedOneTabClient](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/RestAPIClient/SharedOneTabClient.java):** Gespeicherte Termine werden sofort an den eigenen Google-Kalender übergeben. Genauso kann auf Termine des Google-Kalenders zugegriffen werden, um diese in der Appdarzustellen.

**[AuthenticationManager](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Authentication/AuthenticationManager.java):** Um nicht mehrfach den gleichen Code implementieren zu müsse, kann in einer separaten Klasse überprüft werden, ob und welche Authentifizierungsmethode auf dem Gerät aktiv ist. Außerdem kann hier eine neue Methode eingerichtet werden, falls sie nicht bereits existiert.

**[NotificationPublisher](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Notifications/NotificationPublisher.java):** Um eine Benachrichtigung als Erinnerung für einen Termin zu verschicken existiert eine dafür vorgesehene Klasse. Diese kann sowohl Benachrichtigungen planen, indem sie den AlarmManager nutzt, als diese auch abbrechen. Benachrichtigungen können über eine Action sofort geschlossen werden und auch eine Erinnerung fünf Minuten vor einem Termin ist möglich. Falls eine Benachrichtigung angeklickt wird, führt sie zur Anzeige des Termins, der dafür aus der Datenbank ausgelesen wird.

**[shortcuts.xml](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/res/xml/shortcuts.xml):** Die App kann über Shortcuts geöffnet werden. Diese können – nach Eingabe des Fingerabdrucks, falls dieser aktiviert wurde, zur AddDateActivity oder AddCalendarActivity führen.


### Demo

[Verlinken Sie zum Abschluss des Projekts ein kurzes Video, in dem Sie die wesentlichen Nutzungsszenarien Ihrer Anwendung demonstrieren.]

## Team

### Vorstellung

[Beschreiben Sie hier die einzelnen Teammitglieder mit Namen, E-Mail-Adresse und Github-Nutzernamen.]


**Daniel Kuzma:** email, dak2791

**Kerim Agalarov**: email, AKamKer

**Niclas Hildner:** email, github

**Philipp Hamann:** email, hamteer

**Virginia Schellenberg:** vis9494@thi.de & schellenberg.virginia@mail.de, vis9494

### Zuständigkeiten

[Nennen Sie mindestens eine Komponente Ihrer Anwendung, die in wesentlichen Teilen vom jeweiligen Teammitglied entwickelt wurde.]


**Daniel Kuzma:** 

**Kerim Agalarov:** FingerprintActivity (Fingerabdruck-Login), Google-Integration

**Niclas Hildner:** 

**Philipp Hamann:** 

**Virginia Schellenberg:** FingerprintInitializationActivity (Intents), GoogleInitializationActivity(Intents), NotificationsInitializationActivity(Intents), AuthenticationManager (Fingerabdruck-Login), NotificationPublisher (Interaktive Notifications, Intents), shortcuts.xml (App Shortcut)


### Projektablauf

[Beschreiben Sie die verschiedenen Schritte und Zusammentreffen bis zum Abschluss des Projekts.]


**1.	Termin (07.12.2022):** Sammeln von Vorschlägen für die App

Aufgaben:

- Alle: Gedanken machen, welchen Vorschlag derjenige präferiert und mögliche Anforderungen notieren


**2.	Treffen (14.12.2022):** Definition von Anforderungen für populärste Ideen


**3.	Treffen (15.02.2023):** Erste Absprache zur Planung von Aufgaben

Aufgaben:
	
- Daniel Kuzma: Informieren über Google-API-Termine und beginnen mit Datenbankanbindung

- Kerim Agalarov: Bereits erstellte Activity mit Fingerabdruck-Login auf GitHub pushen
		
- Niclas Hildner: Informieren über Google-Kalender-API um Termine zu teilen und mit Einbau beginnen
		
- Philipp Hamann: Informieren über Material Calendar View und dessen Implementierung
		
- Virginia Schellenberg: Urlaub
		
		
**4.	Treffen (19.02.2023):** ?

Aufgaben:	
	
- Daniel Kuzma: Datenbank testen (API-Anfragen aus App heraus stellen)

- Kerim Agalarov: SettingsActivity erstellen
		
- Niclas Hildner: Google-Login einbauen
		
- Philipp Hamann: Kalender-Element anpassen
		
- Virginia Schellenberg: Urlaub
		
			    
**5.	 Treffen (22.02.2023):** ?

Aufgaben:
	
- Daniel Kuzma: Datenbank verbessern und API-Anfragen einbauen

- Kerim Agalarov: API erstellen
		
- Niclas Hildner: API erstellen
		
- Philipp Hamann: Activity-Arbeitsfluss und Layouts planen
		
- Virginia Schellenberg: Urlaub
		
		
**6.	Treffen (28.02.2023):** Vorstellung bisheriger Fortschritte, erster Blick auf Activity-Workflow durch Philipp Hamann

Aufgaben:
	
- Daniel Kuzma: ?

- Kerim Agalarov: Herausfinden, wie API-Aufrufe an anderen Codestellen möglich sind
		
- Niclas Hildner: API kapseln
		
- Philipp Hamann: DateActivity erstellen und Activities weiter ausarbeiten
		
- Virginia Schellenberg: Shortcuts erstellen
		
		
**7.	Treffen (02.03.2023):** Kurze Präsentation von Fortschritten, Symbolvideos von Fingerabdrucksperre bei WhatsApp durch Virginia Schellenberg

Aufgaben:	
	
- Daniel Kuzma: Finale Anpassung der Datenbank und auf GitHub pushen

- Kerim Agalarov: API in eigene Klasse kapseln und Aufrufe an geeigneten Stellen
		
- Niclas Hildner: API in eigene Klasse kapseln und Aufrufe an geeigneten Stellen
		
- Philipp Hamann: UI aller Activities mit XML erstellen
		
- Virginia Schellenberg: Shortcuts finalisieren, Ändern der applicationId und Anpassung von FingerprintActivity (auch bei Shortcuts anzeigen und Möglichkeit von PIN, Passwort oder Muster)
		
		
**8.	Treffen (08.03.2023):** Präsentation von Google-API durch Kerim Agalarov und Niclas Hildner, UI durch Philipp Hamann und der finalen Shortcuts durch Virginia Schellenberg

Aufgaben:	
	
- Daniel Kuzma: Urlaub

- Kerim Agalarov: API-Funktionen vervollständigen
		
- Niclas Hildner: API-Funktionen vervollständigen
		
- Philipp Hamann: UI vervollständigen
		
- Virginia Schellenberg: Arbeitsfluss für erstmaliges Öffnen zur Initialisierung von Benachrichtigungen, Google-Anmeldung und Fingerabdrucksperre
		
		
**9.	Treffen (12.03.2023):** Vorstellung fertiger UI durch Philipp Hamann und Engagieren externer Designerin für Gestaltung von Logo

Aufgaben:	
	
- Daniel Kuzma: Datenbankklassen fertig refactoren und Datenbankaufrufe an geeigneten Stellen

- Kerim Agalarov: API testen und API-Aufrufe an geeigneten Stellen einbauen
		
- Niclas Hildner: API testen und API-Aufrufe an geeigneten Stellen einbauen
		
- Philipp Hamann: UI für Darkmode und Java-Gerüst der Activity-Klassen erstellen
		
- Virginia Schellenberg: Mit Notifications beginnen und in Settings hinzufügen
		
		
**10.	Treffen (15.03.2023):** Diskussion über Datenbanken

Aufgaben:
	
- Alle: Weiter an bisherigen Aufgaben arbeiten


**11.	Treffen (16.03):** ?

Aufgaben:
	
- Alle: Weiter an bisherigen Aufgaben arbeiten

    
**12.	Treffen (19.03.2023):** Diskussionen über bisherigen Stand

Aufgaben:
	
- Daniel Kuzma: AddDateActivity perfektionieren

- Kerim Agalarov: ?
		
- Niclas Hildner: Events einbinden und Event und Datenbank in OnResume() speichern
		
- Philipp Hamann: UI von DateActivity, CalendarActivity und AddDateActivity anpassen
		
- Virginia Schellenberg: AlarmManager für Notifications einbauen, NotificationPublisher finalisieren und Anpassung von Notifications auf DateActivity möglich machen
		
		
**13.	Treffen (22.03.2023):** ?



## Guidelines zur Nutzung dieses Repositorys

### Allgemeine Hinweise und Vorgaben

* Das Repository besteht im initialen Stand aus einem einzelnen Master-Branch. Versuchen Sie bei der Arbeit am Projekt darauf zu achten, dass sich in diesem Branch stets die aktuell lauffähige und fehlerfreie Version Ihrer Anwendung befindet. Nutzten Sie für die eigentliche Entwicklung ggf. weitere Branches.
* Gehen Sie sorgfältig bei der Erstellung von Issues und *Commit Messages* vor: Die Qualität dieser Artefakte fließt nicht in die Bewertung ein, trotzdem sollten Sie versuchen, Ihr Vorgehen anhand nachvollziehbarer Versionseinträge und klarere Aufgabenbeschreibung gut zu dokumentieren.
* Halten Sie diese Readme-Datei(en) stets aktuell.
* Diese sollte auch wichtige Informationen für die Nutzung beinhalten (Handbuch).
* Spätestens zur Projektabgabe legen Sie eine Release-Version des finalen Stands Ihrer Anwendung an und hängen an diese eine installierbare (Debug-) APK-Datei an.
* Achten Sie insbesondere darauf anzugeben, welches API-Level / NDK-Version ihrem Projekt zugrunde liegt und auf die 'Kompilierbarkeit' ihres finalen Codes.
