GET whatchatz/api/messages/{chatId} Hole alle Nachrichten für einen Chat

GET whatchatz/api/contacts Liste alle Kontakte des Benutzer

POST whatchatz/api/users Benutzer-Login (Body: UserCreationRequest)


WebSocket Schnittstellen:  
ws://<host>/ws
OnOpen: Verbindet einen Benutzer mit einem Chatraum (authentifiziert über Token im Query-String)
OnMessage: Sende eine Nachricht als JSON (MessageRequest)
OnClose: Trennt die Verbindung vom Chatraum
OnError: Fehlerbehandlung während der WebSocket-Kommunikation
Nachrichtentypen (WebSocket):  
NEW_MESSAGE: Benachrichtigung an andere Clients im Chatraum über neue Nachrichten
ERROR: Fehlernachrichten mit Code und Grund
Diese Übersicht kann je nach Implementierung und weiteren Endpunkten angepasst werden