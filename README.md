# FriendCalendar

## Wichtige Informationen und Handbuch
[Hier ist die Debug-APK zu finden](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/master/app-debug.apk)
### Systemumgebung / Testumgebung
Als Entwicklungs- und Testumgebung wurde Windows 10 verwendet. Die App wurde auf einem emulierten Google Pixel 6 Pro mit API Level 29 ausgeführt und getestet.  
### Testaccount
Da diese App auf die Google API zugreift und die App noch nicht veröffentlicht wurde, muss der Tester der App folgenden Google-Account verwenden, um den vollen Funktionsumfang nutzen zu können. Zudem muss das Google-Konto im Mobilgerät (in den Einstellungen) hinzugefügt werden.: <br>
<br>
E-Mail Adresse: `freundeskalender.test1@gmail.com` <br>
Passwort: `TestKalender1`<br>
### Debug SHA1-Keystore
Wenn der Tester der App die App lokal in Android-Studio builden möchte, sodass eine vollständig lauffähige Version entsteht, muss der Tester folgenden [Debug-Keystore](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/master/debug.keystore) in Android-Studio einbinden, da nur dieser im Google-Cloud-Projekt hinterlegt ist. <br>
<br>


Hier eine Anleitung, um den [Debug-Keystore](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/master/debug.keystore) einzubinden:
1. Die Projekt-Struktur öffnen (Strg+Alt+Umschalt+S)
2. In der linken Leiste die Option "Modules" auswählen
3. In der neuen Spalte "Modules" nun "app" auswählen
4. Den Reiter "Signing Configs" auswählen
5. Das geöffnete Formular wie im nachfolgenden Bild ausfüllen und bei "Store File" den absoluten Pfad der heruntergeladenen [Debug-Keystore](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/master/debug.keystore) angeben
<img src="https://user-images.githubusercontent.com/73745030/227740136-7d0d0e5b-6a02-46de-a70f-5823d7d0bbc3.png" width=60%>

### ❗ Hinweis
Der Nutzer der App wird in der aktuellen Version nach jedem Schließen der App aus sicherheitstechnischen Gründen automatisch von Google abgemeldet. Dies führt dazu, dass der Nutzer der App sich bei erneutem Öffnen über den "Login mit Google"-Button in den Einstellungen wieder mit dem Google-Account anmelden muss.  

## Projektbeschreibung

### Projektziele

[Beschreiben Sie hier in einer kurzen Zusammenfassung Hintergrund, Ziele und Funktionen Ihrer Anwendung. Fügen Sie mehrere sinnvollen Screenshot ein, die den Funktionsumfang der Anwendung demonstrieren.]


Unsere App „FriendCalendar“ ist ein Kalender, mithilfe von welchem man sowohl private als auch öffentliche Termine, zugänglich für Freunde, erstellen kann. Von fünf Ideenvorschlägen entschieden wir uns letztendlich für diesen, da wir mit den Versionen auf dem Markt nie hundertprozentig zufrieden waren und uns daher daran versuchen wollten, eine gute Alternative zu schaffen.

So kann man über unsere Applikation Termine erstellen und diese mit Freunden über Google teilen oder als private Termine nur im eigenen Kalender speichern. Natürlich lassen sich gespeicherte Termine auch bearbeiten oder wieder löschen. All dies ermöglicht die im Hintergrund agierende Datenbank. Außerdem kann der Nutzer entscheiden, ob er sich an Termine über eine Benachrichtigung erinnern zu lassen, damit er diese nicht vergisst. Um seine privaten Termine vor ungewollten Eingriffen zu schützen, ist es dem Nutzer möglich die App mit einem Fingerabdruck oder einer anderen Login-Methode zu sichern.

<img src="https://user-images.githubusercontent.com/94178859/227730290-085998e7-8242-422c-b507-6f43a5785bf5.png" width="200"> <img src="https://user-images.githubusercontent.com/94178859/227730838-53f76df9-32b3-4c04-bdc4-fe460dec56d1.png" width="200"> <img src="https://user-images.githubusercontent.com/94178859/227730876-6d484cd2-446a-4421-8659-0a6dfafff85b.png" width="200">

Links: Login mit Fingerabdruck zum Schutz der Daten

Mitte: Hauptansicht des Kalenders

Rechts: Ansicht eines Kalendertages

<img src="https://user-images.githubusercontent.com/94178859/227730377-3a461e77-221f-4b96-b8a7-6dd1465cd9ae.png" width="200"> <img src="https://user-images.githubusercontent.com/94178859/227731042-4e98e37e-7a0f-4997-aa1f-fe977e788a8d.png" width="200"> <img src="https://user-images.githubusercontent.com/94178859/227730347-e5e9a268-de7c-493d-8ced-347909229eaa.png" width="200">

Links: Bearbeitung eines Termines (Anlegen ähnlich)

Mitte: Hinzufügen eines Freundes von Google

Rechts: Benachrichtigung zur Erinnerung an einen Termin


### Funktionen

[Beschreiben Sie die Funktionen ihrer App. Beziehen Sie sich dabei auf den Anforderungskatalog und stellen Sie eine Verbindung zu den entsprechneden Files oder Directories her.]


**[NotificationsInitializationActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/NotificationInitializationActivity.java), [FingerprintInitializationActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/FingerprintInitializationActivity.java), [GoogleInitialization](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/GoogleInitializationActivity.java):** Beim erstmaligen Öffnen der App nach der Installation wird der Nutzer in einen Initialisierungsfluss weitergeleitet. Bei diesem durchläuft er drei Activities, die ihn nacheinander fragen, ob er Benachrichtigungen aktivieren möchte, dort wird bei Zustimmung zuerst um deren Erlaubnis gebeten, ob er seine Daten mithilfe einer Fingerabdrucksperre sichern und sich mit Google anmelden möchte. Danach gelangt er auf die Hauptseite der App. Diese Activities sollen dem Nutzer die Möglichkeit geben, den Features der App zuzustimmen und sie gleich einzurichten. Um dem Nutzer allerdings nichts aufzudrängen, hat er die Möglichkeit, die Anfragen abzulehnen. Das User Interface ist sehr simpel aufgebaut, um dem Nutzer nur die wichtigsten Informationen zu zeigen. Außerdem dienen die Activities zur Initialisierung des Fingerabdruck-Logins und der Google-Integration, als auch der interaktiven Notifications. Zudem sind die Activities über Intents, in diesem Fall ohne Payload, verbunden.

**[CalendarActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/CalendarActivity.java):** Bei der Hauptseite der App handelt es sich um eine Darstellung des Kalenders, wobei der Nutzer zwischen einer Monats- und Wochenansicht wechseln kann. Zu dieser Activity gelangt der Nutzer immer, wenn er die App über ihr Icon öffnet und im Falle einer aktivierten Fingerabdrucksperre eben diese erfolgreich absolviert. Von hier aus kann der Nutzer die Einstellungen öffnen, einen neuen Termin erstellen, einen Freund hinzufügen oder auf die Anzeige eines einzelnen Tages zugreifen. All dies gelingt mithilfe von Intents, je nachdem welche Activity aufgerufen wird mit oder ohne Payload.

**[FingerprintActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/FingerprintActivity.java):** Sofern der Nutzer die Fingerabdrucksperre eingerichtet hat, wird er beim Öffnen der App, egal auf welchem Wege, auf eine Activity geleitet, wo er seinen Fingerabdruck eingeben muss. Nur sofern der Fingerabdruck-Login erfolgreich gelingt, hat er Zugriff auf die restliche App. Wenn die Fingerabdrucksperre nicht eingerichtet ist, wird diese Seite übersprungen. Anstelle seines Fingerabdrucks kann der Nutzer außerdem auch seine PIN, sein Passwort oder Muster nutzen. Um auf die richtige Activity zu springen muss über den aufrufenden Intent eine Payload mit dem Activity-Klassennamen mitgegeben werden.

**[SettingsActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/SettingsActivity.java):** In den Einstellungen kann der Nutzer nachträglich Benachrichtigungen aktivieren oder wieder deaktivieren, ebenso wie die Fingerabdrucksperre. Außerdem kann er die Verknüpfung der App mit dem eigenen Google-Konto entsprechend anpassen.

**[AddDateActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/AddDateActivity.java):** Um einen neuen Termin zu erstellen kann der Nutzer einen Titel, ein Datum, eine Uhrzeit für Beginn als auch Ende, eine Beschreibung und einen Ort angeben. Zudem kann er aus einem Dropdown Feld auswählen, mit welchen Freunden der Termin geteilt werden soll, bzw. ob er privat ist. Über zwei Checkboxen hat er auch die Möglichkeit, den neuen Termin mit seinem Google-Kalender zu synchronisieren und sich eine Benachrichtigung etwa 15 Minuten vor dem Termin schicken zu lassen. Falls der Nutzer ungültige Daten eingibt, so wird ihm dies prompt vermittelt. Gültige Termine werden hingegen in der Datenbank auf dem Gerät gespeichert.

**[AddCalendarActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/AddCalendarActivity.java):** Um einen neuen Freund und somit auch dessen Termine hinzuzufügen genügt es, dass der Nutzer dessen Emailadresse eingibt und dies über den entsprechenden Button bestätigt.

**[SingleDayActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/SingleDayActivity.java):** Wenn ein Tag in der CalendarActivity angeklickt wird, so führt dies den Nutzer zur Anzeige aller Termine an diesem Tag. Dafür muss dem aufrufenden Intent eine Payload mit dem entsprechenden Kalendartag mitgegeben werden. Die Termine werden mittels Adapter aus der Datenbank ausgelesen und nach Uhrzeit sortiert als vordefinierte, wiederverwendbare Layoutbausteine angezeigt. Sofern der Nutzer einen dieser Termine anklickt, führt ihn dies zu dessen Anzeige.

**[DateActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/DateActivity.java):** Ein bereits bestehender Termin wird ausder Datenbank geladen und kann nach Belieben verändert werden. Dabei können die gleichen Attribute gesetzt werden, wie auch beim Erstellen eines Termins in der AddDateActivity. Davon abgesehen kann ein Termin natürlich auch gelöscht werden.

**[CalendarEventManager](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/DataAccess/EventManager.java), [CalendarEvent](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/DatabaseEntities/CalenderEvent.java):** Ein neu angelegter Termin wird als CalendarEvent in der entsprechenden Datenbank gespeichert.

**[CalendarManager](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/DataAccess/CalenderManager.java), [Calendar](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/DatabaseEntities/Calender.java):** Wenn ein Freund hinzugefügt wird, wird dessen Kalender, bzw. Termine als Calendar-Objekt in der Datenbank gespeichert.

**[InputFormatException](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Exception/InputFormatException.java):** Falls eine Termineingabe nicht valide ist, wird dies über eine Exception ausgegeben.

**[CalendarCl](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/RestAPIClient/CalendarCl.java), [CalendarEventList](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/RestAPIClient/CalendarEventList.java), [CalendarEvents](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/RestAPIClient/CalendarEvents.java), [CalendarListCl](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/RestAPIClient/CalendarListCl.java), [SharedOneTabClient](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/RestAPIClient/SharedOneTabClient.java):** Gespeicherte Termine werden sofort an den eigenen Google-Kalender übergeben. Genauso kann auf Termine des Google-Kalenders zugegriffen werden, um diese in der App darzustellen. So wird die Google-Integration ermöglicht.

**[AuthenticationManager](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Authentication/AuthenticationManager.java):** Um nicht mehrfach den gleichen Code implementieren zu müssen, kann in einer separaten Klasse überprüft werden, ob und welche Authentifizierungsmethode auf dem Gerät aktiv ist. Außerdem kann hier eine neue Methode eingerichtet werden, falls sie nicht bereits existiert. Dies erleichtert den Fingerabdruck-Login.

**[NotificationPublisher](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Notifications/NotificationPublisher.java):** Um eine Benachrichtigung als Erinnerung für einen Termin zu verschicken existiert eine dafür vorgesehene Klasse. Diese kann sowohl Benachrichtigungen planen, indem sie den AlarmManager nutzt, als diese auch abbrechen. Die interaktiven Notifications können über eine Action sofort geschlossen werden und auch eine Erinnerung fünf Minuten vor einem Termin ist über eine weitere Action möglich. Falls eine Benachrichtigung angeklickt wird, führt dies mittels Intent mit Payload zur Anzeige des Termins, der dafür aus der Datenbank ausgelesen wird.

**[shortcuts.xml](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/res/xml/shortcuts.xml):** Die App kann über Shortcuts geöffnet werden. Diese können – nach Eingabe des Fingerabdrucks, falls dieser aktiviert wurde - zur AddDateActivity oder AddCalendarActivity führen, wofür dem Intent die entsprechende Payload in Form des Klassennamens mitgegeben wird.


### Demo


https://user-images.githubusercontent.com/73745030/227784812-de65aae1-f042-45f3-99c7-649385012bd3.mp4


[Demo Video](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/19052612add9b8f0facc70c5b81ced3507bf9c1a/Freundeskalender.mp4)

## Team

### Vorstellung

[Beschreiben Sie hier die einzelnen Teammitglieder mit Namen, E-Mail-Adresse und Github-Nutzernamen.]


**Daniel Kuzma:** dak2791@thi.de & daniel1kuzma@gmail.com, dak2791

**Kerim Agalarov**: kerim.agalarov@stud.hs-coburg.de, AKamKer

**Niclas Hildner:** niclas.hildner@stud.hs-coburg.de, Your Name & Motherak

**Philipp Hamann:** phh1901@thi.de & philham.001@gmail.com, hamteer

**Virginia Schellenberg:** vis9494@thi.de & schellenberg.virginia@mail.de, [@github/vis9494](https://github.com/vis9494)

### Zuständigkeiten

[Nennen Sie mindestens eine Komponente Ihrer Anwendung, die in wesentlichen Teilen vom jeweiligen Teammitglied entwickelt wurde.]

**Daniel Kuzma:** <br>
[CalenderManager](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/master/app/src/main/java/com/frcal/friendcalender/DataAccess/CalenderManager.java), 
[EventManager](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/master/app/src/main/java/com/frcal/friendcalender/DataAccess/EventManager.java), 
[Calender](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/master/app/src/main/java/com/frcal/friendcalender/DatabaseEntities/Calender.java), 
[CalenderEvent](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/master/app/src/main/java/com/frcal/friendcalender/DatabaseEntities/CalenderEvent.java), 
[AppDatabase](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/master/app/src/main/java/com/frcal/friendcalender/room/AppDatabase.java), 
[CalenderEventAttributeTypeConverter](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/master/app/src/main/java/com/frcal/friendcalender/room/CalenderEventAttributeTypeConverter.java), 
[DAO](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/master/app/src/main/java/com/frcal/friendcalender/room/DAO.java), 
[DatabaseHelper](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/master/app/src/main/java/com/frcal/friendcalender/room/DatabaseHelper.java), 
(+ Datenbank-Aufrufe in den Activities)

**Kerim Agalarov:** <br>
Fingerprint Login:
[FingerprintActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/FingerprintActivity.java), 
[SettingsActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/b3d8123bf8ff47a5db900a97b7a6f89f555288fb/app/src/main/java/com/frcal/friendcalender/Activities/SettingsActivity.java) <br>

Objekte für Google Integration/API-Aufrufe: <br>
[SharedOneTabClient](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/1cfebd4ab4d2caad6073628b67550b3591d214d7/app/src/main/java/com/frcal/friendcalender/RestAPIClient/SharedOneTabClient.java),
[CalendarListCl](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/1cfebd4ab4d2caad6073628b67550b3591d214d7/app/src/main/java/com/frcal/friendcalender/RestAPIClient/CalendarListCl.java),
[CalendarCl](app/src/main/java/com/frcal/friendcalender/RestAPIClient/CalendarCl.java),
[AsyncCalCl](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/1cfebd4ab4d2caad6073628b67550b3591d214d7/app/src/main/java/com/frcal/friendcalender/RestAPIClient/AsyncCalCl.java) <br>

API-Aufrufe eingebaut in: <br>
[AddDateActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/1cfebd4ab4d2caad6073628b67550b3591d214d7/app/src/main/java/com/frcal/friendcalender/Activities/AddDateActivity.java),
[CalendarActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/1cfebd4ab4d2caad6073628b67550b3591d214d7/app/src/main/java/com/frcal/friendcalender/Activities/CalendarActivity.java),
[DateActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/1cfebd4ab4d2caad6073628b67550b3591d214d7/app/src/main/java/com/frcal/friendcalender/Activities/DateActivity.java)

**Niclas Hildner:** <br>
[CalendarEvents](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/master/app/src/main/java/com/frcal/friendcalender/RestApiClient/CalendarEvents.java), 
[CalendarEventList](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/master/app/src/main/java/com/frcal/friendcalender/RestApiClient/CalendarEventList.java), 
[AsyncCalEventList](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/master/app/src/main/java/com/frcal/friendcalender/RestApiClient/AsyncCallEventList.java), 
[AsyncCalEvent](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/master/app/src/main/java/com/frcal/friendcalender/RestApiClient/AsyncCallEvent.java), 
(+API-Aufrufe in den Activities)

**Philipp Hamann:** <br>
Layouts für Activities: [activity_add_calendar.xml](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/res/layout/activity_add_calendar.xml), [activity_add_date.xml](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/res/layout/activity_add_date.xml), [activity_calendar.xml](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/res/layout/activity_calendar.xml), [activity_date.xml](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/res/layout/activity_date.xml), [activity_single_day.xml](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/res/layout/activity_single_day.xml), [activity_settings.xml](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/res/layout/activity_settings.xml) <br>
Layout für Listen-Items in Tagesansicht: [date_list_item.xml](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/res/layout/date_list_item.xml) <br>
An verschiedenen Stellen eingesetzte Icons: zu finden im Ordner [drawable](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/res/drawable) <br>
[AddDateActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/AddDateActivity.java) und [DateActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/DateActivity.java): Layout-Anbindung, Java-Grundgerüst, AlertDialog für Terminteilung, Verarbeitung von Nutzereingaben für Datenbank und API <br>
[SingleDayActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/SingleDayActivity.java): Layout-Anbindung, Java-Grundgerüst, Auffüllen des RecyclerView mithilfe der Klassen [DateListRecyclerAdapter](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/RecyclerView/DateListRecyclerAdapter.java) & [DateListViewHolder](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/RecyclerView/DateListViewHolder.java) <br>
[CalendarActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/CalendarActivity.java): Layout-Anbindung, Java-Grundgerüst, CalendarView (Hervorhebung von aktuellem Tag via [OneDayDecorator](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Decorators/OneDayDecorator.java), Markierung von Terminen via [EventDecorator](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Decorators/EventDecorator.java)) <br>
[InputFormatException](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Exception/InputFormatException.java)

**Virginia Schellenberg:** <br>
[FingerprintInitializationActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/FingerprintInitializationActivity.java) (Intents), [GoogleInitialization](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/GoogleInitializationActivity.java) (Intents), [NotificationsInitializationActivity](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Activities/NotificationInitializationActivity.java) (Intents), [AuthenticationManager](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Authentication/AuthenticationManager.java) (Fingerabdruck-Login), [NotificationPublisher](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/java/com/frcal/friendcalender/Notifications/NotificationPublisher.java) (Interaktive Notifications, Intents), [shortcuts.xml](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/a4d3a8df1303a294250da1289f3dfd363f2a969f/app/src/main/res/xml/shortcuts.xml) (App Shortcut), [activity_fingerprint.xml](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/84b5eb40c992b42d449dee38dcbce0781cb4ee36/app/src/main/res/layout/activity_fingerprint.xml), [activity_fingerprint_initialization.xml](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/84b5eb40c992b42d449dee38dcbce0781cb4ee36/app/src/main/res/layout/activity_fingerprint_initialization.xml), [activity_google_initialization.xml](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/84b5eb40c992b42d449dee38dcbce0781cb4ee36/app/src/main/res/layout/activity_google_initialization.xml), [activity_notification_initialization.xml](https://github.com/Wirtschaftsinformatik-Passau/abschlussprojekt-freundekalender/blob/84b5eb40c992b42d449dee38dcbce0781cb4ee36/app/src/main/res/layout/activity_notification_initialization.xml)


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
		
		
**4.	Treffen (19.02.2023):** Vorstellung verschiedener Möglichkeiten des CalendarView durch Philipp Hamann, Diskussion von Möglichkeiten und potenziellen Schwierigkeiten der Google-Kalender-Api

Aufgaben:	
	
- Daniel Kuzma: Datenbank testen (API-Anfragen aus App heraus stellen)

- Kerim Agalarov: SettingsActivity erstellen
		
- Niclas Hildner: Google-Login einbauen
		
- Philipp Hamann: Kalender-Element anpassen
		
- Virginia Schellenberg: Urlaub
		
			    
**5.	 Treffen (22.02.2023):** Vorstellung des initialen Datenbankschemas durch Daniel Kuzma, Vorstellung der Hervorhebung von Events im CalendarView durch Philipp Hamann, Vorstellung von Settings und Google-Login durch Niclas Hilder und Kerim Agalarov

Aufgaben:
	
- Daniel Kuzma: Datenbank verbessern und API-Anfragen einbauen

- Kerim Agalarov: API erstellen
		
- Niclas Hildner: API erstellen
		
- Philipp Hamann: Activity-Arbeitsfluss und Layouts planen
		
- Virginia Schellenberg: Urlaub
		
		
**6.	Treffen (28.02.2023):** Vorstellung bisheriger Fortschritte, erster Blick auf Activity-Workflow durch Philipp Hamann

Aufgaben:
	
- Daniel Kuzma: Datenbank ausarbeiten/ unnötige Sachen aus Datenbank entfernen und weitere API-Anfragen

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


**11.	Treffen (16.03):** Diskussionen über bisherigen Stand

Aufgaben:
	
- Alle: Weiter an bisherigen Aufgaben arbeiten

    
**12.	Treffen (19.03.2023):** Diskussionen über bisherigen Stand

Aufgaben:
	
- Daniel Kuzma: AddDateActivity perfektionieren

- Kerim Agalarov: Sign-In Bugfixes, Snychronisation von Terminen aus GMail Kalender 
		
- Niclas Hildner: Events einbinden und Event in Datenbank speichern
		
- Philipp Hamann: UI von DateActivity, CalendarActivity und AddDateActivity anpassen
		
- Virginia Schellenberg: AlarmManager für Notifications einbauen, NotificationPublisher finalisieren und Anpassung von Notifications auf DateActivity möglich machen
		
		
**13.	Treffen (22.03.2023):** Diskussionen und Bugfixes seitens API-Anbindung, Vorstellung der fertigen Notifications durch Virginia Schellenberg

Aufgaben:

- Alle: Weiteres Bugfixing


**14.	Treffen (25.03.2023):** Finale Review der App, Debug-APK erstellen, Log-Messages auskommentieren, letzte Aufgaben verteilen

Aufgaben:

 - Alle: ReadMe um eigene Aufgaben/Komponenten ergänzen
 
 - Daniel Kuzma: ReadMe ergänzen: Systemumgebung, API-Level, Vorgehen mit Testaccount(s), DebugKeystore-Informationen, Logout-Hinweis
 
 - Kerim Agalarov: Video aufnehmen
 
 - Niclas Hildner: Video aufnehmen
 
 
**15.	Treffen (26.03.2023):** Projektabschluss & Review: Ungenutzte Methoden/Variablen/Imports im Code entfernt, Video aufgenommen und verlinkt, Readme finalisiert

## Guidelines zur Nutzung dieses Repositorys

### Allgemeine Hinweise und Vorgaben

* Das Repository besteht im initialen Stand aus einem einzelnen Master-Branch. Versuchen Sie bei der Arbeit am Projekt darauf zu achten, dass sich in diesem Branch stets die aktuell lauffähige und fehlerfreie Version Ihrer Anwendung befindet. Nutzten Sie für die eigentliche Entwicklung ggf. weitere Branches.
* Gehen Sie sorgfältig bei der Erstellung von Issues und *Commit Messages* vor: Die Qualität dieser Artefakte fließt nicht in die Bewertung ein, trotzdem sollten Sie versuchen, Ihr Vorgehen anhand nachvollziehbarer Versionseinträge und klarere Aufgabenbeschreibung gut zu dokumentieren.
* Halten Sie diese Readme-Datei(en) stets aktuell.
* Diese sollte auch wichtige Informationen für die Nutzung beinhalten (Handbuch).
* Spätestens zur Projektabgabe legen Sie eine Release-Version des finalen Stands Ihrer Anwendung an und hängen an diese eine installierbare (Debug-) APK-Datei an.
* Achten Sie insbesondere darauf anzugeben, welches API-Level / NDK-Version ihrem Projekt zugrunde liegt und auf die 'Kompilierbarkeit' ihres finalen Codes.
