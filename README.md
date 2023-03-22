# Projekttitel

## Projektbeschreibung

### Projektziele

[Beschreiben Sie hier in einer kurzen Zusammenfassung Hintergrund, Ziele und Funktionen Ihrer Anwendung. Fügen Sie mehrere sinnvollen Screenshot ein, die den Funktionsumfang der Anwendung demonstrieren.]

Unsere App „FriendCalendar“ ist ein Kalender, mithilfe welches man sowohl private als auch öffentliche Termine, zugänglich für Freunde, erstellen kann. Von fünf Ideenvorschlägen entschieden wir uns letztendlich für diesen, da wir mit den Versionen auf dem Markt nie hundertprozentig zufrieden waren und uns daher daran versuchen wollten, eine gute Alternative zu erschaffen.

So kann man über unsere Applikation Termine erstellen und diese mit Freunden über Google teilen oder als private Termine nur im eigenen Kalender speichern. Natürlich lassen sich gespeicherte Termine auch bearbeiten oder wieder löschen. All dies ermöglicht die im Hintergrund agierende Datenbank. Außerdem kann der Nutzer entscheiden, ob er sich an Termine über eine Benachrichtigung erinnern zu lassen, damit er diese nicht vergisst. Um seine privaten Termine vor ungewollten Eingriffen zu schützen, ist es dem Nutzer möglich die App mit einem Fingerabdruck, bzw. anderer Methode zu sichern.

### Funktionen

[Beschreiben Sie die Funktionen ihrer App. Beziehen Sie sich dabei auf den Anforderungskatalog und stellen Sie eine Verbindung zu den entsprechneden Files oder Directories her.]

NotificationsInitializationActivity, FingerprintInitializationActivity, GoogleInitialization: Beim erstmaligen Öffnen der App nach der Installation wird der Nutzer in einen Initialisierungsfluss weitergeleitet. Bei diesem durchläuft er drei Activities, die ihn nacheinander fragen, ob er Benachrichtigungen aktivieren möchte, dort wird bei Zustimmung zuerst um deren Erlaubnis gebeten, ob er seine Daten mithilfe einer Fingerabdrucksperre sichern und sich mit Google anmelden möchte.

CalendarActivity: Danach gelangt er auf die Hauptseite der App, eine Darstellung des Kalenders, wobei der Nutzer zwischen einer Monats- und Wochenansicht wechseln kann. Zu dieser Activity gelangt der Nutze immer, wenn er die App über ihr Icon öffnet und im Falle einer aktivierten Fingerabdrucksperre eben diese erfolgreich absolviert. Von hier aus kann der Nutzer die Einstellungen öffnen, einen neuen Termin erstellen, einen Freund hinzufügen oder auf die Anzeige eines einzelnen Tages zugreifen. Falls der Nutzer ungültige Daten eingibt, so wird ihm dies prompt vermittelt. Gültige Termine werden hingegen in der Datenbank gespeichert.
FingerprintActivity: Sofern der Nutzer die Fingerabdrucksperre eingerichtet hat, wird er beim Öffnen der App, egal auf welchem Wege, auf eine Activity geleitet, wo er seinen Fingerabdruck eingeben muss. Nur sofern dies erfolgreich gelingt, hat er Zugriff auf die restliche App. Wenn die Fingerabdrucksperre nicht eingerichtet ist, wird diese Seite übersprungen.

SettingsActivity: In den Einstellungen kann der Nutzer nachträglich B4enachrichtigungen aktivieren und wieder deaktivieren, ebenso wie die Fingerabdrucksperre. Außerdem kann er die Verknüpfung der App mit dem eigenen Google-Konto entsprechend anpassen.

AddDateActivity: Um einen neuen Termin zu erstellen kann der Nutzer einen Titel, ein Datum, eine Uhrzeit für Beginn als auch Ende, eine Beschreibung und einen Ort angeben. Zudem kann er aus einem Dropdown Feld auswählen, mit welchen Freunden der Termin geteilt werden soll, bzw. ob er privat ist. Über zwei Checkboxen hat er auch die Möglichkeit, den neuen Termin mit seinem Google-Kalender zu synchronisieren und sich eine Benachrichtigung etwa 15 Minuten vor dem Termin schicken zu lassen.
AddCalendarActivity: Um einen neuen Freund und somit auch dessen Termine hinzuzufügen genügt es, dass der Nutzer dessen Emailadresse eingibt und dies über den entsprechenden Button bestätigt.

SingleDayActivity: Wenn ein Tag in der CalendarActivity angeklickt wird, so führt dies den Nutzer zur Anzeige aller Termine an diesem Tag. Diese werden mittels Adapter aus der Datenbank ausgelesen und nach Uhrzeit sortiert als vordefinierte Layoutbausteine angezeigt. Sofern der Nutzer einen dieser Termine anklickt, führt ihn dies zur Anzeige von diesem.

DateActivity: Ein bereits bestehender Termin kann nach Belieben verändert werden. Dabei können die gleichen Attribute gesetzt werden, wie auch beim Erstellen eines Termins. Davon abgesehen kann ein Termin natürlich auch gelöscht werden.

CalendarEventManager, CalendarEvent: Ein neu angelegter Termin wird als CalendarEvent in der entsprechenden Datenbank gespeichert.

CalendarManager, Calendar: Wenn ein Freund hinzugefügt wird, wird dessen Kalender, bzw. Termine als Calendar-Objekt in der Datenbank gespeichert.

InputFormatException: Falls eine Termineingabe nicht valide ist, wird dies über eine Exception ausgegeben.

CalendarCl, CalendarEventList, CalendarEvents, CalendarListCl, SharedOneTabClient: Gespeicherte Termine werden sofort an den eigenen Google-Kalender übergeben. Genauso kann auf Termine des Google-Kalenders zugegriffen werden, um diese in der Appdarzustellen.

AuthenticationManager: Um nicht mehrfach den gleichen Code implementieren zu müsse, kann in einer separaten Klasse überprüft werden, ob und welche Authentifizierungsmethode auf dem Gerät aktiv ist. Außerdem kann hier eine neue Methode eingerichtet werden, falls sie nicht bereits existiert.

NotificationPublisher: Um eine Benachrichtigung als Erinnerung für einen Termin zu verschicken existiert eine dafür vorgesehene Klasse. Diese kann sowohl Benachrichtigungen planen, indem sie den AlarmManager nutzt, als diese auch abbrechen. Benachrichtigungen können über eine Action sofort geschlossen werden und auch eine Erinnerung fünf Minuten vor einem Termin ist möglich. Falls eine Benachrichtigung angeklickt wird, führt sie zur Anzeige des Termins, der dafür aus der Datenbank ausgelesen wird.

shortcuts.xml: Die App kann über Shortcuts geöffnet werden. Diese können – nach Eingabe des Fingerabdrucks, falls dieser aktiviert wurde, zur AddDateActivity oder AddCalendarActivity führen.


### Demo

[Verlinken Sie zum Abschluss des Projekts ein kurzes Video, in dem Sie die wesentlichen Nutzungsszenarien Ihrer Anwendung demonstrieren.]

## Team

### Vorstellung

[Beschreiben Sie hier die einzelnen Teammitglieder mit Namen, E-Mail-Adresse und Github-Nutzernamen.]

### Zuständigkeiten

[Nennen Sie mindestens eine Komponente Ihrer Anwendung, die in wesentlichen Teilen vom jeweiligen Teammitglied entwickelt wurde.]

### Projektablauf

[Beschreiben Sie die verschiedenen Schritte und Zusammentreffen bis zum Abschluss des Projekts.]

## Guidelines zur Nutzung dieses Repositorys

### Allgemeine Hinweise und Vorgaben

* Das Repository besteht im initialen Stand aus einem einzelnen Master-Branch. Versuchen Sie bei der Arbeit am Projekt darauf zu achten, dass sich in diesem Branch stets die aktuell lauffähige und fehlerfreie Version Ihrer Anwendung befindet. Nutzten Sie für die eigentliche Entwicklung ggf. weitere Branches.
* Gehen Sie sorgfältig bei der Erstellung von Issues und *Commit Messages* vor: Die Qualität dieser Artefakte fließt nicht in die Bewertung ein, trotzdem sollten Sie versuchen, Ihr Vorgehen anhand nachvollziehbarer Versionseinträge und klarere Aufgabenbeschreibung gut zu dokumentieren.
* Halten Sie diese Readme-Datei(en) stets aktuell.
* Diese sollte auch wichtige Informationen für die Nutzung beinhalten (Handbuch).
* Spätestens zur Projektabgabe legen Sie eine Release-Version des finalen Stands Ihrer Anwendung an und hängen an diese eine installierbare (Debug-) APK-Datei an.
* Achten Sie insbesondere darauf anzugeben, welches API-Level / NDK-Version ihrem Projekt zugrunde liegt und auf die 'Kompilierbarkeit' ihres finalen Codes.
